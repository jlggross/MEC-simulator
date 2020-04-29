package main;

public class RAN_5G {

	double latencia;		//Em s
	double alpha;			//Em W
	double beta; 			//Em W
	long taxaTransferencia;	//Em bits/s
	
	/* Construtor
	 * 
	 * */
	public RAN_5G() {
		this.latencia = (double) (5 * Math.pow(10, -3));
		this.alpha = (double) (0.52 * Math.pow(10, -3));
		this.beta = (double) 3.86412; 
		this.taxaTransferencia = (long) Math.pow(10, 9);
	}
	
	
	/* Calcula potência da transmissão de dados 5G
	 * - taxaTransferencia : Em bits por segundo
	 * 
	 * retorno : em W
	 * */
	public double calculaPotencia() {
		double potencia;
		potencia = (double) (this.alpha * this.taxaTransferencia / Math.pow(10, 6) + this.beta);
		return potencia;
	}
	
	
	/* Calcula tempo da transmissão de dados 5G
	 * - tamanhoDados : Em bits
	 * 
	 * Retorno: Em micro segundos  
	 * */
	public double calculaTempoTransmissao(long tamanhoDados) {
		double tempoTransmissao;
		tempoTransmissao = (double) tamanhoDados / (double) this.taxaTransferencia; // Em segundos
		tempoTransmissao = tempoTransmissao * Math.pow(10, 6); // Em micro segundos
		return tempoTransmissao;
	}
	
	
	/* Calcula energia consumida durante a transferência de dados
	 * - tamanhoDados : Em bits
	 * 
	 * Retorno: Em W * micro-segundo
	 * */
	public double calculaEnergiaConsumida(long tamanhoDados) {
		double energia;
		energia = this.calculaPotencia() * this.calculaTempoTransmissao(tamanhoDados); // Em W * micro-segundo
		return energia;
	}
}
