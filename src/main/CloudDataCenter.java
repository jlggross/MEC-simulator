package main;

public class CloudDataCenter {

	private String id;						
	private long standardFrequency;			// In Hz
	private long turboBoostFrequency;		// In Hz
	
	
	/* Constructor
	 * 
	 * */
	public CloudDataCenter(String id) {
		this.id = id;
		
		this.standardFrequency = (long) (2.8 * Math.pow(10, 9));
		this.turboBoostFrequency = (long) (3.9 * Math.pow(10, 9));
	}		
	
	
	/* Getters */
	public String getId() {
		return id;
	}
	
	public long getStandarFrequency() {
		return standardFrequency;
	}

	public long getTurboBoostFrequency() {
		return turboBoostFrequency;
	}
	
	
	/* Calculate dynamic energy for the standard operating frequency
	 * 
	 * */
	public double calculateDynamicEnergyStandardFreq(long computacionalLoad) {
		double energyDynamic;
		energyDynamic = 13.85 * calculateExecutionTimeStardardFreq(computacionalLoad); // In W * micro-second	
		return energyDynamic;
	}
	
	
	/* Calculate dynamic energy for turbo boost frequency
	 * 
	 * */
	public double calculateDynamicEnergyTurboFreq(long computacionalLoad) {
		double energyDynamic;
		energyDynamic = 24.28 * calculaTempoExecucaoFreqTurboBoost(computacionalLoad); // In W * micro-second	
		return energyDynamic;
	}
	
	
	/* Calculate execution time for standard frequency
	 * 
	 * */
	public double calculateExecutionTimeStardardFreq(long computacionalLoad) {
		double time;
		time = (double) computacionalLoad / (double) this.standardFrequency;	// In seconds
		time = time * Math.pow(10, 6); // In micro seconds
		return time;
	}
	
	
	/* Calculate execution time for turbo boost frequency
	 * 
	 * */
	public double calculaTempoExecucaoFreqTurboBoost(long computacionalLoad) {
		double time;
		time = (double) computacionalLoad / (double) this.turboBoostFrequency; // In seconds	
		time = time * Math.pow(10, 6); // In micro seconds
		return time;
	}
	
}
