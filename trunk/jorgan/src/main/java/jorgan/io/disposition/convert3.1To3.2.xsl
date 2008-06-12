<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="initiator-initiate">
		<switch-activate>
			<xsl:apply-templates select="@*|node()"/>
		</switch-activate>
	</xsl:template>

	<xsl:template match="initiator-initiated">
		<switch-activated>
			<xsl:apply-templates select="@*|node()"/>
		</switch-activated>
	</xsl:template>

	<xsl:template match="sequence">
		<regulator>
			<xsl:apply-templates select="@*|node()"/>
		</regulator>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>	
 </xsl:stylesheet>