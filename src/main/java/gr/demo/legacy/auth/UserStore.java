package gr.demo.legacy.auth;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * User store class.
 * Stores the users in memory.
 */
public class UserStore {

    // users map: username -> password hash
    private Map<String, String> users = new HashMap<String, String>();

    // constructor
    public UserStore() {
        // add default users
        users.put("alice", hash("password123"));
        users.put("bob", hash("qwerty"));
        users.put("admin", hash(AuthConfig.ADMIN_PASSWORD));
    }

    // checks if the user exists
    public boolean exists(String username) {
        if (users.containsKey(username)) {
            return true;
        } else {
            return false;
        }
    }

    // gets the hash of a user
    public String getHash(String username) {
        return users.get(username);
    }

    // hashes a password using MD5
    public String hash(String password) {
        try {
            // get the digest
            MessageDigest md = MessageDigest.getInstance("MD5");
            // compute the hash
            byte[] bytes = md.digest(password.getBytes());
            // convert to hex
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String h = Integer.toHexString(0xff & bytes[i]);
                if (h.length() == 1) {
                    sb.append('0');
                }
                sb.append(h);
            }
            return sb.toString();
        } catch (Exception e) {
            // ignore
            return null;
        }
    }
}
