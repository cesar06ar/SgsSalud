/**
* This file is part of Iamin Core: Universal Software
*
* Copyright (c)2012 José Luis Granda <jlgranda@eqaula.org> (Eqaula Tecnologías Cia Ltda)
* Copyright (c)2012 Eqaula Tecnologías Cia Ltda (http://eqaula.org)
*
* If you are developing and distributing open source applications under
* the GNU General Public License (GPL), then you are free to re-distribute Glue
* under the terms of the GPL, as follows:
*
* SocialPM is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Iamin Core is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with SocialPM. If not, see <http://www.gnu.org/licenses/>.
*
* For individuals or entities who wish to use SocialPM privately, or
* internally, the following terms do not apply:
*
* For OEMs, ISVs, and VARs who wish to distribute SocialPM with their
* products, or host their product online, OCPsoft provides flexible
* OEM commercial licenses.
*
* Optionally, Customers may choose a Commercial License. For additional
* details, contact an OCPsoft representative (sales@eqaula.org)
*/
package edu.sgssalud.rewrite;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;



/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RegexConstraint implements Constraint<String>
{
   private final String pattern;

   public RegexConstraint(String pattern)
   {
      this.pattern = pattern;
   }

   @Override
   public boolean isSatisfiedBy(Rewrite event, EvaluationContext context, String value)
   {
      return value.matches(pattern);
   }
}
