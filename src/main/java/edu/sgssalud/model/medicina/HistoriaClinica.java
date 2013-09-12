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

import edu.sgssalud.model.BussinesEntity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "HistoriaClinica")
@DiscriminatorValue(value = "HC")
@PrimaryKeyJoinColumn(name = "id")
public class HistoriaClinica extends BussinesEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaApertura;
    //private List <AntecedentesPersonales> antecedentesPersonales;
    private String observacionAntecedentesPersonales;
    private String observacionAntecedentesFamiliares;

    public Date getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(Date fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public String getObservacionAntecedentesPersonales() {
        return observacionAntecedentesPersonales;
    }

    public void setObservacionAntecedentesPersonales(String observacionAntecedentesPersonales) {
        this.observacionAntecedentesPersonales = observacionAntecedentesPersonales;
    }

    public String getObservacionAntecedentesFamiliares() {
        return observacionAntecedentesFamiliares;
    }

    public void setObservacionAntecedentesFamiliares(String observacionAntecedentesFamiliares) {
        this.observacionAntecedentesFamiliares = observacionAntecedentesFamiliares;
    }
    
    
}
