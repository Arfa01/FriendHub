package com.example.handlingformsubmission;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.handlingformsubmission.Gson.gson;

public class UserClass {
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
    public int postCounter;
    private LocalDateTime userCreationDate;
    public List<String> myPosts;  // List of post IDs
    private List<String> friendRequestsSent;
    private List<String> friendRequestsReceived;
    private List<String> friendRequestsRecievedUserNames;
    private List<String> friendsList;
    public static List<UserClass> allUsers = UserClass.loadUsers(); // Load users from file.

    private static final String BASE_PATH = "Users";

    // Static initializer block to load idCounter on program start
    static {
        loadIdCounter();
    }

    // Default Constructor
    public UserClass() {
    }

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
        this.postCounter = 0;

        this.myPosts = new ArrayList<>();
        this.friendRequestsSent = new ArrayList<>();
        this.friendRequestsReceived = new ArrayList<>();
        this.friendRequestsRecievedUserNames = new ArrayList<>();
        this.friendsList = new ArrayList<>();

        saveUserToFile();
    }

    //Edit userdetails
    public void updatePostCounter(){
        this.postCounter = ++postCounter;
        saveUserToFile();
    }


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
            saveListToFile(userFolder, "RequestsReceived.json", friendRequestsRecievedUserNames);
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
                                user.friendRequestsRecievedUserNames = loadListFromFile(userFolder, "RequestsReceived.json");
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

    public static UserClass findUserById(String userId) {
        ArrayList<UserClass> allUsers = loadUsers(); // Load all users
        for (UserClass user : allUsers) {
            if (user.userId.equals(userId)) {
                return user; // Return the matching user
            }
        }
        return null; // User not found
    }

    public static String findUsernameById(String userId) {
        ArrayList<UserClass> allUsers = loadUsers(); // Load all users
        for (UserClass user : allUsers) {
            if (user.userId.equals(userId)) {
                return user.userName; // Return the matching user
            }
        }
        return null; // User not found
    }




    // Function: Send Friend Request
    public void sendFriendRequest(String recipientId) {
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
            recipient.friendRequestsRecievedUserNames.add(this.userName); // Add sender to recipient's received list
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
    public void viewFriendRequests() {
//        List<String> requests = graph.viewConnectionRequests(this.userId);
//        System.out.println("Friend Requests Received: " + requests);
        System.out.println("Friend Requests Received: " + friendRequestsReceived);
        System.out.println("Friend Requests Received: " + friendRequestsRecievedUserNames);
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
        this.friendRequestsRecievedUserNames.remove(sender.userName);
        sender.friendRequestsSent.remove(this.userId);
        this.saveUserToFile();
        sender.saveUserToFile();

        updateUserInList(this);
        updateUserInList(sender);
    }
    // Helper method to update the user in allUsers
    private void updateUserInList(UserClass updatedUser) {
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUserId().equals(updatedUser.getUserId())) {
                allUsers.set(i, updatedUser);
                break;
            }
        }
    }

    // Function: View All Friends
    public void viewAllFriends() {
        System.out.println("Your Friends: " + friendsList);
    }


    // Function: User Login
    public static String loginUser(String userName, String password, List<UserClass> users) {
        for (UserClass user : users) {
            if (user.userName.equals(userName) && user.password.equals(password)) {
                currentUser = user;
                return "Login successful! Welcome, " + currentUser.fullName;
            }
        }
        return "Invalid credentials! Try again.";
    }

    // Function: User Logout
    public static String logout() {
        if (currentUser != null) {
            String message = "Logging out... " + currentUser.getUserId();
            currentUser = null;
            return message;
        }
        return "No user is currently logged in.";
    }

// Function: View Mutual Friends

    public ArrayList<String> mutualFriends(String otherUserId) {
        UserClass otherUser = findUserById(otherUserId);
        if (otherUser == null) {
            System.out.println("User with ID " + otherUserId + " does not exist.");
            return null;
        }

        Set<String> myFriends = new HashSet<>(this.friendsList);
        Set<String> otherFriends = new HashSet<>(otherUser.friendsList);

        myFriends.retainAll(otherFriends); // Find intersection of both sets
        ArrayList<String > mutual = new ArrayList<>(myFriends);
        System.out.println("Mutual Friends with User ID " + otherUserId + ": " + myFriends);
        return mutual;
    }


    // Function: Suggested Friends
    public ArrayList<String> suggestedFriends() {
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
        ArrayList<String> userSuggestions = new ArrayList<>();
        while (!suggestions.isEmpty()) {
            Suggestion suggestion = suggestions.poll();
            userSuggestions.add(suggestion.userId);
            System.out.println("User ID: " + suggestion.userId + " | Mutual Friends: " + suggestion.count);
        }
        return userSuggestions;
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


    // Getters and Setters
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

    public List<String> getFriendRequestsRecievedUserNames() {
        return friendRequestsRecievedUserNames;
    }

    public void setFriendRequestsRecievedUserNames(List<String> friendRequestsRecievedUserNames) {
        this.friendRequestsRecievedUserNames = friendRequestsRecievedUserNames;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int idCounter) {
        UserClass.idCounter = idCounter;
    }

    public static void setCurrentUser(UserClass currentUser) {
        UserClass.currentUser = currentUser;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBioDetails() {
        return bioDetails;
    }

    public void setBioDetails(String bioDetails) {
        this.bioDetails = bioDetails;
    }

    public LocalDateTime getUserCreationDate() {
        return userCreationDate;
    }

    public void setUserCreationDate(LocalDateTime userCreationDate) {
        this.userCreationDate = userCreationDate;
    }

    public List<String> getMyPosts() {
        return myPosts;
    }

    public void setMyPosts(List<String> myPosts) {
        this.myPosts = myPosts;
    }

    public List<String> getFriendRequestsSent() {
        return friendRequestsSent;
    }

    public void setFriendRequestsSent(List<String> friendRequestsSent) {
        this.friendRequestsSent = friendRequestsSent;
    }

    public void setFriendRequestsReceived(List<String> friendRequestsReceived) {
        this.friendRequestsReceived = friendRequestsReceived;
    }

    public List<String> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<String> friendsList) {
        this.friendsList = friendsList;
    }

    public static List<UserClass> getAllUsers() {
        return allUsers;
    }

    public static void setAllUsers(List<UserClass> allUsers) {
        UserClass.allUsers = allUsers;
    }


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


}
