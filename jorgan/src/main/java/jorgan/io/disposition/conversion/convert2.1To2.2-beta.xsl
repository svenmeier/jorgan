<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="piston">
        <combination>
			<xsl:apply-templates select="@*|*"/>
        </combination>
	</xsl:template>

	<xsl:template match="setMessage">
        <storeMessage>
			<xsl:apply-templates select="@*|*"/>
        </storeMessage>
	</xsl:template>

	<xsl:template match="getMessage">
        <recallMessage>
			<xsl:apply-templates select="@*|*"/>
        </recallMessage>
	</xsl:template>

	<xsl:template match="setWithGet">
        <captureWithRecall/>
	</xsl:template>

	<xsl:template match="on">
        <active/>
	</xsl:template>

	<xsl:template match="onMessage">
        <activateMessage>
            <xsl:if test="status">
                <status><xsl:value-of select="status"/></status>
            </xsl:if>
            <xsl:if test="data1">
                <data1><xsl:value-of select="data1"/></data1>
            </xsl:if>
            <xsl:if test="data2">
                <data2><xsl:value-of select="data2"/></data2>
            </xsl:if>
        </activateMessage>
	</xsl:template>
    
	<xsl:template match="offMessage">
        <deactivateMessage>
            <xsl:if test="status">
                <status><xsl:value-of select="status"/></status>
            </xsl:if>
            <xsl:if test="data1">
                <data1><xsl:value-of select="data1"/></data1>
            </xsl:if>
            <xsl:if test="data2">
                <data2><xsl:value-of select="data2"/></data2>
            </xsl:if>
        </deactivateMessage>
	</xsl:template>
    
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>	
</xsl:stylesheet>