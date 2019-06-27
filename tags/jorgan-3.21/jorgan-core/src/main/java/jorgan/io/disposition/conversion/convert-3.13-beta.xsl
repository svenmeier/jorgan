<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="console">
		<console>
			<xsl:apply-templates select="@*|name|description|skin|screen|style|zoom|references"/>
			<messages></messages>
		</console>
		<connector>
			<xsl:attribute name="id"><xsl:value-of select="translate(generate-id(.), translate(generate-id(.), '0123456789', ''), '')"/></xsl:attribute>						
			<xsl:apply-templates select="name|description|input|output"/>
			<references>
				<xsl:for-each select="references/console-locationReference">
					<reference>
						<xsl:apply-templates select="@*"/>
					</reference>
				</xsl:for-each>
			</references>
			<messages></messages>
		</connector>
	</xsl:template>

	<xsl:template match="sams.samsConsole">
		<console>
			<xsl:apply-templates select="@*|name|description|skin|screen|style|zoom|references"/>
			<messages></messages>
		</console>
		<sams.sams>
			<xsl:attribute name="id"><xsl:value-of select="translate(generate-id(.), translate(generate-id(.), '0123456789', ''), '')"/></xsl:attribute>						
			<xsl:apply-templates select="name|description|input|output|duration"/>
			<references>
				<xsl:for-each select="references/console-locationReference">
					<reference>
						<xsl:apply-templates select="@*"/>
					</reference>
				</xsl:for-each>
			</references>
			<messages>
				<xsl:for-each select="messages/sams.samsConsole-tabTurningOn">
					<sams.sams-tabTurningOn>
						<xsl:apply-templates select="node()"/>
					</sams.sams-tabTurningOn>
				</xsl:for-each>
				<xsl:for-each select="messages/sams.samsConsole-cancelTabOn">
					<sams.sams-cancelTabOn>
						<xsl:apply-templates select="node()"/>
					</sams.sams-cancelTabOn>
				</xsl:for-each>
				<xsl:for-each select="messages/sams.samsConsole-tabTurningOff">
					<sams.sams-tabTurningOff>
						<xsl:apply-templates select="node()"/>
					</sams.sams-tabTurningOff>
				</xsl:for-each>
				<xsl:for-each select="messages/sams.samsConsole-cancelTabOff">
					<sams.sams-cancelTabOff>
						<xsl:apply-templates select="node()"/>
					</sams.sams-cancelTabOff>
				</xsl:for-each>
				<xsl:for-each select="messages/sams.samsConsole-tabTurnedOn">
					<sams.sams-tabTurnedOn>
						<xsl:apply-templates select="node()"/>
					</sams.sams-tabTurnedOn>
				</xsl:for-each>
				<xsl:for-each select="messages/sams.samsConsole-tabTurnedOff">
					<sams.sams-tabTurningOff>
						<xsl:apply-templates select="node()"/>
					</sams.sams-tabTurningOff>
				</xsl:for-each>
			</messages>
		</sams.sams>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
 </xsl:stylesheet>