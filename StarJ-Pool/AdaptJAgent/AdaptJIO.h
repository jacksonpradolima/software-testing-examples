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

#ifndef _ADAPTJ_IO_H
#define _ADAPTJ_IO_H

#define ADAPTJ_WRITE_BYTE(val, buf, bufLen) *buf=val; buf++; bufLen++;
#define ADAPTJ_WRITE_JSHORT(val, buf, bufLen) *((jshort *) buf)=JShortMSB(val); buf += 2; bufLen += 2;
#define ADAPTJ_WRITE_JINT(val, buf, bufLen) *((jint *) buf)=JIntMSB(val); buf += 4; bufLen += 4;
#define ADAPTJ_WRITE_JLONG(val, buf, bufLen) *((jlong *) buf)=JLongMSB(val); buf += 8; bufLen += 8;
#define ADAPTJ_WRITE_UTF8(val, valLen, buf, bufLen) ADAPTJ_WRITE_JSHORT(valLen, buf, bufLen); strncpy((char *)buf, (char *)val, valLen); buf += valLen; bufLen += valLen;

void showMessage(const char *msg);
void showMessage2(const char *format, const char *param);
void showMessageInt(const char *format, const long int param);
void reportWarning(const char *msg);
void reportWarning2(const char *format, const char *param);
void reportWarningInt(const char *format, const long int param);
void reportError(const char *msg);
void reportError2(const char *format, const char *param);

/* LOW-LEVEL FUNCTIONS */

int AdaptJReadEvent(AdaptJEvent *opt, FILE *f);
int AdaptJReadShort(jshort *s, FILE *f);
int AdaptJReadByte(byte *b, FILE *f);
int AdaptJReadBytes(void *s, jint count, FILE *f);

#endif
