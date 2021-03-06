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

import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.odontologia.ConsultaOdontologica;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author tania
 */
@Entity
@Table(name = "Receta")
@NamedQueries(value = {
    @NamedQuery(name = "Receta.buscarPorId",
            query = "select r FROM Receta r "
            + " WHERE r.id = :recetaId"
            + " ORDER BY r.id"),
    @NamedQuery(name = "Receta.buscarPorCriterio",
            query = "select r FROM Receta r where"
            + " LOWER(r.numvalue) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(r.estado) like lower(concat('%',:clave,'%')) OR"
            + " r.paciente.nombres = :clave OR"
            + " r.paciente.apellidos = :clave"
            + " ORDER BY r.id"),
    @NamedQuery(name = "Receta.buscarPorFecha",
            query = "select r FROM Receta r where"
            + " r.fechaEmision = :clave OR"
            + " r.fechaEntrega = :clave"
            + " ORDER BY r.id")
})
public class Receta implements Serializable, Comparable<Receta> {

    private static final long serialVersionUID = 3L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    //private static Logger log = Logger.getLogger(Receta.class);    
    private Long numero;
    private String numvalue;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaEmision;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaEntrega;
    private String estado;  //el estado puede ser emitido y entregado 

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receta", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Receta_Medicamento> listaRecetaMedicamento = new ArrayList<Receta_Medicamento>();

    private String medicaciones;
    private String indicaciones;

    @ManyToOne(optional = true)
    @JoinColumn(name = "responsableEmision_id")
    private Profile responsableEmision;

    @ManyToOne
    @JoinColumn(name = "responsableEntrega_id")
    private Profile ResponsableEntrega;

    //el usuario responsable de emitir la receta se carga de la consulta medica
    @ManyToOne
    @JoinColumn(name = "consultaMedica_id")
    private ConsultaMedica consultaMedica;

    @ManyToOne
    @JoinColumn(name = "consultaOdontologica_id")
    private ConsultaOdontologica consultaOdontologica;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getMedicaciones() {
        return medicaciones;
    }

    public void setMedicaciones(String medicaciones) {
        this.medicaciones = medicaciones;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public ConsultaMedica getConsultaMedica() {
        return consultaMedica;
    }

    public void setConsultaMedica(ConsultaMedica consultaMedica) {
        this.consultaMedica = consultaMedica;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Profile getResponsableEntrega() {
        return ResponsableEntrega;
    }

    public void setResponsableEntrega(Profile ResponsableEntrega) {
        this.ResponsableEntrega = ResponsableEntrega;
    }

    public ConsultaOdontologica getConsultaOdontologica() {
        return consultaOdontologica;
    }

    public void setConsultaOdontologica(ConsultaOdontologica consultaOdontologica) {
        this.consultaOdontologica = consultaOdontologica;
    }

    public Profile getResponsableEmision() {
        return responsableEmision;
    }

    public void setResponsableEmision(Profile responsableEmision) {
        this.responsableEmision = responsableEmision;
    }

    public List<Receta_Medicamento> getListaRecetaMedicamento() {
        return listaRecetaMedicamento;
    }

    public void setListaRecetaMedicamento(List<Receta_Medicamento> listaRecetaMedicamento) {
        for (Receta_Medicamento rm : listaRecetaMedicamento) {
            rm.setReceta(this);
        }
        this.listaRecetaMedicamento = listaRecetaMedicamento;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getNumvalue() {
        return numvalue;
    }

    public void setNumvalue(String numvalue) {
        this.numvalue = numvalue;
    }

    public void agregarRecetaMedicamento(Receta_Medicamento rm) {
        if (!listaRecetaMedicamento.contains(rm)) {
            rm.setReceta(this);
            listaRecetaMedicamento.add(rm);
        }
    }

    public List<Medicamento> getMedicamentos() {
        List<Medicamento> medicamentos = new ArrayList<Medicamento>();
        for (Receta_Medicamento rm : getListaRecetaMedicamento()) {
            medicamentos.add(rm.getMedicamento());
        }
        return medicamentos;
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
    public boolean equals(final Object obj) {
//        if (!(obj instanceof Receta)) {
//            return false;
//        }
//        Receta other = (Receta) obj;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.farmacia.Receta[ "
                + "id=" + id + ","
                + "indicaciones=" + indicaciones + ","
                + "indicaciones=" + medicaciones + ","
                + " ]";
    }

    @Override
    public int compareTo(Receta o) {
        return (int) (this.getId() - o.getId());
    }
}
