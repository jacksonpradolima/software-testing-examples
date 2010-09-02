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

#include "AdaptJAgent.h"

char buff[CPE_MAX_BUFF];
boolean mapInitialized = false;
AdaptJZipMap_t classNameToJar;

void initMap(const char **classPath, int cpSize) {
    int i;
    struct stat s;

    initZipMap(&classNameToJar, 500, 0.7, stringHash, stringEq);

    for (i = 0; i < cpSize; i++) {
        if (stat(classPath[i], &s) >= 0) {
            if (S_ISREG(s.st_mode) && (endsWith(classPath[i], ".jar")
                    || endsWith(classPath[i], ".zip")))
            {
                /* Add all of the class files from this jar file
                 * to the growing hash map */
                
                /* FIXME: cleaner method can be used ... */

                unzFile uf;
                unz_global_info gi;
                uLong j;
                char *s;
                
                uf = unzOpen(classPath[i]);

                if (uf == NULL) {
                    fprintf(stderr, "Unable to open \"%s\"\n", classPath[i]);
                    continue;
                }
                
                if (unzGetGlobalInfo (uf,&gi) != UNZ_OK) {
                    fprintf(stderr, "Failed to get Global Info\n");
                    unzClose(uf);
                    continue;
                }
                
                for (j = 0; j < gi.number_entry; j++) {
                    char filename_inzip[256];
                    unz_file_info file_info;
                    /* int unused; */
                    
                    if (unzGetCurrentFileInfo(uf,&file_info,filename_inzip,sizeof(filename_inzip),NULL,0,NULL,0) != UNZ_OK) {
                        fprintf(stderr, "Failed to get file info\n");
                        unzClose(uf);
                        continue;
                    }

                    if (endsWith(filename_inzip, ".class")) {
                        size_t nameLen;
                        JarEntry_t *jarEntry;

                        jarEntry = NEW(JarEntry_t);
                        if (jarEntry == NULL) {
                            fprintf(stderr, "Failed to create new jar entry\n");
                            unzCloseCurrentFile(uf);
                            unzClose(uf);
                            continue;
                        }
                        
                        nameLen = strlen(filename_inzip);
                        s = (char *) calloc(nameLen - 5, sizeof(char));
                        strncpy(s, filename_inzip, nameLen - 6);
                        replace(s, '/', '.');
                        jarEntry->path = (char *) classPath[i];
                        if (unzGetFilePos(uf, &(jarEntry->pos)) == UNZ_OK) {
                            if (zipMapPut(&classNameToJar, s, jarEntry) != ZIP_MAP_OK) {
                                fprintf(stderr, "Failed to store the relationship \"%s\" --> \"%s\"\n", s, classPath[i]);
                            }
                        } else {
                            fprintf(stderr, "Failed to store the relationship (2) \"%s\" --> \"%s\"\n", s, classPath[i]);
                        }
                    }

                    unzCloseCurrentFile(uf);
                    if ((j != gi.number_entry - 1) && (unzGoToNextFile(uf) != UNZ_OK)) {
                        fprintf(stderr, "Could not fetch next file\n");
                        unzCloseCurrentFile(uf);
                        unzClose(uf);
                        continue;
                    }
                }
                unzCloseCurrentFile(uf);
                unzClose(uf);
            }   
        }
    }
}

ccfrFile_t *openArchivedClass(const JarEntry_t *jarEntry) {
    return openArchiveFileLocation(jarEntry->path, &(jarEntry->pos));
}

ccfrFile_t *openClassFile(const char *classfile) {
    return openRegularFile(classfile);
}

int processArchive(const char *className, const JarEntry_t *jarEntry, classfile_t *class) {
    ccfrFile_t *f;
    
    f = openArchivedClass(jarEntry);
    if (f != NULL) {
        if (parseClass(class, f)) {
            if (getClassName(class, buff, CPE_MAX_BUFF) == 1) {
                if (!strcmp(className, buff)) {
                    closeCCFRFile(f);
                    return 1;
                }
            } 

            cleanupClassfile(class);
        }
        closeCCFRFile(f);
    }

    return 0;
}

int processClassFile(const char *className, const char *classFile, classfile_t *class) {
    ccfrFile_t *f;
 
    f = openRegularFile(classFile);
    if (f != NULL) {
        if (parseClass(class, f)) {
            if (getClassName(class, buff, CPE_MAX_BUFF) == 1) {
                if (!strcmp(className, buff)) {
                    closeCCFRFile(f);
                    return 1;
                }
            }

            cleanupClassfile(class);
        }
        closeCCFRFile(f);
    }

    return 0;
}

int resolveClass(const char *className, const char **classPath, int cpSize, classfile_t *class) {
    int i;
    struct stat s;
    JarEntry_t *jarEntry;

    /*
    if (!mapInitialized) {
        initMap(classPath, cpSize);
        mapInitialized = true;
    }
    */
    
    initClassfile(class);
    
    if (zipMapGet(&classNameToJar, className, &jarEntry) == ZIP_MAP_OK) {
        if (processArchive(className, jarEntry, class) > 0) {
            return 1;
        }
    } 
    
    for (i = 0; i < cpSize; i++) {
        if (stat(classPath[i], &s) >= 0) {
            if (S_ISREG(s.st_mode)) {
                /* Only check .class files, since archives
                 * have been processed earlier */
                if (endsWith(classPath[i], ".class")) {
                    if(processClassFile(className, classPath[i], class) > 0) {
                        return 1;
                    }
                }
            } else if (S_ISDIR(s.st_mode)) {
                /* Look for the appropriate class file in dir */
                if (endsWith(classPath[i], "/")) {
                    strcpy(buff, classPath[i]);
                    strcat(buff, className);
                    replace(buff + strlen(classPath[i]), '.', '/');
                    strcat(buff, ".class");
                } else {
                    strcpy(buff, classPath[i]);
                    strcat(buff, "/");
                    strcat(buff, className);
                    replace(buff + strlen(classPath[i]) + 1, '.', '/');
                    strcat(buff, ".class");
                }

                if ((stat(buff, &s) >= 0) && (S_ISREG(s.st_mode))) {
                    /* It exits and is a regular file */
                    if(processClassFile(className, buff, class) > 0) {
                        return 1;   
                    }
                }
            }
        }
    }

    fprintf(stderr, "Could not resolve: %s\n", className);

    return 0;
}
