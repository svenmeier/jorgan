<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="stop" mode="bank">
		<xsl:if test="patch">
			<bank>
				<xsl:value-of select="patch/bank"/>
			</bank>
		</xsl:if>
	</xsl:template>

	<xsl:template match="stop">
		<stop>
			<xsl:apply-templates select="@*|*"/>
			<xsl:if test="patch">
				<program>
					<xsl:value-of select="patch/program"/>
				</program>
			</xsl:if>
		</stop>
	</xsl:template>

	<xsl:template match="soundSource">
		<soundSource>
			<xsl:apply-templates select="@*|*"/>
			
			<xsl:variable name="id" select="@id"/>
			<xsl:apply-templates select="//stop[reference/@id = $id]" mode="bank" />
		</soundSource>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>	
</xsl:stylesheet>