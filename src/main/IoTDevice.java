package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

public class IoTDevice {

	// Variáveis fixas
	private List<Pair<Long, Double>> paresFreqTensao = new ArrayList<Pair<Long, Double>>();

	private String id;					// Identificação do nodo
	private double capacitancia; 		// Capacitância da arquitetura
	private double potenciaIdle;		// Potência da CPU em idle	
	private double nivelBateria;		// Em W * (micro segundo)	
	private double LIS;					// Em W * (micro segundo) - Limite Inferior de Segurança

	private long tempoBase;				// Em micro segundos
										/* Tempo no qual é criada a primeira tarefa. A partir daí
										cria novas tarefas em ciclos de tempo equivalentes à taxa
										de geração da aplicação. Por exemplo: O tempoBase é definido
										como 150 micro segundos, ou seja, o tempo global do sistema
										começa em zero e quando atingir 150 micro segundos uma tarefa
										é gerada. A próxima tarefa só será gerada após passado o tempo
										da taxa de geração da aplicação. Se a taxa de geração for 0,1
										segundos, então a primeira tarefa será gerada no tempoBase,
										ou seja, 150 micro segundos e a segunda tarefa gerada no 
										tempoBase + taxa de geração, ou 150 micro segundos + 10.000 
										micro segundos (0,1 s). */
	private long taxaGeracaoTarefas;	// Em micro segundos
										/* Indica qual a taxa de geração de novas tarefas, se de 1 em
										1 segundos, se de 0,1 em 0,1 segundos, etc. */
	
	
	private int statusCPU;				// Recebe os valores CPU_LIVRE ou CPU_OCUPADA
	private static int CPU_LIVRE = 1;
	private static int CPU_OCUPADA = 2;
	
	
	/* Construtor
	 * 
	 * */
	public IoTDevice(String id, long taxaGeracaoTarefas) {
		this.id = id;
		this.taxaGeracaoTarefas = taxaGeracaoTarefas;
		this.tempoBase = new Random().nextInt((int) this.taxaGeracaoTarefas) + 1; //Em micro segundos		
		
		this.capacitancia = (double) (2.2 * Math.pow(10, -9)); // Em Farads
		this.potenciaIdle = (double) (900 * Math.pow(10, -6)); // Em W
		this.nivelBateria = 36000 * Math.pow(10, 6); // 36000 Ws - Equivalente a 36000*10^6 W*micro-segundo, 10Wh ou 2000mAh a 5V
		this.LIS = nivelBateria * 0.1;
		
		// Frequências de operação do Arduino Mega 2560
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (1 * Math.pow(10, 6)), 1.8));
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (2 * Math.pow(10, 6)), 2.3));
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (4 * Math.pow(10, 6)), 2.7));
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (8 * Math.pow(10, 6)), 4.0));
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (16 * Math.pow(10, 6)), 5.0));
		
		this.statusCPU = CPU_LIVRE;
	}
	
	/* Getters */
	public String getId() {
		return this.id;
	}
	
	public long getTempobase() {
		return this.tempoBase;
	}
	
	public List<Pair<Long, Double>> getParesFreqTensao() {
		return paresFreqTensao;
	}
	
	public double getNivelBateria() {
		return this.nivelBateria;
	}
	
	
	/* Calcula potência dinâmica do dispositivo IoT
	 * 
	 * Retorno: Em W
	 * */
	public double calculaPotenciaDinamica(long frequenciaOperacao, double tensao) {
		double potencia;
		potencia = (double) (this.capacitancia * Math.pow(tensao, 2) * (double) frequenciaOperacao); // Em W		
		return potencia;
	}
	
	
	/* Calcula tempo de execução
	 * 
	 * Retorno: Em micro segundos
	 * */
	public double calculaTempoExecucao(long frequenciaOperacao, long cargaComputacional) {
		double tempo;
		tempo = (double) ((double) cargaComputacional / (double) frequenciaOperacao); // Resultado em segundos
		tempo = tempo * Math.pow(10, 6); // Resultado em micro segundos
		return tempo;
	}
	
	
	/* Calcula energia dinâmica consumida
	 * 
	 * Retorno: W * micro-segundo
	 * */
	public double calculaEnergiaDinamicaConsumida(long frequenciaOperacao, double tensao, long cargaComputacional) {
		double energia;
		energia = this.calculaPotenciaDinamica(frequenciaOperacao, tensao) * this.calculaTempoExecucao(frequenciaOperacao, cargaComputacional); // Resultado em W * micro-segundo
		return energia;
	}
	
	
	/* Calcula energia idle consumida
	 * - tempoEmIdle: em micro segundos
	 * 
	 * Retorno: W * micro-segundo 
	 * */
	public double calculaEnergiaIdleConsumida(double tempoEmIdle) {
		double energiaIdle;
		energiaIdle = this.potenciaIdle * tempoEmIdle;
		return energiaIdle;
	}
	
	
	/* Consumir bateria do dispositivo IoT
	 * - energiaConsumida em W*s
	 * */
	public void consomeBateria(double energiaConsumida) {
		this.nivelBateria = this.nivelBateria - energiaConsumida;
	}	
	
	
	/* Verifica se a CPU está livre
	 * - Retorna TRUE se a CPU estiver livre livre
	 * - Retorna FALSE se a CPU estiver ocupada
	 * */
	public boolean verificaCPULivre() {
		if(this.nivelBateria <= this.LIS) {
			System.out.println(this.id + " atingiu nível de bateria LIS.");
			return Boolean.FALSE;
		}
		if(this.statusCPU == CPU_LIVRE)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	
	/* Altera o status da CPU
	 * - novoStatus = 1 : CPU_LIVRE
	 * - novoStatus = 2 : CPU_OCUPADA
	 * */
	public void alteraStatusCPU(int novoStatus) {
		if(novoStatus != CPU_LIVRE && novoStatus != CPU_OCUPADA) {
			System.out.println(id + "-alteraStatusCPU() : novoStatus não é LIVRE nem OUCPADO.");
			System.exit(0);
		}
		
		this.statusCPU = novoStatus;
	}

}
