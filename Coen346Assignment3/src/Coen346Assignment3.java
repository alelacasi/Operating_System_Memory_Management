import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
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
	static LinkedList<Process> waitQ1 = new LinkedList<>();
	static LinkedList<Process> waitQ2 = new LinkedList<>();
	// critical time of system
	static AtomicInteger time = new AtomicInteger(0);
	
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	
	
	//System.currentTimeMillis();
	//number of miliseconds in seconds used for conversion
	int mili = 1000;

	
	public static void main(String[] args) {
		int length = 0;

		try {
			FileReader fr =  new FileReader ("processes.txt");
			BufferedReader br = new BufferedReader(fr);
			length= Integer.parseInt(br.readLine());			
				br.close();
				
			}catch (IOException e) {
				out.println("File not found");
			}
		out.println("Length: "+length);
		
		int[] arr_execut = new int[length];
		int[] arr_arrive = new int[length];
			
		try {
			FileReader fr =  new FileReader ("processes.txt");
			BufferedReader br = new BufferedReader(fr);
			String str;
			int count=0;
			out.println("Input values: ");
			br.readLine();
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
		
			for(int i=0; i<length;i++) {
				out.println(arr_arrive[i] +" "+arr_execut[i]);
			}out.print("\n");
		
			//Make processes in the and add them to the queue
	    	Process [] p = new Process[length];
		    for(int i=0; i<length; i++) {
		    	p[i]=  new Process(i+1,arr_arrive[i],arr_execut[i]);
		    	
		    }out.println("\n");
		    
		    double smallest = p[0].getArrivalTime();
		    for(int i = 0;i<length;i++) {
		    	if(p[i].getArrivalTime()<smallest) {
		    		smallest = p[i].getArrivalTime();
		    	}
		    }
		    out.println("Time: "+ time.get());
		    while(time.get()<smallest) {
		    	time.addAndGet(1);
		    	out.println("Time: "+ time.get());
		    }
		    
		    int[] match = new int[length];
		    //initializing match to -1 for all values. No matches unless value changed
		    for(int i=0;i<length;i++) {
		    	match[i] = -1;
		    }
		    
		    for(int i=0;i<length-1;i++) {
		    	for(int j=i+1;j<length;j++) {
		    		if(p[i].getArrivalTime()==p[j].getArrivalTime()) {
		    			//value changed when matched
		    			match[i] = j;
		    			out.println("TRUE at "+i+" and "+match[i]);

		    		}
		    	}
		    }

		    for(int i=0;i<length;i++) {
		    	if(match[i]>0) {
		    		out.println("Q2");
		    		waitQ2.add(p[i]);
		    		p[i].printProcess();
		    	}else {
		    		out.println("Q1");
		    		waitQ1.add(p[i]);
		    		p[i].printProcess();
		    	}
		    }
		    
		    //Thread declaration for multiple threads
		   Thread [] t= new Thread [length];
		   
		   int count1 = 0;
		   int count2 = 0;

		   for(int i=0;i<length;i++) {
			   if(match[i]>0) {
			   		t[i] = new Thread(new MyThread2(waitQ2.get(count2)));
			   		count2++;
			   
		   		}else { 
		   			t[i] = new Thread(new MyThread1(waitQ1.get(count1)));
		   			count1++;
		   		}
		   }
		    
		    //Thread start for all the threads
		    for(int i=0; i<length; i++) {
		    	t[i].start();
		    }

		    
		  //Join all threads
		    try {
			    for(int i=0; i<length; i++) {
			    	t[i].join();
			    }

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    		   
	}
	
	
	
	public static class MyThread1 implements Runnable{
		
		int process;
		double arrivalTime;
		double runTime;
		int count = 0;
		double Ttime = 0;	// local thread time that increments within each thread

		public MyThread1(Process p) {

			this.process = p.getProcessNumber();
			this.runTime = p.getExecuteTime();
			this.arrivalTime = p.getArrivalTime();

		}
		public void run() {
			int c =0;
			while(runTime!=0) {
				
				//Check execution queue every time
				try {
					AllowQ1.acquire();
					checkArrival1();
				}catch (InterruptedException e) {e.printStackTrace();}finally{
					AllowQ1.release();
				}
				
				if(!readyQueue1.isEmpty()) {
					if(readyQueue1.getFirst().getProcessNumber()==process) {
						try {				
							//Round Robin logic
							AllowRobin1.acquire();
							
							if((int)(Ttime*10)%10==0) {
								if(c==0) {
									Ttime=time.get();
									c++;
								}else {
									time.set((int)Ttime);
								}	
							}
							
							count++;
							if(count==1) {
								out.println("Time "+df2.format(Ttime)+", Process "+process+", Started");
							}
							out.println("Time "+df2.format(Ttime)+", Process "+process+", Resumed");
							double q = 0.1;
							
							if (runTime > q) {
								Ttime += q;  
							    runTime = runTime - q; 
							    
							}else {
								Ttime += runTime;  
								runTime = 0; 
								out.println("Time "+df2.format(Ttime)+", Process "+process+", Finished");
							}
							readyQueue1.getFirst().setProcess(process, arrivalTime, runTime);
							
							if(runTime == 0) {
								out.println("Time "+df2.format(Ttime)+ ", Process "+ process+" is Done.");
								if((int)(Ttime*10)%10==0) {
									time.set((int)Ttime);
									Ttime=time.get();
									c=0;
									}
								readyQueue1.removeFirst();
								
							}else if(runTime>0.1) {
								out.println("Time "+df2.format(Ttime)+", Process "+process+", Paused");
								if((int)(Ttime*10)%10==0) {
									time.set((int)Ttime);
									Ttime=time.get();
									c=0;
									}
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
				//time.addAndGet(1);
				//out.println("Time "+time.get());
			}
			}
			
		}
	}

	
	public static class MyThread2 implements Runnable{
		
		int process;
		double arrivalTime;
		double runTime;
		int count = 0;
		double Ttime = 0;

		public MyThread2(Process p) {

			this.process = p.getProcessNumber();
			this.runTime = p.getExecuteTime();
			this.arrivalTime = p.getArrivalTime();

		}
		public void run() {
			int c=0;
			while(runTime!=0) {
				//Check execution queue every time
				try {
					AllowQ2.acquire();
					checkArrival2();			
				}catch (InterruptedException e) {e.printStackTrace();}finally{
					AllowQ2.release();
				}
				
				if(!readyQueue2.isEmpty()) {
				if(readyQueue2.getFirst().getProcessNumber()==process) {
					try {				
						//Round Robin logic
						AllowRobin2.acquire();
						
						if((int)(Ttime*10)%10==0) {
							if(c==0) {
								Ttime=time.get();
								c++;
							}else {
								time.set((int)Ttime);
							}	
						}
						
						count++;
						if(count==1) {
							out.println("Time "+df2.format(Ttime)+", Process "+process+", Started");
						}
						
						out.println("Time "+df2.format(Ttime)+", Process "+process+", Resumed");
						double q = 0.1;
						//check for starvation 
						//do priority on Size
					    if (runTime > q) {					    	  
					    	Ttime+=q; 
					        runTime = runTime - q;

					      }else {  
					    	// for last time 
					    	Ttime+= runTime; 
					        runTime = 0; 
					        out.println("Time "+df2.format(Ttime)+", Process "+process+", Finished");
					      }
					      readyQueue2.getFirst().setProcess(process, arrivalTime, runTime);
	
						if(runTime == 0) {
							out.println("Time "+df2.format(Ttime)+ ", Process "+ process+" is Done.");
							if((int)(Ttime*10)%10==0) {
								time.set((int)Ttime);
								Ttime=time.get();
								c=0;
								}
							readyQueue2.removeFirst();
						}else if(runTime>0.1) {
							if((int)(Ttime*10)%10==0) {
								time.set((int)Ttime);
								Ttime=time.get();
								c=0;
								}
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
				//time.addAndGet(1);
				//out.println("Time "+time.get());
			}
			}
			
		}
	}
	
	//Checks if the process has arrived and creates a thread for it.
	public static void checkArrival1() {
		for(int i=0;i<waitQ1.size();i++) {
			if(waitQ1.get(i).getArrivalTime()<=time.get()) {
				//if the value is empty don't add
				if(waitQ1.get(i).getExecuteTime()==0) {
					//do nothing
				}
				//If priorityQ does not contain value, add to queue
				else if(!readyQueue1.contains(waitQ1.get(i))) {
					readyQueue1.addFirst(waitQ1.get(i));
				}
			}
		}

	}
	
	public static void checkArrival2() {
		for(int i=0;i<waitQ2.size();i++) {
			if(waitQ2.get(i).getArrivalTime()<=time.get()) {
				//if the value is empty don't add
				if(!(waitQ2.get(i).getExecuteTime()==0) && !readyQueue2.contains(waitQ2.get(i))) {
					readyQueue2.addFirst(waitQ2.get(i));
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
