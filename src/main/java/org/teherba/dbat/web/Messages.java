/*  Messages.java - Static help texts and other language specific messages for Dbat. äöüÄÖÜ
 *  @(#) $Id$ 
 *  2022-02-04: major version to be edited here, minor from CVSID = git checksum,  compile date from MANIFEST.MF
 *  2018-02-13: emailAddress from Configuration
 *  2018-01-11: property "console=none|select|update"
 *  2017-05-27: javadoc
 *  2017-02-11: adapted to www.teherba.org/dbat
 *  2016-12-09: add fr messages
 *  2016-12-09: view-source: not for Chrome
 *  2016-11-15: sourceUrl was specUrl
 *  2016-10-11: IOException
 *  2016-09-20: getVersionString with parameter localObject
 *  2016-09-16: getVersionString was from Configuration, now from MetaInfPage
 *  2016-08-26: package org.teherba.dbat.web
 *  2016-05-12: getViewSourceLink
 *  2012-12-10: getSortTitle
 *  2012-11-22: "V" before JDBC driver version
 *  2012-10-19: comment for 'usage' method removed; getDefaultCounterDesc
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
package org.teherba.dbat.web;
import  org.teherba.dbat.Configuration;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  org.teherba.common.web.BasePage;
import  org.teherba.common.web.MetaInfPage;
import  java.io.IOException;
import  java.io.Serializable;
import  java.sql.Driver;
import  java.sql.DriverManager;
import  java.util.Enumeration;
import  javax.servlet.http.HttpServletRequest;
import  org.xml.sax.helpers.AttributesImpl;

/** Language specific message texts and formatting for Dbat's user interface.
 *  Apart from the language specific processing is found in the JSPs (in dbat/web),
 *  all internationalization of the Java source code is assembled in this class.
 *  Currently implemented natural languages (denoted by 2-letter ISO <em>country</em> codes) are:
 *  <ul>
 *  <li>en - English</li>
 *  <li>de - German</li>
 *  </ul>
 *  <p>
 *  All methods in this class are not stateful, and therefore are
 *  <em>static</em> for easier activation.
 *  @author Dr. Georg Fischer
 */
public class Messages implements Serializable {
    public final static String majorVersion = "12";
    public final static String CVSID = "@(#) $Id$";

    /** EMail address for meta pages */
    private static String emailAddress = "punctum@punctum.com";

    /** No-args Constructor
     */
    public Messages() {
    } // Constructor

    /** Sets the application-specific error message texts
     *  @param basePage reference to the hash for message texts
     *  @param config object for configuration parameters
     */
    public static void addMessageTexts(BasePage basePage, Configuration config) {
        String appLink = "<a title=\"main\" href=\"index.html\">" + basePage.getAppName() + "</a>";
        //--------
        emailAddress = config.getEmailAddress();
        basePage.add("en", "001", appLink);
        basePage.add("en", "002"
                , " <a href=\"mailto:" + emailAddress
                + "?&subject=" + basePage.getAppName()
                + "\">" + emailAddress /* .substring(0, emailAddress.indexOf("@")) */ + "</a>"
                );
        //--------
        String laux = basePage.LANG_AUX;  // pseudo language code for links to auxiliary information
        int imess   = basePage.START_AUX; // start of messages    for links to auxiliary information
        String
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"index\"       href=\"servlet?spec=index\">");
        basePage.add("en", smess, "{parm}List</a> of available specifications");
        basePage.add("de", smess, "{parm}Liste</a> der abrufbaren Spezifikationen");
        basePage.add("fr", smess, "{parm}Liste</a> des spécifications disponibles");
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"con\"         href=\"servlet?view=con\">");
        basePage.add("en", smess, "{parm}SQL Console</a>");
        basePage.add("de", smess, "{parm}SQL-Konsole</a>");
        basePage.add("fr", smess, "{parm}Console SQL</a>");
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"describe\"    href=\"servlet?spec=describe\">");
        basePage.add("en", smess, "{parm}describe DDL</a> of a Table or View");
        basePage.add("de", smess, "{parm}describe </a> - DLL einer Tabelle oder View");
        basePage.add("fr", smess, "{parm}décris LDD</a> d'une table (virtuelle)");
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"help\"        href=\"servlet?view=help&lang=en\">");
        basePage.add("en", smess, "{parm}Help</a> - Commandline Options");
        basePage.add("de", smess, "{parm}Hilfe</a> - Kommandozeilen-Optionen");
        basePage.add("fr", smess, "{parm}Aide</a> - options de l'interface en ligne de commande");
        smess = String.format("%03d", imess ++);
        basePage.add(laux, smess, "<a title=\"validate\"    href=\"servlet?view=validate&lang=en\">");
        basePage.add("en", smess, "{parm}Validate</a> Regular Expressions");
        basePage.add("de", smess, "{parm}Validierung</a> von regulären Ausdrücken");
        basePage.add("fr", smess, "{parm}Validation</a> des expressions régulières");
        imess = basePage.addStandardLinks(imess);
        //--------
        basePage.add("en", "301", "Specification file <em>{parm}</em> was moved to <em><a href=\"{par2}\">{par2}</a></em>."
                + "<br />Please update your bookmarks."
                + "<br />You will be redirected to the new page in {par3} s.");
        basePage.add("de", "301", "Die Spezifikationsdatei <em>{parm}</em> wurde nach <em><a href=\"{par2}\">{par2}</a>"
                + "</em> verschoben."
                + "<br />Bitte &auml;ndern Sie Ihre Favoriten/Lesezeichen."
                + "<br />Sie werden in {par3} s auf die neue Seite umgelenkt.");
        basePage.add("fr", "301", "Le fichier de spécification <em>{parm}</em> a été déplacé vers <em><a href=\"{par2}\">{par2}</a></em>."
                + "<br />Veuillez mettre à jour vos signets."
                + "<br />Vous serez redirigé vers la nouvelle page dans {par3} s.");
        //--------
        basePage.add("en", "404", "A specification file <em>{parm}</em> was not found."
                + "<br />Please check the "    + appLink + " home page.");
        basePage.add("de", "404", "Eine Spezifikationsdatei <em>{parm}</em> wurde nicht gefunden."
                + "<br />Bitte rufen Sie die " + appLink + "-Startseite auf.");
        basePage.add("fr", "404", "Un fichier de spécification <em>{parm}</em> n'a pas été trouvé. "
                + "<br />Veuillez vérifier la page d'accueil" + appLink + ".");
        //--------
        basePage.add("en", "410", "Execution of SQL instructions is not allowed.");
        basePage.add("de", "410", "SQL-Befehle sind nicht erlaubt.");
        basePage.add("fr", "410", "Commandes SQL  ne sont pas possible.");
        basePage.add("en", "411", "Execution of SQL instructions for DB connection "
                + "<em>{parm}</em> is not allowed.");
        basePage.add("de", "411", "SQL-Befehle f&#xfc;r DB-Verbindung "
                + "<em>{parm}</em> sind nicht erlaubt.");
        basePage.add("fr", "411", "Commandes SQL pour DB connection "
                + "<em>{parm}</em> ne sont pas possible.");
        //--------
    } // addMessageTexts

    /** Gets the default word particles for the count of rows below a result table
     *  @param language ISO country code: "de", "en"
     *  @return language specific word particles
     */
    public static String getDefaultCounterDesc(String language) {
        String result = null;
        if (false) {
        } else if (language.equals("de")) {
            result = "Zeile,n";
        } else if (language.equals("fr")) {
            result = "Rangée,s";
        } else { // default: en
            result = "row,s";
        }
        return result;
    } // getDefaultCounterDesc

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
        } else if (language.equals("fr")) {
            result += ""
                    +"Pour l'entrée rouge des champs une erreur de validation a eu lieu. <br />"
                    + "S'il vous plaît mettez le curseur sur le champ ou cliquez sur l'étoile pour afficher la "
                    + "<a href=\"servlet?view=validate&amp;lang=" + language + "&amp;regex=\" target=\"_blank\">règle de validation</a>."
                    ;
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
                + "  -f  input.sql   process SQL statements from this file\n"
                + "  -f  spec.xml    test a web specification file (with -p parameters) \n"
        //      + "  -g  col         SELECT col, count(col) FROM table GROUP BY col\n"
                + "  -g  columns     comma separated list of columns which cause a group change (new headings)\n"
                + "  -h              print this usage help text\n"
                + "  -i  table       print INSERT statements for \"SELECT * FROM table\"\n"
                + "  -l  l1,l2,...   define output column widths (for -m fix)\n"
                + "  -m  mode        output mode: tsv (TAB-separated values, default), csv (c.f. -s),\n"
                + "                  dbiv, echo, fix (c.f. -l), htm(l), jdbc, json, probe,\n"
                + "                  spec, sql, sqlj, taylor, trans, xls(x), xml\n"
                + "  -n  table       SELECT count(*) FROM table\n"
                + "  -p  name=val    optional parameter setting (repeatable)\n"
                + "  -r  table [url] load raw ([whitespace] separated) values from an URL (or from STDIN) into table\n"
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
                + "  -f  input.sql   SQL-Anweisungen in dieser Datei ausfuehren\n"
                + "  -f  spec.xml    Test einer Web-Spezifikationsdatei (mit Parametern nach -p) \n"
        //      + "  -g  col         SELECT col, count(col) FROM table GROUP BY col\n"
                + "  -g  columns     Liste von Spalten fuer einen Gruppenwechsel (mit neuen Ueberschriften)\n"
                + "  -h              Ausgabe dieses Hilfetexts\n"
                + "  -i  table       INSERT-Anweisungen fuer \"SELECT * FROM table\" ausgeben\n"
                + "  -l  l1,l2,...   Definition der Spalten-Breiten bei -m fix\n"
                + "  -m  mode        Ausgabe-Modus: tsv (TAB-getrennt, Default), csv (vgl. -s),\n"
                + "                  dbiv, echo, fix (vgl. -l), htm(l), jdbc, json, probe,\n"
                + "                  spec, sql, sqlj, taylor, trans, xls(x), xml\n"
                + "  -n  table       SELECT count(*) FROM table\n"
                + "  -p  name=val    Parameterwert setzen (wiederholbar)\n"
                + "  -r  table [url] Roh-Werte (durch Leerraum getrennt) von URL (oder von STDIN) in table laden\n"
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

    /** French help text */
    private static final String frHelpText = ""
                + "Utilisation:\n"
                + "  java -jar dbat.jar [-acdfghlnrstvx] (table | \"sql\"| file | - | parameter ...)\n"
                + "  java org.teherba.dbat.Dbat \"SELECT entry, morph FROM words\"\n"
                + "  -29 tableau     SELECT * FROM tableau FETCH FIRST 29 ROWS ONLY\n"
                + "  -a colonne      aggreate (concatène) les valeurs de cette colonne\n"
             // + "  -b              utilise INSERT de lot\n"
                + "  -c propfile     fichier de propriétés avec les paramètres de connexion etc.\n"
                + "  -d tableau      affiche la description LDD (DROP / CREATE TABLE) pour le tableau\n"
                + "  -e codage       ISO-8859-1 (par défaut), UTF-8 etc. (deux fois pour l'entrée / sortie)\n"
                + "  -f fichier.sql  processus des instructions SQL à partir de ce fichier\n"
                + "  -f spec.xml     teste un fichier de spécification web (avec les paramètres -p)\n"
             // + "  -g col          SELECT col, count (col) FROM table GROUP BY col\n"
                + "  -g colonnes     liste de colonnes séparées par des virgules qui provoquent un changement de groupe (nouveaux en-têtes)\n"
                + "  -h              imprime ce texte d'aide d'utilisation\n"
                + "  -i tableau      imprimer les instructions INSERT pour \"SELECT * FROM tableau\"\n"
                + "  -l l1, l2, ...  définissent les largeurs des colonnes de sortie (pour -m fix)\n"
                + "  -m mode         mode de sortie: tsv (valeurs séparées par TAB, par défaut), csv (c.f. -s),\n"
                + "                  dbiv, echo, fix (c.f. -l), htm(l), jdbc, json, probe,\n"
                + "                  spec, sql, sqlj, taylor, trans, xls(x), xml\n"
                + "  -n tableau      SELECT count (*) FROM tableau\n"
                + "  -p name=val     réglage optionnel des paramètres (répétable)\n"
                + "  -r tableau[url] Insérer les valeurs brutes ([espace] séparées) d'un URL (ou de STDIN) dans le tableau\n"
                + "  -s sep          séparateur pour mode csv\n"
                + "  -sa sep         séparateur pour une agrégation\n"
                + "  -sp sep         séparateur pour CREATE PROCEDURE\n"
                + "  -t tableau      nom de tableau (pour -g)\n"
                + "  -v              verbose: imprimer les instructions SQL et le temps d'exécution\n"
                + "  -x              imprime aucune rubrique / résumé\n"
             // + "  -z fichier      Fichier ZIP qui fournit ou reçoit des valeurs de colonnes (B)LOB\n"
                + "Les options et les actions sont évaluées de gauche à droite.\n"
                + "Les instructions SQL doivent contenir un espace, et doivent être insérées entre guillemets doubles.\n"
                + "Les noms de fichiers ne doivent pas contenir d'espaces. '-' est STDIN.\n"
                + "Pilotes JDBC inclus:\n"
                ;
    /** Get the Dbat's version, the explanation of the options and
     *  the available JDBC drivers.
     *  @param language 2-letter code en, de, fre
     *  @param config all configuration properties
     *  @param tableFactory factory for table serializers
     *  @throws IOException if an IO error occurs
     *  @return a block of plain text
     */
    public static String getHelpText(String language, Configuration config, TableFactory tableFactory) throws IOException {
        StringBuffer help = new StringBuffer(2048);
        final String SPACE2 = "  ";
        String version = (new MetaInfPage()).getVersionString(tableFactory, "dbat")
            .replaceAll("V\\d+", "V" + majorVersion);
        int checkPos = CVSID.indexOf(": ");
        if (checkPos >= 0) {
            version = version.replaceAll("\\.\\w+", "." + CVSID.substring(checkPos + 2, checkPos + 6)); // @(#) $Id$;
        }
        help.append("Dbat " + version + " - DataBase Application Tool\n");
        if (false) {
        } else if (language.startsWith("de")) {
            help.append(deHelpText);
        } else if (language.startsWith("fr")) {
            help.append(frHelpText);
        } else {
            help.append(enHelpText);
        }
        try { Class.forName("com.ibm.db2.jcc.DB2Driver" ).newInstance(); } catch (Exception exc) { }
        try { Class.forName("com.mysql.jdbc.Driver"     ).newInstance(); } catch (Exception exc) { }
        try { Class.forName("org.sqlite.JDBC"           ).newInstance(); } catch (Exception exc) { }
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                String driverName = driver.toString();
                int atPos = driverName.indexOf('@');
                if (atPos < 0) {
                    atPos = driverName.length();
                }
                help.append(SPACE2);
                help.append(driverName.substring(0, atPos));
                help.append(" V");
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
        } else if (language.startsWith("fr")) {
            help.append("Formats de sortie inclus (-m):\n");
        } else {
            help.append("Implemented output formats (-m):\n");
        }
        help.append(tableFactory.getHelpList(language));
        
        if (false) {
        } else if (language.startsWith("de")) {
            help.append("Email-Adresse: ");
        } else if (language.startsWith("fr")) {
            help.append("Adresse email: ");
        } else {
            help.append("Email address: ");
        }
        help.append(config.getEmailAddress());
        help.append("\n");
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
                    + ", " + manipulatedSum + " betroffene Zeile"            + (manipulatedSum != 1 ? "n" : "")
                    + " in " + Long.toString((System.nanoTime() - startTime) / 1000000L) + " ms");
        } else if (language.equals("fr")) {
            result = (""
                    + " traité " +  instructionSum + " instruction"  +   (instructionSum != 1 ? "s" : "") + " SQL"
                    + ", " + manipulatedSum + (manipulatedSum != 1 ? " rangées concernées" : " rangée concernée")
                    + " en " + Long.toString((System.nanoTime() - startTime) / 1000000L) + " ms");
        } else { // default: en
            result = (""
                    + " executed " +  instructionSum + " SQL statement"  + (instructionSum != 1 ? "s" : "")
                    + " affecting " + manipulatedSum + " row"            + (manipulatedSum != 1 ? "s" : "")
                    + " in " + Long.toString((System.nanoTime() - startTime) / 1000000L) + " ms");
        }
        return result;
    } // getTimingMessage

    /** Gets the title text for a sortable column.
     *  @param language ISO country code: "de", "en"
     *  @return language specific title attribute for the &lt;th&gt; element.
     */
    public static String getSortTitle(String language) {
        StringBuffer result = new StringBuffer(64);
        if (false) {
        } else if (language.equals("de")) {
            result.append("Klick =&gt; Sort");
        } else if (language.equals("fr")) {
            result.append("Clic =&gt; tri");
        } else {
            result.append("Click =&gt; Sort");
        }
        return result.toString();
    } // getSortTitle

    /** Gets a link to the source representation of a specification file
     *  with "view-source:" and the full URL
     *  if the user's browser knows this feature, or the empty string otherwise
     *  @param request Http request with User-Agent header and the URL
     *  @return link or ampty string
     */
    public static String getViewSourceLink(HttpServletRequest request) {
        String result = "";
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null
                &&  (   userAgent.indexOf("Firefox/") >= 0
           //       ||  userAgent.indexOf("Chrome/" ) >= 0
                    ||  userAgent.indexOf("OPR/"    ) >= 0 // Opera now V37; >= V17
                    )
                &&  (   userAgent.indexOf("Edge/"   ) <  0 // and all Internet Explorer versions do not know view-source:
                    )
                ) { // User-Agent is suitable
            result = request.getRequestURL().toString(); // all upto, but not inlcuding "?" - http://localhost:8080/dbat/servlet
            int spos = result.lastIndexOf("/");
            result = "view-source:" + result.substring(0, spos) + "/";
        } // User-Agent suitable for view-source:
        return result;
    } // getViewSourceLink

    /** Gets the markup text for the page trailer.
     *  For HTML and XML, the text contains links.
     *  @param trailerSelect a space separated list of keywords, with a leading space, in any order:
     *  " none plain out time dbat script xls more"
     *  @param language ISO country code: "de", "en"
     *  @param sourceUrl  link to the specification source
     *  @param specName base name (with subdirectory, without ".xml") of the Dbat specification file
     *  @param xlsUrl   link to Excel display of the query results
     *  @param moreUrl  link to "more" page
     *  @return language specific trailer markup text,
     *  for example:
     *  <pre>
     *  Output on 2011-08-05 21:03:40 by Dbat script test/align01, Excel, more
     *  </pre>
     */
    public static String getTrailerText(String trailerSelect, String language, String sourceUrl, String specName, String xlsUrl, String moreUrl) {
        StringBuffer result = new StringBuffer
                (128);
                // ("<!-- " + trailerSelect + "-->");
        boolean withLink = ! trailerSelect.contains(" plain");
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
        } else if (language.startsWith("fr")) {
                outPart     = "Sorties";
                timePart    = " au ";
                dbatPart    = " par ";
                scriptPart  = " script ";
                xlsPart     = "Excel";
                morePart    = "plus";
        } else { // default: en
        }
        if (trailerSelect.contains(" out")) {
            result.append(outPart);
        } // out
        if (trailerSelect.contains(" time")) {
            result.append(timePart);
            result.append(BaseTable.getISOTimestamp());
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
            //  result.append("view-source:"); // View-source is applied in SpecificationHandler
                result.append(sourceUrl);
                result.append("\"");
                result.append(" type=\"text/plain\""); // neither text/xml, application/xhtml+xml nor application/xml did work
                result.append(">");
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
     *  @param language ISO country code: "de", "en", "fr"
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
            } else if (language.equals("fr")) {
                title = "Erreur pendant la validation de champ avec pattern &quot;";
            } else { // default: en
                title = "Error in field validation with pattern &quot;";
            }
        } else {
            if (false) {
            } else if (language.equals("de")) {
                title = "Feld-Validierung gegen Muster &quot;";
            } else if (language.equals("fr")) {
                title = "Valididation de champ avec pattern &quot;";
            } else { // default: en
                title = "Field validation with pattern &quot;";
            }
        } // valid
        attrs2.addAttribute("", "title", "title", "CDATA", title  + escapedPattern + "&quot;");
        return result;
    } // validateFormField

} // Messages
