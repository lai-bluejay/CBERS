package cn.ac.lai.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import cn.ac.lai.utils.JournalProfile;

public class Journal {
	private DBConnect dbcon; 
	private final String tableName = "journal";
	
	public Journal(DBConnect dbcon) {
		this.dbcon = dbcon; 
	}
	
	public int getID(String issn) {
		int id = -1; 
		final String sql = "SELECT id FROM " + this.tableName + " WHERE UPPER(TRIM(issn)) = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, issn.trim().toUpperCase()); 
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return id; 
	}
	
	public String getName(int id) {
		String name = null; 
		final String sql = "SELECT name FROM " + this.tableName + " WHERE id = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, id); 
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				name = rs.getString(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return name; 
	}
	
	public String getName(String issn) {
		String name = null; 
		final String sql = "SELECT name FROM " + this.tableName + " WHERE UPPER(TRIM(issn)) = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, issn.trim().toUpperCase()); 
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				name = rs.getString(1); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return name; 
	}
	
	public JournalProfile get(int id) {
		JournalProfile profile = null; 
		
		final String sql = "SELECT name, issn, impact_factor FROM " + this.tableName + " WHERE id = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, id); 
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				profile = new JournalProfile(rs.getString("name"), rs.getString("issn"), 
						rs.getDouble("impact_factor")); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return profile; 
	}
	
	public int insert(String name, String issn) {
		int id = getID(issn);
		if (id != -1) {
			return id; 
		}
		
		final String sql = "INSERT INTO " + this.tableName + " (name, issn) VALUES (?, ?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS); 
		try {
			pstmt.setString(1, name.trim());
			pstmt.setString(2, issn.trim().toUpperCase());
			pstmt.executeUpdate(); 
			
			ResultSet rsKey = pstmt.getGeneratedKeys(); 
			rsKey.next(); 
			id = rsKey.getInt(1); 
			pstmt.close(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return id; 
	}
	
	public void insert(FileInputStream input) {
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			
			StringBuilder html = new StringBuilder(); 
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				html.append(nextLine); 
			}
			
			String regEx = "<dt><strong>\\d+\\.(.+?)<br>";
			Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			
			for (Matcher m = pattern.matcher(html); m.find(); ) {
				String[] parts = m.group(1).split("</strong></dt>.+ISSN:\\s"); 
				
				insert(parts[0].replaceAll("&amp;", "&"), parts[1]); 
			} 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	// temp function
	public void print(FileInputStream input, PrintStream out) {
		Map<String, JournalProfile> list = new HashMap<String, JournalProfile>(); 
		
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				String[] parts = nextLine.split("\t"); 
				
				list.put(parts[1], new JournalProfile(parts[0], parts[1], Double.parseDouble(parts[2]))); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Map.Entry<String, JournalProfile> entry: list.entrySet()) {
			out.println(entry.getValue()); 
		}
	}
	
	public void insert(FileInputStream input, int sid) {
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			
			StringBuilder html = new StringBuilder(); 
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				html.append(nextLine); 
			}
			
			String regEx = "<dt><strong>\\d+\\.(.+?)<br>";
			Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			
			for (Matcher m = pattern.matcher(html); m.find(); ) {
				String[] parts = m.group(1).split("</strong></dt>.+ISSN:\\s"); 
				
				int jid = insert(parts[0].replaceAll("&amp;", "&"), parts[1]); 
				JournalSubject js = new JournalSubject(this.dbcon);
				js.insert(jid, sid); 
			} 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void updateImpactFactor(String issn, double impactFactor) {
		final String sql = "UPDATE " + this.tableName + " SET impact_factor = ? WHERE UPPER(TRIM(issn)) = ?"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setDouble(1, impactFactor);
			pstmt.setString(2, issn.trim().toUpperCase());
			pstmt.executeUpdate(); 
			
			pstmt.close(); 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public void updateImpactFactor(FileInputStream input, String sheetName) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(input);
			workbook.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL); 
			HSSFSheet sheet = workbook.getSheet(sheetName);
			
			// skip first 4 row
			for (int rn = sheet.getFirstRowNum() + 4; rn <= sheet.getLastRowNum(); rn++) {
				Row row = sheet.getRow(rn); 
				if (row == null) {
					continue; 
				}
				
				Cell cellIssn = row.getCell(2); 
				String issn = (cellIssn != null)? cellIssn.getStringCellValue().trim().toUpperCase(): null; 
				
				Cell cellImpactFactor = row.getCell(4); 
				double impactFactor = (cellImpactFactor != null)? cellImpactFactor.getNumericCellValue(): 0.0; 
				
				if (issn != null && !issn.equals("")) {
					updateImpactFactor(issn, impactFactor); 
				}
			}
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	public List<JournalProfile> getJournalList(int sid) {
		List<JournalProfile> list = new ArrayList<JournalProfile>(); 
		
		JournalSubject js = new JournalSubject(this.dbcon); 
		List<Integer> ids = js.getJournalID(sid); 
		for (Integer id: ids) {
			list.add(get(id)); 
		}
		
		return list; 
	}
	
	public void print(PrintStream out) {
		final String sql = "SELECT name, issn, impact_factor FROM " + this.tableName 
			+ " ORDER BY impact_factor DESC"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				out.println(rs.getString("name") + "\t" + rs.getString("issn") + "\t" + rs.getDouble("impact_factor")); 
			} 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public void print(PrintStream out, int topN) {
		final String sql = "SELECT name, issn, impact_factor FROM " + this.tableName 
			+ " ORDER BY impact_factor DESC limit 0, ?"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, topN); 
			
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				out.println(rs.getString("name") + "\t" + rs.getString("issn") + "\t" + rs.getDouble("impact_factor")); 
			} 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public void print(PrintStream out, int topN, int sid) {
		final String sql = "SELECT j.name name, j.issn issn, j.impact_factor impact_factor FROM " + this.tableName 
			+ " j, journal_subject js WHERE j.id = js.jid AND js.sid = ? ORDER BY impact_factor DESC limit 0, ?"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setInt(1, sid); 
			pstmt.setInt(2, topN); 
		
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				out.println(rs.getString("name") + "\t" + rs.getString("issn") + "\t" + rs.getDouble("impact_factor")); 
			} 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		DBConnect dbcon = new DBConnect(); 
		dbcon.openDB();
		
		Journal jour = new Journal(dbcon);
		
		/*
		String[] fnames = {
				"data/isi/ENGINEERING, CHEMICAL.htm", 
				"data/isi/ENDOCRINOLOGY & METABOLISM.htm", 
				"data/isi/CHEMISTRY, PHYSICAL.htm", 
				"data/isi/CHEMISTRY, ORGANIC.htm",
				"data/isi/CHEMISTRY, MULTIDISCIPLINARY.htm",
				"data/isi/CHEMISTRY, MEDICINAL.htm",
				"data/isi/CHEMISTRY, APPLIED.htm",
				"data/isi/POLYMER SCIENCE.htm",
				"data/isi/PHARMACOLOGY & PHARMACY.htm",
				"data/isi/TOXICOLOGY.htm",
				"data/isi/BIOCHEMISTRY & MOLECULAR BIOLOGY.htm"
		}; 
		
		for (int i = 0; i < fnames.length; i++) {
			Subject sub = new Subject(dbcon); 
			int beginIndex = fnames[i].lastIndexOf('/') + 1; 
			int endIndex = fnames[i].lastIndexOf('.'); 
			String subjectName = fnames[i].substring(beginIndex, endIndex); 
			int sid = sub.getID(subjectName); 
			jour.insert(new FileInputStream(fnames[i]), sid); 
		}
		*/
		
		// update impact factor
		//jour.updateImpactFactor(new FileInputStream("data/isi/2013-JCR2012-SCI-IF.xls"), "Sheet1"); 
		
		// print journal list
		PrintStream out = new PrintStream("data/isi/journal-list-tmp.txt"); 
		for (int id = 1; id < 12; id++) {
			jour.print(out, 100, id);
		}
		
		jour.print(new FileInputStream("data/isi/journal-list-tmp.txt"), 
				new PrintStream("data/isi/journal-list.txt")); 
		
		System.out.println("done."); 
		dbcon.closePSTMT(); 
	}
}
