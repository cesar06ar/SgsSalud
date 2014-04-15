/*** 
*
* You should have received a copy of the GNU General Public License
* along with Glue. If not, see <http://www.gnu.org/licenses/>.
*
* For individuals or entities who wish to use Glue privately, or
* internally, the following terms do not apply:
*
* For OEMs, ISVs, and VARs who wish to distribute Glue with their
* products, or host their product online, Eqaula provides flexible
* OEM commercial licenses.
*
* Optionally, Customers may choose a Commercial License. For additional
* details, contact an Eqaula representative (sales@eqaula.org)
*/
package edu.sgssalud.gravatar;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Named;
import edu.sgssalud.util.Assert;
import edu.sgssalud.util.StringValidations;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named
public class Gravatars
{
   private static final String GRAVATAR_BASE_URL = "http://www.gravatar.com/avatar/";
    //private static final String GRAVATAR_BASE_URL = "http://www.gmail.com";
   public String getURLFor(final String email)
   {
      return getURLFor(email, 140, GravatarDefault.MYSTERY_MAN, GravatarRating.G);
   }

   public String getURLFor(final String email, final int size)
   {
      return getURLFor(email, size, GravatarDefault.MYSTERY_MAN, GravatarRating.G);
   }

   public String getURLFor(final String email, final int size, final GravatarDefault deflt, final GravatarRating rating)
   {
      Assert.isTrue((size >= 1) && (size <= 200), "The Gravatar must be anywhere from 1px to 200px in size");
      Assert.notNull(email != null, "The email address provided was null");
      Assert.notNull(deflt, "Must supply a Gravatar default for when the email address is not registered");
      Assert.notNull(rating, "Must supply a Gravatar rating");
      Assert.isTrue(StringValidations.isEmailAddress(email), "The email address provided was invalid");
      try
      {
         String hash = getMD5(email);

         String url = GRAVATAR_BASE_URL + hash + "?s=" + size + "&d=" + deflt + "&r=" + rating;
         return url;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not build MD5 checksum for User email [" + email + "]", e);
      }
   }

   private String getMD5(final String message)
   {
      try
      {
         MessageDigest md =
                  MessageDigest.getInstance("MD5");
         return hex(md.digest(message.getBytes("CP1252")));
      }
      catch (NoSuchAlgorithmException e)
      {
      }
      catch (UnsupportedEncodingException e)
      {
      }
      return null;
   }

   private String hex(final byte[] array)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < array.length; ++i)
      {
         sb.append(Integer.toHexString((array[i]
                  & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString();
   }

}
