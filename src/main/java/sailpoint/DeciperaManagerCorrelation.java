package sailpoint;

import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;

import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.server.IdnRuleUtil;

public class DeciperaManagerCorrelation {

  private Map correlate() {
    IdnRuleUtil idn;
    Link link = new Link();
    Logger log = LogManager.getLogger(DeciperaManagerCorrelation.class);



// TODO 1. Get manager Attribute from current identity
// TODO 2. match against First last concat on Identities.


    Map returnMap = new HashMap();
    Identity identity = link.getIdentity();

    String managerEmail = (String) link.getAttribute("manager.email");
    returnMap.put("identityAttributeName", "email");
    returnMap.put("identityAttributeValue", managerEmail);

    return returnMap;
  }

}
