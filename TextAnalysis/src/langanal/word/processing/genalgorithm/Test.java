package langanal.word.processing.genalgorithm;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
		//TestDataReadWrite.readWriteTestWordFileFromTXT("testdata.txt", "testdata.data");
		GeneticAlgorithm test = new GeneticAlgorithm();
		ArrayList<WeightChrom> finalList = test.runAlgorithm(1000);
		System.out.println("all: " + finalList);
		System.out.println("Best: " + finalList.get(finalList.size()-1));
		
	}

}
