package middleware;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Policy Manager for CPU Torrent
 * @author swapneelsheth
 */
public class PolicyManager {
	
	/**
	 * 2-D Array of servers - id, ratio pairs (The ratio refers to what fraction of the computation should be sent to this particular server)
	 */
	private String[] serverName;
	private float[] serverProb;
	private String configFile;
	
	/**
	 * The Max No of Servers in the System
	 */
	private int MAX = 10;
	
	/**
	 * Constructor
	 */
	public PolicyManager(String configFile) {
		serverName = new String[MAX];
		serverProb = new float[MAX];
		this.configFile = configFile;
	}

	/**
	 * This method returns the Server to be chosen from a Random Distribution according to the Configuration File
	 * @return The name of the Server chosen
	 */
	String getServer() {
		double rand = Math.random();
		float val = 0;
		for (int i = 0; i < MAX; i++) {
			val += serverProb[i];
			if (rand < val) {
				return serverName[i];
			}
		}
		return null;
	}
	
	/**
	 * Loads the Configuration File
	 * @param fileName The Configuration File
	 */
	void loadFile() {
		try {
			FileReader fr = new FileReader(configFile);
			BufferedReader br = new BufferedReader(fr);
			String temp;
			int count = 0;
			while ((temp = br.readLine()) != null && count < MAX) {
				String[] arr = temp.split(":");
				serverName[count] = arr[0];
				serverProb[count] = Float.parseFloat(arr[1]);
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
