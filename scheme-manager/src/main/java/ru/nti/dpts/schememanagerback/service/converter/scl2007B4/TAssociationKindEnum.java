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
 * <p>Java class for tAssociationKindEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="tAssociationKindEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="pre-established"/>
 *     &lt;enumeration value="predefined"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tAssociationKindEnum")
@XmlEnum
public enum TAssociationKindEnum {

    @XmlEnumValue("pre-established")
    PRE_ESTABLISHED("pre-established"),
    @XmlEnumValue("predefined")
    PREDEFINED("predefined");
    private final String value;

    TAssociationKindEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TAssociationKindEnum fromValue(String v) {
        for (TAssociationKindEnum c: TAssociationKindEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
