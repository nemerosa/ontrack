package net.nemerosa.ontrack.model.support;

import org.junit.Assert;
import org.junit.Test;

public class ConnectionResultTest {

    @Test
    public void onError_doesnt_throw_when_not_in_error() {
        ConnectionResult.ok().onErrorThrow(RuntimeException::new);
    }

    @Test
    public void onError_throws_when_in_error() {
        try {
            ConnectionResult.error("Error message").onErrorThrow(RuntimeException::new);
            Assert.fail("Should have failed");
        } catch (RuntimeException ex) {
            Assert.assertEquals("Error message", ex.getMessage());
        }
    }

}
