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

package edu.sgssalud.model.farmacia;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "Receta_Medicamento")
public class Receta_Medicamento implements Serializable, Comparable<Receta_Medicamento> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private int cantidad;
    
    @ManyToOne()
    private Receta receta;    
    
    @ManyToOne()
    private Medicamento medicamento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }    

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }    
    
    public boolean isPersistent(){
        return getId() != null;
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
        if (!(object instanceof Receta_Medicamento)) {
            return false;
        }
        Receta_Medicamento other = (Receta_Medicamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.farmacia.Receta_Medicamento[" 
                + " id = " + id 
                + " cantidad = " + cantidad 
                + " medicamento_id = " + medicamento.getId()
                + " ]";
    }

    @Override
    public int compareTo(Receta_Medicamento o) {
        return (int)(this.getId() - o.getId());
    }
    
}
