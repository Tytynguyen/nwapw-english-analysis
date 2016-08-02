package langanal.word.processing.genalgorithm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import langanal.word.base.Word;
import langanal.word.base.WordInfo;

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

	/**
	 * Reads a test data txt file, formats, and write a new .data file.
	 * @param readname
	 * @param writename
	 * @return Saved arraylist of pairs of test words
	 */
	public static ArrayList<ArrayList<LinkedList<Word>>> readWriteTestWordFileFromTXT(String readname, String writename){
		try {
			ArrayList<ArrayList<LinkedList<Word>>> returnWords = new ArrayList<ArrayList<LinkedList<Word>>>();
			ArrayList<Boolean> returnRelevant = new ArrayList<Boolean>();

			String line;
			int linenum = 0;

			BufferedReader in;

			in = new BufferedReader(new FileReader(filePrefix + readname));

			String[] splitLine;	//line split by " "

			//Read
			while((line = in.readLine()) != null){
				splitLine = line.split(" ");
				returnWords.add(new ArrayList<LinkedList<Word>>());
				
				System.out.println("Word 1: " + splitLine[0]);
				returnWords.get(linenum).add(WordInfo.getDictionaryWords(splitLine[0]));
				
				System.out.println("Word 2: " + splitLine[1]);
				returnWords.get(linenum).add(WordInfo.getDictionaryWords(splitLine[1]));
				
				returnRelevant.add(Boolean.parseBoolean(splitLine[2]));
				
				linenum++;
			}
			in.close();
			
			//Write
			writeTestWordFile(returnWords, returnRelevant, writename);
			return returnWords;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
