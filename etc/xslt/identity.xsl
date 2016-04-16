<?xml version="1.0"  encoding="UTF-8"?>
<!--
	XML richtig einruecken
	@(#) $Id$
    2006-10-24, Dr. Georg Fischer:  aus XML Hacks # 38
    
    Aufruf: xalan -i 4 -o outputfile inputfile pretty.xsl
-->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
	<xsl:strip-space elements="*"/>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
