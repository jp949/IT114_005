
import javax.swing.text.DefaultCaret;

/**
 * 
 * This class is the user interface of chat application
 *
 */
public class ChatApplicationUI extends javax.swing.JFrame {

	private static final long serialVersionUID = 507030164269413483L;
	static ChatClient client;

	public ChatApplicationUI() {
		initComponents();
		DefaultCaret caret = (DefaultCaret) messageHistoryPanel.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		DefaultCaret caret1 = (DefaultCaret) activeClientsPanel.getCaret();
		caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	/**
	 * initialize components
	 */
	private void initComponents() {

		userNameField = new javax.swing.JTextField();
		host = new javax.swing.JTextField();
		portField = new javax.swing.JTextField();
		activeUsers = new javax.swing.JScrollPane();
		activeClientsPanel = new javax.swing.JTextArea();
		jScrollPane4 = new javax.swing.JScrollPane();
		messageArea = new javax.swing.JTextArea();
		disconnectButton = new javax.swing.JButton();
		jScrollPane6 = new javax.swing.JScrollPane();
		messageHistoryPanel = new javax.swing.JTextArea();
		connect = new javax.swing.JButton();
		sendButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		userNameField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				userNameActionPerformed(evt);
			}
		});

		host.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				hostActionPerformed(evt);
			}
		});

		userNameField.setText("User");
		host.setText("127.0.0.1");
		portField.setText("3001");

		activeClientsPanel.setColumns(20);
		activeClientsPanel.setRows(100);
		activeUsers.setViewportView(activeClientsPanel);

		messageArea.setColumns(20);
		messageArea.setRows(5);
		jScrollPane4.setViewportView(messageArea);

		disconnectButton.setText("Disconnect");
		disconnectButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				disconnectActionPerformed(evt);
			}
		});

		messageHistoryPanel.setColumns(20);
		messageHistoryPanel.setRows(5);
		jScrollPane6.setViewportView(messageHistoryPanel);

		connect.setText("Connect");
		connect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				connectActionPerformed(evt);
			}
		});

		sendButton.setText("Send");
		sendButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(userNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 49,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(host, javax.swing.GroupLayout.PREFERRED_SIZE, 127,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, 58,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(connect).addGap(0, 8, Short.MAX_VALUE))
								.addComponent(activeUsers).addComponent(jScrollPane4).addComponent(jScrollPane6)
								.addGroup(layout.createSequentialGroup().addComponent(disconnectButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(sendButton)))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(27, 27, 27)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(userNameField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(host, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(connect))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(activeUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 136,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)
						.addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(disconnectButton).addComponent(sendButton))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		pack();
	}

	private void userNameActionPerformed(java.awt.event.ActionEvent evt) {

	}

	private void hostActionPerformed(java.awt.event.ActionEvent evt) {

	}

	private void connectActionPerformed(java.awt.event.ActionEvent evt) {
		int _port = -1;
		try {
			_port = Integer.parseInt(portField.getText());
		} catch (Exception num) {
			System.out.println("Port not a number");
		}
		if (_port > -1) {
			client = ChatClient.connect(host.getText(), _port);
//			client.registerListener(this);
			client.postConnectionData(userNameField.getText());
			connect.setEnabled(false);

		}
	}

	private void sendActionPerformed(java.awt.event.ActionEvent evt) {
		client.sendMessage(messageArea.getText());
		messageArea.setText("");
	}

	private void disconnectActionPerformed(java.awt.event.ActionEvent evt) {
		client.disconnect();
	}

	public static void main(String args[]) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ChatApplicationUI().setVisible(true);
			}
		});
	}

	public void onReciveActive(String userNames) {
		if (activeClientsPanel != null) {
			activeClientsPanel.setText(userNames);
		}
	}

	public void onReceiveConnect(String userName) {

		if (messageHistoryPanel != null) {
			messageHistoryPanel.append(userName);
			messageHistoryPanel.append(System.lineSeparator());
		}

	}

	public void onReceiveMessage(String Message) {
		if (messageHistoryPanel != null) {
			messageHistoryPanel.append(Message);
			messageHistoryPanel.append(System.lineSeparator());
		}

	}

	public void onReceiveDisconnect(String Username) {
		if (messageHistoryPanel != null) {
			messageHistoryPanel.append(Username);
			messageHistoryPanel.append(System.lineSeparator());
		}
	}

	private javax.swing.JTextArea activeClientsPanel;
	private javax.swing.JButton connect;
	private javax.swing.JButton sendButton;
	private javax.swing.JTextArea messageHistoryPanel;
	private javax.swing.JTextField host;
	private javax.swing.JScrollPane activeUsers;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JScrollPane jScrollPane6;
	private javax.swing.JTextArea messageArea;
	private javax.swing.JTextField portField;
	private javax.swing.JButton disconnectButton;
	private javax.swing.JTextField userNameField;

}
