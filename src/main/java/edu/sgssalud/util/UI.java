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
package edu.sgssalud.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.controller.profile.GroupHome;
import edu.sgssalud.model.BussinesEntity;
import edu.sgssalud.model.BussinesEntityAttribute;
import edu.sgssalud.model.Group;
import edu.sgssalud.model.Property;
import edu.sgssalud.service.BussinesEntityService;

@Named("ui")
@RequestScoped
public class UI {

    @Inject
    @Web
    private EntityManager em;
    @Inject
    protected BussinesEntityService bussinesEntityService;

    @PostConstruct
    public void init() {
        bussinesEntityService.setEntityManager(em);
    }

    public List<Property> getProperties(BussinesEntity entity) {
        if (entity == null) {
            return new ArrayList<Property>();
        }

        if (entity.getType() == null) {
            return new ArrayList<Property>();
        }

        Query q = em.createNamedQuery("Property.findByBussinesEntityTypeName");
        q.setParameter("bussinesEntityTypeName", entity.getType().getName());
        Collections.sort(q.getResultList());
        return q.getResultList();

    }

    public List<BussinesEntityAttribute> getAttributes(BussinesEntity entity, String names) {
        List<String> types = Lists.stringToList(names);
        Query q = em.createNamedQuery("BussinesEntityAttribute.findByBussinesEntityIdAndTypes");
        q.setParameter("bussinesEntityName", entity.getType().getName());
        q.setParameter("types", types);
        Collections.sort(q.getResultList());
        return q.getResultList();

    }

    public List<GroupHome.ColumnModel> findColumnsTemplate(Group g) {
        List<GroupHome.ColumnModel> columns = new ArrayList<GroupHome.ColumnModel>();
        BussinesEntity template = makeBussinessEntity(g);
        //List<ColumnModel> _columns = new ArrayList<ColumnModel>();
        for (BussinesEntityAttribute a : template.getAttributes()) {
            if (a.getProperty().isShowInColumns()) {
                columns.add(new GroupHome.ColumnModel(a.getProperty().getLabel(), a.getProperty().getName()));
            }
        }
        if (columns.isEmpty() || g.getProperty().isShowDefaultBussinesEntityProperties()) {
            //TODO aplicar internacionalización
            columns.add(new GroupHome.ColumnModel("name", "name"));
            columns.add(new GroupHome.ColumnModel("code", "code"));
        }

        return columns;
    }

    public BussinesEntity makeBussinessEntity(Group g) {
        Date now = Calendar.getInstance().getTime();
        //TODO internacionalizar cadenas estáticas
        String name = "New instance";
        BussinesEntity entity = new BussinesEntity();
        entity.setName(name);
        //TODO implementar generador de códigos para entidad de negocio
        entity.setCode("NewCode");
        entity.setCreatedOn(now);
        entity.setLastUpdate(now);
        entity.setActivationTime(now);
        entity.setExpirationTime(Dates.addDays(now, 364));
        entity.setAuthor(null); //Establecer al usuario actual
        entity.buildAttributes(g.getName(), bussinesEntityService); //Construir atributos de grupos
        return entity;
    }

    public Group getGroup(BussinesEntity entity, Property p) {
        Query q = em.createNamedQuery("Group.findByBussinesEntityIdAndPropertyId");
        q.setParameter("bussinesEntityId", entity.getId());
        q.setParameter("propertyId", p.getId());
        return q.getResultList().isEmpty() ? new Group() : (Group) q.getResultList().get(0);

    }

    
     public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }
    

    public List<SelectItem> getValuesAsSelectItem(List<Object> values) {
        List<SelectItem> items = new ArrayList<SelectItem>();
        SelectItem item = null;
        item = new SelectItem(null, UI.getMessages("common.choice"));
        items.add(item);
        for (Object o : values) {
            item = new SelectItem(cleanValue(o), cleanValue(o).toString());
            items.add(item);
        }

        return items;
    }

    public static String getMessages(String key) {
        FacesContext fc = FacesContext.getCurrentInstance();
        Locale myLocale = fc.getExternalContext().getRequestLocale();
        ResourceBundle myResources = ResourceBundle.getBundle("org.eqaula.messages", myLocale);

        return myResources.containsKey(key) ? myResources.getString(key) : key;
    }

    private Object cleanValue(Object value) {
        
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)){
            return value;
        }
        
        String cleaned = value.toString();

        if (cleaned.contains("*")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }
    
}
