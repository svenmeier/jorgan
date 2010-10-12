<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="label|keyboard|soundSource|stop|coupler|combination|captor|swell|tremulant|variation|sequence|activator|regulator|keyer|incrementer|memory">
		<xsl:copy>
			<xsl:apply-templates select="@*|*[not(name() = 'zoom')]"/>
			<xsl:choose>
	            <xsl:when test="zoom">
	                <zoom><xsl:value-of select="zoom"/></zoom>
	            </xsl:when>
	            <xsl:otherwise>
			        <zoom>1.0</zoom>
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