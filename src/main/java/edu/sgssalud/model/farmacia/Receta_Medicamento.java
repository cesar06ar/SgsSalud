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

package edu.sgssalud.model.farmacia;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "Receta_Medicamento")
public class Receta_Medicamento implements Serializable, Comparable<Receta_Medicamento> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;    
    private int ingreso;
    private int cantidad;
    private int saldo;
    private String detalle;
    
    @ManyToOne()
    private Receta receta;    
    
    @ManyToOne()
    private Medicamento medicamento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }    

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }   
    
    public int getIngreso() {
        return ingreso;
    }
    
    public void setIngreso(int ingreso) {
        this.ingreso = ingreso;
    }  
    
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }    

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }  

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }  
            
    public boolean isPersistent(){
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

        if (!(object instanceof Receta_Medicamento)) {
            return false;
        }
        Receta_Medicamento other = (Receta_Medicamento) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.farmacia.Receta_Medicamento[" 
                + " id = " + id 
                + " cantidad = " + cantidad 
                + " saldo = " + saldo 
                + " medicamento_id = " + medicamento.getId()
                + " ]";
    }

    @Override
    public int compareTo(Receta_Medicamento o) {
        return (int)(this.getId() - o.getId());
//        if (this.getFecha().after(o.getFecha())) {
//            return 1;
//        }else{
//            return -1;
//        }
    }
    
}
