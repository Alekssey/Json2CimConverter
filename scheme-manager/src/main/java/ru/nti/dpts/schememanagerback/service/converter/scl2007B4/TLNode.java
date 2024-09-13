//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.04.12 at 06:34:15 PM MSK 
//


package ru.nti.dpts.schememanagerback.service.converter.scl2007B4;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for tLNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tLNode">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.iec.ch/61850/2003/SCL}tUnNaming">
 *       &lt;attribute name="iedName" type="{http://www.iec.ch/61850/2003/SCL}tIEDNameOrNone" default="None" />
 *       &lt;attribute name="ldInst" type="{http://www.iec.ch/61850/2003/SCL}tLDInstOrEmpty" default="" />
 *       &lt;attribute name="prefix" type="{http://www.iec.ch/61850/2003/SCL}tPrefix" default="" />
 *       &lt;attribute name="lnClass" use="required" type="{http://www.iec.ch/61850/2003/SCL}tLNClassEnum" />
 *       &lt;attribute name="lnInst" type="{http://www.iec.ch/61850/2003/SCL}tLNInstOrEmpty" default="" />
 *       &lt;attribute name="lnType" type="{http://www.iec.ch/61850/2003/SCL}tName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tLNode")
public class TLNode
    extends TUnNaming
{

    @XmlAttribute(name = "iedName")
    protected String iedName;
    @XmlAttribute(name = "ldInst")
    protected String ldInst;
    @XmlAttribute(name = "prefix")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String prefix;
    @XmlAttribute(name = "lnClass", required = true)
    protected List<String> lnClass;
    @XmlAttribute(name = "lnInst")
    protected String lnInst;
    @XmlAttribute(name = "lnType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String lnType;

    /**
     * Gets the value of the iedName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIedName() {
        if (iedName == null) {
            return "None";
        } else {
            return iedName;
        }
    }

    /**
     * Sets the value of the iedName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIedName(String value) {
        this.iedName = value;
    }

    /**
     * Gets the value of the ldInst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLdInst() {
        if (ldInst == null) {
            return "";
        } else {
            return ldInst;
        }
    }

    /**
     * Sets the value of the ldInst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLdInst(String value) {
        this.ldInst = value;
    }

    /**
     * Gets the value of the prefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrefix() {
        if (prefix == null) {
            return "";
        } else {
            return prefix;
        }
    }

    /**
     * Sets the value of the prefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrefix(String value) {
        this.prefix = value;
    }

    /**
     * Gets the value of the lnClass property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lnClass property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLnClass().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLnClass() {
        if (lnClass == null) {
            lnClass = new ArrayList<String>();
        }
        return this.lnClass;
    }

    /**
     * Gets the value of the lnInst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLnInst() {
        if (lnInst == null) {
            return "";
        } else {
            return lnInst;
        }
    }

    /**
     * Sets the value of the lnInst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLnInst(String value) {
        this.lnInst = value;
    }

    /**
     * Gets the value of the lnType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLnType() {
        return lnType;
    }

    /**
     * Sets the value of the lnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLnType(String value) {
        this.lnType = value;
    }

}
