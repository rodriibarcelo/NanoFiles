# NanoFiles - A Distributed File Sharing System

NanoFiles is a peer-to-peer (P2P) file-sharing system that enables users to share and access files efficiently over a distributed network. It includes a directory server for node management and uses socket-based communication for seamless interaction between peers.

📂 Project Structure

NFServerComm - Handles communication between nodes and the server.

NFServerThread - Manages client connections in separate threads.

NFConnector - Facilitates connections between P2P nodes.

NFController - Main system controller.

NFControllerLogicP2P - Implements logic for P2P communication.

NFControllerLogicDir - Handles interactions with the directory server.

PeerMessage - Defines messages exchanged between nodes.

DirMessage - Defines messages sent to the directory server.

NFDirectoryServer - Manages node registration and queries.

NFShell - Command-line interface for user interaction.

🚀 Compilation and Usage

Requirements

Java 8 or later

Eclipse or any compatible IDE

Running the Project

Compile the project in a Java-supported environment.

Start the directory server by running NFDirectoryServer.

Launch multiple client instances using NFShell.

Clients can register and search for files in the P2P network.

👥 Authors

Developed by the project team.

📜 License

This project is licensed under the MIT License.
