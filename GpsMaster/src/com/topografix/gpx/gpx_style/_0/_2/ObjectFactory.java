//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.02.02 at 04:06:17 PM CET
//


package com.topografix.gpx.gpx_style._0._2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.topografix.gpx.gpx_style._0._2 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Fill_QNAME = new QName("http://www.topografix.com/GPX/gpx_style/0/2", "fill");
    private final static QName _Align_QNAME = new QName("http://www.topografix.com/GPX/gpx_style/0/2", "align");
    private final static QName _Text_QNAME = new QName("http://www.topografix.com/GPX/gpx_style/0/2", "text");
    private final static QName _VerticalAlign_QNAME = new QName("http://www.topografix.com/GPX/gpx_style/0/2", "vertical-align");
    private final static QName _Line_QNAME = new QName("http://www.topografix.com/GPX/gpx_style/0/2", "line");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.topografix.gpx.gpx_style._0._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TextType }
     *
     */
    public TextType createTextType() {
        return new TextType();
    }

    /**
     * Create an instance of {@link FillType }
     *
     */
    public FillType createFillType() {
        return new FillType();
    }

    /**
     * Create an instance of {@link LineType }
     *
     */
    public LineType createLineType() {
        return new LineType();
    }

    /**
     * Create an instance of {@link FontFamilyType }
     *
     */
    public FontFamilyType createFontFamilyType() {
        return new FontFamilyType();
    }

    /**
     * Create an instance of {@link FontType }
     *
     */
    public FontType createFontType() {
        return new FontType();
    }

    /**
     * Create an instance of {@link DashType }
     *
     */
    public DashType createDashType() {
        return new DashType();
    }

    /**
     * Create an instance of {@link ExtensionsType }
     *
     */
    public ExtensionsType createExtensionsType() {
        return new ExtensionsType();
    }

    /**
     * Create an instance of {@link DasharrayType }
     *
     */
    public DasharrayType createDasharrayType() {
        return new DasharrayType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FillType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.topografix.com/GPX/gpx_style/0/2", name = "fill")
    public JAXBElement<FillType> createFill(FillType value) {
        return new JAXBElement<FillType>(_Fill_QNAME, FillType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlignType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.topografix.com/GPX/gpx_style/0/2", name = "align")
    public JAXBElement<AlignType> createAlign(AlignType value) {
        return new JAXBElement<AlignType>(_Align_QNAME, AlignType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TextType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.topografix.com/GPX/gpx_style/0/2", name = "text")
    public JAXBElement<TextType> createText(TextType value) {
        return new JAXBElement<TextType>(_Text_QNAME, TextType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerticalAlignType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.topografix.com/GPX/gpx_style/0/2", name = "vertical-align")
    public JAXBElement<VerticalAlignType> createVerticalAlign(VerticalAlignType value) {
        return new JAXBElement<VerticalAlignType>(_VerticalAlign_QNAME, VerticalAlignType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.topografix.com/GPX/gpx_style/0/2", name = "line")
    public JAXBElement<LineType> createLine(LineType value) {
        return new JAXBElement<LineType>(_Line_QNAME, LineType.class, null, value);
    }

}
