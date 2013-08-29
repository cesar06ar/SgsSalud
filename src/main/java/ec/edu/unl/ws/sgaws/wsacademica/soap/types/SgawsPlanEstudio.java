
package ec.edu.unl.ws.sgaws.wsacademica.soap.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the idParalelo property.
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
     * Sets the value of the idParalelo property.
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
