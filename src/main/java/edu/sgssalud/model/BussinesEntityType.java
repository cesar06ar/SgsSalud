/*
 * Copyright 2012 jlgranda.
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
package edu.sgssalud.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;

/**
 *
 * @author jlgranda
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "BussinesEntityType.findAllBussinesEntityTypes",
    query = "select t FROM BussinesEntityType t"),
    @NamedQuery(name = "BussinesEntityType.findForName",
    query = "select t FROM BussinesEntityType t where t.name = :name")    
})
public class BussinesEntityType implements Serializable {

    private static final long serialVersionUID = -2155543217133636282L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private String name;
    private String label;
    private String description;
    
    @OneToMany(mappedBy = "bussinesEntityType", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Structure> structures = new ArrayList<Structure>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    

    public List<Structure> getStructures() {
        return structures;
    }

    public void setStructures(List<Structure> structures) {
        this.structures = structures;
    }
    
    

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(getName()).
                toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BussinesEntityType other = (BussinesEntityType) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(getName(), other.getName()).
                isEquals();
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.BussinesEntityType[ id=" + id + " ]";
    }

    public void addStructure(Structure structure) {
        structure.setBussinesEntityType(this);
        this.structures.add(structure);
    }
    
    public boolean isPersistent() {
        return getId() != null;
    }
}
