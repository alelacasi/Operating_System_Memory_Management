import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


/*
	Still need to implement Write to File
	Still need to implement proper display of output
	Still need to improve the array comparison
	Currently only done as RR based on coming in and does not do priority
*/
public class Coen346Assignment3 {
	static Semaphore AllowRobin1=new Semaphore(1);
	static Semaphore AllowQ1 = new Semaphore(1);
	static Semaphore AllowRobin2=new Semaphore(1);
	static Semaphore AllowQ2 = new Semaphore(1);
	static LinkedList<Process> readyQueue1 = new LinkedList<>();	//Queue with whole thread info,  Arrive time, Run time, Wait time, Completion time
	static LinkedList<Process> readyQueue2 = new LinkedList<>();	//First and second processors
	static Semaphore sharedVar = new Semaphore(1);
	static LinkedList<Process> waitQ = new LinkedList<>();
	// critical time of system in miliseconds
	static AtomicInteger time = new AtomicInteger(0);
	//number of miliseconds in seconds used for conversion
	static int milisec = 1;

	
	public static void main(String[] args) {
		int length = 0;

		try {
			FileReader fr =  new FileReader ("Input.txt");
			BufferedReader br = new BufferedReader(fr);
			
			while((br.readLine()) !=null) {
				length++;
			}			
				br.close();
				
			}catch (IOException e) {
				out.println("File not found");
			}
		
		int[] arr_execut = new int[length];
		int[] arr_arrive = new int[length];
			
		try {
			FileReader fr =  new FileReader ("Input.txt");
			BufferedReader br = new BufferedReader(fr);
			String str;
			int count=0;
			out.println("Input values: ");
			while((str = br.readLine()) !=null) {
				String tokens[]=str.split("\\s+");
				/*for(int i=0;i<tokens.length;i++) {
					array[count]=Integer.parseInt(tokens[i]);
					count++;
				}*/
				arr_arrive[count]=Integer.parseInt(tokens[0]);
				arr_execut[count]=Integer.parseInt(tokens[1]);
				
				//s+=str+"\n";
				out.println(str);
				count++;
			}
			
			br.close();
			}catch (IOException e) {
				out.println("File not found");
			}
			
			
			//Check values of Arrive and Execute
			out.println("Array: ");
		
			for(int i=0; i<arr_arrive.length;i++) {
				out.println(arr_arrive[i] +" "+arr_execut[i]);
			}out.print("\n");
		
			//Make processes in the and add them to the queue
	    	Process [] p = new Process[length];
		    for(int i=0; i<length; i++) {
		    	p[i]=  new Process(i+1,arr_arrive[i],arr_execut[i]);
		    	waitQ.add(p[i]);
		    	
		    }out.println("\n");
		    
		    double smallest = waitQ.get(0).getArrivalTime();
		    for(int i = 0;i<waitQ.size();i++) {
		    	if(waitQ.get(i).getArrivalTime()<smallest) {
		    		smallest = waitQ.get(i).getArrivalTime();
		    	}
		    }
		    out.println("Time: "+ time.get()/milisec);
		    while(time.get()/milisec<smallest) {
		    	time.addAndGet(milisec);
		    	out.println("Time: "+ time.get()/milisec);
		    }
		    for(int i=0;i<waitQ.size();i++) {
		    	if(i%2==0) {
			    	if(waitQ.get(i).getArrivalTime()<=time.get()) {
			    		readyQueue2.add(waitQ.get(i));
			    		printQ2();
			    	}
		    	}else {
			    	if(waitQ.get(i).getArrivalTime()<=time.get()) {
			    		readyQueue1.add(waitQ.get(i));
			    		printQ1();
			    	}
		    	}
		    	
		    }/*
		    
		    //Thread declaration for multiple threads
		   Thread [] t= new Thread [waitQ.size()];
		   for(int i=0; i<waitQ.size(); i++) {
			   if(i%2==0) {
				   t[i] = new Thread(new MyThread2(waitQ.get(i)));
			   }else {
		    	t[i] = new Thread(new MyThread1(waitQ.get(i)));
		    	}
		    }

		    
		    //Thread start for all the threads
		    for(int i=0; i<waitQ.size(); i++) {
		    	t[i].start();
		    }

		    
		  //Join all threads
		    try {
			    for(int i=0; i<waitQ.size(); i++) {
			    	t[i].join();
			    }

			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		    		   
	}
	
	
	
	public static class MyThread1 implements Runnable{
		
		int process;
		double arrivalTime;
		double runTime;
		double quantum = 0; //arbitrary thresh hold to avoid starvation
		int count = 0;

		public MyThread1(Process p) {

			this.process = p.getProcessNumber();
			this.runTime = p.getExecuteTime();
			this.arrivalTime = p.getArrivalTime();

		}
		public void run() {
			while(runTime!=0) {
				//Check execution queue every time
				try {
					AllowQ1.acquire();
					checkArrival1();
					if(!readyQueue1.isEmpty()) {
					    double largest = readyQueue1.getFirst().getExecuteTime();
					    for(int i = 1;i<readyQueue1.size();i++) {
					    	if(readyQueue1.get(i).getExecuteTime()>largest) {
					    		largest = readyQueue1.get(i).getExecuteTime();
					    	}
					    }
					    quantum = largest*0.1;
						}
				}catch (InterruptedException e) {e.printStackTrace();}finally{
					AllowQ1.release();
				}
				
				if(!readyQueue1.isEmpty()) {
				if(readyQueue1.getFirst().getProcessNumber()==process) {
					try {				
						//Round Robin logic
						AllowRobin1.acquire();
						count++;
						if(count==1) {
							out.println("Time "+time.get()/milisec+", Process "+process+", Started");
						}
						
						out.println("Time "+time.get()/milisec+", Process "+process+", Resumed");
						//readyQueue.getFirst().setWait();
						int q=3;
						//check for starvation 

						//do priority on Size
						if (runTime > 0.1) { 
						//readyQueue.getFirst().printProcess();
							  
							time.addAndGet(q);  
						    runTime = runTime - q; 
						      
						
						}else {
							time.addAndGet((int)runTime*milisec); 
							runTime = 0; 
							out.println("Time "+time.get()/milisec+", Process "+process+", Finished");
						}
						readyQueue1.getFirst().setProcess(process, arrivalTime, runTime);
						if(q>0) { 
							checkQ1();
						}
						
						if(runTime == 0) {
							checkQ1();
							out.println("Time "+time.get()/milisec+ ", Process "+ process+" is Done.");
							readyQueue1.removeFirst();
						}else if(runTime>0.1) {
							out.println("Time "+time.get()/milisec+", Process "+process+", Paused");
						}
					}catch (InterruptedException e) {e.printStackTrace();}finally {
						// calling release() after a successful acquire()
						AllowRobin1.release();
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {e.printStackTrace();}
				
			}else{
				time.addAndGet(milisec);
				out.println("Time "+time.get()/milisec);
			}
			}
			
		}
	}

	
	public static class MyThread2 implements Runnable{
		
		int process;
		double arrivalTime;
		double runTime;
		double quantum = 0; //arbitrary thresh hold to avoid starvation
		int count = 0;

		public MyThread2(Process p) {

			this.process = p.getProcessNumber();
			this.runTime = p.getExecuteTime();
			this.arrivalTime = p.getArrivalTime();

		}
		public void run() {
			while(runTime!=0) {
				//Check execution queue every time
				try {
					AllowQ2.acquire();
					checkArrival2();
					if(!readyQueue2.isEmpty()) {
					    double largest = readyQueue2.getFirst().getExecuteTime();
					    for(int i = 1;i<readyQueue2.size();i++) {
					    	if(readyQueue2.get(i).getExecuteTime()>largest) {
					    		largest = readyQueue2.get(i).getExecuteTime();
					    	}
					    }
					    quantum = largest*0.1;
						}
				}catch (InterruptedException e) {e.printStackTrace();}finally{
					AllowQ2.release();
				}
				
				if(!readyQueue2.isEmpty()) {
				if(readyQueue2.getFirst().getProcessNumber()==process) {
					try {				
						//Round Robin logic
						AllowRobin2.acquire();
						count++;
						if(count==1) {
							out.println("Time "+time.get()/milisec+", Process "+process+", Started");
						}
						
						out.println("Time "+time.get()/milisec+", Process "+process+", Resumed");
						//readyQueue.getFirst().setWait();
						int q = 3;
						//check for starvation 
						//do priority on Size
					    if (runTime > 0.1) { 
					    	//readyQueue.getFirst().printProcess();
					    	  
					    	time.addAndGet(q); 
					        runTime = runTime - q; 
					          

					      }else {  
					    	// for last time 
					    	time.addAndGet((int)runTime*milisec); 
					        runTime = 0; 
					        out.println("Time "+time.get()/milisec+", Process "+process+", Finished");
					      }
					      readyQueue2.getFirst().setProcess(process, arrivalTime, runTime);
						  if(q>0) {
					      checkQ2();
					      }
	

						
						if(runTime == 0) {
							checkQ2();
							out.println("Time "+time.get()/milisec+ ", Process "+ process+" is Done.");
							readyQueue2.removeFirst();
						}else if(runTime>0.1) {
							out.println("Time "+time.get()/milisec+", Process "+process+", Paused");
						}
					}catch (InterruptedException e) {e.printStackTrace();}finally {
						// calling release() after a successful acquire()
						AllowRobin2.release();
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {e.printStackTrace();}
				
			}else{
				time.addAndGet(milisec);
				out.println("Time "+time.get()/milisec);
			}
			}
			
		}
	}
	
	//Checks if the process has arrived and creates a thread for it.
	public static void checkArrival1() {
		Thread  t = new Thread();
		for(int i=0;i<waitQ.size();i++) {
			if(waitQ.get(i).getArrivalTime()<=time.get()/milisec) {
				//if the value is empty don't add
				if(waitQ.get(i).getExecuteTime()==0) {
					//do nothing
				}
				//If priority does not contain value, add to queue
				else if(!readyQueue1.contains(waitQ.get(i))) {
					readyQueue1.addFirst(waitQ.get(i));	
					t = new Thread(new MyThread1(readyQueue1.getFirst()));
				}
			}
		}

	}
	
	public static void checkArrival2() {
		Thread  t = new Thread();
		for(int i=0;i<waitQ.size();i++) {
			if(waitQ.get(i).getArrivalTime()<=time.get()/milisec) {
				//if the value is empty don't add
				if(waitQ.get(i).getExecuteTime()==0) {
					//do nothing
				}
				//If priority does not contain value, add to queue
				else if(!readyQueue2.contains(waitQ.get(i))) {
					readyQueue2.addFirst(waitQ.get(i));	
					t = new Thread(new MyThread2(readyQueue2.getFirst()));
				}
			}
		}

	}
	
	//Algorithm that puts the values in order, currently inefficient Needs improvement
	public static void checkQ1() {
		for(int i=1;i<readyQueue1.size();i++) {
			//If time for 1>2, switch
			if(readyQueue1.get(i-1).getExecuteTime()>readyQueue1.get(i).getExecuteTime()) {
				Process holder = readyQueue1.get(i);
				readyQueue1.remove(i);
				for(int j=0;j<readyQueue1.size();j++) {
					if(!readyQueue1.contains(holder)) {
						if(holder.getExecuteTime()<readyQueue1.get(j).getExecuteTime()) {
							readyQueue1.add(j, holder);
							break;
						}
					}
				}
			}
		}
	}
	
	public static void checkQ2() {
		for(int i=1;i<readyQueue2.size();i++) {
			//If time for 1>2, switch
			if(readyQueue2.get(i-1).getExecuteTime()>readyQueue2.get(i).getExecuteTime()) {
				Process holder = readyQueue2.get(i);
				readyQueue2.remove(i);
				for(int j=0;j<readyQueue2.size();j++) {
					if(!readyQueue2.contains(holder)) {
						if(holder.getExecuteTime()<readyQueue2.get(j).getExecuteTime()) {
							readyQueue2.add(j, holder);
							break;
						}
					}
				}
			}
		}
	}
	//print queue, good visual check
	public static void printQ1() {
		for (int i=0;i<readyQueue1.size();i++) {
			out.print(readyQueue1.get(i).getProcessNumber());
			if(i+1!=readyQueue1.size()) {
				out.print(" -> ");
			}
		}out.println();
	} 
	public static void printQ2() {
		for (int i=0;i<readyQueue2.size();i++) {
			out.print(readyQueue2.get(i).getProcessNumber());
			if(i+1!=readyQueue2.size()) {
				out.print(" -> ");
			}
		}out.println();
	} 
	}
