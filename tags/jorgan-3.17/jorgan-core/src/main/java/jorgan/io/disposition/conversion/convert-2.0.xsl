<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="soundSource/reference|swell/reference|tremulant/reference|variation/reference">
	</xsl:template>

	<xsl:template match="soundSource|swell|tremulant|variation">
		<xsl:copy>
			<xsl:choose>
				<xsl:when test="@id">
					<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>						
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>						
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="*"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="stop">
		<stop>
			<xsl:apply-templates select="@*|*"/>
			<xsl:variable name="id" select="@id"/>
			<xsl:for-each select="//soundSource[reference/@id = $id]|//swell[reference/@id = $id]|//tremulant[reference/@id = $id]|//variation[reference/@id = $id]">
				<reference>
					<xsl:choose>
						<xsl:when test="@id">
							<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>						
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>						
						</xsl:otherwise>
					</xsl:choose>
				</reference>
			</xsl:for-each>
		</stop>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>	
</xsl:stylesheet>