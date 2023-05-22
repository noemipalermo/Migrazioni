package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulator {

	// STATO DEL SISTEMA e OUTPTU
	private Map<Country, Integer> stanziali;
	
	
	// PARAMETRI SIMULAZIONE
	private Graph<Country, DefaultEdge> graph; // input dall'esterno che non modifichiamo
	private int nPersone = 1000; // numero di persone da simulare
	private Country partenza; // Stato da cui parto
	
	// OUTPUT
	private int nPassi; 
	
	
	// CODA DEGLI EVENTI
	private PriorityQueue<Event> queue;


	public Simulator(Graph<Country, DefaultEdge> graph, Country partenza) {
		super();
		this.graph = graph;
		this.partenza = partenza;
		
		nPassi=0;
		stanziali = new HashMap<Country, Integer>();
		for(Country c : this.graph.vertexSet()) { // Metto nella mappa tutti i country che ho nel grafo con valore di stanziali a 0
			stanziali.put(c, 0);
		}
		queue = new PriorityQueue<>();
	}
	
	
	public int getnPassi() {
		return nPassi;
	}


	public void initialize() {
		this.queue.add(new Event(0, this.partenza, this.nPersone));
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e);
			Country destinazione = e.getDestinazione();
			int time = e.getTime();
			int dimensione = e.getDimensione();
			
			if(time>this.nPassi) {
				this.nPassi=time;
			}
			List<Country> vicini = Graphs.neighborListOf(this.graph, destinazione);
			int migranti = dimensione/2/vicini.size();
			
			System.out.println(destinazione.getStateAbb()+" ha "+ vicini.size()+" confinanti");

			// dimensione/2 si dividono negli stati adiacenti e generano eventi INGRESSO con la quota di personeù
			// numero di adiacenti = grado del vertice di partenza
			// i rimanenti diventano stanziali nello stato 'destinazione'
			
			if(migranti>0) {
				for(Country c: vicini) {
					this.queue.add(new Event(time+1, c, migranti));
				}
			}
			//i rimandenti diventano stanziali nello stato 'destinazione'
			int nuoviStanziali = dimensione-migranti * vicini.size();
			this.stanziali.put(destinazione, this.stanziali.get(destinazione)+nuoviStanziali);
			
			
		}
	}

	public Map<Country, Integer> getStanziali() {
		
		return stanziali;
	}
	
	
}
