/**
*
*/
package edu.sgssalud.gravatar;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public enum GravatarRating
{
   G, PG, R, X;

   public String getRating()
   {
      return name().toLowerCase();
   }

   @Override
   public String toString()
   {
      return getRating();
   }
}
