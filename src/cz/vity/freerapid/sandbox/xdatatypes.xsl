<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xd="http://www.syntea.cz/xdef/2.0"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
                xmlns:bk="userDefinedFunctions"
        >
    <xsl:variable name="ns" select="'http://www.w3.org/2001/XMLSchema'"/>

    <xsl:template name="simpleType">
        <xsl:param name="base" select="'null'"/>
        <xsl:param name="name" select="'null'"/>
        <xsl:element name="xsd:simpleType" namespace="{$ns}">
            <xsl:if test="$name != 'null'">
                <xsl:attribute name="name" select="$name"/>
            </xsl:if>
            <xsl:element name="xsd:restriction" namespace="{$ns}">
                <xsl:attribute name="base" select="$base"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template name="attribute-integer">
        <xsl:param name="value" select="'null'"/>
        <xsl:param name="name" select="'null'"/>

        <xsl:analyze-string select="$value" regex="int\(\)">
            <xsl:matching-substring>
                <xsl:call-template name="simpleType">
                    <xsl:with-param name="base" select="'xsd:integer'"/>
                    <xsl:with-param name="name" select="$name"/>
                </xsl:call-template>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:analyze-string select="$value" regex="int\(([0-9]+),([0-9]+)\)">
                    <xsl:matching-substring>
                        <xsl:element name="xsd:simpleType" namespace="{$ns}">
                            <xsl:if test="$name != 'null'">
                                <xsl:attribute name="name" select="$name"/>
                            </xsl:if>
                            <xsl:element name="xsd:restriction" namespace="{$ns}">
                                <xsl:attribute name="base" select="'xsd:integer'"/>
                                <xsl:element name="xsd:minInclusive" namespace="{$ns}">
                                    <xsl:attribute name="value" select="fn:regex-group(1)"/>
                                </xsl:element>
                                <xsl:element name="xsd:maxInclusive" namespace="{$ns}">
                                    <xsl:attribute name="value" select="fn:regex-group(2)"/>
                                </xsl:element>
                            </xsl:element>
                        </xsl:element>
                    </xsl:matching-substring>
                </xsl:analyze-string>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xsl:template name="attribute-string">
        <xsl:param name="value" select="'null'"/>
        <xsl:param name="name" select="'null'"/>

        <xsl:analyze-string select="$value" regex="string\(\)">
            <xsl:matching-substring>
                <xsl:call-template name="simpleType">
                    <xsl:with-param name="base" select="'xsd:string'"/>
                    <xsl:with-param name="name" select="$name"/>
                </xsl:call-template>
            </xsl:matching-substring>
        </xsl:analyze-string>
        <xsl:analyze-string select="$value" regex="string\(([0-9]+),([0-9]+)\)">
            <xsl:matching-substring>
                <xsl:copy-of select="bk:str-type($name, fn:regex-group(1), fn:regex-group(2))"/>
            </xsl:matching-substring>
        </xsl:analyze-string>
        <xsl:analyze-string select="$value" regex="string\(([0-9]+)\)">
            <xsl:matching-substring>
                <xsl:copy-of select="bk:str-type($name, fn:regex-group(1), fn:regex-group(1))"/>
            </xsl:matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xsl:function name="bk:str-type">
        <xsl:param name="name"/>
        <xsl:param name="minLength"/>
        <xsl:param name="maxLength"/>

        <xsl:element name="xsd:simpleType" namespace="{$ns}">
            <xsl:if test="$name != 'null'">
                <xsl:attribute name="name" select="$name"/>
            </xsl:if>
            <xsl:element name="xsd:restriction" namespace="{$ns}">
                <xsl:attribute name="base" select="'xsd:string'"/>
                <xsl:element name="xsd:minLength" namespace="{$ns}">
                    <xsl:attribute name="value" select="$minLength"/>
                </xsl:element>
                <xsl:element name="xsd:maxLength" namespace="{$ns}">
                    <xsl:attribute name="value" select="$maxLength"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>

    </xsl:function>

    <xsl:template name="attribute-occurs">
        <xsl:param name="value" select="'null'"/>
        <xsl:analyze-string select="$value" regex="occurs ([0-9]+)..([0-9]+)">
            <xsl:matching-substring>
                <xsl:attribute name="minOccurs" select="fn:regex-group(1)"/>
                <xsl:attribute name="maxOccurs" select="fn:regex-group(2)"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:analyze-string select="$value" regex="occurs ([0-9]+)..*">
                    <xsl:matching-substring>
                        <xsl:attribute name="minOccurs" select="fn:regex-group(1)"/>
                        <xsl:attribute name="maxOccurs" select="'unbounded'"/>
                    </xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <xsl:analyze-string select="$value" regex="occurs ([0-9]+)">
                            <xsl:matching-substring>
                                <xsl:attribute name="minOccurs" select="fn:regex-group(1)"/>
                                <xsl:attribute name="maxOccurs" select="fn:regex-group(1)"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                        <xsl:analyze-string select="$value" regex="occurs \?">
                            <xsl:matching-substring>
                                <xsl:attribute name="minOccurs" select="'0'"/>
                                <xsl:attribute name="maxOccurs" select="'1'"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                        <xsl:analyze-string select="$value" regex="occurs \*">
                            <xsl:matching-substring>
                                <xsl:attribute name="maxOccurs" select="'unbounded'"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                        <xsl:analyze-string select="$value" regex="occurs \+">
                            <xsl:matching-substring>
                                <xsl:attribute name="minOccurs" select="'1'"/>
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xsl:template name="attribute-ref">
        <xsl:param name="value" select="'null'"/>
        <xsl:analyze-string select="$value" regex="\s*ref (.+)\s*">
            <xsl:matching-substring>
                <xsl:attribute name="ref" select="fn:regex-group(1)"/>
            </xsl:matching-substring>
        </xsl:analyze-string>
    </xsl:template>

</xsl:stylesheet>