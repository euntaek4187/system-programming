package symbolTable;
public class Symbol {
	private String token;
	private String type;
	private String size;
	private String initialValue;
	private String offset;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
}
