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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tPowerTransformer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tPowerTransformer">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.iec.ch/61850/2003/SCL}tEquipment">
 *       &lt;sequence>
 *         &lt;element name="TransformerWinding" type="{http://www.iec.ch/61850/2003/SCL}tTransformerWinding" maxOccurs="unbounded"/>
 *         &lt;element name="SubEquipment" type="{http://www.iec.ch/61850/2003/SCL}tSubEquipment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="EqFunction" type="{http://www.iec.ch/61850/2003/SCL}tEqFunction" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.iec.ch/61850/2003/SCL}tPowerTransformerEnum" fixed="PTR" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPowerTransformer", propOrder = {
    "transformerWinding",
    "subEquipment",
    "eqFunction"
})
public class TPowerTransformer
    extends TEquipment
{

    @XmlElement(name = "TransformerWinding", required = true)
    protected List<TTransformerWinding> transformerWinding;
    @XmlElement(name = "SubEquipment")
    protected List<TSubEquipment> subEquipment;
    @XmlElement(name = "EqFunction")
    protected List<TEqFunction> eqFunction;
    @XmlAttribute(name = "type", required = true)
    protected TPowerTransformerEnum type;

    /**
     * Gets the value of the transformerWinding property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transformerWinding property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransformerWinding().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TTransformerWinding }
     * 
     * 
     */
    public List<TTransformerWinding> getTransformerWinding() {
        if (transformerWinding == null) {
            transformerWinding = new ArrayList<TTransformerWinding>();
        }
        return this.transformerWinding;
    }

    /**
     * Gets the value of the subEquipment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subEquipment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubEquipment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TSubEquipment }
     * 
     * 
     */
    public List<TSubEquipment> getSubEquipment() {
        if (subEquipment == null) {
            subEquipment = new ArrayList<TSubEquipment>();
        }
        return this.subEquipment;
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
     *     {@link TPowerTransformerEnum }
     *     
     */
    public TPowerTransformerEnum getType() {
        if (type == null) {
            return TPowerTransformerEnum.PTR;
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TPowerTransformerEnum }
     *     
     */
    public void setType(TPowerTransformerEnum value) {
        this.type = value;
    }

}
