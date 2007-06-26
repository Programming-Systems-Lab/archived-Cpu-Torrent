package middleware;

import java.rmi.*;
import java.rmi.server.*;

/**
 * RMI Server for CPU Torrent
 * @author swapneelsheth
 */
public class CPUTorrentRMIServer extends UnicastRemoteObject implements client.CPUTorrentRMIInterface {
	
	QueueManager qm;
	
	public CPUTorrentRMIServer() throws RemoteException {
		qm = new QueueManager();
	}

	public String submit(byte[] fastaFile) throws RemoteException {
		return(qm.addJob(fastaFile));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CPUTorrentRMIServer cpu = new CPUTorrentRMIServer();
			
			//Change the server binding details here if needed
			Naming.rebind("rmi://localhost:1099/CPUTorrent", cpu);
			System.out.println("RMI Server: Server Registered");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public byte[] getResult(String jobId) throws RemoteException {
		return(qm.getResult(jobId));
	}

	public int getStatus(String jobId) throws RemoteException {
		return(qm.getStatus(jobId));
	}
}
