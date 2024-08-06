package sailpoint;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;

import sailpoint.object.Application;
import sailpoint.object.Identity;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;

/**
 * UniqueUsernameGenerator
 */
public class UniqueUsernameGenerator {

  Logger log = LogManager.getLogger(UniqueUsernameGenerator.class);
  Identity identity = new Identity();
  Application application = new Application();
  IdnRuleUtil idn;

  int MAX_USERNAME_LENGTH = 12;

  public String generateUsername(String firstName, String lastName) throws GeneralException {
    firstName = StringUtils.trimToNull(firstName);
    lastName = StringUtils.trimToNull(lastName);
    String otherName = identity.getStringAttribute("otherName");

    if (firstName != null) {
      firstName = firstName.replaceAll("[^a-zA-Z0-9]", "");
    }

    if (lastName != null) {
      lastName = lastName.replaceAll("[^a-zA-Z0-9]", "");
    }

    if (otherName != null) {
      otherName = otherName.replaceAll("[^a-zA-Z0-9]", "");
    }

    if (firstName == null || lastName == null) {
      log.info("Generate Usename | first name or last name is null");
      return null;
    }

    String username = null;
    String fullName = firstName + "." + lastName;

    if (fullName.length() > MAX_USERNAME_LENGTH) {

      if (firstName.length() > (MAX_USERNAME_LENGTH - 2)) {
        for (int lastNameLength = 0; lastNameLength < lastName.length(); lastNameLength++) {
          username = firstName.substring(0, (MAX_USERNAME_LENGTH - 2)) + "." + lastName.charAt(lastNameLength);
          username = username.toLowerCase();
          if (isUnique(username)) {
            log.info("AD Genereate Username | Uniqu Username Generated: " + username);
            log.info("Exiting Generate Usename");
            return username;
          }

        }
      }
    }
    return null;
  }

  public boolean isUnique(String username) throws GeneralException {
    return !idn.accountExistsByDisplayName(application.getName(), username);

  }

  

}