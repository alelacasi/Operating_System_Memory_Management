
public class Memory {

	String id;
	int val;
	
	Memory(){
		this.id = null;
		this.val = -1;
	}
	
	Memory(String ID, int v){
		this.id = ID;
		this.val = v;
	}
	
	public String getId() {
		return this.id;
	}
	public void setId(String Id) {
		this.id = Id;
	}
	public int getValue() {
		return this.val;
	}
	public void setValue(int v) {
		this.val = v;
	}
}
