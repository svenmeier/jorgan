<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="UTF-8" omit-xml-declaration="no" indent="yes"/>
	<xsl:param name="class"                 select="'stop'"          />
	<xsl:param name="referencesHeader"      select="'References'"    />
	<xsl:param name="sampleOrPatternHeader" select="'Sample/Pattern'"/>
	<xsl:param name="descriptionHeader"     select="'Description'"   />

    <xsl:template match="/">
        <xsl:apply-templates select="instruction/class[@name=$class]"/>
    </xsl:template>
    
	<xsl:template match="class">
		<html>
			<head>
				<title><xsl:value-of select="displayName"/></title>
			</head>
			<body>
				<p>
					<xsl:value-of select="description"/>
				</p>
				
				<p>
					<xsl:apply-templates select="property"/>
				</p>
		
				<xsl:apply-templates select="references"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="property">
		<dl>
			<dt><b><xsl:value-of select="displayName"/></b></dt>
			<dd>
				<p>
					<xsl:value-of select="description"/>
				</p>
				<xsl:if test="value">
					<table border="1">
						<tr>
							<th><xsl:value-of select="$sampleOrPatternHeader"/></th>
							<th><xsl:value-of select="$descriptionHeader"/></th>
						</tr>
						<xsl:apply-templates select="value"/>
					</table>
				</xsl:if>
			</dd>	
		</dl>
	</xsl:template>
	
	<xsl:template match="value">
		<tr>	
			<td>
				<xsl:choose>
					<xsl:when test="pattern">
						<i>&lt;<xsl:value-of select="pattern"/>&gt;</i>
					</xsl:when>
					<xsl:when test="sample">
						<b><xsl:value-of select="sample"/>&#160;</b>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:value-of select="description"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="references">
		<h3><xsl:value-of select="$referencesHeader"/></h3>
		<p>
			<xsl:value-of select="."/>
		</p>
	</xsl:template>
	
</xsl:stylesheet>