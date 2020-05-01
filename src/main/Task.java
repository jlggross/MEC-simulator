package main;

public class Task {

	// Variáveis fixas
	private String idTask;			
	private String idDevice;			// IoT Device that created the task
	private long deadline;				// In micro seconds
										/* If task is non-critical, then the deadline is -1 */
	private long baseTime;				/* System time when the task was created. 
	 									It is important to check this time stamp and compare it to the
	 									deadline while processing the task. */

	private long computationalLoad; 	// In CPU cycles
	private long dataEntrySize;			// In bits
	private long returnDataSize;		// In bits
	
	private double energyExecution;		// In W*micro-seconds		
	private double energyTransfer;		// In W*micro-seconds
	private long timeExecution;			// In micro seconds
	private long timeTransfer;			// In micro seconds
	
	private static int TASK_ALIVE = 1; 		// Task being allocated and executed
	private static int TASK_CONCLUDED = 2;	// Concluída. Se tarefa com deadline, terminou antes do deadline
	private static int TASK_CALCELLED = 3;	// Cancelada. Dispositivo ficou sem bateria ou tarefa finalizou depois do deadline
	private int taskStatus;		
	
	private static int POLICY1_IOT = 1;
	private static int POLICY2_MEC = 2;
	private static int POLICY3_CLOUD = 3;
	private int policy; 				// Indica qual a politica de escalonamento adotada
										/* Setado na fase de escalonamento, durante a simulação.
										 * Pode conter os valores 1, 2 e 3, para processamento local no device,
										 * processamento local no servidor MEC ou processamento remoto na Cloud. */
	
	/* Constructor
	 * 
	 * */
	public Task(String idTarefa, String idDevice, long deadline, long tempoBase, long cargaComputacional, long tamanhoEntrada, long tamanhoRetorno) {
		this.idTask = idTarefa;
		this.idDevice = idDevice;
		this.deadline = deadline;
		this.baseTime = tempoBase;
		this.computationalLoad = cargaComputacional;
		this.dataEntrySize = tamanhoEntrada;
		this.returnDataSize = tamanhoRetorno;
		
		this.energyExecution = 0;
		this.energyTransfer = 0;
		this.timeExecution = 0;
		this.timeTransfer = 0;
		this.taskStatus = TASK_ALIVE;
	}
	
	
	/* Getters */
	public String getIdTarefa() {
		return this.idTask;
	}
	
	public String getIdDeviceGerador() {
		return this.idDevice;
	}
	
	public int getStatusTarefa() {
		return this.taskStatus;
	}
	
	public long getDeadline() {
		return deadline;
	}
	
	public long getTempoBase() {
		return baseTime;
	}
	
	public long getCargaComputacional() {
		return computationalLoad;
	}
	
	public long getTamanhoEntrada() {
		return dataEntrySize;
	}

	public long getTamanhoRetorno() {
		return returnDataSize;
	}
	
	public int getPolitica() {
		return policy;
	}
	
	/* Retorna energia total consumida pela tarefa
	 * 
	 * */
	public double getEnergiaTotalConsumida() {
		return this.energyExecution + this.energyTransfer;
	}

	public double getEnergiaExecucao() {
		return energyExecution;
	}
	
	public double getEnergiaTransmissaoDados() {
		return energyTransfer;
	}
	
	
	/* Retorna tempo total decorrido até finalização da tarefa
	 * 
	 * */
	public long getTempoTotalDecorrido() {
		return this.timeExecution + this.timeTransfer;
	}
	
	public long getTempoExecucao() {
		return timeExecution;
	}
	
	public long getTempoTransmissaoDados() {
		return timeTransfer;
	}

	
	/* Setters */
	public void setEnergiaExecucao(double energiaExecucao) {
		this.energyExecution = energiaExecucao;
	}

	public void setEnergiaTransmissaoDados(double energiaTransmissaoDados) {
		this.energyTransfer = energiaTransmissaoDados;
	}
	
	/* Indica quanto tempo a tarefa necessita desde a criação até a finalização, para
	 * a política de alocação selecionada. Considera o tempo de processamento + os tempos de
	 * transmissão de dados
	 * */
	public void setTempoExecucao(double tempoExecucao) {
		this.timeExecution = (long) tempoExecucao;
	}
	
	public void setTempoTransmissaoDados(double tempoTransmissaoDados) {
		this.timeTransfer = (long) tempoTransmissaoDados;
	}
	
	public void setPolitica(int politica) {
		if(politica != POLICY1_IOT && politica != POLICY2_MEC && politica != POLICY3_CLOUD) {
			System.out.println(this.getIdTarefa() + " - setPolitica() : política de alocação diferente de 1, 2 ou 3");
			System.exit(0);
		}
					
		this.policy = politica;
	}
	
	
	
	
	/* Verifica se tarefa deve ser finalizada
	 * - Se o tempoBase (tempo de sistema no qual foi criado a tarefa) mais o tempo 
	 * gasto nas transferências de dados e na execução forem iguais ao tempo atual do sistema,
	 * então a tarefa deve finalizar. Caso contrário a tarefa não é finalizada.
	 * 
	 * */
	public boolean verificaSeTarefaDeveFinalizar(long tempoSistema) {
		if(this.taskStatus != TASK_ALIVE) {
			System.out.println("ERRO - função verificaSeTarefaDeveFinalizar : " + this.idTask + " já está finalizada");
			System.exit(0);
		}
		
		long tempoParaFinalizacao = this.baseTime + this.getTempoTotalDecorrido();
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
			this.taskStatus = TASK_CONCLUDED;
		else if(tempoSistema < (this.baseTime + this.deadline))
			this.taskStatus = TASK_CONCLUDED;
		else {
			this.taskStatus = TASK_CALCELLED;
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
