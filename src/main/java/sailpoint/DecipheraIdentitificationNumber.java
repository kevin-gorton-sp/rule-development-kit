package sailpoint;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import sailpoint.server.IdnRuleUtil;
import sailpoint.rule.Identity;
import sailpoint.tools.Util;

public class DecipheraIdentitificationNumber {
  Logger log = LogManager.getLogger(UniqueUsernameGenerator.class);
  IdnRuleUtil idn;

  public Integer Main() {
    String value = "C";
    Integer userInt = 99;
    log.error("Identity Attribute Rule: Entering getLastUidInt");
    log.error("Identity Attribute Rule: function workerType: " + value);
    List identities = idn.findIdentitiesBySearchableIdentityAttribute("workerTypeDescription", "Equals", value,
        "identificationNumber");
    if (identities == null || identities.size() == 0) {
      log.error("Identity Attribute Rule: Not Identities found for workerType " + value);
      return userInt;
    }
    log.error("Identity Attribute Rule: size if loop: " + identities.size());
    int lastElement = identities.size() - 1;
    log.error("Identity Attribute Rule: size if loop lastElement: " + lastElement);
    Identity identity = (Identity) identities.get(lastElement);
    log.error("Identity Attribute Rule: identity" + identity.toString());
    Map attrs = identity.getAttributes();
    log.error("Identity Attribute Rule: attrs: " + attrs.toString());
    String userId = (String) attrs.get("identificationNumber");
    if (Util.isNullOrEmpty(userId)) {
      log.error("error");
      return userInt;
    }
    log.error("Identity Attribute Rule: userId: " + userId);
    userInt = Integer.parseInt(userId.replaceAll("[^0-9]", ""));
    log.error("Identity Attribute Rule: userInt: " + userInt);
    log.error("Identity Attribute Rule: Exiting getLastUidInt");
    return userInt;
    

  }
}
