package gr.demo.legacy.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * User store class.
 * Stores the users in memory.
 * TODO: do not keep the passwords as plain text
 */
public class UserStore {

    // users map: username -> password
    private Map<String, String> users = new HashMap<String, String>();

    // constructor
    public UserStore() {
        // add default users
        users.put("alice", "password123");
        users.put("bob", "qwerty");
        users.put("admin", AuthConfig.ADMIN_PASSWORD);
    }

    // checks if the user exists
    public boolean exists(String username) {
        if (users.containsKey(username)) {
            return true;
        } else {
            return false;
        }
    }

    // gets the password of a user
    public String getPassword(String username) {
        return users.get(username);
    }
}
