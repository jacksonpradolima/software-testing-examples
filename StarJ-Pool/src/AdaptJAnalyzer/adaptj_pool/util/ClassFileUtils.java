package adaptj_pool.util;

import org.apache.bcel.classfile.*;

public class ClassFileUtils {
    private ClassFileUtils() {
        // no instances
    }

    public static boolean definesMethod(JavaClass c, String method_name, String method_signature) {
        if (c == null) {
            return false;
        }

        Method[] class_methods = c.getMethods();
        for (int i = 0; i < class_methods.length; i++) {
            Method m = class_methods[i];
            if (m.getName().equals(method_name)
                    && m.getSignature().equals(method_signature)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasMethod(JavaClass c, String method_name, String method_signature) {
        if (c == null) {
            return false;
        }

        return definesMethod(c, method_name, method_signature)
                || hasMethod(getSuperClass(c), method_name, method_signature);
    }

    public static boolean matchesInterface(JavaClass c, JavaClass iface,
            String method_name, String method_signature) {
        // If we have an invokeinterface call, then the situation
        // the complexity of the operation can be high.
        // The simplest (and most frequent) case occurs when the dynamic
        // receiver implements the interface directly (case #1). However, this
        // does not have to be the case (case #2). For example, a call
        // to java.util.Set.iterator()Ljava/util/Iterator; may actually
        // at runtime be executed as 
        // java.util.Collections$SynchronizedCollection.iterator()Ljava/util/Iterator;
        // (this is a real example), which complicates matters a bit. The real
        // receiver must have been a subclass of SynchronizedCollection
        // which implements java.util.Set (i.e. a synchronized set), but
        // which did not override the iterator() method, resulting in
        // such a dynamic behaviour. In that case, we need to make sure
        // that:
        //   1) Case #1 fails
        //   2) There exists a superinterface X of iface which defines
        //      the appropriate method, and that the runtime receiver
        //      implements that method.
            
        if (!definesMethod(c, method_name, method_signature)) {
            return false;
        }

        if (isImplementation(c, iface)) {
            return true;
        }

        String[] iface_names = iface.getInterfaceNames();
        if (iface_names != null) {
            for (int i = 0; i < iface_names.length; i++) {
                JavaClass sup_iface = ClassPathExplorer.v().getJavaClass(iface_names[i]);
                // sup_iface is a superinterface of iface
                if (definesMethod(sup_iface, method_name, method_signature)
                        && matchesInterface(c, sup_iface, method_name, method_signature)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isImplementation(JavaClass c, JavaClass inter) {
        if (c == null) {
            return false;
        }

        String inter_name = inter.getClassName();
        String[] iface_names = c.getInterfaceNames();
        if (iface_names != null) {
            for (int i = 0; i < iface_names.length; i++) {
                String iface_name = iface_names[i];
                if (inter_name.equals(iface_name)) {
                    // Easy case -- exact match
                    return true;
                }

                // Does this interface extend the desired interface?
                JavaClass iface = ClassPathExplorer.v().getJavaClass(iface_name);
                if (isImplementation(iface, inter)) {
                    // yes
                    return true;
                } // else look at the next interface
            }
        }

        return isImplementation(getSuperClass(c), inter); 
    }

    public static boolean isSubclass(JavaClass sub, JavaClass sup) {
        if (sub == null || sup == null) {
            return false;
        }
        String sup_name = sup.getClassName();
        JavaClass tmp = getSuperClass(sub);
        while (tmp != null) {
            if (sup_name.equals(tmp.getClassName())) {
                return true;
            }
            tmp = getSuperClass(tmp);
        }

        return false;
    }

    public static JavaClass getSuperClass(JavaClass c) {
        // The following code is really ugly but required because of how
        // BCEL handles super classes. FIXME.
        if ("java.lang.Object".equals(c.getClassName())) {
            return null;
        }

        return ClassPathExplorer.v().getJavaClass(c.getSuperclassName());
    }
}
