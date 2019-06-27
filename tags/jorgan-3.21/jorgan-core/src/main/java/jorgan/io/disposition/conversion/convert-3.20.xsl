<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="rank">
		<xsl:copy>
			<xsl:apply-templates select="@*|*[not(name()='channel')]"/>

			<xsl:choose>
				<xsl:when test="channel != ''">
					<channel><xsl:value-of select="channel"></xsl:value-of></channel>
				</xsl:when>
				<xsl:otherwise>
					<channel>greaterEqual 0</channel>
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