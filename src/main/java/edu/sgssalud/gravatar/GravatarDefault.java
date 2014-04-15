/**
*
*/
package edu.sgssalud.gravatar;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public enum GravatarDefault
{
   STATUS_404("404"), MYSTERY_MAN("mm"), IDENTICON("identicon"), MONSTER_ID("monsterid"), WAVATAR("wavatar"), RETRO(
            "retro");

   private String key;

   private GravatarDefault(final String key)
   {
      this.key = key;
   }

   public String getKey()
   {
      return key;
   }

   @Override
   public String toString()
   {
      return key;
   }
}
