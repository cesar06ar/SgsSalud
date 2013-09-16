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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author cesar
 */
@Entity
public class TipoAnalisis implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    @Enumerated(EnumType.STRING) //anotación tipos de datos enumerados 
    @Column(nullable = false)
    private TipoAnalisis.Categoria categoria;
    private Double valorReferencia;
    private Double resultado;
    
    @ManyToOne()
    @JoinColumn(name = "pedidoExamLab_id")
    private PedidoExamenLaboratorio pedidoExamenLaboratorio;
    
    @ManyToOne
    @JoinColumn(name = "resultadoExamLabClinico_id")
    private ResultadoExamenLaboratorioClinico resultadoExamLabClinico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Double getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(Double valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public Double getResultado() {
        return resultado;
    }

    public void setResultado(Double resultado) {
        this.resultado = resultado;
    }

    public boolean isPersistent() {
        return getId() != null;
    }

    public PedidoExamenLaboratorio getPedidoExamenLaboratorio() {
        return pedidoExamenLaboratorio;
    }

    public void setPedidoExamenLaboratorio(PedidoExamenLaboratorio pedidoExamenLaboratorio) {
        this.pedidoExamenLaboratorio = pedidoExamenLaboratorio;
    }

    public ResultadoExamenLaboratorioClinico getResultadoExamLabClinico() {
        return resultadoExamLabClinico;
    }

    public void setResultadoExamLabClinico(ResultadoExamenLaboratorioClinico resultadoExamLabClinico) {
        this.resultadoExamLabClinico = resultadoExamLabClinico;
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
        if (!(object instanceof TipoAnalisis)) {
            return false;
        }
        TipoAnalisis other = (TipoAnalisis) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.labClinico.TipoAnalisis[ id=" + id + " ]";
    }

    public enum Categoria {

        HEMATOLOGICOS(0),
        BIOQUIMICO_ENZIMATICOS(1),
        ELECTROLITOS(2),
        HORMONAS(3),
        INMUNOLOGICOS(4),
        LIQUIDOS_BIOLOGICOS(5),
        ORINA(6),
        HECES(7),
        SECRECIONES(8),
        OTROS(9);
        private int categoria;

        private Categoria(int categoria) {
            this.categoria = categoria;
        }

        public int getCategoria() {
            return categoria;
        }
    }
}
