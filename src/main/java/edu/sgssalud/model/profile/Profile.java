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
package edu.sgssalud.model.profile;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.Group;

import org.hibernate.validator.constraints.NotEmpty;
import org.jasypt.util.password.BasicPasswordEncryptor;

import org.hibernate.validator.constraints.Email;

import org.hibernate.annotations.Index;
import org.jboss.solder.logging.Logger;

@Entity
@Table(name = "Profile")
@DiscriminatorValue(value = "PR")
@PrimaryKeyJoinColumn(name = "id")
/*
 * Consultas nombradas para User
 */
@NamedQueries({
    @NamedQuery(name = "Profile.findUserByLogin", query = "select u from Profile u where u.username = :username"),
    @NamedQuery(name = "Profile.findRoleById", query = "select u.role from Profile u where u.id = :id"),
    /*@NamedQuery(name = "Profile.findGroupsByUserAndType", query = "select g FROM Profile u JOIN u.groups g WHERE u=:user and g.type=:groupType"),
     @NamedQuery(name = "Profile.findUserByGroupsAndRole", query = "select entity From Profile entity join entity.groups g where g in (:groups) and entity.role.name=:role"),*/
    @NamedQuery(name = "Profile.findUsersByNameOrUsername", query = "select u from Profile u where lower(u.username)  LIKE lower(:name) or lower(u.name) LIKE lower(:name)"),
    @NamedQuery(name = "Profile.findUserByEmail", query = "from Profile u where u.email = :email")})
public class Profile extends BussinesEntity implements Serializable {

    private static Logger log = Logger.getLogger(Profile.class);
    private static final long serialVersionUID = 274770881776410973L;
    @Column(nullable = true)
    private String firstname;
    @Column(nullable = true)
    private String surname;
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String username;
    @NotEmpty
    @Column(nullable = false)
    private String password;
    @Column(length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private byte[] photo;
    @Email(message = "#{messages['MailBadFormat']}")
    @Index(name = "userEmailIndex")
    @Column(nullable = false, length = 128, unique = false)
    private String email;
    @ManyToOne
    private Group role;
    private String mobileNumber;
    private String workPhoneNumber;
    @Column
    private boolean confirmed;
    @Column
    private boolean showBootcamp;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profiles_keys")
    private Set<String> identityKeys = new HashSet<String>();
    @Column
    private boolean emailSecret = true;
    @Column(length = 50)
    private String screenName;
    @Column
    private String bio;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            username = username.toLowerCase();
        }
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = new BasicPasswordEncryptor().encryptPassword(password);
    }

    public String getPassword() {
        return this.password;
    }

    public boolean verifyPassword(String password) {
        return new BasicPasswordEncryptor().checkPassword(password,
                this.password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Group getRole() {
        return role;
    }

    public void setRole(Group role) {
        this.role = role;
    }

    public String getWorkPhoneNumber() {
        return workPhoneNumber;
    }

    public void setWorkPhoneNumber(String workPhoneNumber) {
        this.workPhoneNumber = workPhoneNumber;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(final String screenName) {
        this.screenName = screenName;
    }

    public boolean isEmailSecret() {
        return emailSecret;
    }

    public void setEmailSecret(final boolean emailSecret) {
        this.emailSecret = emailSecret;
    }

    public Set<String> getIdentityKeys() {
        return identityKeys;
    }

    public void setIdentityKeys(final Set<String> keys) {
        this.identityKeys = keys;
    }

    public boolean isUsernameConfirmed() {
        return confirmed;
    }

    public void setUsernameConfirmed(final boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((username == null) ? 0 : username.hashCode());
        return result;
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
        Profile other = (Profile) obj;
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    public boolean isShowBootcamp() {
        return showBootcamp;
    }

    public void setShowBootcamp(final boolean showBootcamp) {
        this.showBootcamp = showBootcamp;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Transient
    public String getFullName() {
        String fullName = Strings.nullToEmpty(this.getFirstname() + " " + this.getSurname());
        boolean flag = Strings.isNullOrEmpty(this.getFirstname()) && Strings.isNullOrEmpty(this.getSurname());
        return flag ? getUsername() : fullName;
    }

    @Override
    public String toString() {
        return Profile.class.getName()
                + "id=" + getId() + ","
                + "fullName=" + getFullName() + ","
                + "IdentityKeys=" + getIdentityKeys() + ","
                + " ]";
    }
}
