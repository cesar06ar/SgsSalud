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
package edu.sgssalud.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class DeletableObject<E extends DeletableObject<E>> extends PersistentObject<E>
{
   private static final long serialVersionUID = 49008810086044438L;

   @Column
   private boolean deleted = false;

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "deletedOn")
   private Date deletedOn;

   @Override
   @PrePersist
   void prePersist()
   {
      if (isDeleted() && (getDeletedOn() == null))
      {
         setDeletedOn(new Date());
      }
      super.prePersist();
   }

   @Override
   void preUpdate()
   {
      if (isDeleted() && (getDeletedOn() == null))
      {
         setDeletedOn(new Date());
      }
      super.preUpdate();
   }

   public boolean isDeleted()
   {
      return deleted;
   }

   public void setDeleted(final boolean deleted)
   {
      this.deleted = deleted;
   }

   public Date getDeletedOn()
   {
      return deletedOn;
   }

   public void setDeletedOn(final Date deletedOn)
   {
      this.deletedOn = deletedOn;
   }
}
