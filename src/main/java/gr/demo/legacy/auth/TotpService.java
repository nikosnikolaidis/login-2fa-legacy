package gr.demo.legacy.auth;

import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * TOTP service class.
 * Generates and verifies time based one time passwords.
 */
public class TotpService {

    // base32 alphabet
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    // decodes a base32 string to bytes
    public byte[] decodeBase32(String s) {
        // make upper case
        s = s.toUpperCase();
        // remove spaces
        s = s.replace(" ", "");
        // remove padding
        s = s.replace("=", "");
        // create the result array
        byte[] result = new byte[s.length() * 5 / 8];
        // variables
        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;
        // loop through the characters
        for (int i = 0; i < s.length(); i++) {
            // get the character
            char c = s.charAt(i);
            // find its value
            int val = ALPHABET.indexOf(c);
            // check the value
            if (val < 0) {
                // invalid character
                return null;
            }
            // shift the buffer
            buffer = buffer << 5;
            // add the value
            buffer = buffer | val;
            // add the bits
            bitsLeft = bitsLeft + 5;
            // check if we have a full byte
            if (bitsLeft >= 8) {
                // add the byte
                result[index] = (byte) (buffer >> (bitsLeft - 8));
                // next index
                index = index + 1;
                // remove the bits
                bitsLeft = bitsLeft - 8;
            }
        }
        // return the result
        return result;
    }

    // generates the code for a given time counter
    public String generateCode(String secret, long counter) {
        try {
            // decode the secret
            byte[] key = decodeBase32(secret);
            // create the message (8 bytes)
            byte[] msg = new byte[8];
            // fill the message
            long value = counter;
            for (int i = 7; i >= 0; i--) {
                msg[i] = (byte) (value & 0xff);
                value = value >> 8;
            }
            // create the mac
            Mac mac = Mac.getInstance("HmacSHA1");
            // init the mac
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            // compute the hash
            byte[] hash = mac.doFinal(msg);
            // get the offset
            int offset = hash[hash.length - 1] & 0xf;
            // compute the binary code
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);
            // compute the otp
            int otp = binary % 1000000;
            // convert to string
            String result = Integer.toString(otp);
            // add leading zeros
            while (result.length() < 6) {
                result = "0" + result;
            }
            // return it
            return result;
        } catch (Exception e) {
            // ignore the error
            return null;
        }
    }

    // verifies a code (allows 1 step before and after)
    public boolean verify(String secret, String code) {
        // get the current counter
        long counter = System.currentTimeMillis() / 1000 / AuthConfig.TIME_STEP;
        // check the three windows
        for (long i = counter - 1; i <= counter + 1; i++) {
            // generate the code
            String expected = generateCode(secret, i);
            // compare
            if (expected != null && expected.equals(code)) {
                return true;
            }
        }
        // not valid
        return false;
    }

    // generates backup codes
    public String[] generateBackupCodes() {
        // create the random
        Random r = new Random();
        // create the array
        String[] codes = new String[10];
        // fill the array
        for (int i = 0; i < 10; i++) {
            // generate a number
            int n = r.nextInt(99999999);
            // convert to string
            codes[i] = String.valueOf(n);
        }
        // return the codes
        return codes;
    }
}
