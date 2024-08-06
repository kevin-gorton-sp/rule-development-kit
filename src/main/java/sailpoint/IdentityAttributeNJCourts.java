package sailpoint;

import org.apache.log4j.Logger;

import sailpoint.server.IdnRuleUtil;
import sailpoint.object.Bundle;
import sailpoint.object.Identity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IdentityAttributeNJCourts {
  public int Main() {

    Identity identity = new Identity();
    String role_id = "Rle ID";

    if ((identity.getAssignedRole(role_id) != null)) {
      return 1;
    }
    return 0;

  }
}