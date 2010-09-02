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

int readAttribute(attribute_info_t *attr, cp_info_t *cp, ccfrFile_t *f) {
    u2 i;
    if (!read_u2(&(attr->attribute_name_index), f)) {
        return 0;
    }

    if (!read_u4(&(attr->attribute_length), f)) {
        return 0;
    }

    i = attr->attribute_name_index;
    if (cp[i].tag == CONSTANT_Utf8) {
        u1 *bytes = cp[i].u.CONSTANT_Utf8_info.bytes;
        u2 length = cp[i].u.CONSTANT_Utf8_info.length;

        if (!strncmp(bytes, CONSTANT_VALUE, CONSTANT_VALUE_SIZE)
                && (length == CONSTANT_VALUE_SIZE)) {
            attr->type = CONSTANT_VALUE_ATTR;
            return readConstantValueAttribute(attr, f);
        }
        
        if (!strncmp(bytes, CODE, CODE_SIZE)
                && (length == CODE_SIZE)) {
            attr->type = CODE_ATTR;
            return readCodeAttribute(attr, cp, f);
        }
        
        if (!strncmp(bytes, EXCEPTIONS, EXCEPTIONS_SIZE)
                && (length == EXCEPTIONS_SIZE)) {
            attr->type = EXCEPTIONS_ATTR;
            return readExceptionsAttribute(attr, f);
        }

        if (!strncmp(bytes, INNER_CLASSES, INNER_CLASSES_SIZE)
                && (length == INNER_CLASSES_SIZE)) {
            attr->type = INNER_CLASSES_ATTR;
            return readInnerClassesAttribute(attr, f);
        }
        
        if (!strncmp(bytes, SYNTHETIC, SYNTHETIC_SIZE)
                && (length == SYNTHETIC_SIZE)) {
            /* Synthetic Attribute has no specific data */
            attr->type = SYNTHETIC_ATTR;
            return (attr->attribute_length == ((u4) 0));
        }

        if (!strncmp(bytes, SOURCE_FILE, SOURCE_FILE_SIZE)
                && (length == SOURCE_FILE_SIZE)) {
            attr->type = SOURCE_FILE_ATTR;
            return readSourceFileAttribute(attr, f);
        }

        if (!strncmp(bytes, LINE_NUMBER_TABLE, LINE_NUMBER_TABLE_SIZE)
                && (length == LINE_NUMBER_TABLE_SIZE)) {
            attr->type = LINE_NUMBER_TABLE_ATTR;
            return readLineNumberTableAttribute(attr, f);
        }

        if (!strncmp(bytes, LOCAL_VARIABLE_TABLE, LOCAL_VARIABLE_TABLE_SIZE)
                && (length == LOCAL_VARIABLE_TABLE_SIZE)) {
            attr->type = LOCAL_VARIABLE_TABLE_ATTR;
            return readLocalVariableTableAttribute(attr, f);
        }

        if (!strncmp(bytes, DEPRECATED, DEPRECATED_SIZE)
                && (length == DEPRECATED_SIZE)) {
            attr->type = DEPRECATED_ATTR;
            /* Synthetic Attribute has no specific data */
            return (attr->attribute_length == ((u4) 0));
        }
        
        /* Fall back to Generic attribute */
        attr->type = GENERIC_ATTR;
        return readGenericAttribute(attr, f);
        
    }
    
    return 0;
}

int readConstantValueAttribute(attribute_info_t *attr, ccfrFile_t *f) {
    return read_u2(&(attr->u.ConstantValue.constantvalue_index), f);
}

int readCodeAttribute(attribute_info_t *attr, cp_info_t *cp, ccfrFile_t *f) {
    if (!read_u2(&(attr->u.Code.max_stack), f)) {
        return 0;
    }

    if (!read_u2(&(attr->u.Code.max_locals), f)) {
        return 0;
    }

    if (!read_u4(&(attr->u.Code.code_length), f)) {
        return 0;
    }

    if (!read_u1_table(&(attr->u.Code.code), attr->u.Code.code_length, f)) {
        return 0;
    }

    if (!read_u2(&(attr->u.Code.exception_table_length), f)) {
        return 0;
    }

    if (!read_exception_table(&(attr->u.Code.exception_table), attr->u.Code.exception_table_length, f)) {
        return 0;
    }

    if (!read_u2(&(attr->u.Code.attributes_count), f)) {
        return 0;
    }

    return read_attributes(&(attr->u.Code.attributes), attr->u.Code.attributes_count, cp, f);
}

int readExceptionsAttribute(attribute_info_t *attr, ccfrFile_t *f) {
    if (!read_u2(&(attr->u.Exceptions.number_of_exceptions), f)) {
        return 0;
    }

    return read_u2_table(&(attr->u.Exceptions.exception_index_table), (u4) attr->u.Exceptions.number_of_exceptions, f);
}

int readInnerClassesAttribute(attribute_info_t *attr, ccfrFile_t *f) {
    if (!read_u2(&(attr->u.InnerClasses.number_of_classes), f)) {
        return 0;
    }

    return read_classes_table(&(attr->u.InnerClasses.classes), (u4) attr->u.InnerClasses.number_of_classes, f);
}

int readSourceFileAttribute(attribute_info_t *attr, ccfrFile_t *f) {
    return read_u2(&(attr->u.SourceFile.sourcefile_index), f);
}

int readLineNumberTableAttribute(attribute_info_t *attr, ccfrFile_t *f) {
    if (!read_u2(&(attr->u.LineNumberTable.line_number_table_length), f)) {
        return 0;
    }

    return read_line_number_table(&(attr->u.LineNumberTable.line_number_table),
            (u4) attr->u.LineNumberTable.line_number_table_length, f);
}

int readLocalVariableTableAttribute(attribute_info_t *attr, ccfrFile_t *f) {
    if (!read_u2(&(attr->u.LocalVariableTable.local_variable_table_length), f)) {
        return 0;
    }

    return read_local_variable_table(&(attr->u.LocalVariableTable.local_variable_table),
            (u4) attr->u.LocalVariableTable.local_variable_table_length, f);
}

int readGenericAttribute(attribute_info_t *attr, ccfrFile_t *f) {
    return read_u1_table(&(attr->u.generic.info), attr->attribute_length, f);
}

int readExceptionTableItem(exception_table_item_t *item, ccfrFile_t *f) {
    if (!read_u2(&(item->start_pc), f)) {
        return 0;
    }
    
    if (!read_u2(&(item->end_pc), f)) {
        return 0;
    }
    
    if (!read_u2(&(item->handler_pc), f)) {
        return 0;
    }
    
    return read_u2(&(item->catch_type), f);
}

int read_exception_table(exception_table_item_t **t, u2 length, ccfrFile_t *f) {
    if (length > ((u2) 0)) {
        u2 i;

        *t = NEW_ARRAY(exception_table_item_t, length);
        if (*t == NULL) {
            return 0;
        }
        for (i = 0; i < length; i++) {
            if (!readExceptionTableItem(*t + i, f)) {
                free(*t);
                *t = NULL;
                return 0;
            }
        }
    } else {
        *t = NULL;
    }

    return 1;
}

int readClassItem(class_item_t *item, ccfrFile_t *f) {
    if (!read_u2(&(item->inner_class_info_index), f)) {
        return 0;
    }

    if (!read_u2(&(item->outer_class_info_index), f)) {
        return 0;
    }

    if (!read_u2(&(item->inner_name_index), f)) {
        return 0;
    }

    return read_u2(&(item->inner_class_access_flags), f);
}

int read_classes_table(class_item_t **t, u2 length, ccfrFile_t *f) {
    if (length > ((u2) 0)) {
        u2 i;

        *t = NEW_ARRAY(class_item_t, length);
        if (*t == NULL) {
            return 0;
        }
        for (i = 0; i < length; i++) {
            if (!readClassItem(*t + i, f)) {
                free(*t);
                *t = NULL;
                return 0;
            }
        }
    } else {
        *t = NULL;
    }

    return 1;
}

int readLineNumberTableItem(line_number_table_item_t *item, ccfrFile_t *f) {
    if (!read_u2(&(item->start_pc), f)) {
        return 0;
    }

    return read_u2(&(item->line_number), f);
}

int read_line_number_table(line_number_table_item_t **t, u2 length, ccfrFile_t *f) {
    if (length > ((u2) 0)) {
        u2 i;

        *t = NEW_ARRAY(line_number_table_item_t, length);
        if (*t == NULL) {
            return 0;
        }
        for (i = 0; i < length; i++) {
            if (!readLineNumberTableItem(*t + i, f)) {
                free(*t);
                *t = NULL;
                return 0;
            }
        }
    } else {
        *t = NULL;
    }

    return 1;
}

int readLocalVariableTableItem(local_variable_table_item_t *item, ccfrFile_t *f) {
    if (!read_u2(&(item->start_pc), f)) {
        return 0;
    }

    if (!read_u2(&(item->length), f)) {
        return 0;
    }

    if (!read_u2(&(item->name_index), f)) {
        return 0;
    }

    if (!read_u2(&(item->descriptor_index), f)) {
        return 0;
    }

    return read_u2(&(item->index), f);
}

int read_local_variable_table(local_variable_table_item_t **t, u2 length, ccfrFile_t *f) {
    if (length > ((u2) 0)) {
        u2 i;

        *t = NEW_ARRAY(local_variable_table_item_t, length);
        if (*t == NULL) {
            return 0;
        }
        for (i = 0; i < length; i++) {
            if (!readLocalVariableTableItem(*t + i, f)) {
                free(*t);
                *t = NULL;
                return 0;
            }
        }
    } else {
        *t = NULL;
    }

    return 1;
}

int read_attributes(attribute_info_t **t, u2 length, cp_info_t *cp, ccfrFile_t *f) {
    if (length > ((u2) 0)) {
        u2 i;

        *t = NEW_ARRAY(attribute_info_t, length);
        if (*t == NULL) {
            return 0;
        }
        for (i = 0; i < length; i++) {
            if (!readAttribute(*t + i, cp, f)) {
                free(*t);
                *t = NULL;
                return 0;
            }
        }
    } else {
        *t = NULL;
    }

    return 1;
}
