/**
 * 
 */
package client;

/**
 * Class that submits jobs to the RMI server.
 * @author aaronfernandes
 *
 */
public class SubmitJob {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i=1;i<=5;i++) {	// submit individual jobs.
			new TestRMIClient("fasta" + i + ".f").start();
		}
		
		// done submitting jobs.
	}

}
