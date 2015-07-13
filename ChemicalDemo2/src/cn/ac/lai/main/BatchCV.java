package cn.ac.lai.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class BatchCV {
	
	public static void load(String fname) {
		
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				if (nextLine.trim().equals("")) {
					continue; 
				}
				String [] parts = nextLine.trim().split("\\s"); 
				
				//System.out.println(parts.length); 
				if (parts.length != 12) {
					throw new IllegalArgumentException("The format of file \"" + fname + "\" is wrong at #line: "+ nLine); 
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		int fold = 10; 
		int[] costs = {-3, -2, -1, 0, 1, 2, 3}; 
		String[] cases = {"without", "500", "1000", "1500", "2000"}; 
		//String[] cases = {"without"};
		for (String cs: cases) {
			String fname = "data/cv-" + cs + "train.bat"; 
			PrintStream out = new PrintStream(fname); 
			out.println("cd /d E:/workspace2/ChemicalDemo/data");
			for (int v = 0; v < fold; v++) {
				for (int c: costs) {
					out.println("@echo 2^" + c + " " + v);
					//train.bat
					out.println("crf_learn -f 2 -p 4 -c " + Math.pow(2, c) 
							+ " ./crf/PatentCorpus-" + cs + ".template"
							+ " ./crf/cv/" + cs + "/" + v + ".train"
							+ " ./crf/cv/" + cs + "/" + c+ "/" + v + ".model");
					//test.bat
					/*out.println("crf_test -v 1 -n 1" 
							+ " -m ./crf/cv/" + cs+ "/" + c+ "/" + v + ".model"
							+ " -o ./crf/cv/" + cs + "/" + c + "/" + v + ".out"
							+ " ./crf/cv/" + cs + "/" + v + ".test"); */
				}
			}
			out.println("pause");
			out.close(); 
		}
		
		System.out.println("done."); 
	}
}
