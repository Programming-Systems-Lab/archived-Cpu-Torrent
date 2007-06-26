/**
 * @author swapneelsheth, aaron
 * Job Class which stores information about a job
 */

package source;

import java.io.Serializable;

public class Job implements Serializable{
	private String jobId;
	private String fastaSequence;
	private String blastResult;
	private byte numRetries;
	private String serviceType;
	public static final int NEW = 0;
	public static final int BLASTING = 1;
	public static final int BLASTED = 2;
	public static final int FAILED = 3;
	
	/**
	 * status stores the status of the job 
	 */
	int status;
	
	/**
	 * Constructor
	 * @param jobId jobId of the job submitted
	 */
	public Job(String jobId, String fastaSequence) {
		this.jobId = jobId;
		this.fastaSequence = fastaSequence;
		this.status = NEW;
		this.numRetries = (byte)0;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}

	public String getFastaSequence() {
		return fastaSequence;
	}

	public void setFastaSequence(String fastaSequence) {
		this.fastaSequence = fastaSequence;
	}

	public String getBlastResult() {
		return blastResult;
	}

	public void setBlastResult(String blastResult) {
		this.blastResult = blastResult;
	}

	public byte getNumRetries() {
		return numRetries;
	}

	public void setNumRetries(byte numRetries) {
		this.numRetries = numRetries;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
}
