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
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.util.Dates;

/**
 * @author paul
 * @adaptedby jlgranda
 *
 */
@Entity
@Table(name = "GGroup")
@DiscriminatorValue(value = "GR")
@PrimaryKeyJoinColumn(name = "groupId")
/*
 * Group querynamed
 */
@NamedQueries({
    /*@NamedQuery(name = "Group.findGroupByBussinessEntityIdAndType", query = "select g FROM BussinesEntity "
     + "o JOIN o.groups g WHERE o.id=:id and g.type=:type"),
     @NamedQuery(name = "Group.findGroupByType", query = "select g FROM Group "
     + " g where g.type=:type"),
     @NamedQuery(name = "Group.findParentsByType", query = "select _group from Group _group join _group.members _member where _member=:member and _group.type = :type"),
     @NamedQuery(name = "Group.findGroupByNameAndParent", query = "select _member from Group _group join _group.members _member "
     + " where _group.id = :id and lower(_member.name) LIKE lower(:name)"),
     @NamedQuery(name = "Group.findGroupByNameAndParentAndType", query = "select _member from Group _group join _group.members _member "
     + " where _member.type=:type and _group.id = :id and lower(_member.name) LIKE lower(:name)"),
     @NamedQuery(name = "Group.findGroupById", query = "from Group g where g.id = :id"),
     @NamedQuery(name = "Group.findGroupsByType", query = "from Group g where g.type = :type"),
     @NamedQuery(name = "Group.findGroupByName", query = "from Group g where lower(g.name) = lower(:name) and g.type = :type"),*/
    @NamedQuery(name = "Group.findByBussinesEntityIdAndPropertyId", query = "Select m.group from BussinesEntity be JOIN be.memberships m where m.bussinesEntity.id = :bussinesEntityId and m.group.property.id = :propertyId")})
public class Group extends BussinesEntity implements Serializable {

    private static final long serialVersionUID = 5665775223006691311L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(Group.class);

    public Group() {
        super();
    }

    public List<BussinesEntity> getMembers() {
        List<BussinesEntity> members = new ArrayList<BussinesEntity>();
        for (Membership m : getMemberships()) {
            members.add(m.getBussinesEntity());
        }

        return members;
    }

    public void setMembers(List<BussinesEntity> members) {
        // this.members = members;
        //TODO
    }

    public List<BussinesEntity> findOtherMembers(BussinesEntity me) {

        List<BussinesEntity> _buffer = new ArrayList<BussinesEntity>();

        if (me == null) {
            return _buffer;
        }

        for (BussinesEntity e : getMembers()) {
            if (me.getId() != null && !me.getId().equals(e.getId())) {
                _buffer.add(e);
            }
        }
        return _buffer;
    }

    public void add(BussinesEntity be) {
        Date now = Calendar.getInstance().getTime();
        Membership membershipt = new Membership();
        membershipt.setCreatedOn(now);
        membershipt.setLastUpdate(now);
        membershipt.setActivationTime(now);
        membershipt.setExpirationTime(Dates.addDays(now, 364));
        membershipt.setGroup(this);
        membershipt.setBussinesEntity(be);

        if (!getMemberships().contains(membershipt)) {
            log.infof("Add to membershipt {0}. Group {1}, BussinesEntity {2}", membershipt, this, be);
            getMemberships().add(membershipt);
        }
    }

    public void remove(BussinesEntity be) {
        Membership membershipt = new Membership();
        membershipt.setGroup(this);
        membershipt.setBussinesEntity(be);
        if (getMemberships().contains(membershipt)) {
            log.infof("remove to membershipt {0}. Group {1}, BussinesEntity {2}", membershipt, this, be);
            getMemberships().remove(membershipt);
        }
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
        Group other = (Group) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(getName(), other.getName()).
                append(getCode(), other.getCode()).
                isEquals();
    }
}