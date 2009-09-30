<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:template match="/">
		<memory>
    	  	<xsl:for-each select="//memory">
				<titles>
			      	<xsl:for-each select="titles/string">
			      		<string>
							<xsl:value-of select="."/>
			      		</string>
			      	</xsl:for-each>
				</titles>
				
				<combinations>
			      	<xsl:for-each select="references/reference">
						<xsl:variable name="id">
							<xsl:value-of select="element/@ref"/>
						</xsl:variable>
						
			      		<combination>
							<xsl:attribute name="id">
								<xsl:value-of select="translate($id, '-', '0')"/>
							</xsl:attribute>
	
							<references>
						      	<xsl:for-each select="//combination[@id = $id]/references/combination-switchReference">
						      		<switch>
										<xsl:attribute name="id">
											<xsl:value-of select="element/@ref"/>
										</xsl:attribute>
										<actives>
											<xsl:value-of select="actives"/>
										</actives>
									</switch>
						      	</xsl:for-each>
	
						      	<xsl:for-each select="//combination[@id = $id]/references/combination-continuousReference">
						      		<continuous>
										<xsl:attribute name="id">
											<xsl:value-of select="element/@ref"/>
										</xsl:attribute>
										<values>
											<xsl:value-of select="values"/>
										</values>
									</continuous>
						      	</xsl:for-each>
							</references>
			      		</combination>
			      	</xsl:for-each>
				</combinations>
	      	</xsl:for-each>
		</memory>
	</xsl:template>
 </xsl:stylesheet>