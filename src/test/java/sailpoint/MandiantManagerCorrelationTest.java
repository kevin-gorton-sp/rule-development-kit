package sailpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import bsh.EvalError;
import bsh.Interpreter;
import sailpoint.object.Link;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;

public class MandiantManagerCorrelationTest {
    Logger log = LogManager.getLogger(MandiantManagerCorrelationTest.class);

    private static final String RULE_FILENAME = "src/main/resources/rules/Rule - ManagerCorrelation - Manager googleUID Correlation.xml";

    @Test
    public void testWhereManagerHasEmail() throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        IdnRuleUtil idn = mock();
        Link link = mock();
        Object managerAttributeValue = mock();
        String managerId = "12345";

        when(link.getAttribute("manager_id")).thenReturn(managerId);
        List<sailpoint.rule.Identity> identities = new ArrayList<sailpoint.rule.Identity>();
        sailpoint.rule.Identity currentIdentity = mock();
        identities.add(currentIdentity);
        when(idn.findIdentitiesBySearchableIdentityAttribute(any(), any(), any(), any())).thenReturn(identities);
        Map<String, Object> identityAttributes = new HashMap<String, Object>();
        identityAttributes.put("googleUid", managerId);        
        log.debug(": " + identityAttributes);
        when(currentIdentity.getAttributes()).thenReturn(identityAttributes);

        i.set("log", log);
        i.set("idn", idn);
        i.set("link", link);
        i.set("managerAttributeValue", managerAttributeValue);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);

        // @SuppressWarnings("unchecked");
        Map<String, String> result = (Map<String, String>) i.eval(source);
        log.debug("Restult: " + result);
        // verify(link, times(1)).getAttribute("manager.email");

        assertNotNull(result);
        
        assertEquals(result.get("identityAttributeName"), "googleUid");
        assertEquals(result.get("identityAttributeValue"), managerId);

    }
}