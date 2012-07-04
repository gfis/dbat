/*  MorePage.java - replacement for more.jsp: input form with all parameters
 *  @(#) $Id$
 *	2012-07-01: subpackage view
 *  2012-02-11, Georg Fischer: copied from more.jsp
 */
/*
 * Copyright 2012 Dr. Georg Fischer <punctum at punctum dot kom>
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
package org.teherba.dbat.view;
import  org.teherba.dbat.format.BaseTable;
import  org.teherba.dbat.format.TableFactory;
import  java.io.PrintWriter;
import	java.util.Iterator;
import  java.util.Map;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  org.apache.log4j.Logger;

/** This class prints a form which contains all parameters
 *	and options for table output, together with links to 
 *  overview, API, notices and other documentation.
 *	The code is extracted from the former <em>more.jsp</em>.
 *  @author Dr. Georg Fischer
 */
public class MorePage {
	public final static String CVSID = "@(#) $Id$";
	public final static long serialVersionUID = 19470629;

	/** log4j logger (category) */
	private Logger log;

	/** No-argument constructor
	 */
	public MorePage() {
		log = Logger.getLogger(MorePage.class.getName());
	} // constructor()
	
	/** Processes an http GET request
	 *  @param request request with header fields
	 *  @param response response with writer
	 *	@param tableFactory factory for table serializers
	 *  @throws IOException
	 */
	public void forward(HttpServletRequest request, HttpServletResponse response, TableFactory tableFactory) {
		try {
			PrintWriter out = response.getWriter();
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
            out.write("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            out.write("<head>\n");
            out.write("<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml;charset=UTF-8\" />\n");
            out.write("<meta name=\"robots\" content=\"noindex, nofollow\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" />\n");

			out.write("<title>Database Administration Tool</title>\n");
			out.write("<style>\ntd,th\n");
			out.write("{vertical-align:top;margin:0px;padding-top:0px;padding-bottom:0px;padding-left:10px;padding-right:10px;border:none;}\n</style>\n<script src=\"script.js\" type=\"text/javascript\">\n</script>\n</head>\n");
			String[] optEnc    = new String []
					/*  0 */ { "ISO-8859-1"
					/*  1 */ , "UTF-8"
					} ;
			String[] enEnc    = new String []
					/*  0 */ { "ISO-8859-1"
					/*  1 */ , "UTF-8"
					} ;
			String[] optLang = new String []
					/*  0 */ { "de"
					/*  1 */ , "en"
					} ;
			String[] enLang = new String []
					/*  0 */ { "Deutsch"
					/*  1 */ , "English"
					} ;
			String encoding     = "ISO-8859-1";
			String mode         = "html";
			String language     = "en";
			String specName     = "?";
			Map parameterMap = request.getParameterMap(); // do not! use /*<1.5*/<String, String[]>/*1.5>*/
			Iterator /*<1.5*<String>*1.5>*/ parmIter = parameterMap.keySet().iterator();
			StringBuffer inputFields = new StringBuffer(256);

			while (parmIter.hasNext()) {
				String name = (String) parmIter.next();
				String[] values = request.getParameterValues(name);
				if (values.length <= 0) { // ignore empty value lists
				} else if (name.equals("enc"		)) {
					encoding = values[0];
				} else if (name.equals("mode"		)) {
					mode     = values[0];
				} else if (name.equals("lang"		)) {
					language = values[0];
				} else if (name.equals("spec"		)) {
					specName = values[0];
				} else if (name.equals("view"		)) {
					// ignore
				} else { // unknown parameter name - must be an input field
					inputFields.append("<tr><td>");
					inputFields.append(name);
					inputFields.append("</td><td><input name=\"");
					inputFields.append(name);
					inputFields.append("\" value=\"");
					inputFields.append(values[0]);
					inputFields.append("\" /></td><td>&nbsp;</td></tr>\n");
				} // unknown
			} // while parmIter
			int index = 0;
			out.write("<body>\n<!--\nenc=\"");
			out.write(encoding);
			out.write("\", mode=\"");
			out.write(mode);
			out.write("\", lang=\"");
			out.write(language);
			out.write("\", spec=\"");
			out.write(specName);
			out.write("\" \n-->\n<h3><a href=\"index.html\">Dbat</a>");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("-Spezifikation ");
			} else {
				out.write(" Specification ");
			}
			out.write("<em><a href=\"spec/");
			out.write(specName.replaceAll("\\.", "/"));
			out.write(".xml\">");
			out.write(specName);
			out.write("</a></em>\n</h3>\n");
			
			out.write("<form action=\"servlet\" method=\"get\">\n");
			out.write("<input type = \"hidden\" name=\"view\" value=\"\" />\n");
			out.write("<input type = \"hidden\" name=\"spec\" value=\"");
			out.write(specName);
			out.write("\" />\n");

			out.write("<table cellpadding=\"8\">\n<tr><td class=\"bold\">\n");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Parametername");
			} else {
				out.write("Parameter Name");
			}
			out.write("\n</td><td class=\"bold\">\n");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Wert");
			} else {
				out.write("Value");
			}
			out.write("</td>\n<td>&nbsp;</td>\n</tr>\n");
			out.write(inputFields.toString());
			out.write("\n<tr><td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n</tr>\n");
			out.write("<tr><td class=\"bold\">Encoding</td>\n<td class=\"bold\">\n");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Ausgabeformat");
			} else {
				out.write("Output Format");
			}
			out.write("</td>\n<td>&nbsp;</td>\n</tr>\n<tr valign=\"top\">\n<td>\n<select name=\"enc\" size=\"");
			out.write(String.valueOf(optEnc.length));
			out.write("\">\n");
			index = 0;
			while (index < optEnc.length) {
				out.write("<option value=\""
						  + optEnc[index] + "\""
						  + (optEnc[index].equals(encoding) ? "  selected=\"1\"" : "")
						  + ">"
						  + enEnc[index] + "</option>\n");
				index ++;
			} // while index
			out.write("</select>\n<br />\n<br />\n <span class=\"bold\">\n");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Sprache");
			} else {
				out.write("Language");
			}
			out.write("</span>\n<br />\n<select name=\"lang\" size=\"");
			out.write(String.valueOf(optLang.length));
			out.write("\">\n");
			index = 0;
			while (index < optLang.length) {
				out.write("<option value=\""
						  + optLang[index] + "\""
						  + (optLang[index].equals(language) ? "  selected=\"1\"" : "")
						  + ">"
						  + enLang[index] + "</option>\n");
				index ++;
			} // while index
			out.write("</select>\n</td>\n<td>\n<select name=\"mode\" size=\"");
			out.write(String.valueOf(tableFactory.getCount()));
			out.write("\">\n");
			Iterator /*<1.5*/<BaseTable>/*1.5>*/ iter = tableFactory.getIterator();
			while (iter.hasNext()) {
				BaseTable tableFormat = (BaseTable) iter.next();
				String code = tableFormat.getFirstFormatCode();
				out.write("<option value=\"" + code + "\""
						  + (code.equals(mode) ? " selected=\"1\"" : "")
						  + ">"
						  + code + " - " + tableFormat.getDescription(language) + "</option>\n");
			} // while iter
			out.write("</select><p />&nbsp;\n</td><td>");
			out.write("<ul>\n");

			out.write("<li><a href=\"servlet?view=con\">");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("SQL-Konsole");
			} else {
				out.write("SQL Console");
			}
			out.write("\n</li>\n");

			out.write("<li><a href=\"index.html\">");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("&Uuml;bersicht");
			} else {
				out.write("Overview");
			}
			out.write("</a>, <a href=\"servlet?view=validate&value=M&regex=\\w\">");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Regex-Validierung</a>");
			} else {
				out.write("Regex Validation</a>");
			}
			out.write("\n</li>\n");

			out.write("<li><a href=\"servlet?spec=describe\">");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("DDL</a> einer Tabelle oder View");
			} else {
				out.write("DDL</a> of a table or view");
			}
			out.write("\n</li>\n<li><a href=\"servlet?spec=index\">");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Liste</a> der abrufbaren Spezifikationen");
			} else {
				out.write("List</a> of available specifications");
			}
			out.write("\n</li>\n<li><a href=\"servlet?view=help&lang=\"");
			out.write(language);
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("\">Hilfe - Kommandozeilen-Optionen");
			} else {
				out.write("\">Help - Commandline Options");
			}
			out.write("\n</a>\n</li>\n<li>");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("<a href=\"docs/Dbat.html\">Allgemeine Beschreibung</a>");
			} else {
				out.write("<a href=\"docs/Dbat.html\">General Description</a>");
			}
			out.write(",\n");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("<a href=\"docs/Dbat-Spezifikation.html\">Spezifikationsdateien</a>");
			} else {
				out.write("<a href=\"docs/Dbat-Spezifikation.html\">Specification Files</a>");
			}
			out.write("\n</li>\n<li><a href=\"docs/api/index.html\">");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("API-Dokumentation</a>");
			} else {
				out.write("API Documentation</a>");
			}
			out.write("\n(Javadoc)\n</li>");
			out.write("\n<li><a href=\"servlet?view=manifest\">\nManifest\n</a>");
			out.write(", \n<a href=\"servlet?view=license\">");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Lizenz</a>");
			} else {
				out.write("License</a>");
			}
			out.write(", \n<a href=\"servlet?view=notice\"  >");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Referenzen</a>");
			} else {
				out.write("References</a>");
			}
			out.write("\n</li>\n</ul>\n");
			out.write("<input type=\"submit\" value=\"");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Absenden");
			} else {
				out.write("Submit");
			}
			out.write("\" />\n</td>\n</tr>\n");
			out.write("</table>\n</form>\n");
			out.write("<p>\n<span style=\"font-size:small\">\n");
			if (false) {
			} else if (language.startsWith("de")) {
				out.write("Fragen, Hinweise:");
			} else {
				out.write("Questions, remarks:");
			}
			out.write("\n<a href=\"mailto:punctum@punctum.com\">Dr. Georg Fischer</a>\n</span>\n</p>\n");
			out.write("</body>\n</html>\n");
		} catch (Exception exc) {
			log.error(exc.getMessage(), exc);
		} finally {
		}
	} // forward

} // MorePage
