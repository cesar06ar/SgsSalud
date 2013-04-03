/**
* This file is part of Glue: Adhesive BRMS
*
* Copyright (c)2012 José Luis Granda <jlgranda@eqaula.org> (Eqaula Tecnologías Cia Ltda)
* Copyright (c)2012 Eqaula Tecnologías Cia Ltda (http://eqaula.org)
*
* If you are developing and distributing open source applications under
* the GNU General Public License (GPL), then you are free to re-distribute Glue
* under the terms of the GPL, as follows:
*
* GLue is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Glue is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Glue. If not, see <http://www.gnu.org/licenses/>.
*
* For individuals or entities who wish to use Glue privately, or
* internally, the following terms do not apply:
*
* For OEMs, ISVs, and VARs who wish to distribute Glue with their
* products, or host their product online, Eqaula provides flexible
* OEM commercial licenses.
*
* Optionally, Customers may choose a Commercial License. For additional
* details, contact an Eqaula representative (sales@eqaula.org)
*/
package edu.sgssalud.model.security;

import static org.jboss.seam.security.annotations.management.EntityType.IDENTITY_ATTRIBUTE;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jboss.seam.security.annotations.management.IdentityEntity;
import org.jboss.seam.security.annotations.management.IdentityProperty;
import org.jboss.seam.security.annotations.management.PropertyType;

@Entity @IdentityEntity(IDENTITY_ATTRIBUTE)
@Table(name = "identity_attributes")
public class IdentityObjectAttribute implements Serializable
{
   private static final long serialVersionUID = -1351668083235705417L;
   @Id
   @GeneratedValue
   private Long id;

   @ManyToOne
   @JoinColumn(name = "IDENTITY_OBJECT_ID")
   private IdentityObject identityObject;

   @IdentityProperty(PropertyType.NAME)
   private String name;

   @IdentityProperty(PropertyType.VALUE)
   private String value;

   public IdentityObject getIdentityObject()
   {
      return identityObject;
   }

   public void setIdentityObject(final IdentityObject identityObject)
   {
      this.identityObject = identityObject;
   }

   public String getName()
   {
      return name;
   }

   public void setName(final String name)
   {
      this.name = name;
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(final String value)
   {
      this.value = value;
   }

   public Long getId()
   {
      return id;
   }

   public void setId(final Long id)
   {
      this.id = id;
   }
}