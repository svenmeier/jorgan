<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:template match="/">
      	<xsl:for-each select="//memory">
			<memory>
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
				      		<xsl:choose>
				      			<xsl:when test="element/@ref">
									<xsl:value-of select="element/@ref"/>
				      			</xsl:when>
				      			<xsl:otherwise>
									<xsl:value-of select="@id"/>
				      			</xsl:otherwise>
				      		</xsl:choose>			      	
						</xsl:variable>
						
			      		<combination>
							<xsl:attribute name="id">
								<xsl:value-of select="translate($id, '-', '0')"/>
							</xsl:attribute>
	
							<references>
						      	<xsl:for-each select="//combination[@id = $id]/references/combination-switchReference">
						      		<switch>
										<xsl:attribute name="id">
											<xsl:value-of select="translate(@id, '-', '0')"/>
										</xsl:attribute>
										<actives>
											<xsl:value-of select="actives"/>
										</actives>
									</switch>
						      	</xsl:for-each>
	
						      	<xsl:for-each select="//combination[@id = $id]/references/combination-continuousReference">
						      		<continuous>
										<xsl:attribute name="id">
											<xsl:value-of select="translate(@id, '-', '0')"/>
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
			</memory>
      	</xsl:for-each>
	</xsl:template>
 </xsl:stylesheet>