<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

	<xsl:template match="organ">
		<organ>
			<console>
				<xsl:if test="name">
					<name><xsl:value-of select="name"/></name>
				</xsl:if>
				<xsl:if test="device">
					<device><xsl:value-of select="device"/></device>
				</xsl:if>
				<xsl:if test="style">
					<style><xsl:value-of select="style"/></style>
				</xsl:if>
				<xsl:if test="skin">
					<skin><xsl:value-of select="skin"/></skin>
				</xsl:if>
				<xsl:for-each 
					select="division|division/stop|division/coupler|division/piston|division/tremulant|division/swell|division/variation|piston">
					<xsl:call-template name="consoleReference"/>
				</xsl:for-each>
				<xsl:for-each 
					select="division/drawbar">
					<xsl:call-template name="consoleDrawbarReference"/>
				</xsl:for-each>
			</console>
			<xsl:apply-templates select="division|piston"/>
		</organ>
	</xsl:template>
	
	<xsl:template match="division">
		<keyboard>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
			<xsl:if test="style">
  				<style><xsl:value-of select="style"/></style>
			</xsl:if>  			
			<xsl:if test="channel">
				<channel><xsl:value-of select="channel"/></channel>
			</xsl:if>  			
			<xsl:if test="command">
				<command><xsl:value-of select="command"/></command>
			</xsl:if>  			
			<xsl:if test="threshold">
				<threshold><xsl:value-of select="threshold"/></threshold>
			</xsl:if>  			
			<xsl:if test="from">
				<from><xsl:value-of select="from"/></from>
			</xsl:if>  			
			<xsl:if test="to">
				<to><xsl:value-of select="to"/></to>
			</xsl:if>  			
			<xsl:if test="transpose">
				<transpose><xsl:value-of select="transpose"/></transpose>
			</xsl:if>
			<xsl:if test="../device">
				<device><xsl:value-of select="../device"/></device>
			</xsl:if>
			<xsl:variable name="id" select="@id"/>
			<xsl:if test="not(coupler[(transpose='0' or octave='0') and division/@ref=$id])">
				<xsl:for-each select="stop|drawbar">
					<xsl:call-template name="reference"/>
				</xsl:for-each>
			</xsl:if>
			<xsl:for-each select="coupler">
				<xsl:call-template name="reference"/>
			</xsl:for-each>
		</keyboard>
		<soundSource>
			<name><xsl:value-of select="name"/></name>
			<xsl:if test="device">
				<device><xsl:value-of select="device"/></device>
			</xsl:if>
		  <xsl:if test="type">
  			<type><xsl:value-of select="type"/></type>
			</xsl:if>
		  <xsl:if test="delay">
  			<delay><xsl:value-of select="delay"/></delay>
			</xsl:if>
			<xsl:for-each select="stop|drawbar">
				<xsl:call-template name="reference"/>
			</xsl:for-each>
		</soundSource>
		<xsl:apply-templates select="stop|drawbar|coupler|piston|tremulant|swell|variation"/>
	</xsl:template>
	<xsl:template match="stop">
		<stop>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
			<xsl:if test="velocity">
  			<velocity><xsl:value-of select="velocity"/></velocity>
			</xsl:if>
			<xsl:if test="volume">
			<volume><xsl:value-of select="volume"/></volume>
			</xsl:if>
			<xsl:if test="pan">
  			<pan><xsl:value-of select="pan"/></pan>
			</xsl:if>
			<xsl:if test="transpose">
  			<transpose><xsl:value-of select="transpose"/></transpose>
			</xsl:if>			
			<xsl:if test="shortcut">
				<shortcut><xsl:value-of select="shortcut"/></shortcut>
			</xsl:if>
			<xsl:if test="style">
				<style><xsl:value-of select="style"/></style>
			</xsl:if>
			<xsl:apply-templates select="onMessage|offMessage|patch" />
		</stop>
	</xsl:template>

	<xsl:template match="drawbar">
		<swell>
			<xsl:attribute name="id"><xsl:value-of select="concat('drawbar', generate-id())"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
			<xsl:apply-templates select="message" />
			<volume>0</volume>
			<xsl:if test="shortcut">
				<shortcut><xsl:value-of select="shortcut"/></shortcut>
			</xsl:if>
			<xsl:if test="style">
				<style><xsl:value-of select="style"/></style>
			</xsl:if>
			<reference>
				<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			</reference>
		</swell>
		<stop>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
			<xsl:if test="velocity">
  			<velocity><xsl:value-of select="velocity"/></velocity>
			</xsl:if>
			<xsl:if test="volume">
			<volume><xsl:value-of select="volume"/></volume>
			</xsl:if>
			<xsl:if test="pan">
  			<pan><xsl:value-of select="pan"/></pan>
			</xsl:if>
			<xsl:if test="transpose">
  			<transpose><xsl:value-of select="transpose"/></transpose>
			</xsl:if>			
			<xsl:apply-templates select="patch" />
			<on/>
		</stop>
	</xsl:template>

	<xsl:template match="coupler">
		<coupler>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
			<xsl:if test="transpose">
	  			<transpose><xsl:value-of select="transpose"/></transpose>
			</xsl:if>
			<xsl:if test="octave">
	  			<transpose><xsl:value-of select="octave * 12"/></transpose>
			</xsl:if>
			<xsl:if test="style">
				<style><xsl:value-of select="style"/></style>
			</xsl:if>
			<xsl:if test="shortcut">
				<shortcut><xsl:value-of select="shortcut"/></shortcut>
			</xsl:if>
			<xsl:apply-templates select="onMessage|offMessage" />
			<xsl:if test="division/@ref = ../@id and (octave = 0 or transpose = 0)">
				<inverse/>
			</xsl:if>
			<xsl:for-each select="division">
				<xsl:call-template name="couplerReference"/>
			</xsl:for-each>
		</coupler>
	</xsl:template>
	
	<xsl:template match="piston">
		<piston>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
			<xsl:if test="not(setable)">
				<fixed/>
			</xsl:if>
			<xsl:if test="style">
				<style><xsl:value-of select="style"/></style>
			</xsl:if>
			<xsl:if test="shortcut">
				<shortcut><xsl:value-of select="shortcut"/></shortcut>
			</xsl:if>
			<xsl:apply-templates select="getMessage|setMessage" />
			<xsl:variable name="piston" select="."/>
			<xsl:for-each select="..//stop|..//coupler|..//tremulant|..//variation">
				<xsl:call-template name="pistonReference">
					<xsl:with-param name="piston" select="$piston"/>
				</xsl:call-template>
			</xsl:for-each>
		</piston>
	</xsl:template>
	
	<xsl:template match="swell">
		<swell>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
		  <xsl:if test="volume">
  			<volume><xsl:value-of select="volume"/></volume>
			</xsl:if>
		  <xsl:if test="cutoff">
  			<cutoff><xsl:value-of select="cutoff"/></cutoff>
			</xsl:if>
		  <xsl:if test="position">
  			<position><xsl:value-of select="position"/></position>
			</xsl:if>
			<xsl:if test="style">
				<style><xsl:value-of select="style"/></style>
			</xsl:if>
			<xsl:if test="shortcut">
				<shortcut><xsl:value-of select="shortcut"/></shortcut>
			</xsl:if>
			<xsl:apply-templates select="message"/>
			<xsl:for-each select="../stop|../drawbar">
				<xsl:call-template name="reference"/>
			</xsl:for-each>
		</swell>
	</xsl:template>

	<xsl:template match="tremulant">
		<tremulant>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
		  <xsl:if test="frequency">
    		<frequency><xsl:value-of select="frequency"/></frequency>
			</xsl:if>
		  <xsl:if test="amplitude">
  			<amplitude><xsl:value-of select="amplitude"/></amplitude>
			</xsl:if>
			<xsl:if test="style">
				<style><xsl:value-of select="style"/></style>
			</xsl:if>
			<xsl:if test="shortcut">
				<shortcut><xsl:value-of select="shortcut"/></shortcut>
			</xsl:if>
			<xsl:apply-templates select="onMessage|offMessage" />
			<xsl:for-each select="../stop">
				<xsl:call-template name="reference"/>
			</xsl:for-each>
		</tremulant>
	</xsl:template>

	<xsl:template match="variation">
		<variation>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<name><xsl:value-of select="name"/></name>
			<xsl:if test="bank">
  			<bank><xsl:value-of select="bank"/></bank>
			</xsl:if>
			<xsl:if test="program">
        <program><xsl:value-of select="program"/></program>
			</xsl:if>
			<xsl:if test="style">
				<style><xsl:value-of select="style"/></style>
			</xsl:if>
			<xsl:if test="shortcut">
				<shortcut><xsl:value-of select="shortcut"/></shortcut>
			</xsl:if>
			<xsl:apply-templates select="onMessage|offMessage" />
			<xsl:for-each select="../stop">
				<xsl:call-template name="reference"/>
			</xsl:for-each>
		</variation>
	</xsl:template>
	
	<xsl:template match="message">
		<message>
		  <xsl:if test="status">
  			<status><xsl:value-of select="status"/></status>
		  </xsl:if>
		  <xsl:if test="data1">
  			<data1><xsl:value-of select="data1"/></data1>
		  </xsl:if>
		  <xsl:if test="data2">
  			<data2><xsl:value-of select="data2"/></data2>
		  </xsl:if>
		</message>
	</xsl:template>
	<xsl:template match="onMessage">
		<onMessage>
		  <xsl:if test="status">
  			<status><xsl:value-of select="status"/></status>
		  </xsl:if>
		  <xsl:if test="data1">
  			<data1><xsl:value-of select="data1"/></data1>
		  </xsl:if>
		  <xsl:if test="data2">
  			<data2><xsl:value-of select="data2"/></data2>
		  </xsl:if>
		</onMessage>
	</xsl:template>
	<xsl:template match="offMessage">
		<offMessage>
		  <xsl:if test="status">
  			<status><xsl:value-of select="status"/></status>
		  </xsl:if>
		  <xsl:if test="data1">
  			<data1><xsl:value-of select="data1"/></data1>
		  </xsl:if>
		  <xsl:if test="data2">
  			<data2><xsl:value-of select="data2"/></data2>
		  </xsl:if>
		</offMessage>
	</xsl:template>
	<xsl:template match="patch">
		<patch>
		  <xsl:if test="bank">
    		<bank><xsl:value-of select="bank"/></bank>
			</xsl:if>
		  <xsl:if test="program">
  			<program><xsl:value-of select="program"/></program>
			</xsl:if>
		</patch>
	</xsl:template>
	
	<xsl:template name="reference">
		<reference>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
		</reference>
	</xsl:template>
	<xsl:template name="consoleReference">
		<xsl:if test="location">
			<reference>
				<xsl:choose>
					<xsl:when test=". = drawbar">
						<xsl:attribute name="id"><xsl:value-of select="concat('drawbar', generate-id())"/></xsl:attribute>						
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>						
					</xsl:otherwise>
				</xsl:choose>
				<x><xsl:value-of select="location/x"/></x>				
				<y><xsl:value-of select="location/y"/></y>				
			</reference>
		</xsl:if>
	</xsl:template>
	<xsl:template name="consoleDrawbarReference">
		<xsl:if test="location">
			<reference>
				<xsl:attribute name="id"><xsl:value-of select="concat('drawbar', generate-id())"/></xsl:attribute>						
				<x><xsl:value-of select="location/x"/></x>				
				<y><xsl:value-of select="location/y"/></y>				
			</reference>
		</xsl:if>
	</xsl:template>
	<xsl:template name="couplerReference">
		<xsl:variable name="ref" select="@ref"/>
    <xsl:for-each select="//stop[../@id=$ref]">
			<xsl:call-template name="reference"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="pistonReference">		
		<xsl:param name="piston"/>
		<reference>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<xsl:variable name="ref" select="@id"/>
			<xsl:if test="$piston/registratable/@ref = $ref">
				<on/>
			</xsl:if>
		</reference>
	</xsl:template>
</xsl:stylesheet>

