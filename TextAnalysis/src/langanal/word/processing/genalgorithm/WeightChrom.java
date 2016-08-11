package langanal.word.processing.genalgorithm;

import java.util.Random;

public class WeightChrom implements Comparable{
	public int id;	//Population specific id
	public double fitness; //fitness of chromosome
	public double relFitness; //roulette

	/**
	 * Holds the chromosome's weights in an array
	 * 
	 * Order:
	 * [0] = definitionWeight
	 * [1] = partofspeechWeight
	 * [2] = synonymWeight
	 * [3] = antonymWeight
	 */
	private float[] weights = new float[11];

	/**
	 * Creates a new WeightChrom with the defined ID and weights
	 * @param id identification number of chromosome. Should be unique
	 * @param definitionWeight
	 * @param partofspeechWeight
	 * @param synonymWeight
	 * @param antonymWeight
	 */
	WeightChrom(int id, float definitionWeight, float partofspeechWeight, float synonymWeight, float antonymWeight,
			float exampleWeight, float synonymDefWeight, float antonymDefWeight, float defDefWeight, 
			float isSynWeight, float isAntWeight, float degreeOfSeparationWeight
			){
		this.id = id;
		weights[0] = definitionWeight;
		weights[1] = partofspeechWeight;
		weights[2] = synonymWeight;
		weights[3] = antonymWeight;
		weights[4] = exampleWeight;
		weights[5] = synonymDefWeight;
		weights[6] = antonymDefWeight;
		weights[7] = defDefWeight;
		weights[8] = isSynWeight;
		weights[9] = isAntWeight;
		weights[10] = degreeOfSeparationWeight;
	}

	/**
	 * Creates a new WeightChrom with randomly generated weights
	 * @param id
	 */
	WeightChrom(int id){
		Random rand = new Random();
		this.id = id;
		for(int x = 0;x<10;x++){
			weights[x] = rand.nextFloat()*10;
		}
	}

	WeightChrom(int id, WeightChrom other){
		this.id = id;
		this.weights = other.getWeights().clone();
	}

	public float getDefWeight(){	return this.weights[0];	}
	public float getPOSWeight(){	return this.weights[1];	}
	public float getSynWeight(){	return this.weights[2];	}
	public float getAntWeight(){	return this.weights[3];	}
	public float getExampleWeight(){	return this.weights[4];	}
	public float getSynonymDefWeight(){	return this.weights[5];	}
	public float getAntonymDefWeight(){	return this.weights[6];	}
	public float getDefDefWeight(){	return this.weights[7];	}
	public float getIsSynWeight(){	return this.weights[8];	}
	public float getIsAntWeight(){	return this.weights[9];	}
	public float getDegreeOfSeparation(){	return this.weights[10];	}

	public float[] getWeights(){	return this.weights;	}

	@Override
	public String toString(){
		return "{Chromosome: [" + this.id + 
				"] | Fitness: [" + this.fitness + 
				"] | relFitness: [" + this.relFitness +
				"] | Def: [" + this.weights[0] + 
				"] | POS: [" + this.weights[1] + 
				"] | Syn: [" + this.weights[2] +
				"] | Ant: [" + this.weights[3] +
				"] | Exa: [" + this.weights[4] +
				"] | SynDef: [" + this.weights[5] +
				"] | AntDef: [" + this.weights[6] +
				"] | DefDef: [" + this.weights[7] +
				"] | IsSyn: [" + this.weights[8] +
				"] | IsAnt: [" + this.weights[9] +
				"] | Deg: [" + this.weights[10] +
				"]}";
	}

	/**
	 * Compares by fitness
	 */
	@Override
	public int compareTo(Object o) {
		WeightChrom other = (WeightChrom) o;
		if(this.fitness > other.fitness){	return 1;	}
		if(this.fitness == other.fitness){	return 0;	}
		else{	return -1;	}
	}
}
