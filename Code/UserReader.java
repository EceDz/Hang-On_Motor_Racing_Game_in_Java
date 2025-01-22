package Project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserReader extends User {

    public UserReader(String username, String password) {
        super(username, password);
    }

    private static final String USERS_FILE_PATH = "src/Project/users.txt";
    private static String currentUser;

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    public static Map<String, String> readUsers() {
        Map<String, String> userInfo = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                	userInfo.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("The users file was not found: " + fnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Error reading the file: " + ioe.getMessage());
        }
        return userInfo;
    }

    public static void writeUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE_PATH, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
        } catch (FileNotFoundException fnfe) {
            System.out.println("The users file could not be found: " + fnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Error writing to the file: " + ioe.getMessage());
        }
    }

    public static boolean userExists(String username) {
        Map<String, String> userInfo = readUsers();
        return userInfo.containsKey(username);
    }

    public static boolean userCheck(String username, String password) {
        Map<String, String> userInfo = readUsers();
        return userInfo.containsKey(username) &&
        		userInfo.get(username).equals(password);
    }

    public static Map<String, String> getAllUsers() {
        return readUsers();
    }
}
