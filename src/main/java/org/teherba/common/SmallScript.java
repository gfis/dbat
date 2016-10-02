/*  SmallScript: methods for superscript and subscript digits
 *  @(#) $Id$
 *  2015-11-04: Georg Fischer
 */
/*
 * Copyright 2015 Dr. Georg Fischer <punctum at punctum dot kom>
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
import  java.lang.StringBuffer;

/** Class SmallScript implements methods for the 
 *  conversion of ordinary number strings to 
 *  superscripted and subscripted representations with Unicode characters.
 *  @author Dr. Georg Fischer
 */
public class SmallScript {
    private static final long serialVersionUID = 1L;
    public final static String CVSID = "@(#) $Id$";

    /** No-args Constructor
     */
    public SmallScript() {
    } // no-args Constructor

    /** Convert to superscripts
     *  @param number number to be converted
     *  @return a sequence of Unicode superscript digits
     */
    public static String toSuperscript(int number) {
        String value = String.valueOf(number);
        StringBuffer result = new StringBuffer(16);
        int ival = 0;
        while (ival < value.length()){
            int chv = value.charAt(ival);
            switch (chv) {
                default: // '-','+', ' '
                    result.append(chv);
                    break;
                case '0':
                    result.append('\u2070');
                    break;
                case '1':
                    result.append('\u00B9');
                    break;
                case '2':
                    result.append('\u00B2');
                    break;
                case '3':
                    result.append('\u00B3');
                    break;
                case '4':
                    result.append('\u2074');
                    break;
                case '5':
                    result.append('\u2075');
                    break;
                case '6':
                    result.append('\u2076');
                    break;
                case '7':
                    result.append('\u2077');
                    break;
                case '8':
                    result.append('\u2078');
                    break;
                case '9':
                    result.append('\u2079');
                    break;
            } // switch chv 
            ival ++;
        } // while ival     
        return result.toString();
    } // toSupersrcipt

    /** Convert to subscripts
     *  @param number number to be converted
     *  @return a sequence of Unicode subscript digits
     */
    public static String toSubscript(int number) {
        String value = String.valueOf(number);
        StringBuffer result = new StringBuffer(16);
        int ival = 0;
        while (ival < value.length()){
            int chv = value.charAt(ival);
            switch (chv) {
                default: // '-','+', ' '
                    result.append(chv);
                    break;
                case '0':
                    result.append('\u2080');
                    break;
                case '1':
                    result.append('\u2081');
                    break;
                case '2':
                    result.append('\u2082');
                    break;
                case '3':
                    result.append('\u2083');
                    break;
                case '4':
                    result.append('\u2084');
                    break;
                case '5':
                    result.append('\u2085');
                    break;
                case '6':
                    result.append('\u2086');
                    break;
                case '7':
                    result.append('\u2087');
                    break;
                case '8':
                    result.append('\u2088');
                    break;
                case '9':
                    result.append('\u2089');
                    break;
            } // switch chv 
            ival ++;
        } // while ival     
        return result.toString();
    } // toSubscript
    
    /** Append a the representation of an exponent, possibly as superscript
     *  @param result append to this StringBuffer
     *  @param mode 
     *  <ul>
     *  <li>0 = normal</li>
     *  <li>1 = full (for substitution), with coefficients 1 and relation " = 0"</li>
     *  <li>2 = nice / human legible, with superscripts for exponents</li>
     *  <li>3 = with prime factors</li>
     *  <li>4 = with colored prime factors</li>
     *  <li>5 = exponentiation as products</li>
     *  </ul>
     *  @param exp integer exponent to be represented
     */
    public static void appendExponent(StringBuffer result, int mode, int exp) {
        switch (mode) {
            default:
            case 0:
                if (exp > 1) {
                    result.append('^');
                    result.append(exp);
                }
                break;
            case 1:
                result.append('^');
                result.append(exp);
                break;
            case 2:
            case 3:
            case 4:
                switch (exp) {
                    case 1:
                        break;
                    case 2:
                        result.append('\u00b2');
                        break;
                    case 3:
                        result.append('\u00b3');
                        break;
                    default:
                        if (true) {
                            // Windows console can't display these
                            // even with 'chcp 1253' or 'chcp 65001'
                            result.append(SmallScript.toSuperscript(exp));
                        } else {
                            result.append('^');
                            result.append(exp);
                        }
                        break;
                } // switch exp
                break;
        } // switch mode
    } // appendExponent
    
} // SmallScript