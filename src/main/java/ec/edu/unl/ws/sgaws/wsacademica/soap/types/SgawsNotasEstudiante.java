
package ec.edu.unl.ws.sgaws.wsacademica.soap.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cedula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id_carrera" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id_oferta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cedula",
    "idCarrera",
    "idOferta"
})
@XmlRootElement(name = "sgaws_notas_estudiante")
public class SgawsNotasEstudiante {

    @XmlElement(required = true)
    protected String cedula;
    @XmlElement(name = "id_carrera", required = true)
    protected String idCarrera;
    @XmlElement(name = "id_oferta", required = true)
    protected String idOferta;

    /**
     * Obtiene el valor de la propiedad cedula.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Define el valor de la propiedad cedula.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCedula(String value) {
        this.cedula = value;
    }

    /**
     * Obtiene el valor de la propiedad idCarrera.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCarrera() {
        return idCarrera;
    }

    /**
     * Define el valor de la propiedad idCarrera.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCarrera(String value) {
        this.idCarrera = value;
    }

    /**
     * Obtiene el valor de la propiedad idOferta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdOferta() {
        return idOferta;
    }

    /**
     * Define el valor de la propiedad idOferta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdOferta(String value) {
        this.idOferta = value;
    }

}
