import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.*;

/**
* OS(H) Assesed Exercise 1
* Nikolay Ivanov 2115451i
*********************************************************************
* Implements three different process scheduling algorithms:
* First Come First Serve,
* Shortest Job First (non-preemptive),
* Round Robin
*/

public class Algs{

	/**
	* public static void method roundRobin(String arg1, int arg2)
	* Implements the Round Robin scheduling algorithm
	* Outputs scheduled processes formatted like: PID: %d, WT: %d, TA: %d\n
	*/
	public static void roundRobin(ArrayList<String[]> processes, int quantum){
		Queue<String[]> Q1 = new LinkedList<String[]>(); // our Queue for arrived processes
		LinkedList<String[]> Q0 = new LinkedList<String[]>();  // our Queue for processes that are ahead of the current time
		// I am using a linkedList in this case so I can add at the front of the Queue

		int rt[] = new int[processes.size()]; // an array that holds the remaining times for each process
		int time = 0; // current time
		int totalWaitingTime = 0; 
		int totalTurnaroundTime = 0; 
		int i = 0;
		for(String[] p: processes){
			rt[i] = Integer.parseInt(p[1]); // append the remaining times to the array(remaining time is the CBT at start)
			i++;
			Q0.add(p); 
		}

		Q1.add(Q0.remove()); // add the 1st process to our Queue

		String[] current; 
		String[] next; 
		boolean flag = true;

		while(!Q1.isEmpty()){
			current = Q1.remove(); // current process taken from the Queue

			// if the remaining time of the current process is greater than the quantum
			if(rt[Integer.parseInt(current[0])] > quantum && rt[Integer.parseInt(current[0])] > 0){
				time += quantum;
				// the current time has changed so we look for all processes that have arrive during that time and add them to the Queue
				while(true){
					if(!Q0.isEmpty()){
						next = Q0.removeFirst();
						if(Integer.parseInt(next[2]) <= time){ // if the AAT is less than the current time append to Q1
							Q1.add(next);
						} else {
							Q0.addFirst(next); // else we put it back to ahead of time Q at position 0 and maintain the Sorted by AAT property
							break;
						}
					} else break;
				}
				rt[Integer.parseInt(current[0])] -= quantum; // adjust the remaining time of the current process
				Q1.add(current); // put the process back in the Queue
			} 
			// otherwise the remaining time is lesser or equal to the quantum meaning that the process is finished
			else if(rt[Integer.parseInt(current[0])] <= quantum && rt[Integer.parseInt(current[0])]>0){
				time += rt[Integer.parseInt(current[0])]; // adjust the time with the remaining time of the current process
				rt[Integer.parseInt(current[0])] = 0; 

				// again the current time has changed so we check for newly come processes and add them to the back of the Queue
				while(true){
					if(!Q0.isEmpty()){
						next = Q0.removeFirst();
						if(Integer.parseInt(next[2]) <= time){ // if the AAT is less than the current time append to Q1
							Q1.add(next);
						} else {
							Q0.addFirst(next); //else we put it back to ahead of time Q at position 0 and maintain the Sorted by AAT property
							break;
						}
					} else break;
				}

				// output the finished process's data and adjust the total waiting and total turnaround times
				System.out.format("PID: %s, WT: %d, TA: %d\n", Integer.parseInt(current[0]), time - Integer.parseInt(current[1]) - Integer.parseInt(current[2]), time - Integer.parseInt(current[2]));
				totalWaitingTime += time-Integer.parseInt(current[1])-Integer.parseInt(current[2]);
				totalTurnaroundTime += time-Integer.parseInt(current[2]);
			}		
		}
		// finally output the averages
		System.out.format("AWT: %.2f, ATT: %.2f\n", (double)totalWaitingTime/(double)processes.size(), (double)totalTurnaroundTime/(double)processes.size());
	}


	/**
	* public static void method sjfAlg(String arg1)
	* Implements the Shortest Job First (non-preemptive) scheduling algorithm
	* Outputs scheduled processes formatted like: PID: %d, WT: %d, TA: %d\n
	*/
	public static void sjfAlg(ArrayList<String[]> processes){
		
		String[] currentProcess, currentProcess2, tmp;
		int time = 0, sum = 0, sum2 = 0; 
		int min;
		int k = 1;
		int[] wt = new int[processes.size()]; // array where we store the waiting time of each process
		int[] ta = new int[processes.size()]; // array where we store the turnaround time of each process

		// sort the array based on fastest CBT but also paying attention the the AAT
		for(int j=0; j < processes.size(); j++){
			if(k < processes.size()){
				currentProcess = processes.get(j);
				time += Integer.parseInt(currentProcess[1]);
				min = Integer.parseInt(processes.get(k)[1]);
				for(int i = k; i < processes.size(); i++){
					currentProcess2 = processes.get(i);
					if(time >= Integer.parseInt(currentProcess2[2]) && Integer.parseInt(currentProcess2[1]) < min){
						min = Integer.parseInt(currentProcess2[1]);
						tmp = processes.get(k);
						processes.set(k, currentProcess2);
						processes.set(i, tmp);
					}
				}
			}
			k++;
		}

		wt[0] = 0; // 1st one we know has 0 waiting time

		// since the for loop starts from 1 we want to calc and print 
		// the data for the 0 elements

		sum2 = Integer.parseInt(processes.get(0)[1]);
		ta[0] = sum2 - Integer.parseInt(processes.get(0)[2]);
		System.out.format("PID: %s, WT: %d, TA: %d\n", processes.get(0)[0], wt[0], ta[0]);
		for (int i = 1; i < processes.size(); i++){	
			//calculate the waiting time for each process
			sum += Integer.parseInt(processes.get(i-1)[1]);
			wt[i] = sum - Integer.parseInt(processes.get(i)[2]);

			//calculate the turnaround time for each process
			sum2 += Integer.parseInt(processes.get(i)[1]);
			ta[i] = sum2 - Integer.parseInt(processes.get(i)[2]);

			//print the info
			System.out.format("PID: %s, WT: %d, TA: %d\n", processes.get(i)[0], wt[i], ta[i]);
		}

		// finally print the averages rounded UP
		System.out.format("AWT: %.2f, ATT: %.2f\n", (double)(IntStream.of(wt).sum()/(double)processes.size()), 
        										(double)(IntStream.of(ta).sum()/(double)processes.size()));
	}


	/**
	* public static void method fcfsAlg(String arg1)
	* Implements the First Come First Serve scheduling algorithm
	* Outputs scheduled processes formatted like: PID: %d, WT: %d, TA: %d\n
	*/
	public static void fcfsAlg(ArrayList<String[]> processes){

		

		String[] currentProcess = processes.get(0);

        int time = 0; // time passed 
    	int [] wt = new int[processes.size()]; // array of waiting time for each process
    	int [] ta = new int[processes.size()]; // array of turnaround time for each process
    	
    	wt[0] = time;
    	ta[0] = Integer.parseInt(currentProcess[1]);
    	time += Integer.parseInt(currentProcess[1]);

    		//turnaround = wt.current + CBT.current
			// wt = CBT.previous - AAT.current
    	System.out.format("PID: %s, WT: %d, TA: %d\n", currentProcess[0], wt[0], ta[0]);
        for(int i = 1; i < processes.size(); i++){
        	currentProcess = processes.get(i);
    		wt[i] = time - Integer.parseInt(currentProcess[2]);
    		time += Integer.parseInt(currentProcess[1]);
			ta[i] = wt[i] + Integer.parseInt(currentProcess[1]);

			System.out.format("PID: %s, WT: %d, TA: %d\n", currentProcess[0], wt[i], ta[i]);
        }
        System.out.format("AWT: %.2f, ATT: %.2f\n", (double)(IntStream.of(wt).sum()/(double)processes.size()), 
        										(double)(IntStream.of(ta).sum()/(double)processes.size()));
        
	}


	/**
	* public static ArrayList<String[]> method fileHandle(String arg1)
	* Takes a csv filename as input, reads and parses the data,
	* finally returns an ArrayList of String[] with all the processes
	* in the format [[PID, CBT, AAT], [PID, CBT, AAT], ...]
	*/
	public static ArrayList<String[]> fileHandle(String filename){
		String csvFile = filename;
       	String line = "";
        String csvSplitBy = ",";
        ArrayList<String[]> processes = new ArrayList<String[]>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] process = line.split(csvSplitBy);
            	processes.add(process);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // make sure that the list is sorted by AAT
    	Collections.sort(processes,new Comparator<String[]>() {
            public int compare(String[] strings, String[] otherStrings) {
            	Integer first = Integer.parseInt(strings[2]);
            	Integer second = Integer.parseInt(otherStrings[2]);
                return first.compareTo(second);
            }
        });

        return processes;
	}


	/**
	* public static void method main(String[] arg1)
	* The main method of the Class Algs()
	* Calls and executes the given scheduling algorithms
	*/
	public static void main(String[] args){

		System.out.println("FCFS W1");
		fcfsAlg(fileHandle("W1.csv"));
		System.out.println("FCFS W2");
		fcfsAlg(fileHandle("W2.csv"));
		System.out.println("SJF W1");
		sjfAlg(fileHandle("W1.csv"));
		System.out.println("SJF W2");
		sjfAlg(fileHandle("W2.csv"));
		System.out.println("RR W1 Q=15");
		roundRobin(fileHandle("W1.csv"), 15);
		System.out.println("RR W2 Q=15");
		roundRobin(fileHandle("W2.csv"), 15);
		System.out.println("RR W1 Q=5");
		roundRobin(fileHandle("W1.csv"), 5);
		System.out.println("RR W2 Q=5");
		roundRobin(fileHandle("W2.csv"), 5);
		System.out.println("RR W1 Q=40");
		roundRobin(fileHandle("W1.csv"), 40);
		System.out.println("RR W2 Q=40");
		roundRobin(fileHandle("W2.csv"), 40);
	}

	//END OF PROGRAM
}
