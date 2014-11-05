<?xml version="1.0" encoding="UTF-8"?>
<!--
    Generates a Dbiv interactive view from Dbat's XML description of a view/table
    @(#) $Id: dbat_dbiv.xsl 935 2012-05-14 17:51:59Z gfis $
    2012-05-09, Dr. Georg Fischer: copied from dbiv_spec.xsl
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
        xmlns:iv  ="http://www.teherba.org/2011/dbiv"
        xmlns     ="http://www.teherba.org/2011/dbiv"
        xmlns:db  ="http://www.teherba.org/2007/dbat"
        xmlns:ht  ="http://www.w3.org/1999/xhtml"
        xmlns:date="http://exslt.org/dates-and-times"
        xmlns:str ="http://exslt.org/strings"
        extension-element-prefixes="date str"
        >
    <xsl:param name="conn"  >worddb</xsl:param><!-- connection id -->
    <xsl:param name="lang"  >en</xsl:param><!-- language; default: de -->
    <xsl:param name="area"  >test</xsl:param><!-- application (sub-) division -->
    <xsl:param name="debug" >true</xsl:param><!-- true or empty -->
    <xsl:param name="rdbms" >mysql</xsl:param><!-- mysql, db2zos, db2luw, oracle ... -->

    <xsl:output method="xml" indent="yes" encoding="UTF-8" />
    <xsl:strip-space elements="*"/>

<xsl:template match="dbat">
    <!-- Transformation of the root element -->
    <xsl:comment><!-- File header comment -->
        <xsl:value-of select="concat(' Interactive View', create/@name, '.iv.xml', ' äöü&#10;')" />
        <xsl:value-of select="concat('&#9;@(#) $', 'Id$', '&#10;')" /> <!-- CVS inserts timestamp here -->
        <xsl:value-of select="concat('&#9;Generated by dbat/etc/xslt/dbat_dbiv.xsl at ', date:date-time(), '&#10;')" />
<!--
        <xsl:value-of select="concat('&#9;DO NOT EDIT HERE!','&#10;')" />
-->
    </xsl:comment>
    <dbiv   xmlns   ="http://www.teherba.org/2011/dbiv"
            xmlns:db="http://www.teherba.org/2007/dbat"
            xmlns:ht="http://www.w3.org/1999/xhtml"
            encoding="UTF-8"
            lang    ="$lang"
            conn    ="$conn"
            >
            <xsl:attribute name="lang"  ><xsl:value-of select='$lang' /></xsl:attribute>
            <xsl:attribute name="conn"  ><xsl:value-of select='$conn' /></xsl:attribute>
            <xsl:attribute name="script"><xsl:value-of select='concat($area, "/", ./create/@name)' /></xsl:attribute>
        <xsl:apply-templates select="create" />
        <xsl:value-of select='"&#10;"' />
    </dbiv>
</xsl:template>

<xsl:template match="create">
    <xsl:value-of select='"&#10;&#9;"' />
    <view>
        <xsl:attribute name="name"><xsl:value-of select='@name' /></xsl:attribute>
        <xsl:value-of select='"&#10;&#9;&#9;"' />
        <title>
            <ht:a>
                <xsl:attribute name="href"><xsl:value-of select='concat("servlet?spec=", $area, ".index")' /></xsl:attribute>
                <xsl:value-of select='@name' />
            </ht:a>
            <xsl:value-of select='concat(" - Form ", @name)' />
        </title>
        <xsl:value-of select='"&#10;&#9;&#9;"' />
        <counter desc="Row,s" />
        <xsl:apply-templates select="column" />
        <xsl:value-of select='"&#10;&#9;"' />
    </view>
</xsl:template>

<xsl:template match="column">
    <xsl:value-of select='"&#10;&#9;&#9;"' />
    <field>
        <xsl:attribute name="name"  ><xsl:value-of select='@name'   /></xsl:attribute>
        <xsl:attribute name="label" >
            <xsl:choose>
                <xsl:when test='string-length(@remark) != 0'>
                    <xsl:value-of select='@remark' />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@name" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>

        <xsl:choose>
            <xsl:when test='@type = "VARCHAR"'>
                <xsl:attribute name="type"  ><xsl:value-of select='"char"'  /></xsl:attribute>
            </xsl:when>
            <xsl:when test='@type = "INT" or @type = "DECIMAL"'>
                <xsl:attribute name="type"  >
                    <xsl:value-of select='translate(@type, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz")' />
                </xsl:attribute>
                <xsl:attribute name="align" ><xsl:value-of select='"right"' /></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="type"  >
                    <xsl:value-of select='translate(@type, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz")' />
                </xsl:attribute>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:if test='string-length(@width) != 0'>
        <xsl:attribute name="size"  ><xsl:value-of select='@width'  /></xsl:attribute>
        </xsl:if>
        <xsl:value-of select='"&#10;&#9;&#9;"' />
    </field>
</xsl:template>

<xsl:template match="db:*">
    <!-- ignore -->
</xsl:template>

</xsl:stylesheet>
