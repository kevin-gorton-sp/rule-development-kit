package sailpoint;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import sailpoint.object.Application;
import sailpoint.object.Identity;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DecipheraEmployeeNumberGeneratorTest {
    Logger log = LogManager.getLogger(UsernameGeneratorTest.class);

    private static final String RULE_FILENAME = "src/main/resources/rules/Rule - AttributeGenerator - DecipheraEmployeeNumberGenerator.xml";

    @Test
    public void testUsernameGeneratorWhereTypeIsConsultantValid () throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        IdnRuleUtil idn = mock();
        when(idn.accountExistsByDisplayName(any(), any())).thenReturn(false);

        Application application = mock(Application.class);
        when(application.getName()).thenReturn("Active Directory [source]");
        
        Identity identity = mock(Identity.class);
        when(identity.getAttribute("workerType")).thenReturn("consultant");
        String result = "";

        Field field = mock(Field.class);
        i.set("log", log);
        i.set("idn", idn);
        i.set("application", application);
        i.set("identity", identity);
        i.set("field", field);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
        result = (String) i.eval(source);

        assertNotNull(result);
        assertEquals(result, "tyler.smith");

        log.info("Beanshell script returned: " + result);

    }

    @Test
    public void testUsernameGeneratorWhereFirstAndLastLongerThanMAXLENGTH () throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        IdnRuleUtil idn = mock();
        when(idn.accountExistsByDisplayName(any(), any())).thenReturn(false);

        Application application = mock(Application.class);
        when(application.getName()).thenReturn("Active Directory [source]");

        Identity identity = mock(Identity.class);
        when(identity.getFirstname()).thenReturn("Kiefer");
        when(identity.getLastname()).thenReturn("Sutherland");
        when(idn.findAccountsByAttribute(any(), any(), any(), any()).thenReturn(new ArrayList()));
        String result = "";

        i.set("log", log);
        i.set("idn", idn);
        i.set("application", application);
        i.set("identity", identity);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
        result = (String) i.eval(source);

        assertNotNull(result);
        assertEquals(result, "kiefer.s");

        log.info("Beanshell script returned: " + result);

    }

    @Test
    public void testUniqueLogic () throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        IdnRuleUtil idn = mock();
        when(idn.accountExistsByDisplayName(any(), any())).thenReturn(true).thenReturn(true).thenReturn(false);

        Application application = mock(Application.class);
        when(application.getName()).thenReturn("Active Directory [source]");
        
        
        Identity identity = mock(Identity.class);
        when(identity.getAttribute("workerType")).thenReturn("consultant");
        when(identity.getName()).thenReturn("Some Identity");
        String result = "";

        i.set("log", log);
        i.set("idn", idn);
        i.set("application", application);
        i.set("identity", identity);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
        result = (String) i.eval(source);

        assertNotNull(result);
        assertEquals(result, "kiefer.t");

        log.info("Beanshell script returned: " + result);

    }
}
