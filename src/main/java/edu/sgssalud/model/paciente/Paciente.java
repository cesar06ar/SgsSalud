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
package edu.sgssalud.model.paciente;

import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.util.FechasUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.solder.logging.Logger;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "Paciente")
@DiscriminatorValue(value = "PR")
@PrimaryKeyJoinColumn(name = "id")
public class Paciente extends BussinesEntity implements Serializable {

    private static Logger log = Logger.getLogger(Paciente.class);
    private static final long serialVersionUID = 1L;
    
    public enum Genero {

        MASCULINO(0), 
        FEMENINO(0);        
        private int genero;

        private Genero(int genero) {
            this.genero = genero;
        }

        public int getGenero() {
            return genero;
        }
        
    }
    
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String nombreUsuario;
    @NotEmpty
    @Column(nullable = false)
    private String clave;     
    @NotEmpty
    @Column(nullable = true)
    private String cedula;
    @NotEmpty
    @Column(nullable = true)
    private String apellidos;
    @NotEmpty
    @Column(nullable = true)
    private String nombres;
    @Column(length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private Byte[] foto;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNacimiento;    
    @Column(length = 25)
    private Integer edad;
    private String direccion;
    private String telefono;
    private String celular;
    @Email(message = "#{messages['MailBadFormat']}")
    @Index(name = "userEmailIndex")   //investigar
    @Column(nullable = false, length = 128, unique = false)
    private String email;
    @Enumerated(EnumType.STRING) //tipos de datos enumerados 
    @Column(nullable = false)  
    private Paciente.Genero genero;
    @Column(length = 50)
    private String nacionalidad;
    @Column(length = 50)
    private String profesion;
    @Column(length = 50)
    private String ocupacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_paciente_keys")
    private Set<String> identityKeys = new HashSet<String>();   //investigar mas... 
    
    @Column
    private boolean confirmed;
    @Column
    private boolean showBootcamp;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
     
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public Byte[] getFoto() {
        return foto;
    }

    public void setFoto(Byte[] foto) {
        this.foto = foto;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        if(this.fechaNacimiento != null){
            this.setEdad(FechasUtil.calcularEdad(fechaNacimiento));
        }
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public Set<String> getIdentityKeys() {
        return identityKeys;
    }

    public void setIdentityKeys(Set<String> identityKeys) {
        this.identityKeys = identityKeys;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isShowBootcamp() {
        return showBootcamp;
    }

    public void setShowBootcamp(boolean showBootcamp) {
        this.showBootcamp = showBootcamp;
    }
       
        
    @Override
    public String toString() {
        return "edu.sgssalud.model.paciente.Paciente[ "
        //return Paciente.class.getName()
                + "id=" + getId() + ","
                + "nombres=" + getNombres() + ","
                + "IdentityKeys=" + getIdentityKeys() + ","
                + " ]";
    }
//    public void vacio(){
//        //log.info("Verifica si ingresa a metodo...");
//    }
}
