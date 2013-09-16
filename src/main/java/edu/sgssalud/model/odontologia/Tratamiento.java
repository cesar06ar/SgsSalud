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
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author cesar
 */
@Entity
public class Tratamiento implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRelizacion;    
    private String nombre;
    private String procedimiento;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaProximaVisita;    
    
    @OneToMany()
    @JoinColumn(name = "diente_id")
    private Diente diente;
    
    @ManyToOne
    @JoinColumn(name = "consultaOdontologica_id")
    private ConsultaOdontologica consultaOdontologica;  
    
    @ManyToOne
    @JoinColumn(name = "consultaMedica_id")
    private ConsultaMedica consultaMedica;
        
    @OneToOne()
    @JoinColumn(name = "servicioDisponible_id")
    private Servicio servicioDisponible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaRelizacion() {
        return fechaRelizacion;
    }

    public void setFechaRelizacion(Date fechaRelizacion) {
        this.fechaRelizacion = fechaRelizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public Date getFechaProximaVisita() {
        return fechaProximaVisita;
    }

    public void setFechaProximaVisita(Date fechaProximaVisita) {
        this.fechaProximaVisita = fechaProximaVisita;
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

    public Servicio getServicioDisponible() {
        return servicioDisponible;
    }

    public void setServicioDisponible(Servicio servicioDisponible) {
        this.servicioDisponible = servicioDisponible;
    }

    public ConsultaMedica getConsultaMedica() {
        return consultaMedica;
    }

    public void setConsultaMedica(ConsultaMedica consultaMedica) {
        this.consultaMedica = consultaMedica;
    }
    
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
        return "edu.sgssalud.model.odontologia.Tratamiento[ id=" + id + " ]";
    }
    
}
