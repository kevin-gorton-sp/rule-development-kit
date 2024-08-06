package sailpoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import bsh.EvalError;
import bsh.Interpreter;
import sailpoint.object.Application;
import sailpoint.object.Identity;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;

public class UniqueUsernameGeneratorTest {
  Logger log = LogManager.getLogger(UniqueUsernameGeneratorTest.class);
  private static final String RULE_FILENAME = "src/main/resources/rules/Rule - AttributeGenerator - UniqueUsernameGenerator.xml";
  
  @Test
  public void testFirstnameGreaterThanTheMaxlength() throws GeneralException, EvalError {
    Interpreter i = new Interpreter();
    IdnRuleUtil idn = mock();
    when(idn.accountExistsByDisplayName(any(), any())).thenReturn(true).thenReturn(false);
    Application application = mock(Application.class);
    when(application.getName()).thenReturn("Active Directory [source]");

    Identity identity = mock(Identity.class);
    when(identity.getFirstname()).thenReturn("Michaelangelo");
    when(identity.getLastname()).thenReturn("Smith");
    when(identity.getStringAttribute("otherName")).thenReturn("");

    String result = "";

    i.set("log", log);
    i.set("idn", idn);
    i.set("application", application);
    i.set("identity", identity);

    String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
    result = (String) i.eval(source);

    log.info("BeanShell script returned result: " + result);
  }

  public void testLastnameTooLong() {
    Interpreter i = new Interpreter();
    IdnRuleUtil idn = mock();

    
  }

  
}
