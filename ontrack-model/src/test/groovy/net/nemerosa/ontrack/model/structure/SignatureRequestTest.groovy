package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test

import java.time.LocalDateTime

class SignatureRequestTest {

    @Test
    void 'From signature'() {
        def time = TestUtils.dateTime()
        def request = SignatureRequest.of(Signature.of(time, 'User'))
        assert request.user == 'User'
        assert request.time == time
    }

    @Test
    void 'To signature'() {
        def time0 = TestUtils.dateTime()
        def time = time0.plusDays(1)
        def s = new SignatureRequest(null, null).getSignature(Signature.of(time, 'User'))
        assert s.user.name == 'User'
        assert s.time == time
    }

    @Test
    void 'To signature with blank user'() {
        def time0 = TestUtils.dateTime()
        def time = time0.plusDays(1)
        def s = new SignatureRequest(null, '').getSignature(Signature.of(time, 'User'))
        assert s.user.name == 'User'
        assert s.time == time
    }

    @Test
    void 'To signature with user'() {
        def time0 = TestUtils.dateTime()
        def time = time0.plusDays(1)
        def s = new SignatureRequest(null, 'Other').getSignature(Signature.of(time, 'User'))
        assert s.user.name == 'Other'
        assert s.time == time
    }

    @Test
    void 'To signature with time'() {
        def time0 = TestUtils.dateTime()
        def time = time0.plusDays(1)
        def s = new SignatureRequest(time0, null).getSignature(Signature.of(time, 'User'))
        assert s.user.name == 'User'
        assert s.time == time0
    }

    @Test
    void 'To signature with time and user'() {
        def time0 = TestUtils.dateTime()
        def time = time0.plusDays(1)
        def s = new SignatureRequest(time0, 'Other').getSignature(Signature.of(time, 'User'))
        assert s.user.name == 'Other'
        assert s.time == time0
    }

}
