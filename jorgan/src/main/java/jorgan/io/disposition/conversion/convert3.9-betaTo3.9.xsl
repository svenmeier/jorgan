<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="whenActivated">
		<xsl:copy>
			<xsl:choose>
				<xsl:when test="node() = 'true'">ACTIVATE</xsl:when>
				<xsl:when test="node() = 'false'">IGNORE</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="node()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="whenDeactivated">
		<xsl:copy>
			<xsl:choose>
				<xsl:when test="node() = 'true'">ACTIVATE</xsl:when>
				<xsl:when test="node() = 'false'">IGNORE</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="node()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>		
	
 </xsl:stylesheet>