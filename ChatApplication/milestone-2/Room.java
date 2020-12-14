import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Room {
	private SocketServer server;// used to refer to accessible server functions
	private String name;

	public Room(String name, SocketServer server) {
		this.name = name;
		this.server = server;
	}

	public String getName() {
		return name;
	}

	private List<ServerThread> clients = new ArrayList<ServerThread>();

	protected synchronized void addClient(ServerThread client) {
		client.setCurrentRoom(this);
		if (clients.indexOf(client) > -1) {
			Debug.log("Attempting to add a client that already exists");
		} else {
			clients.add(client);
			sendMessage(client, "joined the room " + getName());
		}
	}

	protected synchronized void removeClient(ServerThread client) {
		clients.remove(client);
		// we don't need to broadcast it to the server
		// only to our own Room
		sendMessage(client, "left the room");
	}

	/***
	 * Helper function to process messages to trigger different functionality.
	 * 
	 * @param message The original message being sent
	 * @param client  The sender of the message (since they'll be the ones
	 *                triggering the actions)
	 */
	private boolean processJoinRoomCommands(String message, ServerThread client) {
		boolean wasCommand = false;
		try {
			if (message.indexOf("/") > -1) {
				String[] comm = message.split("/");
				String part1 = comm[1];
				String[] comm2 = part1.split(" ");
				String command = comm2[0];
				String roomName;
				switch (command) {
				case "createroom":
					roomName = comm2[1];
					if (server.createNewRoom(roomName)) {
						server.joinRoom(roomName, client);
					}
					wasCommand = true;
					break;
				case "joinroom":
					roomName = comm2[1];
					server.joinRoom(roomName, client);
					wasCommand = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wasCommand;
	}
	
	
	private String processPlayCommands(String message) {
		try {
			if (message.indexOf("/") > -1) {
				String[] comm = message.split("/");
				String part1 = comm[1];
				String[] comm2 = part1.split(" ");
				String command = comm2[0];
				switch (command) {
				case "flip":
					message = (generateRandomNumber(2)%2 ==0)?"head":"tail";
					break;
					
				case "roll":
					message = String.valueOf((generateRandomNumber(10000)));
					break;	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	
	private String processStyling(String message) {
		if(message == null) return null;
		
		if(message.startsWith("*") && message.endsWith("*")) {
			message = message.replaceAll("\\*", "");
			message= "<b>"+message+"</b>";
		}
		else if(message.startsWith("_") && message.endsWith("_")) {
			message = message.replaceAll("_", "");
			message= "<i>"+message+"</i>";
		}
		else if(message.startsWith("-") && message.endsWith("-")) {
			message = message.replaceAll("\\-", "");
			message= "<u>"+message+"</u>";
		}
		else if(message.startsWith("#") && message.endsWith("#")) {
			message = message.replaceAll("#", "");
			message = "<color>"+message+"</color>";
		}
		return message;
	}

	/***
	 * Takes a sender and a message and broadcasts the message to all clients in
	 * this room. Client is mostly passed for command purposes but we can also use
	 * it to extract other client info.
	 * 
	 * @param sender  The client sending the message
	 * @param message The message to broadcast inside the room
	 */
	protected void sendMessage(ServerThread sender, String message) {
		Debug.log(getName() + ": Sending message to " + clients.size() + " clients");
		if (processJoinRoomCommands(message, sender)) {
			// it was a command, don't broadcast
			return;
		}
		
		message = processPlayCommands(message);
		message = processStyling(message);		
		Iterator<ServerThread> iter = clients.iterator();
		message = String.format("User[%s]: %s", sender.getName(), message);
		while (iter.hasNext()) {
			ServerThread client = iter.next();
			boolean messageSent = client.send(message);
			if (!messageSent) {
				iter.remove();
				Debug.log("Removed client " + client.getId());
			}
		}
	}
	
	private int generateRandomNumber(int upperLimit) {
		Random rand = new Random();
		return rand.nextInt(upperLimit); 
	}
}
