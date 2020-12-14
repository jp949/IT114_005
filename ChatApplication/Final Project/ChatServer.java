
import java.awt.Font;
import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 * this class is a chat server
 *
 */
public class ChatServer {

	public static boolean isRunning = true;
	// clients connected with the chat server
	private List<UserThread> clients = new ArrayList<UserThread>();
	Queue<String> messages = new LinkedList<String>();
	public static long ClientID = 0;
	private ServerUI listener;

	public void registerListener(ServerUI listener) {
		this.listener = listener;
	}

	public synchronized long getNextId() {
		ClientID++;
		return ClientID;
	}

	public String processCommand(String message) {
		if (message == null) {
			return null;
		}

		if (message.contains("/flip")) {
			double randomNumber = Math.random();
			randomNumber = randomNumber * 2;
			String ht = (randomNumber % 2 == 0)?"head":"tail";
			message = message+" "+ht;
			message ="<span color='red'>"+message+"</span>";
			return message;
		}
		
		if (message.contains("/roll")) {
			int randomNumber = (int) Math.random();
			
			String ht = String.valueOf(randomNumber);
			message = message+" " +ht;
			message ="<span color='green'>"+message+"</span>";
			return message;
		}
		
		if (message.contains("mute @")) {
			String[] partial = message.split("mute @");
			if (partial.length >= 2) {
				String user = partial[1];
				return user +" muted";
			}
		}
		
		if (message.contains("quit @")) {
			String[] partial = message.split("quit @");
			if (partial.length >= 2) {
				return message +" muted you";
			}
		}
		if (message.contains("*") && message.contains("*")) {
			message = message.replaceAll("* ", "<b>");
			message = message.replaceAll("* ", "<b>");
		}
		return message;
	}

	public void paint(Graphics g) {

		Font myFont = new Font("Courier", Font.BOLD | Font.ITALIC, 10);

		g.setFont(myFont);

		g.drawString("Bold & Italic Font Example", 10, 50);
	}

	public void start(int port) {
		startQueueReader();

		try (ServerSocket serverSocket = new ServerSocket(port);) {

			while (ChatServer.isRunning) {
				try {
					Socket client = serverSocket.accept();
					UserThread thread = new UserThread(client, this);
					thread.start();
					thread.setClientId(getNextId());
					clients.add(thread);

					listener.onClientConnected(thread.getClientName());
					System.out.println("Client added to clients pool");
					Iterator<UserThread> iter = clients.iterator();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				isRunning = false;
				Thread.sleep(50);
				System.out.println("closing server socket");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void startQueueReader() {
		System.out.println("Preparing Queue Reader");
		Thread queueReader = new Thread() {
			@Override
			public void run() {
				String message = "";
				try (FileWriter write = new FileWriter("chathistory.txt", true)) {
					while (isRunning) {
						message = messages.poll();
						if (message != null) {
							message = messages.poll();
							write.append(message);
							write.write(System.lineSeparator());
							write.flush();
						}

						sleep(50);
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		queueReader.start();
		System.out.println("Started Queue Reader");
		Thread activeList = new Thread() {
			@Override
			public void run() {

				try {
					while (isRunning) {
						String message = "";
						Iterator<UserThread> iter = clients.iterator();
						while (iter.hasNext()) {
							message += iter.next().getClientName() + "\n";
						}
						Payload payload = new Payload();
						payload.setPayloadType(PayloadType.ACTIVE);
						payload.setMessage(message);
						iter = clients.iterator();
						while (iter.hasNext()) {
							UserThread client = iter.next();
							client.send(payload);
						}

						sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		activeList.start();

	}

	int getClientIndexByThreadId(long id) {
		for (int i = 0, l = clients.size(); i < l; i++) {
			if (clients.get(i).getId() == id) {
				return i;
			}
		}
		return -1;
	}

	public synchronized void broadcast(Payload payload, String name) {
		String msg = payload.getMessage();
		payload.setMessage((name != null ? name : "[Name Error]") + (msg != null ? ": " + msg : ""));
		broadcast(payload);
	}

	public synchronized void broadcast(Payload payload) {
		System.out.println("Sending message to " + clients.size() + " clients");

		Iterator<UserThread> iter = clients.iterator();

		payload.setMessage(processCommand(payload.getMessage()));
		if (payload.getMessage() == null) {
			return;
		}
		while (iter.hasNext()) {
			UserThread client = iter.next();
			boolean messageSent = client.send(payload);
			if (!messageSent) {
				iter.remove();
			}
		}
	}

	public synchronized void broadcast(Payload payload, long id) {

		int from = getClientIndexByThreadId(id);
		String msg = payload.getMessage();
		payload.setMessage((from > -1 ? "Client[" + from + "]" : "unknown") + (msg != null ? ": " + msg : ""));

		broadcast(payload);

	}

	public synchronized void broadcast(String message, long id) {
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.MESSAGE);
		payload.setMessage(message);
		broadcast(payload, id);
	}

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		System.out.print("Port: ");
		int port = scanner.nextInt();

		System.out.println("Starting Server on port: " + port);
		ChatServer server = new ChatServer();
		System.out.println("Listening on port " + port);
		server.start(port);
		System.out.println("Server Stopped");
		scanner.close();
	}
}
