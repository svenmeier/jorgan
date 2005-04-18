<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="UTF-8" omit-xml-declaration="yes" indent="no"/>
	<xsl:param name="class"    select="'keyboard'"/>
	<xsl:param name="property" select="'channel'"/>

    <xsl:template match="/">
        <xsl:apply-templates select="instruction/class[@name=$class]"/>
    </xsl:template>

	<xsl:template match="class">
		<xsl:value-of select="displayName"/>
	</xsl:template>

</xsl:stylesheet>