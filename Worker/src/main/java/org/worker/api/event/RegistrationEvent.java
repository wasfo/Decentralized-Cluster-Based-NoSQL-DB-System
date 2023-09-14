package org.worker.api.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.worker.user.User;

@AllArgsConstructor
@Data
public class RegistrationEvent extends WriteEvent {

    private User user;

    public RegistrationEvent() {
    }
}
