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
package edu.sgssalud.model.servicios;

import edu.sgssalud.model.profile.Profile;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author cesar
 */
@Entity
public class Servicio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String categoria;
    private String nombre;
    private String descripcion;
    private String rutaImg;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaLimiteVigencia;
    private boolean todasZonas;
    @OneToOne
    @JoinColumn(name = "responsable_id")
    private Profile responsable;     //Quien presta este servicio

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaImg() {
        return rutaImg;
    }

    public void setRutaImg(String rutaImg) {
        this.rutaImg = rutaImg;
    }
    
    public Date getFechaLimiteVigencia() {
        return fechaLimiteVigencia;
    }

    public void setFechaLimiteVigencia(Date fechaLimiteVigencia) {
        this.fechaLimiteVigencia = fechaLimiteVigencia;
    }

    public boolean isTodasZonas() {
        return todasZonas;
    }

    public void setTodasZonas(boolean todasZonas) {
        this.todasZonas = todasZonas;
    }
    
    public Profile getResponsable() {
        return responsable;
    }

    public void setResponsable(Profile responsable) {
        this.responsable = responsable;
    }

    public boolean isPersistent() {
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
        if (!(object instanceof Servicio)) {
            return false;
        }
        Servicio other = (Servicio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.servicios.Servicio[ "
                + "id=" + id + ","
                + "nombre=" + nombre + ","
                + "descripcion=" + descripcion + ","
                + " ]";
    }
}
