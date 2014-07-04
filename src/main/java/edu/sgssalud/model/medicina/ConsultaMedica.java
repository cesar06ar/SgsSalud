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
package edu.sgssalud.model.medicina;

import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.farmacia.Receta;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.util.FechasUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "ConsultaMedica")
@DiscriminatorValue(value = "CM")
@PrimaryKeyJoinColumn(name = "id")
@NamedQueries(value = {
    @NamedQuery(name = "ConsultaMedica.buscarPorFechas",
            query = "select e from ConsultaMedica e where"
            + " e.fechaConsulta BETWEEN :inicio AND :fin"            
            + " ORDER BY e.id"),
    @NamedQuery(name = "ConsultaMedica.buscarSignosVitalesPorRagnoFechasYsexo",
            query = "select c from ConsultaMedica c where"
            + " c.createdOn between :fInicio and :fFin "
            + " and c.signosVitales.responsable.id = :responsableId"
            + " and c.historiaClinica.fichaMedica.paciente.genero = :genero")
    })
public class ConsultaMedica extends BussinesEntity implements Serializable, Comparable<ConsultaMedica> {

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaConsulta;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaConsulta;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date tiempoConsulta;
    private String motivoConsulta;
    private String enfermedadActual;
    private String observacionRevisionOS;
    private String observacionExamenFisico;
    private String examenFisico;
    private String diagnostico;
    private String tratamiento;
    private String observacionConsulta;

    public ConsultaMedica() {
        fechaConsulta = new Date();
        horaConsulta = new Date();        
    }
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "historiaClinica_id")
    private HistoriaClinica historiaClinica;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "signosVitales_id")
    private SignosVitales signosVitales;
    @OneToMany(mappedBy = "consultaMedica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Receta> recetas = new ArrayList<Receta>();
        
    
    public Date getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(Date fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public Date getTiempoConsulta() {
        return tiempoConsulta;
    }

    public Date getHoraConsulta() {
        return horaConsulta;
    }

    public void setHoraConsulta(Date horaConsulta) {
        this.horaConsulta = horaConsulta;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    public void setTiempoConsulta(Date tiempoConsulta) {                
        this.tiempoConsulta = tiempoConsulta;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getEnfermedadActual() {
        return enfermedadActual;
    }

    public void setEnfermedadActual(String enfermedadActual) {
        this.enfermedadActual = enfermedadActual;
    }

    public String getExamenFisico() {
        return examenFisico;
    }

    public void setExamenFisico(String examenFisico) {
        this.examenFisico = examenFisico;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getObservacionConsulta() {
        return observacionConsulta;
    }

    public void setObservacionConsulta(String observacionConsulta) {
        this.observacionConsulta = observacionConsulta;
    }

    public SignosVitales getSignosVitales() {
        return signosVitales;
    }

    public void setSignosVitales(SignosVitales signosVitales) {
        this.signosVitales = signosVitales;
    }

    public String getObservacionRevisionOS() {
        return observacionRevisionOS;
    }

    public void setObservacionRevisionOS(String observacionRevisionOS) {
        this.observacionRevisionOS = observacionRevisionOS;
    }

    public String getObservacionExamenFisico() {
        return observacionExamenFisico;
    }

    public void setObservacionExamenFisico(String observacionExamenFisico) {
        this.observacionExamenFisico = observacionExamenFisico;
    }

    public List<Receta> getRecetas() {
        return recetas;
    }

    public void setRecetas(List<Receta> recetas) {
        for (Receta r : recetas) {
            r.setConsultaMedica(this);
        }
        this.recetas = recetas;
    }

    public void agregarReceta(Receta r) {
        if (!this.recetas.contains(r)) {
            r.setConsultaMedica(this);
            this.recetas.add(r);
        }
    }
   
    
    @Override
    public int compareTo(ConsultaMedica o) {
//        if (this.getFechaConsulta().before(o.getFechaConsulta())) {
//            return 1;
//        }else if(this.horaConsulta.before(o.getHoraConsulta())){
//            return 1;
//        }        
        return (int)(o.getId() - this.getId());
    }     
    
}
