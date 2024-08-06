
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import sailpoint.object.Application;
import sailpoint.object.Attributes;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ManagedAttribute.Type;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningPlan.Operation;
import sailpoint.rule.Account;
import sailpoint.rule.ManagedAttributeDetails;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;
import sailpoint.object.Identity;

public class EpicBPM {
  Logger log = LogManager.getLogger(UsernameGenerator.class);
  ProvisioningPlan plan = new ProvisioningPlan();
  Application application = new Application();
  IdnRuleUtil idn;

  Identity identity = plan.getIdentity();
  String loginTypeLookupSource = "";
  String defaultLoginType = "TestLOGIN";

  /**
   * 
   * Configure using Source Attributes
   * 
   * "cloudServicesIDNSetup": {
   * "loginTypeLookupSource" : "Source name",
   * "defaultLoginType: "Default Login Type"
   * }
   * 
   */

  /**
   * retrieds the Linked Template name from the Process plan
   * 
   * @param id
   * @return
   */
  private String getLinkedTemplateName(String id) {
    log.debug("EPIC LTC BPR - getLinkedTemplateName - LinkedTemplateID: " + id);
    log.debug("EPIC LTC BPR - getLinkedTemplateName - Current Application ID: " + application.getId());
    // Get details of LinkedTemplate object by LinkedTemplateID
    ManagedAttributeDetails linkedTemplateDetails = idn.getManagedAttributeDetails(application.getId(),
        "LinkedTemplateID", id, Type.Entitlement);
    // Get the attributes and name fom the LinkedTemplates entitlement object
    Map linkedTemplateAttributes = linkedTemplateDetails != null ? linkedTemplateDetails.getAttributes() : null;
    log.debug(
        "EPIC LTC BPR - getLinkedTemplateName - linkedTemplateAttributes: " + linkedTemplateAttributes.toString());
    String linkedTemplateName = linkedTemplateAttributes != null
        && linkedTemplateAttributes.containsKey("LinkedTemplateName")
            ? (String) linkedTemplateAttributes.get("LinkedTemplateName")
            : null;
    log.debug("EPIC LTC BPR - getLinkedTemplateName - LinkedTemplateName: " + linkedTemplateName);
    return linkedTemplateName;
  }

  /**
   * Gets Login Type from Source loginTypeLookupSource
   * ("Epic DefaultLinkedTemplateID [source]")
   * 
   * @param name loginTypeLookupSource
   * @return loginType
   * @throws GeneralException
   */
  private String getLoginType(String name) throws GeneralException {
    log.debug("EPIC LTC BPR - getloginType - lookup source: " + loginTypeLookupSource);
    log.debug("EPIC LTC BPR - getloginType - lookup name: " + name);
    // Get record from account Source by its Display Name
    Account loginTypeAccount = idn.getAccountByDisplayName(loginTypeLookupSource, name);
    log.debug("EPIC LTC BPR - getloginType - LoginTypeAccount: " + loginTypeAccount.toString());
    Map loginTypeAttributes = loginTypeAccount != null ? loginTypeAccount.getAttributes() : null;
    log.debug("EPIC LTC BPR - getloginType - loginTypeAttributes: " + loginTypeAttributes.toString());
    String loginType = loginTypeAttributes != null ? (String) loginTypeAttributes.get("LoginType") : "";
    log.debug("EPIC LTC BPR - getloginType - loginType: " + loginType);
    if (loginType == null || loginType == "") {
      return defaultLoginType;
    }
    return loginType;
  }

  /**
   * Places a Remove and Add ACtion for Linked Template Config Hashes
   * 
   * @param ltcToRemove
   * @param accountRequest
   */
  private void removeLinkedTemplateConfig(String ltcToRemove, AccountRequest accountRequest) {
    log.error("EPIC LTC BPR - removeLinkedTemplateConfig - Removing LinkedTemplateConfig: " + ltcToRemove);
    accountRequest.add(new AttributeRequest("LinkedTemplateConfig", ProvisioningPlan.Operation.Remove, ltcToRemove));
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
    String nowDate = formatter.format(new Date());
    String[] split = ltcToRemove.split("#", -1);
    split[2] = nowDate;
    String newLTC = String.join("#", split);
    log.error("EPIC LTC BPR - removeLinkedTemplateConfig - Adding LinkedTemplateConfig: " + newLTC);
    accountRequest
        .add(new AttributeRequest("LinkedTemplateConfig", ProvisioningPlan.Operation.Add, newLTC));
  }

  /**
   * builds Create LinkedTemplateConfig Hash
   * 
   * @param linkedTemplateId
   * @return
   * @throws GeneralException
   */
  private String linkedTemplateConfigString(String linkedTemplateId) throws GeneralException {
    String linkedTemplateName = linkedTemplateId != null ? getLinkedTemplateName(linkedTemplateId) : null;
    log.debug("EPIC LTC BPR - linkedTemplateConfigString - linkedTemplateName: " + linkedTemplateName);
    String loginType = getLoginType(linkedTemplateName);
    log.debug("EPIC LTC BPR - linkedTemplateConfigString - loginType: " + loginType);
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
    String nowDate = formatter.format(new Date());
    String templateConfigValue = (String) linkedTemplateId + "#" + nowDate + "##" + loginType;
    log.debug("EPIC LTC BPR - linkedTemplateConfigString - TemplateConfigValue: " + templateConfigValue);
    return templateConfigValue;
  }

  /**
   * Gathers attributes from the linked template Config Value
   * 
   * @param ltc
   * @param name
   * @return
   */
  private String getLinkedTemplateConfigAttribute(String ltc, String name) {
    Integer length = name.length() + 3;
    Integer start = ltc.indexOf(name);
    Integer end = ltc.indexOf(";", ltc.indexOf(name));
    end = end == -1 ? ltc.length() + 1 : end;
    String attribute = start != -1 ? ltc.substring(start + length, end - 1) : "";
    return attribute;
  }

  /**
   * Builds a Linked Template Config Hash from current Linked Template Config
   * Value
   * 
   * @param ltc
   * @return
   */
  private String buildLinkedTemplateConfig(String ltc) {
    log.error("EPIC LTC BPR - buildLinkedTemplateConfig - ltc:" + ltc);
    String ID = getLinkedTemplateConfigAttribute(ltc, "Id");
    String startDate = getLinkedTemplateConfigAttribute(ltc, "StartDate");
    String endDate = getLinkedTemplateConfigAttribute(ltc, "EndDate");
    String loginType = getLinkedTemplateConfigAttribute(ltc, "LoginType");
    if (loginType != null) {
      loginType = loginType.trim().replace("[", "").replace("]", "");
    }
    String templateConfig = ID + "#" + startDate + "#" + endDate + "#" + loginType;
    log.error("EPIC LTC BPR - buildLinkedTemplateConfig - TemplateConfig [" + templateConfig + "]");
    return templateConfig;
  }

  /**
   * gets the current Linked Template Config values from the Current Account
   * 
   * @param accountRequest
   * @return
   * @throws Exception
   */
  private List getCurrenLTCs(AccountRequest accountRequest) throws Exception {
    Account account = idn.getAccountByNativeIdentity(application.getName(), accountRequest.getNativeIdentity());
    if (account == null) {
      throw new Exception("EPIC LTC BPR - getCurrentLTCs - account: IS NULL");
    }
    log.debug("EPIC LTC BPR - getCurrentLTCs - accountAttributes: " + account.getAttributes().toString());
    List returnLTCs = new ArrayList();
    if (idn.getRawAccountAttribute(account, "LinkedTemplateConfig") instanceof List) {
      List currentLTCs = (List) idn.getRawAccountAttribute(account, "LinkedTemplateConfig");
      for (Object ltc : currentLTCs) {
        returnLTCs.add(buildLinkedTemplateConfig(ltc.toString()));
      }
    } else if (idn.getRawAccountAttribute(account, "LinkedTemplateConfig") instanceof String) {
      String ltc = (String) idn.getRawAccountAttribute(account, "LinkedTemplateConfig");
      returnLTCs.add(buildLinkedTemplateConfig(ltc));
    }
    log.debug("EPIC LTC BPR - getCurrentLTCs - returnLTCs: " + returnLTCs);
    return returnLTCs;
  }

  /**
   * Main Code to process the Provisioning Plan.
   * 
   * @throws Exception
   */
  private void processPlan() throws Exception {
    log.error("EPIC LinkedTemplateConfig Before Provisioning -- Entering");
    for (AccountRequest accountRequest : plan.getAccountRequests()) {
      log.debug("EPIC LTC BPR - processPlan - Executing Account Request for '" + accountRequest.toString());
      AttributeRequest linkedTemplate = accountRequest.getAttributeRequest("LinkedTemplateID");
      log.debug("EPIC LTC BPR - processPlan - linkedTemplate: " + linkedTemplate);
      Operation linkedTemplateOp = linkedTemplate != null ? linkedTemplate.getOp() : null;
      log.debug("EPIC LTC BPR - processPlan - linkedTemplateOp: " + linkedTemplateOp.toString());
      if (linkedTemplateOp.equals(ProvisioningPlan.Operation.Add)) {
        log.debug("EPIC LTC BPR - processPlan(ADD) - Adding entitlement to Provisioning Plan");
        if (linkedTemplate.getValue() instanceof List) {
          List linkedTemplateIds = (List) linkedTemplate.getValue();
          log.debug("EPIC LTC BPR - processPlan(ADD) - linkedTemplateId is a List");
          for (Object linkedTemplateId : linkedTemplateIds) {
            String templateConfigValue = linkedTemplateConfigString(linkedTemplateId.toString());
            accountRequest
                .add(new AttributeRequest("LinkedTemplateConfig", ProvisioningPlan.Operation.Add, templateConfigValue));
          }
        } else if (linkedTemplate.getValue() instanceof String) {
          String linkedTemplateId = (String) linkedTemplate.getValue();
          log.debug("EPIC LTC BPR - processPlan(ADD) - linkedTemplateId: " + linkedTemplateId);
          String templateConfigValue = linkedTemplateConfigString(linkedTemplateId.toString());
          accountRequest
              .add(new AttributeRequest("LinkedTemplateConfig", ProvisioningPlan.Operation.Add, templateConfigValue));
        } else {
          throw new Exception("EPIC LTC BPR - linkedTemplate Value is neither a List or String");
        }
      } else if (linkedTemplateOp.equals(ProvisioningPlan.Operation.Remove)) {
        log.debug("EPIC LTC BPR - processPlan(REMOVE) - Removing/Updating Entitlement from Provisioning Plan");
        log.error("EPIC LTC BPR - processPlan(REMOVE) - applicationName: " + application.getName());
        log.error("EPIC LTC BPR - processPlan(REMOVE) - nativeidentity: " + accountRequest.getNativeIdentity());
        List currentLTCs = getCurrenLTCs(accountRequest);
        if (currentLTCs != null && currentLTCs.size() > 0) {
          log.error("EPIC LTC BPR - processPlan(REMOVE) - currentLTCs: " + currentLTCs.toString());
          String ltcToRemove = "";
          log.debug("EPIC LTC BPR - processPlan(REMOVE) linkedTemplate:" + linkedTemplate);
          if (linkedTemplate.getValue() instanceof List) {
            List linkedTemplateIds = (List) linkedTemplate.getValue();
            log.debug("EPIC LTC BPR - processPlan(REMOVE) linkedTemplateIds:" + linkedTemplateIds.toString());
            for (Object linkedTemplateId : linkedTemplateIds) {
              for (Object currentLTC : currentLTCs) {
                if (currentLTC.toString().startsWith(linkedTemplateId.toString())) {
                  ltcToRemove = currentLTC.toString();
                }
              }
            }
            if (ltcToRemove == "") {
              throw new Exception("EPIC LTC BPR - processPlan(REMOVE) - NO ltcToRemove found");
            }
            log.debug("EPIC LTC BPR - processPlan(REMOVE) - Removing LinkedTemplateConfig: " + ltcToRemove);
            removeLinkedTemplateConfig(ltcToRemove, accountRequest);
          } else if (linkedTemplate.getValue() instanceof String) {
            for (Object currentLTC : currentLTCs) {
              Object linkedTemplateId = linkedTemplate.getValue();
              if (currentLTC.toString().startsWith(linkedTemplateId.toString())) {
                ltcToRemove = currentLTC.toString();
              }
            }
            if (ltcToRemove != "") {
              log.debug("EPIC LTC BPR - processPlan(REMOVE) - Removing LinkedTemplateConfig: " + ltcToRemove);
              removeLinkedTemplateConfig(ltcToRemove, accountRequest);
            }
          } else {
            throw new Exception("linkedTemplate is neither a string or a list");
          }
        }
        log.error("EPIC LTC BPR - processPlan(REMOVE) - No Current LinkedTemplateConfig's to update");
      } else {
        log.error(
            "EPIC LTC BPR - processPlan - Provisioning Plan Operation not handled: " + linkedTemplateOp.toString());
      }
    }
    log.error("EPIC LinkedTemplateConfig Before Provisioning -- Exiting");
  }

  private void getAttributes() throws Exception {
    Attributes appAtts = application.getAttributes();
    if (appAtts != null && appAtts.get("cloudServicesIDNSetup") != null) {
      Map idnSetup = (Map) appAtts.get("cloudServicesIDNSetup");
      log.debug("In Before Provisioning Rule :::: idnSetup: " + idnSetup);
      // List eventConfigs = (List) idnSetup.get("eventConfigurations");
      String loginTypeLookupSource = idnSetup != null && idnSetup.containsKey("loginTypeLookupSource")
          ? (String) idnSetup.get("loginTypeLookupSource")
          : null;
      if (loginTypeLookupSource == null) {
        throw new Exception(
            "loginTypeLookupSource is null please set loginTypeLookupSource in source configuration clousServicesIDNSetup");
      }
      String defaultLoginType = idnSetup != null && idnSetup.containsKey("defaultLoginType")
          ? (String) idnSetup.get("defaultLoginType")
          : null;
      if (defaultLoginType == null) {
        throw new Exception(
            "defaultLoginType is null please set defaultLoginType in source configuration clousServicesIDNSetup");
      }
      log.debug("In Before Provisioning Rule :::: eventConfigs: " + eventConfigs);
      return;
    }
  }
}
