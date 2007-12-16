<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

  	<xsl:template match="console">
	    <console>
			<xsl:apply-templates select="@id|name|style|zoom|skin"/>
			<xsl:if test="device">
				<input><xsl:value-of select="device"/></input>
			</xsl:if>
	      <references>
	      	<xsl:for-each select="references/consoleReference">
				<xsl:variable name="id" select="@id"/>
				<xsl:if test="not(//console[@id = $id] or //keyboard[@id = $id] or //soundSource[@id = $id])">
		      		<console-reference>
						<xsl:apply-templates select="@id|x|y"/>
		      		</console-reference>
	      		</xsl:if>
	      	</xsl:for-each>
	      </references>
	      <messages/>
	      <description><xsl:value-of select="description"/></description>
	    </console>
  	</xsl:template>

  	<xsl:template match="memory">
	    <memory>
			<xsl:apply-templates select="@id|name|style|zoom|locking|titles|references"/>
	      <threshold><xsl:value-of select="number(threshold) div 127"/></threshold>
	      <value><xsl:value-of select="number(value) div 127"/></value>
	      <messages>
	      	<xsl:for-each select="message">
	      		<continuous-change>
	      			<xsl:call-template name="message"/>
	      		</continuous-change>
	      	</xsl:for-each>
	      </messages>
	      <description><xsl:value-of select="description"/></description>
	    </memory>
  	</xsl:template>
  	
  	<xsl:template match="captor">
	    <captor>
			<xsl:apply-templates select="@id|active|locking|name|shortcut|style|zoom|references"/>
	      <description><xsl:value-of select="description"/></description>
	      <messages>
	      	<xsl:for-each select="activateMessage">
	      		<activateable-activate>
	      			<xsl:call-template name="message"/>
	      		</activateable-activate>
	      	</xsl:for-each>
	      	<xsl:for-each select="deactivateMessage">
	      		<activateable-deactivate>
	      			<xsl:call-template name="message"/>
	      		</activateable-deactivate>
	      	</xsl:for-each>
	      </messages>
	    </captor>
  	</xsl:template>  	

  	<xsl:template match="sequence">
	    <sequence>
			<xsl:apply-templates select="@id|locking|name|style|zoom|references"/>
	      <description><xsl:value-of select="description"/></description>
	      <value><xsl:value-of select="number(threshold) div 127"/></value>
	      <value><xsl:value-of select="number(value) div 127"/></value>
	      <messages>
	      	<xsl:for-each select="message">
	      		<continuous-change>
	      			<xsl:call-template name="message"/>
	      		</continuous-change>
	      	</xsl:for-each>
	      </messages>
	    </sequence>
  	</xsl:template>  	
    
  	<xsl:template match="regulator">
	    <regulator>
			<xsl:apply-templates select="@id|locking|name|style|zoom|references"/>
	      <description><xsl:value-of select="description"/></description>
	      <threshold><xsl:value-of select="number(threshold) div 127"/></threshold>
	      <value><xsl:value-of select="number(value) div 127"/></value>
	      <messages>
	      	<xsl:for-each select="message">
	      		<continuous-change>
	      			<xsl:call-template name="message"/>
	      		</continuous-change>
	      	</xsl:for-each>
	      </messages>
	    </regulator>
  	</xsl:template>  	
    
  	<xsl:template match="stop">
	    <stop>
			<xsl:apply-templates select="@id|action|active|locking|name|shortcut|style|transpose|velocity|zoom"/>
			<xsl:variable name="program" select="program"/>
	      <description><xsl:value-of select="description"/></description>
	      <references>
	      	<xsl:for-each select="references/reference">
				<xsl:variable name="id" select="@id"/>
				<xsl:if test="//soundSource[@id = $id]">
		      		<reference>
		      			<xsl:attribute name="id">
							<xsl:value-of select="@id"/>-<xsl:value-of select="$program"/>
						</xsl:attribute>
		      		</reference>
	      		</xsl:if>
	      	</xsl:for-each>
	      </references>
     	  <messages>
	      	<xsl:for-each select="activateMessage">
	      		<activateable-activate>
	      			<xsl:call-template name="message"/>
	      		</activateable-activate>
	      	</xsl:for-each>
	      	<xsl:for-each select="deactivateMessage">
	      		<activateable-deactivate>
	      			<xsl:call-template name="message"/>
	      		</activateable-deactivate>
	      	</xsl:for-each>
	      </messages>
	    </stop>
  	</xsl:template>  	
    
  	<xsl:template match="keyboard">
	    <keyboard>
			<xsl:apply-templates select="@id|name|style|zoom|references"/>
	      <description><xsl:value-of select="description"/></description>
			<xsl:if test="device">
				<input>
					<xsl:value-of select="device"/>
				</input>
			</xsl:if>
	      <messages>
	        <keyboard-pressKey>
	          <status>equal <xsl:value-of select="command + channel"/></status>
	          <data1><xsl:if test="threshold != 0">greater <xsl:value-of select="threshold"/> | </xsl:if>get pitch</data1>
	          <data2>get velocity</data2>
	        </keyboard-pressKey>
	        <keyboard-releaseKey>
	          <status>equal <xsl:value-of select="command + channel"/></status>
	          <data1>get pitch</data1>
	          <data2>equal 0</data2>
	        </keyboard-releaseKey>
	      </messages>
	    </keyboard>
  	</xsl:template>  	
    
  	<xsl:template match="coupler">
	    <coupler>
			<xsl:apply-templates select="@id|action|active|locking|name|shortcut|style|transpose|velocity|zoom|references"/>
	      <description><xsl:value-of select="description"/></description>
   	      <messages>
	      	<xsl:for-each select="activateMessage">
	      		<activateable-activate>
	      			<xsl:call-template name="message"/>
	      		</activateable-activate>
	      	</xsl:for-each>
	      	<xsl:for-each select="deactivateMessage">
	      		<activateable-deactivate>
	      			<xsl:call-template name="message"/>
	      		</activateable-deactivate>
	      	</xsl:for-each>
	      </messages>
	    </coupler>
  	</xsl:template>  	
    
  	<xsl:template match="combination">
	    <combination>
			<xsl:apply-templates select="@id|name|shortcut|style|zoom"/>
	      <description><xsl:value-of select="description"/></description>
	      <references>
	      	<xsl:for-each select="references/combinationReference">
	      		<combination-reference>
					<xsl:apply-templates select="@id|activated"/>
	      		</combination-reference>
	      	</xsl:for-each>
	      </references>
  	      <messages>
	      	<xsl:for-each select="message">
	      		<initiator-initiate>
	      			<xsl:call-template name="message"/>
	      		</initiator-initiate>
	      	</xsl:for-each>
	      </messages>
	    </combination>
  	</xsl:template>  	
    
  	<xsl:template match="activator">
	    <activator>
			<xsl:apply-templates select="@id|active|locking|name|shortcut|style|zoom|references"/>
	      <description><xsl:value-of select="description"/></description>
   	      <messages>
	      	<xsl:for-each select="activateMessage">
	      		<activateable-activate>
	      			<xsl:call-template name="message"/>
	      		</activateable-activate>
	      	</xsl:for-each>
	      	<xsl:for-each select="deactivateMessage">
	      		<activateable-deactivate>
	      			<xsl:call-template name="message"/>
	      		</activateable-deactivate>
	      	</xsl:for-each>
	      </messages>
	    </activator>
  	</xsl:template>  	
    
  	<xsl:template match="swell">
	    <continuousFilter>
			<xsl:apply-templates select="@id|locking|name|style"/>
	      <description><xsl:value-of select="description"/></description>
	      <threshold><xsl:value-of select="number(threshold) div 127"/></threshold>
	      <value><xsl:value-of select="number(value) div 127"/></value>
	      <references/>
	      <messages>
	      	<xsl:for-each select="message">
	      		<continuous-change>
	      			<xsl:call-template name="message"/>
	      		</continuous-change>
	      	</xsl:for-each>
	      	<filter-intercept>
	      		<status>equal 176</status>
	      		<data1>equal 7</data1>
	      		<data2>get volume</data2>
	      	</filter-intercept>
	      	<continuousFilter-engaging>
	      		<status>set 176</status>
	      		<data1>set 7</data1>
	      		<data2>set value | div 2 | add 0.5 | mult volume 127</data2>
	      	</continuousFilter-engaging>
	      </messages>
	      <zoom>1.0</zoom>
	    </continuousFilter>
  	</xsl:template>  	
    
  	<xsl:template match="variation">
	    <activateableFilter>
			<xsl:apply-templates select="@id|active|locking|name|style"/>
	      <description><xsl:value-of select="description"/></description>
	      <references/>
   	      <messages>
	      	<xsl:for-each select="activateMessage">
	      		<activateable-activate>
	      			<xsl:call-template name="message"/>
	      		</activateable-activate>
	      	</xsl:for-each>
	      	<xsl:for-each select="deactivateMessage">
	      		<activateable-deactivate>
	      			<xsl:call-template name="message"/>
	      		</activateable-deactivate>
	      	</xsl:for-each>
	      	<filter-intercept>
          		<status>equal 192</status>
         		<data1>get program</data1>
          		<data2></data2>
	      	</filter-intercept>
	        <activateableFilter-engaged>
	          <status>set 192</status>
	          <data1>set program 0 | add 20</data1>
	          <data2></data2>
	        </activateableFilter-engaged>
	        <activateableFilter-disengaged>
	          <status>set 192</status>
	          <data1>set program 0</data1>
	          <data2></data2>
	        </activateableFilter-disengaged>
	      </messages>
	      <zoom>1.0</zoom>
	    </activateableFilter>
  	</xsl:template>  	

  	<xsl:template match="tremulant">
	    <activateableFilter>
			<xsl:apply-templates select="@id|active|locking|name|style"/>
	      <description><xsl:value-of select="description"/></description>
	      <references/>
   	      <messages>
	      	<xsl:for-each select="activateMessage">
	      		<activateable-activate>
	      			<xsl:call-template name="message"/>
	      		</activateable-activate>
	      	</xsl:for-each>
	      	<xsl:for-each select="deactivateMessage">
	      		<activateable-deactivate>
	      			<xsl:call-template name="message"/>
	      		</activateable-deactivate>
	      	</xsl:for-each>
	      </messages>
	      <zoom>1.0</zoom>
	    </activateableFilter>
  	</xsl:template>  	

  	<xsl:template match="soundSource">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="bank" select="bank"/>
		<xsl:variable name="bankMSB" select="bankMSB"/>
		<xsl:variable name="bankLSB" select="bankLSB"/>
		<xsl:variable name="device" select="device"/>
      	<xsl:for-each select="//stop[references/reference/@id = $id]">
			<xsl:variable name="program" select="program"/>
			<xsl:variable name="remainder" select="following::stop[program=$program and references/reference/@id = $id]"/>
			<xsl:if test="not($remainder)">
			    <rank>
			    	<xsl:attribute name="id">
			    		<xsl:value-of select="$id"/>-<xsl:value-of select="$program"/>
			    	</xsl:attribute>
					<xsl:if test="$device">
						<output><xsl:value-of select="$device"/></output>
					</xsl:if>
			      <channels></channels>
			      <delay>0</delay>
			      <name><xsl:value-of select="name"/></name>
			      <description></description>
			      <zoom>1.0</zoom>
			      <references>
			      	<xsl:for-each select="references/reference">
						<xsl:variable name="temp" select="@id"/>
						<xsl:if test="not(//soundSource[@id = $temp])">
				      		<reference>
				      			<xsl:attribute name="id">
				      				<xsl:value-of select="@id"/>
				      			</xsl:attribute>
				      		</reference>
				      	</xsl:if>
			      	</xsl:for-each>
			      </references>
			      <messages>
			      	<xsl:if test="$bank">
				        <rank-engaged>
				          <status>set 176</status>
				          <data1>set 0</data1>
				          <data2>set <xsl:value-of select="$bank"/></data2>
				        </rank-engaged>
			      	</xsl:if>
			      	<xsl:if test="$bankMSB">
				        <rank-engaged>
				          <status>set 176</status>
				          <data1>set 0</data1>
				          <data2>set <xsl:value-of select="$bankMSB"/></data2>
				        </rank-engaged>
			      	</xsl:if>
			      	<xsl:if test="$bankLSB">
				        <rank-engaged>
				          <status>set 176</status>
				          <data1>set 32</data1>
				          <data2>set <xsl:value-of select="$bankLSB"/></data2>
				        </rank-engaged>
			      	</xsl:if>
			        <rank-engaged>
			          <status>set 192</status>
			          <data1>set <xsl:value-of select="program"/></data1>
			          <data2></data2>
			        </rank-engaged>
			        <rank-disengaged>
			          <status>set 176</status>
			          <data1>set 121</data1>
			          <data2></data2>
			        </rank-disengaged>
			        <rank-disengaged>
			          <status>set 176</status>
			          <data1>set 123</data1>
			          <data2></data2>
			        </rank-disengaged>
			        <rank-notePlayed>
			          <status>set 144</status>
			          <data1>set pitch</data1>
			          <data2>set velocity</data2>
			        </rank-notePlayed>
			        <rank-noteMuted>
			          <status>set 128</status>
			          <data1>set pitch</data1>
			          <data2></data2>
			        </rank-noteMuted>
			      </messages>
			    </rank>
      		</xsl:if>
  		</xsl:for-each>
  	</xsl:template>  	

  	<xsl:template match="keyer">
	  	<keyer>
			<xsl:apply-templates select="@id|active|locking|name|pitch|shortcut|style|velocity|zoom|references"/>
	      <description><xsl:value-of select="description"/></description>
   	      <messages>
	      	<xsl:for-each select="activateMessage">
	      		<activateable-activate>
	      			<xsl:call-template name="message"/>
	      		</activateable-activate>
	      	</xsl:for-each>
	      	<xsl:for-each select="deactivateMessage">
	      		<activateable-deactivate>
	      			<xsl:call-template name="message"/>
	      		</activateable-deactivate>
	      	</xsl:for-each>
	      </messages>
	  	</keyer>
  	</xsl:template>  	

  	<xsl:template match="label">
	  	<label>
			<xsl:apply-templates select="@id|name|style|zoom"/>
	      <description><xsl:value-of select="description"/></description>
	      <references/>
  	      <messages/>
	  	</label>
  	</xsl:template>  	

  	<xsl:template match="incrementer">
	  	<incrementer>
			<xsl:apply-templates select="@id|delta|name|shortcut|style|zoom|references"/>
	      <description><xsl:value-of select="description"/></description>
   	      <messages>
	      	<xsl:for-each select="message">
	      		<initiator-initiate>
	      			<xsl:call-template name="message"/>
	      		</initiator-initiate>
	      	</xsl:for-each>
	      </messages>
	  	</incrementer>
  	</xsl:template>  	

  	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>	
	
	<xsl:template name="message">
		<xsl:for-each select="status">
			<status>
				<xsl:call-template name="data"/>
			</status>
		</xsl:for-each>
		<xsl:for-each select="data1">
			<data1>
				<xsl:call-template name="data"/>
			</data1>
		</xsl:for-each>
		<xsl:for-each select="data2">
			<data2>
				<xsl:call-template name="data"/>
			</data2>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="data">
		<xsl:choose>
			<xsl:when test="node() = -1"></xsl:when>
			<xsl:when test="node() = -2">greater 0</xsl:when>
			<xsl:otherwise>equal <xsl:value-of select="node()"/></xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
</xsl:stylesheet>