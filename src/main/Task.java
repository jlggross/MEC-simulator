package main;

public class Task {

	// Variáveis fixas
	private String idTarefa;			// Nome da tarefa
	private String idDevice;			// Origem da criação da tarefa
	private long deadline;				// Em micro segundos
										/* Se a tarefa não for crítica, então o deadline é -1 */
	private long tempoBase;				/* Tempo no sistema no qual a tarefa foi criada. 
	 									Importante considerar essa marcação de tempo para
	 									criação da tarefa para ir verificando o deadline.*/

	private long cargaComputacional; 	// Em ciclos de CPU
	private long tamanhoEntrada;		// Em bits
	private long tamanhoRetorno;		// Em bits
	
	private double energiaExecucao;			// Em W*micro-segundo		
	private double energiaTransmissaoDados;	// Em W*micro-segundo
	private long tempoExecucao;				// Em micro segundos
	private long tempoTransmissaoDados;		// Em micro segundos
	
	private static int TAREFA_VIVA = 1; 		// Em processo de alocação ou em execução
	private static int TAREFA_CONCLUIDA = 2;	// Concluída. Se tarefa com deadline, terminou antes do deadline
	private static int TAREFA_CANCELADA = 3;	// Cancelada. Dispositivo ficou sem bateria ou tarefa finalizou depois do deadline
	private int statusTarefa;		
	
	private static int POLITICA1 = 1;
	private static int POLITICA2 = 2;
	private static int POLITICA3 = 3;
	private int politica; 				// Indica qual a politica de escalonamento adotada
										/* Setado na fase de escalonamento, durante a simulação.
										 * Pode conter os valores 1, 2 e 3, para processamento local no device,
										 * processamento local no servidor MEC ou processamento remoto na Cloud. */
	
	/* Construtor
	 * 
	 * */
	public Task(String idTarefa, String idDevice, long deadline, long tempoBase, long cargaComputacional, long tamanhoEntrada, long tamanhoRetorno) {
		this.idTarefa = idTarefa;
		this.idDevice = idDevice;
		this.deadline = deadline;
		this.tempoBase = tempoBase;
		this.cargaComputacional = cargaComputacional;
		this.tamanhoEntrada = tamanhoEntrada;
		this.tamanhoRetorno = tamanhoRetorno;
		
		this.energiaExecucao = 0;
		this.energiaTransmissaoDados = 0;
		this.tempoExecucao = 0;
		this.tempoTransmissaoDados = 0;
		this.statusTarefa = TAREFA_VIVA;
	}
	
	
	/* Getters */
	public String getIdTarefa() {
		return this.idTarefa;
	}
	
	public String getIdDeviceGerador() {
		return this.idDevice;
	}
	
	public int getStatusTarefa() {
		return this.statusTarefa;
	}
	
	public long getDeadline() {
		return deadline;
	}
	
	public long getTempoBase() {
		return tempoBase;
	}
	
	public long getCargaComputacional() {
		return cargaComputacional;
	}
	
	public long getTamanhoEntrada() {
		return tamanhoEntrada;
	}

	public long getTamanhoRetorno() {
		return tamanhoRetorno;
	}
	
	public int getPolitica() {
		return politica;
	}
	
	/* Retorna energia total consumida pela tarefa
	 * 
	 * */
	public double getEnergiaTotalConsumida() {
		return this.energiaExecucao + this.energiaTransmissaoDados;
	}

	public double getEnergiaExecucao() {
		return energiaExecucao;
	}
	
	public double getEnergiaTransmissaoDados() {
		return energiaTransmissaoDados;
	}
	
	
	/* Retorna tempo total decorrido até finalização da tarefa
	 * 
	 * */
	public long getTempoTotalDecorrido() {
		return this.tempoExecucao + this.tempoTransmissaoDados;
	}
	
	public long getTempoExecucao() {
		return tempoExecucao;
	}
	
	public long getTempoTransmissaoDados() {
		return tempoTransmissaoDados;
	}

	
	/* Setters */
	public void setEnergiaExecucao(double energiaExecucao) {
		this.energiaExecucao = energiaExecucao;
	}

	public void setEnergiaTransmissaoDados(double energiaTransmissaoDados) {
		this.energiaTransmissaoDados = energiaTransmissaoDados;
	}
	
	/* Indica quanto tempo a tarefa necessita desde a criação até a finalização, para
	 * a política de alocação selecionada. Considera o tempo de processamento + os tempos de
	 * transmissão de dados
	 * */
	public void setTempoExecucao(double tempoExecucao) {
		this.tempoExecucao = (long) tempoExecucao;
	}
	
	public void setTempoTransmissaoDados(double tempoTransmissaoDados) {
		this.tempoTransmissaoDados = (long) tempoTransmissaoDados;
	}
	
	public void setPolitica(int politica) {
		if(politica != POLITICA1 && politica != POLITICA2 && politica != POLITICA3) {
			System.out.println(this.getIdTarefa() + " - setPolitica() : política de alocação diferente de 1, 2 ou 3");
			System.exit(0);
		}
					
		this.politica = politica;
	}
	
	
	
	
	/* Verifica se tarefa deve ser finalizada
	 * - Se o tempoBase (tempo de sistema no qual foi criado a tarefa) mais o tempo 
	 * gasto nas transferências de dados e na execução forem iguais ao tempo atual do sistema,
	 * então a tarefa deve finalizar. Caso contrário a tarefa não é finalizada.
	 * 
	 * */
	public boolean verificaSeTarefaDeveFinalizar(long tempoSistema) {
		if(this.statusTarefa != TAREFA_VIVA) {
			System.out.println("ERRO - função verificaSeTarefaDeveFinalizar : " + this.idTarefa + " já está finalizada");
			System.exit(0);
		}
		
		long tempoParaFinalizacao = this.tempoBase + this.getTempoTotalDecorrido();
		if(tempoParaFinalizacao == tempoSistema) {
			this.finalizaTarefa(tempoSistema);
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	
	/* Finaliza tarefa
	 * 
	 * */
	private void finalizaTarefa(long tempoSistema) {
		if(this.deadline == -1)
			this.statusTarefa = TAREFA_CONCLUIDA;
		else if(tempoSistema < (this.tempoBase + this.deadline))
			this.statusTarefa = TAREFA_CONCLUIDA;
		else {
			this.statusTarefa = TAREFA_CANCELADA;
		}
	}
	
	
	/* Verifica se a tarefa é crítica
	 * 
	 * */
	public boolean verificaSeTarefaCritica() {
		if(this.deadline == -1)
			return Boolean.FALSE;
		else
			return Boolean.TRUE;
	}

}
