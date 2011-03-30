<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="fluidsynth.fluidsynthSound">
		<xsl:copy>
			<xsl:apply-templates select="@*|*[not(name() = 'reverb' or name() = 'chorus')]"/>
			<xsl:if test="not(interpolate)">
				<interpolate>ORDER_4TH</interpolate>
			</xsl:if>
			<xsl:if test="not(cores)">
				<cores>1</cores>
			</xsl:if>
			<xsl:if test="not(polyphony)">
				<polyphony>256</polyphony>
			</xsl:if>
		</xsl:copy>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
 </xsl:stylesheet>