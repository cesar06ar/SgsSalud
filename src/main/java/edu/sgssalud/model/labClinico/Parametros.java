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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 *
 * @author cesar
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Parametros.buscarTodosOrderanos",
    query = "select p FROM Parametros p "
            + "where p.examenLabClinico=:idExamen"
    + " ORDER BY p.posicion")
})
public class Parametros implements Serializable, Comparable<Parametros> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull(message = "ingrese un nombre")
    private String nombre;    
    private String valor;
    private String unidadMedida;
    private String categoria;
    private String valorReferenciaInf;
    private String valorReferenciaSup;
    private Integer posicion;
    private String tipoDato = "Casilla";

    @ManyToOne
    @JoinColumn(name = "examenLabClinico_id")
    private ExamenLabClinico examenLabClinico;

    public Parametros() {
    }

    public Parametros(String categoria, String nombre, String unidadMedida, String valorReferenciaInf, String valorReferenciaSup) {
        
        this.nombre = nombre;        
        this.unidadMedida = unidadMedida;
        this.categoria = categoria;
        this.valorReferenciaInf = valorReferenciaInf;
        this.valorReferenciaSup = valorReferenciaSup;        
    }    
    
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

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }  
    
    public ExamenLabClinico getExamenLabClinico() {
        return examenLabClinico;
    }

    public void setExamenLabClinico(ExamenLabClinico examenLabClinico) {
        this.examenLabClinico = examenLabClinico;
    }    

    public String getValorReferenciaInf() {
        return valorReferenciaInf;
    }

    public void setValorReferenciaInf(String valorReferenciaInf) {
        this.valorReferenciaInf = valorReferenciaInf;
    }

    public String getValorReferenciaSup() {
        return valorReferenciaSup;
    }

    public void setValorReferenciaSup(String valorReferenciaSup) {
        this.valorReferenciaSup = valorReferenciaSup;
    }  

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
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
        if (!(object instanceof Parametros)) {
            return false;
        }
        Parametros other = (Parametros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        //return "edu.sgssalud.model.labClinico.Parametros[ id=" + id + " ]";
        return getCategoria();
    }

    @Override
    public int compareTo(Parametros o) {
        return (int)(this.getPosicion() - o.getPosicion());
    }

}
