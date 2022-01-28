/* ByteRecord.java - access methods for byte fields and EBCDIC conversion
 * @(#) $Id$
 * 2017-05-27: javadoc 1.8
 * 2016-10-14: Date, Timestamp, Attributes and SAXException only for "a"
 * 2008-08-04: get|set1, setPadChar
 * 2008-06-23: get|setRecordTag, getRecordURI
 * 2007-01-19: padForSet in char.setString
 * 2006-09-29, Dr. Georg Fischer: 4 classes combined and splitted by LineSplitter
 *
 * c.f. http://forum.java.sun.com/thread.jspa?threadID=530813&messageID=2557841 EBCDIC in Java
 * Cp037 or Cp1047
 *
 * Caution, this source file is generated from Records.txt by org.teherba.xtrans.LineSplitter -
 * do not edit here!
 */

package org.teherba.xtrans;
import  org.teherba.xtrans.BaseRecord;
import  org.teherba.xtrans.Field;
import  java.io.InputStream;
import  java.io.OutputStream;
import  org.apache.logging.log4j.Logger;
import  org.apache.logging.log4j.LogManager;

/**
 * Class for
 * Byte
 * records of fixed length, and access methods for fields in such records.
 * All positions start at 0.
 * Strings are always left-adjusted in fields, and are padded with spaces
 * or truncated at the right end.
 * Get operations which return strings trim all spaces at the right end, maybe returning an empty string.
 * Numerical values (number, packed, long) are right-adjusted in fields, and are
 * padded with zeroes resp. nils, or truncated at the left end.
 * Plus sings are not stored, any minus sign is in the leftmost position.
 * Numerical get operations ignore spaces.
 * @author Dr. Georg Fischer
 */

public class ByteRecord extends BaseRecord {
   public final static String CVSID = "@(#) $Id$";

   /** log4j logger (category) */
   private Logger log;

   /** name of the element for a record */
   private String recordTag;

   /** Gets the record's element name
    *  @return XML tag for one record
    */
   public String getRecordTag() {
       return recordTag;
   } // getRecordTag

   /** Sets the record's element name
    *  @param tag XML tag for one record
    */
   protected void setRecordTag(String tag) {
       recordTag = tag;
   } // setRecordTag

   /** Internal buffer for the record */
   protected byte[] buffer;

   /** Constructor with specified size
    *  @param bsize length of the buffer for the record
    */
   public ByteRecord(int bsize) {
       buffer = new byte[bsize];
       log = LogManager.getLogger(ByteRecord.class.getName());
		padChar = (byte) ' ';
		encoding = "ISO-8859-1";
   } // ByteRecord

   /** Constructor with default size
    */
   public ByteRecord() {
       this(2906);
   } // ByteRecord

   /** ISO-8859-1 to EBCDIC conversion table */
   private static final String LATIN1_TO_EBCDIC =
             "\u0000\u0001\u0002\u0003\u0037\u002d\u002e\u002f"
           + "\u0016\u0005\u001a\u000b\u000c\r\u000e\u000f"
           + "\u0025\u0011\u0012\u0013\u003c\u00b5\u0032\u0026"
           + "\u0018\u0019\u003f\u0027\u001c\u001d\u001e\u001f"
           + "\u0040\u004f\u007f\u007b\u005b\u006c\u0050\u007d"
           + "\u004d]\\\u004e\u006b\u0060\u004b\u0061"
           + "\u00f0\u00f1\u00f2\u00f3\u00f4\u00f5\u00f6\u00f7"
           + "\u00f8\u00f9\u007a\u005e\u004c\u007e\u006e\u006f"
           + "\u007c\u00c1\u00c2\u00c3\u00c4\u00c5\u00c6\u00c7"
           + "\u00c8\u00c9\u00d1\u00d2\u00d3\u00d4\u00d5\u00d6"
           + "\u00d7\u00d8\u00d9\u00e2\u00e3\u00e4\u00e5\u00e6"
           + "\u00e7\u00e8\u00e9\u004a\u00e0\u005a\u00be\u006d"
           + "\u0079\u0081\u0082\u0083\u0084\u0085\u0086\u0087"
           + "\u0088\u0089\u0091\u0092\u0093\u0094\u0095\u0096"
           + "\u0097\u0098\u0099\u00a2\u00a3\u00a4\u00a5\u00a6"
           + "\u00a7\u00a8\u00a9\u00c0\u006a\u00d0\u00a1\u004e"
           + "\u0068\u00dc\u0051\u0042\u0043\u0044\u0047\u0048"
           + "\u0052\u0053\u0054\u0057\u0056\u0058\u0063\u0067"
           + "\u0071\u009c\u009e\u00cb\u00cc\u00cd\u00db\u00dd"
           + "\u00df\u00ec\u00fc\u00b0\u00b1\u00b2\u00b3\u00b4"
           + "\u0045\u0055\u00ce\u00de\u0049\u0069\u009a\u009b"
           + "\u00ab\u00a9\u00ba\u00b8\u00b7\u00aa\u008a\u008b"
           + "\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9"
           + "\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9"
           + "\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9"
           + "\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9"
           + "\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9"
           + "\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9\u00a9"
           + "\u0070\u0059\u0070\u0070\u0070\u0070\u00a0\u0070"
           + "\u0070\u0070\u0070\u0070\u0070\u0070\u0070\u0070"
           + "\u0070\u008f\u0070\u0070\u0070\u0070\u00a1\u0070"
           + "\u0090\u0070\u0041\u0070\u00fa\u00ea\u0070\u0000"
           ;
   /** EBCDIC -&gt; ISO-8859-1 conversion table */
   private static final String EBCDIC_TO_LATIN1 =
             "\u0000\u0001\u0002\u0003\u0020\u0009\u0020\u007f"
           + "\u0020\u0020\u0020\u000b\u000c\r\u000e\u000f"
           + "\u0010\u0011\u0012\u0013\u0020\u0020\u0008\u0020"
           + "\u0018\u0019\u0020\u0020\u001c\u001d\u001e\u001f"
           + "\u0020\u0020\u0020\u0020\u0020\n\u0017\u001b"
           + "\u0020\u0020\u0020\u0020\u0020\u0005\u0006\u0007"
           + "\u0020\u0020\u0016\u0020\u0020\u0020\u0020\u0004"
           + "\u0020\u0020\u0020\u0020\u0020\u0015\u0020\u001a"
           + "\u0020\u0020\u0083\u0084\u0085\u00a0\u00c6\u0086"
           + "\u0087\u00a4\u005b\u002e\u003c\u0028\u002b\u0021"
           + "\u0026\u0082\u0088\u0089\u008a\u00a1\u008c\u008b"
           + "\u008d\u00e1]\u0024\u002a\u0029\u003b\u005e"
           + "\u002d\u002f\u00b6\u008e\u00b7\u00b5\u00c7\u008f"
           + "\u0080\u00a5\u007c\u002c\u0025\u005f\u003e\u003f"
           + "\u00ed\u0090\u00d2\u00d3\u00d4\u00d6\u00d7\u00d8"
           + "\u00de\u0060\u003a\u0023\u0040\u0027\u003d\""
           + "\u00ed\u0061\u0062\u0063\u0064\u0065\u0066\u0067"
           + "\u0068\u0069\u00ae\u00af\u00d0\u00ec\u00e7\u00f1"
           + "\u00f8\u006a\u006b\u006c\u006d\u006e\u006f\u0070"
           + "\u0071\u0072\u00a6\u00a7\u0091\u0020\u0092\u0024"
           + "\u00e6\u007e\u0073\u0074\u0075\u0076\u0077\u0078"
           + "\u0079\u007a\u00ad\u00a8\u00d2\u00ed\u00e8\u00a9"
           + "\u009b\u009c\u009d\u009e\u009f\u0015\u0014\u00ac"
           + "\u00ab\u00f3\u00aa\u007c\u0099\u00f9\u0027\u0020"
           + "\u007b\u0041\u0042\u0043\u0044\u0045\u0046\u0047"
           + "\u0048\u0049\u002d\u0093\u0094\u0095\u00a2\u00e4"
           + "\u007d\u004a\u004b\u004c\u004d\u004e\u004f\u0050"
           + "\u0051\u0052\u00a1\u0096\u0081\u0097\u00a3\u0098"
           + "\\\u0020\u0053\u0054\u0055\u0056\u0057\u0058"
           + "\u0059\u005a\u00fd\u00e2\u0099\u00e3\u00e0\u00e5"
           + "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037"
           + "\u0038\u0039\u00fc\u0084\u009a\u0081\u00e9\u00fe"
           ;

   /** Gets the internal buffer object
    *  @return buffer
    */
   public byte[] getBuffer() {
       return this.buffer;
   } // getBuffer

   /** Sets the internal buffer object
    *  @param buffer for the record
    */
   public void setBuffer(byte buffer[]) {
       this.buffer = buffer;
   } // setBuffer

   /*=====================================*/
	/** encoding used for strings in byte records */
   private String encoding;

	/** Sets the encoding used for strings in byte records
	 *	@param enc encoding like "US-ASCII", "UTF-8" or "ISO-8859-1"
	 */
	public void setEncoding(String enc) {
		encoding = enc;
	} // setEncoding

   /*=====================================*/
	/** character used for padding string fields */
   private char padChar;

	/** Sets the padding character for {@link #setString}
	 *	@param pad character to be used to fill fields
	 */
	public void setPadChar(char pad) {
		padChar = pad;
	} // setPadChar
   /** Reads a record from an open stream into the record buffer,
    *  and sets the pointer to the start of the buffer.
    *  @param reader open input file
    *  @return number of bytes read, or -1 at end of file
    */
   public int read(InputStream reader) {
       int len = read(reader, buffer.length);
       setBufferLength(len);
       return len;
   } // read

   /** Reads the specified number of bytes
    *  from an open stream into the record buffer,
    *  and sets the pointer to the start of the buffer.
    *  @param reader open input file
    *  @param len number of bytes to be read
    *  @return number of bytes read, or -1 at end of file
    */
   public int read(InputStream reader, int len) {
       int result = -1;
       currentPos = 0;
       try {
           result = reader.read(buffer, 0, len);
       } catch (Exception exc) {
           log.error(exc.getMessage(), exc);
       }
       setBufferLength(result);
       return result;
   } // read

   /** Reads an IBM z/OS variable length record (RECFM=V),
    *  taking the record length from a leading 4 byte field
    *  of the form hh ll 00 00, where these 4 byte are included
    *  in the length, for example 0x00960000 for a 150 byte DTA record.
    *  @param reader open input file
    *  @return true if end-of-file was not yet detected
    */
   public boolean readVariable(InputStream reader) {
       boolean result = true;
       try {
           if (reader.read(buffer, 0, 4) < 0) {
               result = false;
           }
           else {
               int len = (int) getMSB(0, 2);
               if (len <= 0 || len > buffer.length) {
                   len = buffer.length;
                   len = reader.read(buffer, 4, len - 7); // read rest of buffer
               /*
                   throw new RuntimeException( "invalid length in Bytesatz.readVariable4: " + len);
               */
                   setBufferLength(len);
               } else {
                   len = reader.read(buffer, 4, len - 4);
                   if (len < 0) {
                       result = false; // EOF
                       currentPos = 0;
                       setBufferLength(-1);
                   } else {
                       setBufferLength(len + 4);
                   }
               }
           /*  who needs this padding?
               int ipos = len; // read length
               while (ipos < buffer.length) {
                   // fill with EBCDIC spaces
                   buffer[ipos ++] = 0x40;
               } // while
           */
           }
       } catch (Exception exc) {
           log.error(exc.getMessage(), exc);
           setBufferLength(-1);
       }
       return result;
   } // readVariable

   /** Writes the full length of the record
    *  @param writer open output file handle
    */
   public void write(OutputStream writer) {
       try {
           write(writer, buffer.length);
       } catch (Exception exc) {
           log.error(exc.getMessage(), exc);
       }
   } // write

   /** Writes an initial portion of the record
    *  @param writer open output file
    *  @param len length to be written
    */
   public void write(OutputStream writer, int len) {
       try {
           writer.write(buffer, 0, len);
       } catch (Exception exc) {
           log.error(exc.getMessage(), exc);
       }
   } // write

   /** Writes an IBM z/OS variable length record (RECFM=V),
    *  putting the record length into a leading 4 byte field
    *  of the form hh ll 00 00, where these 4 byte are included
    *  in the length, for example 0x00960000 for a 150 byte DTA record.
    *  @param writer open output file
    */
   public void writeVariable(OutputStream writer) {
       try {
           int len = (int) getMSB(0, 2);
           setMSB(2, 2, 0); // OSD4 (BS-2000) used 0x4040 here
           writer.write(buffer, 0, len);
       } catch (Exception exc) {
           log.error(exc.getMessage(), exc);
       }
   } // writeVariable

   /*  padding is not neccessary since the buffer is
       initialized automatically. There may be gaps filled with null bytes.
   */

   /** Fills the rest of the record with some padding character;
    *  the record pointer is set to the end of the buffer
    *  @param pad character to be used for padding (space or nil)
    */
   public void fill(char pad) {
       while (currentPos < buffer.length) {
           buffer[currentPos ++] = (byte) pad;
       } // while padding
   } // fill

   /*===========*/
   /*  1 (Byte) */
   /*===========*/

   /** Gets a single byte from the buffer and decode it into a character,
    *  starting at the current position
    *  @return the character
    */
   public char             get1() {
       char ch = padChar;
       if (currentPos < buffer.length   && currentPos >= 0) { // as long as record buffer is not yet exhausted
			try {
				ch = (new String(buffer, currentPos, 1, encoding)).charAt(0);				
			} catch (Exception exc) {
	           	ch = (char) (0xff & buffer[currentPos ++]);
			}
       } // if in buffer
       return ch;
   } // get1
   /** Gets a byte from the buffer,
    *  starting at the specified position
    *  @param start starting position of the field
    *  @return the character
    */
   public char get1(int start) {
       currentPos = start;
       return get1();
   } // get1

   /** Gets a byte from the buffer,
    *  @param field field to be read
    *  @return the character
    */
   public char get1(Field field) {
       currentPos = field.start;
       return get1();
   } // get1

   /** Sets an integer into a field of length 1,
    *  starting at the current read/write pointer,
    *  and update the read/write pointer
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int          set1(int value) {
       if (currentPos >= 0 && currentPos < buffer.length) { // already and still in buffer
            buffer[currentPos ++] = (byte) value;
       }
       return currentPos;
   } // set1
   public String           getString(int len) {
       StringBuffer result = new StringBuffer(len);
       int index = 0;
       while (index < len && currentPos < 0) {
           result.append(padChar);
           index ++;
           currentPos ++;
       }
		int start = currentPos;
       while (index < len && currentPos < buffer.length) {
           index ++;
           currentPos ++;
       }
       try {
       	result.append(new String(buffer, start, currentPos - start, encoding));
       } catch (Exception exc) {
	        try {
   	    	result.append(new String(buffer, start, currentPos - start, "ISO-8859-1"));
       	} catch (Exception exc2) {
       	}
       }
       while (index < len) {
           result.append(padChar);
           index ++;
       }
	/*	        	
       for (index = 0; index < len; index ++) {
           if (currentPos < buffer.length && currentPos >= 0) {
               // as long as record buffer is not yet exhausted
               result.append((char) (0xff & buffer[currentPos ++]));
           } else { // pad with spaces
               result.append(padChar);
           }
       } // for index
   */
       return result.toString(); // .trim();
   } // getString

   /** Gets a string terminated by the specified character,
    *  starting at the current position
    *  @param term character which terminates the string
    *  @return a string of characters
    */
   public String           getString(char term) {
       StringBuffer result = new StringBuffer(296);
       boolean busy = true;
       while (busy) {
           if (currentPos < buffer.length && currentPos >= 0) {
               // as long as record buffer is not yet exhausted
               char ch = (char) (0xff & buffer[currentPos ++]);
               busy = ch != term;
               if (busy) {
                   result.append(ch);
               }
           } else {
               busy = false;
           }
       } // while busy
       return result.toString(); // .trim();
   } // getString

   /** Sets a string into a field of length <em>len</em>,
    *  starting at the current read/write pointer,
    *  truncate or pad with spaces at the right end,
    *  and update the read/write pointer
    *  @param len width of field
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int          setString(int len, String value) {
       if (value == null) {
           value = "";
       }
       int index = 0;
       while (index < value.length() && index < len && currentPos < buffer.length) {
           if (currentPos >= 0) { // already in buffer
               buffer[currentPos ++] = (byte) value.charAt(index);
           }
           index ++;
       } // while index
       while (index < len && currentPos < buffer.length) { // pad with spaces
           if (currentPos >= 0) { // already in buffer
               buffer[currentPos ++] = (byte) padChar;
           }
           index ++;
       } // while padding
       return currentPos;
   } // setString

   /*==================*/
   /*  EBCDIC String   */
   /*==================*/

   /** Gets a string from a field of length <em>len</em>,
    *  starting at the current position
    *  @param len width of field
    *  @return converted string of characters
    */
   public String getEBCDICString(int len) {
       StringBuffer result = new StringBuffer(len);
       int index;
       for (index = 0; index < len; index ++) {
           if (currentPos < buffer.length && currentPos >= 0) {
               // as long as record buffer is not yet exhausted
               result.append(EBCDIC_TO_LATIN1.charAt(0xff & buffer[currentPos ++]));
           } else { // pad with spaces
               result.append(padChar);
           }
       } // for index
       return result.toString(); // .trim();
   } // getEBCDICString

   /** Gets a string from a field of length <em>len</em>,
    *  starting at the specified position
    *  @param start starting position of the field
    *  @param len width of field
    *  @return converted string of characters
    */
   public String getEBCDICString(int start, int len) {
       currentPos = start;
       return getEBCDICString(len);
   } // getEBCDICString

   /** Gets an EBCDIC string from a field,
    *  @param field field to be read
    *  @return converted string of characters
    */
   public String getEBCDICString(Field field) {
       currentPos = field.start;
       return getEBCDICString(field.len);
   } // getEBCDICString

   /** Sets a string as EBCDIC into a field of length <em>len</em>,
    *  starting at the current position
    *  @param len width of field
    *  @param value string of characters to be converted
    *  @return modified position in buffer
    */
   public int setEBCDICString(int len, String value) {
       int index = 0;
       if (value == null) {
           value = "";
       }
       while (index < value.length() && index < len && currentPos < buffer.length) {
           if (currentPos >= 0) { // already in buffer
               buffer[currentPos ++] = (byte) (LATIN1_TO_EBCDIC.charAt(value.charAt(index)));
           }
           index ++;
       } // while index
       while (index < len && currentPos < buffer.length) { // pad with spaces
           if (currentPos >= 0) { // already in buffer
               buffer[currentPos ++] = (byte) (LATIN1_TO_EBCDIC.charAt(padChar));
           }
           index ++;
       } // while padding
       return currentPos;
   } // setEBCDICString

   /** Sets a string as EBCDIC into a field of length <em>len</em>,
    *  starting at the specified position
    *  @param start starting position of the field
    *  @param len width of field
    *  @param value string of characters to be converted
    *  @return modified position in buffer
    */
   public int setEBCDICString(int start, int len, String value) {
       currentPos = start;
       return setEBCDICString(len, value);
   } // setEBCDICString

   /** Sets a string as EBCDIC into a field,
    *  @param field field to be written
    *  @param value string of characters to be converted
    *  @return modified position in buffer
    */
   public int setEBCDICString(Field field, String value) {
       currentPos = field.start;
       return setEBCDICString(field.len, value);
   } // setEBCDICString

   /*==========*/
   /*  Number  */
   /*==========*/

   /** Gets a number from a field of length <em>len</em>,
    *  starting at the current position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  and update the read/write pointer
    *  @param len width of field
    *  @return number read from the field
    *  @throws NumberFormatException for invalid characters (no digit, comma, point, dash)
    */
   public          long getNumber(int len) throws NumberFormatException {
       long result = 0; // assume no digit
       int sign = 1; // assume positive
       int start = (currentPos < 0) ? 0 : currentPos;
       currentPos += len;
       while (start < currentPos && start < buffer.length  ) {
           char ch = (char) (0xff & buffer[start]);
           if (Character.isDigit(ch)) {
               result = result * 10 + Character.digit(ch, 10);
           } else if (ch == '-') {
               sign = -1;
           } else if ("., ".indexOf(ch) < 0) { // ignore dot, comma, space
               throw new NumberFormatException("invalid character (no digit, ',', '.', '-', '+')");
           }
           start ++;
       } // while
       return result * sign;
   } // getNumber

   /** Sets a number into a field of length <em>len</em>,
    *  starting at the current position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  and update the read/write pointer
    *  @param len width of field
    *  @param value number to be written
    *  @return modified position in buffer
    */
   public          int setNumber(int len, long value) {
       int sign = 1;
       if (value < 0) {
           sign = -1;
           value = - value;
       }
       String digits = Long.toString(value);
       int start = (currentPos < 0) ? 0 : currentPos;
       currentPos += len;
       if (digits.length() > len) { // number too wide - truncate left
           digits = digits.substring(digits.length() - len);
       }
       // number fits into field now
       int fpos = start + len - digits.length();
       int index;
       index = 0;
       int ipos = fpos;
       while (index < digits.length() && index < len && ipos < buffer.length) {
           // process all characters in source
           if (currentPos >= 0) { // already in buffer
               buffer[ipos ++] = (byte) digits.charAt(index);
           }
           index ++;
       } // while index
       if (sign < 0) { // negative sign in first position
           buffer[start ++] = (byte) '-';
       }
       while (start < fpos) { // pad with leading zeroes
           buffer[start ++] =  (byte) '0';
       } // while leading
       return currentPos;
   } // setNumber

   /*==========================*/
   /*  Unsigned Packed Decimal */
   /*==========================*/

   /** Gets an unsigned packed decimal number from a field of length <em>len</em>,
    *  starting at the current position,
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param len width of field
    *  @return number read from the field
    */
   public String getUnsignedDecimal(int len) {
       StringBuffer value = new StringBuffer(2 * len); // result to be returned
       value.setLength(2 * len);
       int  istr = value.length(); // index of rightmost digit
       int  opos = currentPos; // start of field
       currentPos += len; // behind field
       int  start = currentPos - 1; // last position in field (maybe behind end of buffer)
       int  toggle = 0; // 0 = right nibble, 1 = left
       int  digit; // current digit below 'istr', or 0

       if (currentPos > buffer.length) { // 'currentPos' never behind end of buffer
           currentPos = buffer.length;
       }
       while (istr > 0) { // while storign digits
           if (toggle == 0) { // right
               if (start >= 0 && start < currentPos) { // still in buffer
                   digit = buffer[start] & 0x0f;
               } else {
                   digit = 0;
               }
               toggle = 1; // left
           } else { // read from left nibble
               if (start >= 0 && start < currentPos) { // still in buffer
                   digit = (buffer[start] >> 4) & 0x0f;
               } else {
                   digit = 0;
               }
               start --; // next byte to the left
               toggle = 0;
           } // left nibble

           // store the digit
           if (digit >= 0 && digit <= 9) { // normal decimal digit
               digit += '0';
           } else if (digit >= 0x0a && digit <= 0x0f) {
               digit = digit - 0x0a + 'a';
           } else { // error, replaced by 0
               digit = '0';
           }
           istr --; // start with rightmost digit, decrement to the left
           value.setCharAt(istr, (char) digit);
       } // while istr
       return value.toString();
   } // getUnsignedDecimal

   /** Gets an unsigned packed decimal number from a field of length <em>len</em>,
    *  starting at the specified position,
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param start starting position of the field
    *  @param len width of field
    *  @return number read from the field
    */
   public String getUnsignedDecimal(int start, int len) {
       currentPos = start;
       return getUnsignedDecimal(len);
   } // getUnsignedDecimal

   /** Gets an unsigned packed decimal number from a field
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param field field of record to be read (offset and length)
    *  @return number read from the field
    */
   public String getUnsignedDecimal(Field field) {
       currentPos = field.start;
       return getUnsignedDecimal(field.len);
   } // getUnsignedDecimal

   /** Sets an unsigned packed decimal number into a field of length <em>len</em>,
    *  starting at the current position,
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param len width of field
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setUnsignedDecimal(int len, String value) {
       if (value == null) {
           value = "";
       }
       int  istr = value.length(); // becomes index of last digit below
       int  opos = currentPos; // start of field
       currentPos += len; // behind field
       int  start = currentPos - 1; // at end of field (maybe behind end of buffer
       int  toggle = 0; // 0 = right nibble, 1 = left
       int  digit; // current digit at 'istr', or 0

       if (currentPos > buffer.length) { // 'currentPos' never behind end of buffer
           currentPos = buffer.length;
       } // Pufferende

       while (start >= opos) { // storing digits while buffer not exhausted
           istr --; // start with last digit, decrement to the left
           if (istr >= 0) { // still in source
               digit = value.charAt(istr);
               if (digit >= '0' && digit <= '9') {
                   // normal digit
                   digit -= '0';
               } else if (digit == 'c') {
                   // positive
                   digit = 0x0c;
               } else if (digit == 'd') {
                   // negative
                   digit = 0x0d;
               } else if (digit == 'f') {
                   // no sign
                   digit = 0x0f;
               } else { // space or error - set to 0
                   digit = 0;
               }
           } else { // source string to short, pad left with zeroes
               digit = 0;
           }

           if (toggle == 0) { // set right nibble
               if (start >= 0 && start < currentPos) { // already in buffer
                   buffer[start] = (byte) digit;
               } // right nibble
               toggle = 1;
           }
           else { // set left nibble
               if (start >= 0 && start < currentPos) { // already in buffer
                   buffer[start] |= (byte) (digit << 4);
               }
               start --; // switch to next byte to the left
               toggle = 0;
           } // left nibble
       } // while istr
       return currentPos;
   } // setUnsignedDecimal

   /** Sets an unsigned packed decimal number into a field of length <em>len</em>,
    *  starting at the specified position,
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param start starting position of the field
    *  @param len width of field
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setUnsignedDecimal(int start, int len, String value) {
       currentPos = start;
       return setUnsignedDecimal(len, value);
   } // setUnsignedDecimal

   /** Sets an unsigned packed decimal number into a field
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param field field of record to be written (offset and length)
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setUnsignedDecimal(Field field, String value) {
       currentPos = field.start;
       return setUnsignedDecimal(field.len, value);
   } // setUnsignedDecimal

   /*==================*/
   /*   Packed Decimal */
   /*==================*/

   /** Gets a packed decimal number from a field of length <em>len</em>,
    *  starting at the current position,
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param len width of field
    *  @return number read from the field
    */
   public String getDecimal(int len) {
       String digits = getUnsignedDecimal(len);
       char sign; // stores sign nibble
       int last = digits.length() - 1;
       sign = (digits.length() > 0) ? digits.charAt(last) : ' ';
       if (sign == 'c') { // positive
           return "" + digits.substring(0, last);
       } else if (sign == 'd') { // negative
           return "-" + digits.substring(0, last);
       } else { // none
           return       digits.substring(0, last);
       }
   } // getDecimal

   /** Gets a packed decimal number from a field of length <em>len</em>,
    *  starting at the specified position,
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param start starting position of the field
    *  @param len width of field
    *  @return number read from the field
    */
   public String getDecimal(int start, int len) {
       // eine gepackte Zahl mit signeichen ab Position auslesen
       currentPos = start;
       return getDecimal(len);
   } // getDecimal

   /** Gets a packed decimal number from a field
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param field field of record to be read (offset and length)
    *  @return number read from the field
    */
   public String getDecimal(Field field) {
       currentPos = field.start;
       return getDecimal(field.len);
   } // getDecimal

   /** Sets a packed decimal number into a field of length <em>len</em>,
    *  starting at the current position,
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param len width of field
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setDecimal(int len, String value) {
       if (value == null) {
           value = "";
       }
   /*
       if (value.length() > 0 && value.substring(0, 1).equals ("+")) {
           setUnsignedDecimal(len, value.substring(1) + "c");
       } else
   */
       if (value.length() > 0 && value.substring(0, 1).equals ("-")) {
           setUnsignedDecimal(len, value.substring(1) + "d");
       } else {
           setUnsignedDecimal(len, value              + "c");
       }
       return currentPos;
   } // setDecimal

   /** Sets a packed decimal number into a field of length <em>len</em>,
    *  starting at the specified position,
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param start starting position of the field
    *  @param len width of field
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setDecimal(int start, int len, String value) {
       currentPos = start;
       return setDecimal(len, value);
   } // setDecimal

   /** Sets a packed decimal number into a field
    *  and update the read/write pointer;
    *  the field contains 2*<em>len</em> digits
    *  @param field field of record to be written (offset and length)
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setDecimal(Field field, String value) {
       currentPos = field.start;
       return setDecimal(field.len, value);
   } // setDecimal

   /*==============================================*/
   /*  Most Significant Byte first (Big Endian)    */
   /*==============================================*/

   /** Gets a binary number from a field of length <em>len</em>,
    *  in big endian order (most significant byte first),
    *  starting at the current position,
    *  and update the read/write pointer
    *  @param len width of field (1, 2, 4, 8)
    *  @return number read from field
    */
   public long getMSB(int len) {
       long value = 0; // result of method
       while (len > 0 && currentPos < buffer.length) { // while fetching bytes and field not exhausted
           len --;
           value <<= 8;
           value |= (buffer[currentPos ++] & 0xff);
       } // while len
       return value;
   } // getMSB

   /** Gets a binary number from a field of length <em>len</em>,
    *  in big endian order (most significant byte first),
    *  starting at the specified position,
    *  and update the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field (1, 2, 4, 8)
    *  @return number read from field
    */
   public long getMSB(int start, int len) {
       currentPos = start;
       return getMSB(len);
   } // getMSB

   /** Gets a binary number from a field,
    *  in big endian order (most significant byte first),
    *  and update the read/write pointer
    *  @param field field to be read
    *  @return number read from field
    */
   public long getMSB(Field field) {
       currentPos = field.start;
       return getMSB(field.len);
   } // getMSB

   /** Sets a binary number into a field of length <em>len</em>,
    *  in big endian order (most significant byte first),
    *  starting at the current position,
    *  and update the read/write pointer
    *  @param len width of field (1, 2, 4, 8)
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setMSB(int len, long value) {
       currentPos += len; // behind the field
       int start = currentPos; // may be behind end of buffer
       if (currentPos > buffer.length) { // 'currentPos' never behind end of buffer
           currentPos = buffer.length;
       }
       while (len > 0) { // storing digits while field not exhausted
           start --; // start with last byte, decrement to the left
           if (start >= 0) { // still in buffer
               buffer[start] = (byte) (value & 0xff);
               value >>= 8;
           }
           len --;
       } // while len
       return currentPos;
   } // setMSB

   /** Sets a binary number into a field of length <em>len</em>,
    *  in big endian order (most significant byte first),
    *  starting at the specified position,
    *  and update the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field (1, 2, 4, 8)
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setMSB(int start, int len, long value) {
       currentPos = start;
       return setMSB(len, value);
   } // setMSB

   /** Sets a binary number into a field,
    *  in big endian order (most significant byte first),
    *  and update the read/write pointer
    *  @param field field to be written
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setMSB(Field field, long value) {
       currentPos = field.start;
       return setMSB(field.len, value);
   } // setMSB

   /*==================================================*/
   /*  Least Significant Byte first (Little Endian)    */
   /*==================================================*/

   /** Gets a binary number from a field of length <em>len</em>,
    *  in little endian order (least significant byte first),
    *  starting at the current position,
    *  and update the read/write pointer
    *  @param len width of field (1, 2, 4, 8)
    *  @return number read from field
    */
   public long getLSB(int len) {
       long value = 0; // result of method
       int index = currentPos + len - 1;
       while (index >= currentPos) { // while fetching bytes and field not exhausted
           value <<= 8;
           if (index < buffer.length) {
               value |= (buffer[index] & 0xff);
           }
           index --;
       } // while len
       currentPos += len;
       if (currentPos > buffer.length) { // 'currentPos' never behind end of buffer
           currentPos = buffer.length;
       }
       return value;
   } // getLSB

   /** Gets a binary number from a field of length <em>len</em>,
    *  in little endian order (least significant byte first),
    *  starting at the specified position,
    *  and update the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field (1, 2, 4, 8)
    *  @return number read from field
    */
   public long getLSB(int start, int len) {
       currentPos = start;
       return getLSB(len);
   } // getLSB

   /** Gets a binary number from a field,
    *  in little endian order (least significant byte first),
    *  and update the read/write pointer
    *  @param field field to be read
    *  @return number read from field
    */
   public long getLSB(Field field) {
       currentPos = field.start;
       return getLSB(field.len);
   } // getLSB

   /** Sets a binary number into a field of length <em>len</em>,
    *  in little endian order (least significant byte first),
    *  starting at the current position,
    *  and update the read/write pointer
    *  @param len width of field (1, 2, 4, 8)
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setLSB(int len, long value) {
       int index = currentPos; // may be behind end of buffer
       currentPos += len; // behind the field
       if (currentPos > buffer.length) { // 'currentPos' never behind end of buffer
           currentPos = buffer.length;
       }
       while (index < currentPos)  { // storing digits while field not exhausted
           if (index < buffer.length) { // still in buffer
               buffer[index] = (byte) (value & 0xff);
           }
           value >>= 8;
           index ++;
       } // while len
       return currentPos;
   } // setLSB

   /** Sets a binary number into a field of length <em>len</em>,
    *  in little endian order (least significant byte first),
    *  starting at the specified position,
    *  and update the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field (1, 2, 4, 8)
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setLSB(int start, int len, long value) {
       currentPos = start;
       return setLSB(len, value);
   } // setLSB

   /** Sets a binary number into a field,
    *  in little endian order (least significant byte first),
    *  and update the read/write pointer
    *  @param field field to be written
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setLSB(Field field, long value) {
       currentPos = field.start;
       return setLSB(field.len, value);
   } // setLSB

   /*=====================================*/

   /** Converts <em>len</em> bytes starting at <em>start</em>
    *  into a hexadecimal representation;
    *  does <strong>not</strong> increment the record pointer
    *  @param  start starting position (0, 1, ...)
    *  @param  len length of the field in bytes
    *  @return string of hex digits, separated by spaces
    */
   public String dump(int start, int len) {
       StringBuffer line = new StringBuffer(3 * len);
       int  ipos = start;
       String hex;
       while (ipos < start + len && ipos < buffer.length) {
           if (ipos > start) {
               line.append(' ');
           }
           hex = Integer.toHexString(buffer[ipos] & 0xff);
           if (hex.length() < 2) {
               line.append(' ');
           }
           line.append(hex);
           ipos ++;
       } // while ipos
       return line.toString();
   } // dump

   /** Checks the reversibility of the conversion tables
    */
   public static void checkMap() {
       for (int ascii1 = 0; ascii1 < 255; ascii1 ++) {
           String as1hex   = Integer.toHexString (ascii1 & 0xff);
           int ebcdic      = LATIN1_TO_EBCDIC.charAt(ascii1);
           String ebchex   = Integer.toHexString (ebcdic & 0xff);
           int ascii2      = EBCDIC_TO_LATIN1.charAt(ebcdic);
           String as2hex   = Integer.toHexString (ascii2 & 0xff);
           System.out.println (
                   as1hex + " \"" + (ascii1 >= 32 ? (char) ascii1 : ' ') + "\" " +
                   as2hex + " \"" + (ascii2 >= 32 ? (char) ascii2 : ' ') + "\" "
                   );
       }
   } // checkMap

   /** Tests a condition, and writes an error message if it's not true
    *  @param name name of testcase
    *  @param cond condition to be tested
    *  @param text text of error message to be printed
    */
   private void testCase(String name, boolean cond, String text) {
       System.err.print(name);
       if (! cond) {
           System.err.println(":\t" + text + " " + this.dump(0, this.getBufferSize()));
       } else {
           System.err.println();
       }
   } // testCase

   /** Test program
    *  @param args commandline arguments
    */
   public static void main(String args[]) {
       ByteRecord rec = new ByteRecord(16);
       rec.setMSB(2, 0xf1f9);
       rec.testCase("MSB1", rec.getMSB(0, 2) == 0xf1f9, "setMSB error");
       rec.setMSB(2, 4, 0xf4f7f0f6);
       rec.testCase("MSB2", rec.getMSB(1, 5) == 0xf9f4f7f0f6L, "setMSB error " + rec.getMSB(1, 5));
       rec.testCase("STR1", rec.getEBCDICString(0, 6).equals("194706"), "getEBCDICString error " + rec.getEBCDICString(0, 6));
   } // main

}
