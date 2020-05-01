package main;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class MECServer {

	private List<Pair<Long, Double>> pairsFrequencyVoltage = new ArrayList<Pair<Long, Double>>();

	private String id;					
	private double capacitance; 		// Chipset capacitance
	private double powerIdle;				

	private List<Boolean> statusCPUs = new ArrayList<Boolean>();	// 20 CPU cores
	private static boolean CPU_OCCUPIED = Boolean.TRUE;
	private static boolean CPU_FREE = Boolean.FALSE;
	
	private static int MAX_CPUS = 20;
	
	/* Constructor
	 * 
	 * */
	public MECServer(String id) {
		this.id = id;
		this.capacitance = (double) (1.8 * Math.pow(10, -9)); // In Farads
		this.powerIdle = (double) (0.675); // In W
		
		// Operating frequencies for Raspberry Pi 4 Model B		
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (600 * Math.pow(10, 6)), 0.8));
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (750 * Math.pow(10, 6)), 0.825));
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (1000 * Math.pow(10, 6)), 1.0));
		this.pairsFrequencyVoltage.add(new Pair<Long, Double> ((long) (1500 * Math.pow(10, 6)), 1.2));
		
		// Status of all CPU cores from the NEC server
		for(int i = 0; i < MAX_CPUS; i++)
			statusCPUs.add(CPU_FREE);
	}
	
	
	/* Getters */
	public String getId() {
		return id;
	}
	
	public List<Pair<Long, Double>> getPairsFrenquecyVoltage() {
		return pairsFrequencyVoltage;
	}	

	public int getNumberOfFreeCPUs() {
		int cont = 0;
		for(int i = 0; i < MAX_CPUS; i++) {
			if(statusCPUs.get(i) == CPU_FREE)
				cont++;
		}
		
		return cont;
	}
	
	
	
	/* Calculate dynamic power of a CPU core from the MEC server
	 * 
	 * Return: In Watts
	 * */
	public double calculateDynamicPower(long operatingFrequency, double voltage) {
		double power;
		power = (double) (this.capacitance * Math.pow(voltage, 2) * (double) operatingFrequency); // In W		
		return power;
	}
	
	
	/* Calculate execution time
	 * 
	 * Return: In micro seconds
	 * */
	public double calculateExecutionTime(long operatingFrequency, long computationalLoad) {
		double time;
		time = (double) computationalLoad / (double) operatingFrequency; // In seconds
		time = time * Math.pow(10, 6); // In micro seconds
		return time;
	}
	
	
	/* Calculate consumed energy
	 * 
	 * Return: In W * micro-seconds
	 * */
	public double calculateDynamicEnergyConsumed(long operatingFrequency, double voltage, long computationalLoad) {
		double energy;
		energy = this.calculateDynamicPower(operatingFrequency, voltage) * 
				this.calculateExecutionTime(operatingFrequency, computationalLoad); // In W * micro-seconds
		return energy;
	}	
	
	
	/* Verify if there is a free CPU core
	 * - Return TRUE if there is a free CPU core
	 * - Return FALSE if all CPU cores are occupied
	 * */
	public boolean verifyCPUFree() {
		for(boolean status : statusCPUs) {
			if(status == CPU_FREE)
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	
	/* Occupy CPU to process task
	 * - Return TRUE if a CPU core has been occupied
	 * - Return FALSE if all CPU cores are already occupied
	 * */
	public boolean ocuppyCPU() {
		if(this.verifyCPUFree() == Boolean.TRUE) {
			for(int i = 0; i < MAX_CPUS; i++) {
				if(statusCPUs.get(i) == CPU_FREE) {
					statusCPUs.remove(i);
					statusCPUs.add(CPU_OCCUPIED);
					return Boolean.TRUE;
				}
			}
		}
		
		System.out.println(id + "-occupyCPU() : All CPU cores are already occupied.");
		return Boolean.FALSE;
	}
	
	
	/* Free CPU core for future usage. Resource is made available.
	 * - Return TRUE if the occupied CPU core is now free
	 * - Return FALSE if all CPU cores are already free
	 * */
	public boolean freeCPU() {
		for(int i = 0; i < MAX_CPUS; i++) {
			if(statusCPUs.get(i) == CPU_OCCUPIED) {
				statusCPUs.remove(i);
				statusCPUs.add(CPU_FREE);
				return Boolean.TRUE;
			}
		}	
		
		System.out.println(id + "-freeCPU() : All CPU cores are alerady free.");
		return Boolean.FALSE;
	}
}
