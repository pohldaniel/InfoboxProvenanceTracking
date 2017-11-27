package io;

import parallel.*;
import rdf.ProvenanceManager;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLStreamException;

import org.apache.jena.atlas.logging.Log;

import dump.SingleArticle;


/**
 * The main class 
 */
public class Main {

	public static String timestamp;
	
	
	
	/**
	 * Main method for the project
	 *
	 * @param args parameter
	 */
	public static void main(String[] args) {
		  CLParser clParser = new CLParser(args);
		  clParser.validate();
		
		if(clParser.getDebug()) {
		  String timestamp;


		  
		  SingleArticle singelArticel = new SingleArticle(clParser);
		  singelArticel.setPathForArticle("United_States", "en","2017-04-08T20:11:11Z");
		  
		  
		  
		  ExecutorService executor = Executors.newFixedThreadPool(clParser.getThreads());
		  ProvenanceManager provenanceManager = singelArticel.createProvenanceManager();
		  Runnable worker = provenanceManager;
		  executor.execute(worker);
		  executor.shutdown();
		  try {
				while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {		 
				}

		  } catch (InterruptedException e) {
				Log.info(e, "AWAITING_COMPLETION_OF_THREADS");
		 }
		  
		  singelArticel.delete();
		  timestamp=provenanceManager.getDumpParser().getTimestampt();
		  System.out.println(timestamp);
		  
		  
		  
		  
		  
		  for(int i =0; i< 0; i++ ) {
		 
			  SingleArticle singelArticel2 = new SingleArticle(clParser);
			  singelArticel2.setPathForArticle("United_States", "en",timestamp);
			  
			  ExecutorService executor2 = Executors.newFixedThreadPool(clParser.getThreads());
			  ProvenanceManager provenanceManager2 = singelArticel2.createProvenanceManager();
			  Runnable worker2 = provenanceManager2;
			  executor2.execute(worker2);
			  executor2.shutdown();
			  try {
					while (!executor2.awaitTermination(1, TimeUnit.SECONDS)) {		 
					}

			  } catch (InterruptedException e) {
					Log.info(e, "AWAITING_COMPLETION_OF_THREADS");
			 }
			  singelArticel2.delete();
			  
			  timestamp=provenanceManager2.getDumpParser().getTimestampt();
			  System.out.println(timestamp);
		  }
		  
		}else {  
		
		  ArrayBlockingQueue<String> queue = new  ArrayBlockingQueue<String> (clParser.getThreadsF());
		  
		  Producer producer = new Producer(queue, clParser.getPath());
  		  producer.start();
  		  
  		for(int i= 0; i < clParser.getThreadsF(); i++)
		{
  			
  			new Consumer(queue,clParser, "File" + i).start();
		}
		}	
	}// end main

}// end class