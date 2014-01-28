/*
 * Copyright 2013 cesar.
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
package edu.sgssalud.model.odontologia;

import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.servicios.Servicio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "Tratamiento")
@NamedQueries(value = {
    @NamedQuery(name = "Tratamiento.buscarPorOdontogramaYConsulta",
            query = "select t FROM Tratamiento t JOIN t.diente d"
            + " WHERE d.odontograma.id = :odontogramaId"
            //+ " AND t.consultaOdontologica_id = :consultaOdontId"            
            + " ORDER BY t.id"),
    @NamedQuery(name = "Tratamiento.buscarPorDienteYNombre",
            query = "select t FROM Tratamiento t "
            + " WHERE t.diente.id = :dienteId"
            + " AND t.nombre = :nombreTratamiento"
            + " ORDER BY t.id")})
public class Tratamiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRealizacion;
    private String nombre;
    private String procedimiento;
    @Transient
    private String ruta;
    /*
     private boolean c1;
     private boolean c2;
     private boolean c3;
     private boolean c4;
     private boolean c5;*/
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tratamiento_cuadrante")
    @Column(length = 25)
    private Set<String> cuadrante = new HashSet<String>();

    @ManyToOne()
    @JoinColumn(name = "diente_id")
    private Diente diente;

    @ManyToOne()
    @JoinColumn(name = "consultaOdontologica_id")
    private ConsultaOdontologica consultaOdontologica;

//    @ManyToOne()
//    @JoinColumn(name = "servicioDisponible_id")
//    private Servicio servicioDisponible;
    public Tratamiento() {

    }

    public Tratamiento(String nombre, String ruta) {
        this.nombre = nombre;
        this.ruta = ruta;
//        this.c1 = c1;
//        this.c2 = c2;
//        this.c3 = c3;
//        this.c4 = c4;
//        this.c5 = c5;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(Date fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public Diente getDiente() {
        return diente;
    }

    public void setDiente(Diente diente) {
        this.diente = diente;
    }

    public ConsultaOdontologica getConsultaOdontologica() {
        return consultaOdontologica;
    }

    public void setConsultaOdontologica(ConsultaOdontologica consultaOdontologica) {
        this.consultaOdontologica = consultaOdontologica;
    }

    public Set<String> getCuadrante() {
        return cuadrante;
    }

    public void setCuadrante(Set<String> cuadrante) {
        this.cuadrante = cuadrante;
    }

    public List<String> getCuandrantes() {
        if (!getCuadrante().isEmpty()) {
            return new ArrayList<String>(getCuadrante());
        } else {
            return null;
        }
    }
//    public Servicio getServicioDisponible() {
//        return servicioDisponible;
//    }
//
//    public void setServicioDisponible(Servicio servicioDisponible) {
//        this.servicioDisponible = servicioDisponible;
//    }    

    public boolean isPersistent() {
        return getId() != null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tratamiento)) {
            return false;
        }
        Tratamiento other = (Tratamiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.odontologia.Tratamiento[ "
                + "id=" + getId() + ","
                + "nombre=" + getNombre() + ","
                + "fecha" + getFechaRealizacion() + ","
                + " ]";
    }

}
