<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="combination/reference">
        <reference>
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="(active = 'true') or (active = '')">
					10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
				</xsl:when>
				<xsl:otherwise>
					00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
				</xsl:otherwise>
			</xsl:choose>
        </reference>
	</xsl:template>

	<xsl:template match="crescendo">
        <activation>
			<xsl:apply-templates select="@*|*"/>
        </activation>
	</xsl:template>

	<xsl:template match="recallMessage">
        <message>
			<xsl:apply-templates select="@*|*"/>
        </message>
	</xsl:template>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>		
</xsl:stylesheet>