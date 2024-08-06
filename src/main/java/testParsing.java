import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import sailpoint.rule.Account;
import sailpoint.server.IdnRuleUtil;

public class testParsing {
  Logger log = LogManager.getLogger(UsernameGenerator.class);
  IdnRuleUtil idn;

  private String getLinkedTemplateConfigAttribute(String ltc, String name) {
    Integer length = name.length() + 3;
    Integer start = ltc.indexOf(name);
    Integer end = ltc.indexOf(";", ltc.indexOf(name));
    end = end == -1 ? ltc.length() + 1 : end;
    String attribute = start != -1 ? ltc.substring(start + length, end - 1) : "";
    return attribute;
  }

  private void buildLinkedTemplateConfig(String ltc) {
    log.debug("for LTC " + ltc);
    String ID = getLinkedTemplateConfigAttribute(ltc, "Id");
    log.debug("MYUID: [" + ID + "]");
    String startDate = getLinkedTemplateConfigAttribute(ltc, "StartDate");
    log.debug("Start: [" + startDate + "]");
    String endDate = getLinkedTemplateConfigAttribute(ltc, "EndDate");
    log.debug("End: [" + endDate + "]");
    String loginType = getLinkedTemplateConfigAttribute(ltc, "LoginType");
    log.debug("loginType: [" + loginType + "]");
    String templateConfig = ID + "#" + startDate + "#" + endDate + "#" + loginType;
    log.debug("TemplateConfig [" + templateConfig + "]");
  }

  private void test() {
    Account account = new Account();
    if (idn.getRawAccountAttribute(account, "LinkedTemplateConfig") instanceof List) {
      List currentLTCs = (List) idn.getRawAccountAttribute(account, "LinkedTemplateConfig");
      for (Object ltc : currentLTCs) {
        buildLinkedTemplateConfig(ltc.toString());
      }
    } else if (idn.getRawAccountAttribute(account, "LinkedTemplateConfig") instanceof String){
      String ltc = (String) idn.getRawAccountAttribute(account, "LinkedTemplateConfig");
      buildLinkedTemplateConfig(ltc);
    }

  }

}
