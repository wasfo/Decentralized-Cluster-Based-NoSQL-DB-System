
package org.com.api;
import lombok.Data;


@Data
public abstract class APIRequest {
    protected boolean isBroadcasted = false;
}
