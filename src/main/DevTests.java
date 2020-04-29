package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;
import org.javatuples.Quartet;

public class DevTests {

	public static void main(String args[]) throws IOException {
		
		if(Boolean.FALSE) {
			Random aleatorio = new Random();
		    for(int i = 1; i < 100; i++) {
		    	long valor = aleatorio.nextInt((int) (10*Math.pow(10, 6)));
		    	System.out.println("Número gerado: " + valor);
		    }
		}
	    
		if(Boolean.FALSE) {
		    // Tempos em micro segundos
		    long taxaGeracaoTarefas = 100;
		    long tempobase = 10;
		    		    
		    long tempoSistema = 0;
		    for(int i = 0; i < 500; i++) {
			    // Geração em 10
			    // Geração em 110
			    // Geração em 210
			    // Geração em 310		    	
		    	System.out.println("tempoSistema: " + tempoSistema);
		    	if(((tempoSistema - tempobase) % taxaGeracaoTarefas) == 0)
		    		System.out.println("Gera tarefa");
		    	
		    	tempoSistema++;
		    }
		}
		
		if(Boolean.FALSE) {
			int contadorTarefasCriticas = 0;
			int qtdeTarefas = 500;
			float percentualCriticas = (float) 0.1;
			
			int a = 100 % 50;
			
			for(int i = 0; i < qtdeTarefas; i++) {
				int aux = (int) ((i+1)*qtdeTarefas*percentualCriticas) % qtdeTarefas;
				if(aux == 0) {
					System.out.println("Tarefa-" + i + " é crítica.");
					contadorTarefasCriticas++;
				}
				else
					System.out.println("Tarefa-" + i + " não é crítica.");
			}
			
			System.out.println("Qtde tarefas críticas " + contadorTarefasCriticas);
		}
	    
		// Tuple test
		if(Boolean.FALSE) {
			List<Pair<Long, Double>> paresFreqTensao = new ArrayList<Pair<Long, Double>>();
			paresFreqTensao.add(new Pair<Long, Double> ((long) (1 * Math.pow(10, 6)), 1.8));
			paresFreqTensao.add(new Pair<Long, Double> ((long) (2 * Math.pow(10, 6)), 2.3));
			paresFreqTensao.add(new Pair<Long, Double> ((long) (4 * Math.pow(10, 6)), 2.7));
			paresFreqTensao.add(new Pair<Long, Double> ((long) (8 * Math.pow(10, 6)), 4.0));
			paresFreqTensao.add(new Pair<Long, Double> ((long) (16 * Math.pow(10, 6)), 5.0));
			
			List<Quartet<Double, Double, Double, Long>> costList = new ArrayList<Quartet<Double, Double, Double, Long>>();
					
			long cargaComputacional = (long) (2 * Math.pow(10, 6));
			double capacitancia = (double) (12 * Math.pow(10, -12));
			
			double fatorEnergia, fatorTempo;
			fatorEnergia = 0.3;
			fatorTempo = 1 - fatorEnergia;			
			for(Pair<Long, Double> pair : paresFreqTensao) {
				double potencia = (double) (capacitancia * Math.pow(pair.getValue1(), 2) * pair.getValue0());
				double tempoExec = (double) (cargaComputacional / (double) pair.getValue0());
				double energiaDin = potencia * tempoExec;
				double custo = fatorEnergia * energiaDin + fatorTempo * tempoExec;
				
				System.out.println("PotenciaDin: " + potencia + "; tempoExec: " + tempoExec + "; energiaDin: " + energiaDin);
				costList.add(new Quartet<Double, Double, Double, Long> (custo, energiaDin, tempoExec, pair.getValue0()));
			}
			
			costList.sort(null);
			for(Quartet<Double, Double, Double, Long> quartet : costList) {
				System.out.println(quartet);
			}	
		}
	    
		// Split test
		if(Boolean.FALSE) {
			String s = "Tarefa-135";
			String [] a = s.split("-");
			
			System.out.println("a[1]: " + a[1]);
			
			String f = s.split("-")[1];
			System.out.println("f: " + f);
			
			int id = Integer.parseInt(s.split("-")[1]);
			System.out.println("id: " + id);
			
			for(String i : a)
				System.out.println("i: " + i);
		}
		
		// Write file
		if(Boolean.TRUE) {
			String filename = "out.txt";
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		    writer.write("Legumes;Preço;Qtde"+"\n");
		    writer.write("Batata" + ";" + 10.0 + ";" + 2 + "\n");
		    writer.write("Cenoura" + ";" + 9.0 + ";" + 10 + "\n");
		    writer.write("Tomate" + ";" + 8.0 + ";" + 7 + "\n");
		     
		    writer.close();
		}
		
	}	
	
	
}



