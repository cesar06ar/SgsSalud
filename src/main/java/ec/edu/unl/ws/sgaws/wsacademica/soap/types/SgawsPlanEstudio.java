
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
 *         &lt;element name="id_paralelo" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "idParalelo"
})
@XmlRootElement(name = "sgaws_plan_estudio")
public class SgawsPlanEstudio {

    @XmlElement(name = "id_paralelo", required = true)
    protected String idParalelo;

    /**
     * Obtiene el valor de la propiedad idParalelo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdParalelo() {
        return idParalelo;
    }

    /**
     * Define el valor de la propiedad idParalelo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdParalelo(String value) {
        this.idParalelo = value;
    }

}
