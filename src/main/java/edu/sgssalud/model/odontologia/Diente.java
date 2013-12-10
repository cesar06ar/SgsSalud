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
import javax.persistence.OneToMany;

/**
 *
 * @author cesar
 */
@Entity
public class Diente implements Serializable, Comparable<Diente> {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    
    private String nombre;
    private int posicion;  
    private int cuadrante;
    private String rutaIcon;
    @ManyToOne
    @JoinColumn(name = "odontograma_id")
    private Odontograma odontograma;
    
    @OneToMany(mappedBy = "consultaOdontologica", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)    
    private List<Tratamiento> tratamientos;
    
    public Diente() {
    }

    public Diente(String nombre, int posicion, int cuadrante, String rutaIcon) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.cuadrante = cuadrante;
        this.rutaIcon = rutaIcon;
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

    public String getRutaIcon() {
        return rutaIcon;
    }

    public void setRutaIcon(String rutaIcon) {
        this.rutaIcon = rutaIcon;
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
                + "rutaIcon=" + rutaIcon + ","
                + " ]";
    }

    @Override
    public int compareTo(Diente o) {
        return (int)o.getPosicion() - this.getPosicion();
    }
    
}
