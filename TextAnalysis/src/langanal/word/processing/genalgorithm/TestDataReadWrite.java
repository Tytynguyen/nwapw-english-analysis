package langanal.word.processing.genalgorithm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import langanal.word.base.Word;

public class TestDataReadWrite {

	private static final String filePrefix = "src/langanal/word/processing/genalgorithm/data/";

	/**
	 * Writes a file saving the test words and relevancy
	 * @param testWords LinkedList<Word>[][] of test words
	 * @return Object of words written to file
	 **/
	public static ArrayList<ArrayList<LinkedList<Word>>> writeTestWordFile(ArrayList<ArrayList<LinkedList<Word>>> testWords, ArrayList<Boolean> relevant, String filename){
		try {
			FileOutputStream f_out = new FileOutputStream(filePrefix + filename);
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

			obj_out.writeObject(testWords);
			obj_out.writeObject(relevant);

			obj_out.close();
			return testWords;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Reads a file of test data, and returns the test word pairs
	 * @param filename
	 * @return Pairs of test words
	 **/
	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<LinkedList<Word>>> readTestWordFileWords(String filename){
		try {
			FileInputStream f_in = new FileInputStream(filePrefix + filename);
			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			Object obj = obj_in.readObject();
			obj_in.close();

			if(obj instanceof ArrayList<?>){
				return (ArrayList<ArrayList<LinkedList<Word>>>) obj;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("ArrayList<LinkedList<Word>> not found in file!");
		return null;
	}

	/**
	 * Reads a file of test data, and returns the test relevance
	 * @param filename
	 * @return ArrayList of booleans whether the pair is relevant or not.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Boolean> readTestWordFileRelevant(String filename){
		try {
			FileInputStream f_in = new FileInputStream(filePrefix + filename);
			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			obj_in.readObject();	//skips words
			Object obj = obj_in.readObject();
			obj_in.close();

			if(obj instanceof ArrayList<?>){
				return (ArrayList<Boolean>) obj;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("int[] not found in file!");
		return null;
	}
}
