package langanal.word.processing.genalgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import langanal.word.base.Word;
import langanal.word.processing.WordProcessing;

public class GeneticAlgorithm {
	private final int populationSize = 100;	//size of each population
	private final float crossoverRate = 0.7f;	//% of crossover
	private final float mutationRate = 0.2f;	//% of mutation
	private int lastID; //stores last used ID

	private final Random rand = new Random();

	private WeightChrom bestChrom;
	private WeightChrom worstChrom;

	//Stores the test word pairs. Usage:testWords[wordPair][first or second word in pair]
	private ArrayList<ArrayList<LinkedList<Word>>> testWords;
	private ArrayList<Boolean> testRelevant;

	private String defaultTestData = "testdata.data";

	private ArrayList<WeightChrom> currentPopulation;
	private ArrayList<WeightChrom> newPopulation;

	/**
	 *Stores manually set weights, which should be the best ones.
	 */
	public static class Weights {

		//Best weights

		public static float definitionWeightBest = 0.5f;
		public static float POSWeightBest = 0.2f;
		public static float synonymsWeightBest = 1f;
		public static float antonymsWeightBest = 1f;

	}

	GeneticAlgorithm(){
		initPopulation(this.populationSize);
		loadTestData("");
	}

	/**
	 * Initializes the first population of size 'populationSize'
	 * with randomly generated chromosomes with random weights that are 0<x<10 floats
	 * 
	 * @return Randomly generated population of WeightChrom in ArrayList
	 **/

	private ArrayList<WeightChrom> initPopulation(int popSize){
		this.currentPopulation = new ArrayList<WeightChrom>(popSize);
		for(int ind = 0;ind<popSize;ind++){
			//Adds a chrom to the population with random weights
			currentPopulation.add(ind, new WeightChrom(ind,rand.nextFloat()*10,rand.nextFloat()*10,rand.nextFloat()*10,rand.nextFloat()*10));
		}
		this.lastID = populationSize-1;
		return this.currentPopulation;
	}

	/**
	 * Runs the genetic algorithm for specific number of generations.
	 * @param maxGenerations Max number of generations
	 * @return Weight chromosomes sorted by worse fitness to best fitness
	 */
	public ArrayList<WeightChrom> runAlgorithm(int maxGenerations){
		for(int gen = 0; gen<maxGenerations ; gen++){
			this.newPopulation = new ArrayList<WeightChrom>();
			for(int newPop = 0; newPop<this.populationSize; newPop+=2){
				WeightChrom chrom1 = null;
				WeightChrom chrom2 = null;

				/**
				 * Roulette
				 */
				ArrayList<WeightChrom> sortedList = 
						calcFitness(this.currentPopulation, this.testWords, this.testRelevant);
				//Chrom1
				float chromRoulette = rand.nextFloat();
				for(WeightChrom cur : sortedList){
					if(cur.fitness > chromRoulette){
						chrom1 = cur;
					}
				}
				//chrom2
				chromRoulette = rand.nextFloat();
				for(WeightChrom cur : sortedList){
					if(cur.fitness > chromRoulette){
						chrom2 = cur;
					}
				}

				while(chrom1.id == chrom2.id){	//lazy
					chromRoulette = rand.nextFloat();
					for(WeightChrom cur : sortedList){
						if(cur.fitness > chromRoulette){
							chrom2 = cur;
						}
					}
				}

				/**
				 * Crossover
				 */
				if(rand.nextFloat()<this.crossoverRate){
					crossover(chrom1, chrom2);
				}

				/**
				 * Mutation
				 */
				for(int g = 0; g<chrom1.getWeights().length ; g++){
					if(rand.nextFloat()<this.mutationRate){
						mutate(chrom1, g);
					}
					if(rand.nextFloat()<this.mutationRate){
						mutate(chrom2, g);
					}
				}

				newPopulation.add(chrom1);
				newPopulation.add(chrom2);
			}
			this.currentPopulation = this.newPopulation;
		}
		Collections.sort(newPopulation);
		return newPopulation;
	}

	/**
	 * Crosses over two chromosomes' data, swapping a randomly selected weight
	 * @param first Chromosome to be crossed over
	 * @param second Other chromosome to be crossed over
	 * @return Integer of the gene that was crossed over. Check WeightChrom for key.
	 */
	private int crossover(WeightChrom first, WeightChrom second){
		int gene = this.rand.nextInt(first.getWeights().length);
		float holder = first.getWeights()[gene];

		first.getWeights()[gene] = second.getWeights()[gene];
		second.getWeights()[gene] = holder;
		return gene;
	}

	/**
	 * Mutates a chromosome at a random gene, giving it a random value
	 * @param chromosome WeightChrom to be mutated
	 * @param gene the gene to be mutated, see WeightChrom for key
	 * @return The gene's new value
	 */
	private float mutate(WeightChrom chromosome, int gene){
		chromosome.getWeights()[gene] = rand.nextFloat()*10;
		return chromosome.getWeights()[gene];
	}

	/**
	 * Calculates the fitness of each individual in a population
	 * @param population Population of WeightChrom to be calculated
	 * @param wordpairs The test words to be used
	 * @param relevant ArrayList of booleans whether the pairs relate
	 * @return a sorted roulette wheel of chromosomes
	 **/
	@SuppressWarnings("unchecked")
	private ArrayList<WeightChrom> calcFitness(ArrayList<WeightChrom> population, 
			ArrayList<ArrayList<LinkedList<Word>>> wordpairs, ArrayList<Boolean> relevant){
		int totalCorrect = 0;	//Stores the number of correct relevancy guesses for the entire population

		//Sets the fitness of each chromosome to # of correct relevancy guesses they make.
		for(WeightChrom chrom : population){
			int curPair = 0; //Stores the current pair num
			for(ArrayList<LinkedList<Word>> pair : wordpairs){

				LinkedList<Word> word1 = pair.get(0);
				LinkedList<Word> word2 = pair.get(1);

				boolean isRelevant = relevant.get(curPair);

				float curChromRel = WordProcessing.compareWords(word1, word2, chrom.getDefWeight(), 
						chrom.getPOSWeight(), chrom.getSynWeight(), chrom.getAntWeight());

				//If the chromosome's relevancy is within the bounds of our subjective data(Booleans)
				if(isRelevant && (curChromRel>50) || !isRelevant && (curChromRel<50)){
					chrom.fitness++;
					totalCorrect++;
				}
				curPair++;
			}
		}

		//Sets the fitness of each chromosome to its correct guesses / totalCorrect
		//Also stores them in a returnlist
		ArrayList<WeightChrom> returnList = new ArrayList<WeightChrom>();
		for(WeightChrom chrom : population){
			chrom.fitness = chrom.fitness / totalCorrect;
			returnList.add(chrom);
		}

		Collections.sort(returnList);

		//Roulette wheel: sets the fitness of each chrom to fit within a 0 to 1 range, according to their probabilities
		float currentTotal = 0f;	//stores the total to add onto the next chromosome

		for(WeightChrom chrom : returnList){
			chrom.fitness += currentTotal;
			currentTotal = chrom.fitness;
		}

		return returnList;
	}

	/**
	 * Loads test data into testWords and testRelevant
	 * @param filename filename in the data directory. Use "" to use default data 
	 */
	public ArrayList<ArrayList<LinkedList<Word>>> loadTestData(String filename){
		if(filename.equals("")){
			ArrayList<ArrayList<LinkedList<Word>>> holder = TestDataReadWrite.readTestWordFileWords(defaultTestData);
			this.testWords = holder;
			this.testRelevant = TestDataReadWrite.readTestWordFileRelevant(defaultTestData);
			return holder;
		}else{
			ArrayList<ArrayList<LinkedList<Word>>> holder = TestDataReadWrite.readTestWordFileWords(filename);
			this.testWords = holder;
			this.testRelevant = TestDataReadWrite.readTestWordFileRelevant(filename);
			return holder;
		}
	}
}