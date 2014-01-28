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
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author jlgranda
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "BussinesEntityAttribute.findByBussinesEntityIdAndTypes",
    query = "select a FROM BussinesEntityAttribute a JOIN a.property p "
    + "WHERE p.structure.bussinesEntityType.name = :bussinesEntitytypeName "
    + "AND p.type IN (:types) "
    + "ORDER BY p.id"),
    @NamedQuery(name = "BussinesEntityAttribute.countRequiredProperties",
    query = "select count(a)  FROM BussinesEntityAttribute a WHERE a.bussinesEntity.id = :bussinesEntityId AND a.property.structure.bussinesEntityType.name = :bussinesEntityTypeName AND a.property.required = true GROUP BY a.property.structure"),
    @NamedQuery(name = "BussinesEntityAttribute.countCompletedRequiredProperties",
    query = "select count(a)  FROM BussinesEntityAttribute a WHERE a.bussinesEntity.id = :bussinesEntityId AND a.property.structure.bussinesEntityType.name = :bussinesEntityTypeName AND a.property.required = true AND a.valueByteArray != a.property.valueByteArray GROUP BY a.property.structure")
})
public class BussinesEntityAttribute implements Serializable, Comparable<BussinesEntityAttribute> {

    private static final long serialVersionUID = 7807041724651919898L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    private BussinesEntity bussinesEntity;
    private String name;
    @Transient
    private Object value;
    @Basic(fetch = FetchType.LAZY)
    private byte[] valueByteArray;
    private String type;
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
    private String stringValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BussinesEntity getBussinesEntity() {
        return bussinesEntity;
    }

    public void setBussinesEntity(BussinesEntity bussinesEntity) {
        this.bussinesEntity = bussinesEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected byte[] getValueByteArray() {
        return valueByteArray;
    }

    protected void setValueByteArray(byte[] valueByteArray) {
        this.valueByteArray = valueByteArray;
    }

    public Object getValue() {
        return SerializationUtils.deserialize(getValueByteArray());
    }

    public void setValue(Object value) {
        setValueByteArray(SerializationUtils.serialize((Serializable) value));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(getName()).
                append(getType()).
                append(getProperty()).
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
        BussinesEntityAttribute other = (BussinesEntityAttribute) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(getName(), other.getName()).
                append(getType(), other.getType()).
                append(getProperty(), other.getProperty()).
                isEquals();
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.BussinesEntityAttribute[ "
                + "id=" + id + ","
                + "name=" + name + ","
                + "type=" + type + ","
                + " ]";
    }

    @Override
    public int compareTo(BussinesEntityAttribute o) {
        return (int)(this.getProperty().getSequence() - o.getProperty().getSequence());
    }
}
