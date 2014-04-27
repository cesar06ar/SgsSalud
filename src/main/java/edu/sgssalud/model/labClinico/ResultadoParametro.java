/*
 * Copyright 2014 cesar.
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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
public class ResultadoParametro implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    
    private String nombre;    
    private String valor;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resultadoExamLabCli_id")
    private ResultadoExamenLabClinico resultadoExamenLabClinico;
    @ManyToOne
    private Parametros parametro;
    
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

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }   

    public ResultadoExamenLabClinico getResultadoExamenLabClinico() {
        return resultadoExamenLabClinico;
    }

    public void setResultadoExamenLabClinico(ResultadoExamenLabClinico resultadoExamenLabClinico) {
        this.resultadoExamenLabClinico = resultadoExamenLabClinico;
    }    

    public Parametros getParametro() {
        return parametro;
    }

    public void setParametro(Parametros parametro) {
        this.parametro = parametro;
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
        if (!(object instanceof ResultadoParametro)) {
            return false;
        }
        ResultadoParametro other = (ResultadoParametro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.labClinico.ResultadoParametro[ id=" + id + " ]";
    }
    
}
