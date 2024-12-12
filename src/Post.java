import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private static int postCounter = 0;
    private int postUserId;
    private int postId;
    private String postContent;
    private LocalDateTime postTimestamp;
    private int likes;
    private List<String> comments = new ArrayList<>();
    private static final String BASE_PATH = "Posts";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    // Constructor
    public Post(int postUserId, String postContent) {
        this.postUserId = postUserId;
        this.postContent = postContent;
        this.postId = ++postCounter; // Auto-increment post ID
        this.postTimestamp = LocalDateTime.now();
    }

    public int getPostId() {
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


    public static void viewPostsByUser(int userId) {
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


}
