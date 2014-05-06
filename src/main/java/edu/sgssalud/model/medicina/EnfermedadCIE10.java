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
package edu.sgssalud.model.medicina;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "EnfermedadCIE10")
public class EnfermedadCIE10 implements Serializable, Comparable<EnfermedadCIE10> {

    private static final long serialVersionUID = -1;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String codigo;
    @Column(length = 400)
    private String nombre;
    private String descripcion;

    public EnfermedadCIE10() {
    }

    public EnfermedadCIE10(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public boolean isPersistent() {
        return getId() != null;
    }

    @Override
    public int compareTo(EnfermedadCIE10 o) {
        return (int) (this.getId() - o.getId());
    }

    @Override
    public String toString() {
        return "edu.edu.sgssalud.model.medicina.EnfermedadCIE10[ "
                + "id=" + getId() + ","
                + "codigo=" + this.codigo + ","
                + "nombre" + this.nombre + ","
                + " ]";
    }
}
