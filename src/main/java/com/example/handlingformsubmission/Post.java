package com.example.handlingformsubmission;
import static com.example.handlingformsubmission.UserClass.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Post {
    public static int postCounterGlobal = 1;
    private static final String POST_COUNTER_FILE = "postCounter.json"; // File to store postCounter
    private static final String BASE_PATH = "Posts"; // Base directory for post storage
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private String postUserId;
    private String postId;
    private String postContent;
    private LocalDateTime postTimestamp;
    private int likes;
    private List<String> comments;
    public static final List<Post> allPosts = new ArrayList<>();
    private static Map<String, Post> posts = new HashMap<>();
    // Load postCounter from file
    public static void loadPostCounter() {
        try (FileReader reader = new FileReader(POST_COUNTER_FILE)) {
            Integer counterValue = gson.fromJson(reader, Integer.class);
            postCounterGlobal = (counterValue != null) ? counterValue : 1;
        } catch (IOException e) {
            postCounterGlobal = 1; // Default to 0 if file doesn't exist
        }
    }

    // Save postCounter to file
    public static void savePostCounter() {
        try (FileWriter writer = new FileWriter(POST_COUNTER_FILE)) {
            gson.toJson(postCounterGlobal, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Constructor
    public Post(String postUserId, String postContent) {
        this.postUserId = postUserId;
        this.postContent = postContent;
        this.postTimestamp = LocalDateTime.now();
        this.comments = new ArrayList<>();
        this.likes = 0;
        this.postId = String.valueOf(++postCounterGlobal);
        UserClass user = UserClass.findUserById(postUserId);
//        int currentPostCounter = user.postCounter;
//        this.postId = String.valueOf(++currentPostCounter); // Auto-increment post ID
        user.saveUserToFile();
        user.updatePostCounter();
        savePostCounter();
        savePostToFile();
        loadPosts();
    }

    public String getPostId() {
        return postId;
    }


    // Save post details to file
    public void savePostToFile() {
        try {
            File postFolder = new File(BASE_PATH);
            if (!postFolder.exists()) postFolder.mkdirs();

            File postFile = new File(postFolder, "Post_" + postId + ".json");
            try (FileWriter writer = new FileWriter(postFile)) {
                gson.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAllPosts() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BASE_PATH), "post_*.json")) {
            for (Path file : stream) {
                try (Reader reader = new FileReader(file.toFile())) {
                    Post post = gson.fromJson(reader, Post.class);
                    System.out.println(file.getFileName());
                    allPosts.add(post);
                } catch (IOException e) {
                    System.err.println("Error reading file: " + file.getFileName());
                }
            }
            System.out.println("Loaded " + allPosts.size() + " posts.");
        } catch (IOException e) {
            System.err.println("Error accessing directory: " + e.getMessage());
        }
    }
    public static ArrayList<Post> loadPosts() {
        //loadPostCounter();
        ArrayList<Post> postsList = new ArrayList<>();
        File baseDir = new File(BASE_PATH);

        if (baseDir.exists() && baseDir.isDirectory()) {
            File[] postFolders = baseDir.listFiles(file ->file.getName().startsWith("Post_"));

            if (postFolders != null) {
                for (File postFolder : postFolders) {
                    try {
                        // Load Post Details
                        File postFile = new File(postFolder, postFolder.getName() + ".json");
                        if (postFile.exists()) {
                            try (FileReader reader = new FileReader(postFile)) {
                                Post post = gson.fromJson(reader, Post.class);
                                allPosts.add(post); // Add to list
                                posts.put(post.getPostUserId(), post); // Map by User ID or other logic
                                postsList.add(post);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error loading post data from folder: " + postFolder.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
        return postsList;
    }


    public static void viewPostsByUser(String userId) {
        Path postsDirectory = Paths.get(BASE_PATH);
        if (Files.exists(postsDirectory)) {
            try {
                ArrayList<String> postId = new ArrayList<>();
                ArrayList<String> postContent = new ArrayList<>();
                JSONArray jsonArray = new JSONArray();

//                for (int i = 0; i < FriendsId.size(); i++) {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("id", FriendsId.get(i));
//                    jsonObject.put("name", findUsernameById(FriendsId.get(i)));
//                    jsonArray.add(jsonObject);
//                }
                Files.walk(postsDirectory)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                String content = new String(Files.readAllBytes(file));
                                Post post = gson.fromJson(content, Post.class);
                                if (post.postUserId == userId) {
                                    System.out.println("Post ID: " + post.postId);
                                    postId.add(post.postId);
                                    System.out.println("Content: " + post.postContent);
                                    postContent.add(post.postContent);
                                    System.out.println("Timestamp: " + post.postTimestamp);
                                    System.out.println("----------------------------------");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void addLike() {
        likes++;
    }
    public void addComment(String comment) {
        comments.add(comment);
    }


    public void displayPostDetails() {
        System.out.println("Post ID: " + postId);
        System.out.println("Content: " + postContent);
        System.out.println("Likes: " + likes);
        System.out.println("Comments:");
        for (String comment : comments) {
            System.out.println("- " + comment);
        }
        System.out.println("----------------------------------");
    }


    public static void deletePostById(int postId) {
        Path postPath = Paths.get(BASE_PATH, "Post_" + postId, "Post_" + postId + ".json");
        try {
            if (Files.deleteIfExists(postPath)) {
                System.out.println("Post " + postId + " deleted successfully.");
            } else {
                System.out.println("Post " + postId + " not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Utility Function: to load a Post object from file
    public static Post loadPostById(int postId) {
        try {
            Path postPath = Paths.get("Posts", "Post_" + postId, "Post_" + postId + ".json");
            if (Files.exists(postPath)) {
                String content = new String(Files.readAllBytes(postPath));
                return new Gson().fromJson(content, Post.class);
            } else {
                System.out.println("Post with ID " + postId + " does not exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //Global Posts Retrieval
    public static void viewAllPosts() {
        Path postsDirectory = Paths.get(BASE_PATH);
        if (Files.exists(postsDirectory)) {
            try {
                Files.walk(postsDirectory)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                String content = new String(Files.readAllBytes(file));
                                Post post = gson.fromJson(content, Post.class);
                                System.out.println("Post ID: " + post.postId);
                                System.out.println("User ID: " + post.postUserId);
                                System.out.println("Content: " + post.postContent);
                                System.out.println("Timestamp: " + post.postTimestamp);
                                System.out.println("----------------------------------");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // getters/setters for serialization
    public String getContent() { return postContent; }
    public LocalDateTime getTimestamp() { return postTimestamp; }
    public List<String> getComments() {
        return comments;
    }
    public int getLikes() {
        return likes;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public String getPostContent() {
        return postContent;
    }

    public LocalDateTime getPostTimestamp() {
        return postTimestamp;
    }

    // Function 2: Get all posts for a specific userId
    public static List<Map<String, String>> getPostsByUserId(String userId) {
        System.out.println("all posts from getPostsBuUserId:" + allPosts);
        return allPosts.stream()
                .filter(post -> post.getPostUserId().equals(userId))
                .map(post -> {
                    Map<String, String> postDetails = new HashMap<>();
                    postDetails.put("postUserId", post.getPostUserId());
                    postDetails.put("postContent", post.getPostContent());
                    postDetails.put("postTimestamp", post.getPostTimestamp().toString());
                    return postDetails;
                })
                .collect(Collectors.toList());
    }
}
