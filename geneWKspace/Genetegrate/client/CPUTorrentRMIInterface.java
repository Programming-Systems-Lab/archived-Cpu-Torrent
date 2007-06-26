package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface for CPU Torrent
 * This Interface provides the set of functions to be implemented by the RMI Server for providing the APIs to Genetegrate/PredictProtein
 * @author swapneelsheth
 */
public interface CPUTorrentRMIInterface extends Remote {
	
	/**
	 * Submit a Job to CPU Torrent.
	 * @param fastaFile The FASTA File to run the Blast Job on.
	 * @return A string representing the id for the job
	 * @throws RemoteException
	 */
	public String submit(byte[] fastaFile) throws RemoteException;
	
	/**
	 * Get the Status of a Job
	 * @param jobId The Id of the Job to get the status for
	 * @return Status Code
	 * @throws RemoteException
	 */
	public int getStatus(String jobId) throws RemoteException;
	
	/**
	 * Gets the results of a blast operation
	 * @param jobId The Id of the Job to get the results for
	 * @return a byte array containing the results
	 * @throws RemoteException
	 */
	public byte[] getResult(String jobId) throws RemoteException;
}
