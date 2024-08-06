package sailpoint;

import org.apache.log4j.Logger;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;

import sailpoint.object.Application;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.rule.Account;
import sailpoint.server.IdnRuleUtil;

public class Correlation {
  
  IdnRuleUtil idn;
  Account account;
  Application application;

  private Map correlation() {

    Map returnMap = new HashMap();
    String newSAMAccountName = null;
    Map attributes = account.getAttributes();
    if (attributes != null && attributes.containsKey("sAMAccountName")) {
      newSAMAccountName = (String)attributes.get("sAMAccountName");
    }

    String parseString = newSAMAccountName.substring(0, 3);

    if (parseString == "adm-") {
      newSAMAccountName = newSAMAccountName.substring(4);
    }

    returnMap.put("identityAttributeName", "sAMAccountName");
    returnMap.put("identityAttributeValue", newSAMAccountName);

    return returnMap;

  }
}