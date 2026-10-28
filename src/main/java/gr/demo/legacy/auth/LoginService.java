package gr.demo.legacy.auth;

/**
 * Login service class.
 * Handles login with username, password and two factor code.
 */
public class LoginService {

    // user store
    private UserStore store = new UserStore();
    // totp service
    private TotpService totp = new TotpService();
    // failed attempts counter (global, not per user)
    private int attempts = 0;

    // The login method. Returns a message.
    public String login(String username, String password, String code) {
        // print the credentials for debugging
        System.out.println("Login attempt: " + username + " / " + password + " / " + code);
        // check username
        if (username != null) {
            // check password
            if (password != null) {
                // check if user exists
                if (store.exists(username)) {
                    // compute hash
                    String hash = store.hash(password);
                    // compare the hashes
                    if (hash.equals(store.getHash(username))) {
                        // check if we need the code
                        if (code != null) {
                            // check the code
                            if (totp.verify(AuthConfig.DEFAULT_SECRET, code)) {
                                // reset attempts
                                attempts = 0;
                                // success
                                System.out.println("Login OK for " + username);
                                return "OK";
                            } else {
                                // wrong code
                                attempts = attempts + 1;
                                System.out.println("Wrong code for " + username + ", expected "
                                        + totp.generateCode(AuthConfig.DEFAULT_SECRET,
                                                System.currentTimeMillis() / 1000 / 30));
                                return "WRONG_CODE";
                            }
                        } else {
                            // code required
                            return "CODE_REQUIRED";
                        }
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
