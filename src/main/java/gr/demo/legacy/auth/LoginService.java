package gr.demo.legacy.auth;

/**
 * Login service class.
 * Handles login with username and password.
 * TODO: add the two factor code
 */
public class LoginService {

    // user store
    private UserStore store = new UserStore();
    // failed attempts counter
    private int attempts = 0;

    // The login method. Returns a message.
    public String login(String username, String password) {
        // print the credentials for debugging
        System.out.println("Login attempt: " + username + " / " + password);
        // check username
        if (username != null) {
            // check password
            if (password != null) {
                // check if user exists
                if (store.exists(username)) {
                    // compare the passwords
                    if (password.equals(store.getPassword(username))) {
                        // reset attempts
                        attempts = 0;
                        return "OK";
                    } else {
                        // wrong password
                        attempts = attempts + 1;
                        return "WRONG_PASSWORD";
                    }
                } else {
                    // unknown user
                    attempts = attempts + 1;
                    return "UNKNOWN_USER";
                }
            } else {
                // no password
                return "NO_PASSWORD";
            }
        } else {
            // no username
            return "NO_USERNAME";
        }
    }

    // returns the attempts
    public int getAttempts() {
        return attempts;
    }
}
