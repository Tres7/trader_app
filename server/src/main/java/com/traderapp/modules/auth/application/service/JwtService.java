package com.traderapp.modules.auth.application.service;

import com.traderapp.modules.auth.domain.entities.User;

public interface JwtService {
    String generateAccessToken(User user);
}
