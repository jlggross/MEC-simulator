package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Octet;
import org.javatuples.Triplet;

/* Informa��es sobre a simula��o:
 * 
 * - O tempo de ciclo do sistema � de 1 micro segundo. A simula��o varre o sistema 
 * e avan�a no tempo 1 micro segundo para nova an�lise. Todas as tomadas de decis�o
 * ocorrem em intervalos de 1 micro segundo. 
 * - O tempo de simula��o ir� correr at� a finaliza��o da �ltima tarefa
 * 
 * Tempos
 * - todos os tempos s�o em micro segundos, que � o tempo de ciclo de simula��o
 * */


public class SimulatorExecution {

	static int CPU_LIVRE = 1;
	static int CPU_OCUPADA = 2;
	
	static int POLITICA1 = 1;
	static int POLITICA2 = 2;
	static int POLITICA3 = 3;
	
	static int TAREFA_VIVA = 1;
	static int TAREFA_CONCLUIDA = 2;	
	static int TAREFA_CANCELADA = 3;
	
		
	public static void main(String args[]) throws IOException {
		
		// Par�metros para a simula��o
		List<Application> appList = new ArrayList<Application>();
		
		// Aplica��o 1 - Carga 2000 * 10^6
		long taxaGeracao = (long) (10 * Math.pow(10, 6)); //Em micro segundos
		long entradaDados = (long) (36.288 * 8 * Math.pow(10, 6));
		long resultados = (long) Math.pow(10, 4);
		long cargaComputacional = (long) (20 * Math.pow(10, 6));
		long deadlineCriticas = (long) (0.5 * Math.pow(10, 6)); //Em micro segundos
		double percentualCriticas = (double) 0.1;
		appList.add(new Application("App1-1", taxaGeracao, entradaDados, resultados, cargaComputacional, percentualCriticas, deadlineCriticas));
		
		// Aplica��o 1 - Carga 20 * 10^6
		taxaGeracao = (long) (10 * Math.pow(10, 6)); //Em micro segundos
		entradaDados = (long) (36.288 * 8 * Math.pow(10, 6));
		resultados = (long) Math.pow(10, 4);
		cargaComputacional = (long) (20 * Math.pow(10, 6));
		deadlineCriticas = (long) (0.5 * Math.pow(10, 6)); //Em micro segundos
		percentualCriticas = (double) 0.1;
		appList.add(new Application("App1-3", taxaGeracao, entradaDados, resultados, cargaComputacional, percentualCriticas, deadlineCriticas));
		
		// Aplica��o 1 - Carga 2 * 10^6
		taxaGeracao = (long) (10 * Math.pow(10, 6)); //Em micro segundos
		entradaDados = (long) (36.288 * 8 * Math.pow(10, 6));
		resultados = (long) Math.pow(10, 4);
		cargaComputacional = (long) (2 * Math.pow(10, 6));
		deadlineCriticas = (long) (0.5 * Math.pow(10, 6)); //Em micro segundos
		percentualCriticas = (double) 0.1;
		appList.add(new Application("App1-3", taxaGeracao, entradaDados, resultados, cargaComputacional, percentualCriticas, deadlineCriticas));
		
		// Aplica��o 2
		taxaGeracao = (long) (0.1 * Math.pow(10, 6)); //Em micro segundos
		entradaDados = (long) (4 * 8 * Math.pow(10, 6));
		resultados = (long) (5 * Math.pow(10, 3));
		cargaComputacional = (long) (200 * Math.pow(10, 6));
		deadlineCriticas = (long) (0.1 * Math.pow(10, 6)); //Em micro segundos
		percentualCriticas = (double) 0.5;
		appList.add(new Application("App2", taxaGeracao, entradaDados, resultados, cargaComputacional, percentualCriticas, deadlineCriticas));
		
		
		// ---------------------------------------------------------------------------
		// Simula��o
		// ---------------------------------------------------------------------------		
		List<Integer> listaQtdeMaxTarefas = Arrays.asList(500, 5000);
		List<Integer> listaQtdeIoTdevices = Arrays.asList(100, 500, 1000);
		List<Integer> listaQtdeMECServers = Arrays.asList(1, 2);
		int qtdeDataCenters = 1;
		
		for(int qtdeTarefas : listaQtdeMaxTarefas) {
			List<Task> listaTarefasAndamento = new ArrayList<Task>();
			Task[] listaTarefasFinalizadas = new Task[qtdeTarefas];
		for(int qtdeIoTDevices : listaQtdeIoTdevices) {
			if(qtdeIoTDevices > qtdeTarefas)
				continue;
		for(int qtdeMECServers : listaQtdeMECServers) { 
		for(Application app : appList) {
			app.setQtdeTarefas(qtdeTarefas); // Define a quantidade de tarefas que ser�o geradas
			
			// ---------------------------------------------------------------------------
			// Inicializa��o
			// ---------------------------------------------------------------------------

			// Cria listas de nodos
			long taxaGeracaoTarefas = app.getTaxaGeracao();
			
			IoTDevice[] listaIoTDevices = new IoTDevice[qtdeIoTDevices];
			for(int i = 0; i < qtdeIoTDevices; i++)
				listaIoTDevices[i] = new IoTDevice("Device-" + i, taxaGeracaoTarefas);
			
			MECServer[] listaMECServers = new MECServer[qtdeMECServers];
			for(int i = 0; i < qtdeMECServers; i++)
				listaMECServers[i] = new MECServer("MEC-" + i);
			
			CloudDataCenter[] listaDataCenters = new CloudDataCenter[qtdeDataCenters];
			for(int i = 0; i < qtdeDataCenters; i++)
				listaDataCenters[i] = new CloudDataCenter("DataCenter-" + i);
			
			long tempoSistema = 0; //tempo da simula��o (zero micro segundos) 
			int tarefasCanceladasEConcluidas = 0;
			int tarefasCriadas = 0;
			
			// ---------------------------------------------------------------------------
			// Come�o da simula��o
			// ---------------------------------------------------------------------------
			System.out.println("Cen�rio 01-" + qtdeTarefas + "-" + qtdeIoTDevices + "-" + qtdeMECServers + "-" + (long) app.getCargaComputacional());
			
			// Define fatores de peso ponderado para energia e tempo
			double fatorEnergia, fatorTempo;
			fatorEnergia = 4.0 / 5.0;
			fatorTempo = 1 - fatorEnergia;
			
			// Define fatores de prioriza��o das pol�ticas de aloca��o
			double alpha, beta, gama;
			alpha = beta = gama = 1.0 / 3.0;
			
			while(Boolean.TRUE) {
				
				// Verifica se h� tarefas a serem geradas
				for(int i = 0; i < qtdeIoTDevices; i++) {
					if(((tempoSistema - listaIoTDevices[i].getTempobase()) % app.getTaxaGeracao()) == 0) {
						
						// Tarefa � gerada
						Task novaTarefa = new Task("TarefaDummy", "DeviceDummy", -1, 0, 0, 0, 0);
						if(tarefasCriadas < qtdeTarefas) {
							if(app.defineSeTarefaCritica(tarefasCriadas) == Boolean.TRUE) {
								novaTarefa = new Task("Tarefa-" + tarefasCriadas, listaIoTDevices[i].getId(), 
													app.getDeadlineCriticas(), tempoSistema, app.getCargaComputacional(),
													app.getEntradaDados(), app.getResultados());
							}
							else {
								novaTarefa = new Task("Tarefa-" + tarefasCriadas, listaIoTDevices[i].getId(), -1, 
													tempoSistema, app.getCargaComputacional(), app.getEntradaDados(), app.getResultados());
							}
							tarefasCriadas++;
						}
						else
							break;
						
						// ---------------------------------------------------------------------------
						// In�cio da aloca��o da tarefa
						// ---------------------------------------------------------------------------
						
						// Calcula os custos do sistema
						Scheduler scheduler = new Scheduler(novaTarefa, fatorEnergia, fatorTempo, alpha, beta, gama);
						//scheduler.imprimeCustos();
						
						// Verifica se o dispositivo IoT que gerou a tarefa est� com a CPU livre
						boolean flagIoTDevice = Boolean.FALSE;
						if(listaIoTDevices[i].verificaCPULivre() == Boolean.TRUE)
							flagIoTDevice = Boolean.TRUE;
						
						// Verifica se h� alguma CPU livre nos servidores MEC
						boolean flagMECServer = Boolean.FALSE;
						for(int j = 0; j < qtdeMECServers; j++) {
							if(listaMECServers[j].verificaCPULivre() == Boolean.TRUE) {
								flagMECServer = Boolean.TRUE;
								break;
							}
						}
							
						// Busca menor custo para aloca��o da tarefa
						Octet<Double, Double, Double, Double, Double, Long, Double, Integer> octet;
						octet = scheduler.defineMenorCusto(flagIoTDevice, flagMECServer);

						// -----------------------------------------------------------------------------
						// Atualiza energia e tempo de execu��o e pol�tica de escalonamento para a tarefa
						// -----------------------------------------------------------------------------						
						novaTarefa.setEnergiaExecucao(octet.getValue1());
						novaTarefa.setEnergiaTransmissaoDados(octet.getValue2());
						novaTarefa.setTempoExecucao(octet.getValue3());
						novaTarefa.setTempoTransmissaoDados(octet.getValue4());
						novaTarefa.setPolitica(octet.getValue7());
						
						
						if(Boolean.FALSE) {
						System.out.println(listaIoTDevices[i].getId() + " - N�vel de bateria: " + listaIoTDevices[i].getNivelBateria() +
											"; CPU Livre: " + listaIoTDevices[i].verificaCPULivre());
						}
						
						// Ocupa os recursos de hardware
						if(octet.getValue7() == POLITICA1) {
							listaIoTDevices[i].alteraStatusCPU(CPU_OCUPADA);
							listaIoTDevices[i].consomeBateria(octet.getValue1() + octet.getValue2());
						} else if(octet.getValue7() == POLITICA2) {
							for(int j = 0; j < qtdeMECServers; j++) {
								if(listaMECServers[j].verificaCPULivre() == Boolean.TRUE) {
									listaMECServers[j].OcupaCPU();
									break;
								}
							}
						}					
						
						// Tarefa alocada. Segue para monitoramente na lista de tarefas em andamento
						listaTarefasAndamento.add(novaTarefa);
						
						// Imprime a quantidade de CPUs ocupadas nos servidores MEC
						if(Boolean.FALSE) {
							System.out.println(novaTarefa.getIdTarefa() + " criada; Tempo Sistema: " + tempoSistema);
							for(int j = 0; j < qtdeMECServers; j++) {
								int qtde = listaMECServers[j].getQuantidadeCPUsLivres();
								System.out.println(listaMECServers[j].getId() + " com " + qtde + " CPUs livres; Tempo Sistema: " + tempoSistema);
							}
						}
						
						// ---------------------------------------------------------------------------
						// Fim da aloca��o da tarefa
						// ---------------------------------------------------------------------------
					}
				}
				
				// ---------------------------------------------------------------------------
				// In�cio da verifica��o para finaliza��o de tarefas
				// ---------------------------------------------------------------------------
				
				// Verifica se h� alguma tarefa rec�m finalizada
				if(!listaTarefasAndamento.isEmpty()) {
					List<Task> listaTarefasAndamentoAux = new ArrayList<Task>();
					listaTarefasAndamentoAux.addAll(listaTarefasAndamento);
					for(Task aux : listaTarefasAndamentoAux) {
						Task task = aux;
						
						if(task.verificaSeTarefaDeveFinalizar(tempoSistema) == Boolean.TRUE) {
							listaTarefasFinalizadas[tarefasCanceladasEConcluidas] = task;
							tarefasCanceladasEConcluidas++;
							listaTarefasAndamento.remove(aux);
							
							// Liberar recursos
							if(task.getPolitica() == POLITICA1) {
								int id = Integer.parseInt(task.getIdDeviceGerador().split("-")[1]);
								listaIoTDevices[id].alteraStatusCPU(CPU_LIVRE);
							}
							if(task.getPolitica() == POLITICA2) {
								for(int j = 0; j < qtdeMECServers; j++) {
									if(listaMECServers[j].LiberaCPU() == Boolean.TRUE)
										break;
								}
							}
								
							//System.out.println(aux.getIdTarefa() + " finalizada; Tempo Sistema: " + tempoSistema);
							if(Boolean.TRUE) {
								if( tarefasCanceladasEConcluidas % 100 == 0 )
									System.out.println("Qtde tarefas conclu�das: " + tarefasCanceladasEConcluidas);
							}
						}
						
						//System.out.println("Tarefas Finalizadas: " + tarefasCanceladasEConcluidas);
					}
				}
				
				
				// Verifica se todas as tarefas foram conclu�das ou canceladas
				if(tarefasCanceladasEConcluidas == qtdeTarefas) {
					
					if(Boolean.FALSE) {
						for(int j = 0; j < qtdeTarefas; j++) {
							System.out.println(listaTarefasFinalizadas[j].getIdTarefa() + "; Energia: " + listaTarefasFinalizadas[j].getEnergiaTotalConsumida());
						}
					}
					break; // Finaliza a rodada de simula��o
				}
				
				// ---------------------------------------------------------------------------
				// Fim da verifica��o para finaliza��o de tarefas
				// ---------------------------------------------------------------------------
				
				
				// Atualiza tempo do sistema (avan�a 1 micro segundo)
				tempoSistema++;
			}	
			
			// ---------------------------------------------------------------------------
			// Estat�sticas
			// ---------------------------------------------------------------------------
			
			/* Formato dos arquivos de execu��o:
			 * 
			 * 1. Cen�rio de testes de varia��o de carga computacional
			 * [n�meroCen�rio-qtdeTarefas-qtdeIoTDevices-qtdeMECServers-tamanhoCargaComputacional-tipoInformacao]
			 * Ex.: 01-500-100-1-2000000000-imprimeTempoFinalizacaoVsPoliticaVsFinalizacao.txt
			 * 
			 * */
			
			if(Boolean.TRUE) {
				// Imprime dados para testes de carga computacional
				String filename = "01-" + qtdeTarefas + "-" + qtdeIoTDevices + "-" + qtdeMECServers + "-" + (long) app.getCargaComputacional();
				imprimeTempoFinalizacaoVsPoliticaVsFinalizacao(filename, listaTarefasFinalizadas, fatorEnergia, fatorTempo);
			}

			/*			
			System.out.println("================================================================");
			System.out.println("==== Par�metros Simula��o ====");
			System.out.println("================================================================");
			System.out.println("Qtde tarefas: " + qtdeTarefas);
			System.out.println("Qtde IoT Devices: " + qtdeIoTDevices);
			System.out.println("Qtde MEC Servers: " + qtdeMECServers);
			System.out.println("Qtde Data Centers: " + qtdeDataCenters);
			System.out.println("================================================================");
			System.out.println("==== Par�metros Aplica��o ====");
			System.out.println("================================================================");
			System.out.println("Aplica��o: " + app.getId());
			System.out.println("Taxa de gera��o: " + app.getTaxaGeracao() + " micro segundos");
			System.out.println("Entrada de dados: " + app.getEntradaDados() + " bits");
			System.out.println("Carga computacional: " + app.getCargaComputacional() + " ciclos de CPU");
			System.out.println("Deadline cr�ticas: " + app.getDeadlineCriticas() + " micro segundos");
			System.out.println("Percentual cr�ticas: " + app.percentualCriticas()*100 + "%");
			System.out.println("================================================================");
			System.out.println("==== Estat�sticas simula��o ====");
			System.out.println("================================================================");
			System.out.println("Tempo da simula��o: " + tempoSistema + " micro segundos");
			
			int [] cont = new int[3];
			for(int j = 0; j < qtdeTarefas; j++) {
				if(listaTarefasFinalizadas[j].getPolitica() == POLITICA1)
					cont[0]++;
				if(listaTarefasFinalizadas[j].getPolitica() == POLITICA2)
					cont[1]++;
				if(listaTarefasFinalizadas[j].getPolitica() == POLITICA3)
					cont[2]++;
			}
			System.out.println(cont[0] + " tarefas executadas localmente, no dispositivo local");
			System.out.println(cont[1] + " tarefas executadas localmente, no servidor local");
			System.out.println(cont[2] + " tarefas executadas remotamente, na Cloud");
			System.out.println("================================================================");
			System.out.println("==== Dispositivos IoT ====");
			System.out.println("================================================================");
			for(int j = 0; j < qtdeIoTDevices; j++) {
				System.out.println(listaIoTDevices[j].getId() + " - N�vel de bateria: " + listaIoTDevices[j].getNivelBateria());
			}
			
			System.out.println("================================================================");
			System.out.println("==== Tarefas ====");
			System.out.println("================================================================");
			int aux = 0;
			for(int j = 0; j < qtdeTarefas; j++) {
				if(listaTarefasFinalizadas[j].getDeadline() == -1)
					aux++;
			}
			
			System.out.println("Qtde tarefas normais: " + aux);
			System.out.println("Qtde tarefas cr�tica: " + (qtdeTarefas-aux));
			aux = 0;
			for(int j = 0; j < qtdeTarefas; j++) {
				if(listaTarefasFinalizadas[j].getStatusTarefa() == TAREFA_CONCLUIDA)
					aux++;
			}
			System.out.println("Qtde tarefas conclu�das: " + aux);
			System.out.println("Qtde tarefas canceladas: " + (qtdeTarefas-aux));
			System.out.println("================================================================");
			System.out.println("==== Energia e Tempo ====");
			System.out.println("================================================================");
			System.out.println("Energia total consumida");
			System.out.println("Tempo m�dio de execu��o");
			
			if(Boolean.FALSE)
				System.exit(0);
			*/
			//System.exit(0);
		}
		}
		}
		}

	}
	
	
	/* Impress�o de dados */
	
	/* Imprime no arquivo a tupla 
	 * [Tempo; Pol�tica; Status Finaliza��o, Energia CPU, Energia transmiss�o, Tempo CPU, Tempo transmiss�o, Custo]
	 * 
	 * 0 Tempo 							: � o tempo no qual a tarefa foi encerrada no sistema.
	 * 1 Pol�tica 						: � a pol�tica de aloca��o escolhida. Pode ser 1, 2 ou 3.
	 * 2 Status Finaliza��o				: � o status de finaliza��o da tarefa. Pode ser status conclu�do ou cancelado. 
	 * 3 Energia CPU					: Energia din�mica consumida para execu��o na CPU
	 * 4 Energia transmiss�o de dados	: Energia consumida para realizar as transmiss�es de dados
	 * 5 Tempo Execu��o CPU				: Tempo de execu��o para processamento na CPU
	 * 6 Tempo transmiss�o de dados		: Tempo decorrido nas transmiss�es de dados 
	 * 7 Custo							: Custo
	 * 
	 * */
	public static void imprimeTempoFinalizacaoVsPoliticaVsFinalizacao(String filename, Task[] tarefasFinalizadas, 
			double fatorEnergia, double fatorTempo) throws IOException {
		
		String tipoTeste = "imprimeTempoFinalizacaoVsPoliticaVsFinalizacao.txt";
		filename = filename + "-" + tipoTeste;
		
		// Octet<Tempo; Pol�tica; Status Finaliza��o, Energia CPU, Energia transmiss�o, Tempo CPU, Tempo transmiss�o, Custo>
		List<Octet<Long, String, String, Long, Long, Long, Long, Long>> listOctet = 
				new ArrayList<Octet<Long, String, String, Long, Long, Long, Long, Long>>();
		
		for(int i = 0; i < tarefasFinalizadas.length; i++) {
			String politica;
			if(tarefasFinalizadas[i].getPolitica() == POLITICA1)
				politica = "POLITICA1";
			else if(tarefasFinalizadas[i].getPolitica() == POLITICA2)
				politica = "POLITICA2";
			else
				politica = "POLITICA3";
			
			String statusFinalizacao;
			if(tarefasFinalizadas[i].getStatusTarefa() == TAREFA_CONCLUIDA)
				statusFinalizacao = "TAREFA_CONCLUIDA";
			else
				statusFinalizacao = "TAREFA_CANCELADA";
			
			Octet<Long, String, String, Long, Long, Long, Long, Long> octet = 
					new Octet<Long, String, String, Long, Long, Long, Long, Long>
					( 
						(long) (tarefasFinalizadas[i].getTempoBase() + tarefasFinalizadas[i].getTempoTotalDecorrido()),
						politica, 
						statusFinalizacao, 
						(long) tarefasFinalizadas[i].getEnergiaExecucao(), 
						(long) tarefasFinalizadas[i].getEnergiaTransmissaoDados(), 
						tarefasFinalizadas[i].getTempoExecucao(), 
						tarefasFinalizadas[i].getTempoTransmissaoDados(), 
						(long) (fatorEnergia*tarefasFinalizadas[i].getEnergiaTotalConsumida() + fatorTempo*tarefasFinalizadas[i].getTempoTotalDecorrido())
					);
			
			listOctet.add(octet);
		}
		
		// Ordena a lista de tuplas pelo tempo de finaliza��o das tarefas
		listOctet.sort(null);
		
		String header = "Tempo;Politica;Status Finalizacao;Energia CPU;Energia Transmissoes;Tempo CPU;Tempo Transmissoes;Custo\n";
		imprimeOctetoParaArquivo(filename, header, listOctet);
	}
	
	
	/* Imprime Octeto
	 * 
	 * 0 Tempo 							: � o tempo no qual a tarefa foi encerrada no sistema.
	 * 1 Pol�tica 						: � a pol�tica de aloca��o escolhida. Pode ser 1, 2 ou 3.
	 * 2 Status Finaliza��o				: � o status de finaliza��o da tarefa. Pode ser status conclu�do ou cancelado. 
	 * 3 Energia CPU					: Energia din�mica consumida para execu��o na CPU
	 * 4 Energia transmiss�o de dados	: Energia consumida para realizar as transmiss�es de dados
	 * 5 Tempo Execu��o CPU				: Tempo de execu��o para processamento na CPU
	 * 6 Tempo transmiss�o de dados		: Tempo decorrido nas transmiss�es de dados 
	 * 7 Custo							: Custo
	 * 
	 * */
	public static void imprimeOctetoParaArquivo(String filename, String header, 
			List<Octet<Long, String, String, Long, Long, Long, Long, Long>> listOctet) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	    
		writer.write(header);
	    for(Octet<Long, String, String, Long, Long, Long, Long, Long> octet : listOctet) {
			writer.write(octet.getValue0() + ";" + octet.getValue1() + ";" + octet.getValue2() + ";" +
						 octet.getValue3() + ";" + octet.getValue4() + ";" + octet.getValue5() + ";" + 
						 octet.getValue6() + ";" + octet.getValue7() + "\n");
	    }
	    writer.close();
	}
	
}
