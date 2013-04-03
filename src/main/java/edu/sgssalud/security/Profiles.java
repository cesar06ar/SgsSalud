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
package edu.sgssalud.security;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import edu.sgssalud.cdi.Current;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.profile.ProfileService;
import edu.sgssalud.web.ParamsBean;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @adapter <a href="mailto:jlgranda81@gmail.com">José Luis Granda</a>
 */
@Named("profiles")
@RequestScoped
public class Profiles implements Serializable
{
   private static final long serialVersionUID = 8474539305281711165L;

   @Inject
   @Web
   private EntityManager em;

   @Inject
   private ProfileService ps;

   @Inject
   private ParamsBean params;

   @PostConstruct
   public void init()
   {
      ps.setEntityManager(em);
   }

   private Profile current = new Profile();

   /*@Produces
   @Named("profile")
   @RequestScoped
   @Current*/
   public Profile getCurrent()
   {
       
      if (!current.isPersistent())
      {
         try {
            current = ps.getProfileByUsername(params.getProfileUsername());
         }
         catch (NoResultException e) {
            // not an error
         }
      }
      return current;
   }

   public Object verifyExists()
   {
      if (!getCurrent().isPersistent())
      {
         //return Forward.to("/404");
          return "404";
      }
      return null;
   }

   public void setEntityManager(final EntityManager em)
   {
      this.em = em;
   }
   
   public long getCount()
   {
      return ps.getProfileCount();
   }

}
