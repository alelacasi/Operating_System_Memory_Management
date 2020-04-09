
public class Command {

	String command;
	String variableID;
	int value;
	
	Command(String cmd, String id, int val){
		this.command = cmd;
		this.variableID = id;
		this.value = val;
	}
	Command(String cmd, String id){
		this.command = cmd;
		this.variableID = id;
		this.value = -1;
	}
	
	public int getSize() {
		if(value==-1) {
			return 2;
		}
		return 3;
	}
	
	public String getCmd(){
		return this.command;
	}
	public void setCmd(String cmd) {
		this.command = cmd;
	}
	public String getId() {
		return this.variableID;
	}
	public void setId(String id) {
		this.variableID = id;
	}
	public int getValue() {
		return this.value;
	}
	public void setValue(int val) {
		this.value = val;
	}
	
	
}
