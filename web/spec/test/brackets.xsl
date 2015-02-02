<?xml version="1.0"  encoding="UTF-8"?>
<!--
	enclose all elements in <b>...</b>
	@(#) $Id$
    2012-02-16, Dr. Georg Fischer: copied from identity.xsl, for test of &mode="trans"
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
	<xsl:strip-space elements="*"/>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:element name="b">
				<xsl:apply-templates select="@*|node()" />
			</xsl:element>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
