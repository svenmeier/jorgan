<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="keyboard">
		<keyboard>
			<xsl:apply-templates select="@*|description|messages|name|references|style|zoom"/>
		</keyboard>

		<xsl:call-template name="input"/>
	</xsl:template>

	<xsl:template match="console">
		<console>
			<xsl:apply-templates select="@*|description|messages|name|screen|skin|style|zoom"/>
			<references>
				<xsl:apply-templates select="references/*"/>
				<xsl:if test="output">
					<reference>
						<xsl:attribute name="id"><xsl:value-of select="output"/></xsl:attribute>
					</reference>
				</xsl:if>
			</references>
		</console>

		<xsl:call-template name="input"/>
		<xsl:call-template name="output"/>
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
		
		<xsl:call-template name="output"/>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template name="input">
		<xsl:if test="input">
			<xsl:variable name="input" select="input"/>
			<xsl:variable name="remainder" select="following::*[input=$input]"/>
			<xsl:if test="not($remainder)">
				<midiInput>
					<xsl:attribute name="id"><xsl:value-of select="input"/></xsl:attribute>
					<description></description>
					<device><xsl:value-of select="input"/></device>
					<messages/>
					<name><xsl:value-of select="input"/></name>
					<references>
						<xsl:for-each select="//*[input=$input]">
							<reference>
								<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
							</reference>
						</xsl:for-each>
					</references>
					<zoom>1.0</zoom>
				</midiInput>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="output">
		<xsl:if test="output">
			<xsl:variable name="output" select="output"/>
			<xsl:variable name="remainder" select="following::*[output=$output]"/>
			<xsl:if test="not($remainder)">
				<midiOutput>
					<xsl:attribute name="id"><xsl:value-of select="output"/></xsl:attribute>
					<description></description>
					<device><xsl:value-of select="output"/></device>
					<messages/>
					<name><xsl:value-of select="output"/></name>
					<references/>
					<zoom>1.0</zoom>
				</midiOutput>
			</xsl:if>
		</xsl:if>
	</xsl:template>
 </xsl:stylesheet>