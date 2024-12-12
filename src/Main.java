import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    // Load users and posts at startup
    ArrayList<UserClass> allUsers = UserClass.loadUsers(); // Call the load method to get users
    ArrayList<Post> allPosts = Post.loadPosts(); // Call the load method to get posts
    private static Graph socialGraph = new Graph();  // Global graph object to manage friendships
    private static List<UserClass> allUsers = new ArrayList<>(); // List to store all users

    public static void main(String[] args) {
        loadAllUsers(); // Load all users on startup
        System.out.println("=== Welcome to Social Media App ===");

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Register a New User");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    UserClass newUser = UserClass.registerUser(socialGraph);
                    if (newUser != null) {
                        allUsers.add(newUser);
                        saveAllUsers();
                    }
                    break;
                case 2:
                    UserClass.loginUser(socialGraph, allUsers);
                    if (UserClass.getCurrentUser() != null) {
                        userMenu(UserClass.getCurrentUser());
                    }
                    break;
                case 3:
                    System.out.println("Exiting the app. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    private static void userMenu(UserClass currentUser) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== User Menu (" + currentUser.getUserName() + ") ===");
            System.out.println("1. Friend Management");
            System.out.println("2. Post Management");
            System.out.println("3. View Feed (All Posts)");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    friendMenu(currentUser);
                    break;
                case 2:
                    currentUser.postMenu();  // Call post management menu
                    break;
                case 3:
                    currentUser.viewAllPosts();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    UserClass.logout();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (UserClass.getCurrentUser() != null && choice != 4);
    }

    private static void friendMenu(UserClass currentUser) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Friend Management ===");
            System.out.println("1. Send Friend Request");
            System.out.println("2. View Friend Requests");
            System.out.println("3. Respond to Friend Requests");
            System.out.println("4. View All Friends");
            System.out.println("5. View Mutual Friends");
            System.out.println("6. View Suggested Friends");
            System.out.println("7. Go Back");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter User ID to send request: ");
                    int recipientId = scanner.nextInt();
                    currentUser.sendFriendRequest(socialGraph, recipientId);
                    break;
                case 2:
                    currentUser.viewFriendRequests(socialGraph);
                    break;
                case 3:
                    System.out.print("Enter Sender User ID: ");
                    int senderId = scanner.nextInt();
                    System.out.print("Accept request? (true/false): ");
                    boolean accept = scanner.nextBoolean();
                    currentUser.respondToFriendRequest(socialGraph, senderId, accept);
                    break;
                case 4:
                    currentUser.viewAllFriends(socialGraph);
                    break;
                case 5:
                    System.out.print("Enter User ID to see mutual friends: ");
                    int otherUserId = scanner.nextInt();
                    currentUser.mutualFriends(socialGraph, otherUserId);
                    break;
                case 6:
                    currentUser.suggestedFriends(socialGraph);
                    break;
                case 7:
                    System.out.println("Returning to User Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    // Load all users (simulate persistence)
    private static void loadAllUsers() {
        // Add logic to load users from files (simulate or use actual files)
        System.out.println("Loading users...");
    }

    // Save all users (simulate persistence)
    private static void saveAllUsers() {
        // Add logic to save users to files

        System.out.println("Saving users...");
    }
}
