import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Post {
    private static int postCounter = 0;
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
    private static Map<String, Post> posts = new HashMap<>();
    // Load postCounter from file
    private static void loadPostCounter() {
        try (FileReader reader = new FileReader(POST_COUNTER_FILE)) {
            Integer counterValue = gson.fromJson(reader, Integer.class);
            postCounter = (counterValue != null) ? counterValue : 0;
        } catch (IOException e) {
            postCounter = 0; // Default to 0 if file doesn't exist
        }
    }

    // Save postCounter to file
    private static void savePostCounter() {
        try (FileWriter writer = new FileWriter(POST_COUNTER_FILE)) {
            gson.toJson(postCounter, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Constructor
    public Post(String postUserId, String postContent) {
        this.postUserId = postUserId;
        this.postContent = postContent;
        this.postId = String.valueOf(++postCounter); // Auto-increment post ID
        this.postTimestamp = LocalDateTime.now();
        this.comments = new ArrayList<>();
        this.likes = 0;
        savePostCounter();
    }

    public String getPostId() {
        return postId;
    }


    // Save post details to file
    public void savePostToFile() {
        try {
            File postFolder = new File(BASE_PATH + "/Post_" + postId);
            if (!postFolder.exists()) postFolder.mkdirs();

            File postFile = new File(postFolder, "Post_" + postId + ".json");
            try (FileWriter writer = new FileWriter(postFile)) {
                gson.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Post> loadPosts() {
        loadPostCounter();
        ArrayList<Post> postsList = new ArrayList<>();
        File baseDir = new File(BASE_PATH);

        if (baseDir.exists() && baseDir.isDirectory()) {
            File[] postFolders = baseDir.listFiles(file -> file.isDirectory() && file.getName().startsWith("Post_"));

            if (postFolders != null) {
                for (File postFolder : postFolders) {
                    try {
                        // Load Post Details
                        File postFile = new File(postFolder, postFolder.getName() + ".json");
                        if (postFile.exists()) {
                            try (FileReader reader = new FileReader(postFile)) {
                                Post post = gson.fromJson(reader, Post.class);
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
                Files.walk(postsDirectory)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                String content = new String(Files.readAllBytes(file));
                                Post post = gson.fromJson(content, Post.class);
                                if (post.postUserId == userId) {
                                    System.out.println("Post ID: " + post.postId);
                                    System.out.println("Content: " + post.postContent);
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
}
