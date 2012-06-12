<?xml version="1.0"  encoding="UTF-8"?>
<!--
	Add an attribute trans="formed" to all <td> elements
	@(#) $Id: formed.xsl 889 2012-02-17 18:48:22Z gfis $
    2012-02-16, Dr. Georg Fischer: copied from brackets.xsl, for test of &mode="trans"
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
	<xsl:strip-space elements="*"/>
	<xsl:template match="//td">
		<xsl:copy>
			<xsl:attribute name="trans"><xsl:text>formed</xsl:text></xsl:attribute>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
