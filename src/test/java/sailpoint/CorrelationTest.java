package sailpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import bsh.EvalError; 
import bsh.Interpreter;
import sailpoint.object.Application;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.rule.Account;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;

public class CorrelationTest {
    Logger log = LogManager.getLogger(CorrelationTest.class);

    private static final String RULE_FILENAME = "src/main/resources/rules/Rule - Correlation - DecipheraAdAdminCorrelationRule.xml";

    @Test
    public void testAccountInformation () throws GeneralException, EvalError {
        Interpreter i = new Interpreter();
        
        IdnRuleUtil idn = mock();
        Account account = mock();
        Application application = mock();
        
        Map myAttributes = new HashMap();
        myAttributes.put("sAMAccountName", "adm-jkalle");

        when(account.getAttributes()).thenReturn(myAttributes);

        i.set("log", log);
        i.set("idn", idn);
        i.set("account", account);
        i.set("application", application);


        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
        
        @SuppressWarnings("unchecked")
        // Map<String, String> result = (Map<String, String>) i.eval(source);
        
        String result = (String) i.eval(source);
        // assertNotNull(result);
    }
}
