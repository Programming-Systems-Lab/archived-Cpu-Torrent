/**
 * @author swapneelsheth, aaron
 * Queue Manager class which manages the Queuing
 */

package middleware;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;

import source.Job;

public class QueueManager {
	PolicyManager pm;
	QueueProcessor qp;
	static Hashtable<String, Job> queue;
	private static byte MAX_RETRIES = 5;
	private static int notifyCounter;

	/**
	 * Constructor 
	 */
	public QueueManager() {
		queue = new Hashtable<String, Job>();
		pm = new PolicyManager("middleware/Simple.conf");
		qp = new QueueProcessor(getQueue(), pm);
		qp.start();
	}

	/**
	 * @return the queue
	 */
	public Hashtable<String,Job> getQueue() {
		return queue;
	}

	/**
	 * Adds a job to the queue
	 * @param fastaSequence the sequence
	 * @return the id of the job in the queue
	 */
	String addJob(byte[] fastaSequence) {
		String fastaString = new String(fastaSequence);
		String jid;
		/**
		 * Create a new job and add it to the queue.
		 */

		synchronized(queue) {
			int id = queue.size();
			jid = new Integer(id).toString();
			Job j = new Job(jid, fastaString);
			queue.put(jid,j);
			System.out.println("Q Manager: Job " + jid + " added");
			System.out.println("Q Manager: size of queue:" + queue.size());
			//queue.notify();
			notifyQProcessor();
		}

		return(jid);
	}

	/**
	 * Gets the results of a blast operation
	 * @param jid the job to be queried for
	 * @return The results of the blast operation
	 */
	byte[] getResult(String jid) {		
		if (jid == null) {
			return(new String("Illegal Job").getBytes());
		}
		Job j;
		String resultFileName = null;
		synchronized(queue) {

			j = (Job)queue.get(jid);
			if (j==null) {
				return(new String("Job not found").getBytes());
			}
			else if (j.getStatus() == source.Job.BLASTED) {
				j = (Job)queue.remove(jid);
				resultFileName = jid + ".blastpgp";
			} else if (j.getStatus() == source.Job.FAILED) {
				queue.remove(jid);
				return (new String("Failed").getBytes());
			} else return (new String("Processing").getBytes());
		}


		// read results from the file and return them.
		if (resultFileName != null) {
			StringBuffer sb = new StringBuffer();
			String temp;

			try {
				FileReader fr = new FileReader(resultFileName);
				BufferedReader br = new BufferedReader(fr);

				while((temp = br.readLine()) != null) {
					sb.append(temp + "\n");
				}
				//	 Deleting file with the results as the user has already read them.
				br.close();
				fr.close();
				java.io.File f = new java.io.File(resultFileName);
				if (f.exists()) {
					f.delete();
				}
			} catch(java.io.IOException ie) {
				ie.printStackTrace();
			}
			return(sb.toString().getBytes());
		}
		else {
			return(new String("Error").getBytes());
		}

	}

	/**
	 * Get the job status
	 * @param jid the job whose status is to be returned
	 * @return the status code
	 */
	int getStatus(String jid) {
		Job j;

		synchronized(queue) {
			j = queue.get(jid);
		}
		return(j.getStatus());
	}
	
	/**
	 * Notifies the queuemanager of a job failure
	 * @param j the failed job
	 */

	public static void notifyJobFailure(Job j) {
		int retries = (int) j.getNumRetries();
		retries++;
		
		//No. of Retries have exceeded - dont try to execute the job anymore. Set status to failed.
		if (retries >= MAX_RETRIES) {
			// Set job status to failed.
			
			j.setStatus(source.Job.FAILED);
		} else {
			j.setNumRetries((byte) retries);
			j.setStatus(Job.NEW);
			synchronized (queue) {
				notifyQProcessor();
			}
		}
	}
	
	/**
	 * Notifies the queue processor to process jobs in the queue
	 *
	 */
	public static void notifyQProcessor() {
		notifyCounter++;
		synchronized(queue) {
			queue.notify();
		}
	}
	
	/**
	 * Decrements the notify counter.
	 *
	 */
	
	public static void decrementNotifyCounter() {
		notifyCounter--;
	}

	
	public static int getNotifyCounter() {
		return notifyCounter;
	}
}
