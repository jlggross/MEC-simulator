package main;

public class Task {

	private String idTask;			
	private String idDevice;			// IoT Device that created the task
	private long deadline;				// In micro seconds
										/* If task is non-critical, then the deadline is -1 */
	private long baseTime;				/* System time when the task was created. 
	 									It is important to check this time stamp and compare it to the
	 									deadline while processing the task. */

	private long computationalLoad; 	// In CPU cycles
	private long dataEntrySize;			// In bits
	private long returnDataSize;		// In bits
	
	private double energyExecution;		// In W*micro-seconds		
	private double energyTransfer;		// In W*micro-seconds
	private long timeExecution;			// In micro seconds
	private long timeTransfer;			// In micro seconds
	
	private static int TASK_ALIVE = 1; 		// Task being allocated and executed
	private static int TASK_CONCLUDED = 2;	// Task is concluded. If task is critical, it will be concluded if task finishes before deadline.
	private static int TASK_CALCELLED = 3;	// Task is cancelled. Device ran out of battery or task finalized after deadline.
	private int taskStatus;		
	
	private static int POLICY1_IOT = 1;
	private static int POLICY2_MEC = 2;
	private static int POLICY3_CLOUD = 3;
	private int policy; 				// Indicate the chosen scheduling policy
										// Chosen in the scheduling fase, during simulation. Can have values 1, 2 or 3
	
	/* Constructor
	 * 
	 * */
	public Task(String idTarefa, String idDevice, long deadline, long baseTime, long computationalLoad, long dataEntrySize, long returnDataSize) {
		this.idTask = idTarefa;
		this.idDevice = idDevice;
		this.deadline = deadline;
		this.baseTime = baseTime;
		this.computationalLoad = computationalLoad;
		this.dataEntrySize = dataEntrySize;
		this.returnDataSize = returnDataSize;
		
		this.energyExecution = 0;
		this.energyTransfer = 0;
		this.timeExecution = 0;
		this.timeTransfer = 0;
		this.taskStatus = TASK_ALIVE;
	}
	
	
	/* Getters */
	public String getIdTask() {
		return this.idTask;
	}
	
	public String getIdDeviceGenerator() {
		return this.idDevice;
	}
	
	public int getTaskStatus() {
		return this.taskStatus;
	}
	
	public long getDeadline() {
		return deadline;
	}
	
	public long getBaseTime() {
		return baseTime;
	}
	
	public long getComputationalLoad() {
		return computationalLoad;
	}
	
	public long getEntryDataSize() {
		return dataEntrySize;
	}

	public long getReturnDataSize() {
		return returnDataSize;
	}
	
	public int getPolicy() {
		return policy;
	}
	
	/* Return total consumed energy for the task
	 * 
	 * */
	public double getTotalConsumedEnergy() {
		return this.energyExecution + this.energyTransfer;
	}

	public double getExecutionEnergy() {
		return energyExecution;
	}
	
	public double getTransferEnergy() {
		return energyTransfer;
	}
	
	
	/* Return total elapsed time until task is finished
	 * 
	 * */
	public long getTotalElapsedTime() {
		return this.timeExecution + this.timeTransfer;
	}
	
	public long getExecutionTime() {
		return timeExecution;
	}
	
	public long getTransferTime() {
		return timeTransfer;
	}

	
	/* Setters */
	public void setExecutionEnergy(double executionEnergy) {
		this.energyExecution = executionEnergy;
	}

	public void setTransferEnergy(double transferEnergy) {
		this.energyTransfer = transferEnergy;
	}
	
	/* Indicates how much time the task needs from creation to conclusion for the
	 * selected allocation policy. Considers the processing time plus the transfer
	 * times.
	 * */
	public void setExecutionTime(double executionTime) {
		this.timeExecution = (long) executionTime;
	}
	
	public void setTransferTime(double transferTime) {
		this.timeTransfer = (long) transferTime;
	}
	
	public void setPolicy(int policy) {
		if(policy != POLICY1_IOT && policy != POLICY2_MEC && policy != POLICY3_CLOUD) {
			System.out.println("Error - " + this.getIdTask() + " - setPolicy() : policy must be 1, 2 or 3");
			System.exit(0);
		}
					
		this.policy = policy;
	}
	
	
	
	/* Verify if task must finish
	 * - If the base time + transfer time + execution time are equal to the system time,
	 * then the task must finish. Otherwise the task keeps running.
	 * 
	 * */
	public boolean verifyIfTaskMustFinish(long systemTime) {
		if(this.taskStatus != TASK_ALIVE) {
			System.out.println("Error - verifyIfTaskMustFinish() : " + this.idTask + " is alterady finished");
			System.exit(0);
		}
		
		long timeToConclusion = this.baseTime + this.getTotalElapsedTime();
		if(timeToConclusion == systemTime) {
			this.finalizeTask(systemTime);
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	
	/* Finalize task
	 * 
	 * */
	private void finalizeTask(long systemTime) {
		if(this.deadline == -1)
			this.taskStatus = TASK_CONCLUDED;
		else if(systemTime < (this.baseTime + this.deadline))
			this.taskStatus = TASK_CONCLUDED;
		else {
			this.taskStatus = TASK_CALCELLED;
		}
	}
	
	
	/* Verify if task is critical
	 * 
	 * */
	public boolean verifyIfTaskIsCritical() {
		if(this.deadline == -1)
			return Boolean.FALSE;
		else
			return Boolean.TRUE;
	}

}
