/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sgssalud.faces.converter;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.view.Location;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

/**
 *
 * @author lucho
 */
@ConversationScoped
@FacesConverter(value = "org.eqaula.glue.faces.converter.GenericConverter")
public class GenericConverter implements Serializable, Converter {
    private static final long serialVersionUID = -1426027895207895464L;

    //@PersistenceContext(name="lotcarPU")
    //@Inject @Any
    private EntityManager em;

    public GenericConverter() {
        try {
            Properties p = System.getProperties();
            System.out.println("POLICY ADDED");
            final Context ctx = new InitialContext(p);
            em = (EntityManager) //buscamos el contexto devuelve el objeto y lo convierte al em
                    //java:comp/evn es obligatorio
                    ctx.lookup("java:comp/env/persistence/em");
            //ctx.lookup("java:comp/env/[name of your persistence unit here]");

        } catch (final NamingException ne) {
            ne.printStackTrace();
        }
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string.trim().equals("")) {
            return null;
        } else {
            try {
                Class c = getClazz(fc, uic);
                return em.find(c, Long.parseLong(string));
            } catch (Exception e) {
                e.printStackTrace();
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Conversion Error"));
            }
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o == null || o.equals("")) {
            return "";
        } else {
            try {
                return String.valueOf(getId(o));
            } catch (Exception e) {
                e.printStackTrace();
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Conversion Error"));
            }
        }
    }

    private Long getId(Object o)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class clazz = o.getClass();
        Method method = clazz.getMethod("getId");
        Object value = method.invoke(o);
        return (Long) value;
    }

    @SuppressWarnings("unchecked")
    private Class getClazz(FacesContext facesContext, UIComponent component) {

        ELContext elContext = facesContext.getELContext();
        ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory()
                .createValueExpression(elContext, component.getValueExpression("value").getExpressionString(), Location.class);

        return valueExpression.getType(facesContext.getELContext());
    }
}
