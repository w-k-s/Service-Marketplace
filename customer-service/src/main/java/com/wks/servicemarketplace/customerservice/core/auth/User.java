package com.wks.servicemarketplace.customerservice.core.auth;

import java.security.Principal;

public interface User extends Principal {
    String getId();
}
