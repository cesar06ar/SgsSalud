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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
    // la fecha de apertura de la historiaClinica se guarda en (createOn)
    
    //private List <AntecedentesPersonales> antecedentesPersonales;    
           
    @OneToOne
    @JoinColumn(name = "fichaMedica_id")
    private FichaMedica fichaMedica;
    
    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConsultaMedica> consultas = new ArrayList<ConsultaMedica>();
   
    public FichaMedica getFichaMedica() {
        return fichaMedica;
    }

    public void setFichaMedica(FichaMedica fichaMedica) {
        this.fichaMedica = fichaMedica;
    }

    public List<ConsultaMedica> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<ConsultaMedica> consultas) {
        for (ConsultaMedica cm : consultas) {
            cm.setHistoriaClinica(this);
        }
        this.consultas = consultas;
    }   
    
    public void agregarConsulta(ConsultaMedica c){
        if(!this.consultas.contains(c)){
            c.setHistoriaClinica(this);
            this.consultas.add(c);
        }
        
    }
}
