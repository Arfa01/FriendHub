import java.io.*;
import java.util.*;

class Graph {
    // Adjacency list to store connections
    private Map<Integer, Set<Integer>> adjacencyList;
    // Connection requests
    private Map<Integer, Set<Integer>> connectionRequests;

    public Graph() {
        adjacencyList = new HashMap<>();
        connectionRequests = new HashMap<>();
    }

    // Add a node to the graph
    public void addNode(int id) {
        if (!adjacencyList.containsKey(id)) {
            adjacencyList.put(id, new HashSet<>());
            System.out.println("Node " + id + " added.");
        } else {
            System.out.println("Node " + id + " already exists.");
        }
    }

    // Remove a node and all its connections
    public void removeNode(int id) {
        if (adjacencyList.containsKey(id)) {
            adjacencyList.remove(id);
            for (Set<Integer> connections : adjacencyList.values()) {
                connections.remove(id);
            }
            System.out.println("Node " + id + " removed.");
        } else {
            System.out.println("Node " + id + " does not exist.");
        }
    }

    // Add a connection between two nodes
    public void addConnection(int id1, int id2) {
        if (adjacencyList.containsKey(id1) && adjacencyList.containsKey(id2)) {
            adjacencyList.get(id1).add(id2);
            adjacencyList.get(id2).add(id1);
            System.out.println("Connection added between " + id1 + " and " + id2);
        } else {
            System.out.println("One or both nodes do not exist.");
        }
    }

    // Remove a connection between two nodes
    public void removeConnection(int id1, int id2) {
        if (adjacencyList.containsKey(id1) && adjacencyList.get(id1).contains(id2)) {
            adjacencyList.get(id1).remove(id2);
            adjacencyList.get(id2).remove(id1);
            System.out.println("Connection removed between " + id1 + " and " + id2);
        } else {
            System.out.println("Connection does not exist between " + id1 + " and " + id2);
        }
    }

    // View all connections of a node
    public List<Integer> viewConnectionsById(int id) {
        if (adjacencyList.containsKey(id)) {
            //System.out.println("Connections of node " + id + ": " + adjacencyList.get(id));
            return (List<Integer>) adjacencyList.get(id);
        } else {
            System.out.println("User " + id + " does not exist.");
            return null;
        }
    }

    // View common connections between two nodes
    public List<Integer> viewCommonConnectionsBetweenNodes(int id1, int id2) {
        if (adjacencyList.containsKey(id1) && adjacencyList.containsKey(id2)) {
            Set<Integer> common = new HashSet<>(adjacencyList.get(id1));
            common.retainAll(adjacencyList.get(id2));
            //System.out.println("Common connections between " + id1 + " and " + id2 + ": " + common);
            return (List<Integer>) common;
        } else {
            System.out.println("One or both users do not exist.");
        }
        return null;
    }

    // Find next-to-adjacent nodes of a node (nodes 2 hops away)
    public List<Integer> findNextToAdjacentNodes(int id) {
        if (!adjacencyList.containsKey(id)) {
            System.out.println("Node " + id + " does not exist.");
            return null;
        }

        Set<Integer> nextToAdjacent = new HashSet<>();
        for (Integer neighbor : adjacencyList.get(id)) {
            nextToAdjacent.addAll(adjacencyList.get(neighbor));
        }
        nextToAdjacent.removeAll(adjacencyList.get(id)); // Remove direct connections
        nextToAdjacent.remove(id); // Remove the node itself

        //System.out.println("Next-to-adjacent nodes of " + id + ": " + nextToAdjacent);
        return (List<Integer>) nextToAdjacent;
    }

    // Load graph from a file
    public void loadGraphFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader((filename)))) {
            adjacencyList.clear();
            connectionRequests.clear();

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                int nodeId = Integer.parseInt(parts[0]);
                addNode(nodeId);

                if (parts.length > 1) {
                    String[] connections = parts[1].split(",");
                    for (String conn : connections) {
                        if (!conn.isEmpty()) {
                            addConnection(nodeId, Integer.parseInt(conn));
                        }
                    }
                }
            }
            System.out.println("Graph loaded from file: " + filename);
        } catch (IOException e) {
            System.out.println("Error loading graph from file: " + e.getMessage());
        }
    }

    // Save graph to a file
    public void saveGraphToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<Integer, Set<Integer>> entry : adjacencyList.entrySet()) {
                bw.write(entry.getKey() + ":");
                bw.write(String.join(",", entry.getValue().stream()
                        .map(String::valueOf)
                        .toArray(String[]::new)));
                bw.newLine();
            }
            System.out.println("Graph saved to file: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving graph to file: " + e.getMessage());
        }
    }

    // Add a connection request from one node to another
    public void addConnectionRequest(int id1, int id2) {
        if (id1 == id2) {
            System.out.println("A node cannot send a connection request to itself.");
            return;
        }

        connectionRequests.putIfAbsent(id2, new HashSet<>());
        connectionRequests.get(id2).add(id1);

        System.out.println("Connection request sent from " + id1 + " to " + id2);
    }

    // View pending connection requests for a node
    public List<Integer> viewConnectionRequests(int id) {
        if (connectionRequests.containsKey(id)) {
            //System.out.println("Connection requests for " + id + ": " + connectionRequests.get(id));
            return (List<Integer>) connectionRequests.get(id);
        } else {
            //System.out.println("No connection requests for node " + id);
            return null;
        }
    }

    public void removeConnectionRequest(int senderId, int receiverId) {
        if (connectionRequests.containsKey(receiverId)) {
            List<Integer> requests = (List<Integer>) connectionRequests.get(receiverId);
            if (requests.remove((Integer) senderId)) { // Remove senderId from the list
                System.out.println("Friend request from User " + senderId + " to User " + receiverId + " has been removed.");
            } else {
                System.out.println("No friend request found from User " + senderId + " to User " + receiverId + ".");
            }
        } else {
            System.out.println("User " + receiverId + " has no pending friend requests.");
        }
    }




    //EXTRA FUNCTIONS

    //“How you’re connected to a celebrity or influencer”
    // a fun feature (e.g., “you’re 3 steps away from user X”).
    public List<Integer> findShortestFriendshipPath(int startId, int endId) {
        if (!adjacencyList.containsKey(startId) || !adjacencyList.containsKey(endId)) {
            throw new IllegalArgumentException("One or both user IDs do not exist.");
        }

        Queue<List<Integer>> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.add(Arrays.asList(startId));

        while (!queue.isEmpty()) {
            List<Integer> path = queue.poll();
            int current = path.get(path.size() - 1);

            if (current == endId) return path;
            if (!visited.contains(current)) {
                visited.add(current);
                for (int neighbor : adjacencyList.get(current)) {
                    List<Integer> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }
        return Collections.emptyList(); // No path exists
    }


    //Identify top influencers based on their connectivity score.
    public Map<Integer, Integer> calculateInfluenceScores() {
        Map<Integer, Integer> influenceScores = new HashMap<>();
        for (Integer node : adjacencyList.keySet()) {
            influenceScores.put(node, adjacencyList.get(node).size());
        }
        return influenceScores;
    }




}



/*
public class GeneralGraph{
    public static void main(String[] args) {
        Graph graph = new Graph();

        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.addNode(5);
        graph.addNode(6);
        graph.addNode(7);

        graph.addConnection(1,2);
        graph.addConnection(1,3);
        graph.addConnection(1,4);
        graph.addConnection(4,2);
        graph.addConnection(4,7);
        graph.addConnection(7,2);
        graph.addConnection(5,6);
        graph.addConnection(6,2);

        graph.viewConnectionsById(2);
        graph.viewConnectionsById(3);
        graph.viewConnectionsById(4);
        graph.viewConnectionsById(7);

        graph.viewCommonConnectionsBetweenNodes(2,7);

        graph.findNextToAdjacentNodes(5);

        graph.addConnectionRequest(5, 1);

        graph.removeNode(7);

        graph.saveGraphToFile("graph.txt");
        graph.loadGraphFromFile("graph.txt");

    }
}

 */