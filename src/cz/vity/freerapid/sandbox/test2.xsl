<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">
        <!-- the version of the resulting schema -->
        <xsl:variable name="version">version3</xsl:variable>
        <!-- the version of the common components imported schema -->
        <xsl:variable name="commonVersion">version2</xsl:variable>
        <!-- the version of the custom components imported schema -->
        <xsl:variable name="customVersion">version1</xsl:variable>
        <!-- the targetNamespace -->
        <xsl:variable name="target">http://xmlns.myexample.com/
            <xsl:value-of select="$version"/>
        </xsl:variable>
        <!-- the common components namespace -->
        <xsl:variable name="common">http://xmlns.myexample.com/common/
            <xsl:value-of select="$commonVersion"/>
        </xsl:variable>
        <!-- the custom components namespace -->
        <xsl:variable name="custom">http://xmlns.myexample.com/custom/
            <xsl:value-of select="$customVersion"/>
        </xsl:variable>
        <!-- we cannot do this:
="http://xmlns.myexample.com/{$commonVersion}"
we must add the namespaces generated above to dummy attributes -->
        <xsl:variable name="default-ns-node">
            <xsl:element name="default-ns-element" namespace="{$target}"/>
        </xsl:variable>
        <xsl:variable name="common-ns-node">
            <xsl:element name="common-ns-element" namespace="{$common}">
                <xsl:attribute name="common:dummy" namespace="{$common}"/>
            </xsl:element>
        </xsl:variable>
        <xsl:variable name="custom-ns-node">
            <xsl:element name="custom-ns-element" namespace="{$common}">
                <xsl:attribute name="custom:dummy" namespace="{$custom}"/>
            </xsl:element>
        </xsl:variable>
        <!-- ========================================== -->
        <xsd:schema>


            <!-- since we cannot do xsl:copy, we simply copy the namespace axis referring to the local-name only -->
            <xsl:copy-of select="$default-ns-node/*/namespace::*[local-name()='']"/>
            <xsl:copy-of select="$common-ns-node/*/namespace::*[local-name()='common']"/>
            <xsl:copy-of select="$custom-ns-node/*/namespace::*[local-name()='custom']"/>
            <!-- form defaults and targetNamespace attributes don't need special treatment, so they can be simply added with xsl:attribute -->
            <xsl:attribute name="elementFormDefault">qualified</xsl:attribute>
            <xsl:attribute name="attributeFormDefault">unqualified</xsl:attribute>
            <xsl:attribute name="targetNamespace">
                <xsl:value-of select="$target"/>
            </xsl:attribute>
            <!-- ========================================== -->
            <!-- the namespace attribute in import statements can use variables directly -->
        </xsd:schema>
    </xsl:template>
</xsl:stylesheet>