package genetic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Nature<T> {
	private final GeneticAlgorithmFunction<T> natureFunctions;
	/** Maximum generation depth. */
	private final int generationSize;
	private final int populationSize;
	private final int genomeSize;
	private final double crossoverRate;
	private final double mutationRate;
	private final boolean keepBest;
	private final Random rng;

	private List<Genome<T>> currentGeneration;
	private List<Genome<T>> nextGeneration;

	private double totalFitness;
	private List<Double> cummulativeFitness;

	/* Threading */
	private final int numThreads = Runtime.getRuntime().availableProcessors();
	private ExecutorService pool;

	private class PopulationFitnessTester implements Runnable {
		private final GeneticAlgorithmFunction<T> natureFunctions;
		private final List<Genome<T>> genomes;

		public PopulationFitnessTester(
				GeneticAlgorithmFunction<T> natureFunctions,
				List<Genome<T>> genomes) {
			this.natureFunctions = natureFunctions;
			this.genomes = genomes;
		}

		@Override
		public void run() {
			for (Genome<T> genome : genomes) {
				genome.setFitness(natureFunctions.fitness(genome));
			}
		}
	}

	public Nature(GeneticAlgorithmFunction<T> natureFunctions,
			int generationSize, int populationSize, int genomeSize,
			double crossoverRate, double mutationRate, boolean keepBest) {
		super();
		this.natureFunctions = natureFunctions;
		this.generationSize = generationSize;
		this.populationSize = populationSize;
		this.genomeSize = genomeSize;
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
		this.keepBest = keepBest;

		currentGeneration = new ArrayList<Genome<T>>(populationSize);
		nextGeneration = new ArrayList<Genome<T>>(populationSize);
		cummulativeFitness = new ArrayList<Double>();

		pool = Executors.newFixedThreadPool(numThreads);

		rng = new Random();
	}

	/**
	 * Creates genome for every population in the current generation.
	 */
	public void createGenomes() {
		currentGeneration.clear();

		for (int i = 0; i < populationSize; i++) {
			Genome<T> genome = natureFunctions.randomGenome(genomeSize);
			currentGeneration.add(genome);
		}

	}

	public void rankAndSortPopulations() {
		totalFitness = 0;

		Collection<Callable<Object>> tasks = new LinkedList<Callable<Object>>();
		int stepSize = populationSize / numThreads;

		for (int i = 0; i < populationSize; i += stepSize) {

			List<Genome<T>> subList = new ArrayList<Genome<T>>();
			if (i + stepSize >= populationSize) {
				subList = currentGeneration.subList(i, populationSize - 1);
			} else {
				subList = currentGeneration.subList(i, i + stepSize);
			}

			tasks.add(Executors.callable(new PopulationFitnessTester(
					natureFunctions, subList)));
		}

		/* Make sure all the threads are done by this point. */
		try {
			for (Future<?> f : pool.invokeAll(tasks)) {
				f.get();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		for (Genome<T> genome : currentGeneration) {
			totalFitness += genome.getFitness();
		}

		Collections.sort(currentGeneration, new Genome.GenomeComparer<T>());

		double fitnessCount = 0;
		cummulativeFitness.clear();

		for (Genome<T> genome : currentGeneration) {
			fitnessCount += genome.getFitness();
			cummulativeFitness.add(fitnessCount);
		}

	}

	private int rouletteSelection() {
		double randomFitness = rng.nextDouble() * totalFitness;

		int idx = -1;
		int mid;
		int first = 0;
		int last = populationSize - 1;
		mid = (last - first) / 2;

		while (idx == -1 && first <= last) {
			if (randomFitness < cummulativeFitness.get(mid)) {
				last = mid;
			} else if (randomFitness > cummulativeFitness.get(mid)) {
				first = mid;
			}
			mid = (first + last) / 2;
			// lies between i and i+1
			if ((last - first) == 1)
				idx = last;
		}

		return idx;
	}

	public void createNextGeneration() {
		nextGeneration.clear();
		Genome<T> best = null;

		if (keepBest) {
			/* The best is the last in the sorted list. */
			best = currentGeneration.get(currentGeneration.size() - 1);
		}

		/* TODO: check if this leads to multiple mixing */
		for (int i = 0; i < populationSize; i += 2) {
			int i1 = rouletteSelection();
			int i2 = rouletteSelection();

			Genome<T> parent1, parent2, child1, child2;
			parent1 = currentGeneration.get(i1);
			parent2 = currentGeneration.get(i2);

			/* FIXME: deep copy? */
			child1 = new Genome<T>(new ArrayList<T>(parent1.getGenes()));
			child2 = new Genome<T>(new ArrayList<T>(parent2.getGenes()));

			if (rng.nextDouble() < crossoverRate) {
				parent1.crossOver(parent2, child1, child2);
			} else {

				child1 = parent1;
				child2 = parent2;
			}

			child1.mutate(mutationRate);
			child2.mutate(mutationRate);

			nextGeneration.add(child1);
			nextGeneration.add(child2);

		}

		if (keepBest && best != null) {
			nextGeneration.set(0, best);
		}

		currentGeneration.clear();

		for (int i = 0; i < populationSize; i++) {
			currentGeneration.add(nextGeneration.get(i));
		}
	}

	/**
	 * Starts the evolution.
	 */
	public Genome<T> evolve() {
		/* Start with random genome. */
		createGenomes();
		rankAndSortPopulations();

		for (int i = 0; i < generationSize; i++) {
			if (i % 10 == 0) {
				System.out.println("Best fitness: "
						+ currentGeneration.get(currentGeneration.size() - 1)
								.getFitness());
				System.out.println("Starting on generation " + i);
			}

			createNextGeneration();
			rankAndSortPopulations();
		}

		/* Print the results. */
		System.out.print(currentGeneration);

		System.out.println("\nBest result: "
				+ currentGeneration.get(currentGeneration.size() - 1)
				+ " with score "
				+ currentGeneration.get(currentGeneration.size() - 1)
						.getFitness());

		return currentGeneration.get(currentGeneration.size() - 1);
	}

}
