package langanal.word.processing.genalgorithm;

public class WeightChrom implements Comparable{
	public int id;	//Population specific id
	public float fitness; //fitness of chromosome

	/**
	 * Holds the chromosome's weights in an array
	 * 
	 * Order:
	 * [0] = definitionWeight
	 * [1] = partofspeechWeight
	 * [2] = synonymWeight
	 * [3] = antonymWeight
	 */
	private float[] weights = new float[4];

	/**
	 * Creates a new WeightChrom with the defined ID and weights
	 * @param id identification number of chromosome. Should be unique
	 * @param definitionWeight
	 * @param partofspeechWeight
	 * @param synonymWeight
	 * @param antonymWeight
	 */
	WeightChrom(int id, float definitionWeight, float partofspeechWeight, float synonymWeight, float antonymWeight){
		this.id = id;
		weights[0] = definitionWeight;
		weights[1] = partofspeechWeight;
		weights[2] = synonymWeight;
		weights[3] = antonymWeight;
	}

	public float getDefWeight(){	return this.weights[0];	}
	public float getPOSWeight(){	return this.weights[1];	}
	public float getSynWeight(){	return this.weights[2];	}
	public float getAntWeight(){	return this.weights[3];	}
	public float[] getWeights(){	return this.weights;	}

	@Override
	public String toString(){
		return "{Chromosome: " + this.id + " | Fitness: [" + this.fitness + 
				"] | Def: [" + this.weights[0] + 
				"] | POS: [" + this.weights[1] + 
				"] | Syn: [" + this.weights[2] +
				"] | Ant: [" + this.weights[3] +
				"]}";
	}

	@Override
	public int compareTo(Object o) {
		WeightChrom other = (WeightChrom) o;
		if(this.fitness > other.fitness){	return 1;	}
		if(this.fitness == other.fitness){	return 0;	}
		else{	return -1;	}
	}
}
