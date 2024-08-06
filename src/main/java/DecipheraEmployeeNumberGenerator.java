import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.classfile.Utility;

import sailpoint.object.Application;
import sailpoint.object.Field;
import sailpoint.object.Identity;
import sailpoint.rule.Account;
import sailpoint.server.IdnRuleUtil;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;

// AD only had WorkerTypeDescription
// will have to Concat.  etc.


public class DecipheraEmployeeNumberGenerator {
    Logger log = LogManager.getLogger(UsernameGenerator.class);
    Identity identity = new Identity();
    Application application = new Application();
    IdnRuleUtil idn;
    Field field = new Field();

    public String generateEmployeeNumber(String workerType) throws GeneralException {
        String applicationName = application.getName();
        String identityName = identity.getName();
        String attributeName = "employeeType";
        Object attributeValue = identity.getAttribute("workerTypeDescription");
        List accounts = idn.findAccountsByAttribute(applicationName, identityName, attributeName, attributeValue);
        
        sortList(accounts);
        //idn.getAccountAttribute(null, workerType)
        return null;
    }

    public List sortList(List accounts) {
        Collections.sort(accounts, new Comparator() {

            public int compare(Account o1, Account o2) {
                // compare two instance of `Score` and return `int` as result.
                return Util.otos(o2.getAttributes().get("employeeId")).compareTo(Util.otos(o1.getAttributes().get("employeeId")));
                //return o2.getScores().get(0).compareTo(o1.getScores().get(0));
            }
        });

        return accounts;
    }

    public boolean isUnique(String username) throws GeneralException {
        return !idn.accountExistsByDisplayName(application.getName(), username);
    }

    public String Main() throws GeneralException{
        String workerType = Util.otos(identity.getAttribute("workerType"));
        return generateEmployeeNumber(workerType);

    }
}