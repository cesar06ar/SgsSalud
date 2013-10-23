
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
 *         &lt;element name="id_periodo" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "idPeriodo"
})
@XmlRootElement(name = "sgaws_ofertas_academicas")
public class SgawsOfertasAcademicas {

    @XmlElement(name = "id_periodo", required = true)
    protected String idPeriodo;

    /**
     * Obtiene el valor de la propiedad idPeriodo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPeriodo() {
        return idPeriodo;
    }

    /**
     * Define el valor de la propiedad idPeriodo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPeriodo(String value) {
        this.idPeriodo = value;
    }

}
