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
package edu.sgssalud.model.odontologia;

import edu.sgssalud.model.medicina.*;
import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.farmacia.Receta;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author tania
 */
@Entity
@Table(name = "ConsultaOdontologica")
@DiscriminatorValue(value = "CO")
@PrimaryKeyJoinColumn(name = "id")
public class ConsultaOdontologica extends BussinesEntity implements Serializable, Comparable<ConsultaOdontologica> {

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaConsulta;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaConsulta;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date tiempoConsulta;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaProximaVisita; 
    @Column(length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private byte[] radiografiaDental;
    private String motivoConsulta;
    private String diagnostico;
    private String diagnosticoRadiografico;
    private String observacion;
    private String observacionExamenDentario;
    private String observacionExamenFisico;
    @ManyToOne
    @JoinColumn(name = "fichaOdontologica_id")
    private FichaOdontologica fichaOdontologica;
    @OneToOne
    @JoinColumn(name = "signosVitales_id")
    private SignosVitales signosVitales;
    @OneToMany(mappedBy = "consultaOdontologica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Receta> recetas = new ArrayList<Receta>();
//    @OneToMany(mappedBy = "consultaOdontologica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Tratamiento> tratamientoDientes = new ArrayList<Tratamiento>();

    public Date getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(Date fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public Date getHoraConsulta() {
        return horaConsulta;
    }

    public void setHoraConsulta(Date horaConsulta) {
        this.horaConsulta = horaConsulta;
    }

    public Date getTiempoConsulta() {
        return tiempoConsulta;
    }

    public void setTiempoConsulta(Date tiempoConsulta) {
        this.tiempoConsulta = tiempoConsulta;
    }

    public byte[] getRadiografiaDental() {
        return radiografiaDental;
    }

    public void setRadiografiaDental(byte[] radiografiaDental) {
        this.radiografiaDental = radiografiaDental;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }    

    public Date getFechaProximaVisita() {
        return fechaProximaVisita;
    }

    public void setFechaProximaVisita(Date fechaProximaVisita) {
        this.fechaProximaVisita = fechaProximaVisita;
    } 
        
    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }    
    
    public String getDiagnosticoRadiografico() {
        return diagnosticoRadiografico;
    }

    public void setDiagnosticoRadiografico(String diagnosticoRadiografico) {
        this.diagnosticoRadiografico = diagnosticoRadiografico;
    }

    public FichaOdontologica getFichaOdontologica() {
        return fichaOdontologica;
    }

    public void setFichaOdontologica(FichaOdontologica fichaOdontologica) {
        this.fichaOdontologica = fichaOdontologica;
    }

    public SignosVitales getSignosVitales() {
        return signosVitales;
    }

    public void setSignosVitales(SignosVitales signosVitales) {
        this.signosVitales = signosVitales;
    }

    public List<Receta> getRecetas() {
        return recetas;
    }

    public void setRecetas(List<Receta> recetas) {
        for (Receta r : recetas) {
            r.setConsultaOdontologica(this);
        }
        this.recetas = recetas;
    }

//    public List<Tratamiento> getTratamientoDientes() {
//        return tratamientoDientes;
//    }
//
//    public void setTratamientoDientes(List<Tratamiento> tratamientoDientes) {
//        for (Tratamiento td : tratamientoDientes) {
//            td.setConsultaOdontologica(this);
//        }
//        this.tratamientoDientes = tratamientoDientes;
//    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    } 

    public String getObservacionExamenDentario() {
        return observacionExamenDentario;
    }

    public void setObservacionExamenDentario(String observacionExamenDentario) {
        this.observacionExamenDentario = observacionExamenDentario;
    }

    public String getObservacionExamenFisico() {
        return observacionExamenFisico;
    }

    public void setObservacionExamenFisico(String observacionExamenFisico) {
        this.observacionExamenFisico = observacionExamenFisico;
    } 
        
    public void agregarReceta(Receta r) {
        if (!this.recetas.contains(r)) {
            r.setConsultaOdontologica(this);
            this.recetas.add(r);
        }
    }

//    public void agregarTratamiento(Tratamiento t) {
//        if (!this.tratamientoDientes.contains(t)) {
//            t.setConsultaOdontologica(this);
//            this.tratamientoDientes.add(t);
//        }
//    }

    @Override
    public int compareTo(ConsultaOdontologica o) {
        return (int)(o.getId() - this.getId());
    }
}
