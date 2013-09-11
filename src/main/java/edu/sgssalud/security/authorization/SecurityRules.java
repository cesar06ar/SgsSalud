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
package edu.sgssalud.security.authorization;

import edu.sgssalud.cdi.Current;
import edu.sgssalud.model.profile.Profile;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;
import org.jboss.solder.logging.Logger;
import org.picketlink.idm.api.Role;


public class SecurityRules {

    private static Logger log = Logger.getLogger(SecurityRules.class);
    public static String ADMIN = "Admin";
    private static String ACCOUNTANT = "Accountant";
    private static String FARMACEUTICA = "Farmaceutica";
    private static String PACIENTE = "paciente";

    /*public @Secures
     @Admin
     boolean adminCheck() {
     return false; // No one is an admin!
     }
     */
    @Secures
    @Owner
    public boolean isProfileOwner(Identity identity, @Current Profile profile) {
        if (profile == null || identity.getUser() == null) {
            return false;
        } else {            
            return profile.getIdentityKeys().contains(getUsername(identity))
                    || identity.hasRole("admin", "USERS", "GROUP")
                    || identity.inGroup(SecurityRules.ADMIN, "GROUP")
                    || "admin".contains(getUsername(identity));
        }
    }
    
    @Secures
    @Paciente
    public boolean isPacienteOwner(Identity identity, @Current edu.sgssalud.model.paciente.Paciente pacienteU) {
        if (pacienteU == null || identity.getUser() == null) {
            return false;
        } else {            
            return pacienteU.getIdentityKeys().contains(getUsername(identity))
                    || identity.hasRole("admin", "USERS", "GROUP")
                    || identity.inGroup(SecurityRules.ADMIN, "GROUP")
                    || "admin".contains(getUsername(identity));
        }
    }

    @Secures
    @Admin
    public boolean isAdmin(Identity identity) {
        if (identity.getUser() == null) {
            return false;

        } else {
            return "admin".contains(getUsername(identity))
                    || identity.hasRole("admin", "USERS", "GROUP")
                    || identity.inGroup(SecurityRules.ADMIN, "GROUP");
        }
    }

    @Secures
    @Accountant
    public boolean isAccountant(Identity identity) {
        if (identity.getUser() == null) {
            return false;
        } else {
            return identity.hasRole(ACCOUNTANT, "USERS", "GROUP")
                    || identity.inGroup(ACCOUNTANT, "GROUP")
                    || identity.inGroup(SecurityRules.ADMIN, "GROUP")
                    || "admin".contains(getUsername(identity));
        }
    }

    @Secures
    @farmaceutica
    public boolean isFarmaceutica(Identity identity) {
        if (identity.getUser() == null) {
            return false;
        } else {
            return identity.hasRole(FARMACEUTICA, "USERS", "GROUP")
                    || identity.inGroup(FARMACEUTICA, "GROUP")
                    || identity.inGroup(SecurityRules.ADMIN, "GROUP")
                    || "admin".contains(getUsername(identity));
        }
    }
    

    public static String getUsername(Identity identity) {
        String user = "";
        if (identity != null && identity.getUser() != null) {
            user = identity.getUser().getKey();
        }
        return user;
    }
}
