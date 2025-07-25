# FriendHub – Social Network Simulation API

A Java + Spring Boot-based REST API that simulates a basic social media platform with features like user registration, friend requests, posts, and graph-based relationship analysis.

## Features

- User registration, friend request system, and mutual connections
- Create, update, and delete user posts
- Graph-based relationship system using **BFS** for:
  - Shortest path between users
  - Identifying top influencers
- API tested using **Postman**
- Persistent data storage with JSON using **Gson** and custom **TypeAdapters**
- Modular design with clean class structure and file handling
- Basic frontend integration using HTML/CSS 

## Technologies Used

- Java 17
- Spring Boot (REST Controller, Dependency Injection)
- Java Collections Framework: `ArrayList`, `HashMap`, `HashSet`, `Queue`
- Gson for JSON serialization/deserialization
- Postman for API testing
- File I/O for data persistence
- HTML/CSS for UI

## Project Structure

friendhub/
├── src/
├── libs/
├── Users/
├── Posts/
├── .idea/
├── idCounter.json
├── postCounter.json
├── README.md


## Sample Postman Endpoints

| Method | Endpoint                  | Description                 |
|--------|---------------------------|-----------------------------|
| POST   | `/users`                  | Register a new user        |
| GET    | `/users`                  | View all users             |
| POST   | `/friends/request`        | Send friend request        |
| POST   | `/posts`                  | Create a post              |
| GET    | `/graph/shortest-path`    | Shortest connection path   |
| GET    | `/graph/influencers`      | List top influencers       |


## How to Run

1. Open in IntelliJ or VS Code
2. Make sure Java 17+ is installed
3. Run `FriendhubApplication.java`
4. Test endpoints via Postman or browser (if using HTML frontend)

## Academic Context

Developed as a **Data Structures & Algorithms** semester project at COMSATS University, with focus on applying graph theory to real-world use cases.

---

## Screenshots

<img width="512" height="156" alt="friendhub 1" src="https://github.com/user-attachments/assets/7750f099-fb74-424a-a8c3-ffe3824492e3" />
<img width="459" height="463" alt="fr 2" src="https://github.com/user-attachments/assets/02583e74-00b5-40db-a451-c2a7bc008750" />
<img width="418" height="468" alt="fr 3" src="https://github.com/user-attachments/assets/0fa99d09-15b1-4053-8124-425f355b7f52" />
<img width="605" height="130" alt="fr 4" src="https://github.com/user-attachments/assets/ec757de5-70dd-47de-acb4-b8de1985f81c" />
<img width="569" height="108" alt="fr 5" src="https://github.com/user-attachments/assets/df941f34-9852-4567-98ce-ae370f261c62" />
<img width="402" height="406" alt="fr 6" src="https://github.com/user-attachments/assets/a47f9635-4102-4853-9a88-343ad9a41041" />
<img width="317" height="494" alt="fr 7" src="https://github.com/user-attachments/assets/8c55406b-1fc9-43b2-90f2-0f73d2a7ee85" />
<img width="528" height="549" alt="fr 8" src="https://github.com/user-attachments/assets/b7819637-4ef1-4290-868c-a407cd8bea9b" />

---

## Future Improvements

- Add user authentication (JWT-based)
- Replace JSON with MySQL or MongoDB
- Build complete frontend with React or Vue

