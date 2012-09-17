/*  Reader for a URL or data URI source
    @(#) $Id$
	2011-08-06: extended to InputStream interface
    2011-07-15, Dr. Georg Fischer: copied from SeparatedTable
*/
/*
 * Copyright 2011 Dr. Georg Fischer <punctum at punctum dot kom>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.teherba.dbat;
import  java.io.BufferedReader;
import  java.io.ByteArrayInputStream;
import  java.io.FileInputStream;
import  java.io.InputStream;
import  java.io.InputStreamReader;
import 	java.io.IOException;
import  java.io.StringReader;
import 	java.lang.IllegalArgumentException;
import  java.net.URI;
import  java.nio.channels.Channels;
import  java.nio.channels.ReadableByteChannel;
import  org.apache.log4j.Logger;

/** This reader reads from the following sources:
 *	<ol>
 *	<li>a Uniform Resource Locator (URL) - out of the box of java.net.URL - with the protocols/schemas
 *		<ul>
 *		<li>http: - Hypertext transfer protocol</li>
 *		<li>ftp: - File transfer protocol</li>
 *		<li>gopher: - predecessor of http</li>
 *		</ul>
 *	</li>
 *	<li>data: - data content within an Uniform Resource Identfier (URI) itself</li>
 *	</ol>
 *	A data URI has the following format:<br />
 *	data:[&lt;MIME-type&gt;][;charset=&lt;encoding&gt;][;base64],&lt;data&gt;
 *  <p  />
 *  Depending on the <em>encoding</em> parameter during construction, an instance
 *	of the class is either character or byte oriented. The sets of methods of the
 *	<code>BufferedReader</code> and <code>InputStream</code> are both implemented,
 *	but care must be taken to call the appropriate overloaded method name.
 *  <p  />
 *	This class is used in
 *	<ul>
 *	<li>{@link org.teherba.dbat.format.TayloredTable} for the -u pattern file</li>
 *	<li>{@link SQLAction#execSQLfromURI}</li>
 *	<li>{@link SQLAction#insertFromURI} for the reading of (B|C)LOB values</li>
 *	</ul>
 *  @author Dr. Georg Fischer
 */
public class URIReader {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private Logger log;

	//============================================
	// Bean properties, getters and setters
	//============================================
    /** which encoding to use for input: ISO-8859-1, UTF-8 and so on */
    private String encoding;
    /** Gets the encoding for input or output
     *  @return ISO-8859-1, UTF-8 etc.
     */
    public String getEncoding() {
        return this.encoding;
    } // getEncoding

    /** Sets the encoding
     *  @param enc encoding (ISO-8859-1, UTF-8 ...)
     */
    public void setEncoding(String enc) {
        this.encoding = enc;
    } // setEncoding

    /** URI for additional input file */
    private URI inputURI;
    /** Gets the URI from which content is read
     *  @return URI with http:, file: or data: schema or relative path
     */
    public URI getInputURI() {
        return inputURI;
    } // getInputURI
    /** Sets the URI from which content is read
     *  @param uri URI with http:, file: or data: schema or relative path
     */
    public void setInputURI(String uri) {
		try {
	        inputURI = new URI(uri);
		} catch (Exception exc) {
			inputURI = null;
		}
    } // setInputURI

   	/** whether the reader is character oriented (in contrast to byte streams) */
   	private boolean isEncoded;

   	/** generalized local input stream */
   	private InputStream byteStream;
    /** Gets the byte input stream for this URI
     *  @return input stream
     */
    public InputStream getByteStream() {
        return this.byteStream;
    } // getByteStream

   	/** generalized local buffered reader */
   	private BufferedReader charReader;
    /** Gets the character reader for this URI
     *  @return buffered character reader
     */
    public BufferedReader getCharReader() {
        return this.charReader;
    } // getReader

    //======================
    // Construction
    //======================
    /** No-args Constructor
     */
    public URIReader() {
		this("-"); // read from STDIN (character oriented)
    } // Constructor

    /** Construct from an URI (character oriented)
	 *	@param uri the Uniform Resource Identifier to be used: URL or <em>data:</em> URI
     */
    public URIReader(String uri) {
		this(uri, "UTF-8");
    } // Constructor

    /** Construct from a URI and specifiy character set encoding (if character oriented),
     *	or <code>null</code> (if byte oriented reading should be performed).
	 *	@param uri the Uniform Resource Identifier to be used: URL or <em>data:</em> URI
	 *	@param enc character set to be used to read from bytes (character oriented),
	 *	or <code>null</code> (byte oriented)
     */
    public URIReader(String uri, String enc) {
        log = Logger.getLogger(URIReader.class.getName());
        this.encoding = enc;
    	isEncoded 	= enc != null;
		charReader 	= null;
		byteStream  = null;
		try {
			if (isEncoded) {
				ReadableByteChannel channel = null;
				if (false) {
				} else if (uri == null || uri.equals("-")) { // read from STDIN
		        	channel = Channels.newChannel(System.in);
		       		charReader = new BufferedReader(Channels.newReader(channel, this.encoding));
				} else if (uri.startsWith("data:")) {
					inputURI = new URI(uri);
					String content = inputURI.getSchemeSpecificPart().replaceAll("\\+", " "); // rather primitive, no BASE64
					charReader = new BufferedReader(new StringReader(content));
				} else if (uri.startsWith("file:")
						|| uri.startsWith("ftp:")
						|| uri.startsWith("gopher:")
						|| uri.startsWith("http:")
						) {
					inputURI = new URI(uri);
					charReader = new BufferedReader(new InputStreamReader(inputURI.toURL().openStream(), this.encoding));
				} else { // this will (probably) be an absolute or relative file URL
	            	channel = (new FileInputStream (uri)).getChannel();
		       		charReader = new BufferedReader(Channels.newReader(channel, this.encoding));
				}
				// encoded, characters

			} else { // byte oriented
				if (false) {
				} else if (uri == null || uri.equals("-")) { // read from STDIN
					byteStream = System.in;
				} else if (uri.startsWith("data:")) {
					inputURI = new URI(uri);
					String content = inputURI.getSchemeSpecificPart().replaceAll("\\+", " "); // rather primitive, no BASE64
					byteStream = new ByteArrayInputStream(content.getBytes(this.encoding)); // or US_ASCII ?
				} else if (uri.startsWith("file:")
						|| uri.startsWith("ftp:")
						|| uri.startsWith("gopher:")
						|| uri.startsWith("http:")
						) {
					inputURI = new URI(uri);
					byteStream = inputURI.toURL().openStream();
				} else { // this will (probably) be an absolute or relative file URL
                    byteStream = new FileInputStream(uri);
				}
			} // byte orientend
		} catch (Exception exc) {
            log.error(exc.getMessage(), exc);
		}
    } // Constructor

	//==========================
	// BufferedReader Interface
	//==========================
	/** Closes the stream and releases any system resources associated with it.
	 */
	public void close() throws IOException {
		try {
			if (isEncoded) {
				this.charReader.close();
			} else {
				this.byteStream.close();
			}
		} catch (IOException exc) {
			throw exc;
		}
	} // close

	/** Marks the present position in the stream.
	 *	@param readAheadLimit - Limit on the number of characters
	 *	that may be read while still preserving the mark.
	 *	An attempt to reset the stream after reading characters or bytes up to this limit or beyond may fail.
	 *  A limit value larger than the size of the input buffer will cause a new buffer
	 * 	to be allocated whose size is no smaller than limit.
	 *	Therefore large values should be used with care.
	 */
	public void mark(int readAheadLimit) throws IllegalArgumentException, IOException {
		try {
			if (isEncoded) {
				this.charReader.mark(readAheadLimit);
			} else {
				this.byteStream.mark(readAheadLimit);
			}
		} catch (IllegalArgumentException exc) {
			throw exc;
		} catch (IOException exc) {
			throw exc;
		}
	} // readAheadLimit

	/** Tells whether this stream supports the mark() operation, which it does.
	 */
	public boolean markSupported() {
		return isEncoded
				? this.charReader.markSupported()
				: this.byteStream.markSupported()
				;
	} // markSupported

	/** Reads a single character or byte.
	 *	This method will block until a character is available,
	 *	an I/O error occurs, or the end of the stream is reached.
     *	@return The character or byte read, as an integer in the range 0 to 65535 (0x00-0xffff),
     *	or -1 if the end of the stream has been reached.
     *	@throws IOException - If an I/O error occurs
	 */
	public int read() throws IOException {
		int result = -1;
		try {
			if (isEncoded) {
				result = this.charReader.read();
			} else {
				result = this.byteStream.read();
			}
		} catch (IOException exc) {
			throw exc;
		}
		return result;
	} // read

    /* Attempts to read characters into the specified character buffer.
     *	The buffer is used as a repository of characters as-is:
     *	the only changes made are the results of a put operation.
     *	No flipping or rewinding of the buffer is performed.
     *	@param cb the buffer to read characters into
     *	@return The number of char values added to the buffer,
     *	or -1 if this source of characters is at its end
     *  @throws IOException - if an I/O error occurs
     *  @throws NullPointerException - if cb is null
     *  @throws ReadOnlyBufferException - if cb is a read only buffer
	 */
/* this would be needed for the Readable interface
	public int read(CharBuffer cb) throws IOException {
		int result = -1;
		return result;
	} // read
*/

	/** Reads bytes into an array.
     *	This method will block until some input is available,
     *	an I/O error occurs, or the end of the stream is reached.
	 *	@param  bbuf - Destination buffer
     *	@return The number of bytes read, or -1 if the end of the stream has been reached
     *	@throws IOException - If an I/O error occurs
	 */
	public int read(byte[] bbuf) throws IOException {
		int result = -1;
		try {
			result = this.byteStream.read(bbuf, 0, bbuf.length);
		} catch (IOException exc) {
			throw exc;
		}
		return result;
	} // read

	/** Reads bytes into a portion of an array.
     *	This method will block until some input is available,
     *	an I/O error occurs, or the end of the stream is reached.
	 *	@param  bbuf - Destination buffer
     *	@param  off - Offset at which to start storing bytes
     *	@param  len - Maximum number of byte to read
     *	@return The number of bytes read, or -1 if the end of the stream has been reached
     *	@throws IOException - If an I/O error occurs
	 */
	public int read(byte[] bbuf, int off, int len) throws IOException {
		int result = -1;
		try {
			result = this.byteStream.read(bbuf, off, len);
		} catch (IOException exc) {
			throw exc;
		}
		return result;
	} // read

	/** Reads characters into a portion of an array.
     *	This method will block until some input is available,
     *	an I/O error occurs, or the end of the stream is reached.
	 *	@param  cbuf - Destination buffer
     *	@param  off - Offset at which to start storing characters
     *	@param  len - Maximum number of characters to read
     *	@return The number of characters read, or -1 if the end of the stream has been reached
     *	@throws IOException - If an I/O error occurs
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		int result = -1;
		try {
			result = this.charReader.read(cbuf, off, len);
		} catch (IOException exc) {
			throw exc;
		}
		return result;
	} // read

	/** Reads a line of text.
	 *	A line is considered to be terminated by any one of a line feed ('\n'),
	 *	a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
	 *  @return A String containing the contents of the line,
	 *	not including any line-termination characters,
	 *	or null if the end of the stream has been reached
	 *	@throws IOException - If an I/O error occurs
	 */
	public String readLine() throws IOException {
		String result = null;
		try {
			result = this.charReader.readLine();
		} catch (IOException exc) {
			throw exc;
		}
		return result;
	} // readLine

	/** Tells whether this stream is ready to be read.
	 *	@return     True if the next read() is guaranteed not to block for input, false otherwise.
	 *	Note that returning false does not guarantee that the next read will block.
	 *	@throws IOException
	 */
	public boolean ready() throws IOException {
		boolean result = false;
		try {
			result = this.charReader.ready();
		} catch (IOException exc) {
			throw exc;
		}
		return result;
	} // ready

	/** Resets the stream to the most recent mark.
	 */
	public void reset() throws IOException {
		try {
			if (isEncoded) {
				this.charReader.reset();
			} else {
				this.byteStream.reset();
			}
		} catch (IOException exc) {
			throw exc;
		}
	} // reset

	/** Skips characters.
	 *	This method will block until some characters or bytes are available,
	 *	an I/O error occurs, or the end of the stream is reached.
	 *	@param n - The number of characters or bytes to skip
     *	@return The number of characters oor bytes actually skipped
     *	@throws IllegalArgumentException - If n is negative.
     *	@throws IOException - If an I/O error occurs
	 */
	public long skip(long n) throws IOException  {
		long result = 0l;
		try {
			if (isEncoded) {
				result = this.charReader.skip(n);
			} else {
				result = this.byteStream.skip(n);
			}
		} catch (IOException exc) {
			throw exc;
		}
		return result;
	} // skip

	//======================
    // Main method (test)
    //======================

    /** Test method: read from an URL and print the result on STDOUT.
     *  @param args command line arguments: options, strings, table- or filenames
     */
    public static void main(String[] args) {
        Logger log = Logger.getLogger(URIReader.class.getName());
        int iarg = 0;
        try {
        /*
			URI uri = new URI(args[iarg ++]);
			URL url = uri.toURL();
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			} // while reading
			in.close();
		*/
			URIReader in = new URIReader(args[iarg ++]);
			String line = "";
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			} // while reading
			in.close();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
    } // main

} // URIReader
