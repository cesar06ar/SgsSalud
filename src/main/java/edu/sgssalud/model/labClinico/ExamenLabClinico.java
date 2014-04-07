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

import edu.sgssalud.model.PersistentObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "ExamenLabClinico")
public class ExamenLabClinico extends PersistentObject<ExamenLabClinico> implements Comparable<ExamenLabClinico>{
    
    private Double valorReferencia;
    private Double valorReferencia1;
    private Double costo;
    @Enumerated(EnumType.STRING) //anotación tipos de datos enumerados 
    @Column(nullable = false)
    private ExamenLabClinico.Tipo tipo;

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }    
    
    public Double getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(Double valorReferencia) {
        this.valorReferencia = valorReferencia;
    }    

    public Double getValorReferencia1() {
        return valorReferencia1;
    }

    public void setValorReferencia1(Double valorReferencia1) {
        this.valorReferencia1 = valorReferencia1;
    }    

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    } 

    @Override
    public int compareTo(ExamenLabClinico o) {
        return (int)(this.getId() - o.getId());
    }
        
    public enum Tipo {

        HEMATOLOGICOS(0),
        BIOQUIMICO_Y_ENZIMATICOS(1),
        ELECTROLITOS(2),
        MARCADORES_TUMORALES(3),
        HORMONAS(4),
        INMUNOLOGICOS(5),
        LIQUIDOS_BIOLOGICOS(6),
        ORINA(7),
        HECES(8),
        SECRECIONES(9),
        OTROS(10);
        
        private int tipo;

        private Tipo(int tipo) {
            this.tipo = tipo;
        }

        public int getTipo() {
            return tipo;
        }

    }
    
    @Override
    public String toString(){
       return "edu.sgssalud.model.labClinico.examenLabClinico[ "
                + "id= " + getId() + ","
                + "codigo= " + getCode() + ","
                + "nombre= " + getName() + ","
                + "tipo= " + getTipo() + ","
                + " ]"; 
    }
    
}
