/**
 * @author swapneelsheth, aaron
 * Queue Processor which processes the queue
 */

package middleware;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Iterator;

import source.Job;

public class QueueProcessor extends Thread {
	Hashtable<String, Job> queue;
	PolicyManager pm;

	/**
	 * Constructor
	 * @param queue job queue
	 * @param pm the policy manager
	 */
	public QueueProcessor(Hashtable<String, Job> queue, PolicyManager pm) {
		this.queue = queue;
		this.pm = pm;

		// Move the loadfile part to a more suitable section.
		pm.loadFile();
	}

	/**
	 * Logic to run the blast jobs on NIH or GAiA
	 */
	public void run() {
		
		while (true) {
			
			synchronized(queue) {
				int notifyCounter = QueueManager.getNotifyCounter();
				if (notifyCounter <= 0) {		// We don't have any queued notifies - so wait on the monitor.
					try {
						queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (notifyCounter > 0) { 	// Decrement counter
					QueueManager.decrementNotifyCounter();
				}
				if (queue.size() > 0) {

					Iterator queueItr = queue.keySet().iterator();
					
					while (queueItr.hasNext()) {
						/**
						 * 1. Go through the queue.
						 * 2. If the job has been blasted, send it back to client.
						 * 3. Else, if job is new, then blast it and update status to blasting.
						 * 4. 
						 */
						
						String key = (String)queueItr.next();
						
						Job j = (Job)queue.get(key);
						switch(j.getStatus()){
						case Job.NEW:
							//decide where to send. Send it. Update status to Blasting

							String serverId = pm.getServer();
							//System.out.println(serverId);


							System.out.println("Queue processor: sending job to server: " + serverId);
							j.setStatus(source.Job.BLASTING);							
							services.ParamsObject params = services.ParamsFetch.parms_lookup(serverId);
							Hashtable<String, String> reqdParams = params.getAllRequiredParms();
							Hashtable<String, String> optParams = params.getAllOptionalParms();
							
							String className = reqdParams.get("classname");
							
							try {
									Class theClass = Class.forName("source." + className);
									Constructor[] cons = theClass.getConstructors();
									if (cons.length != 0) {
										((source.Invoker)cons[0].newInstance(j, reqdParams, optParams)).start();
									}
									
								} catch (Exception e) {
									e.printStackTrace();
								}
							

							break;

						case Job.BLASTING:
							// as of now, wait .. Do we need timeout here too?
							break;
						case Job.BLASTED:
							// nothing. Wait to be pinged to return value
							break;
						case Job.FAILED:
							break;
						}
					}
				}
			}
		}
	}
}
