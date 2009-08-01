<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="combination-switchReference">
		<xsl:copy>
			<xsl:apply-templates select="element"/>

			<active>
				<xsl:choose>
					<xsl:when test="starts-with(actives, '0')">false</xsl:when>
					<xsl:otherwise>true</xsl:otherwise>
				</xsl:choose>
			</active>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="combination-continuousReference">
		<xsl:copy>
			<xsl:apply-templates select="element"/>

			<value>
				<xsl:choose>
					<xsl:when test="contains(values, ',')"><xsl:value-of select="substring-before(values, ',')"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="values"/></xsl:otherwise>
				</xsl:choose>
			</value>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="memory">
		<memory.MemorySwitch>
			<xsl:apply-templates select="references"/>
		</memory.MemorySwitch>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>		
	
 </xsl:stylesheet>