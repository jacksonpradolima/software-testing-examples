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

ccfrFile_t *openRegularFile(const char *filename) {
    ccfrFile_t *result;
    FILE *f;

    f = fopen(filename, "rb");
    if (f != NULL) {
        result = (ccfrFile_t *) malloc(sizeof(ccfrFile_t));
        if (result != NULL) {
            result->type = REGULAR_FILE;
            result->u.rf = f;
            return result;
        } 
        fclose(f);
    } 
    return NULL;
}

ccfrFile_t *openArchiveFile(const char *filename, const char *zipEntry) {
    ccfrFile_t *result;
    unzFile f;

    f = unzOpen(filename);
    if (f != NULL) {
        if (unzLocateFile(f, zipEntry, 1) == UNZ_OK) {
            if (unzOpenCurrentFile(f) == UNZ_OK) {
                result = NEW(ccfrFile_t);
                if (result != NULL) {
                    result->type = ARCHIVE_FILE;
                    result->u.af = f;
                    return result;
                } 

                if (unzCloseCurrentFile(f) != UNZ_OK) {
                    fprintf(stderr, "Could not close current archive 1\n");
                }
            }
        }
        /*
        if (unzCloseCurrentFile(f) != UNZ_OK) {
            fprintf(stderr, "Could not close archive 1\n");
        } else {
            fprintf(stderr, "ARCHIVE CLOSING 1\n");
        }
        */
        if (unzClose(f) != UNZ_OK) {
            fprintf(stderr, "ARCHIVE NOT CLOSED 1\n");
        } 
    }

    return NULL;
}

ccfrFile_t *openArchiveFileLocation(const char *filename, const unz_file_pos *pos) {
    ccfrFile_t *result;
    unzFile f;

    f = unzOpen(filename);
    if (f != NULL) {
        if (unzGoToFilePos(f, (unz_file_pos *) pos) == UNZ_OK) {
            if (unzOpenCurrentFile(f) == UNZ_OK) {
                result = NEW(ccfrFile_t);
                if (result != NULL) {
                    result->type = ARCHIVE_FILE;
                    result->u.af = f;
                    return result;
                } 

                if (unzCloseCurrentFile(f) != UNZ_OK) {
                    fprintf(stderr, "Could not close current archive 3\n");
                }
            } else {
                fprintf(stderr, "Could not open current file (loc)\n");
            }
        } else {
            fprintf(stderr, "Could not go to specified location\n");
        }
        if (unzClose(f) != UNZ_OK) {
            fprintf(stderr, "ARCHIVE NOT CLOSED 3\n");
        } 
    } else {
        fprintf(stderr, "Could not simply open \"%s\"\n", filename);
    }

    return NULL;
}

void closeCCFRFile(ccfrFile_t *f) {
    if (f != NULL) {
        if (f->type == REGULAR_FILE) {
            fclose(f->u.rf);
        } else {
            if (unzClose(f->u.af) != UNZ_OK) {
                fprintf(stderr, "ARCHIVE NOT CLOSED 2\n");
            }
        }
        free(f);
    }
}
