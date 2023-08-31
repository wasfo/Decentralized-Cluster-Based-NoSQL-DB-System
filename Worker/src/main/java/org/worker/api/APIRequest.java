
package org.worker.api;

import lombok.Data;
import org.worker.user.UserCredentials;

@Data
public abstract class APIRequest {
    protected UserCredentials userCredentials;
    protected boolean isBroadcasted = false;
}
