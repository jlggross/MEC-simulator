package main;

public class FiberOptics {

	double latencia;		//Em s
	double potencia;		//Em W
	long taxaTransferencia;	//Em bits/s
	
	public FiberOptics() {
		this.latencia = (double) (5 * Math.pow(10, -3));
		this.potencia = 3.65;
		this.taxaTransferencia = (long) Math.pow(10, 9);
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
	 * Retorno: W * micro-segundo  
	 * */
	public double calculaEnergiaConsumida(long tamanhoDados) {
		double energia;
		energia = this.potencia * this.calculaTempoTransmissao(tamanhoDados);
		return energia;
	}
	
}
