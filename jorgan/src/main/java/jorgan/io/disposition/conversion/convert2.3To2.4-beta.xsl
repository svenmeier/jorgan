<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="organ">
		<organ>
			<xsl:apply-templates select="@*"/>
	        <elements>
				<xsl:apply-templates select="*"/>
	        </elements>
		</organ>
	</xsl:template>

	<xsl:template match="console|label|keyboard|soundSource|stop|coupler|combination|captor|swell|tremulant|variation|sequence|activator|regulator|keyer|incrementer">
		<xsl:copy>
			<xsl:apply-templates select="@*|*[not(name() = 'reference')]"/>
	        <references>
				<xsl:apply-templates select="reference"/>
	        </references>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="reference">
		<xsl:choose>
			<xsl:when test="ancestor::console">
				<consoleReference>
					<xsl:apply-templates select="@*|*"/>
				</consoleReference>
			</xsl:when>
			<xsl:when test="ancestor::combination">
				<combinationReference>
					<xsl:apply-templates select="@*"/>
					<activated>
						<xsl:value-of select="node()"/>
					</activated>
				</combinationReference>
			</xsl:when>
			<xsl:otherwise>
				<reference>
					<xsl:apply-templates select="@*|*"/>
				</reference>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="memory">
		<xsl:copy>
			<xsl:apply-templates select="@*|*[not(name() = 'title' or name() = 'reference')]"/>
	        <titles>
	        	<xsl:for-each select="title">
	        		<string>
						<xsl:value-of select="node()"/>
	        		</string>
	        	</xsl:for-each>
	        </titles>
	        <references>
				<xsl:apply-templates select="reference"/>
	        </references>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>	
</xsl:stylesheet>