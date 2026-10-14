package gr.demo.legacy.auth;

import java.util.Scanner;

/**
 * Main class
 */
public class Main {

    // main method
    public static void main(String[] args) {
        // create the scanner
        Scanner sc = new Scanner(System.in);
        // create the service
        LoginService service = new LoginService();
        // ask for the username
        System.out.print("Username: ");
        String u = sc.nextLine();
        // ask for the password
        System.out.print("Password: ");
        String p = sc.nextLine();
        // login
        String r = service.login(u, p);
        // print the result
        System.out.println("Result: " + r);
    }
}
