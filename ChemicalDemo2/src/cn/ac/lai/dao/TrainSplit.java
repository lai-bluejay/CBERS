package cn.ac.lai.dao;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.ac.lai.tools.CokusRandom;
import cn.ac.lai.tools.RandomSamplers;

public class TrainSplit {
	private DBConnect dbcon; 
	private final String tableName = "training_split";
	private int fold; 
	
	public TrainSplit(DBConnect dbcon, int fold) {
		this.dbcon = dbcon; 
		this.fold = fold; 
		
		if (size() == 0) {
			split(); 
		}
	}
	
	public TrainSplit(DBConnect dbcon) {
		this(dbcon, 10); 
	}
	
	private String insert(String fileID, int split) {
		final String sql = "INSERT INTO " + this.tableName + " (fileID, split) VALUES (?, ?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			int col = 1; 
			pstmt.setString(col++, fileID.trim().toUpperCase());
			pstmt.setInt(col++, split); 
			pstmt.executeUpdate(); 
			
			pstmt.close(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}

		return fileID.trim().toUpperCase(); 
	}
	
	public int size() {
		int count = 0; 
		
		final String sql = "SELECT count(*) FROM " + this.tableName; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return count; 
	}
	
	public int size(int split) {
		int count = 0; 
		
		final String sql = "SELECT count(*) FROM " + this.tableName + "WHERE split = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, split);
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return count; 
	}
	
	private void split() {
		TrainCorpus train = new TrainCorpus(this.dbcon); 
		String[] ids = train.getAllfileID(); 
		int[] splitperm = null; // permutation of the corpus used for splitting
		int[] splitstarts = null; //starting points of the corpus segments
		
		RandomSamplers rs = new RandomSamplers(new CokusRandom(56567651));
		splitperm = rs.randPerm(ids.length);
		splitstarts = new int[this.fold + 1];
		
		for (int p = 0; p <= this.fold; p++) {
			splitstarts[p] = Math.round(ids.length * (p / (float) this.fold));
		}
		
		for (int split = 0; split < this.fold; split++) {
			int Mtest = splitstarts[split + 1] - splitstarts[split];
			int mstart = splitstarts[split];
			
			for (int m = 0; m < Mtest; m++) {
				insert(ids[splitperm[m + mstart]], split); 
			}
		}
	}
	
	public String[] getTrainCorpus(int split) {
		List<String> ids = new ArrayList<String>(); 
		
		final String sql = "SELECT fileID FROM " + this.tableName + " WHERE split = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, split);
			
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				ids.add(rs.getString("fileID")); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return ids.toArray(new String[ids.size()]); 
	}
	
	public String[] getTestCorpus(int split) {
		List<String> ids = new ArrayList<String>(); 
		
		final String sql = "SELECT fileID FROM " + this.tableName + " WHERE split <> ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, split);
			
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				ids.add(rs.getString("fileID")); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return ids.toArray(new String[ids.size()]); 
	}
	
	public void printTrainFeatures(int split, PrintStream outInst, PrintStream outID, WordRepresentation brown) {
		TrainCorpus train = new TrainCorpus(this.dbcon);
		
		train.printFeatures(getTrainCorpus(split), outInst, outID, brown); 
	}
	
	public void printTestFeatures(int split, PrintStream outInst, PrintStream outID, WordRepresentation brown) {
		TrainCorpus train = new TrainCorpus(this.dbcon);
		train.printFeatures(getTestCorpus(split), outInst, outID, brown); 
	}
	
	public void printTestWithCEMFormat(int split, PrintStream out) {
		TrainAnnotation a = new TrainAnnotation(this.dbcon);
		
		a.print(out, getTestCorpus(split)); 
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		int fold = 10; 
		TrainSplit spliter = new TrainSplit(dbcon, fold); 
		int fileNum=90;
		
		// without
		for (int v = 0; v < fold; v++) {
			System.out.println(v); 

			spliter.printTrainFeatures(v, new PrintStream("data/crf/cv/without/" + v + ".train"), 
					new PrintStream("data/crf/cv/without/" + v + ".train-id"), null); 
			fileNum--;
			fileNum--;
			System.out.println("/cv/without/" + v + ".train and /cv/without/" + v + ".train-id "
					+ "have been created!!!"+fileNum+"files rest!!");
			
			spliter.printTestFeatures(v, new PrintStream("data/crf/cv/without/" + v + ".test"), 
					new PrintStream("data/crf/cv/without/" + v + ".test-id"), null); 
			fileNum--;
			fileNum--;
			System.out.println("/cv/without/" + v + ".test and /cv/without/" + v + ".test-id"
					+ " have been created!!!"+fileNum+"files rest!!");
			
			spliter.printTestWithCEMFormat(v, new PrintStream("data/crf/cv/without/" + v + ".test.cem")); 
			fileNum--;
			System.out.println("./cv/without/" + v + ".test.cem has been created!!"+fileNum+"files rest!!");
		}
		
		//String[] cases = {"500", "1000", "1500", "2000"}; 
		String[] cases = {"500"}; 
		for (String cs: cases) {
			WordRepresentation brown = new WordRepresentation("data/brown/" + cs + ".paths"); 
			for (int v = 0; v < fold; v++) {
				spliter.printTrainFeatures(v, 
						new PrintStream("data/crf/cv/" + cs + "/" + v + ".train"), 
						new PrintStream("data/crf/cv/" + cs + "/" + v + ".train-id"), 
						brown); 
				fileNum--;
				fileNum--;
				System.out.println("./cv/" + cs + "/" + v + ".train and ./cv/" + cs + "/" + v + ".train-id "
						+ "have been created!!!"+fileNum+"files rest!!");
				spliter.printTestFeatures(v, 
						new PrintStream("data/crf/cv/" + cs + "/" + v + ".test"), 
						new PrintStream("data/crf/cv/"+ cs + "/" + v + ".test-id"), 
						brown); 
				fileNum--;
				fileNum--;
				System.out.println("./cv/" + cs + "/" + v + ".test and ./cv/" + cs + "/" + v + ".test-id"
						+ " have been created!!!"+fileNum+"files rest!!");

			}
		}
		
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}
