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
import java.util.List;

import opennlp.tools.util.Span;
import cn.ac.lai.utils.TextPolicy;
import cn.ac.lai.utils.AnnotatedEntity;

abstract class Annotation {
	protected DBConnect dbcon; 
	protected String tableName; 
	
	// @tableName: "training_annotation", or "testing_annotation"; 
	public Annotation(DBConnect dbcon, String tableName) {
		this.dbcon = dbcon; 
		this.tableName = tableName; 
	}
	
	abstract void check(String type); 
	
	public int insert(AnnotatedEntity entity) {
		int id = -1; 
		
		final String sql = "INSERT INTO " + this.tableName + " (fileID, type, start_offset, end_offset, cid, eid) VALUES (?, ?, ?, ?, ?, ?)"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS); 
		try {
			Entity e = new Entity(this.dbcon);
			EntityClass c = new EntityClass(this.dbcon);
			
			int col = 1; 
			pstmt.setString(col++, entity.getfileID().trim());
			pstmt.setString(col++, entity.getType().trim().toUpperCase());
			pstmt.setInt(col++, entity.getOffset().getStart()); 
			pstmt.setInt(col++, entity.getOffset().getEnd()); 
			pstmt.setInt(col++, c.insert(entity.getClassName()));
			pstmt.setInt(col++, e.insert(entity.getEntityName()));
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
	
	public int insert(String fileID, String type, String className, Span offset, String entityName) {
		AnnotatedEntity e = new AnnotatedEntity(fileID, type, className,offset, entityName);
		
		return insert(e); 
	}
	
	public int insert(String fileID, String type,String termID, String className, int startOffset, int endOffset, String entityName) {
		return insert(fileID, type, className, new Span(startOffset, endOffset), entityName); 
	}
	
	public void load(String fname) {
		int nLine = 0; 
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
			
			for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
				String [] parts = nextLine.trim().split("\t"); 
				
				if (parts.length != 5) {
					throw new IllegalArgumentException("The format of file \"" + fname + "\" is wrong at #line: "+ nLine); 
				}
				String [] offsetParts=parts[3].trim().split(" ");
				if(offsetParts.length==3){
				insert(parts[0], parts[1].trim(), parts[2].trim(), offsetParts[0].trim(),
						Integer.parseInt(offsetParts[1]), Integer.parseInt(offsetParts[2]), parts[4]); 
				}else{//分行错误，出现";"
					
					insert(parts[0], parts[1].trim(), parts[2].trim(), offsetParts[0].trim(),
							Integer.parseInt(offsetParts[1]), Integer.parseInt(offsetParts[offsetParts.length-1]), parts[4]);	
				}
				}
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	public List<AnnotatedEntity> getEntity(String fileID, TextPolicy policy) {
		List<AnnotatedEntity> entities = new ArrayList<AnnotatedEntity>(); 
		
		String sql = "SELECT a.type type, a.start_offset start_offset, a.end_offset end_offset, e.name entity_name, c.name class_name FROM " 
			+ this.tableName + " a, entity e, entity_class c WHERE a.eid = e.id AND a.cid = c.id AND UPPER(TRIM(fileID)) = ?"; 
		switch(policy) {

		case HARMONIZED: 
			sql += " AND UPPER(TRIM(type)) = ?"; 
			break; 
		case TRAINING: 
			sql += " AND UPPER(TRIM(type)) = ?"; 
			break; 
		}
		sql += " ORDER BY a.id asc"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		//Entity en = new Entity(this.dbcon); 
		//EntityClass enClass = new EntityClass(this.dbcon); 
		try {
			pstmt.setString(1, fileID.trim().toUpperCase());
			switch(policy) {
			case HARMONIZED: 
				pstmt.setString(2, "H");
				break; 
			case TRAINING: 
				pstmt.setString(2, "T");
				break; 
			}
			
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				
				AnnotatedEntity ae = new AnnotatedEntity(fileID, rs.getString("type").trim().toUpperCase(), rs.getString("class_name"),
						new Span(rs.getInt("start_offset"), rs.getInt("end_offset")), 
						rs.getString("entity_name")); 
				entities.add(ae); 
			}
			
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
		return entities; 
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
	
	public int size(String type) {
		check(type); 
		
		int count = 0; 
		
		final String sql = "SELECT count(*) FROM " + this.tableName + " ta, "
			+ this.tableName.substring(0, this.tableName.lastIndexOf('_')) 
			+ " t WHERE ta.fileID = t.fileID AND UPPER(TRIM(t.type)) = ?"; 
		PreparedStatement pstmt = this.dbcon.getPreparedStatement(sql); 
		try {
			pstmt.setString(1, type.trim().toUpperCase()); 
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
	
	public void print(PrintStream out, String[] ids, boolean showEntity) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("("); 
		for (int i = 0; i < ids.length; i++) {
			sb.append("\"" + ids[i] + "\""); 
			if (i < ids.length - 1) {
				sb.append(", "); 
			}
		}
		sb.append(")"); 
		
		final String sql = "SELECT fileID, type, start_offset, end_offset, eid FROM " + this.tableName
			+ " WHERE fileID IN " + sb 
			+ " ORDER BY fileID asc, type desc, start_offset asc, end_offset asc"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				out.print(rs.getString("fileID") + "\t" + rs.getString("type") + ":" 
						+ rs.getInt("start_offset") + ":" + rs.getString("end_offset")); 
				
				if (showEntity) {
					Entity e = new Entity(this.dbcon); 
					out.println("(" + e.getName(rs.getInt("eid")) + ")"); 
				} else {
					out.println(); 
				}
			} 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public void print(PrintStream out, String[] ids) {
		print(out, ids, false); 
	}
	
	public void print(PrintStream out, boolean showEntity) {
		final String sql = "SELECT fileID, type, start_offset, end_offset, eid FROM " + this.tableName 
			+ " ORDER BY fileID asc, type desc, start_offset asc, end_offset asc"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				out.print(rs.getString("fileID") + "\t" + rs.getString("type") + ":" 
						+ rs.getInt("start_offset") + ":" + rs.getString("end_offset")); 
				
				if (showEntity) {
					Entity e = new Entity(this.dbcon); 
					out.println("(" + e.getName(rs.getInt("eid")) + ")"); 
				} else {
					out.println(); 
				}
			} 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public void print(PrintStream out) {
		print(out, false); 
	}
	
	// print nested entities
	public void printNestedEntities(PrintStream out, boolean showEntity) {
		final String sql = "SELECT a1.fileID, a1.type, a1.start_offset, a1.end_offset, a1.eid, "
			+ "a2.fileID, a2.type, a2.start_offset, a2.end_offset, a2.eid FROM " 
			+ this.tableName + " a1, " + this.tableName + " a2 WHERE "
			+ "a1.id <> a2.id AND a1.fileID = a2.fileID AND a1.type = a2.type AND " 
			+ "a1.start_offset <= a2.start_offset AND a1.end_offset >= a2.end_offset"; 
		PreparedStatement pstmt = dbcon.getPreparedStatement(sql); 
		try {
			for (ResultSet rs = pstmt.executeQuery(); rs.next(); ) {
				out.print(rs.getString("a1.fileID") + "\t" + rs.getString("a1.type") + ":" 
						+ rs.getInt("a1.start_offset") + ":" + rs.getString("a1.end_offset")); 
				
				if (showEntity) {
					Entity e = new Entity(this.dbcon); 
					out.println("(" + e.getName(rs.getInt("a1.eid")) + ")"); 
				} else {
					out.println(); 
				}
				
				out.print(rs.getString("a2.fileID") + "\t" + rs.getString("a2.type") + ":" 
						+ rs.getInt("a2.start_offset") + ":" + rs.getString("a2.end_offset"));
				if (showEntity) {
					Entity e = new Entity(this.dbcon); 
					out.println("(" + e.getName(rs.getInt("a2.eid")) + ")"); 
				} else {
					out.println(); 
				}
				
				out.println(); 
			} 
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public void printNestedEntities(PrintStream out) {
		printNestedEntities(out, false); 
	}
}
