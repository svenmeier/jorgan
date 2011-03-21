<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="console">
		<console>
			<xsl:apply-templates select="@*|skin|screen|style|zoom|references"/>
		</console>
		<controller>
			<xsl:apply-templates select="@*|input|output"/>
			<references>
				<xsl:for-each select="console-locationReference">
					<reference>
						<xsl:apply-templates select="@*"/>
					</reference>
				</xsl:for-each>
			</references>
		</controller>
	</xsl:template>

	<xsl:template match="sams.samsConsole">
		<console>
			<xsl:apply-templates select="@*|skin|screen|style|zoom|references"/>
		</console>
		<sams.sams>
			<xsl:apply-templates select="@*|input|output|duration"/>
			<references>
				<xsl:for-each select="console-locationReference">
					<reference>
						<xsl:apply-templates select="@*"/>
					</reference>
				</xsl:for-each>
			</references>
		</sams.sams>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
 </xsl:stylesheet>