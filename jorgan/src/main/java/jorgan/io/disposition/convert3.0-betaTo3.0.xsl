<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="activateableFilter">
		<switchFilter>
			<xsl:apply-templates select="@*|node()"/>
		</switchFilter>
	</xsl:template>

	<xsl:template match="activateable-activate">
		<switch-activate>
			<xsl:apply-templates select="@*|node()"/>
		</switch-activate>
	</xsl:template>

	<xsl:template match="activateable-deactivate">
		<switch-deactivate>
			<xsl:apply-templates select="@*|node()"/>
		</switch-deactivate>
	</xsl:template>
	
	<xsl:template match="activateable-toggle">
		<switch-toggle>
			<xsl:apply-templates select="@*|node()"/>
		</switch-toggle>
	</xsl:template>
	
	<xsl:template match="activateable-activated">
		<switch-activated>
			<xsl:apply-templates select="@*|node()"/>
		</switch-activated>
	</xsl:template>
	
	<xsl:template match="activateable-deactivated">
		<switch-deactivated>
			<xsl:apply-templates select="@*|node()"/>
		</switch-deactivated>
	</xsl:template>
	
	<xsl:template match="activateableFilter-engaged">
		<switchFilter-engaged>
			<xsl:apply-templates select="@*|node()"/>
		</switchFilter-engaged>
	</xsl:template>

	<xsl:template match="activateableFilter-disengaged">
		<switchFilter-disengaged>
			<xsl:apply-templates select="@*|node()"/>
		</switchFilter-disengaged>
	</xsl:template>

	<xsl:template match="status|data1|data2">
		<xsl:copy>
			<xsl:call-template name="replace-string">
		        <xsl:with-param name="text" select="."/>
		        <xsl:with-param name="from" select="'lower'"/>
		        <xsl:with-param name="to" select="'less'"/>
			</xsl:call-template>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="coupler">
		<coupler>
			<xsl:apply-templates select="@id|action|active|locking|name|shortcut|transpose|velocity|zoom|references|description|messages"/>
			<style>
				<xsl:choose>
					<xsl:when test="style = 'coupler' and action = '6'">couplerInverse</xsl:when>
					<xsl:when test="style = 'black' and action = '6'">blackInverse</xsl:when>
					<xsl:otherwise><xsl:value-of select="style"/></xsl:otherwise>
				</xsl:choose>
			</style>
		</coupler>
	</xsl:template>

	<xsl:template match="action">
		<action>
			<xsl:choose>
				<xsl:when test=". = '6'">0</xsl:when>
				<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
			</xsl:choose>
		</action>
	</xsl:template>
	
  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template name="replace-string">
	    <xsl:param name="text"/>
	    <xsl:param name="from"/>
	    <xsl:param name="to"/>
	
	    <xsl:choose>
	      <xsl:when test="contains($text, $from)">
	
		<xsl:variable name="before" select="substring-before($text, $from)"/>
		<xsl:variable name="after" select="substring-after($text, $from)"/>
		<xsl:variable name="prefix" select="concat($before, $to)"/>
	
		<xsl:value-of select="$before"/>
		<xsl:value-of select="$to"/>
	        <xsl:call-template name="replace-string">
		  <xsl:with-param name="text" select="$after"/>
		  <xsl:with-param name="from" select="$from"/>
		  <xsl:with-param name="to" select="$to"/>
		</xsl:call-template>
	      </xsl:when> 
	      <xsl:otherwise>
	        <xsl:value-of select="$text"/>  
	      </xsl:otherwise>
	    </xsl:choose>            
	 </xsl:template>
 </xsl:stylesheet>