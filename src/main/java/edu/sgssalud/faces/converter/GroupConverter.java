/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sgssalud.faces.converter;


import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import edu.sgssalud.cdi.Web;
import edu.sgssalud.model.Group;
import edu.sgssalud.model.profile.Profile;
import edu.sgssalud.service.BussinesEntityService;

/**
 *
 * @author jlgranda
 */
@RequestScoped
@FacesConverter("org.eqaula.glue.faces.converter.GroupConverter")
public class GroupConverter implements Converter, Serializable {
    private static final long serialVersionUID = -3057944404700510467L;

   @Inject
   @Web
   private EntityManager em;

   @Inject
   private BussinesEntityService bs;

    @PostConstruct
    public void setup() {

        System.out.println("GroupConverter started up");

    }

    @PreDestroy
    public void shutdown() {

        System.out.println("GroupConverter shutting down");

    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {

            
        if (value != null && !value.isEmpty() && bs != null) {


            try {
                bs.setEntityManager(em);
                return (Group) bs.find(Group.class, getKey(value).longValue());
            } catch (NoResultException e) {
                return new Profile();
            }

        }

        return null;
    }

    private Long getKey(String value) {
        //get id value from string
        int start = value.indexOf("id=");
        int end = value.indexOf(",") == -1 ? value.indexOf("]") : value.indexOf(",");
        return Long.valueOf(value.substring(start + 3, end).trim());
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object value) {
        return value.toString();
    }
}
