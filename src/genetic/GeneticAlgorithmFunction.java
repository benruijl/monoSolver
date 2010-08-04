package genetic;

public interface GeneticAlgorithmFunction<T> {
	public double fitness(Genome<T> genome);
	public Genome<T> randomGenome(int size);
}
