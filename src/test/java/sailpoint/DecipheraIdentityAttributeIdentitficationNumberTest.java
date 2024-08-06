package sailpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.any;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import bsh.EvalError;
import bsh.Interpreter;
import sailpoint.rule.Identity;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;

public class DecipheraIdentityAttributeIdentitficationNumberTest {
    Logger log = LogManager.getLogger(DecipheraIdentityAttributeIdentitficationNumberTest.class);

    private static final String RULE_FILENAME = "src/main/resources/rules/Rule - IdentityAttribute - Example Rule.xml";

    @Test
    public void testWhereActive() throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        IdnRuleUtil idn = mock();
        Identity identity = mock(Identity.class);

        List<Identity> identites = new ArrayList<Identity>();
        Identity id1 = mock(Identity.class);
        
        Identity id2 = mock(Identity.class);
        Identity id3 = mock(Identity.class);
        Identity id4 = mock(Identity.class);
        Identity id5 = mock(Identity.class);
        Identity id6 = mock(Identity.class);
        
        // identites.add(id1);
        // identites.add(id2);
        // identites.add(id3);
        
        when(idn.findIdentitiesBySearchableIdentityAttribute(any(), any(), any(), any())).thenReturn(identites);

        Map<String, Object> attrs = new HashMap<String, Object>();
        // attrs.put("identificationNumber", "C109");
        attrs.put("uid", "C1345FD");
        attrs.put("anotherAttribute", "test");
        attrs.put("identificationNumber", "CDFRGTHJS");
        log.debug(attrs);
        when(id3.getAttributes()).thenReturn(attrs);
        
        i.set("log", log);
        i.set("idn", idn);
        i.set("identity", identity);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);

        Object result = i.eval(source);

        /// verify(identity, times(4)).getAttribute(argThat(date ->
        /// "startDate".equals(date) || "endDate".equals(date)));

        // assertNotNull(result);
        log.error("the result is " + result);

    }

}
