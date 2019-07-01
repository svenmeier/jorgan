<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="fluidsynth.fluidsynthSound">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		    <audioBuffers>8</audioBuffers>
		    <audioBufferSize>512</audioBufferSize>
		</xsl:copy>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>	
 </xsl:stylesheet>