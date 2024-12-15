import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    // Load users and posts at startup
    static ArrayList<UserClass> allUsers = UserClass.loadUsers(); // Call the load method to get users
    ArrayList<Post> allPosts = Post.loadPosts(); // Call the load method to get posts
    private static Graph socialGraph = new Graph();  // Global graph object to manage friendships

    public static void main(String[] args) {
        UserClass.loadUsers(); // Load all users on startup
        socialGraph.loadGraphFromFile();
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
                        UserClass.saveAllUsers(allUsers);
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
        UserClass.loadUsers(); // Load all users on startup
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== User Menu (" + currentUser.getUserName() + ") ===");
            System.out.println("1. Friend Management");
            System.out.println("2. Post Management");
            System.out.println("3. View Feed (All Posts)");
            System.out.println("4. Delete Account");
            System.out.println("5. Logout");
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
                    System.out.print("Are you sure you want to delete your account? (yes/no): ");
                    scanner.nextLine();
                    String confirmation = scanner.nextLine().trim().toLowerCase();
                    if (confirmation.equals("yes")) {
                        currentUser.deleteAccount();
                        UserClass.logout();
                        System.out.println("Account deleted successfully. Logging out...");
                        return;
                    } else {
                        System.out.println("Account deletion canceled.");
                    }
                    break;
                case 5:
                    System.out.println("Logging out...");
                    UserClass.logout();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (UserClass.getCurrentUser() != null && choice != 5);
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
            System.out.println("7. Unfriend a User");
            System.out.println("8. View Relationship Path");
            System.out.println("9. View Top Influencers");
            System.out.println("10. Go Back");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter User ID to send request: ");
                    String recipientId = scanner.nextLine();
                    currentUser.sendFriendRequest(socialGraph, recipientId);
                    break;
                case 2:
                    currentUser.viewFriendRequests(socialGraph);
                    break;
                case 3:
                    System.out.println("Friend Requests Received: " + currentUser.getFriendRequestsReceived());
                    System.out.print("Enter Sender User ID: ");
                    String senderId = scanner.nextLine();
                    System.out.print("Accept request? (true/false): ");
                    boolean accept = scanner.nextBoolean();
                    currentUser.respondToFriendRequest(senderId, accept);
                    break;
                case 4:
                    currentUser.viewAllFriends(socialGraph);
                    break;
                case 5:
                    System.out.print("Enter User ID to see mutual friends: ");
                    String otherUserId = scanner.nextLine();
                    currentUser.mutualFriends(socialGraph, otherUserId);
                    break;
                case 6:
                    currentUser.suggestedFriends(socialGraph);
                    break;
                case 7:
                    System.out.print("Enter User ID to unfriend: ");
                    String friendId = scanner.nextLine();
                    currentUser.unFriendUser(friendId); // Updated call to unfriend a user
                    break;
                case 8:
                    System.out.println("All Users on this app: " + allUsers);
                    System.out.print("Enter Target User ID to find relationship path: ");
                    String targetUserId = scanner.nextLine();
                    currentUser.viewRelationship(targetUserId);
                    break;
                case 9:
                    System.out.println("\n=== Top Influencers ===");
                    UserClass.topInfluencers();
                    break;
                case 10:
                    System.out.println("Returning to User Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 10);
    }

}
