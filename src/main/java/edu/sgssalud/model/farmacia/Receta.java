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
import edu.sgssalud.model.medicina.ConsultaMedica;
import edu.sgssalud.model.medicina.HistoriaClinica;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import org.jboss.solder.logging.Logger;

/**
 *
 * @author tania
 */
@Entity
public class Receta implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private static Logger log = Logger.getLogger(Receta.class);
    private static final long serialVersionUID = 3L;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;
    private String estado;  //el estado puede ser emitido y entregado 
    
    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Medicamento> medicaciones = new ArrayList<Medicamento>();   
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<Medicamento> getMedicaciones() {
        return medicaciones;
    }

    public void setMedicaciones(List<Medicamento> medicaciones) {        
        for (Medicamento m : medicaciones) {
            m.setReceta(this);
        }
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

    public void agregarMedicamento(Medicamento m) {
        if (!medicaciones.contains(m));
        m.setReceta(this);
        medicaciones.add(m);
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
         if (!(obj instanceof Receta)) {
            return false;
        }
        Receta other = (Receta) obj;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.Property[ "
                + "id=" + id + ","
                + "indicaciones=" + indicaciones + ","                
                + " ]";
    }
}
