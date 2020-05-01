package main;

public class RAN_5G {

	double latency;			// In seconds
	double alpha;			// In Watt
	double beta; 			// In Watt
	long transferRate;		// In bits/s
	
	/* Constructor
	 * 
	 * */
	public RAN_5G() {
		this.latency = (double) (5 * Math.pow(10, -3));
		this.alpha = (double) (0.52 * Math.pow(10, -3));
		this.beta = (double) 3.86412; 
		this.transferRate = (long) Math.pow(10, 9);
	}
	
	
	/* Calculate the transmission power for 5G
	 * 
	 * Return: In W
	 * */
	public double calculatePower() {
		double power;
		power = (double) (this.alpha * this.transferRate / Math.pow(10, 6) + this.beta);
		return power;
	}
	
	
	/* Calculate data transmission time for 5G
	 * - dataSize : In bits
	 * 
	 * Return: In micro seconds  
	 * */
	public double calculateTransferTime(long dataSize) {
		double transferTime;
		transferTime = (double) dataSize / (double) this.transferRate; // In seconds
		transferTime = transferTime * Math.pow(10, 6); // In micro seconds
		return transferTime;
	}
	
	
	/* Calculate consumed energy during data transfer
	 * - dataSize : In bits
	 * 
	 * Return: In W * micro-second
	 * */
	public double calculateConsumedEnergy(long dataSize) {
		double energy;
		energy = this.calculatePower() * this.calculateTransferTime(dataSize); // In W * micro-second
		return energy;
	}
}
