package cz.vity.freerapid.xmlimport.ver1;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}services"/>
 *         &lt;element ref="{}url"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="vendor" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="minVer" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxVer" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="filename" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="filesize" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "services",
        "url"
        })
@XmlRootElement(name = "plugin")
public class Plugin {

    @XmlElement(required = true)
    protected String services;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String url;
    @XmlAttribute(required = true)
    protected String version;
    @XmlAttribute(required = true)
    protected String vendor;
    @XmlAttribute(required = true)
    protected String minVer;
    @XmlAttribute(required = true)
    protected String maxVer;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String premium;
    @XmlAttribute(required = true)
    protected String filename;
    @XmlAttribute(required = true)
    protected long filesize;

    /**
     * Gets the value of the services property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getServices() {
        return services;
    }

    /**
     * Sets the value of the services property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setServices(String value) {
        this.services = value;
    }

    public boolean isSetServices() {
        return (this.services != null);
    }

    /**
     * Gets the value of the url property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUrl(String value) {
        this.url = value;
    }

    public boolean isSetUrl() {
        return (this.url != null);
    }

    /**
     * Gets the value of the version property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVersion(String value) {
        this.version = value;
    }

    public boolean isSetVersion() {
        return (this.version != null);
    }

    /**
     * Gets the value of the vendor property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVendor(String value) {
        this.vendor = value;
    }

    public boolean isSetVendor() {
        return (this.vendor != null);
    }

    /**
     * Gets the value of the minVer property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMinVer() {
        return minVer;
    }

    /**
     * Sets the value of the minVer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMinVer(String value) {
        this.minVer = value;
    }

    public boolean isSetMinVer() {
        return (this.minVer != null);
    }

    /**
     * Gets the value of the maxVer property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMaxVer() {
        return maxVer;
    }

    /**
     * Sets the value of the maxVer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMaxVer(String value) {
        this.maxVer = value;
    }

    public boolean isSetMaxVer() {
        return (this.maxVer != null);
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    public boolean isSetId() {
        return (this.id != null);
    }

    /**
     * Gets the value of the filename property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    public boolean isSetFilename() {
        return (this.filename != null);
    }

    /**
     * Gets the value of the filesize property.
     */
    public long getFilesize() {
        return filesize;
    }

    /**
     * Sets the value of the filesize property.
     */
    public void setFilesize(long value) {
        this.filesize = value;
    }

    public boolean isSetFilesize() {
        return true;
    }


    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public boolean isSetPremium() {
        return true;
    }
}
