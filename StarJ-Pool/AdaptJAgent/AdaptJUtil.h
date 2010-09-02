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

#ifndef _ADAPTJ_UTIL_H
#define _ADAPTJ_UTIL_H

char *getNextOption(char *src, char *option, char *value);
char *stripQuotes(char *s);
jint processEventString(char *eventString, jshort infoMask);
boolean parseBoolean(char *buff, boolean *result);

/* Byte order */
boolean isMSB();
/* LSB to MSB functions */
jshort JShortLSB2MSB(jshort val);
jint JIntLSB2MSB(jint val);
jlong JLongLSB2MSB(jlong val);
/* Identity functions */
jshort JShortMSB2MSB(jshort val);
jint JIntMSB2MSB(jint val);
jlong JLongMSB2MSB(jlong val);

#endif
