/*  LDAPSearch.java - run a query against an LDAP server
    @(#) $Id$
    2011-12-20: Dr. Georg Fischer: copied from
	http://www.stonemind.net/blog/2008/01/23/a-simple-ldap-query-program-in-java/
	http://www.java.net/pub/a/today/2006/04/18/ldaptemplate-java-ldap-made-simple.html
	http://www.coderanch.com/t/134154/Security/LDAP-example-JAVA
	
	http://tools.ietf.org/html/rfc4515 for search syntax

usage:
	java -cp dist/dbat.jar org.teherba.dbat.LDAPSearch cn,l,mail "(objectClass=person)" 
prints:
	Larry Fine	Dallas	LFine@isp.com
	Moe Howard	Dallas	MHoward@isp.com
	Curley Howard	Dallas	CHoward@isp.com
*/

package org.teherba.dbat;
import  org.teherba.dbat.Configuration;
import	javax.naming.NamingEnumeration;
import	javax.naming.Context;
import	javax.naming.directory.Attributes;
import	javax.naming.directory.BasicAttribute;
import	javax.naming.directory.DirContext;
import	javax.naming.directory.InitialDirContext;
import	javax.naming.directory.SearchControls;
import	javax.naming.directory.SearchResult;
import	java.util.Hashtable;
import  org.apache.log4j.Logger;

/** Initializes a connection to an LDAP server, and runs a query.
 */
public class LDAPSearch {
    public final static String CVSID = "@(#) $Id$";
    /** log4j logger (category) */
    private Logger log;
	
    /** No-args Constructor
     */
    public LDAPSearch() {
        log = Logger.getLogger(LDAPSearch.class.getName());
    } // Constructor

	/** Search with a filter, and print the values for the specified attributes
	 *	@param filter LDAP filter expression according to RFC 4515
	 *	@param attributes list of attributes to be returned
	 */
	public void search(String filter, String[] attributes) {
        try {
            StringBuffer buffer = new StringBuffer(256);
            String url = "ldap://localhost:389/o=stooges";
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY	, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL			, url);
			env.put(Context.SECURITY_PRINCIPAL		,"cn=StoogeAdmin,o=stooges"); // DN
			env.put(Context.SECURITY_CREDENTIALS	,"secret1"); // password
            DirContext context = new InitialDirContext(env);

            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration enumeration = context.search("", filter, controls);
            while (enumeration.hasMore()) {
                SearchResult result = (SearchResult) enumeration.next();
                Attributes attribs = result.getAttributes();
				buffer.setLength(0);
                int iattr = 0;
                while (iattr < attributes.length) {
                	NamingEnumeration values = ((BasicAttribute) attribs.get(attributes[iattr])).getAll();
                	while (values.hasMore()) {
                		if (buffer.length() > 0) {
                			buffer.append('\t');
                		}
                	  	buffer.append(values.next().toString());
                	} // while values
                	iattr ++;
                } // while iattr
		        System.out.println(buffer.toString());
            } // while search results             
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
        }
	} // search
	
    //====================
    // Main method - Test
    //====================
    
    /** Test driver -
     *  call it with -h to display possible options and arguments.
     *  The result is printed to STDOUT.
     *  @param args command line arguments: options, strings, table- or filenames
     */
    public static void main(String[] args) {
    	String[] attributes = new String[] { "cn", "l", "mail" };
		String filter = "(objectClass=person)";
		int iargs = 0;
		if (iargs < args.length) {
   	    	attributes = args[iargs ++].split("\\,\\s*");
			if (iargs < args.length) {
	        	filter = args[iargs ++];
        	} // >= 2 arguments
        } // >= 1 argument
        
        LDAPSearch ldapSearch = new LDAPSearch();
        ldapSearch.search(filter, attributes);
    } // main

} // LDAPSearch
