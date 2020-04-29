package main;

/* Classe para defini��o dos par�metros da aplica��o que ser� simulada.
 * 
 * */

public class Application {

	private String id;
	
	private long taxaGeracao; 				// Em micro segundos
	private long entradaDados;				// Em bits
	private long resultados;				// Em bits
	private long cargaComputacional;		// Em ciclos de CPU
	private double percentualCriticas;		// Em percentual - entre 0 e 1
	private long deadlineCriticas;			// Em micro segundos
	private int qtdeTarefas;				
	
	/* Construtor
	 * 
	 * */
	public Application(String id, long taxaGeracao, long entradaDados, long resultados, long cargaComputacional, double percentualCriticas, long deadlineCriticas) {
		
		this.id = id;
		
		this.taxaGeracao = taxaGeracao;
		this.entradaDados = entradaDados;
		this.resultados = resultados;
	    this.cargaComputacional = cargaComputacional;
	    
	    if(this.percentualCriticas > 1 || this.percentualCriticas < 0) {
	    	System.out.println("TestApp " + id + ": percentualCriticas fora dos limites");
	    	System.exit(0);
	    }
		
	    this.percentualCriticas = percentualCriticas;
		this.deadlineCriticas = deadlineCriticas;
		this.qtdeTarefas = 0;
	}
	
	
	/* Getters */
	public String getId() {
		return this.id;
	}
	
	public long getTaxaGeracao() {
		return this.taxaGeracao;
	}

	public long getEntradaDados() {
		return this.entradaDados;
	}
	
	public long getResultados() {
		return resultados;
	}
	
	public double percentualCriticas() {
		return this.percentualCriticas;
	}

	public long getDeadlineCriticas() {
		return this.deadlineCriticas;
	}
	
	public int getQtdeTarefas() {
		return this.qtdeTarefas;
	}
	
	public long getCargaComputacional() {
		return cargaComputacional;
	}
	
	
	/* Setters */
	public void setQtdeTarefas(int qtdeTarefas) {
		this.qtdeTarefas = qtdeTarefas;
	}
	
	
	/* Define se a tarefa � cr�tica ou n�o
	 * - Essa fun��o deve ser chamada toda vez que uma tarefa � criada.
	 * - Ao chamar a aplica��o ir� definir se a nova tarefa � cr�tica ou n�o,
	 * baseado no percentual de tarefas cr�ticas.
	 * */
	public boolean defineSeTarefaCritica(int numTarefa) {
		if((int) ((numTarefa+1) * this.qtdeTarefas * this.percentualCriticas) % this.qtdeTarefas == 0) {
			return Boolean.TRUE; // Tarefa � cr�tica
		}
		else {
			return Boolean.FALSE; // Tarefa n�o � cr�tica
		}
	}
	
}
