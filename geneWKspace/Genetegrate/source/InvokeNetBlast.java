/**
 * Wrapper to invoke netblast.
 */

package source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Hashtable;

import middleware.QueueManager;


public class InvokeNetBlast extends Thread implements Invoker {
	private Process pro;
	private Job j;
	private Hashtable<String, String> requiredParams;
	private Hashtable<String, String> optionalParams;
	private static int timeOutInSeconds = 120;
	
	class killBlast extends TimerTask {
		public void run() {
			System.out.println("Netblast: Timeout");
			pro.destroy();
			cancel();
			//j.setStatus(Job.FAILED);
			QueueManager.notifyJobFailure(j);
		}
	}

	/**
	 * constructor to initialize the job
	 * @param j incoming job
	 * @param requiredParams a hashtable of required parameters need to invoke netblast
	 * @param optionalParams a hashtable of optinal parameters need to invoke netblast
	 */
	public InvokeNetBlast(Job j, Hashtable<String, String> requiredParams, Hashtable <String, String> optionalParams) {
		this.j = j;
		this.requiredParams = requiredParams;
		this.optionalParams = optionalParams;
		writeFastaToFile();
	}
	
	/**
	 * Runs blast.
	 */
	public void run() {
		Timer t = new Timer();
		t.schedule(new killBlast(), timeOutInSeconds * 1000);
		Runtime runner = Runtime.getRuntime();
		try {
			System.out.println("Netblast: about to call blast");
			String command = requiredParams.get("command");
			String inputFasta = j.getJobId() + ".f";
			String program = null;
			if ((optionalParams != null) && (program = optionalParams.get("program")) == null) {
				program = "blastp";
			}
			String outputResult = j.getJobId() + ".blastpgp";
			pro = runner
					.exec(command + " -i " + inputFasta + " -p " + program + " -o " + outputResult);
			if (pro.waitFor() != 0) {
				System.out.println("Netblast: Error while executing");

				// Displaying error that netblast returned.

				InputStream is = pro.getErrorStream();
				InputStreamReader isr = new InputStreamReader(is);
				String line;
				BufferedReader br = new BufferedReader(isr);
				System.out.println("Netblast: Error message from netblast:");
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
				//j.setStatus(Job.FAILED);
				QueueManager.notifyJobFailure(j);
			}
			else {
				j.setStatus(source.Job.BLASTED);
			}
			
		} catch (Exception e) {
			//System.out.println(e.getMessage());
			//j.setStatus(Job.FAILED);
			QueueManager.notifyJobFailure(j);
		} finally {
			if (pro != null)
				pro.destroy();
			t.cancel();
			java.io.File f = new java.io.File(j.getJobId() + ".f");
			if (f.exists()) {		// Delete file on exiting.
				f.delete();
			}
			System.out.println("+++++++++++++Netblast: I'm done+++++++++++++");
		}
	}

	void writeFastaToFile() {
		try {
			String fastaName = j.getJobId();
			System.out.println("Netblast: writing temp fasta input file: " + fastaName);
			BufferedWriter out = new BufferedWriter(new FileWriter(fastaName
					+ ".f"));
			//out.write(">" + fastaName + " \n");
			out.write(j.getFastaSequence());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
