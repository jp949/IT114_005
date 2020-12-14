import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserThread extends Thread {
	private Socket client;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private boolean isRunning = false;
	private ChatServer server;
	private String clientName = "client";

	public UserThread(Socket myClient, ChatServer server) throws IOException {
		this.client = myClient;
		this.server = server;
		isRunning = true;
		out = new ObjectOutputStream(client.getOutputStream());
		in = new ObjectInputStream(client.getInputStream());
	}

	public void setClientId(long id) {
		clientName += "_" + id;
	}

	public String getClientName() {
		return clientName;
	}

	void syncStateToMyClient() {
		System.out.println(this.clientName + " broadcast state");
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.STATE_SYNC);
		try {
			out.writeObject(payload);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	void broadcastConnected() {
		System.out.println(this.clientName + " broadcast connected");
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.CONNECT);
		payload.setMessage("Connected");

		server.broadcast(payload, this.clientName);
	}

	void broadcastDisconnected() {

		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.DISCONNECT);
		payload.setMessage("Disconnected");

		server.broadcast(payload, this.clientName);
	}

	public boolean send(Payload payload) {
		try {
			out.writeObject(payload);
			return true;
		} catch (IOException e) {
			System.out.println("Error sending message to client");
			e.printStackTrace();
			cleanup();
			return false;
		}
	}

	@Override
	public void run() {
		try {

			Payload fromClient;
			while (isRunning && !client.isClosed() && (fromClient = (Payload) in.readObject()) != null) {
				processPayload(fromClient);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Terminating Client");
		} finally {

			broadcastDisconnected();

			System.out.println("Server Cleanup");
			cleanup();
		}
	}

	private void processPayload(Payload payload) {
		System.out.println("Received from client: " + payload);
		switch (payload.getPayloadType()) {
		case CONNECT:
			String clientName = payload.getMessage();
			if (clientName != null) {
				clientName = WordBlackList.filter(clientName);
				this.clientName = clientName;
			}
			broadcastConnected();
			syncStateToMyClient();
			break;
		case DISCONNECT:
			System.out.println("Received disconnect");
			broadcastDisconnected();
			cleanup();
			break;

		case MESSAGE:
			// filter messages
			String message = WordBlackList.filter(payload.getMessage());
			payload.setMessage(message);
			server.broadcast(payload, this.clientName);
			break;
		}
	}

	/**
	 * Following method will cleanup the resources
	 */
	private void cleanup() {
		try {
			in.close();
			out.close();
			client.shutdownInput();
			client.shutdownOutput();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}