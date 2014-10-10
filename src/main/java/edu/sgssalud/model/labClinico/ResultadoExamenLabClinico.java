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
package edu.sgssalud.model.labClinico;

import edu.sgssalud.model.profile.Profile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *RESULTADO DE EXAMEN DE LAB CLINICO
 * @author cesar
 */
@Entity
@Table(name = "ResultadoExamenLabClinico")
@NamedQueries(value = {
    @NamedQuery(name = "ResultadoExamenLabClinico.buscarPorId",
            query = "select r FROM ResultadoExamenLabClinico r "
            + " WHERE r.id = :Id"
            + " ORDER BY r.id")
})
public class ResultadoExamenLabClinico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRealizacion;
    //private Double valorResultado;
    //private String observacion;

    @ManyToOne()
    private ExamenLabClinico ExamenLab;

    @ManyToOne()
    private PedidoExamenLaboratorio pedidoExamenLab;

    @OneToMany(mappedBy = "resultadoExamenLabClinico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@OrderBy("parametro.posicion ASC")
    private List<ResultadoParametro> resultadosParametros = new ArrayList<ResultadoParametro>();

    @ManyToOne
    @JoinColumn(name = "responsableEntrega_id")
    private Profile ResponsableEntrega;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPersistent() {
        return getId() != null;
    }

    public Date getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(Date fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

//    public Double getValorResultado() {
//        return valorResultado;
//    }
//
//    public void setValorResultado(Double valorResultado) {
//        this.valorResultado = valorResultado;
//    }
    public ExamenLabClinico getExamenLab() {
        return ExamenLab;
    }

    public void setExamenLab(ExamenLabClinico ExamenLab) {
        this.ExamenLab = ExamenLab;
    }

    public PedidoExamenLaboratorio getPedidoExamenLab() {
        return pedidoExamenLab;
    }

    public void setPedidoExamenLab(PedidoExamenLaboratorio pedidoExamenLab) {
        this.pedidoExamenLab = pedidoExamenLab;
    }

    public List<ResultadoParametro> getResultadosParametros() {
        Collections.sort(resultadosParametros);
        return resultadosParametros;
    }

    public void setResultadosParametros(List<ResultadoParametro> resultadosParametros) {
        for (ResultadoParametro r : resultadosParametros) {
            r.setResultadoExamenLabClinico(this);
        }
        this.resultadosParametros = resultadosParametros;
    }

    public Profile getResponsableEntrega() {
        return ResponsableEntrega;
    }

    public void setResponsableEntrega(Profile ResponsableEntrega) {
        this.ResponsableEntrega = ResponsableEntrega;
    }

    public void agregarValoresResultados(List<Parametros> pl) {
        System.out.println("INGRESO A AGREGAR VALORES  _________0 ");
        if (!pl.isEmpty()) {
            //System.out.println("INGRESO A AGREGAR VALORES  _________1");
            ResultadoParametro r;
            for (Parametros p : pl) {
                r = new ResultadoParametro();
                r.setNombre(p.getNombre());
                r.setValor(p.getValor());
                r.setResultadoExamenLabClinico(this);
                r.setValorReferenciaInf(p.getValorReferenciaInf());
                r.setValorReferenciaSup(p.getValorReferenciaSup());
                r.setCategoria(p.getCategoria());
                r.setUnidadMedida(p.getUnidadMedida());
                r.setParametro(p);
                resultadosParametros.add(r);
            }
            System.out.println(" AGREGAR VALORES exito _________1");
        }
    }

    public void agregarResultadoParametros(ResultadoParametro rp) {
        if (!resultadosParametros.contains(rp)) {
            rp.setResultadoExamenLabClinico(this);
            resultadosParametros.add(rp);
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ResultadoExamenLabClinico)) {
            return false;
        }
        ResultadoExamenLabClinico other = (ResultadoExamenLabClinico) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.labClinico.ResultadoExamenLaboratorioClinico[ id=" + id
                + " Nombre " + getExamenLab().getName()
                + " fecha " + getFechaRealizacion()
                + " estado " + getPedidoExamenLab().getEstado()
                + " patametros " + getResultadosParametros().size()
                + " ]";
    }
}
