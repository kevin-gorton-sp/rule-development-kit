package sailpoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import bsh.EvalError;
import bsh.Interpreter;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.server.IdnRuleUtil;

public class test_testParsing {
  Logger log = LogManager.getLogger(test_testParsing.class);
  private static final String RULE_FILENAME = "src/main/resources/rules/Rule - BeforeProvisioning - testParsing.xml";
                                               
  IdnRuleUtil idn = mock();
  
  @Test
  public void testParsing() throws EvalError {
    Interpreter i = new Interpreter();


    List<Object> linkedTemplateConfigs = new ArrayList<Object>();
        linkedTemplateConfigs.add("Id = T1111111111 ; Name = CUH IP RN 2018 ; LoginType = TestType");
        linkedTemplateConfigs.add("Id = T2222222222 ; Name = CUH IP RN 2018 ; StartDate = 02/22/22");
        linkedTemplateConfigs.add("Id = T3333333333 ; Name = CUH IP RN 2018");
        linkedTemplateConfigs.add("Id = T4444444444 ; Name = CUH IP RN 2018 ; StartDate = 04/04/24 ; LoginType = TestType");
        linkedTemplateConfigs.add("Id = T5555555555 ; Name = CUH IP RN 2018 ; StartDate = 02/20/20 ; LoginType = TestType ; EndDate = 05/05/25");
        linkedTemplateConfigs.add("Id = T6666666666 ; Name = CUH IP RN 2018 ; StartDate = 02/20/20 ; EndDate = 05/05/22 ; LoginType = TestType");
        linkedTemplateConfigs.add("Id = T7777777777 ; Name = CUH IP RN 2018");
        linkedTemplateConfigs.add("Id = T8888888888 ; Name = CUH IP RN 2018");
        linkedTemplateConfigs.add("Id = T8888888888 ; Name = CUH IP RN 2018");
        when(idn.getRawAccountAttribute(any(), any())).thenReturn(linkedTemplateConfigs);
    
    
    String result = "";
    i.set("log", log);
    i.set("idn", idn);
    String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
    result = (String) i.eval(source);
  }
}
