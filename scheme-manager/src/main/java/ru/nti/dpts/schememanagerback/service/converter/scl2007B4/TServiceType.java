//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.04.12 at 06:34:15 PM MSK 
//


package ru.nti.dpts.schememanagerback.service.converter.scl2007B4;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tServiceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="tServiceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}Name">
 *     &lt;enumeration value="Poll"/>
 *     &lt;enumeration value="Report"/>
 *     &lt;enumeration value="GOOSE"/>
 *     &lt;enumeration value="SMV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tServiceType")
@XmlEnum
public enum TServiceType {

    @XmlEnumValue("Poll")
    POLL("Poll"),
    @XmlEnumValue("Report")
    REPORT("Report"),
    GOOSE("GOOSE"),
    SMV("SMV");
    private final String value;

    TServiceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TServiceType fromValue(String v) {
        for (TServiceType c: TServiceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
