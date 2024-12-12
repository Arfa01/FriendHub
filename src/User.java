import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class UserClass {
    private static int idCounter = 1; // Auto-increment user IDs
    private static final String ID_COUNTER_FILE = "idCounter.json"; // Path to store idCounter
    private static UserClass currentUser = null; // Current logged-in user
    private int currentUserId;
    private int userId;
    private String userName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String emailAddress;
    private String password;
    private String bioDetails;
    private LocalDateTime userCreationDate;
    private List<Integer> myPosts;  // List of post IDs
    private List<Integer> friendRequestsSent;
    private List<Integer> friendRequestsReceived;
    private List<Integer> friendsList;
    private static final String BASE_PATH = "Users";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    // Static initializer block to load idCounter on program start
    static {
        loadIdCounter();
    }


    //Default Constructor
    public UserClass() {
        myPosts = new ArrayList<>();
        friendRequestsSent = new ArrayList<>();
        friendRequestsReceived = new ArrayList<>();
        friendsList = new ArrayList<>();
        this.currentUserId = userId;
    }

    // Constructor for creating a new user
    public UserClass(String userName, String fullName, LocalDate dateOfBirth, String gender,
                     String emailAddress, String password, String bioDetails) {
        this.userId = idCounter++;
        saveIdCounter(); // updated idCounter
        this.userName = userName;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.emailAddress = emailAddress;
        this.password = password;
        this.bioDetails = bioDetails;
        this.userCreationDate = LocalDateTime.now();

        this.myPosts = new ArrayList<>();
        this.friendRequestsSent = new ArrayList<>();
        this.friendRequestsReceived = new ArrayList<>();
        this.friendsList = new ArrayList<>();

        saveUserToFile();
    }


    // Save User Data to Files
    public void saveUserToFile() {
        try {
            File userFolder = new File(BASE_PATH + "/User_" + userId);
            if (!userFolder.exists()) userFolder.mkdirs();

            // Save User Details
            File userDetailsFile = new File(userFolder, "UserDetails.json");
            try (FileWriter writer = new FileWriter(userDetailsFile)) {
                gson.toJson(this, writer);
            }

            // Save Posts, Friend Requests
            saveListToFile(userFolder, "PostIDs.json", myPosts);
            saveListToFile(userFolder, "RequestsSent.json", friendRequestsSent);
            saveListToFile(userFolder, "RequestsReceived.json", friendRequestsReceived);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveListToFile(File folder, String fileName, List<Integer> list) throws IOException {
        File file = new File(folder, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(list, writer);
        }
    }


    // Method to load idCounter from file
    private static void loadIdCounter() {
        try (FileReader reader = new FileReader(ID_COUNTER_FILE)) {
            Integer idCounterValue = new Gson().fromJson(reader, Integer.class);
            idCounter = (idCounterValue != null) ? idCounterValue : 1;      // Default to 1 if file is empty
        } catch (IOException e) {
            idCounter = 1; // If file doesn't exist, start from 1
        }
    }

    // Method to save idCounter to file
    private static void saveIdCounter() {
        try (FileWriter writer = new FileWriter(ID_COUNTER_FILE)) {
            new Gson().toJson(idCounter, writer);
        } catch (IOException e) {
            System.err.println("Error saving idCounter: " + e.getMessage());
        }
    }



    // Function: User Creation
    public static UserClass registerUser(Graph graph) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Full Name: ");
            String fullName = scanner.nextLine();
            System.out.print("Enter User Name: ");
            String userName = scanner.nextLine();
            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            LocalDate dateOfBirth = LocalDate.parse(scanner.nextLine());
            System.out.print("Enter Gender: ");
            String gender = scanner.nextLine();
            System.out.print("Enter Email Address: ");
            String emailAddress = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            System.out.print("Enter Bio Details: ");
            String bioDetails = scanner.nextLine();
            // Create new user
            UserClass newUser = new UserClass(userName, fullName, dateOfBirth, gender, emailAddress, password, bioDetails);
            System.out.println("User created successfully with User ID: " + newUser.userId);
            graph.addNode(newUser.userId);
            System.out.println("Registration successful! User ID: " + newUser.userId);
            return newUser;
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
            return null;
        }
    }


    // Function: User Login
    public static void loginUser(Graph graph, List<UserClass> users) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter User Name:");
        String userName = scanner.nextLine();

        System.out.println("Enter Password:");
        String password = scanner.nextLine();

        for (UserClass user : users) {
            if (user.userName.equals(userName) && user.password.equals(password)) {
                currentUser = user;
                System.out.println("Login successful! Welcome, " + currentUser.fullName);
                return;
            }
        }
        System.out.println("Invalid credentials! Try again.");
    }


    // Function: User Logout
    public static void logout() {
        System.out.println("Logging out..." + currentUser.getUserId());
        currentUser = null;
    }


    // Function: Delete Account
    public void deleteAccount(Graph graph) {
        graph.removeNode(this.userId);
        System.out.println("Account deleted successfully.");
    }

    // Function: Send Friend Request
    public void sendFriendRequest(Graph graph, int recipientId) {
        graph.addConnectionRequest(this.userId, recipientId);
        System.out.println("Friend request sent to User ID: " + recipientId);
    }

    // Function: View Friend Requests
    public void viewFriendRequests(Graph graph) {
        List<Integer> requests = graph.viewConnectionRequests(this.userId);
        System.out.println("Friend Requests Received: " + requests);
    }

    // Function: Accept or Reject Friend Request
    public void respondToFriendRequest(Graph graph, int senderId, boolean accept) {
        if (accept) {
            graph.addConnection(this.userId, senderId);
            System.out.println("Friend request accepted from User ID: " + senderId);
        } else {
            System.out.println("Friend request rejected from User ID: " + senderId);
        }
        graph.removeConnectionRequest(this.userId, senderId);
    }


    // Function: Unfriend a User
    public void unFriendUser(Graph graph, int friendId) {
        graph.removeConnection(this.userId, friendId);
        System.out.println("You have unfriended User ID: " + friendId);
    }


    // Function: View All Friends
    public void viewAllFriends(Graph graph) {
        List<Integer> friends = graph.viewConnectionsById(this.userId);
        System.out.println("Your Friends: " + friends);
    }


    // Function: View Mutual Friends
    public void mutualFriends(Graph graph, int otherUserId) {
        List<Integer> mutual = graph.viewCommonConnectionsBetweenNodes(this.userId, otherUserId);
        System.out.println("Mutual Friends with User ID " + otherUserId + ": " + mutual);
    }


    // Function: Suggested Friends
    public void suggestedFriends(Graph graph) {
        List<Integer> suggestions = graph.findNextToAdjacentNodes(this.userId);
        System.out.println("Suggested Friends: " + suggestions);
    }


    // Function: View Relationship Path
    public void viewRelationship(Graph graph, int targetUserId) {
        List<Integer> path = graph.findShortestFriendshipPath(this.userId, targetUserId);
        System.out.println("Shortest Relationship Path to User ID " + targetUserId + ": " + path);
    }


    // Function: View Top Influencers
    public static void topInfluencers(Graph graph) {
        Map<Integer, Integer> scores = graph.calculateInfluenceScores();
        scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println("User ID: " + entry.getKey() + ", Score: " + entry.getValue()));
    }


    // Function: create a new post
    public void createPost(String content) {
        Post post = new Post(currentUserId, content);
        post.savePostToFile();
        System.out.println("Post created successfully with Post ID: " + post.getPostId());
//        myPosts.add(post.getPostId());
//        saveUserToFile(); // Update user data
    }


    // Function: View all posts by current User
    public void viewMyPosts() {
        System.out.println("Your Posts:");
        Post.viewPostsByUser(currentUserId);
//        System.out.println("Your posts: " + userId + ": " + myPosts);
    }

    // Function: Delete a Specific Post
    public void deleteMyPost(int postId) {
        Post.deletePostById(postId);
    }

    // Function: Like a Specific Post
    public void likePost(int postId) {
        // Load the post and add a like
        Post post = Post.loadPostById(postId);
        if (post != null) {
            post.addLike();
            post.savePostToFile(); // Update post file
            System.out.println("You liked Post ID: " + postId);
        }
    }

    // Function: Add a Comment to a Specific Post
    public void commentOnPost(int postId, String comment) {
        // Load the post and add a comment
        Post post = Post.loadPostById(postId);
        if (post != null) {
            post.addComment("User " + currentUserId + ": " + comment);
            post.savePostToFile(); // Update post file
            System.out.println("Your comment has been added to Post ID: " + postId);
        }
    }

    // Function: View All Posts Globally
    public void viewAllPosts() {
        System.out.println("All Posts in the System:");
        Post.viewAllPosts();
    }


    // Simulate a basic menu to demonstrate Post functionalities
    public void postMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Post Menu ===");
            System.out.println("1. Create Post");
            System.out.println("2. View My Posts");
            System.out.println("3. Delete My Post");
            System.out.println("4. Like a Post");
            System.out.println("5. Comment on a Post");
            System.out.println("6. View All Posts");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter post content: ");
                    String content = scanner.nextLine();
                    createPost(content);
                    break;
                case 2:
                    viewMyPosts();
                    break;
                case 3:
                    System.out.print("Enter Post ID to delete: ");
                    int deleteId = scanner.nextInt();
                    deleteMyPost(deleteId);
                    break;
                case 4:
                    System.out.print("Enter Post ID to like: ");
                    int likeId = scanner.nextInt();
                    likePost(likeId);
                    break;
                case 5:
                    System.out.print("Enter Post ID to comment on: ");
                    int commentId = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter your comment: ");
                    String comment = scanner.nextLine();
                    commentOnPost(commentId, comment);
                    break;
                case 6:
                    viewAllPosts();
                    break;
                case 0:
                    System.out.println("Exiting Post Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }


    //GETTERS
    public static UserClass getCurrentUser() {
        return currentUser;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }
}
