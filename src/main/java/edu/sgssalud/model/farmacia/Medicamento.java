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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import org.jboss.solder.logging.Logger;

/**
 *
 * @author tania
 */
@Entity
@NamedQueries(value = {
    @NamedQuery(name = "Medicamento.buscarPorParametro",
    query = "select e from Medicamento e where"
    + " LOWER(e.nombreComercial) like lower(concat('%',:clave,'%')) OR"
    + " LOWER(e.nombreGenerico) like lower(concat('%',:clave,'%')) OR"
    + " LOWER(e.casaComercialProveedora) like lower(concat('%',:clave,'%'))"
    + " ORDER BY e.nombreComercial"),
    @NamedQuery(name = "Medicamento.buscarPorNumero",
    query = "select e from Medicamento e where"
    + " e.numeroFactura = :clave"),
    @NamedQuery(name = "Medicamento.buscarPorFecha",
    query = "select e from Medicamento e where"
    + " e.fechaIngreso=:clave  OR"
    + " e.fechaElaboracion=:clave")})
public class Medicamento extends BussinesEntity implements Serializable {

    private static Logger log = Logger.getLogger(Medicamento.class);
    private static final long serialVersionUID = 2L;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaIngreso;
    private String casaComercialProveedora;
    private int numeroFactura;
    private String dirCasaComercial;
    private String nombreComercial;
    private String nombreGenerico;
    private String referencia;
    private int unidades;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaElaboracion;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCaducidad;

    public Medicamento() {
        //this.fechaIngreso = new Date();
    }

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

    public String getDirCasaComercial() {
        return dirCasaComercial;
    }

    public void setDirCasaComercial(String dirCasaComercial) {
        this.dirCasaComercial = dirCasaComercial;
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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
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
        return "edu.sgssalud.model.farmacia.Medicamento[ "
                + "id=" + getId() + ","
                + "nombreComercial=" + getNombreComercial() + ","
                + "nombreGenerico" + getNombreGenerico() + ","
                + " ]";
    }
}
