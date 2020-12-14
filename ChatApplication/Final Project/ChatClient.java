import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ChatClient {
	private Socket server;
	private ClientUI listener;

	public void registerListener(ClientUI listener) {
		this.listener = listener;
	}

	private Queue<Payload> toServer = new LinkedList<Payload>();
	private Queue<Payload> fromServer = new LinkedList<Payload>();

	public static ChatClient connect(String address, int port) {
		ChatClient client = new ChatClient();
		client._connect(address, port);
		Thread clientThread = new Thread() {
			@Override
			public void run() {
				client.start();
			}
		};
		clientThread.start();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return client;
	}

	private void _connect(String address, int port) {
		try {
			server = new Socket(address, port);
			System.out.println("Client connected");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void start() {
		if (server == null) {
			return;
		}
		System.out.println("Client Started");

		try (ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(server.getInputStream());) {
			Thread inputThread = new Thread() {
				@Override
				public void run() {
					try {
						while (!server.isClosed()) {
							Payload p = toServer.poll();
							if (p != null) {
								out.writeObject(p);
							} else {
								try {
									Thread.sleep(8);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
						System.out.println("Client shutdown");
					} finally {
						close();
					}
				}
			};
			inputThread.start();

			Thread fromServerThread = new Thread() {
				@Override
				public void run() {
					try {
						Payload p;

						while (!server.isClosed() && (p = (Payload) in.readObject()) != null) {

							fromServer.add(p);
						}
						System.out.println("Stopping server listen thread");
					} catch (Exception e) {
						if (!server.isClosed()) {
							e.printStackTrace();
							System.out.println("Server closed connection");
						} else {
							System.out.println("Connection closed");
						}
					} finally {
						close();
					}
				}
			};
			fromServerThread.start();

			Thread payloadProcessor = new Thread() {
				@Override
				public void run() {
					while (!server.isClosed()) {
						Payload p = fromServer.poll();
						if (p != null) {
							processPayload(p);
						} else {
							try {
								Thread.sleep(8);
							} catch (InterruptedException e) {

								e.printStackTrace();
							}
						}
					}
				}
			};
			payloadProcessor.start();

			while (!server.isClosed()) {
				Thread.sleep(50);
			}
			System.out.println("Exited loop");
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void postConnectionData(String clientName) {
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.CONNECT);
		payload.setMessage(clientName);
		toServer.add(payload);
	}

	public void disconnect() {
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.DISCONNECT);
		payload.setMessage("");
		toServer.add(payload);
	}

	public void sendMessage(String message) {
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.MESSAGE);
		payload.setMessage(message);
		toServer.add(payload);
	}

	private void processPayload(Payload payload) {
		System.out.println(payload);
		switch (payload.getPayloadType()) {
		case ACTIVE:
			listener.onReciveActive(payload.getMessage());
			break;
		case CONNECT:
			System.out.println(String.format("Client \"%s\" connected", payload.getMessage()));
			if (listener != null) {
				listener.onReceiveConnect(payload.getMessage());
			}
			break;
		case DISCONNECT:
			System.out.println(String.format("Client \"%s\" disconnected", payload.getMessage())

			);

			break;
		case MESSAGE:
			System.out.println(String.format("%s", payload.getMessage()));
			if (listener != null) {
				listener.onReceiveMessage(payload.getMessage());

			}

			break;
		case STATE_SYNC:
			System.out.println("Sync");
			break;

		default:
			System.out.println("Unhandled payload type: " + payload.getPayloadType().toString());
			break;
		}
	}

	private void close() {
		if (server != null) {
			try {
				server.close();
				System.out.println("Closed socket");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		ChatClient.connect("127.0.0.1", 3001);
		try {

			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
