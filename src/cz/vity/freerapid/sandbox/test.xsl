<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xd="http://www.syntea.cz/xdef/2.0"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
        >
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xmlns:xsd="http://www.w3.org/2001/XMLSchema"/>
    <xsl:include href="xdatatypes.xsl"/>
    <xsl:strip-space elements="*"/>

    <xsl:variable name="ref" select="//@*[name()='xd:script']/." as="attribute()*"/>

    <xsl:template match="/">
        <xsl:value-of select="count($ref)"/>
        <xsl:variable name="referencedElements" as="element()*">
            <xsl:for-each select="xd:def/*">
                <xsl:variable name="el" select="current()"/>
                <xsl:for-each select="$ref">
                    <xsl:if test="fn:matches(., concat('ref ', $el/name()))">
                        <xsl:sequence select="$el"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:variable>
        <xsl:for-each-group select="$referencedElements" group-by="generate-id()">
            vybrano:
            <xsl:value-of select="name()"/>
            <xsl:value-of select="'parent'"/>
            <xsl:value-of select="parent::*/name()"/>
        </xsl:for-each-group>

        <xsl:apply-templates select="xd:def"/>
    </xsl:template>


    <xsl:template match="xd:def">
        <xsl:element name="xsd:schema" namespace="{$ns}">

            <xsl:for-each select="//*[text()]">
                <xsl:call-template name="checkDataType">
                    <xsl:with-param name="value" select="text()"/>
                    <xsl:with-param name="name" select="concat('T', name())"/>
                </xsl:call-template>
            </xsl:for-each>

            <xsl:apply-templates select="*"/>


        </xsl:element>
    </xsl:template>

    <xsl:template match="comment()">
        <xsl:copy/>
    </xsl:template>

    <xsl:template match="xd:sequence">
        <xsl:element name="xsd:sequence" namespace="{$ns}">
            <xsl:apply-templates select="*"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xd:choice">
        <xsl:element name="xsd:choice" namespace="{$ns}">
            <xsl:apply-templates select="*"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xd:mixed">
        <xsl:element name="xsd:all" namespace="{$ns}">
            <xsl:apply-templates select="*"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="*">

        <xsl:variable name="type" as="element()*">
            <xsl:if test="text()">
                <xsl:call-template name="checkDataType">
                    <xsl:with-param name="value" select="text()"/>
                    <xsl:with-param name="name" select="concat('T', name())"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:variable>

        <xsl:element name="xsd:element" namespace="{$ns}">
            <xsl:if test="not (fn:matches(@xd:script, 'ref '))">
                <xsl:attribute name="name">
                    <xsl:value-of select="name()"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$type/*">
                <xsl:attribute name="type" select="concat('T', name())"/>
            </xsl:if>
            <xsl:apply-templates select="@*[fn:starts-with(name(), 'xd:')]"/>

            <xsl:choose>
                <xsl:when test="*|@*[name() != 'xd:script']">
                    <xsl:element name="xsd:complexType" namespace="{$ns}">
                        <xsl:apply-templates select="*|@*[name() != 'xd:script']"/>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@xd:script">
        <xsl:for-each select="fn:tokenize(., ';')">
            <xsl:choose>
                <xsl:when test="fn:matches(., 'occurs')">
                    <xsl:call-template name="attribute-occurs">
                        <xsl:with-param name="value" select="."/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="fn:matches(., '\s*ref ')">
                    <xsl:call-template name="attribute-ref">
                        <xsl:with-param name="value" select="."/>
                    </xsl:call-template>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:element name="xsd:attribute" namespace="{$ns}">
            <xsl:attribute name="name">
                <xsl:value-of select="name()"/>
            </xsl:attribute>
            <xsl:choose>
                <xsl:when test="fn:matches(., 'required')">
                    <xsl:attribute name="use" select="'required'"/>
                </xsl:when>
                <xsl:when test="fn:matches(., 'optional')">
                    <xsl:attribute name="use" select="'optional'"/>
                </xsl:when>
            </xsl:choose>
            <xsl:call-template name="checkDataType">
                <xsl:with-param name="value" select="."/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <xsl:template name="checkDataType">
        <xsl:param name="value" select="'null'"/>
        <xsl:param name="name" select="'null'"/>

        <xsl:call-template name="attribute-string">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="name" select="$name"/>
        </xsl:call-template>

        <xsl:call-template name="attribute-integer">
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="name" select="$name"/>
        </xsl:call-template>
    </xsl:template>


</xsl:stylesheet>