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

import edu.sgssalud.model.PersistentObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 *
 * @author cesar
 */
@Entity
@Table(name = "EnfermedadCIE10")
public class EnfermedadCIE10 extends PersistentObject<EnfermedadCIE10> implements Comparable<EnfermedadCIE10>{
    
    private static final long serialVersionUID = -1;

    public EnfermedadCIE10() {
    }    
    
    public EnfermedadCIE10(String codigo, String nombre, String categoria, Date fechaCreacion) {
        this.setCode(codigo);
        this.setName(nombre);
        this.setDescription(categoria);
        this.setCreatedOn(fechaCreacion);
        this.setLastUpdate(fechaCreacion);
    }
    
    @ManyToMany(mappedBy = "enfermedadesCIE10")    
    private List<HistoriaClinica> historiasClinicas = new ArrayList<HistoriaClinica>();

    public List<HistoriaClinica> getHistoriasClinicas() {
        return historiasClinicas;
    }

    public void setHistoriasClinicas(List<HistoriaClinica> historiasClinicas) {        
        this.historiasClinicas = historiasClinicas;
    }   

    @Override
    public int compareTo(EnfermedadCIE10 o) {
        return this.getCode().compareToIgnoreCase(o.getCode());
    }

    @Override
    public String toString() {
        return "edu.edu.sgssalud.model.medicina.EnfermedadCIE10[ " 
                + "id=" + getId() + ","
                + "codigo=" + getCode() + ","
                + "nombre" + getName() + ","
                + "categoria" + getDescription() + ","
                + " ]";
    }
}
