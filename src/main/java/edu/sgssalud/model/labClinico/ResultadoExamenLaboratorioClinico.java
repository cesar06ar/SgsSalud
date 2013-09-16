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
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author cesar
 */
@Entity
public class ResultadoExamenLaboratorioClinico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRealizacion;
    private String observacion;
    
    @OneToMany(mappedBy = "resultadoExamLabClinico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TipoAnalisis>  tiposAnalisis;

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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }    

    public List<TipoAnalisis> getTiposAnalisis() {
        return tiposAnalisis;
    }

    public void setTiposAnalisis(List<TipoAnalisis> tiposAnalisis) {
        for (TipoAnalisis ta : tiposAnalisis) {
            ta.setResultadoExamLabClinico(this);
        }
        this.tiposAnalisis = tiposAnalisis;
    }
    
    public void agregarTipoAnalisis(TipoAnalisis tp){
        if(!this.tiposAnalisis.contains(tp)){
            tp.setResultadoExamLabClinico(this);
            this.tiposAnalisis.add(tp);
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
        if (!(object instanceof ResultadoExamenLaboratorioClinico)) {
            return false;
        }
        ResultadoExamenLaboratorioClinico other = (ResultadoExamenLaboratorioClinico) object;
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
