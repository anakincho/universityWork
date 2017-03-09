//Nikolay Ivanov 2115451i
//APH Excercise 2
//This is my own work as defined in the Academic Ethics agreement I have signed.

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
* public class fileCrawler
* This program works as a file crawler
* it takes a directory and a pattern and
* prints out all the files within that directory
* that match the pattern
*/
public class fileCrawler {
	private static AtomicInteger currThreadRun;
	private Thread workers[];

	/**
	* public static class Worker 
	* which implements Runnable
	* all the concurrent processing happens here
	*/
	public static class Worker implements Runnable{
		//declaration of class variable
		private String pattern;
		private LinkedBlockingDeque<String> workQ;
		private ConcurrentSkipListSet<String> filesList;
		

		// public constructor
		public Worker(String pattern, String firstDir) 
		{
			this.pattern  = pattern;
			this.filesList = new ConcurrentSkipListSet<String>();
			this.workQ = new LinkedBlockingDeque<String>();
			this.workQ.add(firstDir);
		}

		/**
		* public method ConcurrentSkipListSet<String> getFilesList()
		* Returns the current fileList
		*/
		public ConcurrentSkipListSet<String> getFilesList(){
			return filesList;
		}
		
		/**
		* public method void run()
		* Here we run the worker threads, and what happens is that
		* each thread gets a file from the workQ and then adds the files whom 
		* match the pattern to the filesList
		*/
		public void run(){
			String file = null; 
			while (true){ 				
				try {
					file = workQ.take();
					currThreadRun.incrementAndGet();
					if (file.equals("waiting")){
						workQ.add("waiting"); 
						break;
					}
					processDirectory(file);
					if (currThreadRun.decrementAndGet() == 0 && workQ.size()==0)
						workQ.add("waiting");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		* public method void processDirectory(String arg1)
		* Used from the sample file DirectoryTree.java
		* checks if a file matches the pattern if so
		* add to the fileList
		* if it is a directory, append the workQ
		*/		
		public void processDirectory(String fileName) {
		    try {
		        File file = new File(fileName);	
		        if (file.isDirectory()) {	
		            String entries[] = file.list(); 
		            if (entries != null) {
		                for (String entry : entries ) {
		                    if (entry.compareTo(".") == 0)
		                        continue;
		                    if (entry.compareTo("..") == 0)
		                        continue;
		                    File currentFile = new File(fileName+"/"+entry);
		                    if(!currentFile.isDirectory() && Regex.matchesPat(pattern,entry))
		                    	filesList.add(fileName+"/"+entry);
		                    workQ.add(fileName+"/"+entry);
		                }
		                
		            }
		        }
		    }
		    catch (Exception e) {
		        System.err.println("Error processing "+ fileName +": "+e);
		    }
		}
	}


	// public constructor
	public fileCrawler(int numOfThreads){
		this.currThreadRun = new AtomicInteger(0);
		this.workers = new Thread[numOfThreads];
	}


	/**
	* public method static void main(String arg1[]) 
	* Processes the arguments from the command line,
	* then creates a new fileCrawler and the main Worker
	* after that it starts the threads
	* after that it joins them 
	* finally when they finish it prints out the matching files
	*/
	public static void main(String args[] ){ 
		//default number of threads if env var CRAWLER_THREADS is not defined
		int numOfThreads = 2;
		String pattern = Regex.cvtPattern(args[0]); // process the given pattern

		// handle env var
		if ((System.getenv("CRAWLER_THREADS") != null))
			numOfThreads = Integer.parseInt(System.getenv("CRAWLER_THREADS"));

		//handle the directory
		String firstDir;
		if (args.length == 1)
			firstDir= "."; // if no directory is given
		else if(args.length == 2)
			firstDir = args[1];
		else 
			return; // if invalid arguments
		
		//create the fileCrawler
		fileCrawler fileCrawler = new fileCrawler(numOfThreads);
		
		//create the main thread
		Worker worker = new Worker(pattern, firstDir);

		//start the threads
		for (int i = 0;i<fileCrawler.workers.length;i++){
			fileCrawler.workers[i] = new Thread(worker);
			fileCrawler.workers[i].start();
		}


		//we wait for the workers to finish
		for (Thread w : fileCrawler.workers){
			try {
				w.join();
			} catch(InterruptedException e){}
		}
		
		//finally print out the matching files
		while (!worker.getFilesList().isEmpty())
			System.out.println(worker.getFilesList().pollFirst());
		
	}

}
