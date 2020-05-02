package main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Octet;
import org.javatuples.Pair;
import org.javatuples.Septet;

public class Scheduler {
	
	// Entities - just to access methods
	private Task task;
	private IoTDevice iotDevice;
	private MECServer serverMEC;
	private CloudDataCenter cloud;
	private RAN_5G transmission5G;
	private FiberOptics transmissionFiber;
	
	// Cost coefficients
	/* They establish the importance between energy and time.  
	 * Their sum must be equal to 1 */
	private double coefficientEnergy;		
	private double coefficientTime;
	
	// Cost lists
	/* - Variable 0 : cost
	 * - Variable 1 : CPU core dynamic energy
	 * - Variable 2 : consumed energy for data transmission = ZERO
	 * - Variable 3 : CPU core execution time
	 * - Variable 4 : elapsed time for data transmission = ZERO
	 * - Variable 5 : operating frequency
	 * - Variable 6 : CPU core supply voltage */
	private List<Septet<Double, Double, Double, Double, Double, Long, Double>> costListIoTDevice = 
			new ArrayList<Septet<Double, Double, Double, Double, Double, Long, Double>>();
	
	/* - Variable 0 : cost
	 * - Variable 1 : CPU core dynamic energy
	 * - Variable 2 : consumed energy for data transmission
	 * - Variable 3 : CPU core execution time
	 * - Variable 4 : elapsed time for data transmission
	 * - Variable 5 : operating frequency
	 * - Variable 6 : CPU core supply voltage */
	private List<Septet<Double, Double, Double, Double, Double, Long, Double>> costListMECServer = 
			new ArrayList<Septet<Double, Double, Double, Double, Double, Long, Double>>();
		
	/* - Variable 0 : cost
	 * - Variable 1 : CPU core dynamic energy
	 * - Variable 2 : consumed energy for data transmissions
	 * - Variable 3 : CPU core execution time
	 * - Variable 4 : elapsed time for data transmission
	 * - Variable 5 : operating frequency
	 * - Variable 6 : CPU core supply voltage = ZERO */
	private List<Septet<Double, Double, Double, Double, Double, Long, Double>> costListCloud = 
			new ArrayList<Septet<Double, Double, Double, Double, Double, Long, Double>>();
	
	
	// Data transfer information
	private double energy5GUp; 			// Considers the transfer of entry data
	private double time5GUp;			// Considers the transfer of entry data
	private double energy5GDown;		// Considers the transfer of result data
	private double time5GDown;			// Considers the transfer of result data
	private double energyFiberUp; 		// Considers the transfer of entry data
	private double timeFiberUp;			// Considers the transfer of entry data
	private double energyFiberDown;		// Considers the transfer of result data
	private double timeFiberDown;		// Considers the transfer of result data

	// Allocation policies
	private static int POLICY1_IOT = 1;	
	private static int POLICY2_MEC = 2;	
	private static int POLICY3_CLOUD = 3;	 
	
	private static int NORMAL_TASK = -1;
	
	/* Constructor
	 * */
	public Scheduler(Task task, double coefficientEnergy, double coefficientTime, double alpha, double beta, double gamma) {
		this.task = task;
		this.iotDevice = new IoTDevice("dummy", 100);
		this.serverMEC = new MECServer("dummy");
		this.cloud = new CloudDataCenter("dummy");
		
		this.transmission5G = new RAN_5G();
		this.transmissionFiber = new FiberOptics();
		
		this.coefficientEnergy = coefficientEnergy;		
		this.coefficientTime = coefficientTime;
		
		// Calculate data transmission costs
		this.energy5GUp = this.transmission5G.calculateConsumedEnergy(this.task.getEntryDataSize());
		this.time5GUp = this.transmission5G.calculateTransferTime(this.task.getEntryDataSize());
		this.energy5GDown = this.transmission5G.calculateConsumedEnergy(this.task.getReturnDataSize());
		this.time5GDown = this.transmission5G.calculateTransferTime(this.task.getReturnDataSize());
		
		this.energyFiberUp = this.transmissionFiber.calculateEnergyConsumed(this.task.getEntryDataSize());
		this.timeFiberUp = this.transmissionFiber.calculateTransmissionTime(this.task.getEntryDataSize());
		this.energyFiberDown = this.transmissionFiber.calculateEnergyConsumed(this.task.getReturnDataSize());
		this.timeFiberDown = this.transmissionFiber.calculateTransmissionTime(this.task.getReturnDataSize());
		
		// Calculate costs for each allocation policy
		/* Alpha + Beta + Gamma must be equal to 1
		 * Each of these coefficients define priorities for the allocation options. */
		this.calculateCostPolicyIoTDevice(alpha);
		this.calculateCostPolicyMECServer(beta);
		this.calculateCostPolicyCloud(gamma);
	}
	
		
	/* Calculate costs for the allocation policy in the IoT device
	 * 
	 * */
	private void calculateCostPolicyIoTDevice(double alpha) {
		// Get pairs frequency-voltage from the IoT device
		List<Pair<Long, Double>> pairsFrequencyVoltage = new ArrayList<Pair<Long, Double>>();
		pairsFrequencyVoltage = this.iotDevice.getPairsFrequencyVoltage();
				
		for(Pair<Long, Double> pairFrequencyVoltage : pairsFrequencyVoltage) {
			double executionTime = this.iotDevice.calculateExecutionTime(pairFrequencyVoltage.getValue0(), 
					this.task.getComputationalLoad()); 
			double dynamicEnergy = this.iotDevice.calculateDynamicEnergyConsumed(pairFrequencyVoltage.getValue0(),
					pairFrequencyVoltage.getValue1(), this.task.getComputationalLoad());
			double cost = (this.coefficientEnergy * dynamicEnergy + this.coefficientTime * executionTime) * alpha;
			
			this.costListIoTDevice.add(
					new Septet<Double, Double, Double, Double, Double, Long, Double> 
					(cost, dynamicEnergy, 0.0, executionTime, 0.0, pairFrequencyVoltage.getValue0(), pairFrequencyVoltage.getValue1()));
		}
		
		// Has the cost of each pair frequency-voltage for the IoT device
		this.costListIoTDevice.sort(null);
	}
	
	
	/* Calculate costs for the allocation policy in the MEC server
	 * 
	 * */
	private void calculateCostPolicyMECServer(double beta) {
		// Get pairs frequency-voltage from the MEC Server
		List<Pair<Long, Double>> pairsFrequencyVoltage = new ArrayList<Pair<Long, Double>>();
		pairsFrequencyVoltage = this.serverMEC.getPairsFrenquecyVoltage();
				
		for(Pair<Long, Double> pairFrequencyVoltage : pairsFrequencyVoltage) {
			double executionTime = this.serverMEC.calculateExecutionTime(pairFrequencyVoltage.getValue0(), this.task.getComputationalLoad()); 
			double dynamicEnergy = this.serverMEC.calculateDynamicEnergyConsumed(pairFrequencyVoltage.getValue0(), 
					pairFrequencyVoltage.getValue1(), this.task.getComputationalLoad());
			double dynamicEnergyTotal = dynamicEnergy + this.energy5GUp + this.energy5GDown;
			double executionTimeTotal = executionTime + this.time5GUp + this.energy5GDown;
			
			double cost = (this.coefficientEnergy * dynamicEnergyTotal + this.coefficientTime * executionTimeTotal) * beta;
			this.costListMECServer.add(
					new Septet<Double, Double, Double, Double, Double, Long, Double> 
					(cost, dynamicEnergy, (this.energy5GUp + this.energy5GDown), 
							executionTime, (this.time5GUp + this.energy5GDown), 
							pairFrequencyVoltage.getValue0(), pairFrequencyVoltage.getValue1()));
		}
		
		// Has the cost of each pair frequency-voltage for the MEC server
		this.costListMECServer.sort(null);
	}
	
	
	/* Calculate costs for the allocation policy in the Cloud
	 * 
	 * */
	private void calculateCostPolicyCloud(double gama) {
		// Calculate cost for standard operating frequency
		long standardFrequency = this.cloud.getStandarFrequency();
		double standardTime = this.cloud.calculateExecutionTimeStardardFreq(this.task.getComputationalLoad());
		double standardEnergy = this.cloud.calculateDynamicEnergyStandardFreq(this.task.getComputationalLoad());
		
		double totalStandardEnergy = standardEnergy + this.energy5GUp + this.energyFiberUp + this.energyFiberDown + this.energy5GDown;
		double totoalStandardTime = standardTime + this.time5GUp + this.timeFiberUp + this.timeFiberDown + this.time5GDown;		
		
		double standardCost = (this.coefficientEnergy * totalStandardEnergy + this.coefficientTime * totoalStandardTime) * gama;
		this.costListCloud.add(
				new Septet<Double, Double, Double, Double, Double, Long, Double> 
				(standardCost, standardEnergy, (this.energy5GUp + this.energyFiberUp + this.energyFiberDown + this.energy5GDown),
						standardTime, (this.time5GUp + this.timeFiberUp + this.timeFiberDown + this.time5GDown),
						standardFrequency, 0.0));
		
		
		// Calculate cost for turbo boost operating frequency
		long turboFrequency = this.cloud.getTurboBoostFrequency();
		double turboTime = this.cloud.calculaTempoExecucaoFreqTurboBoost(this.task.getComputationalLoad());
		double turboEnergy = this.cloud.calculateDynamicEnergyTurboFreq(this.task.getComputationalLoad());
		
		double totalTurboEnergy = turboEnergy + this.energy5GUp + this.energyFiberUp + this.energyFiberDown + this.energy5GDown;
		double totalTurboTime = turboTime + this.time5GUp + this.timeFiberUp + this.timeFiberDown + this.time5GDown;
		
		double turboCost = (this.coefficientEnergy * totalTurboEnergy + this.coefficientTime * totalTurboTime) * gama;
		this.costListCloud.add(
				new Septet<Double, Double, Double, Double, Double, Long, Double>
				(turboCost, turboEnergy, (this.energy5GUp + this.energyFiberUp + this.energyFiberDown + this.energy5GDown),
						turboTime, (this.time5GUp + this.timeFiberUp + this.timeFiberDown + this.time5GDown),
						turboFrequency, 0.0));
		
		// Order the two costs (by cost)
		this.costListCloud.sort(null);
	}

	
	/* Define the allocation policy to execute the task
	 * - flagIoTDevice : If TRUE indicates that the IoT device's CPU is free.
	 * If FALSE, indicates it is occupied.
	 * - flagMECServer : If TRUE indicates that at least one CPU core from the 
	 * MEC server is free. If FALSE, then all MEC server CPU cores are occupied. 
	 * 
	 * Return: Octet with smallest cost 
	 * */
	public Octet<Double, Double, Double, Double, Double, Long, Double, Integer> defineAllocationPolicy(boolean flagIoTDevice, boolean flagMECServer) {	
		// Global cost comparator
		List<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>> globalCostList = 
				new ArrayList<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>>();
		
		// Policy 1 - IoT device: Insert costs for comparison
		if(flagIoTDevice == Boolean.TRUE) {
			for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListIoTDevice) {
				globalCostList.add(septet.add(POLICY1_IOT));
			}
		}
		
		// Policy 2 - MEC Server: Insert costs for comparison
		if(flagMECServer == Boolean.TRUE) {
			for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListMECServer) {
				globalCostList.add(septet.add(POLICY2_MEC));
			}
		}
		
		// Policy 3 - Cloud: Insert costs for comparison
		for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListCloud) {
			globalCostList.add(septet.add(POLICY3_CLOUD));
		}
		
		// Checks task type, if critical of not
		// * For critical tasks: Order by total time
		// * For non-critical tasks: Order by cost
		if(task.getDeadline() != NORMAL_TASK) {
			
			// Task is critical - Rearrange global list comparator to have total as the first value
			List<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>> globalCostListCritical = 
					new ArrayList<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>>();
			for(Octet<Double, Double, Double, Double, Double, Long, Double, Integer> octet : globalCostList) {
				Octet<Double, Double, Double, Double, Double, Long, Double, Integer> aux = 
						new Octet<Double, Double, Double, Double, Double, Long, Double, Integer>
								((octet.getValue3()+octet.getValue4()), octet.getValue1(), octet.getValue2(), octet.getValue0(), 
										octet.getValue4(), octet.getValue5(), octet.getValue6(), octet.getValue7());
				globalCostListCritical.add(aux);
			}
			globalCostListCritical.sort(null);
			
			if(Boolean.FALSE)
				this.printOctetList(globalCostListCritical);
			
			// Alter back octet elements 0 and 3 to keep the stardard of cost being value0 and total time value2
			Octet<Double, Double, Double, Double, Double, Long, Double, Integer> resultOctet = 
					new Octet<Double, Double, Double, Double, Double, Long, Double, Integer> (
							globalCostListCritical.get(0).getValue3(), globalCostListCritical.get(0).getValue1(), 
							globalCostListCritical.get(0).getValue2(), (globalCostListCritical.get(0).getValue0()-globalCostListCritical.get(0).getValue4()), 
							globalCostListCritical.get(0).getValue4(), globalCostListCritical.get(0).getValue5(),
							globalCostListCritical.get(0).getValue6(), globalCostListCritical.get(0).getValue7());
			
			// Return octet with the lowest total time
			return resultOctet; 
			
		} else {
			// Task is normal (non-critical) - Order results by cost
			globalCostList.sort(null);

			if(Boolean.FALSE)
				this.printOctetList(globalCostList);
			
			// Return octet with smallest cost
			return globalCostList.get(0); 
		}
	}
	

	/* Print Octet cost list for one task
	 * - octetList: the list with octets to be printed.
	 * 
	 * */
	private void printOctetList(List<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>> octetList) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		
		System.out.println("-------------------------------------------------");			
		if(this.task.getDeadline() != -1) {
			// Task is critical
			System.out.println(this.task.getIdTask() + " is critical.");
			System.out.println("Cost;CPU core Energy;Transfer Energy;CPU core Time;Diff Time;Transfer Time;Frequency;Voltage;Policy");
			
			for(Octet<Double, Double, Double, Double, Double, Long, Double, Integer> octet : octetList) {
				// Calculate difference between total time and deadline
				long diffTime = (long) (this.task.getDeadline() - octet.getValue0());
				System.out.println(df.format(octet.getValue0()) + ";" + df.format(octet.getValue1()) + ";" + 
						   df.format(octet.getValue2()) + ";" + df.format(octet.getValue3()) + ";" + diffTime + ";" +
						   df.format(octet.getValue4()) + ";" + octet.getValue5() + ";" + 
						   octet.getValue6() + ";" + octet.getValue7());
			}
		}
		else {
			// Task is non-critical (normal)
			System.out.println(this.task.getIdTask() + " is non-critical (normal).");
			System.out.println("Cost;CPU core Energy;Transfer Energy;CPU core Time;Transfer Time;Frequency;Voltage;Policy");
			
			for(Octet<Double, Double, Double, Double, Double, Long, Double, Integer> octet : octetList) {				
				System.out.println(df.format(octet.getValue0()) + ";" + df.format(octet.getValue1()) + ";" + 
						   df.format(octet.getValue2()) + ";" + df.format(octet.getValue3()) + ";" +
						   df.format(octet.getValue4()) + ";" + octet.getValue5() + ";" + 
						   octet.getValue6() + ";" + octet.getValue7());
			}
		}
		
		
	}
	
	
	/* Print systems costs
	 * 
	 * */
	public void printSystemCosts() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		
		System.out.println("-------------------------------------------------");
		System.out.println("Energy coefficient: " + this.coefficientEnergy);
		System.out.println("Time coefficient: " + this.coefficientTime);
		System.out.println("-------------------------------------------------");
		System.out.println("Data Entry Size: " + this.task.getEntryDataSize() + " bits");
		System.out.println("Result Data Size: " + this.task.getReturnDataSize() + " bits");
		System.out.println("-------------------------------------------------");
		System.out.println("5G Energy Up: " + this.energy5GUp + " W");
		System.out.println("5G Time Up: " + this.time5GUp + " s");
		System.out.println("5G Energy Down: " + this.energy5GDown + " W");
		System.out.println("5G Time Down: " + this.time5GDown + " s");
		System.out.println("-------------------------------------------------");
		System.out.println("Fiber Energy Up: " + this.energyFiberUp + " W");
		System.out.println("Fiber Time Up: " + this.timeFiberUp + " s");
		System.out.println("Fiber Energy Down: " + this.energyFiberDown + " W");
		System.out.println("Fiber Time Down: " + this.timeFiberDown + " s");
		
		System.out.println("-------------------------------------------------");
		System.out.println("Costs for local processing, in the IoT device, policy 1: ");
		int i = 0;
		for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListIoTDevice) {
			System.out.println("Cost " + i + ": " + df.format(septet.getValue0()) + "; CPU core energy: " + df.format(septet.getValue1()) +
								"; Transfer energy: " + septet.getValue2() + "; CPU core time: " + septet.getValue3() + 
								"; Transfer time: " + septet.getValue4() + "; Frequency: " + septet.getValue5() + "; Voltage: " + septet.getValue6());
			i++;
		}
		
		System.out.println("-------------------------------------------------");
		System.out.println("Costs for local processing, in the MEC server, policy 2: ");
		i = 0;
		for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListMECServer) {
			System.out.println("Cost " + i + ": " + df.format(septet.getValue0()) + "; CPU core energy: " + df.format(septet.getValue1()) +
					"; Transfer energy: " + septet.getValue2() + "; CPU core time: " + septet.getValue3() + 
					"; Transfer time: " + septet.getValue4() + "; Frequency: " + septet.getValue5() + "; Voltage: " + septet.getValue6());
			i++;
		}
		
		System.out.println("-------------------------------------------------");
		System.out.println("Costs for remote processing, in the Cloud, policy 3: ");
		i = 0;
		for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListCloud) {
			System.out.println("Cost " + i + ": " + df.format(septet.getValue0()) + "; CPU core energy: " + df.format(septet.getValue1()) +
					"; Transfer energy: " + septet.getValue2() + "; CPU core time: " + septet.getValue3() + 
					"; Transfer time: " + septet.getValue4() + "; Frequency: " + septet.getValue5() + "; Voltage: " + septet.getValue6());
			i++;
		}
		
	}
}
