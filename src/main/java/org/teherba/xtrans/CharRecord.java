/* CharRecord.java - access methods for character fields
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
import  java.io.BufferedReader;
import  java.io.PrintWriter;
import  org.apache.logging.log4j.Logger;
import  org.apache.logging.log4j.LogManager;

/**
 * Class for
 * Character
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

public class CharRecord extends BaseRecord {
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
   protected StringBuffer buffer;

   /** Constructor with specified size
    *  @param bsize length of the buffer for the record
    */
   public CharRecord(int bsize) {
       buffer = new StringBuffer(bsize);
       buffer.setLength(0); // empty at the beginning
       log = LogManager.getLogger(CharRecord.class.getName());
		padChar = ' ';
   } // CharRecord

   /** Constructor with default size
    */
   public CharRecord() {
       this(2906);
   } // CharRecord

   /** Gets the internal buffer object
    *  @return buffer
    */
   public StringBuffer getBuffer() {
       return this.buffer;
   } // getBuffer

   /** Sets the internal buffer object
    *  @param buffer for the record
    */
   public void setBuffer(StringBuffer buffer) {
       this.buffer = buffer;
   } // setBuffer

   /*=====================================*/
	/** character used for padding string fields */
   private char padChar;

	/** Sets the padding character for {@link #setString}
	 *	@param pad character to be used to fill fields
	 */
	public void setPadChar(char pad) {
		padChar = pad;
	} // setPadChar

   /** Reads the next line from an open file, and stores it
    *  into the buffer without the trailing newline character
    *  @param reader open input file handle
    *  @return true if end-of-file was not yet detected
    */
   public boolean read(BufferedReader reader) {
       boolean result = true; // dieser Methode
       try {
           String line;
           if ((line = reader.readLine()) != null) { // nicht EOF
               buffer = new StringBuffer(line);
               setBufferLength(line.length());
           } else {
               result = false; // EOF erkannt
               setBufferLength(-1);
           }
       } catch (Exception exc) {
           log.error(exc.getMessage(), exc);
           setBufferLength(-1);
       }
       return result;
   } // read

   /** Writes the full length of the record
    *  @param writer open output file handle
    */
   public void write(PrintWriter writer) {
       try {
           writer.write(buffer.toString());
       } catch (Exception exc) {
           log.error(exc.getMessage(), exc);
       }
   } // write

   /** Writes an initial portion of the record
    *  @param writer open output file
    *  @param len length to be written
    */
   public void write(PrintWriter writer, int len) {
       try {
           writer.write(buffer.toString().substring(0, len));
       } catch (Exception exc) {
           log.error(exc.getMessage(), exc);
       }
   } // write

   /** Ensures that the record contains at least <em>len</em>
    *  additional characters (eventually pad with some character at the end);
    *  the record pointer is set behind that length
    *  @param len number of characters to be ensured
    *  @param pad character to be used for padding (space)
    */
   private void padForGet(int len, char pad) {
       currentPos += len;
       if (currentPos > buffer.length()) {
           // field to be read would extend behind current record length
           int start = buffer.length();
           buffer.setLength(currentPos);
           while (start < currentPos) { // pad up to new length
               buffer.setCharAt(start ++, pad);
           } // while
       } // if gap
   } // padForGet

   /** Ensures that the record length fits for at least <em>len</em>
    *  additional characters (eventually pad with some character at the end);
    *  the record pointer is set behind that length
    *  @param len number of characters to be ensured
    *  @param pad character to be used for padding (space)
    */
   private void padForSet(int len, char pad) {
       if (currentPos > buffer.length()) {
           // field to be written would extend behind current record length
           int start = buffer.length();
           buffer.setLength(currentPos);
           while (start < currentPos) { // pad up to new length
               buffer.setCharAt(start ++, pad);
           } // while
       } // if gap
       if (buffer.length() < currentPos + len) {
           // new field would not fit into record
           buffer.setLength(currentPos + len);
       }
   } // padForSet

   /*===========*/
   /*  1 (Char) */
   /*===========*/

   /** Gets a single character from the buffer,
    *  starting at the current position
    *  @return the character
    */
   public char             get1() {
       char ch = padChar;
       if (currentPos < buffer.length() && currentPos >= 0) { // as long as record buffer is not yet exhausted
           ch = buffer.charAt(currentPos ++);
       } // if in buffer
       return ch;
   } // get1
   /** Gets a character from the buffer,
    *  starting at the specified position
    *  @param start starting position of the field
    *  @return the character
    */
   public char get1(int start) {
       currentPos = start;
       return get1();
   } // get1

   /** Gets a character from the buffer,
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
       if (currentPos >= 0 && currentPos < buffer.length()) { // already and still in buffer
            buffer.setCharAt(currentPos ++, (char) value);
       }
       return currentPos;
   } // set1
   public String           getString(int len) {
       int start = (currentPos < 0) ? 0 : currentPos;
       padForGet(len, padChar);
   /*
       int end = start + len - 1;
       while (end > start && buffer.charAt(end) == padChar) {
           end --;
       }
       return buffer.substring(start, end);
   */
       return ("x" + buffer.substring(start, currentPos)).trim().substring(1); // rtrim
   } // getString

   /** Gets a string terminated by the specified character,
    *  starting at the current position
    *  @param term character which terminates the string
    *  @return a string of characters
    */
   public String           getString(char term) {
       String result = "";
       boolean busy = true;
       if (currentPos < buffer.length() && currentPos >= 0) {
           int termPos = buffer.toString().indexOf(term, currentPos);
           if (termPos >= currentPos) {
               result = buffer.substring(currentPos, termPos);
           }
       }
       return result;
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
       padForSet(len, padChar);
       while (index < value.length() && index < len) { // copy all source characters
           if (currentPos >= 0) { // already in buffer
               buffer.setCharAt(currentPos ++, value.charAt(index));
           }
           index ++;
       } // while index
       while (index < len) { // pad with spaces
           if (currentPos >= 0) { // already in buffer
               buffer.setCharAt(currentPos ++,  padChar);
           }
           index ++;
       } // while padding
       return currentPos;
   } // setString

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
       padForGet(len, padChar);
       while (start < currentPos && start < buffer.length()) {
           char ch = buffer.charAt(start);
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
       padForSet(len, padChar);
       int start = (currentPos < 0) ? 0 : currentPos;
       currentPos += len;
       if (digits.length() > len) { // number too wide - truncate left
           digits = digits.substring(digits.length() - len);
       }
       // number fits into field now
       int fpos = start + len - digits.length();
       buffer.replace(fpos, currentPos, digits);
       if (sign < 0) { // negative sign in first position
           buffer.setCharAt(start ++, '-');
       }
       while (start < fpos) { // pad with leading zeroes
           buffer.setCharAt(start ++, '0');
       } // while leading
       return currentPos;
   } // setNumber

   /*=====================================*/

   /** Converts <em>len</em> bytes starting at <em>start</em>
    *  into a hexadecimal representation;
    *  does <strong>not</strong> increment the record pointer
    *  @param  start starting position (0, 1, ...)
    *  @param  len length of the field in bytes
    *  @return string of hex digits, separated by spaces
    */
   public String dump(int start, int len) {
       return buffer.toString();
   } // dump

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
   } // main

}
