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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
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
    private Double valorResultado;
    //private String observacion;

    @ManyToOne()
    private ExamenLabClinico ExamenLab;
    
    @ManyToOne()
    private PedidoExamenLaboratorio pedidoExamenLab;
    
    @OneToMany(mappedBy = "resultadoExamenLabClinico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ResultadoParametro> resultadosParametros = new ArrayList<ResultadoParametro>();

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

    public Double getValorResultado() {
        return valorResultado;
    }

    public void setValorResultado(Double valorResultado) {
        this.valorResultado = valorResultado;
    }

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
        return resultadosParametros;
    }

    public void setResultadosParametros(List<ResultadoParametro> resultadosParametros) {
        for (ResultadoParametro r : resultadosParametros) {
            r.setResultadoExamenLabClinico(this);
        }
        this.resultadosParametros = resultadosParametros;
    }
    
    public void agregarValoresResultados(List<Parametros> pl){
        System.out.println("INGRESO A AGREGAR VALORES  _________0 ");
        if(!pl.isEmpty()){
            System.out.println("INGRESO A AGREGAR VALORES  _________1");
            ResultadoParametro r ;
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
    
    public void agregarResultadoParametros(ResultadoParametro rp){
        if(!resultadosParametros.contains(rp)){
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ResultadoExamenLabClinico)) {
            return false;
        }
        ResultadoExamenLabClinico other = (ResultadoExamenLabClinico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.labClinico.ResultadoExamenLaboratorioClinico[ id=" + id + " ]";
    }
}
