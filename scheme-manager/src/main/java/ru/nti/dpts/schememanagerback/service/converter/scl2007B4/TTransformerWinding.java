//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.04.12 at 06:34:15 PM MSK 
//


package ru.nti.dpts.schememanagerback.service.converter.scl2007B4;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tTransformerWinding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tTransformerWinding">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.iec.ch/61850/2003/SCL}tAbstractConductingEquipment">
 *       &lt;sequence>
 *         &lt;element name="TapChanger" type="{http://www.iec.ch/61850/2003/SCL}tTapChanger" minOccurs="0"/>
 *         &lt;element name="NeutralPoint" type="{http://www.iec.ch/61850/2003/SCL}tTerminal" minOccurs="0"/>
 *         &lt;element name="EqFunction" type="{http://www.iec.ch/61850/2003/SCL}tEqFunction" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.iec.ch/61850/2003/SCL}tTransformerWindingEnum" fixed="PTW" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTransformerWinding", propOrder = {
    "tapChanger",
    "neutralPoint",
    "eqFunction"
})
public class TTransformerWinding
    extends TAbstractConductingEquipment
{

    @XmlElement(name = "TapChanger")
    protected TTapChanger tapChanger;
    @XmlElement(name = "NeutralPoint")
    protected TTerminal neutralPoint;
    @XmlElement(name = "EqFunction")
    protected List<TEqFunction> eqFunction;
    @XmlAttribute(name = "type", required = true)
    protected TTransformerWindingEnum type;

    /**
     * Gets the value of the tapChanger property.
     * 
     * @return
     *     possible object is
     *     {@link TTapChanger }
     *     
     */
    public TTapChanger getTapChanger() {
        return tapChanger;
    }

    /**
     * Sets the value of the tapChanger property.
     * 
     * @param value
     *     allowed object is
     *     {@link TTapChanger }
     *     
     */
    public void setTapChanger(TTapChanger value) {
        this.tapChanger = value;
    }

    /**
     * Gets the value of the neutralPoint property.
     * 
     * @return
     *     possible object is
     *     {@link TTerminal }
     *     
     */
    public TTerminal getNeutralPoint() {
        return neutralPoint;
    }

    /**
     * Sets the value of the neutralPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link TTerminal }
     *     
     */
    public void setNeutralPoint(TTerminal value) {
        this.neutralPoint = value;
    }

    /**
     * Gets the value of the eqFunction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eqFunction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEqFunction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TEqFunction }
     * 
     * 
     */
    public List<TEqFunction> getEqFunction() {
        if (eqFunction == null) {
            eqFunction = new ArrayList<TEqFunction>();
        }
        return this.eqFunction;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TTransformerWindingEnum }
     *     
     */
    public TTransformerWindingEnum getType() {
        if (type == null) {
            return TTransformerWindingEnum.PTW;
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TTransformerWindingEnum }
     *     
     */
    public void setType(TTransformerWindingEnum value) {
        this.type = value;
    }

}
