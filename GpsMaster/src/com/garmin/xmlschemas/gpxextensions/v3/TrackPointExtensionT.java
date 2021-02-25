//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.02.02 at 03:42:40 PM CET
//


package com.garmin.xmlschemas.gpxextensions.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *     This type contains data fields available in Garmin GDB track points that cannot
 *     be represented in track points in GPX 1.1 instances.
 *
 *
 * <p>Java class for TrackPointExtension_t complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TrackPointExtension_t">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Temperature" type="{http://www.garmin.com/xmlschemas/GpxExtensions/v3}DegreesCelsius_t" minOccurs="0"/>
 *         &lt;element name="Depth" type="{http://www.garmin.com/xmlschemas/GpxExtensions/v3}Meters_t" minOccurs="0"/>
 *         &lt;element name="Extensions" type="{http://www.garmin.com/xmlschemas/GpxExtensions/v3}Extensions_t" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrackPointExtension_t", propOrder = {
    "temperature",
    "depth",
    "extensions"
})
public class TrackPointExtensionT {

    @XmlElement(name = "Temperature")
    protected Double temperature;
    @XmlElement(name = "Depth")
    protected Double depth;
    @XmlElement(name = "Extensions")
    protected ExtensionsT extensions;

    /**
     * Gets the value of the temperature property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * Sets the value of the temperature property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setTemperature(Double value) {
        this.temperature = value;
    }

    /**
     * Gets the value of the depth property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getDepth() {
        return depth;
    }

    /**
     * Sets the value of the depth property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setDepth(Double value) {
        this.depth = value;
    }

    /**
     * Gets the value of the extensions property.
     *
     * @return
     *     possible object is
     *     {@link ExtensionsT }
     *
     */
    public ExtensionsT getExtensions() {
        return extensions;
    }

    /**
     * Sets the value of the extensions property.
     *
     * @param value
     *     allowed object is
     *     {@link ExtensionsT }
     *
     */
    public void setExtensions(ExtensionsT value) {
        this.extensions = value;
    }

}
