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
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

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
    // la fecha de apertura de la historiaClinica se guarda en (createOn)...

    @OneToOne
    @JoinColumn(name = "fichaMedica_id")
    private FichaMedica fichaMedica;

    @OneToMany(mappedBy = "historiaClinica", orphanRemoval = true)
    private List<ConsultaMedica> consultas = new ArrayList<ConsultaMedica>();

    
    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)        
    private List<Hc_Cie10> lista_enfcie10 = new ArrayList<Hc_Cie10>();

    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PedidoExamenLaboratorio> pedidosExamenLab = new ArrayList<PedidoExamenLaboratorio>();

    public FichaMedica getFichaMedica() {
        return fichaMedica;
    }

    public void setFichaMedica(FichaMedica fichaMedica) {
        this.fichaMedica = fichaMedica;
    }

    public List<ConsultaMedica> getConsultas() {
        Collections.sort(consultas);
        return consultas;
    }

    public void setConsultas(List<ConsultaMedica> consultas) {
        for (ConsultaMedica cm : consultas) {
            cm.setHistoriaClinica(this);
        }
        this.consultas = consultas;
    }

    public List<Hc_Cie10> getLista_enfcie10() {
        Collections.sort(lista_enfcie10);
        return lista_enfcie10;
    }

    public void setLista_enfcie10(List<Hc_Cie10> lista_enfcie10) {
        for (Hc_Cie10 enf : lista_enfcie10) {
            if(!this.lista_enfcie10.contains(enf)){
                enf.setHistoriaClinica(this);
            }            
        }
        this.lista_enfcie10 = lista_enfcie10;
    }

    public void agregarConsulta(ConsultaMedica c) {
        if (!this.consultas.contains(c)) {
            c.setHistoriaClinica(this);
            this.consultas.add(c);
        }
    }
    
    public void agregarEnfermedad(Hc_Cie10 enf) {
        if(!this.lista_enfcie10.contains(enf)){
            enf.setHistoriaClinica(this);
            this.lista_enfcie10.add(enf);
            System.out.println("AGREGO ENFERMEDAD  ");
        }        
    }
    
    public void borrarEnfermedad(Hc_Cie10 enf) {
        if(this.lista_enfcie10.contains(enf)){
            enf.setHistoriaClinica(null);
            enf.setEnf_cieE10(null);
            this.lista_enfcie10.remove(enf);
            System.out.println("AGREGO ENFERMEDAD  ");
        }        
    }

    public List<PedidoExamenLaboratorio> getPedidosExamenLab() {
        return pedidosExamenLab;
    }

    public void setPedidosExamenLab(List<PedidoExamenLaboratorio> pedidosExamenLab) {
        for (PedidoExamenLaboratorio p : pedidosExamenLab) {
            p.setHistoriaClinica(this);
        }
        this.pedidosExamenLab = pedidosExamenLab;
    }

    public void agregarPedidoExamen(PedidoExamenLaboratorio p) {
        if (!this.pedidosExamenLab.contains(p)) {
            p.setHistoriaClinica(this);
            this.pedidosExamenLab.add(p);
        }
    }

    @Override
    public String toString() {
        return this.getFichaMedica().getPaciente().getNombres() + " " + getFichaMedica().getPaciente().getNombres();
    }
}
