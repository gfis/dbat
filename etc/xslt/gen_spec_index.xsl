<?xml version="1.0" encoding="UTF-8"?>
<!--
    Generate an INSERT statement for the descriptive table spec_index
    @(#) $Id$
    2016-04-16: evaluate <parm>s
    2012-04-13: COMMIT behind every INSERT
    2011-03-17: normalize-space
    2011-02-10: Dr. Georg Fischer
-->
<!--
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
-->
<xsl:stylesheet version="1.0"
        xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
        xmlns:ht  ="http://www.w3.org/1999/xhtml"
        xmlns:db  ="http://www.teherba.org/2007/dbat"
        >
    <xsl:output method="text" /> <!-- i.e. Java source code -->
    <xsl:strip-space elements="*" /> <!-- remove whitespace in all nodes -->
    <xsl:param name="filename" />
    <xsl:param name="lang" />

    <xsl:variable name="subdir">
        <xsl:choose>
            <xsl:when test="string-length(substring-before($filename, '/')) &gt; 0">
                <xsl:value-of select="substring-before($filename, '/')" />
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="name">
        <xsl:choose>
            <xsl:when test="string-length(substring-before($filename, '/')) &gt; 0">
                <xsl:value-of select="substring-before(substring-after($filename, '/'), '.xml')" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="substring-before($filename, '.xml')" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

    <xsl:template match="db:dbat">
        <xsl:value-of select='concat("&#10;INSERT INTO spec_index", " (subdir, name, lang, title, comment, params) VALUES (&#10;&#9;")' />
        <xsl:value-of select='concat("&apos;", $subdir  )' />
        <xsl:value-of select='concat("&apos;,&apos;", $name      )' />
        <xsl:value-of select='concat("&apos;,&apos;", $lang      )' />
        <xsl:value-of select='concat("&apos;,&apos;", @title     )' />
        <xsl:value-of select='concat("&apos;", ",&#10;&#9;&apos;")' />
        <xsl:apply-templates select="db:comment" />
        <xsl:value-of select='concat("&apos;", "&#10;&#9;,&apos;")' />
        <xsl:apply-templates select="ht:form" />
        <xsl:apply-templates select="node()" mode="parm" />
        <xsl:value-of select='concat("&apos;", ");"              )' />
        <xsl:value-of select='concat("&#10;", "COMMIT;")' />
    </xsl:template>
        
    <xsl:template match="db:comment">
        <xsl:if test="@lang = $lang">
            <xsl:value-of select="concat(normalize-space(.), ' ')" />
        </xsl:if>
    </xsl:template>
    
	<xsl:template match="node()" mode="parm">
       	<xsl:apply-templates select="node()" mode="parm" />
	</xsl:template>

    <xsl:template match="db:parm" mode="parm">
        <xsl:value-of select="concat('&amp;', ./@name, '=', ./@init, ' ')" />
    </xsl:template>
    
    <xsl:template match="ht:form">
        <xsl:for-each select="ht:input">
            <xsl:if test="string-length(./@size) &gt; 0">
                <xsl:value-of select="concat('&amp;', ./@name, '=', ./@init, ' ')" />
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>
