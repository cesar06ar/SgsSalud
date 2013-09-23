/**
 * This file is part of Glue: Adhesive BRMS
 * 
* Copyright (c)2012 José Luis Granda <jlgranda@eqaula.org> (Eqaula Tecnologías
 * Cia Ltda) Copyright (c)2012 Eqaula Tecnologías Cia Ltda (http://eqaula.org)
 * 
* If you are developing and distributing open source applications under the GNU
 * General Public License (GPL), then you are free to re-distribute Glue under
 * the terms of the GPL, as follows:
 * 
* GLue is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
* Glue is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
* You should have received a copy of the GNU General Public License along with
 * Glue. If not, see <http://www.gnu.org/licenses/>.
 * 
* For individuals or entities who wish to use Glue privately, or internally,
 * the following terms do not apply:
 * 
* For OEMs, ISVs, and VARs who wish to distribute Glue with their products, or
 * host their product online, Eqaula provides flexible OEM commercial licenses.
 * 
* Optionally, Customers may choose a Commercial License. For additional
 * details, contact an Eqaula representative (sales@eqaula.org)
 */
package edu.sgssalud.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.Dates;
import edu.sgssalud.util.Lists;

/**
 *
 * @responsable jlgranda
 */
@Entity
@Table(name = "BussinesEntity")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ENTITY_TYPE", discriminatorType = DiscriminatorType.STRING, length = 2)
@NamedQueries({ /*@NamedQuery(name = "BussinesEntity.findBussinesEntityByParentIdAndType",
 query = "select m FROM Group "
 + "g JOIN g.members m WHERE g.id=:id and m.type=:type ORDER BY g.name")*/})
public class BussinesEntity extends DeletableObject<BussinesEntity> {

    private static final long serialVersionUID = -3282665873474370357L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntity.class);
    @ManyToOne(optional = true)
    @JoinColumn(name = "author", nullable = true)
    private Profile responsable;
   /* @ManyToOne
    @JoinColumn(name = "parent")
    public BussinesEntity parent;*/
    /*@OneToMany(targetEntity = Group.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
     @JoinTable(name = "bussinesentity_group", joinColumns =
     @JoinColumn(name = "bussinesEntityId", referencedColumnName = "id"),
     inverseJoinColumns =
     @JoinColumn(name = "groupId", referencedColumnName = "id"))
     private List<Group> groups = new ArrayList<Group>();
     @ManyToOne(cascade = CascadeType.ALL)*/
    //Best practice http://java.dzone.com/articles/deterring-%E2%80%9Ctomany%E2%80%9D?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3a+javalobby/frontpage+%28Javalobby+/+Java+Zone%29
    //Replace ManyToMany fro OneToMany and link entity
    @OneToMany(cascade = CascadeType.ALL)
    private List<Membership> memberships = new ArrayList<Membership>();
    @ManyToOne
    private BussinesEntityType type;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bussinesEntity", fetch = FetchType.LAZY)
    private List<BussinesEntityAttribute> attributes = new ArrayList<BussinesEntityAttribute>();
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    /*public BussinesEntity getParent() {
        return parent;
    }

    public void setParent(BussinesEntity parent) {
        this.parent = parent;
    }*/

    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }

    public void setGroups(List<Group> groups) {
        //TODO this.groups = groups;
    }

    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<Group>();
        for (Membership m : getMemberships()) {
            groups.add(m.getGroup());
        }

        return groups;
    }

    public Group getGroup(String name) {
        for (Group g : getGroups()) {
            if (name.equals(g.getName())) {
                return g;
            }
        }

        return null;
    }

    public List<Group> findGroups(final String... names) {
        List<Group> _buffer = new ArrayList<Group>();
        Group g = null;
        for (String name : names) {
            g = getGroup(name);
            if (g != null) {
                _buffer.add(g);
            }
        }
        return _buffer;
    }

    public List<Group> findGroupsFor(final String names) {
        return findGroups(names.split(","));
    }

    public void add(Group g) {
        Date now = Calendar.getInstance().getTime();
        Membership membershipt = new Membership();
        membershipt.setGroup(g);
        membershipt.setBussinesEntity(this);
        membershipt.setCreatedOn(now);
        membershipt.setLastUpdate(now);
        membershipt.setActivationTime(now);
        membershipt.setExpirationTime(Dates.addDays(now, 364));
        if (!getMemberships().contains(membershipt)) {
            getMemberships().add(membershipt);
        }
    }

    public void remove(Group g) {
        /*if (groups.contains(g)) {
         this.getGroups().remove(g);
         }
         */
    }

    public boolean containsGroup(String name) {
        for (Group g : getGroups()) {
            if (name.equalsIgnoreCase(g.getName())) {
                return true;
            }
        }
        return false;
    }

    public Profile getResponsable() {
        return responsable;
    }

    public void setResponsable(Profile responsable) {
        this.responsable = responsable;
    }

    public BussinesEntityType getType() {
        return type;
    }

    public void setType(BussinesEntityType type) {
        this.type = type;
    }

    public List<BussinesEntityAttribute> getAttributes() {
        Collections.sort(attributes);
        return attributes;
    }

    public void setAttributes(List<BussinesEntityAttribute> attributes) {
        for (BussinesEntityAttribute attr : attributes) {
            attr.setBussinesEntity(this);
        }
        this.attributes.addAll(attributes);
    }

    public void addBussinesEntityAttribute(BussinesEntityAttribute attribute) {
        attribute.setBussinesEntity(this);
        this.attributes.add(attribute);
    }

    public boolean containsBussinesEntityAttribute(String name) {
        for (BussinesEntityAttribute attr : getAttributes()) {
            if (name.equalsIgnoreCase(attr.getName())) {
                return true;
            }
        }
        return false;
    }

    public BussinesEntityAttribute getBussinessEntityAttribute(String name) {
        for (BussinesEntityAttribute attr : getAttributes()) {
            if (name.equalsIgnoreCase(attr.getName())) {
                return attr;
            }
        }
        return null;
    }

    public String findAttributeValue(String name) {
        if ("code".equalsIgnoreCase(name)) {
            return getCode();
        } else if ("name".equalsIgnoreCase(name)) {
            return getName();
        } else {
            return (getBussinessEntityAttribute(name) != null && getBussinessEntityAttribute(name).getValue() != null ? getBussinessEntityAttribute(name).getValue().toString() : "undefined!");
        }
    }

    /*
     * Retrieve all attributes for strcuture name list
     */
    public List<BussinesEntityAttribute> getBussinessEntityAttributes(String... structureNames) {
        List<BussinesEntityAttribute> _buffer = new ArrayList<BussinesEntityAttribute>();
        for (BussinesEntityAttribute a : getAttributes()) {
            for (String sn : structureNames) {
                if (sn.equalsIgnoreCase(a.getProperty().getStructure().getBussinesEntityType().getName())) {
                    _buffer.add(a);
                }
            }
        }
        return _buffer;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
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
        BussinesEntity other = (BussinesEntity) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(getName(), other.getName()).
                append(getCode(), other.getCode()).
                isEquals();
    }

    //-------------------------------------------------------------------------
    // Attributes management and build
    //-------------------------------------------------------------------------
    public void buildAttributes(BussinesEntityService bes) {
        if (getType() == null) {
            return;
        }
        for (Structure s : getType().getStructures()) {
            for (Property a : s.getProperties()) {
                if (a.getType().equals(Group.class.getName())) {
                    buildGroup(a, bes);
                } else if (a.getType().equals(Structure.class.getName())) {
                    loadStructure(a, bes);
                } else {
                    this.addBussinesEntityAttribute(buildAtribute(a));
                }
            }
        }
    }

    public void buildAttributes(String name, BussinesEntityService bes) {
        BussinesEntityType _type = bes.findBussinesEntityTypeByName(name);
        if (_type == null) {
            return;
        }
        for (Structure s : _type.getStructures()) {
            for (Property a : s.getProperties()) {
                if (a.getType().equals(Group.class.getName())) {
                    //TODO support for groups into groups
                } else if (a.getType().equals(Structure.class.getName())) {
                    loadStructure(a, bes);
                } else {
                    this.addBussinesEntityAttribute(buildAtribute(a));
                }
            }
        }
    }

    protected void loadStructure(Property a, BussinesEntityService bes) {
        BussinesEntityType _type = bes.findBussinesEntityTypeByName(a.getName());
        if (_type == null) {
            return;
        }
        for (Structure s : _type.getStructures()) {
            for (Property attr : s.getProperties()) {
                this.addBussinesEntityAttribute(buildAtribute(attr));
            }
        }
    }

    protected Group buildGroup(Property attr, BussinesEntityService bes) {
        Date now = Calendar.getInstance().getTime();
        Group g = null;
        if (!this.containsGroup(attr.getName())) {
            g = new Group();
            g.setName(attr.getName());
            g.setCreatedOn(now);
            g.setLastUpdate(now);
            g.setActivationTime(now);
            g.setExpirationTime(Dates.addDays(now, 364));
            g.setResponsable(null); //TODO Establecer al usuario actual
            g.setProperty(attr); //almacenar en memoria para dibujar GUI
            g.buildAttributes(bes);
            this.add(g);
        }
        return g;
    }

    protected BussinesEntityAttribute buildAtribute(Property p) {
        BussinesEntityAttribute attribute = new BussinesEntityAttribute();
        attribute.setName(p.getName());
        attribute.setType(p.getType());
        if ("java.lang.String[]".equals(p.getType())) {
            attribute.setValue((Serializable) Lists.findDefaultValue(p.getValue().toString()));
        } else {
            attribute.setValue((Serializable) p.getValue());
        }
        attribute.setProperty(p); //Relaciona los objetos de forma directa
        return attribute;
    }

    /**
     * Return list of BussinesEntityAttribute for BussinesEntityType names lists
     *
     * @param names list of names for BussinessEntityType
     * @return list of BussinesEntityAttribute for BussinesEntityType names
     * lists
     */
    public List<BussinesEntityAttribute> findBussinesEntityAttribute(final String names) {
        if (names == null) {
            return new ArrayList<BussinesEntityAttribute>();
        }
        List<BussinesEntityAttribute> temp = getBussinessEntityAttributes(names.split(","));
        return temp;
    }
    
     public boolean containsBussinesEntityType(String name) {
        List<BussinesEntityAttribute> _buffer = new ArrayList<BussinesEntityAttribute>();
        for (BussinesEntityAttribute a : getAttributes()) {            
                if (name.equalsIgnoreCase(a.getProperty().getStructure().getBussinesEntityType().getName())) {
                    return true;
                }            
        }        
        return false;
    }
}
