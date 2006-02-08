<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="stop|coupler">
		<xsl:copy>
			<xsl:apply-templates select="@*|*"/>
            <xsl:choose>
    			<xsl:when test="action"/>
				<xsl:otherwise>
                    <action>
                        <xsl:choose>
                            <xsl:when test="inverse">6</xsl:when>
			    			<xsl:when test="pitch">
                                <xsl:value-of select="pitch"/>
			    			</xsl:when>
                            <xsl:otherwise>0</xsl:otherwise>
                        </xsl:choose>
                    </action>
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