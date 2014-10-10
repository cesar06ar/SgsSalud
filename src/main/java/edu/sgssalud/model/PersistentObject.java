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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@MappedSuperclass
public abstract class PersistentObject<E extends PersistentObject<E>> implements Serializable {

    private static final long serialVersionUID = -1272280183658745494L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id = null;
    @Column(nullable = true)
    private String code;
    @Column(nullable = true)
    private String name;
    @Column(nullable = true, length = 2048)
    private String description;
    @Version
    @Column(name = "version", nullable=false)
    private int version = 0;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdate", nullable=false)
    private Date lastUpdate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdOn", updatable = false, nullable = false)
    private Date createdOn;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=true)
    public Date activationTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=true)
    private Date expirationTime;
    @Column(nullable=true)
    private Integer priority;

    @PrePersist
    void prePersist() {
        createdOn = new Date();
    }

    @PreUpdate
    void preUpdate() {
        lastUpdate = new Date();
    }

    protected static boolean getBooleanValue(final Boolean value) {
        return Boolean.valueOf(String.valueOf(value));
    }

    public Long getId() {
        return id;
    }

    public boolean isPersistent() {
        return getId() != null;
    }

    @SuppressWarnings("unchecked")
    public E setId(final Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Cannot alter immutable ID of persistent object with id: " + id);
        }
        this.id = id;
        return (E) this;
    }

    public int getVersion() {
        return version;
    }

    @SuppressWarnings("unused")
    private void setVersion(final int version) {
        this.version = version;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    @SuppressWarnings("unchecked")
    public E setLastUpdate(final Date lastUpdate) {
        this.lastUpdate = lastUpdate;
        return (E) this;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    @SuppressWarnings("unchecked")
    public E setCreatedOn(final Date createdOn) {
        this.createdOn = createdOn;
        return (E) this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
