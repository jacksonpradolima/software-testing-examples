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

import adaptj_pool.Scene;
import java.io.*;

/**
 * A utility class based on {@link java.io.RandomAccessFile RandomAccessFile}, but with
 * a buffer. This class is solely used for data input (no output). The major advantage
 * of the {@link java.io.RandomAccessFile RandomAccessFile} is efficient implementation of <code>seek()<code>,
 * which allows to seek a position in the file for reading. This is required by the
 * {@link adaptj_pool.AEFReader AEFReader} class, which needs to reset the file position after
 * it is done parsing the input file.
 *
 * @author Bruno Dufour
 * @see java.io.RandomAccessFile
 */
public class BufferedFileReader implements DataInput {
    protected byte buff[];             // the byte buffer
    protected RandomAccessFile raFile; // the underlying RandomAccessFile
    private int pos = -1;              // current position in the buffer
    private int avail = -1;            // The number of bytes available in the buffer. -1  = none
    protected int buffSize;            // The size of the buffer
    public final static int DEFAULT_BUFFER_SIZE = 4096; // the default buffer size

    /**
     * Constructs a new <code>BufferedFileReader</code> with the default buffer size
     * for the file specified by <code>name</code>.
     *
     * @param name the name of the file to open for reading.
     */
    public BufferedFileReader(String name) throws IOException {
        this(name, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Constructs a new <code>BufferedFileReader</code> with the specified buffer size
     * for the file specified by <code>name</code>.
     *
     * @param name the name of the file to open for reading.
     * @param bufferSize the size of the buffer (in bytes) that will be used
     */
    public BufferedFileReader(String name, int bufferSize) throws IOException {
        this.raFile = new RandomAccessFile(name, "r");
        buffSize = bufferSize;
        buff = new byte[buffSize];
    }

    /**
     * Constructs a new <code>BufferedFileReader</code> with the default buffer size
     * for the file specified by <code>file</code>.
     *
     * @param file a <code>File</code> object representating file to open for reading.
     */
    public BufferedFileReader(File file) throws IOException {
        this(file, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Constructs a new <code>BufferedFileReader</code> with the specified buffer size
     * for the file specified by <code>file</code>.
     *
     * @param file a <code>File</code> object representating file to open for reading.
     * @param bufferSize the size of the buffer (in bytes) that will be used
     */ 
    public BufferedFileReader(File file, int bufferSize) throws IOException {
        this.raFile = new RandomAccessFile(file, "r");
        buffSize = bufferSize;
        buff = new byte[buffSize];
    }

    /**
     * Tries to fill the buffer with the data that is currently available.
     *
     * @throws IOException if an I/O error occurs. Not thrown if end-of-file has been reached.
     */
    private void fill() throws IOException {
        if (avail >= 0) {
            /* Move the bytes we have to the beginning of the buffer */
            if (pos >= avail) {
                for (int i = 0; i < avail; i++) {
                    buff[i] = buff[pos++];
                }
            } else {
                System.arraycopy(buff, pos, buff, 0, avail);
            }
            /* Read as many new bytes as possible. We get the guarantee
             * that at least one byte will be read */
            int numRead = raFile.read(buff, avail, buffSize - avail);
            /* Update the byte counter */
            if (numRead > 0) {
                avail += numRead;
            }
            /* Bytes are now located at index 0 in the buffer */
            pos = 0;
        } else {
            /* We do not have to relocate bytes, since none
             * are currently stored in the buffer. Simply read as much
             * as possible */
            avail = raFile.read(buff);
            pos = 0;
        }
    }

    /** 
     * Reads a byte of data from this file. The byte is returned as an integer in
     * the range 0 to 255 (<code>0x00-0x0ff</code>). This method blocks if no input is yet
     * available. If there is at least 1 byte available in the buffer, this byte
     * will be returned. Otherwise, a call to this method result in the buffer being
     * filled (as much as possible).
     *
     * @return the next byte of data, or -1 if the end of the file has been reached.
     * @throws IOException if an I/O error occurs. Not thrown if end-of-file has been reached.
     */
    public int read() throws IOException {
        if (avail < 1) {
            fill();
            if (avail < 1) {
                /* We could not event read 1 new byte */
                return -1;
            }
        }

        int result = (int) buff[pos++]; // get the next byte
        result &= 0x000000FF; // restrict to the specfied range
        avail--;              // used up one byte
        return result;
    }

    /**
     * Reads up to <code>len</code> bytes of data from this file into an array of bytes. This
     * method blocks until at least one byte of input is available.
     * 
     * @param b the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the maximum number of bytes read.
     * 
     * @return the total number of bytes read into the buffer, or -1 if
     *         there is no more data because the end of the file has been reached.
     * @throws IOException if an I/O error occurs.
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (avail < len) {
            fill();
        }

        if (avail < 0) {
            return -1;
        }
        
        int numToCopy = (avail > len ? len : avail);
        System.arraycopy(buff, pos, b, off, numToCopy);
        avail -= numToCopy;
        pos += numToCopy;

        return numToCopy;
    }

    /**
     * Reads up to <code>b.length</code> bytes of data from this file into an array
     * of bytes. This method blocks until at least one byte of input is available.
     *
     * @param b the buffer into which the data is read.
     *
     * @return the total number of bytes read into the buffer, or -1 if there is no
     *         more data because the end of this file has been reached.
     * @throws IOException if an I/O error occurs.
     */
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads a boolean from this file. This method reads a single byte from the file,
     * starting at the current file pointer. A value of 0 represents <code>false</code>.
     * Any other value represents true. This method blocks until the byte is read,
     * the end of the stream is detected, or an exception is thrown.
     *
     * @return the boolean value read.
     * @throws EOFException if this file has reached the end.
     * @throws IOException if an I/O error occurs.
     */
    public boolean readBoolean() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }

        return (ch != 0);
    }
    
     /**
     * Reads a signed eight-bit value from this file. This method reads a 
     * byte from the file, starting from the current file pointer. 
     * If the byte read is <code>b</code>, where 
     * <code>0&nbsp;&lt;=&nbsp;b&nbsp;&lt;=&nbsp;255</code>, 
     * then the result is:
     * <blockquote><pre>
     *     (byte)(b)
     * </pre></blockquote>
     * <p>
     * This method blocks until the byte is read, the end of the stream 
     * is detected, or an exception is thrown. 
     *
     * @return     the next byte of this file as a signed eight-bit
     *             <code>byte</code>.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     */
    public byte readByte() throws IOException {
        int ch = this.read();
	if (ch < 0) {
	    throw new EOFException();
        }
	return (byte)(ch);
    }

    /**
     * Reads an unsigned eight-bit number from this file. This method reads 
     * a byte from this file, starting at the current file pointer, 
     * and returns that byte. 
     * <p>
     * This method blocks until the byte is read, the end of the stream 
     * is detected, or an exception is thrown. 
     *
     * @return     the next byte of this file, interpreted as an unsigned
     *             eight-bit number.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     */
    public int readUnsignedByte() throws IOException {
        int ch = this.read();
	if (ch < 0) {
	    throw new EOFException();
        }
	return ch;
    }
    
    /**
     * Reads a Unicode character from this file. This method reads two
     * bytes from the file, starting at the current file pointer. 
     * If the bytes read, in order, are 
     * <code>b1</code> and <code>b2</code>, where 
     * <code>0&nbsp;&lt;=&nbsp;b1,&nbsp;b2&nbsp;&lt;=&nbsp;255</code>, 
     * then the result is equal to:
     * <blockquote><pre>
     *     (char)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this file as a Unicode character.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public char readChar() throws IOException {
        int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0) {
	    throw new EOFException();
        }
        return (char)((ch1 << 8) + (ch2 << 0));
    }
    
    /**
     * Reads a <code>double</code> from this file. This method reads a 
     * <code>long</code> value, starting at the current file pointer, 
     * as if by the <code>readLong</code> method 
     * and then converts that <code>long</code> to a <code>double</code> 
     * using the <code>longBitsToDouble</code> method in 
     * class <code>Double</code>.
     * <p>
     * This method blocks until the eight bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next eight bytes of this file, interpreted as a
     *             <code>double</code>.
     * @exception  EOFException  if this file reaches the end before reading
     *             eight bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.RandomAccessFile#readLong()
     * @see        java.lang.Double#longBitsToDouble(long)
     */
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }
    
    /**
     * Reads a <code>float</code> from this file. This method reads an 
     * <code>int</code> value, starting at the current file pointer, 
     * as if by the <code>readInt</code> method 
     * and then converts that <code>int</code> to a <code>float</code> 
     * using the <code>intBitsToFloat</code> method in class 
     * <code>Float</code>. 
     * <p>
     * This method blocks until the four bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next four bytes of this file, interpreted as a
     *             <code>float</code>.
     * @exception  EOFException  if this file reaches the end before reading
     *             four bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.RandomAccessFile#readInt()
     * @see        java.lang.Float#intBitsToFloat(int)
     */
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }
    
    public void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }
    
    public void readFully(byte[] b, int off, int len) throws IOException {
        int n = 0;
	do {
	    int count = this.read(b, off + n, len - n);
	    if (count < 0)
		throw new EOFException();
	    n += count;
	} while (n < len);
    }
    
    /**
     * Reads a signed 32-bit integer from this file. This method reads 4 
     * bytes from the file, starting at the current file pointer. 
     * If the bytes read, in order, are <code>b1</code>,
     * <code>b2</code>, <code>b3</code>, and <code>b4</code>, where 
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>, 
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </pre></blockquote>
     * <p>
     * This method blocks until the four bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next four bytes of this file, interpreted as an
     *             <code>int</code>.
     * @exception  EOFException  if this file reaches the end before reading
     *               four bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public int readInt() throws IOException {
        int ch1 = this.read();
	int ch2 = this.read();
	int ch3 = this.read();
	int ch4 = this.read();
	if ((ch1 | ch2 | ch3 | ch4) < 0) {
	    throw new EOFException();
        }
	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }
    
    /**
     * Reads the next line of text from this file.  This method successively
     * reads bytes from the file, starting at the current file pointer, 
     * until it reaches a line terminator or the end
     * of the file.  Each byte is converted into a character by taking the
     * byte's value for the lower eight bits of the character and setting the
     * high eight bits of the character to zero.  This method does not,
     * therefore, support the full Unicode character set.
     *
     * <p> A line of text is terminated by a carriage-return character
     * (<code>'&#92;r'</code>), a newline character (<code>'&#92;n'</code>), a
     * carriage-return character immediately followed by a newline character,
     * or the end of the file.  Line-terminating characters are discarded and
     * are not included as part of the string returned.
     *
     * <p> This method blocks until a newline character is read, a carriage
     * return and the byte following it are read (to see if it is a newline),
     * the end of the file is reached, or an exception is thrown.
     *
     * @return     the next line of text from this file, or null if end
     *             of file is encountered before even one byte is read.
     * @exception  IOException  if an I/O error occurs.
     */
    public String readLine() throws IOException {
        StringBuffer input = new StringBuffer();
	int c = -1;
	boolean eol = false;

	while (!eol) {
	    switch (c = read()) {
	    case -1:
	    case '\n':
		eol = true;
		break;
	    case '\r':
		eol = true;
		long cur = getFilePointer();
		if ((read()) != '\n') {
		    seek(cur);
		}
		break;
	    default:
		input.append((char)c);
		break;
	    }
	}

	if ((c == -1) && (input.length() == 0)) {
	    return null;
	}
	return input.toString();
    }
    
    /**
     * Reads a signed 64-bit integer from this file. This method reads eight
     * bytes from the file, starting at the current file pointer. 
     * If the bytes read, in order, are 
     * <code>b1</code>, <code>b2</code>, <code>b3</code>, 
     * <code>b4</code>, <code>b5</code>, <code>b6</code>, 
     * <code>b7</code>, and <code>b8,</code> where:
     * <blockquote><pre>
     *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
     * </pre></blockquote>
     * <p>
     * then the result is equal to:
     * <p><blockquote><pre>
     *     ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48)
     *     + ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32)
     *     + ((long)b5 &lt;&lt; 24) + ((long)b6 &lt;&lt; 16)
     *     + ((long)b7 &lt;&lt; 8) + b8
     * </pre></blockquote>
     * <p>
     * This method blocks until the eight bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next eight bytes of this file, interpreted as a
     *             <code>long</code>.
     * @exception  EOFException  if this file reaches the end before reading
     *               eight bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public long readLong() throws IOException {
        return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }
    
    /**
     * Reads a signed 16-bit number from this file. The method reads two 
     * bytes from this file, starting at the current file pointer. 
     * If the two bytes read, in order, are 
     * <code>b1</code> and <code>b2</code>, where each of the two values is 
     * between <code>0</code> and <code>255</code>, inclusive, then the 
     * result is equal to:
     * <blockquote><pre>
     *     (short)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this file, interpreted as a signed
     *             16-bit number.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public short readShort() throws IOException {
        int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0) {
	    throw new EOFException();
        }
	return (short)((ch1 << 8) + (ch2 << 0));
    }
    
    /**
     * Reads an unsigned 16-bit number from this file. This method reads 
     * two bytes from the file, starting at the current file pointer. 
     * If the bytes read, in order, are 
     * <code>b1</code> and <code>b2</code>, where 
     * <code>0&nbsp;&lt;=&nbsp;b1, b2&nbsp;&lt;=&nbsp;255</code>, 
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 8) | b2
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this file, interpreted as an unsigned
     *             16-bit integer.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public int readUnsignedShort() throws IOException {
        int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0) {
	    throw new EOFException();
        }
        return (ch1 << 8) + (ch2 << 0);
    }
    
    /**
     * Reads in a string from this file. The string has been encoded 
     * using a modified UTF-8 format. 
     * <p>
     * The first two bytes are read, starting from the current file 
     * pointer, as if by 
     * <code>readUnsignedShort</code>. This value gives the number of 
     * following bytes that are in the encoded string, not
     * the length of the resulting string. The following bytes are then 
     * interpreted as bytes encoding characters in the UTF-8 format 
     * and are converted into characters. 
     * <p>
     * This method blocks until all the bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     a Unicode string.
     * @exception  EOFException            if this file reaches the end before
     *               reading all the bytes.
     * @exception  IOException             if an I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent 
     *               valid UTF-8 encoding of a Unicode string.
     * @see        java.io.RandomAccessFile#readUnsignedShort()
     */
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }
    
    /**
     * Attempts to skip over <code>n</code> bytes of input discarding the 
     * skipped bytes. 
     * <p>
     * 
     * This method may skip over some smaller number of bytes, possibly zero. 
     * This may result from any of a number of conditions; reaching end of 
     * file before <code>n</code> bytes have been skipped is only one 
     * possibility. This method never throws an <code>EOFException</code>. 
     * The actual number of bytes skipped is returned.  If <code>n</code> 
     * is negative, no bytes are skipped.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     */
    public int skipBytes(int n) throws IOException {
        if (n <= 0) {
	    return 0;
        }
        
	if (n < avail) {
            pos += n;
            avail -= n;
            return n;
        }

        long fpos;
	long flen;
	long fnewpos;
	
	fpos = getFilePointer();
	flen = length();
	fnewpos = fpos + n;
	if (fnewpos > flen) {
	    fnewpos = flen;
	}
	seek(fnewpos);

	/* return the actual number of bytes skipped */
	return (int) (fnewpos - fpos);
    }

    /**
     * Returns the current offset in this file. 
     *
     * @return     the offset from the beginning of the file, in bytes,
     *             at which the next read occurs. The buffering does not
     *             affect the value obtained from this method.
     * @exception  IOException  if an I/O error occurs.
     */
    public long getFilePointer() throws IOException {
        long realFP = raFile.getFilePointer();
        if (avail <= 0) {
            return realFP;
        }
        return realFP - avail;
    }

    /**
     * Sets the file-pointer offset, measured from the beginning of this 
     * file, at which the next read occurs.  The offset may be 
     * set beyond the end of the file. Setting the offset beyond the end 
     * of the file does not change the file length. The buffering does not
     * influence the value obtained from this method.
     *
     * @param      pos   the offset position, measured in bytes from the 
     *                   beginning of the file, at which to set the file 
     *                   pointer.
     * @exception  IOException  if <code>pos</code> is less than 
     *                          <code>0</code> or if an I/O error occurs.
     */
    public void seek(long pos) throws IOException {
        raFile.seek(pos);
        this.avail = -1;
        this.pos = -1;
    }
    
    /**
     * Closes this <code>BufferedFileReader</code> and releases any system 
     * resources associated with it. A closed <code>BufferedFileReader</code> 
     * cannot perform input operations and cannot be reopened.
     *
     * @exception  IOException  if an I/O error occurs.
     */
    public void close() throws IOException {
        raFile.close();
    }

    /**
     * Returns the length of this file.
     *
     * @return     the length of this file, measured in bytes.
     * @exception  IOException  if an I/O error occurs.
     */
    public long length() throws IOException {
        return raFile.length();
    }
 }
