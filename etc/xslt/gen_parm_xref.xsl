<?xml version="1.0" encoding="UTF-8"?>
<!--
    Extract all href= and link= attributes, and the name of <parm> and input elements
    @(#) $Id: gen_parm_xref.xsl 864 2012-01-23 19:31:18Z gfis $
	2012-01-18: Dr. Georg Fischer: copied from gen_spec_index.xsl
-->
<!--
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
-->
<xsl:stylesheet version="1.0"
        xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
		xmlns:ht  ="http://www.w3.org/1999/xhtml"
        xmlns:db  ="http://www.teherba.org/2007/dbat"
        >
    <xsl:output method="text" /> <!-- i.e. Java source code -->
    <xsl:strip-space elements="*" /> <!-- remove whitespace in all nodes -->
    <xsl:param name="file" />
<!--
    <xsl:variable name="init">
    	<xsl:value-of select='"INSERT INTO parm_xref(element, name, spec, file) VALUES (&apos;"' />
    </xsl:variable>
    <xsl:variable name="term">
    	<xsl:value-of select='"&apos;);&#10;"' />
    </xsl:variable>
    <xsl:variable name="sep">
    	<xsl:value-of select='"&apos;,&apos;"' />
    </xsl:variable>
-->
    <xsl:variable name="init">
    	<xsl:value-of select='""' />
    </xsl:variable>
    <xsl:variable name="term">
    	<xsl:value-of select='"&#10;"' />
    </xsl:variable>
    <xsl:variable name="sep">
    	<xsl:value-of select='"&#9;"' />
    </xsl:variable>

	<xsl:template match="db:dbat">
			<xsl:value-of select='concat($init, "dbat"		, $sep, "spec",$sep, $file, $sep, $file, $term)' />
		<xsl:apply-templates select="db:select" />
        <xsl:for-each select="//db:parm">
			<xsl:value-of select='concat($init, "parm"		, $sep, @name, $sep, $file, $sep, $file, $term)' />
        </xsl:for-each>
        <xsl:for-each select="//db:when">
			<xsl:value-of select='concat($init, "when"		, $sep, @name, $sep, $file, $sep, $file, $term)' />
        </xsl:for-each>
        <xsl:for-each select="//db:listparm">
			<xsl:value-of select='concat($init, "listparm"	, $sep, @name, $sep, $file, $sep, $file, $term)' />
        </xsl:for-each>
        <xsl:for-each select="//ht:input">
        	<xsl:if test="string-length(@name) &gt; 0" >
			<xsl:value-of select='concat($init, "input"		, $sep, @name, $sep, $file, $sep, $file, $term)' />
			</xsl:if>
        </xsl:for-each>
        <xsl:for-each select="//ht:select">
			<xsl:value-of select='concat($init, "select"	, $sep, @name, $sep, $file, $sep, $file, $term)' />
        </xsl:for-each>
        <xsl:for-each select="//ht:textarea">
			<xsl:value-of select='concat($init, "textarea"	, $sep, @name, $sep, $file, $sep, $file, $term)' />
        </xsl:for-each>
	</xsl:template>

	<xsl:template match="db:select">
		<xsl:apply-templates select="db:col" />
	</xsl:template>

	<xsl:template match="db:col">
        <xsl:for-each select="@link">
			<xsl:value-of select='concat($init, "link"		, $sep, .	 , $sep,        $sep, $file, $term)' />
        </xsl:for-each>
        <xsl:for-each select="@href">
			<xsl:value-of select='concat($init, "href"		, $sep, .    , $sep,        $sep, $file, $term)' />
        </xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
