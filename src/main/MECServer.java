package main;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class MECServer {

	private List<Pair<Long, Double>> paresFreqTensao = new ArrayList<Pair<Long, Double>>();

	private String id;					// Identificação do nodo
	private double capacitancia; 		// Capacitância da arquitetura
	private double potenciaIdle;		// Potência da CPU em idle	

	private List<Boolean> statusCPUs = new ArrayList<Boolean>();	// 20 CPUs
	private static boolean CPU_OCUPADA = Boolean.TRUE;
	private static boolean CPU_LIVRE = Boolean.FALSE;
	
	private static int MAX_CPUS = 20;
	
	/* Construtor
	 * 
	 * */
	public MECServer(String id) {
		this.id = id;
		this.capacitancia = (double) (1.8 * Math.pow(10, -9)); // Em Farads
		this.potenciaIdle = (double) (0.675); // Em W
		
		// Frequências de operação do Raspberry Pi 4 Model B		
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (600 * Math.pow(10, 6)), 0.8));
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (750 * Math.pow(10, 6)), 0.825));
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (1000 * Math.pow(10, 6)), 1.0));
		this.paresFreqTensao.add(new Pair<Long, Double> ((long) (1500 * Math.pow(10, 6)), 1.2));
		
		// Status das 20 CPUs do servidor MEC
		for(int i = 0; i < MAX_CPUS; i++)
			statusCPUs.add(CPU_LIVRE);
	}
	
	
	/* Getters */
	public String getId() {
		return id;
	}
	
	public List<Pair<Long, Double>> getParesFreqTensao() {
		return paresFreqTensao;
	}	

	public int getQuantidadeCPUsLivres() {
		int cont = 0;
		for(int i = 0; i < MAX_CPUS; i++) {
			if(statusCPUs.get(i) == CPU_LIVRE)
				cont++;
		}
		
		return cont;
	}
	
	
	
	/* Calcula potência dinâmica da CPU do servidor
	 * 
	 * Retorno: Em Watts
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
		tempo = (double) cargaComputacional / (double) frequenciaOperacao; // Resultado em segundos
		tempo = tempo * Math.pow(10, 6); // Resultado em micro segundos
		return tempo;
	}
	
	
	/* Calcula energia consumida
	 * 
	 * Retorno: Em W * micro-segundos
	 * */
	public double calculaEnergiaDinamicaConsumida(long frequenciaOperacao, double tensao, long cargaComputacional) {
		double energia;
		energia = this.calculaPotenciaDinamica(frequenciaOperacao, tensao) * this.calculaTempoExecucao(frequenciaOperacao, cargaComputacional); // Resultado em W * micro-segundo
		return energia;
	}	
	
	
	/* Verifica se há CPU livre
	 * - Retorna TRUE se houver uma CPU livre
	 * - Retorna FALSE se todas as CPUs estiverem ocupadas
	 * */
	public boolean verificaCPULivre() {
		for(boolean status : statusCPUs) {
			if(status == CPU_LIVRE)
				return Boolean.TRUE;
		}

		//System.out.println(id + "-verificaCPULivre() : Nenhuma CPUs livre.");
		return Boolean.FALSE;
	}
	
	
	/* Ocupa CPU para processar tarefa
	 * - Returna TRUE se a CPU foi ocupada
	 * - Retorna FALSE se todas as CPUs estão ocupadas
	 * */
	public boolean OcupaCPU() {
		if(this.verificaCPULivre() == Boolean.TRUE) {
			for(int i = 0; i < MAX_CPUS; i++) {
				if(statusCPUs.get(i) == CPU_LIVRE) {
					statusCPUs.remove(i);
					statusCPUs.add(CPU_OCUPADA);
					return Boolean.TRUE;
				}
			}
		}
		
		System.out.println(id + "-OcupaCPU() : Todas CPUs já estão ocupadas.");
		return Boolean.FALSE;
	}
	
	
	/* Libera CPU para uso. Deixa recurso disponível
	 * - Returna TRUE indicando que a CPU ocupada foi liberada
	 * - Retorna FALSE se todas as CPUs já estão livres
	 * */
	public boolean LiberaCPU() {
		for(int i = 0; i < MAX_CPUS; i++) {
			if(statusCPUs.get(i) == CPU_OCUPADA) {
				statusCPUs.remove(i);
				statusCPUs.add(CPU_LIVRE);
				return Boolean.TRUE;
			}
		}	
		
		System.out.println(id + "-LiberaCPU() : Todas CPUs já estão livres.");
		return Boolean.FALSE;
	}
}
