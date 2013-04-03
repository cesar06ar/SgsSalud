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
package edu.sgssalud.util.crypt;

import java.security.MessageDigest;
import javax.enterprise.inject.Typed;
import org.apache.commons.codec.binary.Base64;

@Typed(PasswordEncryptor.class)
public class MD5PasswordEncryptor implements PasswordEncryptor
{
   private static final long serialVersionUID = 1422059557145039442L;

   @Override
   public String encodePassword(final String password, final Object salt)
   {
      try
      {
         MessageDigest digest = MessageDigest.getInstance("MD5");
         digest.reset();
         digest.update(salt.toString().getBytes());
         byte[] passwordHash = digest.digest(password.getBytes());

         Base64 encoder = new Base64();
         byte[] encoded = encoder.encode(passwordHash);
         return new String(encoded);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public boolean isPasswordValid(final String cypherPass, final String password, final Object salt)
   {
      return cypherPass.equals(this.encodePassword(password, salt));
   }

}