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
package edu.sgssalud.model.labClinico;

import edu.sgssalud.model.medicina.HistoriaClinica;
import edu.sgssalud.model.odontologia.FichaOdontologica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "PedidoExamenLaboratorio")
@NamedQueries(value = {
    @NamedQuery(name = "PedidoExamenLab.buscarPorId",
            query = "select r FROM PedidoExamenLaboratorio r "
            + " WHERE r.id = :Id"            
            + " ORDER BY r.id"),
    @NamedQuery(name = "PedidoExamenLab.buscarPorHistoriaCLinica",
            query = "select r FROM PedidoExamenLaboratorio r "
            + " WHERE r.historiaClinica.id = :Id"            
            + " ORDER BY r.id"),
        @NamedQuery(name = "PedidoExamenLab.buscarPorFecha",
            query = "select r FROM PedidoExamenLaboratorio r "
            + " WHERE r.fechaPedido=:fecha")
    })
public class PedidoExamenLaboratorio implements Serializable, Comparable<PedidoExamenLaboratorio> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String estado;  // El estado puede ser Realizado o Pendiente Nuevo 
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPedido;
    private String observacion;
    private String codigoMuestra; //

    @ManyToOne(optional = true)
    @JoinColumn(name = "responsableEmision_id")
    private Profile responsableEmision;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "historiaCLinica_id")
    private HistoriaClinica historiaClinica;

    @ManyToOne
    @JoinColumn(name = "fichaOdontologica_id")
    private FichaOdontologica fichaOdontologica;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getCodigoMuestra() {
        return codigoMuestra;
    }

    public void setCodigoMuestra(String codigoMuestra) {
        this.codigoMuestra = codigoMuestra;
    }

    public Profile getResponsableEmision() {
        return responsableEmision;
    }

    public void setResponsableEmision(Profile responsableEmision) {
        this.responsableEmision = responsableEmision;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    public FichaOdontologica getFichaOdontologica() {
        return fichaOdontologica;
    }

    public void setFichaOdontologica(FichaOdontologica fichaOdontologica) {
        this.fichaOdontologica = fichaOdontologica;
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

        if (!(object instanceof PedidoExamenLaboratorio)) {
            return false;
        }
        PedidoExamenLaboratorio other = (PedidoExamenLaboratorio) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.labClinico.pedidoExamenLaboratorio[ "
                + "id= " + getId() + ","
                + "codigo= " + getFechaPedido() + ","
                + "nombre= " + getEstado() + ","
                + "obs= " + getObservacion() + ","
                + " ]";
    }

    @Override
    public int compareTo(PedidoExamenLaboratorio o) {
        return (int) (o.getId() - this.getId());
    }

}
