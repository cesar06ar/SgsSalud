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
package org.eqaula.glue.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.eqaula.glue.cdi.Web;
import org.eqaula.glue.controller.BussinesEntityHome;
import org.eqaula.glue.controller.profile.ProfileHome;
import org.eqaula.glue.model.BussinesEntity;
import org.eqaula.glue.model.BussinesEntityType;
import org.eqaula.glue.model.Property;
import org.eqaula.glue.model.Structure;
import org.eqaula.glue.model.profile.Profile;
import org.eqaula.glue.security.InitializeDatabase;
import org.eqaula.glue.service.BussinesEntityService;
import org.eqaula.glue.service.BussinesEntityTypeListService;
import org.eqaula.glue.service.BussinesEntityTypeService;
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author cesar
 */
@Named
@ViewScoped
public class BussinesEntityTypeHome extends BussinesEntityHome<BussinesEntityType> implements Serializable {

    private static final long serialVersionUID = 7632987414391869389L;
    private static org.jboss.solder.logging.Logger log = org.jboss.solder.logging.Logger.getLogger(BussinesEntityHome.class);
    @Inject
    @Web
    private EntityManager em;
    @Inject
    private BussinesEntityTypeListService bussinesEntityTypeListService;
    @Inject
    private BussinesEntityTypeService bussinesEntityTypeService;
    @Inject
    private BussinesEntityService bussinesEntityService;
    private String name;

    public BussinesEntityTypeHome() {
        log.info("eqaula --> Inicializo BussinesEntityTypeHome");
    }

    public Long getBussinesEntityTypeId() {
        return (Long) getId();
    }

    public void setBussinesEntityTypeId(Long bussinesEntityTypeId) {
        setId(bussinesEntityTypeId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected BussinesEntityType createInstance() {
        log.info("eqaula --> Creando instance ");
        BussinesEntityType bussinesEntityType = new BussinesEntityType();
        Date now = Calendar.getInstance().getTime();
        Calendar ago = Calendar.getInstance();
        ago.add(Calendar.DAY_OF_YEAR, (-1 * 364 * 18)); //18 años atras
        Structure structure = new Structure();

        structure.setCreatedOn(now);
        structure.setLastUpdate(now);
        /*
         List<Property> attributes = new ArrayList<Property>();
         attributes.add(buildStructureTypeProperty("PersonalData", "Datos Personales", "Información personal relevante", "/pages/profile/data/personal", 1L));
        
         structure.setProperties(attributes); */
        bussinesEntityType.addStructure(structure);

        return bussinesEntityType;
    }

    @TransactionAttribute
    public void load() {
        log.info("eqaula --> Loading instance from BussinesEntityType for " + getId());
        if (isIdDefined()) {
            wire();
        }
        log.info("eqaula --> Loaded instance" + getInstance());
    }

    @TransactionAttribute
    public void wire() {
        getInstance();
    }

    public boolean isWired() {
        return true;
    }

    public BussinesEntityType getDefinedInstance() {
        return isIdDefined() ? getInstance() : null;
    }

    @PostConstruct
    public void init() {
        setEntityManager(em);
        bussinesEntityTypeService.setEntityManager(em);
    }

    @Override
    public Class<BussinesEntityType> getEntityClass() {
        return BussinesEntityType.class;
    }

    @TransactionAttribute
    public String saveBussinesEntityType() {
        log.info("eqaula --> saving " + getInstance().getName());
        if (getInstance().getId() != null) {
            save(getInstance());
        } else {
            try {
                log.info("eqaula --> saving new" + getInstance().getName());
                getInstance().getStructures().get(0).setName("Propiedades para el tipo " + getInstance().getName());
                save(getInstance());

            } catch (Exception ex) {
                log.info("eqaula --> error saving new" + getInstance().getName());
            }
        }
        return "/pages/admin/bussinesentitytype/list";
    }

    @TransactionAttribute
    public String saveNewBussinesEntityType() {
        log.info("eqaula --> saving " + getInstance().getName());
        try {
            log.info("eqaula --> saving new" + getInstance().getName());
            create(getInstance());
            setBussinesEntityTypeId(getInstance().getId());
            wire();
            getInstance().getStructures().get(0).setName("Data for " + getInstance().getName());
            save(getInstance());

        } catch (Exception ex) {
            log.info("eqaula --> error saving new" + getInstance().getName());
        }

        return "/pages/admin/bussinesentitytype/bussinesentitytype?faces-redirect=true&bussinesEntityTypeId=" + getBussinesEntityTypeId();
    }

    @Transactional
    public void saveBussinesEntity() {
        try {
            if (getBussinesEntity() == null) {
                throw new NullPointerException("bussinessEntityType is null");
            }

            if (getBussinesEntity().getName().equalsIgnoreCase("")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No name", null));
            } else {
                save(getBussinesEntity());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se actualizó con exito " + bussinesEntity.getName(), ""));
            }

        } catch (Exception e) {
            System.out.println("saveBussinesEntity ERROR = " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
        }
    }

    @TransactionAttribute
    public void displayBootcampAjax() {
//        getInstance().setShowBootcamp(true);
        update();
    }

    @TransactionAttribute
    public void dismissBootcampAjax() {
//        getInstance().setShowBootcamp(false);
        update();
    }

    public boolean isAssociatedToBussinesEntity() {
        boolean a = false;
                    
            log.info("eqaula --> Ingreso a buscar bussinesEntity: "+getInstance());
            List<BussinesEntity> bussinesEntityList = bussinesEntityService.findBussinesEntityForType(getInstance());            
            log.info("eqaula --> Lista de bussinesEntity: " + bussinesEntityList.get(0).toString());
            if (!bussinesEntityList.isEmpty()) {                
                return (a = true);
//                for (BussinesEntity b : bussinesEntityList) {
////                    if(b.getType().getId()==getInstance().getId()){
////                        a = true;
////                    }                    
//                    a = false;
//                }            
            }else{
                return (a = false);                 
            }
//        } catch (Exception pe) {
//            log.info("eqaula --> error buscar bussinesEntity: " + pe.getMessage());
//        }        
    }
}
