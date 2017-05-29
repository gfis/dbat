/* BaseRecord.java - common methods for record access
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
import  org.teherba.xtrans.Field;
import  java.sql.Date;
import  java.sql.Timestamp;
import  java.text.SimpleDateFormat;
import  org.xml.sax.Attributes;
import  org.xml.sax.SAXException;
import  org.apache.log4j.Logger;

/**
 * Abstract
 * Class for
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

public abstract class BaseRecord {
   public final static String CVSID = "@(#) $Id$";

   /** Readable format for dates */
   protected static final SimpleDateFormat ISO_DATE_FORMAT      = new SimpleDateFormat("yyyy-MM-dd");
   /** Readable format for timestamps with milliseconds */
   protected static final SimpleDateFormat ISO_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

   /** log4j logger (category) */
   private Logger log;

   /** Declared size of the record buffer */
   protected int bufferSize;

   /** Gets the declared record size
    *  @return declared size of the record buffer
    */
   public int getBufferSize() {
       return bufferSize;
   } // getBufferSize

   /** Filled length of the record buffer as read from a file */
   private int bufferLength;

   /** Gets the filled record length read from a file
    *  @return number of bytes / chararcters read into the reocrd
    */
   public int getBufferLength() {
       return bufferLength;
   }  // getBufferLength

   /** Sets the filled record length read from a file
    *  @param len number of bytes / chararcters read into the reocrd
    */
   public void setBufferLength(int len) {
       this.bufferLength = len;
   } // getBufferLength

   /*=====================================*/
   /** Current read/write pointer (position in the internal record buffer).
    *  Is incremented by almost all access methods, and is sometimes
    *  returned by such methods.
    */
   protected int currentPos = 0;

   /** Gets the current read/write pointer
    *  @return position (starting at 0) in internal record buffer
    */
   public int getPosition() {
       return currentPos;
   } // getPosition

   /** Sets the current read/write pointer to
    *  the start of the internal record buffer
    *  @return modified position in buffer
    */
   public int setPosition() {
       return setPosition(0);
   } // setPosition
   /** Sets the current read/write pointer to
    *  the specified position in the internal record buffer
    *  @param start position where to set the pointer
    *  @return modified position in buffer
    */
   public int setPosition(int start) {
       currentPos = start;
       return currentPos;
   } // setPosition

   /** Sets the current read/write pointer to
    *  the start of the specified field in the internal record buffer
    *  @param field field which defines the new position
    *  @return modified position in buffer
    */
   public int setPosition(Field field) {
       currentPos = field.start;
       return setPosition(field.start);
   } // setPosition

   /*=====================================*/


   public abstract int set1(int value);

   /** Sets an integer into a field of length 1,
    *  starting at <em>start</em>,
    *  and update the read/write pointer
    *  @param start starting position of field
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int set1(int start, int value) {
       currentPos = start;
       return set1(value);
   } // set1

   /** Sets an integer into a field,
    *  and update the read/write pointer
    *  @param field field to be written (offset and length)
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int set1(Field field, int value) {
       currentPos = field.start;
       return set1(value);
   } // set1

   /*==========*/
   /*  String  */
   /*==========*/

   /** Gets a string from a field of length <em>len</em>,
    *  starting at the current position
    *  @param len width of field
    *  @return a string of characters
    */
   public abstract String  getString(int len);

   /** Gets a string from a field of length <em>len</em>,
    *  starting at the specified position
    *  @param start starting position of the field
    *  @param len width of field
    *  @return a string of characters
    */
   public String getString(int start, int len) {
       currentPos = start;
       return getString(len);
   } // getString

   /** Gets a string from a field
    *  @param field field to be read
    *  @return a string of characters
    */
   public String getString(Field field) {
       currentPos = field.start;
       return getString(field.len);
   } // getString

   /** Gets a string terminated by the specified character,
    *  starting at the current position
    *  @param term character which terminates the string
    *  @return a string of characters
    */
   public abstract String  getString(char term);

   /** Gets a string terminated by a nil (0x00) character,
    *  starting at the current position
    *  @return a string of characters
    */
   public String getASCIZ() {
       return getString('\u0000');
   } // getAsciz

   /** Sets a string into a field of length <em>len</em>,
    *  starting at the current read/write pointer,
    *  truncate or pad with spaces at the right end,
    *  and update the read/write pointer
    *  @param len width of field
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public abstract int setString(int len, String value);

   /** Sets a string into a field of length <em>len</em>,
    *  starting at <em>start</em>,
    *  truncate or pad with spaces at the right end,
    *  and update the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setString(int start, int len, String value) {
       currentPos = start;
       return setString(len, value);
   } // setString

   /** Sets a string into a field,
    *  truncate or pad with spaces at the right end,
    *  and update the read/write pointer
    *  @param field field to be written (offset and length)
    *  @param value value to be written
    *  @return modified position in buffer
    */
   public int setString(Field field, String value) {
       currentPos = field.start;
       return setString(field.len, value);
   } // setString

   /*======*/
   /* Date */
   /*======*/

   /** Gets an ISO date string from a field of length <em>len</em>,
    *  starting at the current position,
    *  and update the read/write pointer
    *  @param len width of field
    *  @return Java SQL Date object
    */
   public java.sql.Date getDate(int len) {
       return java.sql.Date.valueOf(getString(len));
   } // getDate

   /** Gets an ISO date string from a field of length <em>len</em>,
    *  starting at the specified position,
    *  and update the read/write pointer
    *  @param len width of field
    *  @param start starting position of the field
    *  @return Java SQL Date object
    */
   public java.sql.Date getDate(int start, int len) {
       currentPos = start;
       return getDate(len);
   } // getDate

   /** Gets an ISO date string from a field
    *  and update the read/write pointer
    *  @param field field to be read (offset and length)
    *  @return Java SQL Date object
    */
   public java.sql.Date getDate(Field field) {
       currentPos = field.start;
       return getDate(field.len);
   } // getDate

   /** Sets an ISO date string into a field of length <em>len</em>,
    *  starting at the current position,
    *  and update the read/write pointer
    *  @param len width of field
    *  @param isoDate string of the form yyyy-MM-dd
    *  @return modified position in buffer
    */
   public int setDate(int len, java.sql.Date isoDate) {
       return setString(len, ISO_DATE_FORMAT.format(isoDate));
   } // setDate

   /** Sets an ISO date string into a field of length <em>len</em>,
    *  starting at the specified position,
    *  and update the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field
    *  @param isoDate string of the form yyyy-MM-dd
    *  @return modified position in buffer
    */
   public int  setDate(int start, int len, java.sql.Date isoDate) {
       currentPos = start;
       return setDate(len, isoDate);
   } // setDate

   /** Sets an ISO date string into a field
    *  and update the read/write pointer
    *  @param field field to be written (offset and length)
    *  @param isoDate string of the form yyyy-MM-dd
    *  @return modified position in buffer
    */
   public int setDate(Field field, java.sql.Date isoDate) {
       currentPos = field.start;
       return setDate(field.len, isoDate);
   } // setDate

   /*===========*/
   /* Timestamp */
   /*===========*/

   /** Gets an ISO timestamp string from a field of length <em>len</em>,
    *  starting at the current position,
    *  and update the read/write pointer
    *  @param len width of field
    *  @return Java SQL Timestamp object
    */
   public java.sql.Timestamp getTimestamp(int len) {
       return java.sql.Timestamp.valueOf(getString(len));
   } // getTimestamp

   /** Gets an ISO timestamp string from a field of length <em>len</em>,
    *  starting at the specified position,
    *  and update the read/write pointer
    *  @param len width of field
    *  @param start starting position of the field
    *  @return Java SQL Timestamp object
    */
   public java.sql.Timestamp getTimestamp(int start, int len) {
       currentPos = start;
       return getTimestamp(len);
   } // getTimestamp

   /** Gets an ISO timestamp string from a field
    *  and update the read/write pointer
    *  @param field field to be read (offset and length)
    *  @return Java SQL Timestamp object
    */
   public java.sql.Timestamp getTimestamp(Field field) {
       currentPos = field.start;
       return getTimestamp(field.len);
   } // getTimestamp

   /** Sets an ISO timestamp string into a field of length <em>len</em>,
    *  starting at the current position,
    *  and update the read/write pointer
    *  @param len width of field
    *  @param isoTimestamp string of the form yyyy-MM-dd
    *  @return modified position in buffer
    */
   public int setTimestamp(int len, java.sql.Timestamp isoTimestamp) {
       return setString(len, ISO_TIMESTAMP_FORMAT.format(isoTimestamp));
   } // setTimestamp

   /** Sets an ISO timestamp string into a field of length <em>len</em>,
    *  starting at the specified position,
    *  and update the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field
    *  @param isoTimestamp string of the form yyyy-MM-dd
    *  @return modified position in buffer
    */
   public int  setTimestamp(int start, int len, java.sql.Timestamp isoTimestamp) {
       currentPos = start;
       return setTimestamp(len, isoTimestamp);
   } // setTimestamp

   /** Sets an ISO timestamp string into a field
    *  and update the read/write pointer
    *  @param field field to be written (offset and length)
    *  @param isoTimestamp string of the form yyyy-MM-dd
    *  @return modified position in buffer
    */
   public int setTimestamp(Field field, java.sql.Timestamp isoTimestamp) {
       currentPos = field.start;
       return setTimestamp(field.len, isoTimestamp);
   } // setTimestamp

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
   public abstract long getNumber(int len) throws NumberFormatException;

   /** Gets a number from a field of length <em>len</em>,
    *  starting at the specified position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  and update the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field
    *  @return number read from the field
    *  @throws NumberFormatException for invalid characters (no digit, comma, point, dash)
    */
   public          long getNumber(int start, int len) throws NumberFormatException {
       currentPos = start;
       return getNumber(len);
   } // getNumber

   /** Gets a number from a field,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  and update the read/write pointer
    *  @param field field to be written (offset and length)
    *  @return number read from the field
    *  @throws NumberFormatException for invalid characters (no digit, comma, point, dash)
    */
   public          long getNumber(Field field) throws NumberFormatException {
       currentPos = field.start;
       return getNumber(field.len);
   } // getNumber

   /** Sets a number into a field of length <em>len</em>,
    *  starting at the current position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  and update the read/write pointer
    *  @param len width of field
    *  @param value number to be written
    *  @return modified position in buffer
    */
   public abstract int setNumber(int len, long value);

   /** Sets a number into a field of length <em>len</em>,
    *  starting at the specified position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  and updates the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field
    *  @param value number to be written
    *  @return modified position in buffer
    */
   public          int setNumber(int start, int len, long value) {
       currentPos = start;
       return setNumber(len, value);
   } // setNumber

   /** Sets a number into a field,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  and update the read/write pointer
    *  @param field field to be written (offset and length)
    *  @param value number to be written
    *  @return modified position in buffer
    */
   public          int setNumber(Field field, long value) {
       currentPos = field.start;
       return setNumber(field.len, value);
   } // setNumber

   /*==========*/
   /*  Amount  */
   /*==========*/

   /** Gets a number from a field of length <em>len</em>,
    *  starting at the current position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before <em>precision</em> digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param len width of field
    *  @param precision number of decimal places (0, 1, 2 ...)
    *  @return number read from the field (e.g. cents if precision = 2)
    */
   public long getAmount(int len, int precision) throws NumberFormatException {
       String localBuffer = getString(len);
       long result = 0; // assume not any digit
       int posFraction = -29647; // position of comma, very low
       int  sign = 1; // assume positive
       int start = 0;
       while (start < len) {
           // skip leading spaces
           char ch = localBuffer.charAt(start);
           if (Character.isDigit(ch)) {
             if (posFraction < precision) {
                   result = result * 10 + Character.digit(ch, 10);
               } // else ignore superfluous digit behind ','
               posFraction ++;
           } else if (ch == '-') {
               sign = -1;
           } else if (ch == ',') { // start of decimal places
               posFraction = 0;
           }
           else if ("., +".indexOf(ch) < 0) {
               throw new NumberFormatException("invalid character (no digit, ',', '.', '-', '+')");
           }
           start ++;
       } // while
       if (posFraction < 0) {
           posFraction = 0;
       }
       while (posFraction < precision) { // fill all remaining decimal places
           result *= 10;
           posFraction ++;
       }
       return result * sign;
   } // getAmount

   /** Gets a number from a field of length <em>len</em>,
    *  starting at the current position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before 2 digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param len width of field
    *  @return number read from the field (e.g. cents if precision = 2)
    */
   public long getAmount(int len) throws NumberFormatException {
       return getAmount(len, 2);
   } // getAmount

   /** Gets a number from a field of length <em>len</em>,
    *  starting at the specified position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before <em>precision</em> digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field
    *  @param precision number of decimal places (0, 1, 2 ...)
    *  @return number read from the field (e.g. cents if precision = 2)
    */
   public long getAmount(int start, int len, int precision) throws NumberFormatException {
       currentPos = start;
       return getAmount(len, precision);
   } // getAmount

   /** Gets a number from a field,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before <em>precision</em> digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param field field to be read
    *  @param precision number of decimal places (0, 1, 2 ...)
    *  @return number read from the field (e.g. cents if precision = 2)
    */
   public long getAmount(Field field, int precision) throws NumberFormatException {
       currentPos = field.start;
       return getAmount(field.len, precision);
   } // getAmount

   /** Gets a number from a field,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before 2 digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param field field to be read
    *  @return number read from the field (e.g. cents if precision = 2)
    */
   public long getAmount(Field field) throws NumberFormatException {
       currentPos = field.start;
       return getAmount(field.len, 2);
   } // getAmount

   /** Sets a number into a field of length <em>len</em>,
    *  starting at the current position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before <em>precision</em> digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param len width of field
    *  @param value number to be written (e.g. cents if precision = 2)
    *  @param precision number of decimal places (0, 1, 2 ...)
    *  @return modified position in buffer
    */
   public int setAmount(int len, long value, int precision) {
       StringBuffer localBuffer = new StringBuffer(len);
       int sign = 1;
       if (value < 0) {
           sign = -1;
           value = - value;
       }
       String digits = Long.toString(value); // number as string
       int start = 0;
       int bpos = len - 1; // runs right to left
       int posFraction = - precision; // position of decimal comma,
           // (-2, -1, 0(comma), 3(dot), 6(dot) )
       int ppos = digits.length () - 1; // rightmost position
       while (ppos >= 0 && bpos >= start) {
           if (posFraction == 0 && precision != 0) { // insert the comma
               localBuffer.setCharAt(bpos --, ',');
           } else if (posFraction % 3 == 0 && posFraction > 0) { // insert dot for 1000s
               if (bpos >= start) {
                   localBuffer.setCharAt(bpos --, '.');
               }
           }
           // insert digits right to left
           localBuffer.setCharAt(bpos --, digits.charAt(ppos --));
           posFraction ++;
       } // while
       while (posFraction < 0 && bpos >= start) { // fill zeroes behind decimal comma
           localBuffer.setCharAt(bpos --, '0');
           posFraction ++;
       } // while
       if (posFraction == 0 && bpos >= start) {
           localBuffer.setCharAt(bpos --, ',');
           if (bpos >= start) { // set 0 before ','
               localBuffer.setCharAt(bpos --, '0');
               posFraction ++;
           }
       }
       if (sign < 0 && bpos >= start) { // insert sign
           localBuffer.setCharAt(bpos --, '-');
       } // set positive sign
       while (bpos >= start) { // leading spaces
           localBuffer.setCharAt(bpos --, ' ');
       } // while leading spaces
       return setString(len, localBuffer.toString());
   } // setAmount

   /** Sets a number into a field of length <em>len</em>,
    *  starting at the current position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before <em>precision</em> digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param len width of field
    *  @param value number to be written (e.g. cents if precision = 2)
    *  @return modified position in buffer
    */
   public int setAmount(int len, long value) {
       return setAmount(len, value, 2);
   } // setAmount

   /** Sets a number into a field of length <em>len</em>,
    *  starting at the specified position,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before <em>precision</em> digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param start starting position of the field
    *  @param len width of field
    *  @param value number to be written into the field (e.g. cents if precision = 2)
    *  @param precision number of decimal places (0, 1, 2 ...)
    *  @return modified position in buffer
    */
   public int setAmount(int start, int len, long value, int precision) {
       currentPos = start;
       return setAmount(len, value, precision);
   } // setAmount

   /** Sets a number into a field,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before <em>precision</em> digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param field field to be written
    *  @param value number to be written into the field (e.g. cents if precision = 2)
    *  @param precision number of decimal places (0, 1, 2 ...)
    *  @return modified position in buffer
    */
   public int setAmount(Field field, long value, int precision) {
       currentPos = field.start;
       return setAmount (field.len, value, precision);
   } // setAmount

   /** Gets a number from a field,
    *  right-aligned, with leading zeroes and possibly a negative sign,
    *  with a decimal comma (before 2 digits) and
    *  maybe dots between groups of thousands,
    *  and updates the read/write pointer
    *  @param field field to be read
    *  @param value number to be written into the field (e.g. cents)
    *  @return modified position in buffer
    */
   public int setAmount(Field field, long value) {
       currentPos = field.start;
       return setAmount(field, value, 2);
   } // setAmount

   /*=======================================*/
   /* XML and SAX methods (pseudo-abstract) */
   /*=======================================*/

   /** Root element tag */
   public final String ROOT_TAG  = "records";

   /** Gets the namespace URI for these records
    *  @return namespace URI for <em>xtrans</em>
    */
   public String getNamespaceURI() {
       return "http://www.teherba.org/2006/xtrans/Record";
   } // getNamespaceURI

   /** Replaces offending characters in attributes by entities,
    *  and trim right trailing whitespace
    *  @param value value of the attribute
    *  @return string with offending characters replaced by entities
    */
   protected String replaceAttrText(String value) {
       return ("x" + value
               .replaceAll("&" , "&amp;")
               .replaceAll("\'", "&apos;")
               .replaceAll("\"", "&quot;")
               ).trim().substring(1);
   } // replaceAttrText

    /** Receive notification of the start of an element.
    *  Looks for the element which contains encoded strings.
    *  @param uri The Namespace URI, or the empty string if the element has no Namespace URI
    *  or if Namespace processing is not being performed.
    *  @param localName the local name (without prefix),
    *  or the empty string if Namespace processing is not being performed.
    *  @param qName the qualified name (with prefix),
    *  or the empty string if qualified names are not available.
    *  @param attrs the attributes attached to the element.
    *  If there are no attributes, it shall be an empty Attributes object.
    *  @throws SAXException for SAX errors
    */
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
   } // startElement

   /*==================*/
   /* XML Attributes   */
   /*==================*/

  /** Gets an XML attribute string with a trailing space
    *  @param name name of the attribute
    *  @param value value of the attribute
    *  @return a string of the form
    *  <pre>
    *      name="value"
    *  </pre>
    */
/*
   public String toXMLAttr(String name, String value) {
       return name + "=\""
               + value.replaceAll("&", "&amp;").replaceAll("\"", "&quot;")
               + "\" ";
   }
*/
   /** Gets an XML attribute string with a trailing space
    *  @param name name of the attribute
    *  @param value value of the attribute
    *  @return a string of the form
    *  <pre>
    *      name="value"
    *  </pre>
    */
/*
   public String toXMLAttr(String name, long value) {
       return name + "=\"" + Long.toString(value) + "\" ";
   }
*/
   /** Gets an XML attribute string with a trailing space
    *  @param name name of the attribute
    *  @param value value of the attribute
    *  @return a string of the form
    *  <pre>
    *      name="value"
    *  </pre>
    */
/*
   public String toXMLAttr(String name, java.sql.Date value) {
       return name + "=\"" + ISO_DATE_FORMAT.format(value) + "\" ";
   }
*/
   /** Gets an XML attribute string with a trailing space
    *  @param name name of the attribute
    *  @param value value of the attribute
    *  @return a string of the form
    *  <pre>
    *      name="value"
    *  </pre>
    */
/*
   public String toXMLAttr(String name, java.sql.Timestamp value) {
       return name + "=\"" + ISO_TIMESTAMP_FORMAT.format(value) + "\" ";
   }
*/

}
