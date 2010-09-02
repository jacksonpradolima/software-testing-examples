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

#include "ccfr.h"

/* #define SHOW_DEBUG_CLASS_MSGS */

int parseClass(classfile_t *class, ccfrFile_t *f) {
#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "PARSING CLASS\n");
#endif
    if (class == NULL) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }
 
#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "Reading Magic\n");
#endif
    if (!readMagic(class, f)) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }
    
#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "Reading Version\n");
#endif
    if (!readVersion(class, f)) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }

#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "Reading Constant Pool\n");
#endif
    if (!readConstantPool(class, f)) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }

#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "Reading Class Info\n");
#endif
    if (!readClassInfo(class, f)) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }
    
#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "Reading Interfaces\n");
#endif
    if (!readInterfaces(class, f)) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }
    
#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "Reading Fields\n");
#endif
    if (!readFields(class, f)) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }
    
#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "Reading Methods\n");
#endif
    if (!readMethods(class, f)) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }
    
#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "Reading Attributes\n");
#endif
    if (!readAttributes(class, f)) {
        fprintf(stderr, "CLASS PARSE ERR\n");
        return 0;
    }
    

#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "CLASS PARSED OK\n");
#endif
    return 1;
}

int readMagic(classfile_t *class, ccfrFile_t *f) {
    u4 magic;
    if (!read_u4(&magic, f)) {
        showError("Error reading magic number");
        return 0;
    }

    if (magic != MAGIC_NUMBER) {
        showError("Invalid magic number");
        return 0;
    }
    class->magic = magic;
    return 1;
}

int readVersion(classfile_t *class, ccfrFile_t *f) {
    u2 minor;
    u2 major;
    if (!read_u2(&minor, f)) {
        showError("Error reading minor version");
        return 0;
    }

    if (!read_u2(&major, f)) {
        showError("Error reading minor version");
        return 0;
    }
    
    class->minor_version = minor;
    class->major_version = major;

    return 1;
}

int readConstantPool(classfile_t *class, ccfrFile_t *f) {
    u2 i;
    u1 tag;
    
    /* Read the CP item count */
    if (!read_u2(&(class->constant_pool_count), f)) {
        return 0;
    }

    /* Allocate mem for the CP item array */
    class->constant_pool = (cp_info_t *) malloc(class->constant_pool_count * sizeof(cp_info_t));  /* NEW_ARRAY(cp_info_t, class->constant_pool_count); */
    if (class->constant_pool == NULL) {
        return 0;
    }
    
#ifdef SHOW_DEBUG_CLASS_MSGS
    fprintf(stderr, "STARTING TO READ %d CP ITEMS\n", class->constant_pool_count);
#endif
    
    /* Read the CP */
    for (i = 1; i < class->constant_pool_count; i++) {
        if (!readCPItem(class->constant_pool + i, f)) {
            free(class->constant_pool);
            class->constant_pool = NULL;
            return 0;
        }

        tag = class->constant_pool[i].tag;
        
        if (tag == CONSTANT_Long || tag == CONSTANT_Double) {
            /* 8-bit constants take up 2 spaces in the CP */
            class->constant_pool[++i].tag = ((u1) tag | CONSTANT_Cont_mask);
        }
    }

    return 1;
}

int readClassInfo(classfile_t *class, ccfrFile_t *f) {
    if (!read_u2(&(class->access_flags), f)) {
        return 0;
    }
    
    if (!read_u2(&(class->this_class), f)) {
        return 0;
    }

    return read_u2(&(class->super_class), f);
}


int readInterfaces(classfile_t *class, ccfrFile_t *f) {
    int i;

    if (!read_u2(&(class->interfaces_count), f)) {
        return 0;
    }

    if (class->interfaces_count == 0) {
        class->interfaces = NULL;
        return 1;
    }

    class->interfaces = NEW_ARRAY(u2, class->interfaces_count);

    for (i = 0; i < class->interfaces_count; i++) {
        if (!read_u2(class->interfaces + i, f)) {
            free(class->interfaces);
            class->interfaces = NULL;
            return 0;
        }
    }

    return 1;
}

int readFields(classfile_t *class, ccfrFile_t *f) {
    int i;
    
    if (!read_u2(&(class->fields_count), f)) {
        return 0;
    }

    if (class->fields_count > 0) {
        class->fields = NEW_ARRAY(field_info_t, class->fields_count);
        if (class->fields == NULL) {
            return 0;
        }
        
        for (i = 0; i < class->fields_count; i++) {
            if (!readFieldInfo(class->fields + i, class->constant_pool, f)) {
                free(class->fields);
                class->fields = NULL;
                return 0;
            }
        }
    } else {
        class->fields = NULL;
    }

    return 1;
}

int readMethods(classfile_t *class, ccfrFile_t *f) {
    int i;
    
    if (!read_u2(&(class->methods_count), f)) {
        return 0;
    }

    if (class->methods_count > 0) {
        class->methods = NEW_ARRAY(method_info_t, class->methods_count);
        if (class->methods == NULL) {
            return 0;
        }
        
        for (i = 0; i < class->methods_count; i++) {
            if (!readMethodInfo(class->methods + i, class->constant_pool, f)) {
                free(class->methods);
                class->methods = NULL;
                return 0;
            }
        }
    } else {
        class->methods = NULL;
    }

    return 1;
}

int readAttributes(classfile_t *class, ccfrFile_t *f) {
    if (!read_u2(&(class->attributes_count), f)) {
        return 0;
    }
    
    return read_attributes(&(class->attributes), class->attributes_count, class->constant_pool, f);
}

void initClassfile(classfile_t *class) {
    if (class != NULL) {
        class->magic = ((u4) 0);
        class->major_version = ((u2) 0);
        class->minor_version = ((u2) 0);
        class->constant_pool_count = ((u2) 0);
        class->constant_pool = NULL;
        class->access_flags = ((u2) 0);
        class->this_class = ((u2) 0);
        class->super_class = ((u2) 0);
        class->interfaces_count = ((u2) 0);
        class->interfaces = NULL;
        class->fields_count = ((u2) 0);
        class->fields = NULL;
        class->methods_count = ((u2) 0);
        class->methods = NULL;
        class->attributes_count = ((u2) 0);
        class->attributes = NULL;
    }
}

void cleanupAttributes(attribute_info_t *attribs, u2 count) {
    u2 i;

    for (i = 0; i < count; i++) {
        switch (attribs[i].type) {
            case CODE_ATTR:
                cleanupAttributes(attribs[i].u.Code.attributes,
                    attribs[i].u.Code.attributes_count);
                if (attribs[i].u.Code.code != NULL) {
                    free(attribs[i].u.Code.code);
                }
                if (attribs[i].u.Code.exception_table != NULL) {
                    free(attribs[i].u.Code.exception_table);
                }
                break;
            case EXCEPTIONS_ATTR:
                if (attribs[i].u.Exceptions.exception_index_table != NULL) {
                    free(attribs[i].u.Exceptions.exception_index_table);
                }
                break;
            case INNER_CLASSES_ATTR:
                if (attribs[i].u.InnerClasses.classes != NULL) {
                    free(attribs[i].u.InnerClasses.classes);
                }
                break;
            case LINE_NUMBER_TABLE_ATTR:
                if (attribs[i].u.LineNumberTable.line_number_table != NULL) {
                    free(attribs[i].u.LineNumberTable.line_number_table);
                }
                break;
            case LOCAL_VARIABLE_TABLE_ATTR:
                if (attribs[i].u.LocalVariableTable.local_variable_table != NULL) {
                    free(attribs[i].u.LocalVariableTable.local_variable_table);
                }
                break;
            case GENERIC_ATTR:
                if (attribs[i].u.generic.info != NULL) {
                    free(attribs[i].u.generic.info);
                }
        }
    }
    free(attribs);
}

void cleanupClassfile(classfile_t *class) {
    u2 i;
    if (class != NULL) {
        if (class->constant_pool != NULL) {
            free(class->constant_pool);
        }
        if (class->interfaces != NULL) {
            free(class->interfaces);
        }
        if (class->fields != NULL) {
            for (i = 0; i < class->fields_count; i++) {
                cleanupAttributes(class->fields[i].attributes,
                        class->fields[i].attributes_count);
            }
            free(class->fields);
        }

        if (class->methods != NULL) {
            for (i = 0; i < class->methods_count; i++) {
                cleanupAttributes(class->methods[i].attributes,
                        class->methods[i].attributes_count);
            }
            free(class->methods);
        }

        if (class->attributes != NULL) {
            cleanupAttributes(class->attributes, class->attributes_count);
        }
    }
}

int getClassName(classfile_t *class, char *s, int sLen) {
    u2 index = class->this_class;
    cp_info_t *cp = class->constant_pool;
    
    if ((index <= (u2) 0) || (index >= class->constant_pool_count)) {
        return -1;
    }

    if (cp[index].tag == CONSTANT_Class) {
        u2 name_index;
        u2 length;
        u1 *bytes;

        name_index = cp[index].u.CONSTANT_Class_info.name_index;
        if (cp[name_index].tag == CONSTANT_Utf8) {
            int result;
            length = cp[name_index].u.CONSTANT_Utf8_info.length;
            bytes = cp[name_index].u.CONSTANT_Utf8_info.bytes;

            result = UTF8toCString(bytes, length, s, sLen);

            if (result == 1) {
                replace(s, '/', '.');
            }
            return result;
        }
    }

    return -1;
}

int getMethodByName(classfile_t *class, char *method_name, char *signature, method_info_t **method) {
    u2 i;
    char mname[2048];
    char msig[2048];

    for (i = 0; i < class->methods_count; i++) {
        u2 name_index;
        u2 sig_index;
        cp_info_t *cp;

        cp = class->constant_pool;
        name_index = class->methods[i].name_index;
        sig_index = class->methods[i].descriptor_index;

        if ((name_index <= 0)
                || (sig_index <= 0)
                || (name_index >= class->constant_pool_count)
                || (sig_index >= class->constant_pool_count)) {
            continue;
        }

        if (cp[name_index].tag != CONSTANT_Utf8) {
            continue;
        }

        if (UTF8toCString(cp[name_index].u.CONSTANT_Utf8_info.bytes,
                    cp[name_index].u.CONSTANT_Utf8_info.length,
                    mname,
                    2048) != 1) {
            continue;
        }

        if (strcmp(mname, method_name)) {
            continue;
        }

        if (UTF8toCString(cp[sig_index].u.CONSTANT_Utf8_info.bytes,
                    cp[sig_index].u.CONSTANT_Utf8_info.length,
                    msig,
                    2048) != 1) {
            continue;
        }

        if (!strcmp(msig, signature)) {
            *method = class->methods + i;
            return 1;
        }
    }

    return 0;
}
