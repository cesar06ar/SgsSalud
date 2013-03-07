/*
 * Copyright 2012 cesar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eqaula.glue.controller.management;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import org.apache.http.impl.cookie.DateUtils;
import org.eqaula.glue.cdi.Current;
import org.eqaula.glue.cdi.Web;
import org.eqaula.glue.controller.BussinesEntityHome;
import org.eqaula.glue.model.BussinesEntity;
import org.eqaula.glue.model.BussinesEntityType;
import org.eqaula.glue.model.accounting.Ledger;
import org.eqaula.glue.model.management.Objetive;
import org.eqaula.glue.model.management.Organization;
import org.eqaula.glue.model.management.Owner;
import org.eqaula.glue.model.profile.Profile;
import org.eqaula.glue.service.BussinesEntityService;
import org.eqaula.glue.service.OrganizationService;
import org.eqaula.glue.util.Dates;
import org.jboss.seam.transaction.Transactional;
import org.primefaces.context.RequestContext;

/*
 * @author dianita
 */
@Named
@ViewScoped
public class OwnerHome extends BussinesEntityHome<Owner> implements Serializable {

    private static final long serialVersionUID = 1182642574142830175L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(OwnerHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private BussinesEntityService bussinesEntityService;
    @Current
    @Inject
    private Profile profile;
  
    private Long organizationId;
    private Organization organization;
    @Inject
    private OrganizationService organizationService;

    public OwnerHome() {
    }

    public Long getOwnerId() {
        return (Long) getId();
    }

    public void setOwnerId(Long ownerId) {
        setId(ownerId);
    }

    public String getOwnerName() {
        return getInstance().getName();
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Transactional
    public Organization getOrganization() {
        if (organization == null) {
            if (organizationId == null) {
                organization = null;
            }else {
                organization = organizationService.find(getOrganizationId());
            }
        }
        return organization;
    }
    
    public void setOrganization(Organization organization){
        this.organization=organization;
    }

    
    @TransactionAttribute
    public void load() {
        if (isIdDefined()) {
            wire();
        }
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        organizationService.setEntityManager(em);
        bussinesEntityService.setEntityManager(em);
    }

    @Override
    protected Owner createInstance() {
        BussinesEntityType _type = bussinesEntityService.findBussinesEntityTypeByName(Owner.class.getName());
        Date now = Calendar.getInstance().getTime();
        Owner owner = new Owner();
        owner.setCode(UUID.randomUUID().toString());
        owner.setCreatedOn(now);
        owner.setLastUpdate(now);
        owner.setActivationTime(now);
        owner.setExpirationTime(Dates.addDays(now, 364));
        owner.setType(_type);
        owner.buildAttributes(bussinesEntityService);
        return owner;
    }

    @TransactionAttribute
    public String saveOwner() {
        Date now = Calendar.getInstance().getTime();
        getInstance().setLastUpdate(now);
        if (getInstance().isPersistent()) {
            save(getInstance());
        } else {
            
            getInstance().setAuthor(this.profile);
            getInstance().setOrganization(getOrganization());
            create(getInstance());
        }
        return "/pages/management/organization/view?organizationId=" + getOrganizationId();
    }

    public boolean isWired() {
        return true;
    }

    public Owner getDefiniedInstance() {
        return isIdDefined() ? getInstance() : null;
    }

    @Override
    public Class<Owner> getEntityClass() {
        return Owner.class;
    }

    @Transactional
    public String deleteOwner() {
        try {
            if (getInstance() == null) {
                throw new NullPointerException("Owner is Null");
            }
            if (getInstance().isPersistent()) {
                delete(getInstance());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + getInstance().getName(), ""));
                RequestContext.getCurrentInstance().execute("editDlg.hide()");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡No existe un gerente para ser borrado!", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
        }
        return "/pages/management/organization/list.xhtml?faces-redirect=true";
    }

   
}
