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
import javax.persistence.Basic;
import javax.persistence.Column;
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
import edu.sgssalud.util.Lists;

/**
 *
 * @author jlgranda
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Property.findByBussinesEntityTypeName",
    query = "select p FROM Property p "
    + "WHERE p.structure.bussinesEntityType.name = :bussinesEntityTypeName ORDER BY p.id")
})
public class Property implements Comparable<Property>, Serializable {

    private static final long serialVersionUID = 1020047606754217515L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "property_id")
    private Long id;
    private String groupName; //nombre de grupo
    private String name;
    private String label;
    private Long sequence;
    private String type;
    private boolean required;
    @Column(nullable = true, length = 2048)
    private String helpInline;
    @Basic(fetch = FetchType.LAZY)
    private byte[] valueByteArray;
    private String customForm;
    @Transient
    private Object value;
    @Transient
    private List<String> values;
    private String render;
    private String converter;
    private String validator;
    /**
     * ***********************************************************
     * Attributes for property type org.eqaula.glue.model.Group
     * ***********************************************************
     */
    private String overwrite;
    /**
     * Show/hide default BussinesEntity attributes into form edition for
     * org.equala.glue.Group type
     */
    private boolean showDefaultBussinesEntityProperties = false;
    /**
     * If this property is a group, register a generatorName for code attribute
     * in members. It is apply for other attributes type
     */
    private String generatorName;
    /**
     * El mínimo de miembros requeridos para el grupo
     */
    private Long minimumMembers = 0L;
    /**
     * El límite de miembros permitidos para el grupo
     */
    private Long maximumMembers = 0L;
    /**
     * Show in columns of table for groups.
     */
    private boolean showInColumns = false;
    /**
     * ***********************************************************
     * Attributes for pools
     * ***********************************************************
     */
    private boolean survey;
    @ManyToOne
    @JoinColumn(name = "structure_id")
    private Structure structure;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getHelpInline() {
        return helpInline;
    }

    public void setHelpInline(String helpInline) {
        this.helpInline = helpInline;
    }

    protected byte[] getValueByteArray() {
        return valueByteArray;
    }

    protected void setValueByteArray(byte[] valueByteArray) {
        this.valueByteArray = valueByteArray;
    }

    public Object getValue() {
        if (getValueByteArray() == null) {
            return null;
        } else {
            return SerializationUtils.deserialize(getValueByteArray());
        }
    }

    public void setValue(Serializable value) {
        setValueByteArray(SerializationUtils.serialize((Serializable) value));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomForm() {
        return customForm;
    }

    public void setCustomForm(String customForm) {
        this.customForm = customForm;
    }

    public String getRender() {
        return render;
    }

    public void setRender(String render) {
        this.render = render;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public String getConverter() {
        return converter;
    }

    public void setConverter(String converter) {
        this.converter = converter;
    }

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public String getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(String overwrite) {
        this.overwrite = overwrite;
    }

    public List<String> getValues() {
        return Lists.stringToList(getValue().toString());
    }

    public boolean isShowDefaultBussinesEntityProperties() {
        return showDefaultBussinesEntityProperties;
    }

    public void setShowDefaultBussinesEntityProperties(boolean showDefaultBussinesEntityProperties) {
        this.showDefaultBussinesEntityProperties = showDefaultBussinesEntityProperties;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    public void setGeneratorName(String generatorName) {
        this.generatorName = generatorName;
    }

    public Long getMinimumMembers() {
        return minimumMembers;
    }

    public void setMinimumMembers(Long minimumMembers) {
        this.minimumMembers = minimumMembers;
    }

    public Long getMaximumMembers() {
        return maximumMembers;
    }

    public void setMaximumMembers(Long maximumMembers) {
        this.maximumMembers = maximumMembers;
    }

    public boolean isShowInColumns() {
        return showInColumns;
    }

    public void setShowInColumns(boolean showInColumns) {
        this.showInColumns = showInColumns;
    }

    public boolean isSurvey() {
        return survey;
    }

    public void setSurvey(boolean survey) {
        this.survey = survey;
    }

    public boolean isPersistent() {
        return getId() != null;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(getGroupName()).
                append(getName()).
                append(getType()).
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
        Property other = (Property) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(getGroupName(), other.getGroupName()).
                append(getName(), other.getName()).
                append(getType(), other.getType()).
                isEquals();
    }

    @Override
    public String toString() {
        return "edu.sgssalud.model.Property[ "
                + "id=" + id + ","
                + "name=" + name + ","
                + "type=" + type + ","
                + " ]";
    }

    @Override
    public int compareTo(Property o) {
        return (int) (this.getSequence() - o.getSequence());
    }
}
