package mono;

import genetic.GeneticAlgorithmFunction;
import genetic.Genome;
import genetic.Nature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main implements GeneticAlgorithmFunction<Character> {
	Nature<Character> nature;

	public Main() {
		/* There are 26 characters in the alphabet, so that's the genome size. */
		nature = new Nature<Character>(this, 1000, 100, 26, 0.8, 0.00, true);
		nature.evolve();
	}
	
	/**
	 * This fitness test rates match between the genome and the order
	 * of the alphabet. 
	 * @param genome Genome
	 * @return Rate between 0 and 26
	 */
	public double fitnessTest(Genome<Character> genome) {
		double fitness = 0;
		for (int i = 0; i < genome.getGenes().size(); i++) {		
			fitness += genome.getGenes().get(i).charValue() == i + 97 ? 1 : 0;
		}
		
		return fitness;
	}
	
	@Override
	public double fitness(Genome<Character> genome) {
		double fitness = 0;
		for (int i = 0; i < genome.getGenes().size(); i++) {		
			fitness += genome.getGenes().get(i).charValue() == i + 97 ? 1 : 0;
		}
		
		return fitness;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		System.exit(0);
	}

	@Override
	public Genome<Character> randomGenome(int size) {
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		List<Character> genome = new ArrayList<Character>();
		
		for (int i = 0; i < alphabet.length(); i++) {
			genome.add(alphabet.charAt(i));
		}
		
		Collections.shuffle(genome);
		return new Genome<Character>(genome);
	}
}
