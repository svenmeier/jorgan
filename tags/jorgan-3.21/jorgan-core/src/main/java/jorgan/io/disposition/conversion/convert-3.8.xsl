<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="reference">
		<xsl:copy>
			<xsl:attribute name="id">
				<xsl:value-of select="translate(element/@ref, '-', '0')"/>
			</xsl:attribute>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="console-locationReference">
		<xsl:copy>
			<xsl:attribute name="id">
				<xsl:value-of select="translate(element/@ref, '-', '0')"/>
			</xsl:attribute>

			<xsl:apply-templates select="x|y"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="combination-switchReference">
		<xsl:copy>
			<xsl:attribute name="id">
				<xsl:value-of select="translate(element/@ref, '-', '0')"/>
			</xsl:attribute>

			<active>
				<xsl:choose>
					<xsl:when test="starts-with(actives, '0')">false</xsl:when>
					<xsl:otherwise>true</xsl:otherwise>
				</xsl:choose>
			</active>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="combination-continuousReference">
		<xsl:copy>
			<xsl:attribute name="id">
				<xsl:value-of select="translate(element/@ref, '-', '0')"/>
			</xsl:attribute>

			<value>
				<xsl:choose>
					<xsl:when test="contains(values, ',')"><xsl:value-of select="substring-before(values, ',')"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="values"/></xsl:otherwise>
				</xsl:choose>
			</value>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="memory">
		<memory.memory>
			<xsl:apply-templates select="@id|name|description|references|messages|style|zoom|locking|threshold|value"/>
			<size>
				<xsl:value-of select="count(titles/string)"/>
			</size>
		</memory.memory>
	</xsl:template>

	<xsl:template match="@id">
		<xsl:attribute name="id">
			<xsl:value-of select="translate(., '-', '0')"/>
		</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="rank">
		<rank>
			<xsl:apply-templates select="@*|*[not(self::messages)]"/>
			<messages>
				<xsl:for-each select="messages/rank-disengaged">
					<xsl:if test="status = 'set 176' and data1 = 'set 121'">
						<rank-engaged>
							<status>set 176</status>
							<data1>set 121</data1>
							<data2></data2>
						</rank-engaged>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="messages/*">
					<xsl:if test="not(status = 'set 176') or not(data1 = 'set 121') or not(name() = 'rank-disengaged')">
						<xsl:copy>
							<xsl:apply-templates select="node()"/>
						</xsl:copy>
					</xsl:if>
				</xsl:for-each>
			</messages>
		</rank>
	</xsl:template>

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>		
	
 </xsl:stylesheet>