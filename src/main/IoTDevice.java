package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

public class IoTDevice {

	private List<Pair<Long, Double>> pairsFrequencyVoltage = new ArrayList<Pair<Long, Double>>();

	private String id;					
	private double capacitance; 		// Chipset capacitance
	private double powerIdle;			// Power when CPU in idle	
	private double batteryLevel;		// In W * (micro second)	
	private double ISL;					// In W * (micro second) - Inferior Safety Limit

	private long baseTime;				// In micro seconds
										/* Time in which the first task is created. From there the IoT device
										 * creates new tasks in time periods equal to the task generation rate, 
										 * that is given by the application class.
										 * Example: If baseTime is set to 150 micro seconds, i.e. global system
										 * time went from zero to 150 micro seconds, it means that the IoT device
										 * created it's first task in time 150 micro seconds. The next task to be
										 * created by the IoT device will happen in baseTime + taskGenerationRate
										 * */
	private long taskGenerationRate;	// In micro seconds	
	private int statusCPU;				// Has values CPU_FREE or CPU_OCCUPIED
	private static int CPU_FREE = 1;
	private static int CPU_OCCUPIED = 2;
	
	
	/* Constructor
	 * 
	 * */
	public IoTDevice(String id, long taskGenerationRate) {
		this.id = id;
		this.taskGenerationRate = taskGenerationRate;
		this.baseTime = new Random().nextInt((int) this.taskGenerationRate) + 1; // In micro seconds		
		
		this.capacitance = (double) (2.2 * Math.pow(10, -9)); 	// In Farads
		this.powerIdle = (double) (900 * Math.pow(10, -6)); 	// In W
		this.batteryLevel = 36000 * Math.pow(10, 6); 			// 36000 Ws - Equivalent to 36000*10^6 W*micro-second, 10Wh or 2000mAh with 5V
		this.ISL = batteryLevel * 0.1;							// ISL is 10% of maximum battery capacity
		
		// Operating frequencies for Arduino Mega 2560
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (1 * Math.pow(10, 6)), 1.8));
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (2 * Math.pow(10, 6)), 2.3));
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (4 * Math.pow(10, 6)), 2.7));
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (8 * Math.pow(10, 6)), 4.0));
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (16 * Math.pow(10, 6)), 5.0));
		
		this.statusCPU = CPU_FREE;
	}
	
	/* Getters */
	public String getId() {
		return this.id;
	}
	
	public long getBaseTime() {
		return this.baseTime;
	}
	
	public List<Pair<Long, Double>> getPairsFrequencyVoltage() {
		return pairsFrequencyVoltage;
	}
	
	public double getBatteryLevel() {
		return this.batteryLevel;
	}
	
	
	/* Calculate dynamic power of IoT device
	 * 
	 * Return: power in W
	 * */
	public double calculateDynamicPower(long operatingFrequency, double voltage) {
		double power;
		power = (double) (this.capacitance * Math.pow(voltage, 2) * (double) operatingFrequency); // In W		
		return power;
	}
	
	
	/* Calculate the execution time
	 * 
	 * Return: In micro seconds
	 * */
	public double calculateExecutionTime(long operatingFrequency, long computacionalLoad) {
		double time;
		time = (double) ((double) computacionalLoad / (double) operatingFrequency); // In seconds
		time = time * Math.pow(10, 6); // In micro seconds
		return time;
	}
	
	
	/* Calculate consumed dynamic energy
	 * 
	 * Return: W * micro-second
	 * */
	public double calculateDynamicEnergyConsumed(long operatingFrequency, double voltage, long computacionalLoad) {
		double energy;
		energy = this.calculateDynamicPower(operatingFrequency, voltage) * 
				this.calculateExecutionTime(operatingFrequency, computacionalLoad); // In W * micro-second
		return energy;
	}
	
	
	/* Calculate consumed idle energy
	 * - timeInIdle: in micro seconds
	 * 
	 * Return: W * micro-second 
	 * */
	public double calculateConsumedIdleEnergy(double timeInIdle) {
		double idleEnergy;
		idleEnergy = this.powerIdle * timeInIdle;
		return idleEnergy;
	}
	
	
	/* Consumes battery for IoT device
	 * - consumedEnergy in W*s
	 * */
	public void consumeBaterry(double consumedEnergy) {
		this.batteryLevel = this.batteryLevel - consumedEnergy;
	}	
	
	
	/* Verify if CPU is free
	 * - Return TRUE if CPU is free
	 * - Return FALSE if CPU is occupied
	 * */
	public boolean verifyCPUFree() {
		if(this.batteryLevel <= this.ISL) {
			System.out.println(this.id + " has reached battery's ISL.");
			return Boolean.FALSE;
		}
		if(this.statusCPU == CPU_FREE)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	
	/* Alter the CPU status
	 * - newStatus = 1 : CPU_FREE
	 * - newStatus = 2 : CPU_OCCUPIED
	 * */
	public void alterCPUStatus(int newStatus) {
		if(newStatus != CPU_FREE && newStatus != CPU_OCCUPIED) {
			System.out.println("Error - " + id + " - alterCPUStatus() : new status isn't FREE or OCCUPIED.");
			System.exit(0);
		}
		
		this.statusCPU = newStatus;
	}

}
