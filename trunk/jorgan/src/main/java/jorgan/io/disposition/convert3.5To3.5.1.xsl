<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="combination-reference">
		<combination-switchReference>
			<xsl:apply-templates select="@*"/>
			<actives>
				<xsl:variable name="id" select="@id"/>
				<xsl:choose>
					<xsl:when test="//coupler[@id = $id and substring(style, (string-length(style) - string-length('Inverse')) + 1) = 'Inverse']">
						<xsl:call-template name="split">
							<xsl:with-param name="array" select="translate(activated, '01', '10')" />
						</xsl:call-template>			
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="split">
							<xsl:with-param name="array" select="activated" />
						</xsl:call-template>			
					</xsl:otherwise>
				</xsl:choose>				
			</actives>
		</combination-switchReference>
	</xsl:template>

	<xsl:template match="console-reference">
		<console-locationReference>
			<xsl:apply-templates select="@*|node()"/>
		</console-locationReference>
	</xsl:template>

	<xsl:template match="coupler">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()[not(name() = 'style' or name() = 'action')]"/>
			<xsl:choose>
				<xsl:when test="substring(style, (string-length(style) - string-length('Inverse')) + 1) = 'Inverse'">
					<style>
						<xsl:value-of select="substring(style, 1, string-length(style) - string-length('Inverse'))"/>
					</style>
					<action>6</action>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="style">
						<style><xsl:value-of select="style"/></style>
					</xsl:if>
					<action><xsl:value-of select="action"/></action>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
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