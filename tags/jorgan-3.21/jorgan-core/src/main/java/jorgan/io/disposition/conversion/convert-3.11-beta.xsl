<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="sysex.sysexSound">
		<genericSound>
			<xsl:apply-templates select="@*|output"/>
		</genericSound>
	</xsl:template>

	<xsl:template match="sysex.sysexConsole">
		<console>
			<xsl:apply-templates select="@*|skin|screen|input|output|style|zoom"/>
		</console>
	</xsl:template>

	<xsl:template match="stop|coupler">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
			<xsl:if test="not(from)">
				<from>0</from>
				<to>127</to>
			</xsl:if>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="messages">
		<xsl:copy>
			<xsl:for-each select="*">
				<xsl:choose>
					<xsl:when test="status">
						<xsl:copy><xsl:value-of select="status" />, <xsl:value-of select="data1" />, <xsl:value-of select="data2" /></xsl:copy>
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy><xsl:value-of select="." /></xsl:copy>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:copy>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>		
	
 </xsl:stylesheet>