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
package edu.sgssalud.controller;

import java.io.Serializable;
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
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.BussinesEntityType;
import edu.sgssalud.model.Property;
import edu.sgssalud.model.Structure;
import edu.sgssalud.service.BussinesEntityService;
import edu.sgssalud.service.BussinesEntityTypeService;
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
        bussinesEntityService.setEntityManager(em);
    }

    @Override
    public Class<BussinesEntityType> getEntityClass() {
        return BussinesEntityType.class;
    }

    @TransactionAttribute
    public String saveBussinesEntityType() {
        log.info("eqaula --> saving " + getInstance().getName());
        if (getInstance().isPersistent()) {
            getInstance().getStructures().get(0).setName(getInstance().getName());
            save(getInstance());
        } else {
            try {
                log.info("eqaula --> saving new" + getInstance().getName());
                create(getInstance());
                setBussinesEntityTypeId(getInstance().getId());
                wire();
                getInstance().getStructures().get(0).setName(getInstance().getName());
                save(getInstance());

            } catch (Exception ex) {
                log.info("eqaula --> error saving new" + getInstance().getName());
            }
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

    @Transactional
    public String deleteBussinesEntityType() {
        try {
            if (getInstance() == null) {
                throw new NullPointerException("property is null");
            }

            if (getInstance().isPersistent()) {
                delete(getInstance());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se borró exitosamente:  " + getInstance().getName(), ""));
                //RequestContext.getCurrentInstance().execute("editDlg.hide()"); //cerrar el popup si se grabo correctamente

            } else {
                //remover de la lista, si aún no esta persistido
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No se puede borrar este tipo de entidad de negocio", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORE", e.toString()));
        }
        return "/pages/admin/bussinesentitytype/list";
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
        if (!isIdDefined()) {
            return false;
        }
        boolean b = false;        
        List<BussinesEntity> bussinesEntityList = bussinesEntityService.findBussinesEntityForType(getInstance());
        b = (!bussinesEntityList.isEmpty() || isPropertyAssociatedBussinesEntityAttributes());
        log.info("eqaula --> Ingreso a verificar bussinesEntity: " + b);  //true si esta asociado 
        return b;
    }
    
      
    public boolean isPropertyAssociatedBussinesEntityAttributes(){
        if (!isIdDefined()) {
            return false;
        }
        for (Property p : getInstance().getStructures().get(0).getProperties()) {
            if(!bussinesEntityService.findBussinesEntityAttributeForProperty(p).isEmpty()){
                return true;   //retorna true si esta relacionado con Atributos
            }            
        }        
        return false;
    }
    
    
}
