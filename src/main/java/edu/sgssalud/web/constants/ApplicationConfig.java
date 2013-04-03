
package edu.sgssalud.web.constants;

import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
public class ApplicationConfig
{
   public static final String GUEST_ACCOUNT_NAME = "guest";

   private String siteName = "SgsSalud";
   private String blogUrl = "http://www.unl.edu.ec";
   private boolean analyticsEnabled = false;
   private String analyticsId = "";

   public String getBlogUrl()
   {
      return blogUrl;
   }

   public void setBlogUrl(final String blogUrl)
   {
      this.blogUrl = blogUrl;
   }

   public String getSiteName()
   {
      return siteName;
   }

   public void setSiteName(final String siteName)
   {
      this.siteName = siteName;
   }

   public boolean isAnalyticsEnabled()
   {
      return analyticsEnabled;
   }

   public void setAnalyticsEnabled(final boolean analyticsEnabled)
   {
      this.analyticsEnabled = analyticsEnabled;
   }

   public String getAnalyticsId()
   {
      return analyticsId;
   }

   public void setAnalyticsId(final String analyticsId)
   {
      this.analyticsId = analyticsId;
   }

   public boolean isDebugMode()
   {
      return ProjectStage.Development.equals(FacesContext.getCurrentInstance().getApplication().getProjectStage());
   }
}
