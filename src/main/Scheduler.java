package main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Octet;
import org.javatuples.Pair;
import org.javatuples.Septet;

public class Scheduler {
	
	// Objetos
	private Task task;
	private IoTDevice iotDevice;
	private MECServer serverMEC;
	private CloudDataCenter cloud;
	private RAN_5G transmission5G;
	private FiberOptics transmissionFiber;
	
	// Fatores de custo
	/* Estabelecem a rela��o de peso entre energia e tempo de processamento. 
	 * O somat�rio de ambos � igual a 1 */
	private double fatorEnergiaConsumida;		
	private double fatorTempoProcessamento;
	
	// Listas de custos
	/* - Vari�vel 0 : custo
	 * - Vari�vel 1 : energia din�mica CPU
	 * - Vari�vel 2 : energia transmiss�o de dados = ZERO
	 * - Vari�vel 3 : tempo de execu��o CPU
	 * - Vari�vel 4 : tempo transmiss�o de dados = ZERO
	 * - Vari�vel 5 : frequ�ncia de opera��o
	 * - Vari�vel 6 : tens�o de opera��o */
	private List<Septet<Double, Double, Double, Double, Double, Long, Double>> costListIoTDevice = 
			new ArrayList<Septet<Double, Double, Double, Double, Double, Long, Double>>();
	
	/* - Vari�vel 0 : custo
	 * - Vari�vel 1 : energia din�mica CPU
	 * - Vari�vel 2 : energia transmiss�o de dados
	 * - Vari�vel 3 : tempo de execu��o CPU
	 * - Vari�vel 4 : tempo transmiss�o de dados
	 * - Vari�vel 5 : frequ�ncia de opera��o
	 * - Vari�vel 6 : tens�o de opera��o */
	private List<Septet<Double, Double, Double, Double, Double, Long, Double>> costListMECServer = 
			new ArrayList<Septet<Double, Double, Double, Double, Double, Long, Double>>();
		
	/* - Vari�vel 0 : custo
	 * - Vari�vel 1 : energia din�mica CPU
	 * - Vari�vel 2 : energia transmiss�o de dados
	 * - Vari�vel 3 : tempo de execu��o CPU
	 * - Vari�vel 4 : tempo transmiss�o de dados
	 * - Vari�vel 5 : frequ�ncia de opera��o
	 * - Vari�vel 6 : tens�o de opera��o = ZERO */
	private List<Septet<Double, Double, Double, Double, Double, Long, Double>> costListCloud = 
			new ArrayList<Septet<Double, Double, Double, Double, Double, Long, Double>>();
	
	
	// Informa��es transfer�ncias de dados
	private double energia5GIda; 		// Considera a transfer�ncia dos dados de entrada
	private double tempo5GIda;			// Considera a transfer�ncia dos dados de entrada
	private double energia5GVolta;		// Considera a transfer�ncia dos resultados
	private double tempo5GVolta;		// Considera a transfer�ncia dos resultados
	private double energiaFibraIda; 	// Considera a transfer�ncia dos dados de entrada
	private double tempoFibraIda;		// Considera a transfer�ncia dos dados de entrada
	private double energiaFibraVolta;	// Considera a transfer�ncia dos resultados
	private double tempoFibraVolta;		// Considera a transfer�ncia dos resultados

	// Pol�ticas de aloca��o
	private static int POLITICA1 = 1;	// Indica processamento no dispositivo IoT
	private static int POLITICA2 = 2;	// Indica processamento no servidor MEC
	private static int POLITICA3 = 3;	// Indica processamento na Cloud 
	
	private static int TAREFA_NORMAL = -1;
	
	/* Construtor
	 * */
	public Scheduler(Task task, double fatorEnergia, double fatorTempo, double alpha, double beta, double gama) {
		this.task = task;
		this.iotDevice = new IoTDevice("dummy", 100);
		this.serverMEC = new MECServer("dummy");
		this.cloud = new CloudDataCenter("dummy");
		
		this.transmission5G = new RAN_5G();
		this.transmissionFiber = new FiberOptics();
		
		this.fatorEnergiaConsumida = fatorEnergia;		
		this.fatorTempoProcessamento = fatorTempo;
		
		// Calcula custos de transmiss�es de dados
		this.energia5GIda = this.transmission5G.calculaEnergiaConsumida(this.task.getTamanhoEntrada());
		this.tempo5GIda = this.transmission5G.calculaTempoTransmissao(this.task.getTamanhoEntrada());
		this.energia5GVolta = this.transmission5G.calculaEnergiaConsumida(this.task.getTamanhoRetorno());
		this.tempo5GVolta = this.transmission5G.calculaTempoTransmissao(this.task.getTamanhoRetorno());
		
		this.energiaFibraIda = this.transmissionFiber.calculaEnergiaConsumida(this.task.getTamanhoEntrada());
		this.tempoFibraIda = this.transmissionFiber.calculaTempoTransmissao(this.task.getTamanhoEntrada());
		this.energiaFibraVolta = this.transmissionFiber.calculaEnergiaConsumida(this.task.getTamanhoRetorno());
		this.tempoFibraVolta = this.transmissionFiber.calculaTempoTransmissao(this.task.getTamanhoRetorno());
		
		// Calcula custos
		/* Cada fator estabelece a prioriza��o de utilizar uma ou outra pol�tica aloca��o.
		 * O somat�rio dos fatores alpha, beta e game � igual a 1 */
		this.calculaCustosProcessamentoDevice(alpha);
		this.calculaCustosProcessamentoMEC(beta);
		this.calculaCustosProcessamentoCloud(gama);
	}
	
		
	/* Calcular custos para pol�tica de aloca��o da tarefa no device
	 * 
	 * */
	private void calculaCustosProcessamentoDevice(double alpha) {
		// Captura pares freq x tens�o do dispositivo IoT
		List<Pair<Long, Double>> paresFreqTensao = new ArrayList<Pair<Long, Double>>();
		paresFreqTensao = this.iotDevice.getParesFreqTensao();
				
		for(Pair<Long, Double> parFreqTensao : paresFreqTensao) {
			double tempoExec = this.iotDevice.calculaTempoExecucao(parFreqTensao.getValue0(), this.task.getCargaComputacional()); 
			double energiaDin = this.iotDevice.calculaEnergiaDinamicaConsumida(parFreqTensao.getValue0(), parFreqTensao.getValue1(), this.task.getCargaComputacional());
			double cost = (this.fatorEnergiaConsumida * energiaDin + this.fatorTempoProcessamento * tempoExec) * alpha;
			
			this.costListIoTDevice.add(
					new Septet<Double, Double, Double, Double, Double, Long, Double> 
					(cost, energiaDin, 0.0, tempoExec, 0.0, parFreqTensao.getValue0(), parFreqTensao.getValue1()));
		}
		
		this.costListIoTDevice.sort(null);
	}
	
	
	/* Calcular custos para pol�tica de aloca��o da tarefa no servidor MEC
	 * 
	 * */
	private void calculaCustosProcessamentoMEC(double beta) {
		// Captura pares freq x tens�o do servidor MEC
		List<Pair<Long, Double>> paresFreqTensao = new ArrayList<Pair<Long, Double>>();
		paresFreqTensao = this.serverMEC.getParesFreqTensao();
				
		for(Pair<Long, Double> parFreqTensao : paresFreqTensao) {
			double tempoExec = this.serverMEC.calculaTempoExecucao(parFreqTensao.getValue0(), this.task.getCargaComputacional()); 
			double energiaDin = this.serverMEC.calculaEnergiaDinamicaConsumida(parFreqTensao.getValue0(), parFreqTensao.getValue1(), this.task.getCargaComputacional());
			double energiaDinTotal = energiaDin + this.energia5GIda + this.energia5GVolta;
			double tempoExecTotal = tempoExec + this.tempo5GIda + this.energia5GVolta;
			
			double cost = (this.fatorEnergiaConsumida * energiaDinTotal + this.fatorTempoProcessamento * tempoExecTotal) * beta;
			this.costListMECServer.add(
					new Septet<Double, Double, Double, Double, Double, Long, Double> 
					(cost, energiaDin, (this.energia5GIda + this.energia5GVolta), 
							tempoExec, (this.tempo5GIda + this.energia5GVolta), 
							parFreqTensao.getValue0(), parFreqTensao.getValue1()));
		}
		
		this.costListMECServer.sort(null);
	}
	
	
	/* Calcular custos para pol�tica de aloca��o da tarefa na Cloud
	 * 
	 * */
	private void calculaCustosProcessamentoCloud(double gama) {
		// Calcula custo para frqu�ncia de opera��o padr�o
		long freqPadrao = this.cloud.getFreqPadrao();
		double tempoPadrao = this.cloud.calculaTempoExecucaoFreqPadrao(this.task.getCargaComputacional());
		double energiaPadrao = this.cloud.calculaEnergiaDinamicaFreqPadrao(this.task.getCargaComputacional());
		
		double energiaTotalPadrao = energiaPadrao + this.energia5GIda + this.energiaFibraIda + this.energiaFibraVolta + this.energia5GVolta;
		double tempoTotalPadrao = tempoPadrao + this.tempo5GIda + this.tempoFibraIda + this.tempoFibraVolta + this.tempo5GVolta;		
		
		double custoPadrao = (this.fatorEnergiaConsumida * energiaTotalPadrao + this.fatorTempoProcessamento * tempoTotalPadrao) * gama;
		this.costListCloud.add(
				new Septet<Double, Double, Double, Double, Double, Long, Double> 
				(custoPadrao, energiaPadrao, (this.energia5GIda + this.energiaFibraIda + this.energiaFibraVolta + this.energia5GVolta),
						tempoPadrao, (this.tempo5GIda + this.tempoFibraIda + this.tempoFibraVolta + this.tempo5GVolta),
						freqPadrao, 0.0));
		
		
		// Calcula custo para frqu�ncia de turbo boost
		long freqTurbo = this.cloud.getFreqTurboBoost();
		double tempoTurbo = this.cloud.calculaTempoExecucaoFreqTurboBoost(this.task.getCargaComputacional());
		double energiaTurbo = this.cloud.calculaEnergiaDinamicaFreqTurbo(this.task.getCargaComputacional());
		
		double energiaTotalTurbo = energiaTurbo + this.energia5GIda + this.energiaFibraIda + this.energiaFibraVolta + this.energia5GVolta;
		double tempoTotalTurbo = tempoTurbo + this.tempo5GIda + this.tempoFibraIda + this.tempoFibraVolta + this.tempo5GVolta;
		
		double custoTurbo = (this.fatorEnergiaConsumida * energiaTotalTurbo + this.fatorTempoProcessamento * tempoTotalTurbo) * gama;
		this.costListCloud.add(
				new Septet<Double, Double, Double, Double, Double, Long, Double>
				(custoTurbo, energiaTurbo, (this.energia5GIda + this.energiaFibraIda + this.energiaFibraVolta + this.energia5GVolta),
						tempoTurbo, (this.tempo5GIda + this.tempoFibraIda + this.tempoFibraVolta + this.tempo5GVolta),
						freqTurbo, 0.0));
		
		// Ordena custos
		this.costListCloud.sort(null);
	}

	
	/* Define qual o menor custo
	 * - flagIoTDevice : Se TRUE indica que a CPU do dispositivo IoT est� livre.
	 * Se FALSE, indica que est� ocupada.
	 * - flagMECServer : Se TRUE indica que alguma CPU de algum dos servidores MEC
	 * est� livre. Se FALSE, indica que todas as CPUs de todos os servidores MEC 
	 * est�o ocupadas.
	 * 
	 * Retorno:
	 * - Sexteto com o menor custo 
	 * */
	public Octet<Double, Double, Double, Double, Double, Long, Double, Integer> defineMenorCusto(boolean flagIoTDevice, boolean flagMECServer) {	
		// Comparador global de custos
		List<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>> globalCostList = 
				new ArrayList<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>>();
		
		// Pol�tica 1: Insere custos do dispositivo IoT para compara��o
		if(flagIoTDevice == Boolean.TRUE) {
			for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListIoTDevice) {
				globalCostList.add(septet.add(POLITICA1));
			}
		}
		
		// Pol�tica 2: Insere custos do servidor MEC para compara��o
		if(flagMECServer == Boolean.TRUE) {
			for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListMECServer) {
				globalCostList.add(septet.add(POLITICA2));
			}
		}
		
		// Pol�tica 3: Insere custos da Cloud para compara��o
		for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListCloud) {
			globalCostList.add(septet.add(POLITICA3));
		}
		
		// Confere tipo de tarefa
		// Para tarefa cr�tica, ordena��o por tempo
		// Para tarefa n�o-cr�tica, ordena��o por custo
		if(task.getDeadline() != TAREFA_NORMAL) {
			
			// � tarefa cr�tica. Resultado ordenado pelo menor tempo.
			List<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>> globalCostListCritica = 
					new ArrayList<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>>();
			for(Octet<Double, Double, Double, Double, Double, Long, Double, Integer> octet : globalCostList) {
				Octet<Double, Double, Double, Double, Double, Long, Double, Integer> aux = 
						new Octet<Double, Double, Double, Double, Double, Long, Double, Integer>
								((octet.getValue3()+octet.getValue4()), octet.getValue1(), octet.getValue2(), octet.getValue0(), 
										octet.getValue4(), octet.getValue5(), octet.getValue6(), octet.getValue7());
				globalCostListCritica.add(aux);
			}
			
			globalCostListCritica.sort(null);
			
			// Imprime elementos do sexteto
			if(Boolean.FALSE) this.imprimeListaOcteto(globalCostListCritica);
			
			// Altera ordem dos elementos 0 e 2 para manter o padr�o de custo na posi��o 0 e tmepo na posi��o 2
			Octet<Double, Double, Double, Double, Double, Long, Double, Integer> resultado = 
					new Octet<Double, Double, Double, Double, Double, Long, Double, Integer> (
							globalCostListCritica.get(0).getValue3(), globalCostListCritica.get(0).getValue1(), 
							globalCostListCritica.get(0).getValue2(), (globalCostListCritica.get(0).getValue0()-globalCostListCritica.get(0).getValue4()), 
							globalCostListCritica.get(0).getValue4(), globalCostListCritica.get(0).getValue5(),
							globalCostListCritica.get(0).getValue6(), globalCostListCritica.get(0).getValue7());
			
			return resultado; // Retorna elemento de menor tempo de execu��o
			
		} else {
			// � tarefa normal. Resultado ordenado pelo menor custo global.
			globalCostList.sort(null);

			// Imprime elementos do sexteto
			if(Boolean.FALSE) this.imprimeListaOcteto(globalCostList);
			
			return globalCostList.get(0); // Retorna elemento de menor custo
		}
	}
	

	/* Imprime sexteto
	 * 
	 * */
	private void imprimeListaOcteto(List<Octet<Double, Double, Double, Double, Double, Long, Double, Integer>> octetList) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		
		System.out.println("-------------------------------------------------");
		System.out.println("Conte�do do sexteto");
		
		if(task.getDeadline() != -1) {
			// Tarefa � cr�tica
			System.out.println(task.getIdTarefa() + " � cr�tica.");
		
			int i = 0;
			for(Octet<Double, Double, Double, Double, Double, Long, Double, Integer> octet : octetList) {
				
				/* Calcula se a diferen�a entre o tempo total de execu��o e o deadline  */
				long difTempo = (long) (task.getDeadline() - octet.getValue0());
				
				System.out.println("Tempo " + i + ": " + df.format(octet.getValue0()) + " �s; Energia CPU: " + df.format(octet.getValue1()) +
						" W*�s; Energia transmiss�es: " + df.format(octet.getValue2()) + " W*�s; Custo: " + df.format(octet.getValue3()) + 
						" �s; Diff Tempo: " + difTempo + " �s; Freq: " + octet.getValue5() + " Hz; Tens�o: " + octet.getValue6() + 
						" V; Pol�tica: " + octet.getValue7());
				i++;
			}
		}
		else {
			// Tarefa n�o � cr�tica
			int i = 0;
			
			System.out.println("Custo;Energia CPU;Energia transmiss�es;Tempo CPU;Tempo transmiss�es;Freq;Tens�o;Pol�tica");
			for(Octet<Double, Double, Double, Double, Double, Long, Double, Integer> octet : octetList) {
				/*
				System.out.println("Custo " + i + ": " + df.format(octet.getValue0()) + "; Energia CPU: " + df.format(octet.getValue1()) + 
						" W*�s; Energia transmiss�es: " + df.format(octet.getValue2()) + " W*�s; Tempo CPU: " + df.format(octet.getValue3()) + 
						" �s; Tempo transmiss�es: " + df.format(octet.getValue4()) + " �s; Freq: " + octet.getValue5() + " Hz; Tens�o: " + octet.getValue6() +
						" V; Pol�tica: " + octet.getValue7());
				*/
				System.out.println(df.format(octet.getValue0()) + ";" + df.format(octet.getValue1()) + ";" + 
								   df.format(octet.getValue2()) + ";" + df.format(octet.getValue3()) + ";" + 
								   df.format(octet.getValue4()) + ";" + octet.getValue5() + ";" + 
								   octet.getValue6() + ";" + octet.getValue7());
				i++;
			}
		}
	}
	
	
	/* Imprime custos
	 * 
	 * */
	public void imprimeCustos() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		
		System.out.println("-------------------------------------------------");
		System.out.println("Fator de custo de energia: " + this.fatorEnergiaConsumida);
		System.out.println("Fator de custo de tempo: " + this.fatorTempoProcessamento);
		System.out.println("-------------------------------------------------");
		System.out.println("Entrada de dados: " + this.task.getTamanhoEntrada() + " bits");
		System.out.println("Retorno de resultados: " + this.task.getTamanhoRetorno() + " bits");
		System.out.println("-------------------------------------------------");
		System.out.println("Energia 5G ida: " + this.energia5GIda + " W");
		System.out.println("Tempo 5G ida: " + this.tempo5GIda + " s");
		System.out.println("Energia 5G volta: " + this.energia5GVolta + " W");
		System.out.println("Tempo 5G volta: " + this.tempo5GVolta + " s");
		System.out.println("-------------------------------------------------");
		System.out.println("Energia Fibra ida: " + this.energiaFibraIda + " W");
		System.out.println("Tempo Fibra ida: " + this.tempoFibraIda + " s");
		System.out.println("Energia Fibra volta: " + this.energiaFibraVolta + " W");
		System.out.println("Tempo Fibra volta: " + this.tempoFibraVolta + " s");
		
		System.out.println("-------------------------------------------------");
		System.out.println("Custos para processamento local, no dispositivo: ");
		int i = 0;
		for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListIoTDevice) {
			System.out.println("Custo " + i + ": " + df.format(septet.getValue0()) + "; Energia CPU: " + df.format(septet.getValue1()) +
								"; Energia transmiss�es: " + septet.getValue2() + "; Tempo CPU: " + septet.getValue3() + 
								"; Tempo transmiss�es: " + septet.getValue4() + "; Freq: " + septet.getValue5() + "; Tens�o: " + septet.getValue6());
			i++;
		}
		
		System.out.println("-------------------------------------------------");
		System.out.println("Custos para processamento local, no servidor MEC: ");
		i = 0;
		for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListMECServer) {
			System.out.println("Custo " + i + ": " + df.format(septet.getValue0()) + "; Energia CPU: " + df.format(septet.getValue1()) +
					"; Energia transmiss�es: " + septet.getValue2() + "; Tempo CPU: " + septet.getValue3() + 
					"; Tempo transmiss�es: " + septet.getValue4() + "; Freq: " + septet.getValue5() + "; Tens�o: " + septet.getValue6());
			i++;
		}
		
		System.out.println("-------------------------------------------------");
		System.out.println("Custos para processamento remoto, na Cloud: ");
		i = 0;
		for(Septet<Double, Double, Double, Double, Double, Long, Double> septet : this.costListCloud) {
			System.out.println("Custo " + i + ": " + df.format(septet.getValue0()) + "; Energia CPU: " + df.format(septet.getValue1()) +
					"; Energia transmiss�es: " + septet.getValue2() + "; Tempo CPU: " + septet.getValue3() + 
					"; Tempo transmiss�es: " + septet.getValue4() + "; Freq: " + septet.getValue5() + "; Tens�o: " + septet.getValue6());
			i++;
		}
		
	}
}
