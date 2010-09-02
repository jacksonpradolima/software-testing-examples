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

#ifndef _CCFR_FILE_H
#define _CCFR_FILE_H

#define REGULAR_FILE ((u1) 0)
#define ARCHIVE_FILE ((u1) 1)

typedef struct ccfrFile {
    u1 type;
    union {
        unzFile af;
        FILE *rf;
    } u;
} ccfrFile_t;

ccfrFile_t *openRegularFile(const char *filename);
ccfrFile_t *openArchiveFile(const char *filename, const char *zipEntry);
ccfrFile_t *openArchiveFileLocation(const char *filename, const unz_file_pos *pos);
void closeCCFRFile(ccfrFile_t *f);


#endif
