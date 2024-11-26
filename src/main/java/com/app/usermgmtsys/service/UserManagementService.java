package com.app.usermgmtsys.service;

import com.app.usermgmtsys.dto.RequestResponse;
import org.springframework.stereotype.Service;

public interface UserManagementService {

    RequestResponse register(RequestResponse registerRequest);

    RequestResponse login(RequestResponse loginRequest);
}
