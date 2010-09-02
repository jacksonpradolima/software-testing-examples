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

#ifndef _CCFR_ATTRIBUTES_H
#define _CCFR_ATTRIBUTES_H

#define GENERIC_ATTR              ((u1) 0)
#define CONSTANT_VALUE_ATTR       ((u1) 1)
#define CODE_ATTR                 ((u1) 2)
#define EXCEPTIONS_ATTR           ((u1) 3)
#define INNER_CLASSES_ATTR        ((u1) 4)
#define SYNTHETIC_ATTR            ((u1) 5)
#define SOURCE_FILE_ATTR          ((u1) 6)
#define LINE_NUMBER_TABLE_ATTR    ((u1) 7)
#define LOCAL_VARIABLE_TABLE_ATTR ((u1) 8)
#define DEPRECATED_ATTR           ((u1) 9)

#define CONSTANT_VALUE       "ConstantValue"
#define CODE                 "Code"
#define EXCEPTIONS           "Exceptions"
#define INNER_CLASSES        "InnerClasses"
#define SYNTHETIC            "Synthetic"
#define SOURCE_FILE          "SourceFile"
#define LINE_NUMBER_TABLE    "LineNumberTable"
#define LOCAL_VARIABLE_TABLE "LocalVariableTable"
#define DEPRECATED           "Deprecated"

#define CONSTANT_VALUE_SIZE       13
#define CODE_SIZE                  4
#define EXCEPTIONS_SIZE           10
#define INNER_CLASSES_SIZE        12
#define SYNTHETIC_SIZE             9
#define SOURCE_FILE_SIZE          10
#define LINE_NUMBER_TABLE_SIZE    15
#define LOCAL_VARIABLE_TABLE_SIZE 18
#define DEPRECATED_SIZE           10

typedef struct exception_table_item {
    u2 start_pc;
    u2 end_pc;
    u2 handler_pc;
    u2 catch_type;
} exception_table_item_t;

typedef struct class_item {
    u2 inner_class_info_index;
    u2 outer_class_info_index;
    u2 inner_name_index;
    u2 inner_class_access_flags;
} class_item_t;

typedef struct line_number_table_item {
    u2 start_pc;
    u2 line_number;
} line_number_table_item_t;

typedef struct local_variable_table_item {
    u2 start_pc;
    u2 length;
    u2 name_index;
    u2 descriptor_index;
    u2 index;
} local_variable_table_item_t;

typedef struct attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 type;   /* Not in the Java Class File Spec */
    union {
        struct {
            u2 constantvalue_index;
        } ConstantValue;

        struct {
            u2 max_stack;
            u2 max_locals;
            u4 code_length;
            u1 *code;
            u2 exception_table_length;
            exception_table_item_t *exception_table;
            u2 attributes_count;
            struct attribute_info *attributes;
        } Code;

        struct {
            u2 number_of_exceptions;
            u2 *exception_index_table;
        } Exceptions;

        struct {
            u2 number_of_classes;
            class_item_t *classes;
        } InnerClasses;

        /* Synthetic attribute has no specific information */

        struct {
            u2 sourcefile_index;
        } SourceFile;

        struct {
            u2 line_number_table_length;
            line_number_table_item_t *line_number_table;
        } LineNumberTable;

        struct {
            u2 local_variable_table_length;
            local_variable_table_item_t * local_variable_table;
        } LocalVariableTable;

        /* Deprecated attribute has no specific information */

        struct {
            u1 *info;
        } generic;
    } u;
} attribute_info_t;


int readAttribute(attribute_info_t *attr, cp_info_t *cp, ccfrFile_t *f);
int readConstantValueAttribute(attribute_info_t *attr, ccfrFile_t *f);
int readCodeAttribute(attribute_info_t *attr, cp_info_t *cp, ccfrFile_t *f);
int readExceptionsAttribute(attribute_info_t *attr, ccfrFile_t *f);
int readInnerClassesAttribute(attribute_info_t *attr, ccfrFile_t *f);
int readSourceFileAttribute(attribute_info_t *attr, ccfrFile_t *f);
int readLineNumberTableAttribute(attribute_info_t *attr, ccfrFile_t *f);
int readLocalVariableTableAttribute(attribute_info_t *attr, ccfrFile_t *f);
int readGenericAttribute(attribute_info_t *attr, ccfrFile_t *f);


int readExceptionTableItem(exception_table_item_t *item, ccfrFile_t *f);
int read_exception_table(exception_table_item_t **t, u2 length, ccfrFile_t *f);
int readClassItem(class_item_t *item, ccfrFile_t *f);
int read_classes_table(class_item_t **t, u2 length, ccfrFile_t *f);
int readLineNumberTableItem(line_number_table_item_t *item, ccfrFile_t *f);
int read_line_number_table(line_number_table_item_t **t, u2 length, ccfrFile_t *f);
int readLocalVariableTableItem(local_variable_table_item_t *item, ccfrFile_t *f);
int read_local_variable_table(local_variable_table_item_t **t, u2 length, ccfrFile_t *f);

int read_attributes(attribute_info_t **t, u2 length, cp_info_t *cp, ccfrFile_t *f);
#endif
