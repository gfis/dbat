/*  CommandTokenizer.java - some strange conversion (we don't comment it)
 *  @(#) $Id$
 *  2016-10-13: less imports
 *  2013-01-05: split()
 *  2012-11-24: handling of '.' and '/'
 *  2012-11-09: remove superfluous code
 *  2011-07-21: '_' is also a word char
 *  2010-09-10: tokenizeArgs and tokenizeSQL
 *  2010-02-02, Georg Fischer
 *
 *  tokenizeSQL and echoFormattedSQL are not yet finished
*/
/*
 * Copyright 2010 Dr. Georg Fischer <punctum at punctum dot kom>
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
package org.teherba.common;
import  org.teherba.common.URIReader;
import  java.io.Serializable;
import  java.io.StreamTokenizer;
import  java.io.StringReader;
import  java.util.ArrayList;
import  java.util.StringTokenizer;
import  java.util.regex.Matcher;
import  java.util.regex.Pattern;
import  org.apache.log4j.Logger;

/** Commandline and SQL tokenizing, and some additional, strange conversion 
 *  (we do not comment it).
 *  @author Dr. Georg Fischer
 */
public class CommandTokenizer implements Serializable {
    public final static String CVSID = "@(#) $Id$";

    /** log4j logger (category) */
    private static Logger log = Logger.getLogger(CommandTokenizer.class.getName());;

    /** Splits a commandline into single arguments: words, options, numbers, strings (which were
     *  single or double quoted)                    
     *  @param command string to be splitted
     */ 
    public static String[] tokenize(String command) {
        ArrayList/*<1.5*/<String>/*1.5>*/ result = new ArrayList/*<1.5*/<String>/*1.5>*/();
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(command));
            // Caution, real ranges would not work on a non-ASCII JVM !!
            tokenizer.wordChars('_', '_');
            // DB2 would allow '#', too?
            tokenizer.ordinaryChar('.'); // Better call this a bug than a feature: needed to deactivate the numerical interpretation.
            tokenizer.wordChars('.', '.');
            tokenizer.wordChars(':', ':');
            tokenizer.wordChars('=', '=');
            tokenizer.quoteChar('\'');
            tokenizer.quoteChar('\"');
            tokenizer.wordChars('/', '/');
            tokenizer.ordinaryChar(','); // Better call this a bug than a feature: needed to deactivate the numerical interpretation.
            tokenizer.wordChars(',', ',');
            tokenizer.ordinaryChar('-'); // Better call this a bug than a feature: needed to deactivate the numerical interpretation.
            tokenizer.wordChars('-', '-'); // we want to prefix option words by "-"
            int token = tokenizer.nextToken();
            while (token != StreamTokenizer.TT_EOF) {
                switch (tokenizer.ttype) {
                    case '\'': 
                        result.add(              (tokenizer.sval));
                        break;
                    case '\"': 
                        result.add(              (tokenizer.sval));
                        break;
                    case StreamTokenizer.TT_EOL:
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        result.add(String.valueOf(new Double(tokenizer.nval).longValue()));
                        break;
                    case StreamTokenizer.TT_WORD:
                        result.add(              (tokenizer.sval));
                        break;
                    default:
                        result.add(String.valueOf((char) tokenizer.ttype));
                        break;
                } // switch
                token = tokenizer.nextToken();
            }  // while token
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result.toArray(new String[0]);
    } // tokenize

    /** Splits an SQL statement into words, options, numbers, strings (which were
     *  single or double quoted), parentheses and commas.
     *  @param sql string to be splitted
     */ 
    public static String[] tokenizeSQL(String sql) {
        ArrayList/*<1.5*/<String>/*1.5>*/ result = new ArrayList/*<1.5*/<String>/*1.5>*/();
        try {
            StringTokenizer tokenizer = new StringTokenizer(sql, " \r\n\"\',()", true); // return delimiters
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                char ttype = token.charAt(0);
                switch (ttype) {
                    case '\'': 
                        result.add("\'" + token + "\'");
                        break;
                    case '\"': 
                        result.add("\"" + token + "\"");
                        break;
                    case '\r':
                        break;
                    case '\n':
                        break;
                    default:
                        result.add(token);
                        break;
                } // switch
            }  // while tokens
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result.toArray(new String[0]);
    } // tokenizeSQL

    /** Splits a command into words, options, numbers, strings (which were
     *  single or double quoted)
     *  @param cmd String to be splitted
     */ 
    public static String[] split(String cmd) {
        ArrayList/*<1.5*/<String>/*1.5>*/ result = new ArrayList/*<1.5*/<String>/*1.5>*/();
        try {
            Pattern pattern = Pattern.compile("\\s+|[^ \"']+|\\\"[^\\\"]*\\\"|\\'[^\\']*'"); 
            		// whitespace, words+punctuation, double or single quoted strings (without nested quotes)
            Matcher matcher = pattern.matcher(cmd);
            while (matcher.find()) {
                String part = cmd.substring(matcher.start(), matcher.end());
                char ch0 = part.charAt(0);
                switch (ch0) {
                    case '\'': 
                        result.add(part.substring(1, part.length() - 1));
                        break;
                    case '\"': 
                        result.add(part.substring(1, part.length() - 1));
                        break;
                    case ' ':
                        // ignore separating whitespace
                        break;
                    default:
                        result.add(part);
                        break;
                } // switch
            }  // while tokens
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
        return result.toArray(new String[0]);
    } // tokenizeSQL

    /** Converts a string
     *  @param instr input string
     *  @return output string
     */
    public static String encode(String instr) {
        StringBuffer buf = new StringBuffer(32);
        char ipos = 0;
        while (ipos < instr.length()) {
            char ch = instr.charAt(ipos ++);
            ch ^= (ipos + 2);
            ch ++;
            buf.append(ch);
        } // while ipos
        return buf.toString();
    } // encode
    
    /** Converts a string
     *  @param instr input string
     *  @return output string
     */
    public static String decode(String instr) {
        StringBuffer buf = new StringBuffer(32);
        char ipos = 0;
        while (ipos < instr.length()) {
            char ch = instr.charAt(ipos ++);
            ch --;
            ch ^= (ipos + 2);
            buf.append(ch);
        } // while ipos
        return buf.toString();
    } // decode
    
    /** Main program, reads strings from the commandline and prints the 
     *  converted versions.
     *  @param args commandline arguments:
     *  <ul>
     *  <li>empty, 0 arguments - tokenize a preprogrammed commandline</li>
     *  <li>1 argument: encode it as a token</li>
     *  <li>-f filename - tokenize all lines in that file</li>
     *  <li>-sql filename - tokenize all lines in that file obeying SQL syntax (not properly implemented)</li>
     *  </ul> 
     */
    public static void main(String args[]) throws Exception {
        Logger log = Logger.getLogger(CommandTokenizer.class.getName());;
        int iarg = 0;
        int itok = 0;
        String[] tokens = null;
        if (false) {
        } else if (args.length == 0) { // test method 'tokenize'
            tokens = tokenize("call  my.pr1 -in \"2 double quoted\" -in:int 29647 -in \'2 single quoted\';");
            itok = 0;
            while (itok < tokens.length) {
                System.out.println("token " + itok + " = \"" + tokens[itok] + "\"");
                itok ++;
            } // while
            System.out.println("Number of tokens: " + itok);
        } else if (args[0].equals("-f") || args[0].startsWith("-s")) {
            try {
                URIReader reader = new URIReader(args[1]);
                String line = null;
                while ((line = reader.readLine()) != null) { 
                    tokens = args[0].equals("-f") ? tokenize(line) : tokenize(line);
                    itok = 0;
                    while (itok < tokens.length) {
                        System.out.println(tokens[itok]);
                        itok ++;
                    } // while
                } // while reading from URI
                reader.close();
            } catch (Exception exc) {
                log.error(exc.getMessage(), exc);
            }       
        } else { // 1 argument => encode a token
            while (iarg < args.length) {        
                System.out.print("token=");
                System.out.println(encode(args[iarg ++]));
            } // while iarg
        } // encode
    } // main
    
} // CommandTokenizer
