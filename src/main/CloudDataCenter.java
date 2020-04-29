package main;

public class CloudDataCenter {

	// Variáveis fixas
	private String id;					// Identificação do Data Center	
	private long freqPadrao;			// Em Hz
	private long freqTurboBoost;		// Em Hz
	
	
	/* Construtor
	 * 
	 * */
	public CloudDataCenter(String id) {
		this.id = id;
		
		this.freqPadrao = (long) (2.8 * Math.pow(10, 9));
		this.freqTurboBoost = (long) (3.9 * Math.pow(10, 9));
	}		
	
	
	/* Getters */
	public String getId() {
		return id;
	}
	
	public long getFreqPadrao() {
		return freqPadrao;
	}

	public long getFreqTurboBoost() {
		return freqTurboBoost;
	}
	
	
	/* Calcula energia dinâmica para frequência de operação padrão
	 * 
	 * */
	public double calculaEnergiaDinamicaFreqPadrao(long cargaComputacional) {
		double energiaDin;
		energiaDin = 13.85 * calculaTempoExecucaoFreqPadrao(cargaComputacional); // Em W*micro-segundo	
		return energiaDin;
	}
	
	
	/* Calcula energia dinâmica para frequência turbo boost
	 * 
	 * */
	public double calculaEnergiaDinamicaFreqTurbo(long cargaComputacional) {
		double energiaDin;
		energiaDin = 24.28 * calculaTempoExecucaoFreqTurboBoost(cargaComputacional); // Em W*micro-segundo	
		return energiaDin;
	}
	
	
	/* Calcula tempo de execução para frequência de operação padrão
	 * 
	 * */
	public double calculaTempoExecucaoFreqPadrao(long cargaComputacional) {
		double tempo;
		tempo = (double) cargaComputacional / (double) this.freqPadrao;	// Em segundos
		tempo = tempo * Math.pow(10, 6); // Em micro segundos
		return tempo;
	}
	
	
	/* Calcula tempo de execução para frequência turbo boost
	 * 
	 * */
	public double calculaTempoExecucaoFreqTurboBoost(long cargaComputacional) {
		double tempo;
		tempo = (double) cargaComputacional / (double) this.freqTurboBoost; // Em segundos	
		tempo = tempo * Math.pow(10, 6); // Em micro segundos
		return tempo;
	}
	
}
