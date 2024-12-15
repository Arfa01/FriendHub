import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class UserClass {
    private static int idCounter = 1; // Auto-increment user IDs
    private static final String ID_COUNTER_FILE = "idCounter.json"; // Path to store idCounter
    private static UserClass currentUser = null; // Current logged-in user
    private String currentUserId;
    private String userId;
    private String userName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String emailAddress;
    private String password;
    private String bioDetails;
    private LocalDateTime userCreationDate;
    private List<String> myPosts;  // List of post IDs
    private List<String> friendRequestsSent;
    private List<String> friendRequestsReceived;
    private List<String> friendsList;
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
 /*   public UserClass() {
        myPosts = new ArrayList<>();
        friendRequestsSent = new ArrayList<>();
        friendRequestsReceived = new ArrayList<>();
        friendsList = new ArrayList<>();
        this.currentUserId = userId;
    } */

    // Constructor for creating a new user
    public UserClass(String userName, String fullName, LocalDate dateOfBirth, String gender,
                     String emailAddress, String password, String bioDetails) {
        this.userId = String.valueOf(idCounter++);
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

    /*
    * LOAD FUNCTIONS
    * */

    // Save User Data to Files
    public void saveUserToFile() {
        try {
            File userFolder = new File(BASE_PATH + "/User_" + userId);
            if (!userFolder.exists()) userFolder.mkdirs();

            // System.out.println("Saving user data for User ID: " + userId);

            // Save User Details
            File userDetailsFile = new File(userFolder, "UserDetails.json");
            try (FileWriter writer = new FileWriter(userDetailsFile)) {
                gson.toJson(this, writer);
                // System.out.println("UserDetails.json saved.");
            }

            // Save Posts, Friend Requests
            saveListToFile(userFolder, "PostIDs.json", myPosts);
            // System.out.println("PostIDs.json: " + myPosts);
            saveListToFile(userFolder, "RequestsSent.json", friendRequestsSent);
            // System.out.println("RequestsSent.json: " + friendRequestsSent);
            saveListToFile(userFolder, "RequestsReceived.json", friendRequestsReceived);
            // System.out.println("RequestsReceived.json: " + friendRequestsReceived);

        } catch (IOException e) {
            System.err.println("Error saving user data for User ID: " + userId);
            e.printStackTrace();
        }
    }

    private void saveListToFile(File folder, String fileName, List<String> list) throws IOException {
        File file = new File(folder, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(list, writer);
            //System.out.println("Saved " + list + " to " + file.getAbsolutePath());
        }
    }

    //Load User data to file
    public static ArrayList<UserClass> loadUsers() {
        ArrayList<UserClass> users = new ArrayList<>();
        File baseDir = new File(BASE_PATH);

        if (baseDir.exists() && baseDir.isDirectory()) {
            File[] userFolders = baseDir.listFiles(file -> file.isDirectory() && file.getName().startsWith("User_"));

            if (userFolders != null) {
                for (File userFolder : userFolders) {
                    try {
                        // System.out.println("Loading user from folder: " + userFolder.getName());
                        // Load User Details
                        File userDetailsFile = new File(userFolder, "UserDetails.json");
                        if (userDetailsFile.exists()) {
                            try (FileReader reader = new FileReader(userDetailsFile)) {
                                UserClass user = gson.fromJson(reader, UserClass.class);

                                // Load Posts, Friend Requests
                                user.myPosts = loadListFromFile(userFolder, "PostIDs.json");
                                // System.out.println("Loaded PostIDs.json: " + user.myPosts);

                                user.friendRequestsSent = loadListFromFile(userFolder, "RequestsSent.json");
                                // System.out.println("Loaded RequestsSent.json: " + user.friendRequestsSent);

                                user.friendRequestsReceived = loadListFromFile(userFolder, "RequestsReceived.json");
                                // System.out.println("Loaded RequestsReceived.json: " + user.friendRequestsReceived);

                                users.add(user);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error loading user data from folder: " + userFolder.getName());
                        e.printStackTrace();
                    }
                }
            }
        }else {
            System.err.println("Base directory does not exist.");
        }
        return users;
    }

    private static ArrayList<String> loadListFromFile(File folder, String fileName) {
        File file = new File(folder, fileName);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                return gson.fromJson(reader, listType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }


    private static Map<String, UserClass> loadAllUsersIntoMap() {
        Map<String, UserClass> usersMap = new HashMap<>();
        ArrayList<UserClass> allUsers = loadUsers();

        for (UserClass user : allUsers) {
            usersMap.put(user.getUserId(), user);
        }
        return usersMap;
    }


    // Save all users (simulate persistence)
    public static void saveAllUsers(List<UserClass> allUsers) {
        for (UserClass user : allUsers) {
            user.saveUserToFile();
        }
        System.out.println("All users have been saved successfully.");
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


/*  USER MANAGEMENT  */

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
    public void deleteAccount() {
        // Remove friend connections
        for (String friendId : this.friendsList) {
            UserClass friend = findUserById(friendId);
            if (friend != null) {
                friend.friendsList.remove(this.userId);
                friend.saveUserToFile(); // Save the friend's updated details
            }
        }

        // Remove friend requests sent
        for (String sentId : this.friendRequestsSent) {
            UserClass recipient = findUserById(sentId);
            if (recipient != null) {
                recipient.friendRequestsReceived.remove(this.userId);
                recipient.saveUserToFile();
            }
        }

        // Remove friend requests received
        for (String receivedId : this.friendRequestsReceived) {
            UserClass sender = findUserById(receivedId);
            if (sender != null) {
                sender.friendRequestsSent.remove(this.userId);
                sender.saveUserToFile();
            }
        }

        // Delete all posts created by this user
        for (String postId : this.myPosts) {
            Post.deletePostById(Integer.parseInt(postId));
        }

        // Delete user's folder and files
        File userFolder = new File(BASE_PATH + "/User_" + this.userId);
        if (userFolder.exists()) {
            for (File file : userFolder.listFiles()) {
                if (!file.delete()) {
                    System.err.println("Failed to delete file: " + file.getName());
                }
            }
            if (userFolder.delete()) {
                System.out.println("User account and data deleted successfully.");
            } else {
                System.err.println("Failed to delete user folder.");
            }
        } else {
            System.err.println("User folder does not exist.");
        }
    }


    private static UserClass findUserById(String userId) {
        ArrayList<UserClass> allUsers = loadUsers(); // Load all users
        for (UserClass user : allUsers) {
            if (user.userId.equals(userId)) {
                return user; // Return the matching user
            }
        }
        return null; // User not found
    }

    /* FRIEND MANAGEMENT */

    // Function: Send Friend Request
    public void sendFriendRequest(Graph graph, String recipientId) {
        //Validate if the request can be sent
        if (this.userId.equals(recipientId)) {
            System.out.println("You cannot send a friend request to yourself.");
            return;
        }
        if (friendRequestsSent.contains(recipientId)) {
            System.out.println("Friend request already sent to User ID: " + recipientId);
            return;
        }

        //Add to sender's sent list and recipient's received list
        this.friendRequestsSent.add(recipientId); // Add recipient to sent list
        saveUserToFile(); // Save the sender's updated data

        //Update recipient user
        UserClass recipient = findUserById(recipientId); // Helper method to get recipient user
        if (recipient != null) {
            recipient.friendRequestsReceived.add(this.userId); // Add sender to recipient's received list
            this.saveUserToFile();
            recipient.saveUserToFile(); // Save the recipient's updated data

            //notify
//            graph.addConnectionRequest(this.userId, recipientId);
            System.out.println("Friend request sent successfully to User ID: " + recipientId);
            saveUserToFile();
            recipient.saveUserToFile();
        } else {
            System.out.println("User ID not found: " + recipientId);
        }
    }



    // Function: View Friend Requests
    public void viewFriendRequests(Graph graph) {
//        List<String> requests = graph.viewConnectionRequests(this.userId);
//        System.out.println("Friend Requests Received: " + requests);
        System.out.println("Friend Requests Received: " + friendRequestsReceived);

    }

    // Function: Accept or Reject Friend Request
    public void respondToFriendRequest(String senderId, boolean accept) {
        UserClass sender = findUserById(senderId);
        if (sender == null) {
            System.out.println("Error: Sender user not found.");
            return;
        }

        if (accept) {
            this.friendsList.add(senderId);
            sender.friendsList.add(this.userId);
            System.out.println("Friend request accepted from User ID: " + senderId);
        } else {
            System.out.println("Friend request rejected from User ID: " + senderId);
        }
        this.friendRequestsReceived.remove(senderId);
        sender.friendRequestsSent.remove(this.userId);
        this.saveUserToFile();
        sender.saveUserToFile();

        updateUserInList(this);
        updateUserInList(sender);
    }
    // Helper method to update the user in allUsers
    private void updateUserInList(UserClass updatedUser) {
        for (int i = 0; i < Main.allUsers.size(); i++) {
            if (Main.allUsers.get(i).getUserId().equals(updatedUser.getUserId())) {
                Main.allUsers.set(i, updatedUser);
                break;
            }
        }
    }


    // Function: Unfriend a User
    public void unFriendUser(String friendId) {
        if (!this.friendsList.contains(friendId)) {
            System.out.println("User ID " + friendId + " is not in your friends list.");
            return;
        }

        this.friendsList.remove(friendId);
        saveUserToFile();

        UserClass friend = findUserById(friendId);
        if (friend != null) {
            friend.friendsList.remove(this.userId);
            friend.saveUserToFile(); // Update the friend's data file
        } else {
            System.out.println("Error: Friend with ID " + friendId + " not found.");
        }

        System.out.println("You have successfully unfriended User ID: " + friendId);
    }


    // Function: View All Friends
    public void viewAllFriends(Graph graph) {
//        List<String> friends = graph.viewConnectionsById(this.userId);
//        System.out.println("Your Friends: " + friends);
        System.out.println("Your Friends: " + friendsList);

    }


    // Function: View Mutual Friends

    public void mutualFriends(Graph graph, String otherUserId) {
        UserClass otherUser = findUserById(otherUserId);
        if (otherUser == null) {
            System.out.println("User with ID " + otherUserId + " does not exist.");
            return;
        }

        Set<String> myFriends = new HashSet<>(this.friendsList);
        Set<String> otherFriends = new HashSet<>(otherUser.friendsList);

        myFriends.retainAll(otherFriends); // Find intersection of both sets

        System.out.println("Mutual Friends with User ID " + otherUserId + ": " + myFriends);
    }


    // Function: Suggested Friends
    public void suggestedFriends(Graph graph) {
        PriorityQueue<Suggestion> suggestions = new PriorityQueue<>(Comparator.reverseOrder());
        Map<String, Integer> suggestionCount = new HashMap<>();

        // Step 1: Iterate through direct friends
        for (String friendId : this.friendsList) {
            UserClass friend = findUserById(friendId);
            if (friend == null) continue;

            // Step 2: Iterate through friends of friends
            for (String friendsFriendId : friend.friendsList) {
                if (!friendsFriendId.equals(this.userId) && !this.friendsList.contains(friendsFriendId)) {
                    suggestionCount.put(friendsFriendId, suggestionCount.getOrDefault(friendsFriendId, 0) + 1);
                }
            }
        }

        // Step 3: Populate Priority Queue with suggestions
        for (Map.Entry<String, Integer> entry : suggestionCount.entrySet()) {
            suggestions.add(new Suggestion(entry.getKey(), entry.getValue()));
        }

        // Step 4: Display Suggested Friends in Priority Order
        System.out.println("Suggested Friends (priority based on mutual friends):");
        while (!suggestions.isEmpty()) {
            Suggestion suggestion = suggestions.poll();
            System.out.println("User ID: " + suggestion.userId + " | Mutual Friends: " + suggestion.count);
        }
    }

    // Helper class for Priority Queue
    class Suggestion implements Comparable<Suggestion> {
        String userId;
        int count;

        public Suggestion(String userId, int count) {
            this.userId = userId;
            this.count = count;
        }

        @Override
        public int compareTo(Suggestion other) {
            return Integer.compare(this.count, other.count); // Sort in ascending order
        }
    }


    // Function: View Relationship Path
    public void viewRelationship(String targetUserId) {
        // Load all users into a map for easier access
        Map<String, UserClass> usersMap = loadAllUsersIntoMap();

        // Check if both users exist
        if (!usersMap.containsKey(this.userId) || !usersMap.containsKey(targetUserId)) {
            System.out.println("One or both user IDs do not exist.");
            return;
        }

        // Perform BFS to find the shortest path
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // Start the BFS from the current user
        queue.add(Arrays.asList(this.userId));

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String current = path.get(path.size() - 1);

            if (current.equals(targetUserId)) {
                System.out.println("Shortest Relationship Path to User ID " + targetUserId + ": " + path);
                return;
            }

            if (!visited.contains(current)) {
                visited.add(current);

                // Get the friends list of the current user
                UserClass currentUser = usersMap.get(current);
                for (String neighbor : currentUser.friendsList) {
                    if (!visited.contains(neighbor)) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(neighbor);
                        queue.add(newPath);
                    }
                }
            }
        }

        System.out.println("No relationship path exists between User ID " + this.userId + " and User ID " + targetUserId);
    }



    // Function: View Top Influencers
    public static void topInfluencers() {
        Map<String, UserClass> usersMap = loadAllUsersIntoMap();

        // Calculate influence scores for each user
        Map<String, Integer> influenceScores = new HashMap<>();
        for (UserClass user : usersMap.values()) {
            int score = calculateInfluenceScore(user);
            influenceScores.put(user.getUserId(), score);
        }

        // Sort and display the top influencers
        System.out.println("\n=== Top Influencers ===");
        influenceScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println("User ID: " + entry.getKey() + ", Influence Score: " + entry.getValue()));
    }
    private static int calculateInfluenceScore(UserClass user) {
        int friendsWeight = 2; // Weight for number of friends
        int postsWeight = 1;   // Weight for number of posts

        int numFriends = user.friendsList.size();
        int numPosts = user.myPosts.size();

        // Calculate weighted influence score
        return (numFriends * friendsWeight) + (numPosts * postsWeight);
    }


    /* POST MANAGEMENT */

    // Function: create a new post
    public void createPost(String content) {
        Post post = new Post(currentUserId, content);
        post.savePostToFile();
        System.out.println("Post created successfully with Post ID: " + post.getPostId());
        myPosts.add(post.getPostId());
        saveUserToFile(); // Update user data
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
        }  else {
            System.out.println("Post ID " + postId + " not found.");
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

    public String getUserId() {
        return userId;
    }

    public List<String> getFriendRequestsReceived() {
        return friendRequestsReceived;
    }
}
