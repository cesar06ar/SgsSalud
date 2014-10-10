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
import edu.sgssalud.util.FechasUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author tania
 */
@Entity
@Table(name = "Medicamento")
@DiscriminatorValue(value = "Med")
@PrimaryKeyJoinColumn(name = "id")
@NamedQueries(value = {
    @NamedQuery(name = "Medicamento.buscarPorParametro",
            query = "select e from Medicamento e where"
            + " LOWER(e.nombreComercial) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(e.nombreGenerico) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(e.casaComercialProveedora) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(e.presentacion) like lower(concat('%',:clave,'%'))"
            + " ORDER BY e.nombreComercial"),
    @NamedQuery(name = "Medicamento.buscarPorNumero",
            query = "select e from Medicamento e where"
            + " e.numeroFactura = :clave"),
    @NamedQuery(name = "Medicamento.buscarPorFecha",
            query = "select e from Medicamento e where"
            + " e.fechaIngreso=:clave  OR"
            + " e.fechaElaboracion=:clave")})
public class Medicamento extends BussinesEntity implements Serializable, Comparable<Medicamento> {

    //private static Logger log = Logger.getLogger(Medicamento.class);
    private static final long serialVersionUID = 2L;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaIngreso;
    private String casaComercialProveedora;
    private int numeroFactura;
    private String dirCasaComercial;
    private String nombreComercial;
    private String nombreGenerico;
    private String referencia;
    private String presentacion;
    private int unidades;
    private int cantidadIngreso;
    private boolean generico;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaElaboracion;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCaducidad;
    private String dosificacion;
    @Transient
    private String alerta;

    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "medicamento", fetch = FetchType.LAZY)
    //private List<Receta_Medicamento> listaRecetaMedicamento = new ArrayList<Receta_Medicamento>();
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

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public int getCantidadIngreso() {
        return cantidadIngreso;
    }

    public void setCantidadIngreso(int cantidadIngreso) {
        this.cantidadIngreso = cantidadIngreso;
    }

    public boolean isGenerico() {
        return generico;
    }

    public void setGenerico(boolean generico) {
        this.generico = generico;
    }

    public String getDosificacion() {
        return dosificacion;
    }

    public void setDosificacion(String dosificacion) {
        this.dosificacion = dosificacion;
    }

    public String getAlerta() {
        Date now = Calendar.getInstance().getTime();
        int dias = FechasUtil.getFechaLimite(now, fechaCaducidad);
        if (dias >= 0 && dias < 90) {
            return "POR CADUCARSE";
        } else if (now.after(fechaCaducidad)) {
            return "CADUCADO";
        } else {
            return "";
        }
    }

    public void setAlerta(String alerta) {
        this.alerta = alerta;
    }
    /*
     public List<Receta_Medicamento> getListaRecetaMedicamento() {
     return listaRecetaMedicamento;
     }

     public void setListaRecetaMedicamento(List<Receta_Medicamento> listaRecetaMedicamento) {
     this.listaRecetaMedicamento = listaRecetaMedicamento;
     }

     public List<Receta> getRecetas() {
     List<Receta> recetas = new ArrayList<Receta>();
     for (Receta_Medicamento rm : getListaRecetaMedicamento()) {
     recetas.add(rm.getReceta());
     }
     return recetas;
     }
     */

    @Override
    public String toString() {
        return "edu.sgssalud.model.farmacia.Medicamento[ "
                + "id=" + getId() + ","
                + "nombreComercial=" + getNombreComercial() + ","
                + "nombreGenerico" + getNombreGenerico() + ","
                + " ]";
    }

    @Override
    public int compareTo(Medicamento o) {
        return (int) (this.getId() - o.getId());
    }
}
