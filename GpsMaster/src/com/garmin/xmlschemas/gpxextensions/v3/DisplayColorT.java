//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.02.02 at 03:42:40 PM CET
//


package com.garmin.xmlschemas.gpxextensions.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DisplayColor_t.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DisplayColor_t">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="Black"/>
 *     &lt;enumeration value="DarkRed"/>
 *     &lt;enumeration value="DarkGreen"/>
 *     &lt;enumeration value="DarkYellow"/>
 *     &lt;enumeration value="DarkBlue"/>
 *     &lt;enumeration value="DarkMagenta"/>
 *     &lt;enumeration value="DarkCyan"/>
 *     &lt;enumeration value="LightGray"/>
 *     &lt;enumeration value="DarkGray"/>
 *     &lt;enumeration value="Red"/>
 *     &lt;enumeration value="Green"/>
 *     &lt;enumeration value="Yellow"/>
 *     &lt;enumeration value="Blue"/>
 *     &lt;enumeration value="Magenta"/>
 *     &lt;enumeration value="Cyan"/>
 *     &lt;enumeration value="White"/>
 *     &lt;enumeration value="Transparent"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "DisplayColor_t")
@XmlEnum
public enum DisplayColorT {

    @XmlEnumValue("Black")
    BLACK("Black"),
    @XmlEnumValue("DarkRed")
    DARK_RED("DarkRed"),
    @XmlEnumValue("DarkGreen")
    DARK_GREEN("DarkGreen"),
    @XmlEnumValue("DarkYellow")
    DARK_YELLOW("DarkYellow"),
    @XmlEnumValue("DarkBlue")
    DARK_BLUE("DarkBlue"),
    @XmlEnumValue("DarkMagenta")
    DARK_MAGENTA("DarkMagenta"),
    @XmlEnumValue("DarkCyan")
    DARK_CYAN("DarkCyan"),
    @XmlEnumValue("LightGray")
    LIGHT_GRAY("LightGray"),
    @XmlEnumValue("DarkGray")
    DARK_GRAY("DarkGray"),
    @XmlEnumValue("Red")
    RED("Red"),
    @XmlEnumValue("Green")
    GREEN("Green"),
    @XmlEnumValue("Yellow")
    YELLOW("Yellow"),
    @XmlEnumValue("Blue")
    BLUE("Blue"),
    @XmlEnumValue("Magenta")
    MAGENTA("Magenta"),
    @XmlEnumValue("Cyan")
    CYAN("Cyan"),
    @XmlEnumValue("White")
    WHITE("White"),
    @XmlEnumValue("Transparent")
    TRANSPARENT("Transparent");
    private final String value;

    DisplayColorT(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DisplayColorT fromValue(String v) {
        for (DisplayColorT c: DisplayColorT.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
