//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.04.12 at 06:34:15 PM MSK 
//


package ru.nti.dpts.schememanagerback.service.converter.scl2007B4;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tDomainLNGroupMEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="tDomainLNGroupMEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}Name">
 *     &lt;length value="4"/>
 *     &lt;pattern value="M[A-Z]*"/>
 *     &lt;enumeration value="MDIF"/>
 *     &lt;enumeration value="MENV"/>
 *     &lt;enumeration value="MFLK"/>
 *     &lt;enumeration value="MHAI"/>
 *     &lt;enumeration value="MHAN"/>
 *     &lt;enumeration value="MHYD"/>
 *     &lt;enumeration value="MMDC"/>
 *     &lt;enumeration value="MMET"/>
 *     &lt;enumeration value="MMTN"/>
 *     &lt;enumeration value="MMTR"/>
 *     &lt;enumeration value="MMXN"/>
 *     &lt;enumeration value="MMXU"/>
 *     &lt;enumeration value="MSQI"/>
 *     &lt;enumeration value="MSTA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tDomainLNGroupMEnum")
@XmlEnum
public enum TDomainLNGroupMEnum {

    MDIF,
    MENV,
    MFLK,
    MHAI,
    MHAN,
    MHYD,
    MMDC,
    MMET,
    MMTN,
    MMTR,
    MMXN,
    MMXU,
    MSQI,
    MSTA;

    public String value() {
        return name();
    }

    public static TDomainLNGroupMEnum fromValue(String v) {
        return valueOf(v);
    }

}
