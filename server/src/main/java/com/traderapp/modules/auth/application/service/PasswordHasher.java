package com.traderapp.modules.auth.application.service;

import com.traderapp.modules.auth.domain.valueObjects.PasswordHash;

public interface PasswordHasher {
    PasswordHash hash(String rawPassword);
}
