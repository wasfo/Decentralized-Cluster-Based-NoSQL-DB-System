package org.worker.api.event;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.user.User;

@RequiredArgsConstructor
@Data
public class RegistrationEvent extends WriteEvent {

    private User user;
}
