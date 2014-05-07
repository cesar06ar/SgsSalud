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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "ExamenLabClinico")
public class ExamenLabClinico extends PersistentObject<ExamenLabClinico> implements Comparable<ExamenLabClinico>{
            
    private String categorias;
    private Double costo;
    @Enumerated(EnumType.STRING) //anotación tipos de datos enumerados 
    @Column(nullable = false)
    private ExamenLabClinico.Tipo tipo;
    
    @OneToMany(mappedBy = "examenLabClinico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Parametros> parametros = new ArrayList<Parametros>();
    
    @Transient
    private boolean select;

    public ExamenLabClinico() {
    }

    public ExamenLabClinico(String nombre, String codigo, String categorias, Double costo, Tipo tipo, Date fecha) {
        this.setName(nombre);
        this.setCode(codigo);                       
        this.categorias = categorias;
        this.costo = costo;
        this.tipo = tipo;
        this.setCreatedOn(fecha);
        this.setActivationTime(fecha);
        this.setLastUpdate(fecha);        
    }
    
    
    
    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }    
    
         
    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    } 

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }   

    public String getCategorias() {
        return categorias;
    }

    public void setCategorias(String categorias) {
        this.categorias = categorias;
    }   
    
    public List<Parametros> getParametros() {
        return parametros;
    }

    public void setParametros(List<Parametros> parametros) {
        for (Parametros p : parametros) {
            p.setExamenLabClinico(this);
        }
        this.parametros = parametros;
    }   
    
    public void agregarParametro(Parametros p){
        if(!parametros.contains(p)){
            p.setExamenLabClinico(this);
            parametros.add(p);
        }
    }

//    public String getValorReferencia() {
//        return valorReferencia;
//    }
//
//    public void setValorReferencia(String valorReferencia) {
//        this.valorReferencia = valorReferencia;
//    }  
        
    @Override
    public int compareTo(ExamenLabClinico o) {
        return (int)(this.getId() - o.getId());
    }
        
    public enum Tipo {

        HEMATOLÓGICOS(0),
        BIOQUIMÍCO_Y_ENZIMÁTICOS(1),
        ELECTROLITOS(2),
        MARCADORES_TUMORALES(3),
        HORMONAS(4),
        INMUNOLÓGICOS(5),
        LÍQUIDOS_BIOLÓGICOS(6),
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
        
//        private void setTipo(int tipo) {
//            this.tipo = tipo;
//        }

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
