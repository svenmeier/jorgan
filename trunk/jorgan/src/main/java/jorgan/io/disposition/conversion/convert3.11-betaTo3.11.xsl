<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="filter-intercept">
		<xsl:choose>
			<xsl:when test="../../active">
				<switchFilter-intercept>
					<xsl:apply-templates select="@*|node()"/>
				</switchFilter-intercept>
			</xsl:when>
			<xsl:otherwise>
				<continuousFilter-intercept>
					<xsl:apply-templates select="@*|node()"/>
				</continuousFilter-intercept>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
 </xsl:stylesheet>