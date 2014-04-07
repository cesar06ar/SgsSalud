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
import edu.sgssalud.util.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "enfCIE10_HC", joinColumns = {
        @JoinColumn(name = "historiaClin_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "enfermedadCIE10_id", referencedColumnName = "id")})
    private List<EnfermedadCIE10> enfermedadesCIE10 = new ArrayList<EnfermedadCIE10>();

    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    public List<EnfermedadCIE10> getEnfermedadesCIE10() {
        Collections.sort(enfermedadesCIE10);
        return enfermedadesCIE10;
    }

    public void setEnfermedadesCIE10(List<EnfermedadCIE10> enfermedadesCIE10) {
        this.enfermedadesCIE10 = enfermedadesCIE10;
    }

    public void agregarConsulta(ConsultaMedica c) {
        if (!this.consultas.contains(c)) {
            c.setHistoriaClinica(this);
            this.consultas.add(c);
        }
    }

    public void agregarEnfermedad(EnfermedadCIE10 enfermedad) {
        if (!this.enfermedadesCIE10.contains(enfermedad)) {
            enfermedadesCIE10.add(enfermedad);
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
}
