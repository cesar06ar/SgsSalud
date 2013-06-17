/*
 * Copyright 2013 tania.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sgssalud.model.farmacia;

import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.paciente.Paciente;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import org.jboss.solder.logging.Logger;

/**
 *
 * @author tania
 */
@Entity
public class Medicamento extends BussinesEntity implements Serializable{
    
    private static Logger log = Logger.getLogger(Medicamento.class);
    private static final long serialVersionUID = 2L;
    
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaIngreso;
    private String casaComercialProveedora;
    private int numeroFactura;
    private String direccioncasaComercial;    
    private String nombreComercial;
    private String nombreGenerico;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaElaboracion;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCaducidad;

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getCasaComercialProveedora() {
        return casaComercialProveedora;
    }

    public void setCasaComercialProveedora(String casaComercialProveedora) {
        this.casaComercialProveedora = casaComercialProveedora;
    }

    public int getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(int numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getDireccioncasaComercial() {
        return direccioncasaComercial;
    }

    public void setDireccioncasaComercial(String direccioncasaComercial) {
        this.direccioncasaComercial = direccioncasaComercial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getNombreGenerico() {
        return nombreGenerico;
    }

    public void setNombreGenerico(String nombreGenerico) {
        this.nombreGenerico = nombreGenerico;
    }

    public Date getFechaElaboracion() {
        return fechaElaboracion;
    }

    public void setFechaElaboracion(Date fechaElaboracion) {
        this.fechaElaboracion = fechaElaboracion;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }
    
          
     @Override
    public String toString() {
        return Medicamento.class.getName()
                + "id=" + getId() + ","
                + "nombreComercial=" + getNombreComercial() + ","
                + "nombreGenerico" + getNombreGenerico() + ","
                + " ]";
    }
           
    
    
}
