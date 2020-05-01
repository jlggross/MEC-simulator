package main;

/* Class used to define the application parameters used for the simulation.
 * 
 * */

public class Application {

	private String id;
	
	private long rateGeneration; 			// In micro seconds
	private long dataEntrySize;				// In bits
	private long resultsSize;				// In bits
	private long computacionalLoad;			// In CPU cycles
	private double criticalTasksPercentage;	// In percentage (0 to 1)
	private long ciriticalTasksDeadline;	// In micro seconds
	private int numberOfTasks;				
	
	/* Constructor
	 * 
	 * */
	public Application(String id, long rateGeneration, long dataEntrySize, long resultsSize, 
			long computacionalLoad, double criticalTasksPercentage, long criticalTasksDeadline) {
		
		this.id = id;
		
		this.rateGeneration = rateGeneration;
		this.dataEntrySize = dataEntrySize;
		this.resultsSize = resultsSize;
	    this.computacionalLoad = computacionalLoad;
	    
	    if(this.criticalTasksPercentage > 1 || this.criticalTasksPercentage < 0) {
	    	System.out.println("App " + id + ": criticalTasksPercentage out of bounds");
	    	System.exit(0);
	    }
		
	    this.criticalTasksPercentage = criticalTasksPercentage;
		this.ciriticalTasksDeadline = criticalTasksDeadline;
		this.numberOfTasks = 0;
	}
	
	
	/* Getters */
	public String getId() {
		return this.id;
	}
	
	public long getRateGeneration() {
		return this.rateGeneration;
	}

	public long getDataEntrySize() {
		return this.dataEntrySize;
	}
	
	public long getResultsSize() {
		return resultsSize;
	}
	
	public double getCriticalTasksPercentage() {
		return this.criticalTasksPercentage;
	}

	public long getCriticalTasksDeadline() {
		return this.ciriticalTasksDeadline;
	}
	
	public int getNumberOfTasks() {
		return this.numberOfTasks;
	}
	
	public long getComputationalLoad() {
		return computacionalLoad;
	}
	
	
	/* Setters */
	public void setNumberOfTasks(int numberOfTasks) {
		this.numberOfTasks = numberOfTasks;
	}
	
	
	/* Define if task is critical or not
	 * - This function must be called every time a task is created.
	 * - When calling this function the application will define if the new task is critical or not
	 * based in the percentage of critical tasks of the application.
	 * */
	public boolean defineIfTaskIsCritical(int numberOfTask) {
		if((int) ((numberOfTask+1) * this.numberOfTasks * this.criticalTasksPercentage) % this.numberOfTasks == 0) {
			return Boolean.TRUE; // Task is critical
		}
		else {
			return Boolean.FALSE; // Task is not critical
		}
	}
	
}
