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
package edu.sgssalud.model.medicina;

import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.model.paciente.Paciente;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author cesar
 */
@Entity
public class FichaMedica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Long numeroFicha;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaApertura;
    @OneToOne
    @JoinColumn(name = "usuarioResponsable_id")
    private Profile usuarioResponsable;
    @OneToOne
    @JoinColumn(name = "paciente_id")  //nombre de la columna en la BD
    private Paciente paciente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(Long numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public Date getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(Date fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public Profile getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Profile usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
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
        if (!(object instanceof FichaMedica)) {
            return false;
        }
        FichaMedica other = (FichaMedica) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.medicina.FichaMedica[ id=" + id + " ]";
    }
}
