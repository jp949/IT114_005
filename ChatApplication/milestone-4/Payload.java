import java.io.Serializable;

public class Payload implements Serializable {
	private static final long serialVersionUID = 1L;
	private String message;
	private String room = "Room1";
	private PayloadType payloadType;
	private int number;

	public void setMessage(String s) {
		this.message = s;
	}

	public String getMessage() {
		return this.message;
	}

	public void setPayloadType(PayloadType pt) {
		this.payloadType = pt;
	}

	public PayloadType getPayloadType() {
		return this.payloadType;
	}

	public void setNumber(int n) {
		this.number = n;
	}

	public int getNumber() {
		return this.number;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	@Override
	public String toString() {
		return "Payload [message=" + message + ", room=" + room + ", payloadType=" + payloadType + ", number=" + number
				+ "]";
	}
}
