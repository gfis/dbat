/*  Messages.java - Static help texts and other language specific messages for Dbat.
 *  @(#) $Id$
 *  2012-06-19: more modes; TIMESTAMP_FORMAT for humans; trailer line configurable
 *  2012-04-19: try/catch the known JDBC drivers for -h
 *  2012-01-20: refer to index.html instead of index.jsp
 *  2011-08-06: validateFormField, getTrailerText
 *  2011-05-06, Dr. Georg Fischer: extracted from Dbat.java
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
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.TableFactory;
import  java.io.Serializable;
import  java.sql.Driver;
import  java.sql.DriverManager;
import  java.text.SimpleDateFormat;
import  java.util.Enumeration;
import  org.xml.sax.helpers.AttributesImpl;

/** Language specific message texts and formatting for Dbat's user interface.
 *  Apart from the language specific processing is found in the JSPs (in dbat/web),
 *  all internationalization of the Java source code is assembled in this class.
 *  Currently implemented natural languages (denoted by 2-letter ISO <em>country</em> codes) are:
 *  <ul>
 *  <li>en - English</li>
 *  <li>de - German</li>
 *  </ul>
 *  <p />
 *  All methods in this class are not stateful, and therefore are
 *  <em>static</em> for easier activation.
 *  @author Dr. Georg Fischer
 */
public class Messages implements Serializable {
    public final static String CVSID = "@(#) $Id$";

    /** No-args Constructor
     */
    public Messages() {
    } // Constructor

    /** Gets the message text for a notice about form field validation errors
     *  @param language ISO country code: "de", "en"
     *  @return language specific message text
     */
    public static String getErrorNotice(String language) {
        String result = "<h4 style=\"color:red\">";
        if (false) {
        } else if (language.equals("de")) {
            result += ""
                    + "Bei den rot hinterlegten Eingabefeldern ist ein Validierungsfehler aufgetreten.<br /> "
                    + "Bitte setzen Sie den Mauszeiger auf das Feld oder klicken Sie auf den Stern, um die "
                    + "<a href=\"servlet?view=validate&amp;lang=" + language + "&amp;regex=\" target=\"_blank\">Validierungsregel</a>"
                    + " anzuzeigen.";
        } else { // default: en
            result += ""
                    + "There was a validation problem with the input field(s) highlighted in red.<br /> "
                    + "Please move the mouse cursor over the field or click on the asterisk to see the "
                    + "<a href=\"servlet?view=validate&amp;lang=" + language + "&amp;regex=\" target=\"_blank\">validation rule</a>"
                    + ".";
        }
        return result + "</h4>";
    } // getErrorNotice

    /** English help text */
    private static final String enHelpText = ""
                + "usage:\n"
                + "  java -jar dbat.jar [-acdfghlnrstvx] (table | \"sql\" | file | - | parameter ...)\n"
                + "  java org.teherba.dbat.Dbat \"SELECT entry, morph FROM words\"\n"
                + "  -29 table       SELECT * FROM table FETCH FIRST 29 ROWS ONLY\n"
                + "  -a  column      aggreate (concatenate) the values of this column\n"
        //      + "  -b              use batch INSERTs\n"
                + "  -c  propfile    properties file with connection a.o. parameters\n"
                + "  -d  table       print DDL description (DROP/CREATE TABLE) for table\n"
                + "  -e  encoding    ISO-8859-1 (default), UTF-8 etc. (twice for input/output)\n"
                + "  -f  inputfile   process SQL statements from this file\n"
                + "  -f  spec.xml    test a web specification file (with -p parameters) \n"
        //      + "  -g  col         SELECT col, count(col) FROM table GROUP BY col\n"
                + "  -g  columns     comma separated list of columns which cause a group change (new headings)\n"
                + "  -h              print this usage help text\n"
                + "  -i  table       print INSERT statements for \"SELECT * FROM table\"\n"
                + "  -l  l1,l2,...   define output column widths (for -m fix)\n"
                + "  -m  mode        output mode: tsv (TAB-separated values, default),\n"
                +         "\t\t\tcsv (c.f. -s), echo, fix (c.f. -l), htm(l), jdbc, json, probe, spec, sql, sqlj, taylor, trans, xls, xml\n"
                + "  -n  table       SELECT count(*) FROM table\n"
                + "  -p  name=val    optional parameter setting (repeatable)\n"
                + "  -r  table       insert raw ([whitespace] separated) values from STDIN into table\n"
                + "  -s  sep         separator string for mode csv\n"
                + "  -sa sep         separator string for -a aggregation\n"
                + "  -sp sep         separator string for CREATE PROCEDURE\n"
                + "  -t  table       table name (for -g)\n"
                + "  -v              verbose: print SQL statements and execution time\n"
                + "  -x              print no headings/summary\n"
        //      + "  -z  file        ZIP file which provides or receives (B)LOB column values\n"
                + "Options and actions are evaluated from left to right.\n"
                + "SQL statements must contain a space. Enclose them in double quotes.\n"
                + "Filenames may not contain spaces. '-' is STDIN.\n"
                + "Included JDBC drivers:\n"
                ;

    /** Deutscher Hilfetext */
    private static final String deHelpText = ""
                + "Aufruf:\n"
                + "  java -jar dbat.jar [-acdfghlnrstvx] (table | \"sql\" | file | - | parameter ...)\n"
                + "  java org.teherba.dbat.Dbat \"SELECT entry, morph FROM words\"\n"
                + "  -29 table       SELECT * FROM table FETCH FIRST 29 ROWS ONLY\n"
                + "  -a  column      Zusammenfassung der Werte dieser Spalte pro Gruppe in einer Zeile\n"
        //      + "  -b              use batch INSERTs\n"
                + "  -c  propfile    Properties-Datei mit Verbindungs- u.a. Parametern\n"
                + "  -d  table       DDL-Beschreibung (DROP/CREATE TABLE) der Tabelle ausgeben\n"
                + "  -e  encoding    ISO-8859-1 (default), UTF-8 etc. (2 x fuer Ein-/Ausgabe)\n"
                + "  -f  inputfile   SQL-Anweisungen in dieser Datei ausfuehren\n"
                + "  -f  spec.xml    Test einer Web-Spezifikationsdatei (mit Parametern nach -p) \n"
        //      + "  -g  col         SELECT col, count(col) FROM table GROUP BY col\n"
                + "  -g  columns     Liste von Spalten fuer einen Gruppenwechsel (mit neuen Ueberschriften)\n"
                + "  -h              Ausgabe dieses Hilfetexts\n"
                + "  -i  table       INSERT-Anweisungen fuer \"SELECT * FROM table\" ausgeben\n"
                + "  -l  l1,l2,...   Definition der Spalten-Breiten bei -m fix\n"
                + "  -m  mode        Ausgabe-Modus: tsv (TAB-getrennt, Default),\n"
                +         "\t\t\tcsv (c.f. -s), echo, fix (c.f. -l), htm(l), jdbc, json, probe, spec, sql, sqlj, taylor, trans, xls, xml\n"
                + "  -n  table       SELECT count(*) FROM table\n"
                + "  -p  name=val    Parameterwert setzen (wiederholbar)\n"
                + "  -r  table       Roh-Werte (durch Leerraum getrennt) von STDIN in table INSERTen\n"
                + "  -s  sep         Trennzeichen(kette) fuer Modus csv\n"
                + "  -sa sep         Trennzeichen fuer Zusammenfassung mit -a\n"
                + "  -sp sep         Trennzeichen fuer CREATE PROCEDURE\n"
                + "  -t  table       Tabellenname (fuer -g)\n"
                + "  -v              SQL-Anweisungen und Ausfuehrungszeiten ausgeben\n"
                + "  -x              Keine Spaltenueberschriften und Zeilenanzahl ausgeben\n"
        //      + "  -z  file        ZIP file which provides or receives (B)LOB column values\n"
                + "Optionen und Aktionen werden von links nach rechts abgearbeitet.\n"
                + "SQL-Anweisungen muessen ein Leerzeichen enthalten und in doppelte Anf.zeichen"
                + " eingeschlossen werden. Dateinamen duerfen keine Leerzeichen enthalten. '-' ist STDIN.\n"
                + "Eingebundene JDBC-Treiber:\n"
                ;

    /** Get the tools version, the explanation of the options and
     *  the available JDBC drivers.
     *  @param language one of "en", "de"
     *  @return a block of plain text
     */
    public static String getHelpText(String language) {
        StringBuffer help = new StringBuffer(2048);
        final String SPACE2 = "  ";
        help.append(Configuration.getVersionString() + " - DataBase Application Tool" + "\n");
        if (false) {
        } else if (language.startsWith("de")) {
            help.append(deHelpText);
        } else {
            help.append(enHelpText);
        }
        try { Class.forName("com.ibm.db2.jcc.DB2Driver" ).newInstance(); } catch (Exception exc) { }
        try { Class.forName("com.mysql.jdbc.Driver"     ).newInstance(); } catch (Exception exc) { }
        try { Class.forName("org.sqlite.JDBC"           ).newInstance(); } catch (Exception exc) { }
        try {
            Enumeration/*<1.5*/<Driver>/*1.5>*/ drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                String driverName = driver.toString();
                int atPos = driverName.indexOf('@');
                if (atPos < 0) {
                    atPos = driverName.length();
                }
                help.append(SPACE2);
                help.append(driverName.substring(0, atPos));
                help.append(' ');
                help.append(driver.getMajorVersion());
                help.append('.');
                help.append(driver.getMinorVersion());
                help.append("\n");
            } // while enumerating
        } catch (Exception exc) {
            // log.error(exc.getMessage(), exc);
        }
        if (false) {
        } else if (language.startsWith("de")) {
            help.append("Implementierte Ausgabeformate (-m):\n");
        } else {
            help.append("Implemented output formats (-m):\n");
        }
        help.append((new TableFactory()).getHelpList(language));
        return help.toString();
    } // getHelpText

    /** Gets the message text for timing
     *  @param language ISO country code: "de", "en"
     *  @param startTime time where measurement started
     *  @param instructionSum number of SQL instructions which were executed
     *  @param manipulatedSum number of rows which were affected by the instruction
     *  @param url JDBC driver's URL
     *  @return language specific message text
     */
    public static String getTimingMessage(String language, long startTime, int instructionSum, int manipulatedSum, String url) {
        String result = "";
        if (false) {
        } else if (language.equals("de")) {
            result = (""
                    + " verarbeitete " +  instructionSum + " SQL-Anweisung"  + (instructionSum != 1 ? "en" : "")
                //  + " mit " + config.getDriverURL()
                    + ", " + manipulatedSum + " betroffene Zeile"            + (manipulatedSum != 1 ? "n" : "")
                    + " in " + Long.toString((System.nanoTime() - startTime) / 1000000L) + " ms");
        } else { // default: en
            result = (""
                    + " executed " +  instructionSum + " SQL statement"  + (instructionSum != 1 ? "s" : "")
                //  + " on " + config.getDriverURL()
                    + " affecting " + manipulatedSum + " row"            + (manipulatedSum != 1 ? "s" : "")
                    + " in " + Long.toString((System.nanoTime() - startTime) / 1000000L) + " ms");
        }
        return result;
    } // getTimingMessage

    /** ISO timestamp without milliseconds */
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    /** Gets the markup text for the page trailer.
     *  For HTML and XML, the text contains links.
     *  @param trailerSelect a space separated list of keywords, with a leading space, in any order:
     *	" none plain out time dbat script xls more"
     *  @param language ISO country code: "de", "en"
     *  @param specUrl  link to the specification source
     *  @param specName base name (with subdirectory, without ".xml") of the Dbat specification file
     *  @param xlsUrl   link to Excel display of the query results
     *  @param moreUrl  link to "more" page
     *  @return language specific trailer markup text,
     *  for example:
     *  <pre>
        Output on 2011-08-05T21:03:40.419 by script test/align01, Excel, more
     *  </pre>
     */
    public static String getTrailerText(String trailerSelect, String language, String specUrl, String specName, String xlsUrl, String moreUrl) {
        StringBuffer result = new StringBuffer
        		(128);
        		// ("<!-- " + trailerSelect + "-->"); 
        boolean withLink = ! trailerSelect.contains(",plain");  
        boolean comma = false; // whether to prefix a part with a comma
        String  outPart     = "Output";
        String  timePart    = " on ";
        String  dbatPart    = " by ";
        String  scriptPart  = " script ";
        String  xlsPart     = "Excel";
        String  morePart    = "more";
        if (false) {
        } else if (language.startsWith("de")) {
                outPart     = "Ausgabe";
                timePart    = " am ";
                dbatPart    = " durch ";
                scriptPart  = "-Skript ";
                xlsPart     = "Excel";
                morePart    = "mehr";
        } else { // default: en
        }
        if (trailerSelect.contains(" out")) {
            result.append(outPart);
        } // out
        if (trailerSelect.contains(" time")) {
            result.append(timePart);
            result.append(TIMESTAMP_FORMAT.format(new java.util.Date()));
            comma = true;
        } // time
        if (trailerSelect.contains(" dbat")) {
            result.append(dbatPart);
            if (withLink) {
                result.append("<a href=\"index.html\">");
            }
            result.append("Dbat");
            if (withLink) {
                result.append("</a>");
            }
            comma = true;
        } // dbat
        if (trailerSelect.contains(" script")) {
            result.append(scriptPart);
            if (withLink) {
                result.append("<a target=\"_blank\" href=\"");
                result.append(specUrl);
                result.append("\" type=\"text/plain\">");
            }
            result.append(specName);
            if (withLink) {
                result.append("</a>");
            }
            comma = true;
        } // script
        if (trailerSelect.contains(" xls")) {
            if (comma) {
                result.append(',');
                result.append(withLink ? '\n' : ' ');
            }
            if (withLink) {
                result.append("<a target=\"_blank\" href=\"");
                result.append(xlsUrl);
                result.append("\">");
            }
            result.append(xlsPart);
            if (withLink) {
                result.append("</a>");
            }
            comma = true;
        } // xls       
        if (trailerSelect.contains(" more")) {
            if (comma) {
                result.append(',');
                result.append(withLink ? '\n' : ' ');
            }
            if (withLink) {
                result.append("<a href=\"");
                result.append(moreUrl);
                result.append("\">");
            }
            result.append(morePart);
            if (withLink) {
                result.append("</a>");
            }
            comma = true;
        } // more
        if (comma) {
            result.append('\n');
 		}
        return result.toString();
    } // getTrailerText

    /** Sets the attributes of an input form field depending on the outcome
     *  of a validation check
     *  @param language ISO country code: "de", "en"
     *  @param attrs2 attributes to be modified
     *  @param value input value from the field
     *  @param pattern validation pattern
     *  @return 0 for success, 2 for failure of validation
     */
    public static int validateFormField(String language, AttributesImpl attrs2, String value, String pattern) {
        int result = 0;
        int
        index = attrs2.getIndex("class");
        if (index >= 0) {
            attrs2.removeAttribute(index);
        }
        index = attrs2.getIndex("title");
        if (index >= 0) {
            attrs2.removeAttribute(index);
        }
        String escapedPattern = pattern
                .replaceAll("&"     , "&amp;")
                .replaceAll("<"     , "&lt;")
                .replaceAll(">"     , "&gt;")
                .replaceAll("\\\""  , "&quot;")
                .replaceAll("\\\'"  , "&apos;")
                ;
        String title = null;
        if (! value.matches(pattern)) {
            result = 2;
            String cssClass = "red";
            attrs2.addAttribute("", "class", "class", "CDATA", cssClass);
            if (false) {
            } else if (language.equals("de")) {
                title = "Fehler bei der Feld-Validierung gegen Muster &quot;";
            } else { // default: en
                title = "Error in field validation with pattern &quot;";
            }
        } else {
            if (false) {
            } else if (language.equals("de")) {
                title = "Feld-Validierung gegen Muster &quot;";
            } else { // default: en
                title = "Field validation with pattern &quot;";
            }
        } // valid
        attrs2.addAttribute("", "title", "title", "CDATA", title  + escapedPattern + "&quot;");
        return result;
    } // validateFormField

    /** Display help: commandline options and arguments
     *  @param language language to be used for text output
     */
    public static void usage(String language) {
        try {
            System.out.println(getHelpText(language));
        } catch (Exception exc) {
            // ignore
            // log.error(exc.getMessage(), exc);
        }
    } // usage

    //================
    // Main method
    //================

    /** Test driver - shows the Dbat help text.
     *  The result is printed to STDOUT.
     *  @param args language code: "en", "de"
     */
    public static void main(String[] args) {
        Messages help = new Messages();
        String language = "en"; // default: English
        if (args.length > 0) {
            language = args[0];
        }
        help.usage(language);
    } // main

} // Messages
