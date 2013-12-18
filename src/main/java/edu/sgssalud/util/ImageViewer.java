/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sgssalud.util;

//import com.smartics.common.dao.CrudService;
//import com.smartics.common.dao.QueryParameter;
//import com.smartics.lotcar.common.model.Attachment;

import edu.sgssalud.model.Photos;
import edu.sgssalud.service.generic.CrudService;
import edu.sgssalud.service.generic.QueryParameter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author wduck
 */
@Named(value = "imageViewer")
//@SessionScoped
@RequestScoped
public class ImageViewer implements Serializable {

    /**
     * Creates a new instance of ImagesViewer
     */
    @EJB
    CrudService crudService;

    public ImageViewer() {
    }

//    public StreamedContent getImage() throws IOException {
//        FacesContext context = FacesContext.getCurrentInstance();
//        System.out.println("==== INGRESO A GET IMAGE");
//        //if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
//            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
//        //    return new DefaultStreamedContent();
//        }
//        else {
//            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
//            //String id = context.getExternalContext().getRequestParameterMap().get("id");
//            String image = context.getExternalContext().getRequestParameterMap().get("id");
//            
//            //Image image = service.find(Long.valueOf(id));
//            System.out.println("==== INGRESO A GET IMAGE BYTE " + image.getBytes());
//            return new DefaultStreamedContent(new ByteArrayInputStream(image.getBytes()));
//        }
//    }
    private StreamedContent findPhoto(String namedQuery, final Long id) {
        //System.out.println("=== FIND findAttachment : " + namedQuery + ", param: " + id);
        Photos image = null;
        if (id > 0) {
            try {

                image = crudService.findSingleResultWithNamedQuery(namedQuery,
                        QueryParameter.with("id", id).parameters());
                //System.out.println("image " + image.getPhoto());
            } catch (Exception e) {
                System.out.println("=== ERROR findAttachment : " + e.getMessage() + ", image " + image);
            }
        }
        return buildStreamedContent(image);
    }

    private StreamedContent showPacienteFoto(Long id) {
        return findPhoto("Photos.buscarPorPacienteId", id);
    }

    public StreamedContent getPacienteFoto() {
        //StreamedContent sc = null;
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {

            //System.out.println("=== getPhoto PhaseId.RENDER_RESPONSE : ");
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            File file = new File(getRealPath("/resources/images/paciente.png"));
            byte[] data = UtilRoot.getConverter(file);
            InputStream dbStream = new ByteArrayInputStream(data);            
            return new DefaultStreamedContent(dbStream, new MimetypesFileTypeMap().getContentType(file));
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String pacienteSdtr = context.getExternalContext().getRequestParameterMap().get("pacienteId");
            Long pacienteId = Long.valueOf(pacienteSdtr);
            return showPacienteFoto(pacienteId);             
        }
    }

    private String getRealPath(String path) {
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        return context.getRealPath(path);
    }

    private StreamedContent buildStreamedContent(Photos photo) {

        if (photo == null) {
            //System.out.println("----> @RequestScoped Attachment NULL");
            return new DefaultStreamedContent();
            //return null;
        }

        if (photo.getPhoto() != null && photo.getPhoto().length > 0) {
//            System.out.println("----> @RequestScoped Attachment getData() " + attachment.getData() + ", " 
//                    + attachment.getName() + ", " + attachment.getContentType());

            InputStream dbStream;
            dbStream = new ByteArrayInputStream(photo.getPhoto());
            return new DefaultStreamedContent(dbStream, photo.getContentType(), photo.getName());
        }
        return new DefaultStreamedContent();
        //return null;
    }
    /*
     private StreamedContent showMarkImage(Long id) {
     return findPaciente("AttachmentMarkImage.findByVehicleId", id);
     }
    
     public StreamedContent getMarkImage(){
     FacesContext context = FacesContext.getCurrentInstance();

     if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
     System.out.println("=== getPhoto PhaseId.RENDER_RESPONSE : ");
     // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
     return new DefaultStreamedContent();
     } else {
     // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
     String vehicleIdStr = context.getExternalContext().getRequestParameterMap().get("vehicleId");
     Long vehicleId = Long.valueOf(vehicleIdStr);
     return showMarkImage(vehicleId);
     }
     }*/

//    private StreamedContent buildStreamedContent(Attachment attachment){
//        
//        if (attachment == null){
//            System.out.println("----> @RequestScoped Attachment NULL");
//            return new DefaultStreamedContent();
//            //return null;
//        }
//        
//        if (attachment.getData() != null && attachment.getData().length > 0){
//        
//            System.out.println("----> @RequestScoped Attachment getData() " + attachment.getData() + ", " 
//                    + attachment.getName() + ", " + attachment.getContentType());
//            InputStream dbStream;        
//            dbStream = new ByteArrayInputStream(attachment.getData());
//            return new DefaultStreamedContent(dbStream, attachment.getContentType(), attachment.getName());
//        }
//        return new DefaultStreamedContent();
//        //return null;
//    }
}
