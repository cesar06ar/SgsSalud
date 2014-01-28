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
package edu.sgssalud.model.odontologia;

import java.io.Serializable;
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
import javax.persistence.Transient;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "Diente")
@NamedQueries(value = {
    @NamedQuery(name = "Diente.buscarPorOdontogramaYPosicion",
            query = "select d FROM Diente d "
            + " WHERE d.odontograma.id = :odontogramaId"
            + " AND d.posicion = :posicion"            
            + " ORDER BY d.id")})
public class Diente implements Serializable, Comparable<Diente> {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;    
    private String nombre;
    private int posicion;  
    private int cuadrante;
    @Transient
    private boolean select;
    @ManyToOne
    @JoinColumn(name = "odontograma_id")
    private Odontograma odontograma;
    
    @OneToMany(mappedBy = "diente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)    
    private List<Tratamiento> tratamientos;
    
    public Diente() {
    }

    public Diente(String nombre, int posicion, int cuadrante) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.cuadrante = cuadrante;        
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

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getCuadrante() {
        return cuadrante;
    }

    public void setCuadrante(int cuadrante) {
        this.cuadrante = cuadrante;
    }   

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    } 
        
    public Odontograma getOdontograma() {
        return odontograma;
    }

    public void setOdontograma(Odontograma odontograma) {
        this.odontograma = odontograma;
    }    

    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos(List<Tratamiento> tratamientos) {
        for (Tratamiento t : tratamientos) {
            t.setDiente(this);
        }
        this.tratamientos = tratamientos;
    }
    
    public void agregarTratamiento(Tratamiento t){
        if(!tratamientos.contains(t)){
            t.setDiente(this);
            tratamientos.add(t);
        }
    }
    
    public boolean contineTratamiento(String nombreTratamiento){
        for (Tratamiento t : tratamientos) {
            if(t.getNombre().equals(nombreTratamiento)){
                return true;
            }        
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean isPersistent() {
        return getId() != null;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Diente)) {
            return false;
        }
        Diente other = (Diente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.odontologia.Diente[ "
                + "id=" + id + ","
                + "nombre=" + nombre + ","
                + "posicion=" + posicion + ","
                + "cuadrante=" + cuadrante + ","                
                + " ]";
    }

    @Override
    public int compareTo(Diente o) {
        return (int)o.getPosicion() - this.getPosicion();
    }
    
}
