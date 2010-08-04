package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Special type of genome where all the mutations swap genes.
 * 
 * @author Ben Ruijl
 * 
 * @param <T>
 */
public class Genome<T> {
	private List<T> genes;
	private Random rng;
	private double fitness;

	public static class GenomeComparer<T> implements Comparator<Genome<T>> {

		@Override
		public int compare(Genome<T> arg0, Genome<T> arg1) {
			if (arg0.getFitness() > arg1.getFitness()) {
				return 1;
			} else {
				if (arg0.getFitness() < arg1.getFitness()) {
					return -1;
				}
			}

			return 0;
		}

	}
	
	/*public Genome(Genome<T> old) {
		genes = new ArrayList<T>(old.getGenes().size());
		
		for (T gene : old.getGenes()) {
			genes.add(new T(gene));
		}
	}*/

	public Genome(List<T> genes) {
		super();
		this.genes = genes;
		rng = new Random();
	}

	public List<T> getGenes() {
		return genes;
	}

	public void setGenes(List<T> genes) {
		this.genes = genes;
	}

	public void crossOver(Genome<T> genome2, Genome<T> child1, Genome<T> child2) {
		/* Randomly create a region to cross over. */
		int end = rng.nextInt(genes.size()) + 1;
		int start = rng.nextInt(end);
		
		for (int i = start; i < end; i++) {
			T prim = genes.get(i);
			T sec = genome2.getGenes().get(i);
			
			Collections.swap(child1.getGenes(), i, genes.indexOf(sec));
			Collections.swap(child2.getGenes(), i, genome2.getGenes().indexOf(prim));
		}
	}

	public void mutate(double mutationRate) {
		for (int i = 0; i < genes.size(); i++) {
			if (rng.nextDouble() < mutationRate) {
				Collections.swap(genes, i, rng.nextInt(genes.size()));
			}
		}
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public double getFitness() {
		return fitness;
	}
	
	@Override
	public String toString() {
		return genes.toString();
	}
	
}
