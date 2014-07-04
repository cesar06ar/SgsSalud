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

import edu.sgssalud.model.PersistentObject;
import edu.sgssalud.model.config.Setting;
import edu.sgssalud.model.paciente.Paciente;
import edu.sgssalud.model.profile.Profile;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "Servicio")
@NamedQueries(value = {
    @NamedQuery(name = "ServiciosEnfermeria.buscarPorRagnoFechasYsexo",
            query = "select fm from Servicio fm where"
            + " fm.createdOn between :fInicio and :fFin "
            + " and fm.name = :servicio"
            + " and fm.paciente.genero = :genero")})
public class Servicio extends PersistentObject<Servicio> implements Serializable {

    private static final long serialVersionUID = 1L;
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;

    private String categoria;

//    private String descripcion;
//    //private String rutaImg;
//    @Temporal(javax.persistence.TemporalType.DATE)
//    private Date fechaLimiteVigencia;
//    private boolean todasZonas;
    @OneToOne
    @JoinColumn(name = "responsable_id")
    private Profile responsable;     //Quien presta este servicio

    @ManyToOne
    @JoinColumn(name = "paciente_id")  //nombre de la columna en la BD
    private Paciente paciente;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Profile getResponsable() {
        return responsable;
    }

    public void setResponsable(Profile responsable) {
        this.responsable = responsable;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.servicios.Servicio[ "
                + "id=" + getId() + ","
                + "nombre=" + getName() + ","
                + "descripcion=" + getDescription() + ","
                + " ]";
    }
}
