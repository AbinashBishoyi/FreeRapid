package cz.vity.freerapid.xmlimport.ver1;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element ref="{}plugin" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "plugin"
        })
@XmlRootElement(name = "plugins")
public class Plugins {

    @XmlElement(required = true)
    protected List<Plugin> plugin;

    /**
     * Gets the value of the plugin property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the plugin property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlugin().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Plugin }
     */
    public List<Plugin> getPlugin() {
        if (plugin == null) {
            plugin = new ArrayList<Plugin>();
        }
        return this.plugin;
    }

    public boolean isSetPlugin() {
        return ((this.plugin != null) && (!this.plugin.isEmpty()));
    }

    public void unsetPlugin() {
        this.plugin = null;
    }

}
