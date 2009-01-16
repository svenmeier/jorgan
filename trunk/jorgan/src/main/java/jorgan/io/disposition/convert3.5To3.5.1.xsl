<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="combination-reference">
		<combination-switchReference>
			<xsl:apply-templates select="@*"/>
			<actives>
				<xsl:call-template name="split">
					<xsl:with-param name="array" select="activated" />
				</xsl:call-template>			
			</actives>
		</combination-switchReference>
	</xsl:template>

	<xsl:template match="console-reference">
		<console-locationReference>
			<xsl:apply-templates select="@*|node()"/>
		</console-locationReference>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>	
	
	<xsl:template name="split">
		<xsl:param name="array" select="." />
		
		<xsl:choose>
		    <xsl:when test="not($array)" />
		    <xsl:otherwise>
				<xsl:variable name="byte" select="substring($array, 1, 1)" />
				
				<boolean>
					<xsl:if test="$byte = '0'">false</xsl:if>
					<xsl:if test="$byte = '1'">true</xsl:if>
				</boolean>
				
				<xsl:call-template name="split">
					<xsl:with-param name="array" select="substring($array, 2)" />
				</xsl:call-template>			
		    </xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
 </xsl:stylesheet>