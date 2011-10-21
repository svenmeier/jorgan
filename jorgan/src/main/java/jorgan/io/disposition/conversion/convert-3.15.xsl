<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="fluidsynth.fluidsynthSound">
		<xsl:copy>
			<xsl:apply-templates select="@*|*"/>

			<xsl:if test="not(overflowPercussion)">
				<overflowPercussion>0.10</overflowPercussion>
				<overflowSustained>0.10</overflowSustained>
				<overflowReleased>0.50</overflowReleased> 
				<overflowAge>0.55</overflowAge> 
				<overflowVolume>0.50</overflowVolume> 
			</xsl:if>
		</xsl:copy>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
 </xsl:stylesheet>