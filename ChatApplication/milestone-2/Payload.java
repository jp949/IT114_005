import java.io.Serializable;

public class Payload implements Serializable {
	private static final long serialVersionUID = 1L;
	private String message;
	private PayloadType payloadType;

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

	@Override
	public String toString() {
		return "Payload [message=" + message + ", payloadType=" + payloadType + "]";
	}
}
