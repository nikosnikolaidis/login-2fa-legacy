package gr.demo.legacy.auth;

/**
 * Configuration class
 */
public class AuthConfig {

    // secret used for all users (base32)
    public static final String DEFAULT_SECRET = "JBSWY3DPEHPK3PXP";

    // admin password
    public static final String ADMIN_PASSWORD = "admin";

    // time step in seconds
    public static final int TIME_STEP = 30;

    // number of digits
    public static final int DIGITS = 6;

    // max login attempts
    public static final int MAX_ATTEMPTS = 1000;
}
