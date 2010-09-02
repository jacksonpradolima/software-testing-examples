/* ========================================================================== *
 *                                   AdaptJ                                   *
 *              A Dynamic Application Profiling Toolkit for Java              *
 *                                                                            *
 *  Copyright (C) 2003-2004 Bruno Dufour                                      *
 *                                                                            *
 *  This software is under (heavy) development. Please send bug reports,      *
 *  comments or suggestions to bdufou1@sable.mcgill.ca.                       *
 *                                                                            *
 *  This library is free software; you can redistribute it and/or             *
 *  modify it under the terms of the GNU Library General Public               *
 *  License as published by the Free Software Foundation; either              *
 *  version 2 of the License, or (at your option) any later version.          *
 *                                                                            *
 *  This library is distributed in the hope that it will be useful,           *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU         *
 *  Library General Public License for more details.                          *
 *                                                                            *
 *  You should have received a copy of the GNU Library General Public         *
 *  License along with this library; if not, write to the                     *
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330,              *
 *  Boston, MA 02111-1307, USA.                                               *
 * ========================================================================== */

package adaptj_pool.util;

import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;
import java.util.*;
import org.apache.bcel.classfile.*;
import adaptj_pool.*;
import it.unimi.dsi.fastUtil.*;

/**
 * Resolves class names from a CLASSPATH. The CLASSPATH is composed of a list
 * of directories, jar/zip files and class files.
 * 
 * @author Bruno Dufour
 */
public class ClassPathExplorer {
    private static ClassPathExplorer instance = null;
    private List classPath = null;
    
    private char pathSeparator = System.getProperty("path.separator").charAt(0);
    private char fileSeparator = System.getProperty("file.separator").charAt(0);
    //private boolean isRunningUnderWindows = System.getProperty("os.name").startsWith("Windows");
    private String soughtClassName = null;
    private Map previousResults = new Object2ObjectOpenHashMap();
    private Map processedArchives = new Object2ObjectOpenHashMap();
    private Object2ObjectOpenHashMap classNameToJavaClass = new Object2ObjectOpenHashMap();
    /** 
     * Creates a new <code>ClassPathExplorer</code> with the default
     * class path, which is obtained from {@link adaptj_pool.Scene Scene}
     */
    private ClassPathExplorer() { // no instances
        soughtClassName = null;
    }

    /** 
     * Creates a new <code>ClassPathExplorer</code> with the specified
     * class path.
     */
    private ClassPathExplorer(List classPath) {// no instances
        soughtClassName = null;
        this.classPath = new LinkedList(classPath);
        /* processClassPath(classPath); */
    }
    
    /** Returns the singleton instance of the <code>ClassPathExplorer</code>
     * class. This instance is only created when this method is called for 
     * the first time, so that no memory and/or time is wasted when not needed.
     *
     * @return the singleton instance of the <code>ClassPathExplorer</code> class.
     */
    public static ClassPathExplorer v() {
        /* Make sure that no instance is created if
           not needed */
        if (instance == null) {
            instance = new ClassPathExplorer();
        }
        
        return instance;
    }

    /**
     * Resets the instance of the <code>ClassPathExplorer</code> to its default values.
     */
    public void reset() {
        instance = null;
    }

    public void setClassPath(List classpath) {
        this.classPath = new LinkedList(classpath);
    }
    
    /** 
     * Tries to create a new <code>JavaClass</code> corresponding to the name specified by
     * <code>className</code>.
     *
     * @param className the fully qualified name of the class to be resolved.
     * @return a <code>JavaClass</code> object for the specified class, or <code>null</code>
     * if it cannot be resolved.
     */
    public JavaClass getJavaClass(String className) {
        if (classNameToJavaClass.containsKey(className)) {
            return (JavaClass) classNameToJavaClass.get(className);
        }
        try {
            resolveClass(className, null);
        } catch (ClassFoundException e) {
            previousResults.put(className, e.getCFLocation());
            try {
                if (e.isArchive()) {
                    ZipFile zip = e.getZipFile();
                    if (zip != null) {
                        zip.close();
                    }

                    JavaClass clazz = (new ClassParser(e.getPath(), e.getZipEntryName())).parse();
                    classNameToJavaClass.put(className, clazz);
                    return clazz;
                } else {
                    JavaClass clazz = (new ClassParser(e.getPath())).parse();
                    classNameToJavaClass.put(className, clazz);
                    return clazz;
                }
            } catch (IOException ioe) {
                //throw new RuntimeException("Class not found");
                return null;
            }
        }

        //throw new RuntimeException("Class not found");
        return null;
    }
    
    /**
     * Tries to resolve the class name specified by <code>className</code> based
     * on the current class path. The return value consists of the name of the file
     * which defines this class.
     *
     * @param className the fully qualified name of the class to be resolved. For example,
     *                  <code>java.lang.String</code>.
     * 
     * @return the name of the file which defines this class. If the file is a zip or jar
     *         file, then the entry name of the class is appended at the end of the result, within
     *         square brackets.
     */
    public String getClassFileName(String className) {
        try {
            resolveClass(className, null);
        } catch (ClassFoundException e) {
            previousResults.put(className, e.getCFLocation());
            if (e.isArchive()) {
                return e.getPath() + "[" + e.getZipEntryName() + "]";
            } else {
                return e.getPath();
            }
        }

        return null;
    }
    
    /**
     * Tries  resolve the class name specified by <code>className</code> based
     * on the current class path. The return value consists of an open
     * {@link java.io.DataInputStream DataInputStream} object opened for reading
     * the contents of the class file, or <code>null</code> if the class cannot be found.
     *
     * @param className the fully qualified name of the class to be resolved. For example,
     *                  <code>java.lang.String</code>.
     * 
     * @return an open {@link java.io.DataInputStream DataInputStream} object
     *         or <code>null</code> if the class cannot be found.
     */
    public DataInputStream getInputStream(String className) {
        try {
            resolveClass(className, null);
        } catch (ClassFoundException e) {
            previousResults.put(className, e.getCFLocation());
            try {
                if (e.isArchive()) {
                    ZipFile zip = e.getZipFile();
                    ZipEntry entry = e.getZipEntry();
                    if (zip == null) {
                        zip = new ZipFile(e.getPath());
                        entry = zip.getEntry(e.getZipEntryName());
                    }
                    InputStream inStream = zip.getInputStream(entry);
                    zip.close();
                    return new DataInputStream(inStream);
                } else {
                    return new DataInputStream(new FileInputStream(e.getPath()));
                }
            } catch (IOException ioe) {
            }
        }

        return null;
    }

    /**
     * Tries to resolve <code>className</code> based on <code>classPath</code>. 
     *
     * @param className the fully qualified name of the class to be resolved
     * @param classPath a list of zip/jar/class files/directories constituting 
     *                  the class path to use for resolving the class.
     */
    private void resolveClass(String className, List classPath) {
        if (previousResults.containsKey(className)) {
            throw new ClassFoundException((ClassFileLocation) previousResults.get(className));
        }
 
        if (classPath == null) {
            if (this.classPath != null) {
                classPath = this.classPath;
            } else {
                classPath = Scene.v().getClassPath();
            }
        }
 
        Iterator it = classPath.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof String) {
                String s = (String) o;
                File f = new File(s);
                
                if (!f.exists()) {
                    Scene.v().showWarning("Classpath entry \"" + s + "\" does not exist");
                    continue;
                }

                if (isArchive(f)) {
                    /* Check if we need to look into this archive */
                    Map m = (Map) processedArchives.get(f.getPath());
                    if (m == null) {
                        /* We do */
                        processArchive(className, f);
                    } else {
                        /* We don't */
                        ClassFileLocation cfLoc = (ClassFileLocation) m.get(className);
                        if (cfLoc != null) {
                            throw new ClassFoundException(cfLoc);
                        }
                    }
                } else if (isClassFile(f)) {
                    processClassFile(className, f);
                } else if (f.isDirectory()) {
                    processDirectory(className, f);
                } else {
                    Scene.v().showWarning("Unknown entry type for \"" + s + "\"");
                }
            } else {
                throw new RuntimeException("Classpath has to consist of String objects only!!");
            }   
        }
    }
    
    /**
     * Looks for <code>className</code> within <code>archive</code>.
     *
     * @param className the fully qualified name of the class to look for
     * @param archive an archive (zip or jar) file to look into
     */
    private void processArchive(String className, File archive) {
            ClassFoundException cfe = null;
            ZipFile zip;
            try {
                zip = new ZipFile(archive);
            } catch (ZipException e) {
                Scene.v().reportFileOpenError(archive.toString());
                return;
            } catch (IOException e) {
                Scene.v().reportFileOpenError(archive.toString());
                return;
            }

            Map archivedClasses = new Object2ObjectOpenHashMap();
            Enumeration enum = zip.entries();
            while (enum.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) enum.nextElement();
                String entryName = entry.getName();

                /* make sure that the class name is valid */
                if (entryName != null && entryName.endsWith(".class")) {
                    String tempClassName = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                    ClassFileLocation cfLoc = new ClassFileLocation();
                    try {
                        cfLoc.path = archive.getCanonicalPath();
                    } catch (IOException e) {
                        Scene.v().reportFileOpenError(archive.toString());
                        if (cfe != null) {
                            throw cfe;
                        }
                        return;
                    }
                    cfLoc.isArchive = true;
                    cfLoc.zipEntryName = entryName;
                    if (!archivedClasses.containsKey(tempClassName)) {
                        archivedClasses.put(tempClassName, cfLoc);
                    }
                    if (cfe == null && tempClassName.equals(className)) {
                        cfe = new ClassFoundException(cfLoc, zip, entry);
                    } 
                }
            }

            processedArchives.put(archive.getPath(), archivedClasses);
            if (cfe != null) {
                throw cfe;
            }
    }

    /**
     * Checks whether <code>classFile</code> defines <code>className</code>.
     *
     * @param className the fully qualified name of the class to look for.
     * @param classFile a class file (must exist and be a regular file) to check for a match
     */
    private void processClassFile(String className, File classFile) {
        /* This method is only invoked when the file exists and is a regular file,
           so these conditions are not checked again */
        String tempClassName = getClassName(classFile);
        if (tempClassName.indexOf('.') >= 0) {
            /* This class file is part of a package, which does not match
               the location of the candidate file */
            return;
        }
        if (tempClassName.equals(className)) {
            /* This is a match */
            
            ClassFileLocation cfLoc = new ClassFileLocation();
            try {
                cfLoc.path = classFile.getCanonicalPath();
                cfLoc.isArchive = false;
                throw new ClassFoundException(cfLoc);
                
            } catch (IOException e) {
                Scene.v().reportFileOpenError(classFile.toString());
            }
        }
    }

    /**
     * Checks whether <code>className</code> is defined in one of the class files
     * in <code>dir</code>. The method first computes the theoretical location
     * of such a class file, then looks for it within <code>dir</code>, and finally
     * makes sure that the class name defined by the class file (if found) matches
     * the one we are looking for.
     *
     * @param className the fully qualified name of a class to look for
     * @param dir a directory to look into
     */
    private void processDirectory(String className, File dir) {
        /* If the class we are looking for is located in this directory,
           then theoreticalPath must represent its location */
        String theoreticalPath = dir.getPath() + File.separator + className.replace('.', File.separatorChar) + ".class";
        File theoreticalFile = new File(theoreticalPath);

        if (theoreticalFile.exists() && theoreticalFile.isFile()) {
            /* This could be a match */
            String tempClassName = getClassName(theoreticalFile);
            if (tempClassName != null && tempClassName.equals(className)) {
                /* This is a match */

                ClassFileLocation cfLoc = new ClassFileLocation();
                try {
                    cfLoc.path = theoreticalFile.getCanonicalPath();
                    cfLoc.isArchive = false;
                    throw new ClassFoundException(cfLoc);
                    
                } catch (IOException e) {
                    Scene.v().reportFileOpenError(theoreticalPath);
                }
            }
        }
    }

    /**
     * Extracts the name of the class defined in a class file.
     *
     * @param classFile A <code>File</code> object representing the
     *        class file to be operated on.
     * @return the name of the class defined in <code>classFile</code>, or
     *         <code>null</code> if an error occurs.
     */
    private String getClassName(File classFile) {
        try {
            DataInputStream stream = new DataInputStream(new FileInputStream(classFile));

            ClassParser classParser = new ClassParser(stream, classFile.getName());
            JavaClass clazz = classParser.parse();
            return clazz.getClassName();
            /**
            ClassFile cf = new ClassFile(classFile.getName());
            if (cf.read(stream)) {
                return cf.getClassName().replace('/', '.');
            }
            **/
        } catch (FileNotFoundException e) {
            Scene.v().reportFileNotFoundError(classFile.toString());
        } catch (IOException e) {
            Scene.v().reportFileOpenError(classFile.toString());
        }

        return null;
    }

    /**
     * Determines whether the file designated by <code>path</code> appears
     * to be a java archive (zip or jar).
     *
     * @param path the name of a file to test
     * @return <code>true</code> if the file appears to be a valid archive,
     * <code>false</code> otherwise.
     */
    public boolean isArchive(String path) {
        File f = new File(path);
        return isArchive(f);
    }

    /**
     * Determines whether the file designated by <code>file</code> appears
     * to be a java archive (zip or jar).
     *
     * @param file a <code>File</code> object representing a file to test
     * @return <code>true</code> if the file appears to be a valid archive,
     * <code>false</code> otherwise.
     */
    public boolean isArchive(File f) {
        if(f.isFile() && f.canRead()) {
            String path;
            try {
                path = f.getCanonicalPath();
            } catch(IOException e) {
                return false;
            }

            if(path.endsWith("zip") || path.endsWith("jar")) {
                return true;
            } else {
                Scene.v().showWarning("Classpath entry \"" + path + "\" is not a supported archive file (must be .zip or .jar)");
            }
        }
        return false;
    }
    
    /**
     * Determines whether the file designated by <code>filename</code> appears
     * to be a class file.
     *
     * @param path the name of a file to test
     * @return <code>true</code> if the file appears to be a valid class file,
     * <code>false</code> otherwise.
     */

    public boolean isClassFile(String filename) {
        File f = new File(filename);
        return isClassFile(f);
    }
    
    /**
     * Determines whether the file designated by <code>file</code> appears
     * to be a class file.
     *
     * @param file a <code>File</code> object representing a file to test
     * @return <code>true</code> if the file appears to be a valid class file,
     * <code>false</code> otherwise.
     */
    public boolean isClassFile(File file) {
        try {
            if (!file.isFile()) {
                return false;
            }
            DataInputStream stream = new DataInputStream(new FileInputStream(file));

            String path = file.getCanonicalPath();
            if (path.endsWith(".jimple")) {
                return true;
            }

            if (path.endsWith(".class") && checkMagic(stream)) {
                return true;
            }

            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Extracts the first <code>int</code> of <code>classFileStream</code> and compares
     * it against the default java magic number, <code>0xCAFEBABE</code>
     *
     * @param classFileStream a <code>DataInputStream</code> to read an int from.
     * @return <code>true</code> if the <code>int</code> that is read matches
     *         the java magic number, <code>false</code> otherwise.
     */
    private boolean checkMagic(DataInputStream classFileStream) {
        try {
            return ((classFileStream.readInt() & 0xFFFFFFFFL) == 0xCAFEBABEL);
        } catch (IOException e) {
            return false;
        }
    }

    class ClassFileLocation {
        public String path;
        public boolean isArchive;
        public String zipEntryName;
    }


    class ClassFoundException extends RuntimeException {
        private ClassFileLocation loc;
        private ZipEntry zipEntry;
        private ZipFile zipFile;
        
        
        public ClassFoundException(ClassFileLocation loc) {
            this.loc = loc;
            this.zipFile = null;
            this.zipEntry = null;
        }

        public ClassFoundException(ClassFileLocation loc, ZipFile zipFile, ZipEntry zipEntry) {
            this.loc = loc;
            this.zipFile = zipFile;
            this.zipEntry = zipEntry;
        }

        public String getPath() {
            return this.loc.path; 
        }

        public boolean isArchive() {
            return this.loc.isArchive;
        }

        public String getZipEntryName() {
            return this.loc.zipEntryName;
        }

        public ClassFileLocation getCFLocation() {
            return this.loc;
        }

        public ZipFile getZipFile() {
            return this.zipFile;
        }

        public ZipEntry getZipEntry() {
            return this.zipEntry;
        }
    }
}

