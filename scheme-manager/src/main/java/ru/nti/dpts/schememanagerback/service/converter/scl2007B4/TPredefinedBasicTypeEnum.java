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
 * <p>Java class for tPredefinedBasicTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="tPredefinedBasicTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}Name">
 *     &lt;enumeration value="BOOLEAN"/>
 *     &lt;enumeration value="INT8"/>
 *     &lt;enumeration value="INT16"/>
 *     &lt;enumeration value="INT24"/>
 *     &lt;enumeration value="INT32"/>
 *     &lt;enumeration value="INT64"/>
 *     &lt;enumeration value="INT128"/>
 *     &lt;enumeration value="INT8U"/>
 *     &lt;enumeration value="INT16U"/>
 *     &lt;enumeration value="INT24U"/>
 *     &lt;enumeration value="INT32U"/>
 *     &lt;enumeration value="FLOAT32"/>
 *     &lt;enumeration value="FLOAT64"/>
 *     &lt;enumeration value="Enum"/>
 *     &lt;enumeration value="Dbpos"/>
 *     &lt;enumeration value="Tcmd"/>
 *     &lt;enumeration value="Quality"/>
 *     &lt;enumeration value="Timestamp"/>
 *     &lt;enumeration value="VisString32"/>
 *     &lt;enumeration value="VisString64"/>
 *     &lt;enumeration value="VisString65"/>
 *     &lt;enumeration value="VisString129"/>
 *     &lt;enumeration value="VisString255"/>
 *     &lt;enumeration value="Octet64"/>
 *     &lt;enumeration value="Unicode255"/>
 *     &lt;enumeration value="Struct"/>
 *     &lt;enumeration value="EntryTime"/>
 *     &lt;enumeration value="Check"/>
 *     &lt;enumeration value="ObjRef"/>
 *     &lt;enumeration value="Currency"/>
 *     &lt;enumeration value="PhyComAddr"/>
 *     &lt;enumeration value="TrgOps"/>
 *     &lt;enumeration value="OptFlds"/>
 *     &lt;enumeration value="SvOptFlds"/>
 *     &lt;enumeration value="LogOptFlds"/>
 *     &lt;enumeration value="EntryID"/>
 *     &lt;enumeration value="Octet6"/>
 *     &lt;enumeration value="Octet16"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tPredefinedBasicTypeEnum")
@XmlEnum
public enum TPredefinedBasicTypeEnum {

    BOOLEAN("BOOLEAN"),
    @XmlEnumValue("INT8")
    INT_8("INT8"),
    @XmlEnumValue("INT16")
    INT_16("INT16"),
    @XmlEnumValue("INT24")
    INT_24("INT24"),
    @XmlEnumValue("INT32")
    INT_32("INT32"),
    @XmlEnumValue("INT64")
    INT_64("INT64"),
    @XmlEnumValue("INT128")
    INT_128("INT128"),
    @XmlEnumValue("INT8U")
    INT_8_U("INT8U"),
    @XmlEnumValue("INT16U")
    INT_16_U("INT16U"),
    @XmlEnumValue("INT24U")
    INT_24_U("INT24U"),
    @XmlEnumValue("INT32U")
    INT_32_U("INT32U"),
    @XmlEnumValue("FLOAT32")
    FLOAT_32("FLOAT32"),
    @XmlEnumValue("FLOAT64")
    FLOAT_64("FLOAT64"),
    @XmlEnumValue("Enum")
    ENUM("Enum"),
    @XmlEnumValue("Dbpos")
    DBPOS("Dbpos"),
    @XmlEnumValue("Tcmd")
    TCMD("Tcmd"),
    @XmlEnumValue("Quality")
    QUALITY("Quality"),
    @XmlEnumValue("Timestamp")
    TIMESTAMP("Timestamp"),
    @XmlEnumValue("VisString32")
    VIS_STRING_32("VisString32"),
    @XmlEnumValue("VisString64")
    VIS_STRING_64("VisString64"),
    @XmlEnumValue("VisString65")
    VIS_STRING_65("VisString65"),
    @XmlEnumValue("VisString129")
    VIS_STRING_129("VisString129"),
    @XmlEnumValue("VisString255")
    VIS_STRING_255("VisString255"),
    @XmlEnumValue("Octet64")
    OCTET_64("Octet64"),
    @XmlEnumValue("Unicode255")
    UNICODE_255("Unicode255"),
    @XmlEnumValue("Struct")
    STRUCT("Struct"),
    @XmlEnumValue("EntryTime")
    ENTRY_TIME("EntryTime"),
    @XmlEnumValue("Check")
    CHECK("Check"),
    @XmlEnumValue("ObjRef")
    OBJ_REF("ObjRef"),
    @XmlEnumValue("Currency")
    CURRENCY("Currency"),
    @XmlEnumValue("PhyComAddr")
    PHY_COM_ADDR("PhyComAddr"),
    @XmlEnumValue("TrgOps")
    TRG_OPS("TrgOps"),
    @XmlEnumValue("OptFlds")
    OPT_FLDS("OptFlds"),
    @XmlEnumValue("SvOptFlds")
    SV_OPT_FLDS("SvOptFlds"),
    @XmlEnumValue("LogOptFlds")
    LOG_OPT_FLDS("LogOptFlds"),
    @XmlEnumValue("EntryID")
    ENTRY_ID("EntryID"),
    @XmlEnumValue("Octet6")
    OCTET_6("Octet6"),
    @XmlEnumValue("Octet16")
    OCTET_16("Octet16");
    private final String value;

    TPredefinedBasicTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TPredefinedBasicTypeEnum fromValue(String v) {
        for (TPredefinedBasicTypeEnum c: TPredefinedBasicTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
