package gr.demo.legacy.auth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TotpServiceTest {

    @Test
    public void rfc6238StyleVector() {
        // RFC 4226 test secret "12345678901234567890" in base32
        TotpService t = new TotpService();
        assertEquals("755224", t.generateCode("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ", 0));
    }
}
