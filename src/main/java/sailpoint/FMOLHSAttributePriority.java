package sailpoint;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sailpoint.rule.Account;
import sailpoint.rule.Identity;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;

public class FMOLHSAttributePriority {
  String attributeName;
  String sources;
  String identityId;
  IdnRuleUtil idn;
  Identity identity;
  String oracleSource = "Oracle HCM JDBC";
  String NERMSource = "NERM FMOLHS JDBC";
  String NERMCCSource = "NERM CC JDBC";
  String NERMAssignmentsSource = "NERM Assignments JDBC";

  /*
   * Oracle = Acting then Oracle
   *
   *
   */
  public String getAttributeValue(
    String sourceName,
    String identityName,
    String attributeName
  ) throws GeneralException {
    String attributeValue = "";
    Account account = idn.getFirstAccount(sourceName, identityName);
    if (account != null) {
      Map<String, Object> attributes = account.getAttributes();
      if (attributes != null) {
        attributeValue = Util.getString(attributes, attributeName);
      }
    }
    return attributeValue;
  }

  public Boolean hasActiveAccount(String sourceName, String identityName)
    throws GeneralException {
    Account account = idn.getFirstAccount(sourceName, identityName);
    if (account != null && !account.isDisabled()) {
      return true;
    }
    return false;
  }

  public Date getEndDate(String sourceName, String identityName, String endDateAttribute)
    throws GeneralException, ParseException {
    String date = getAttributeValue(sourceName, identityName, endDateAttribute);
    return Util.stringToDate(date);
  }

  public String getAuthProfile(List<String> sources, String identityName)
    throws GeneralException, ParseException {
    if (hasActiveAccount(oracleSource, identityName)) {
      return getAttributeValue(oracleSource, identityName, attributeName);
    } else if (hasActiveAccount(NERMSource, identityName)) {
      return getAttributeValue(NERMSource, identityName, attributeName);
    } else if (hasActiveAccount(NERMCCSource, identityName)) {
      return getAttributeValue(NERMCCSource, identityName, attributeName);
    } else {
      //TODO get all account.  sort by End Date.  and rturn newest
      Date oracleEndDate = getEndDate(oracleSource, identityName, "TERM_DATE");
      Date nermEndDate = getEndDate(NERMSource, identityName, "TerminatedOn");
      Date nermCcEndDate = getEndDate(NERMCCSource, identityName,"TerminatedOn");
      Map<String, Date> endDates = new TreeMap<String, Date>();
      endDates.put("oracle", oracleEndDate);
      endDates.put("nerm", nermEndDate);
      endDates.put("nermcc", nermCcEndDate);
      // Map<Date, ArrayList> m = new TreeMap<Date, ArrayList>()
      return "";
    }
  }

  public Account getAuthAccounts(String applicationName, String nativeIdentity)
    throws GeneralException {
    Account account = idn.getAccountByNativeIdentity(
      applicationName,
      nativeIdentity
    );

    return account;
  }
}
