public class extra {
    /*

    // Function to create a new user
    public void createUser(Graph graph) {
        try {
            // Auto-generate user ID (< 500)
            Random random = new Random();
            userId = random.nextInt(500);

            // Input user details
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Full Name: ");
            fullName = scanner.nextLine();
            System.out.print("Enter User Name: ");
            userName = scanner.nextLine();
            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            dateOfBirth = LocalDate.parse(scanner.nextLine());
            System.out.print("Enter Gender: ");
            gender = scanner.nextLine();
            System.out.print("Enter Email Address: ");
            emailAddress = scanner.nextLine();
            System.out.print("Enter Password: ");
            password = scanner.nextLine();
            System.out.print("Enter Bio Details: ");
            bioDetails = scanner.nextLine();

            // Auto-set user creation date
            userCreationDate = LocalDateTime.now();

            // Save user details to file
            saveUserDetails();

            // Add the user as a node in the graph
            graph.addNode(userId);
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    // Save user details into separate files
    private void saveUserDetails() {
        try {
            // Create Users folder if not exists
            Path usersDir = Paths.get("Users");
            if (!Files.exists(usersDir)) {
                Files.createDirectory(usersDir);
            }

            // User folder
            Path userFolder = usersDir.resolve("User_" + userId);
            if (!Files.exists(userFolder)) {
                Files.createDirectory(userFolder);
            }

            // Save userDetails
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(userFolder.resolve("userDetails.txt").toString()))) {
                writer.write("User ID: " + userId + "\n");
                writer.write("Full Name: " + fullName + "\n");
                writer.write("User Name: " + userName + "\n");
                writer.write("Date of Birth: " + dateOfBirth + "\n");
                writer.write("Gender: " + gender + "\n");
                writer.write("Email Address: " + emailAddress + "\n");
                writer.write("Password: " + password + "\n");
                writer.write("Bio: " + bioDetails + "\n");
                writer.write("User Creation Date: " + userCreationDate + "\n");
            }

            // Save postIds
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(userFolder.resolve("postIds.txt").toString()))) {
                writer.write(myPosts.toString());
            }

            // Save friendRequestsSent
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(userFolder.resolve("requestsSent.txt").toString()))) {
                writer.write(friendRequestsSent.toString());
            }

            // Save friendRequestsReceived
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(userFolder.resolve("requestsReceived.txt").toString()))) {
                writer.write(friendRequestsReceived.toString());
            }

            System.out.println("User details saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving user details: " + e.getMessage());
        }
    }

    // Function to create a new post
    public void createPost(Stack<Post> postStack) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Post Content: ");
            String content = scanner.nextLine();

            Post post = new Post(userId, content);
            postStack.push(post);

            // Add post ID to user's posts
            myPosts.add(post.getPostId());

            // Save updated posts
            saveUserDetails();
            post.saveToFile();

            System.out.println("Post created successfully!");
        } catch (Exception e) {
            System.out.println("Error creating post: " + e.getMessage());
        }
    }
    // Function to view all posts by the user
    public void viewMyPosts() {
        System.out.println("My Posts: " + myPosts);
    }
 */

    /*
    public static void main(String[] args) {
        Graph graph = new Graph(); // Graph object
        Stack<Post> postStack = new Stack<>(); // Stack for posts

        // Create a new user
        UserClass user1 = new UserClass();
        user1.createUser(graph);

        // Create posts for the user
        user1.createPost(postStack);
        user1.createPost(postStack);

        // View user's posts
        user1.viewMyPosts();
    }
    */



    /*
    // Save post details to file
    public void saveToFile() {
        try {
            // Create Posts folder if not exists
            Path postsDir = Paths.get("Posts");
            if (!Files.exists(postsDir)) {
                Files.createDirectory(postsDir);
            }

            // Save post details to file named postId
            Path postFile = postsDir.resolve("Post_" + postId + ".txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(postFile.toString()))) {
                writer.write("Post ID: " + postId + "\n");
                writer.write("User ID: " + postUserId + "\n");
                writer.write("Content: " + postContent + "\n");
                writer.write("Timestamp: " + postTimestamp + "\n");
            }

            System.out.println("Post saved to file: Post_" + postId + ".txt");
        } catch (IOException e) {
            System.out.println("Error saving post: " + e.getMessage());
        }
    }
     */


}
