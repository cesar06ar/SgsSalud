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

package edu.sgssalud.model.medicina;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "HC_CIE10")
public class Hc_Cie10 implements Serializable, Comparable<Hc_Cie10>{    
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "historiaClinica_id")
    private HistoriaClinica historiaClinica;
    
    @ManyToOne
    @JoinColumn(name = "cie10_id")
    private EnfermedadCIE10 enf_cieE10;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    public EnfermedadCIE10 getEnf_cieE10() {
        return enf_cieE10;
    }

    public void setEnf_cieE10(EnfermedadCIE10 enf_cieE10) {
        this.enf_cieE10 = enf_cieE10;
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
        if (!(object instanceof Hc_Cie10)) {
            return false;
        }
        Hc_Cie10 other = (Hc_Cie10) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.medicina.Hc_Cie10[ id=" 
                + id                 
                +"  "+historiaClinica.getId()
                +"  "+enf_cieE10.getId()
                + " ]";
    }

    @Override
    public int compareTo(Hc_Cie10 o) {
        return (int) (this.getId()-o.getId());
    }
    
}
