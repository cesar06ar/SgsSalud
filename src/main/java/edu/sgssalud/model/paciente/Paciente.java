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
import edu.sgssalud.model.Photos;
import edu.sgssalud.util.FechasUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jboss.solder.logging.Logger;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "Paciente", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@DiscriminatorValue(value = "Pac")
@PrimaryKeyJoinColumn(name = "id")
@NamedQueries(value = {
    @NamedQuery(name = "Paciente.buscarPorParametro",
            query = "select p from Paciente p where"
            + " LOWER(p.nombres) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(p.apellidos) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(p.cedula) like lower(concat('%',:clave,'%'))"
            + " ORDER BY p.apellidos"),
    @NamedQuery(name = "Paciente.buscarPorParametrosTodos",
            query = "select p from Paciente p where"
            + " LOWER(p.cedula) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(p.nombres) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(p.apellidos) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(p.email) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(p.genero) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(p.nacionalidad) like lower(concat('%',:clave,'%')) OR"
            + " LOWER(p.direccion) like lower(concat('%',:clave,'%'))"            
            + " ORDER BY p.apellidos"),
    @NamedQuery(name = "Paciente.buscarPorCedula",
            query = "select p from Paciente p where"
            + " LOWER(p.apellidos) like lower(concat('%',:clave,'%'))"),
    @NamedQuery(name = "Paciente.buscarPorEdad",
            query = "select p from Paciente p where"
            + " p.edad = :clave"),
    @NamedQuery(name = "Paciente.buscarPorFechaNacimiento",
            query = "select p from Paciente p where"
            + " p.fechaNacimiento = :clave")})
public class Paciente extends BussinesEntity implements Serializable, Comparable<Paciente> {

    private static Logger log = Logger.getLogger(Paciente.class);
    private static final long serialVersionUID = 1L;

    public enum Genero {

        MASCULINO(0),
        FEMENINO(1);
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
    //@Lob  no usar si tiene postgres    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "foto_id")
    private Photos foto;
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
    @Enumerated(EnumType.STRING) //anotación tipos de datos enumerados 
    @Column(nullable = false)
    private Paciente.Genero genero;
    @Column(length = 50)
    private String nacionalidad;
    @Column(length = 50)
    private String profesion;
    @Column(length = 50)
    private String ocupacion;

    //Datos Academicos de Paciente Universitario
    private String area;
    private String carrera;
    private String Modulo;
    private String paralelo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_paciente_keys")
    private Set<String> identityKeys = new HashSet<String>();   //investigar mas... 

    @Column
    private boolean confirmed;
    @Column
    private boolean showBootcamp;

    private String tipoEstudiante;

    @Transient
    private String rutaFoto;

    public Paciente() {
    }

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
        this.clave = new BasicPasswordEncryptor().encryptPassword(clave);
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

    public Photos getFoto() {
        return foto;
    }

    public void setFoto(Photos foto) {
        this.foto = foto;
    }   

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        if (this.fechaNacimiento != null) {
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getModulo() {
        return Modulo;
    }

    public void setModulo(String Modulo) {
        this.Modulo = Modulo;
    }

    public String getParalelo() {
        return paralelo;
    }

    public void setParalelo(String paralelo) {
        this.paralelo = paralelo;
    }

    public String getTipoEstudiante() {
        return tipoEstudiante;
    }

    public void setTipoEstudiante(String tipoEstudiante) {
        this.tipoEstudiante = tipoEstudiante;
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

    public String cargarFoto() {
        if (getFoto() != null) {
            return getFoto().toString();
        } else {
            return "/resources/images/paciente.png";
        }
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        if (rutaFoto != null) {
            this.rutaFoto = rutaFoto;
        } else {
            this.rutaFoto = "/resources/images/paciente.png";
        }
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

    @Override
    public int compareTo(Paciente o) {
        return (int) (this.getId() - o.getId());
    }
//    public void vacio(){
//        //log.info("Verifica si ingresa a metodo...");
//    }
}
