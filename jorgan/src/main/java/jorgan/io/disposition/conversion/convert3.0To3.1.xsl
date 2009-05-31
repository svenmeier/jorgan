<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="coupler">
		<coupler>
			<xsl:apply-templates select="@*|action|active|description|locking|messages|name|references|shortcut|style|transpose|zoom"/>
		</coupler>
	</xsl:template>

	<xsl:template match="stop">
		<stop>
			<xsl:apply-templates select="@*|action|active|description|locking|messages|name|references|shortcut|style|transpose|zoom"/>
		</stop>
	</xsl:template>

	<xsl:template match="rank">
		<rank>
			<xsl:apply-templates select="@*|delay|description|messages|name|style|zoom"/>
			<channel><xsl:value-of select="channels"/></channel>
			<references>
				<xsl:apply-templates select="references/*"/>
				<xsl:if test="output">
					<reference>
						<xsl:attribute name="id"><xsl:value-of select="output"/></xsl:attribute>
					</reference>
				</xsl:if>
			</references>
		</rank>		
		
		<xsl:call-template name="genericSound"/>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template name="genericSound">
		<xsl:if test="output">
			<xsl:variable name="output" select="output"/>
			<xsl:variable name="remainder" select="following::rank[output=$output]"/>
			<xsl:if test="not($remainder)">
				<genericSound>
					<xsl:attribute name="id"><xsl:value-of select="output"/></xsl:attribute>
					<description></description>
					<output><xsl:value-of select="output"/></output>
					<messages/>
					<name><xsl:value-of select="output"/></name>
					<references/>
					<zoom>1.0</zoom>
				</genericSound>
			</xsl:if>
		</xsl:if>
	</xsl:template>
 </xsl:stylesheet>