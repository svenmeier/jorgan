<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="messages">
		<xsl:copy>
			<xsl:for-each select="*">
				<xsl:copy><xsl:value-of select="status" />, <xsl:value-of select="data1" />, <xsl:value-of select="data2" /></xsl:copy>
			</xsl:for-each>
		</xsl:copy>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>		
	
 </xsl:stylesheet>