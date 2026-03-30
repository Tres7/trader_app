package com.traderapp.modules.auth.application.ports.output;

import com.traderapp.modules.auth.application.events.PasswordResetRequestedEvent;
import com.traderapp.modules.auth.application.events.UserEmailVerifiedEvent;
import com.traderapp.modules.auth.application.events.UserRegisteredEvent;

public interface AuthEventPublisher {
    void publishUserRegistered(UserRegisteredEvent event);
    void publishUserEmailVerified(UserEmailVerifiedEvent event);
    void publishPasswordResetRequested(PasswordResetRequestedEvent event);
}
