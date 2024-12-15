package com.example.handlingformsubmission;
import org.apache.catalina.User;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

import static com.example.handlingformsubmission.Post.getPostsByUserId;
import static com.example.handlingformsubmission.UserClass.allUsers;
import static com.example.handlingformsubmission.UserClass.findUsernameById;

@RestController
@RequestMapping("/api/users")
public class UserController {

    //private ArrayList<UserClass> allUsers = UserClass.loadUsers();

    @GetMapping("/getAllUsers")
    public ResponseEntity<String> getAllUsers(){
        List <UserClass> users = UserClass.getAllUsers();
        JSONArray jArray = new JSONArray();
        ArrayList<String> userIds = new ArrayList<>();
        for (UserClass user: users){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", user.getUserId());
            jsonObject.put("name", findUsernameById(user.getUserId()));
            jArray.add(jsonObject);
        }
//        jArray.add(userIds);
//        System.out.println("Jarray output" + jArray.toString());
        return ResponseEntity.ok(jArray.toJSONString());

//        return jArray.toJSONString();
    }
    // 1 create user
    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody Map<String, String> payload) {
        System.out.println("api/user/createUser called");
        try {
            // Extract fields from the request payload
            String userName = payload.get("userName");
            String fullName = payload.get("fullName");
            LocalDate dateOfBirth = LocalDate.parse(payload.get("dateOfBirth"));
            String gender = payload.get("gender");
            String emailAddress = payload.get("emailAddress");
            String password = payload.get("password");
            String bioDetails = payload.get("bioDetails");

            // Validate required fields
            if (userName == null || fullName == null || emailAddress == null || password == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing required fields: userName, fullName, emailAddress, password.");
            }

            // Create a new user object
            UserClass newUser = new UserClass(userName, fullName, dateOfBirth, gender, emailAddress, password, bioDetails);

            // Add the user to the allUsers list
            allUsers.add(newUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User created successfully with ID: " + newUser.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    // 1. User Login
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Map<String, String> payload) {
        System.out.println("api/users/login called");
        try {
            String userName = payload.get("userName");
            String password = payload.get("password");

            for (UserClass user : allUsers) {
                if (user.getUserName().equals(userName) && user.getPassword().equals(password)) {
                    UserClass.setCurrentUser(user); // Set the current user
                    return ResponseEntity.ok("Login successful! Welcome, " + user.getFullName());
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials! Try again.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred during login: " + e.getMessage());
        }
    }

    // 2. User Logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        System.out.println("api/users/logout called");
        UserClass currentUser = UserClass.getCurrentUser();

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user is currently logged in.");
        }

        UserClass.logout(); // Log out the current user
        return ResponseEntity.ok("Logout successful!");
    }

    // 2. Send Friend Request
//    @GetMapping("/{userId}/sendFriendRequestold/${recipientId}")
//    public String sendFriendRequestold(@PathVariable String userId, @PathVariable String recipientId) {
//        UserClass sender = findUserById(userId);
//        UserClass recipient = findUserById(recipientId);
//
//        if (sender == null || recipient == null) {
//            return "Invalid User IDs.";
//        }
//        if (userId.equals(recipientId)) {
//            return "Cannot send a friend request to yourself.";
//        }
//        if (sender.getFriendRequestsSent().contains(recipientId)) {
//            return "Friend request already sent.";
//        }
//
//        // Update both users
//        sender.getFriendRequestsSent().add(recipientId);
//        recipient.getFriendRequestsReceived().add(userId);
//        sender.saveUserToFile();
//        recipient.saveUserToFile();
//
////        return "Friend request sent successfully to User ID: " + recipientId;
//
//
//// Create a JSON array
//        JSONArray jsonArray = new JSONArray();
//
//// Iterate over both lists and create JSON objects
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("id", recipientId);
//            jsonObject.put("name", findUsernameById(recipientId));
//            jsonArray.add(jsonObject);
//
//        return jsonArray.toJSONString();
//    }

    @PostMapping("/{userId}/sendFriendRequest")
    public ResponseEntity<?> sendFriendRequest(@PathVariable String userId, @RequestBody Map<String, String> payload) {
        UserClass sender = findUserById(userId);
        String recipientId = payload.get("recipientId");
        UserClass recipient = findUserById(recipientId);

        // Check for errors
        if (sender == null || recipient == null) {
            return ResponseEntity.badRequest().body("Invalid User IDs.");
        }
        if (userId.equals(recipientId)) {
            return ResponseEntity.badRequest().body("Cannot send a friend request to yourself.");
        }
        if (sender.getFriendRequestsSent().contains(recipientId)) {
            return ResponseEntity.badRequest().body("Friend request already sent.");
        }
        if (sender.getFriendsList().contains(recipientId)) {
            return ResponseEntity.badRequest().body("You are already friends with this user.");
        }

        // Update friend request lists
        sender.getFriendRequestsSent().add(recipientId);
        recipient.getFriendRequestsReceived().add(userId);
        sender.saveUserToFile();
        recipient.saveUserToFile();

        // Prepare a JSON response for the recipient
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", recipientId);
        jsonObject.put("name", findUsernameById(recipientId));
        jsonArray.add(jsonObject);

        return ResponseEntity.ok(jsonArray.toJSONString());
    }




    // 3. View Friend Requests
    @GetMapping("/{userId}/viewFriendRequests")
    public List<String> viewFriendRequests(@PathVariable String userId) {
        UserClass user = findUserById(userId);
        if (user != null) {
            return user.getFriendRequestsReceived();
        }
        throw new RuntimeException("User not found.");
    }

    @GetMapping("/{userId}/friendRequests")
    public ResponseEntity<List<String>> getFriendRequests(@PathVariable String userId) {
        UserClass user = findUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user.getFriendRequestsReceived());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }




    @GetMapping("/{userId}/friendRequests2")
    public String getFriendRequests2(@PathVariable String userId) {
        UserClass user = findUserById(userId);

        List<String> FriendsId = user.getFriendRequestsReceived();

// Create a JSON array
        JSONArray jsonArray = new JSONArray();

// Iterate over both lists and create JSON objects
        for (int i = 0; i < FriendsId.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", FriendsId.get(i));
            jsonObject.put("name", findUsernameById(FriendsId.get(i)));
            jsonArray.add(jsonObject);
        }

        return jsonArray.toJSONString();
    }


    // 4. Accept/Reject Friend Request
    @PostMapping("/{userId}/respondToFriendRequest")
    public String respondToFriendRequest(
            @PathVariable String userId,
            @RequestParam String senderId,
            @RequestParam boolean accept) {
        UserClass recipient = findUserById(userId);
        UserClass sender = findUserById(senderId);

        if (recipient == null || sender == null) {
            return "Invalid User IDs.";
        }

        if (recipient.getFriendRequestsReceived().contains(senderId)) {
            if (accept) {
                recipient.getFriendsList().add(senderId);
                sender.getFriendsList().add(userId);
                recipient.getFriendRequestsReceived().remove(senderId);
                sender.getFriendRequestsSent().remove(userId);
                recipient.saveUserToFile();
                sender.saveUserToFile();
                return "Friend request accepted from User ID: " + senderId;
            } else {
                recipient.getFriendRequestsReceived().remove(senderId);
                sender.getFriendRequestsSent().remove(userId);
                return "Friend request rejected from User ID: " + senderId;
            }
        } else {
            return "No friend request exists from User ID: " + senderId;
        }
    }


    // 5. Find User by ID
    @GetMapping("/{userId}")
    public UserClass findUserById(@PathVariable String userId) {
        System.out.println("api/user/userId called");
        for (UserClass user : allUsers) {
            if (user.getUserId().equals(userId)) {
                return user; // Return the matching user
            }
        }
        throw new RuntimeException("User not found with ID: " + userId);
    }

    // 6. View All Friends
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<String>> viewAllFriends(@PathVariable String userId) {
        try {
            UserClass user = findUserById(userId);
            if (user != null) {
                // Return the list of friends
                return ResponseEntity.ok(user.getFriendsList());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @GetMapping("/{userId}/ViewAllFriends2")
    public String ViewAllFriends2(@PathVariable String userId) {
        UserClass user = findUserById(userId);

        List<String> FriendsId = user.getFriendsList();

// Create a JSON array
        JSONArray jsonArray = new JSONArray();

// Iterate over both lists and create JSON objects
        for (int i = 0; i < FriendsId.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", FriendsId.get(i));
            jsonObject.put("name", findUsernameById(FriendsId.get(i)));
            jsonArray.add(jsonObject);
        }

        return jsonArray.toJSONString();
    }


    @GetMapping("/{userId}/viewSuggestedFriends")
    public String viewSuggestedFriends(@PathVariable String userId) {
        UserClass user = findUserById(userId);

        List<String> FriendsId = user.suggestedFriends();

// Create a JSON array
        JSONArray jsonArray = new JSONArray();

// Iterate over both lists and create JSON objects
        for (int i = 0; i < FriendsId.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", FriendsId.get(i));
            jsonObject.put("name", findUsernameById(FriendsId.get(i)));
            jsonArray.add(jsonObject);
        }

        return jsonArray.toJSONString();
    }

    @GetMapping("/{userId}/viewMutualFriends/{otherUserId}")
    public String viewMutualFriends(@PathVariable String userId, @PathVariable String otherUserId) {
        UserClass user = findUserById(userId);

        List<String> FriendsId = user.mutualFriends(otherUserId);

// Create a JSON array
        JSONArray jsonArray = new JSONArray();

// Iterate over both lists and create JSON objects
        for (int i = 0; i < FriendsId.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", FriendsId.get(i));
            jsonObject.put("name", findUsernameById(FriendsId.get(i)));
            jsonArray.add(jsonObject);
        }

        return jsonArray.toJSONString();
    }



    @PostMapping("/{userId}/createPost")
    public ResponseEntity<String> createPost(@PathVariable String userId, @RequestBody Map<String, String> payload) {
        UserClass user = findUserById(userId);

        System.out.println("api/user/createPost called");
        try {
            // Extract fields from the request payload
            String postUserId = userId;
            String postContent = payload.get("postContent");


            System.out.println("Creating post for " + userId);
            System.out.println("Content of post : " + postContent);
            // Validate required fields
            if (postUserId == null || postContent == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing required fields: postContent");
            }

            // Create a new post object
            Post newPost = new Post(postUserId, postContent);

            user.myPosts.add(newPost.getPostId());

            //return ResponseEntity.ok({"message" : "Post created successfully with ID: " + newPost.getPostId()});
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Post created successfully with ID: " + newPost.getPostId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating post: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/myPosts")
    public String myPosts(@PathVariable String userId) {
        UserClass user = findUserById(userId);

        List<Map<String, String>> posts = getPostsByUserId(userId);
        System.out.println("posts for user id " + userId);
        System.out.println(posts);
        System.out.println("JSON Return post is below");
        JSONArray jsonArray = new JSONArray();


            for (Map<String, String> post : posts) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("UserId", post.get("postUserId"));
                jsonObject.addProperty("postContent", post.get("postContent"));
                jsonObject.addProperty("postTimestamp", post.get("postTimestamp"));
                jsonArray.add(jsonObject);
            }

        System.out.println(jsonArray.toJSONString());
        return jsonArray.toJSONString();
    }

    @GetMapping("/{userId}/myFeedold")
    public String myFeedold(@PathVariable String userId) {
        System.out.println("Starting the MyFeed");
        UserClass user = findUserById(userId);
        JSONArray jsonArray = new JSONArray();

        List<String> list = user.getFriendsList();
        List<Map<String, String>> posts = new ArrayList<>();
        for(String u: list){
            System.out.println("Size of list in MyFeed:   " + list.size());
            System.out.println("Strint u in list in Myfeed" + u);
            List<Map<String, String>> postsIterator = getPostsByUserId(u);
            System.out.println("After getPostsbyUserID in MyFeed");
            System.out.println("post Iterator Size:  " + postsIterator.size());
            if(postsIterator.size() == 0){continue;}
            for(Map<String, String> postItem : postsIterator){
                System.out.println("In the Feed");
                System.out.println(postItem);
                //posts.add(item);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("UserId", postItem.get("postUserId"));
                jsonObject.addProperty("postContent", postItem.get("postContent"));
                jsonObject.addProperty("postTimestamp", postItem.get("postTimestamp"));
                jsonArray.add(jsonObject);
            }
        }


//        for (Map<String, String> post : posts) {
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("postId", post.get("postId"));
//            jsonObject.addProperty("postContent", post.get("postContent"));
//            jsonObject.addProperty("postTimestamp", post.get("postTimestamp"));
//            jsonArray.add(jsonObject);
//        }

        System.out.println(jsonArray.toJSONString());
        return jsonArray.toJSONString();
    }



    @GetMapping("/{userId}/myFeed")
    public String myFeeds(@PathVariable String userId) {
        UserClass user = findUserById(userId);

        // Step 1: Get list of friends for the user
        List<String> friendIds = user.getFriendsList(); // Implement this method to fetch friend IDs
        System.out.println("Friends of user " + userId + ": " + friendIds);

        // Step 2: Fetch posts for all friends
        List<Map<String, String>> allFriendPosts = new ArrayList<>();
        for (String friendId : friendIds) {
            List<Map<String, String>> friendPosts = getPostsByUserId(friendId); // Fetch friend's posts
            allFriendPosts.addAll(friendPosts);
        }

        // Step 3: Prepare JSON response
        JSONArray jsonArray = new JSONArray();
        for (Map<String, String> post : allFriendPosts) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("UserId", post.get("postUserId"));
            jsonObject.addProperty("postContent", post.get("postContent"));
            jsonObject.addProperty("postTimestamp", post.get("postTimestamp"));
            jsonArray.add(jsonObject);
        }

        System.out.println("JSON Feed for user " + userId + ":");
        System.out.println(jsonArray.toJSONString());

        return jsonArray.toJSONString();
    }

}
