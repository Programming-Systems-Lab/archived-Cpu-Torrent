/**
 * Wrapper to invoke SGE blast on GAIA.
 */
package source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Hashtable;

import middleware.QueueManager;


public class InvokeSGEBlast extends Thread implements Invoker{
	Process pro;

	Job j;

	static final String blastResultExt = ".blastpgp";

	static final String blastPrefix = "SGE";

	static final long timeout = 2 * 60 * 1000;
        private Hashtable<String, String> requiredParams;
        private Hashtable<String, String> optionalParams;

	int timeoutCounter = 0;

	class killBlast extends TimerTask {
		public void run() {
			System.out.println("Checking SGEBlast");
			int status = checkResults();
			if (status == 1) {
				if (pro != null)
					pro.destroy();
				cleanUp();
			} else if (timeoutCounter > 8) {
				// Problems while reading output file. Set job as failed.
				System.out.println("Blast job timed out");
				// May have to trigger killing the job here
				//j.setStatus(Job.FAILED);
				QueueManager.notifyJobFailure(j);

				if (pro != null)
					pro.destroy();
				cleanUp();
			}
			timeoutCounter++;
		}
		
		/**
		 * Cleans up the temporary files once Cancel needs to be called
		 */
		void cleanUp(){
			String fastaName = blastPrefix + j.getJobId() ;
			java.io.File f = new java.io.File(fastaName + ".f");
			if (f.exists()) {		// Delete file on exiting.
				f.delete();
			}
			f = new java.io.File(fastaName+ ".sh");
			if (f.exists()) {		// Delete file on exiting.
				f.delete();
			}
			cancel();
			
		}
		/**
		 * Checks for results in the file system.
		 * @return 0 if results are absent, 1 otherwise.
		 */
		public int checkResults() {
			int status = 0;
			try {
				System.out.println("Looking for " + "./" + j.getJobId()
						+ blastResultExt);
				BufferedReader in = new BufferedReader(new FileReader("./"
						+ j.getJobId() + blastResultExt));

				in.close();
				// j.setBlastResult(jobResult.toString());
				j.setStatus(Job.BLASTED);
				System.out.println("**********SGEBlst: I'm done***********");
				status = 1;
			} catch (IOException e) {
				System.out.println("Blast not finished yet" + timeoutCounter);
			} finally {
				return status;
			}
		}
	}

	/**
	 * Constructor to initialize the job
         * @param j incoming job
         * @param requiredParams a hashtable of required parameters need to invoke netblast
         * @param optionalParams a hashtable of optinal parameters need to invoke netblast

	 */
	public InvokeSGEBlast(Job j,  Hashtable<String, String> requiredParams, Hashtable <String, String> optionalParams) {
		this.j = j;
                this.requiredParams = requiredParams;
                this.optionalParams = optionalParams;
		writeFastaToFile();
	}

	public void run() {
		String fastaName = blastPrefix + j.getJobId();
		Timer t = new Timer();
		BufferedWriter out;
		
		try {
			String command = requiredParams.get("command");
			String program = requiredParams.get("program");
			String maxAli = optionalParams.get("maxali");
			String priority = optionalParams.get("priority");
			StringBuffer shellCommand = new StringBuffer();
			StringBuffer sgeCommand = new StringBuffer();
			
			shellCommand.append(command);
			shellCommand.append(" ./Code/");
			shellCommand.append(fastaName + ".f");
			if(maxAli != null)
				shellCommand.append(" maxAli=" + maxAli);
			shellCommand.append(" fileOut=./Code/" +  j.getJobId() + blastResultExt);

			sgeCommand.append(program);
			if(priority != null)
				sgeCommand.append(" -p " + priority);
			sgeCommand.append (" ./" + fastaName + ".sh");
			out = new BufferedWriter(new FileWriter(fastaName + ".sh"));
			System.out.println("Executing " + shellCommand.toString());
			out.write(shellCommand.toString());
			// out.write("");
			out.newLine();
			out.close();

			System.out.println("about to call blast");
			Runtime runner = Runtime.getRuntime();
			t.schedule(new killBlast(), timeout, timeout);
			// Check every 2 minutes for a file. Check 8 times
			// Hence we wait for 16 mins before giving up

			System.out.println("Executing "
					+ sgeCommand.toString());
			pro = runner.exec(sgeCommand.toString());

			if (pro.waitFor() != 0) {
				System.out.println("SGEBlast: Error while executing");

				// Displaying error that SGEBlast returned.

				InputStream is = pro.getErrorStream();
				InputStreamReader isr = new InputStreamReader(is);
				String line;
				BufferedReader br = new BufferedReader(isr);
				System.out.println("Error message from SGEBlast:");
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
				//j.setStatus(Job.FAILED);
				QueueManager.notifyJobFailure(j);
			} else {
				// j.setStatus(source.Job.BLASTED);
				System.out.println("SGEBlast: Job Submitted");
			}

		} catch (Exception e) {
			//System.out.println(e.getMessage());
			QueueManager.notifyJobFailure(j);
		} finally {
			// checkResults();
			pro.destroy();
			
		}
	}

	/**
	 * Write the fasta sequence to file.
	 *
	 */
	void writeFastaToFile() {
		try {
			String fastaName = blastPrefix + j.getJobId();
			System.out.println("SGEBlast: writing temp fasta input file: "
					+ fastaName);
			BufferedWriter out = new BufferedWriter(new FileWriter(fastaName
					+ ".f"));
			// out.write(">" + fastaName + " \n");
			out.write(j.getFastaSequence());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
