package main;

public class FiberOptics {

	double latency;		// In seconds
	double power;		// In W
	long transferRate;	// In bits/s
	
	public FiberOptics() {
		this.latency = (double) (5 * Math.pow(10, -3));
		this.power = 3.65;
		this.transferRate = (long) Math.pow(10, 9);
	}

	
	/* Calculate data transmission time for fiber optics
	 * - dataSize : In bits
	 * 
	 * Return: In micro seconds  
	 * */
	public double calculateTransmissionTime(long dataSize) {
		double transmissionTime;
		transmissionTime = (double) dataSize / (double) this.transferRate; // In seconds
		transmissionTime = transmissionTime * Math.pow(10, 6); // In micro seconds
		return transmissionTime;
	}
	
	
	/* Calculate energy consumed during data transfer
	 * - dataSize : In bits
	 * 
	 * Return: W * micro-seconds  
	 * */
	public double calculateEnergyConsumed(long dataSize) {
		double energy;
		energy = this.power * this.calculateTransmissionTime(dataSize);
		return energy;
	}
	
}
