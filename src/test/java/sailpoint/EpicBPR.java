package sailpoint;

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
import sailpoint.object.Application;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningPlan.Operation;
import sailpoint.rdk.utils.RuleXmlUtils;
import sailpoint.rule.Account;
import sailpoint.rule.ManagedAttributeDetails;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;
import sailpoint.object.Attributes;

public class EpicBPR {
    Logger log = LogManager.getLogger(EpicBPR.class);

    private static final String RULE_FILENAME = "src/main/resources/rules/Rule - BeforeProvisioning - Epic Linked Template Config.xml";

    @Test
    public void testProvisioning() throws GeneralException, EvalError {
        Interpreter i = new Interpreter();

        ProvisioningPlan plan = mock(ProvisioningPlan.class);

        Application application = mock(Application.class);
        IdnRuleUtil idn = mock();

        AccountRequest accountRequest = mock(AccountRequest.class);

        List<AccountRequest> accountRequests = new ArrayList<>();
        accountRequests.add(accountRequest);
        Attributes<String, Object> attributes = new Attributes<String, Object>();
        Map<String, Object> cloudServicesIDNSetup = new HashMap<>();
        cloudServicesIDNSetup.put("loginTypeLookupSource", "test");
        cloudServicesIDNSetup.put("defaultLoginType", "Test");
        attributes.putClean("cloudServicesIDNSetup", cloudServicesIDNSetup);

        when(application.getAttributes()).thenReturn(attributes);
        when(plan.getAccountRequests()).thenReturn(accountRequests);
        AttributeRequest linkedTemplate = mock(AttributeRequest.class);

        when(accountRequest.getAttributeRequest("LinkedTemplateID")).thenReturn(linkedTemplate);
        String linkedTemplateId = "T2222222222";
        when(linkedTemplate.getValue()).thenReturn(linkedTemplateId);

        Operation linkedTemplateOp = Operation.Add;
        when(linkedTemplate.getOp()).thenReturn(linkedTemplateOp);

        when(application.getId()).thenReturn("EPIC");

        ManagedAttributeDetails linkedTemplateDetails = mock(ManagedAttributeDetails.class);
        when(idn.getManagedAttributeDetails(any(), any(), any(), any())).thenReturn(linkedTemplateDetails);
        Map<String, Object> linkedTemplateAttributes = new HashMap<String, Object>();
        linkedTemplateAttributes.put("displayName", "linkedTemplate");
        when(linkedTemplateDetails.getAttributes()).thenReturn(linkedTemplateAttributes);

        Account loginTypeAccount = mock(Account.class);
        when(idn.getAccountByDisplayName(any(), any())).thenReturn(loginTypeAccount);
        when(idn.getAccountByNativeIdentity(any(), any())).thenReturn(loginTypeAccount);
        Map<String, Object> loginAccountAttributes = new HashMap<String, Object>();
        loginAccountAttributes.put("LoginType", "HCM");
        when(loginTypeAccount.getAttributes()).thenReturn(loginAccountAttributes);
        List<Object> linkedTemplateConfigs = new ArrayList<Object>();
        linkedTemplateConfigs.add("Id = T1111111111 ; Name = CUH IP RN 2018 ; LoginType = TestType");
        linkedTemplateConfigs
                .add("Id = T2222222222 ; Name = CUH IP RN 2018 ; StartDate = 02/22/22 ; LoginType = [TestType]");
        linkedTemplateConfigs.add("Id = T3333333333 ; Name = CUH IP RN 2018 ; LoginType = [Canto / Haiku, Rover]");
        linkedTemplateConfigs
                .add("Id = T4444444444 ; Name = CUH IP RN 2018 ; StartDate = 04/04/24 ; LoginType = TestType");
        linkedTemplateConfigs.add(
                "Id = T5555555555 ; Name = CUH IP RN 2018 ; StartDate = 02/20/20 ; LoginTypes = [Canto / Haiku, Rover] ; EndDate = 05/05/25");
        linkedTemplateConfigs.add(
                "Id = T6666666666 ; Name = CUH IP RN 2018 ; StartDate = 02/20/20 ; EndDate = 05/05/22 ; LoginType = TestType");
        linkedTemplateConfigs.add("Id = T7777777777 ; Name = CUH IP RN 2018");
        linkedTemplateConfigs.add("Id = T8888888888 ; Name = CUH IP RN 2018");
        linkedTemplateConfigs.add("Id = T8888888888 ; Name = CUH IP RN 2018");

        when(idn.getRawAccountAttribute(any(), any())).thenReturn(linkedTemplateConfigs);

        String result = "";

        i.set("log", log);
        i.set("plan", plan);
        i.set("application", application);
        i.set("idn", idn);

        String source = RuleXmlUtils.readRuleSourceFromFilePath(RULE_FILENAME);
        result = (String) i.eval(source);

        log.info("Beanshell script returned: " + result);
    }
}
