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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author cesar
 */
@Entity
public class SignosVitales implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaActual;
    private Double peso = 0.0;
    private Double presionArterialSistolica;
    private Double presionArterialDiastolica;
    private int pulso;
    private Double frecuenciaRespiratoria;
    private Double talla  = 0.0;
    private Double temperatura;
    //FALTA: mas signos vitales
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaActual() {
        return fechaActual;
    }

    public void setFechaActual(Date fechaActual) {
        this.fechaActual = fechaActual;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }    

    public Double getPresionArterialSistolica() {
        return presionArterialSistolica;
    }

    public void setPresionArterialSistolica(Double presionArterialSistolica) {
        this.presionArterialSistolica = presionArterialSistolica;
    }

    public Double getPresionArterialDiastolica() {
        return presionArterialDiastolica;
    }

    public void setPresionArterialDiastolica(Double presionArterialDiastolica) {
        this.presionArterialDiastolica = presionArterialDiastolica;
    }

    public int getPulso() {
        return pulso;
    }

    public void setPulso(int pulso) {
        this.pulso = pulso;
    }

    public Double getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(Double frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public Double getTalla() {
        return talla;
    }

    public void setTalla(Double talla) {
        this.talla = talla;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
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
        if (!(object instanceof SignosVitales)) {
            return false;
        }
        SignosVitales other = (SignosVitales) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.medicina.SignosVitales[ "
                + "id=" + id + ","
                + "fechaActual=" + fechaActual + ","                
                + "peso=" + peso + ","                
                + " ]";
    }
    
}
