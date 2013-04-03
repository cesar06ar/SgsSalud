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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 *
 * @author jlgranda
 */
@Entity
@NamedQueries(value = {
    @NamedQuery(name = "Structure.findForId", query = "SELECT s FROM Structure s WHERE s.id = :id")
})
public class Structure extends DeletableObject<Structure> implements Serializable {

    private static final long serialVersionUID = -1939876087269734534L;
    @ManyToOne
    private BussinesEntityType bussinesEntityType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "structure", fetch = FetchType.LAZY)
    @OrderBy(value = "sequence")
    private List<Property> properties;

    public BussinesEntityType getBussinesEntityType() {
        return bussinesEntityType;
    }

    public void setBussinesEntityType(BussinesEntityType bussinesEntityType) {
        this.bussinesEntityType = bussinesEntityType;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public boolean addProperty(Property property) {
        property.setStructure(this);
        return this.properties.add(property);
    }

    public void setProperties(List<Property> properties) {
        for (Property p : properties) {
            p.setStructure(this);
        }
        this.properties = properties;
    }

    public boolean removeProperty(Property property) {       
        
        return this.properties.remove(property);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Structure)) {
            return false;
        }
        Structure other = (Structure) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.Structure[ id=" + this.getId() + " ]";
    }
}
