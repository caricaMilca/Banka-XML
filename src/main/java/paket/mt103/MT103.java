//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.26 at 06:20:48 PM CEST 
//


package paket.mt103;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MT103 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MT103">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idPoruke" type="{http://paket/tipovi}slovo50"/>
 *         &lt;element name="swifKodBankeDuznika" type="{http://paket/tipovi}slovo8"/>
 *         &lt;element name="obracunskiRacunBankeDuznika" type="{http://paket/tipovi}slovo18"/>
 *         &lt;element name="swiftKodBankePoverioca" type="{http://paket/tipovi}slovo8"/>
 *         &lt;element name="obracunskiRacunBankePoverioca" type="{http://paket/tipovi}slovo18"/>
 *         &lt;element name="duznik" type="{http://paket/tipovi}string255"/>
 *         &lt;element name="svrhaPlacanja" type="{http://paket/tipovi}string255"/>
 *         &lt;element name="primalac" type="{http://paket/tipovi}string255"/>
 *         &lt;element name="datumNaloga" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="datumValute" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="racunDuznika" type="{http://paket/tipovi}slovo18"/>
 *         &lt;element name="modelZaduzenja" type="{http://paket/tipovi}broj2"/>
 *         &lt;element name="pozivNaBrojZaduzenja" type="{http://paket/tipovi}slovo20"/>
 *         &lt;element name="racunPoverioca" type="{http://paket/tipovi}slovo18"/>
 *         &lt;element name="modelOdobrenja" type="{http://paket/tipovi}broj2"/>
 *         &lt;element name="pozivNaBrojOdobrenja" type="{http://paket/tipovi}slovo20"/>
 *         &lt;element name="iznos" type="{http://paket/tipovi}decimalni15-2"/>
 *         &lt;element name="sifraValute" type="{http://paket/tipovi}slovo3"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MT103", propOrder = {
    "idPoruke",
    "swifKodBankeDuznika",
    "obracunskiRacunBankeDuznika",
    "swiftKodBankePoverioca",
    "obracunskiRacunBankePoverioca",
    "duznik",
    "svrhaPlacanja",
    "primalac",
    "datumNaloga",
    "datumValute",
    "racunDuznika",
    "modelZaduzenja",
    "pozivNaBrojZaduzenja",
    "racunPoverioca",
    "modelOdobrenja",
    "pozivNaBrojOdobrenja",
    "iznos",
    "sifraValute"
})
public class MT103 {

    @XmlElement(required = true)
    protected String idPoruke;
    @XmlElement(required = true)
    protected String swifKodBankeDuznika;
    @XmlElement(required = true)
    protected String obracunskiRacunBankeDuznika;
    @XmlElement(required = true)
    protected String swiftKodBankePoverioca;
    @XmlElement(required = true)
    protected String obracunskiRacunBankePoverioca;
    @XmlElement(required = true)
    protected String duznik;
    @XmlElement(required = true)
    protected String svrhaPlacanja;
    @XmlElement(required = true)
    protected String primalac;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar datumNaloga;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar datumValute;
    @XmlElement(required = true)
    protected String racunDuznika;
    @XmlElement(required = true)
    protected BigInteger modelZaduzenja;
    @XmlElement(required = true)
    protected String pozivNaBrojZaduzenja;
    @XmlElement(required = true)
    protected String racunPoverioca;
    @XmlElement(required = true)
    protected BigInteger modelOdobrenja;
    @XmlElement(required = true)
    protected String pozivNaBrojOdobrenja;
    @XmlElement(required = true)
    protected BigDecimal iznos;
    @XmlElement(required = true)
    protected String sifraValute;

    /**
     * Gets the value of the idPoruke property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPoruke() {
        return idPoruke;
    }

    /**
     * Sets the value of the idPoruke property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPoruke(String value) {
        this.idPoruke = value;
    }

    /**
     * Gets the value of the swifKodBankeDuznika property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwifKodBankeDuznika() {
        return swifKodBankeDuznika;
    }

    /**
     * Sets the value of the swifKodBankeDuznika property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwifKodBankeDuznika(String value) {
        this.swifKodBankeDuznika = value;
    }

    /**
     * Gets the value of the obracunskiRacunBankeDuznika property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObracunskiRacunBankeDuznika() {
        return obracunskiRacunBankeDuznika;
    }

    /**
     * Sets the value of the obracunskiRacunBankeDuznika property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObracunskiRacunBankeDuznika(String value) {
        this.obracunskiRacunBankeDuznika = value;
    }

    /**
     * Gets the value of the swiftKodBankePoverioca property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwiftKodBankePoverioca() {
        return swiftKodBankePoverioca;
    }

    /**
     * Sets the value of the swiftKodBankePoverioca property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwiftKodBankePoverioca(String value) {
        this.swiftKodBankePoverioca = value;
    }

    /**
     * Gets the value of the obracunskiRacunBankePoverioca property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObracunskiRacunBankePoverioca() {
        return obracunskiRacunBankePoverioca;
    }

    /**
     * Sets the value of the obracunskiRacunBankePoverioca property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObracunskiRacunBankePoverioca(String value) {
        this.obracunskiRacunBankePoverioca = value;
    }

    /**
     * Gets the value of the duznik property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDuznik() {
        return duznik;
    }

    /**
     * Sets the value of the duznik property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDuznik(String value) {
        this.duznik = value;
    }

    /**
     * Gets the value of the svrhaPlacanja property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSvrhaPlacanja() {
        return svrhaPlacanja;
    }

    /**
     * Sets the value of the svrhaPlacanja property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSvrhaPlacanja(String value) {
        this.svrhaPlacanja = value;
    }

    /**
     * Gets the value of the primalac property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimalac() {
        return primalac;
    }

    /**
     * Sets the value of the primalac property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimalac(String value) {
        this.primalac = value;
    }

    /**
     * Gets the value of the datumNaloga property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatumNaloga() {
        return datumNaloga;
    }

    /**
     * Sets the value of the datumNaloga property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatumNaloga(XMLGregorianCalendar value) {
        this.datumNaloga = value;
    }

    /**
     * Gets the value of the datumValute property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatumValute() {
        return datumValute;
    }

    /**
     * Sets the value of the datumValute property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatumValute(XMLGregorianCalendar value) {
        this.datumValute = value;
    }

    /**
     * Gets the value of the racunDuznika property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRacunDuznika() {
        return racunDuznika;
    }

    /**
     * Sets the value of the racunDuznika property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRacunDuznika(String value) {
        this.racunDuznika = value;
    }

    /**
     * Gets the value of the modelZaduzenja property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getModelZaduzenja() {
        return modelZaduzenja;
    }

    /**
     * Sets the value of the modelZaduzenja property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setModelZaduzenja(BigInteger value) {
        this.modelZaduzenja = value;
    }

    /**
     * Gets the value of the pozivNaBrojZaduzenja property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPozivNaBrojZaduzenja() {
        return pozivNaBrojZaduzenja;
    }

    /**
     * Sets the value of the pozivNaBrojZaduzenja property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPozivNaBrojZaduzenja(String value) {
        this.pozivNaBrojZaduzenja = value;
    }

    /**
     * Gets the value of the racunPoverioca property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRacunPoverioca() {
        return racunPoverioca;
    }

    /**
     * Sets the value of the racunPoverioca property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRacunPoverioca(String value) {
        this.racunPoverioca = value;
    }

    /**
     * Gets the value of the modelOdobrenja property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getModelOdobrenja() {
        return modelOdobrenja;
    }

    /**
     * Sets the value of the modelOdobrenja property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setModelOdobrenja(BigInteger value) {
        this.modelOdobrenja = value;
    }

    /**
     * Gets the value of the pozivNaBrojOdobrenja property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPozivNaBrojOdobrenja() {
        return pozivNaBrojOdobrenja;
    }

    /**
     * Sets the value of the pozivNaBrojOdobrenja property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPozivNaBrojOdobrenja(String value) {
        this.pozivNaBrojOdobrenja = value;
    }

    /**
     * Gets the value of the iznos property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getIznos() {
        return iznos;
    }

    /**
     * Sets the value of the iznos property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setIznos(BigDecimal value) {
        this.iznos = value;
    }

    /**
     * Gets the value of the sifraValute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSifraValute() {
        return sifraValute;
    }

    /**
     * Sets the value of the sifraValute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSifraValute(String value) {
        this.sifraValute = value;
    }

}