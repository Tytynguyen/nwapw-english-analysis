package langanal.word.processing.genalgorithm;

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
	private final float multiplier = 10; //# to multiply random floats by

	private final Random rand = new Random();

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

		public static float definitionWeightBest = 9.928f;
		public static float POSWeightBest = 9.679f;
		public static float synonymsWeightBest = 7.289f;
		public static float antonymsWeightBest = 7.647f;

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
			currentPopulation.add(ind, new WeightChrom(ind,rand.nextFloat()*multiplier,rand.nextFloat()*multiplier,rand.nextFloat()*multiplier,rand.nextFloat()*multiplier));
		}
		return this.currentPopulation;
	}

	/**
	 * Runs the genetic algorithm for specific number of generations.
	 * @param maxGenerations Max number of generations
	 * @return Weight chromosomes sorted by worse fitness to best fitness
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<WeightChrom> runAlgorithm(int maxGenerations){
		this.newPopulation = new ArrayList<WeightChrom>();
		for(int gen = 0; gen<maxGenerations ; gen++){
			System.out.println("Generation: " + gen);
			this.newPopulation.clear();

			/**
			 * Roulette
			 */
			System.out.println("calculating fitness...");
			this.currentPopulation = calcFitness(currentPopulation, testWords, testRelevant);
			
			System.out.println("POPULATION: " + currentPopulation);
			System.out.println("BEST CHROM: " + currentPopulation.get(currentPopulation.size()-1));

			for(int newPop = 0; newPop<this.populationSize; newPop+=2){

				//Chrom1
					float chromRoulette1 = rand.nextFloat();
					WeightChrom chrom1 = new WeightChrom(newPop,currentPopulation.get(0).getDefWeight(),currentPopulation.get(0).getPOSWeight(),
							currentPopulation.get(0).getSynWeight(),currentPopulation.get(0).getAntWeight());
					for(WeightChrom cur : currentPopulation){
						if(cur.relFitness < chromRoulette1){
							chrom1 = new WeightChrom(newPop,cur.getDefWeight(),cur.getPOSWeight(),cur.getSynWeight(),cur.getAntWeight());
						}
					}


					//chrom2
					float chromRoulette2 = rand.nextFloat();
					WeightChrom chrom2 = new WeightChrom(newPop+1,currentPopulation.get(0).getDefWeight(),currentPopulation.get(0).getPOSWeight(),
							currentPopulation.get(0).getSynWeight(),currentPopulation.get(0).getAntWeight());
					for(WeightChrom cur : currentPopulation){
						if(cur.relFitness < chromRoulette2){
							chrom2 = new WeightChrom(newPop+1,cur.getDefWeight(),cur.getPOSWeight(),cur.getSynWeight(),cur.getAntWeight());
						}
					}
					
					//In case they are the same
					while(chrom1.equals(chrom2)){
						float chromRoulette3 = rand.nextFloat();
						for(WeightChrom cur : currentPopulation){
							if(cur.relFitness < chromRoulette3){
								chrom2 = new WeightChrom(newPop+1,cur.getDefWeight(),cur.getPOSWeight(),cur.getSynWeight(),cur.getAntWeight());
							}
						}
					}
			
				/**
				 * Crossover
				 */
					float crossoverf = rand.nextFloat();
				if(crossoverf<this.crossoverRate){
					crossover(chrom1, chrom2);
				}

				/**
				 * Mutation
				 */
				for(int g = 0; g<chrom1.getWeights().length ; g++){
					float mutrand1 = rand.nextFloat();
					float mutrand2 = rand.nextFloat();
					if(mutrand1<this.mutationRate){
						mutate(chrom1, g);
					}
					if(mutrand2<this.mutationRate){
						mutate(chrom2, g);
					}
				}

				newPopulation.add(chrom1);
				newPopulation.add(chrom2);
			}
			this.currentPopulation = new ArrayList<WeightChrom>(newPopulation);
		}

		return currentPopulation;
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
		chromosome.getWeights()[gene] = rand.nextFloat()*multiplier;
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
	private ArrayList<WeightChrom> calcFitness(ArrayList<WeightChrom> populationToCalc, 
			ArrayList<ArrayList<LinkedList<Word>>> wordpairs, ArrayList<Boolean> relevant){

		ArrayList<WeightChrom> holderPop = new ArrayList<WeightChrom>(populationToCalc);
		//Sets the fitness of each chromosome to # of correct relevancy guesses they make.
		for(WeightChrom chrom : holderPop){
			int curPair = 0; //Stores the current pair num
			double testingd = 0;
			chrom.fitness = 0;
			chrom.relFitness = 0;

			for(ArrayList<LinkedList<Word>> pair : wordpairs){
				LinkedList<Word> word1 = pair.get(0);
				LinkedList<Word> word2 = pair.get(1);

				boolean isRelevant = relevant.get(curPair);

				float curChromRel = WordProcessing.compareWords(word1, word2, chrom.getDefWeight(), 
						chrom.getPOSWeight(), chrom.getSynWeight(), chrom.getAntWeight());
				testingd+=curChromRel;
				
				//If the chromosome's relevancy is within the bounds of our subjective data(Booleans)
				if(isRelevant && (curChromRel>85) || !isRelevant && (curChromRel<15)){
					chrom.fitness++;
				}else if(isRelevant && (curChromRel>50) || !isRelevant && (curChromRel<50)){
					chrom.fitness+= 0.5;
				}
				curPair++;
			}
			//System.out.println(testingd/250);
		}
		
		double lowest = Double.POSITIVE_INFINITY;
		double highest = Double.NEGATIVE_INFINITY;
		for(WeightChrom c : holderPop){
			System.out.print(" " + c.fitness);
			if(c.fitness < lowest){
				lowest = c.fitness;
			}
			if(c.fitness > highest){
				highest = c.fitness;
			}
		}
		System.out.println("LOW: " + lowest + "HIGH: " + highest);
		
		//Sets the fitness of each chromosome to its correct guesses / totalCorrect
		//Also stores them in a returnlist
		for(WeightChrom chrom : holderPop){
			chrom.fitness = (chrom.fitness / wordpairs.size());
		}
		
		Collections.sort(holderPop);

		System.out.println("\tcorrect/total" + holderPop);
		
		//Roulette wheel: sets the fitness of each chrom to fit within a 0 to 1 range, according to their probabilities
		double currentTotal = 0;	//stores the total to add onto the next chromosome
		//calculates currentTotal
		for(WeightChrom chrom : holderPop){
			currentTotal += chrom.fitness;
		}
		//Sets all relFitness to %
		double lastTotal = 0;
		for(WeightChrom chrom : holderPop){
			chrom.relFitness = (chrom.fitness/currentTotal) + lastTotal;
			lastTotal = chrom.relFitness;
		}
		
		System.out.println(holderPop);
		return holderPop;
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