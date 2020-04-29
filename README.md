# Introduction

This project comprises of a simulator I developed for my **master's degree** at Federal University of Rio Grande do Sul (Portuguese: Universidade Federal do Rio Grande do Sul, UFRGS). 

# Simulated environment

The environment is a three layer architecture, the bottom layer has IoT devices, the intermediate layer runs local servers, making Mobile Edge Computing (MEC) available for the final user and the top layer has Data Centers that run traditional Cloud Computing services. The IoT device layer communicates with the MEC layer with 5G and the MEC layer communicates with the Cloud layer with fiber optics.

The MEC servers are local servers placed close to the final user (IoT devices in this case) which provide lower latency when running tasks origined in those IoT devices, if compared to the Cloud latency. 

Tasks are created in the IoT devices. If the scheduling algorithm devices to offload the task and execute it in a MEC server, then a 5G connecting is made, adding latency to the application. But if the task if offloaded to the Cloud, then a 5G connecting is made with the MEC server, which receives the tasks data and source code, and another connecting between the MEC server and the Cloud is made, finally sending the tasks data and source code to the Cloud. In this second case more latency is added to the application. When the task's executing is complete the results must be sent back to the IoT device (the user).

Even with more latency added to the application when tasks run in the MEC server or in the Cloud, it could be a better choice then running locally in the IoT's device core, because the executing time could be very high in the user's device. 

It is expected that the IoT devices move around the network, that's why a wireless connection is required between the bottom layer and the intermediate layer, so that offloading of data and task's source code can be performed.

## Architecture hardware description

* **IoT devices**: Each IoT device is assumed to be an Arduino Mega 2560. THe Arduino Mega 2500 has an ATmega2560 microcontroller with one 16MHz core. The available configurable frequencies for the microcontroller are 16 MHz, 8 MHz, 4MHz, 2 MHz and 1 MHz. The associated core voltages for each of the frequencies are, respectively, 5V, 4V, 2.7V, 2.3V and 1.8V. So, running a task with clock set to 2 MHz will make the voltage core be set to 2.3V.

* **MEC server**: MEC servers are local servers close to the final user (IoT devices). It provides





The IoT devices create tasks that must be assigned to run either in the IoT device itself, in a core from a MEC server or in a core from the Cloud.

## DVFS
The DVFS (Dynamic Voltage and Frequency Scaling) technique is used in order to alter the energy consumed by the CPU cores of both IoT devices and MEC servers during execution time. The TEMS algorithm calculates the final costs of all options of operating frequency and core voltage and chooses the best fitting option, i. e. the pair frequency-voltage that provides the minimum calculated cost for the system.

# The TEMS scheduling algorithm

# Setting the project and running experiments


The simulator executes a scheduling algorithm in a Mobile Edge Computing (MEC) environment. The scheduling algorithm, named TEMS (Time and Energy Minimization Scheduler) is responsable for 


Mobile Edge Computing Simulator developed for my master's
