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
package edu.sgssalud.model.odontologia;

import edu.sgssalud.model.medicina.*;
import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.labClinico.PedidoExamenLaboratorio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
@Table(name = "FichaOdontologica")
@DiscriminatorValue(value = "FC")
@PrimaryKeyJoinColumn(name = "id")
public class FichaOdontologica extends BussinesEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    // la fecha de apertura de la historiaClinica se guarda en (createOn)
    //private List <AntecedentesPersonales> antecedentesPersonales;    
    @OneToOne
    @JoinColumn(name = "fichaMedica_id")
    private FichaMedica fichaMedica;
    @OneToOne
    private Odontograma odontogramaInicial;
    @OneToOne
    private Odontograma odontograma;
    @OneToMany(mappedBy = "fichaOdontologica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConsultaOdontologica> consultas = new ArrayList<ConsultaOdontologica>();
    
    @OneToMany(mappedBy = "fichaOdontologica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoExamenLaboratorio> pedidosExamenLab = new ArrayList<PedidoExamenLaboratorio>();

    public FichaMedica getFichaMedica() {
        return fichaMedica;
    }

    public void setFichaMedica(FichaMedica fichaMedica) {
        this.fichaMedica = fichaMedica;
    }

    public List<ConsultaOdontologica> getConsultas() {
        Collections.sort(consultas);
        return consultas;
    }

    public void setConsultas(List<ConsultaOdontologica> consultas) {
        for (ConsultaOdontologica cm : consultas) {
            cm.setFichaOdontologica(this); // falta:  agregar a q ficha pertenece  
        }
        this.consultas = consultas;
    }

    public Odontograma getOdontogramaInicial() {
        return odontogramaInicial;
    }

    public void setOdontogramaInicial(Odontograma odontogramaInicial) {
        this.odontogramaInicial = odontogramaInicial;
    }

    public Odontograma getOdontograma() {
        return odontograma;
    }

    public void setOdontograma(Odontograma odontograma) {
        this.odontograma = odontograma;
    }

    public void agregarConsulta(ConsultaOdontologica c) {
        if (!this.consultas.contains(c)) {
            c.setFichaOdontologica(this);
            this.consultas.add(c);
        }
    }
    
    public List<PedidoExamenLaboratorio> getPedidosExamenLab() {
        return pedidosExamenLab;
    }

    public void setPedidosExamenLab(List<PedidoExamenLaboratorio> pedidosExamenLab) {
        for (PedidoExamenLaboratorio p : pedidosExamenLab) {
            p.setFichaOdontologica(this);
        }
        this.pedidosExamenLab = pedidosExamenLab;
    }

//    public void agregarPedidoExamen(PedidoExamenLaboratorio p) {
//        if (!this.pedidosExamenLab.contains(p)) {
//            p.setHistoriaClinica(this);
//            this.pedidosExamenLab.add(p);
//        }
//    }
}
