package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.rmi.*;

/**
 * Client to test the RMI Server for CPU Torrent
 * @author swapneelsheth
 */
public class TestRMIClient extends Thread {

	String fastaFile; 	// stores the filename in which the fasta sequence is stored.
	/**
	 * @param args
	 */
	TestRMIClient (String file) {
		fastaFile = file;
	}

	// Do what is needed to submit the job to the client.

	public void run() {
		try {
			CPUTorrentRMIInterface cpu = (CPUTorrentRMIInterface) Naming.lookup("rmi://localhost:1099/CPUTorrent");


			/**
			 * Submitting the fasta file for blasting.
			 */

			BufferedReader br = new BufferedReader(new FileReader(fastaFile));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			byte[] byteFastaFile = sb.toString().getBytes();
			String jid = cpu.submit(byteFastaFile);
			System.out.println("Submitted Job id:" + jid);


			// Check whether the job has completed.

			while (true) {
				Thread.sleep(3*1000);
				/**
				 * checking status of jobs.
				 */
				int status;


				status = cpu.getStatus(jid);

				// If the job has completed, then break out of the loop.
				if (status == source.Job.BLASTED || status == source.Job.FAILED) {
					System.out.println("------------------------------");
					System.out.println("job " + jid + " completed with status " + status);
					break;
				}
				else
					System.out.println("Status of job " + jid + " is: " + status);
			}




			// Get the results for the job.


			byte[] result = cpu.getResult(jid);
			String name = "ClientCopy" + jid + ".blastpgp";
			FileWriter fw = null;
			try {
				fw = new FileWriter(name);
				String stuff = new String(result);
				fw.write(stuff);
				fw.close();
			} catch(java.io.IOException ie) {
				System.out.println("Client: Error writing to file");
				ie.printStackTrace();
			} 

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
