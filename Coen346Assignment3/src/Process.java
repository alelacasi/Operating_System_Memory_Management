
public class Process {
	
	private int processNum;
	private double arrTime;
	private double executTime;
	private int execCount;
	private int waitTime;
	
	//constructor
	Process(int p, double arrT, double execT) {
		this.processNum = p;
		this.arrTime = arrT;
		this.executTime = execT;
	}
	
	public void printProcess() {
		System.out.println("Process number #"+ this.processNum +" containt an arrival time of "
				+ this.processNum + "secs, and executes for "+ this.executTime+" secs");
	}
	
	
	public void setProcess(int p, double arrT, double execT) {
		this.processNum = p;
		this.arrTime = arrT;
		this.executTime = execT;
	}
	public void setProcess(int p, double arrT, double execT, int e) {
		this.processNum = p;
		this.arrTime = arrT;
		this.executTime = execT;
		this.execCount = e;
	}
	
	public void setWait(int w) {
		this.waitTime = w;
	}
	
	public int getWait() {
		return this.waitTime;
	}
	
	public void setCount(int e) {
		this.execCount = e;
	}
	public int getExecCount() {
		return this.execCount;
	}
	public int getProcessNumber() {
		return this.processNum;
	}
	
	public double getArrivalTime() {
		return this.arrTime;
	}
	
	public double getExecuteTime() {
		return this.executTime;
	}

}
