package cn.ac.lai.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.lai.tools.PorterStemmer;
import cn.ac.lai.utils.Dictionary;

public class CRFFeatures {
	
	private final static String regexElement = "[H|He|Li|Be|B|C|N|O|F|Ne|Na|Mg|Al|Si|P|S|Cl|Ar|K|Ca|Sc|Ti|V|Cr|Mn|Fe|Co|Ni|Cu|Zn|Ga|Ge|As|Se|Br|Kr|Rb|Sr|Y|Zr|Nb|Mo|Tc|Ru|Rh|Pd|Ag|Cd|In|Sn|Sb|Te|I|Xe|Cs|Ba|La|Ce|Pr|Nd|Pm|Sm|Eu|Gd|Tb|Dy|Ho|Er|Tm|Yb|Lu|Hf|Ta|W|Re|Os|Ir|Pt|Au|Hg|Tl|Pb|Bi|Po|At|Rn|Fr|Ra|Ac|Th|Pa|U|Np|Pu|Am|Cm|Bk|Cf|Es|Fm|Md|No|Lr|Rf|Db|Sg|Bh|Hs|Mt|Ds|Rg|Cn]"; 
	
	private final static String regexAminoAcid = "[Alanine|Ala|Arginine|Arg|Asparagine|Asn|Aspartate|Asp|Cysteine|Cys|Glutamine|Gln|Glutamine|Glu|Glycine|Gly|Histidine|His|Isoleucine|Ile|Leucine|Leu|Lysine|Lys|Methionine|Met|Phenylalanine|Phe|Proline|Pro|Serine|Ser|Threonine|Thr|Tryptophan|Trp|Tyrosine|Tyr|Valine|Val]"; 
	
	private final static String regexAminoAcid2 = "[A|R|N|D|C|Q|E|G|H|I|L|K|M|F|P|S|T|W|Y|V]"; 
	
	private final static String []symbol = {"'", "-", "$", "(", ")", "*", ",", ".", "/", ":", ";", "[", "]", "^", "{", "}", "~", "+", "<", "=", ">", "‴", "±", "≡", "·", "→", "←", "↔"}; 
	
	private final static String []Roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VII", "IX", "X"}; 
	
	private final static String regexGreekLetter = "[α|β|γ|δ|ϵ|ɛ|ζ|η|θ|ι|κ|λ|μ|ν|ξ|π|ρ|σ|τ|υ|φ|ϕ|χ|ψ|ω|∂|Γ|Δ|Θ|Λ|Ξ|Σ|Φ|Ψ|Ω]";
	
	private final static String []drugNomenclatureSuffix = {"vir", "cillin", "mab", "olol", "tidine", "pine", "done", "sone", "lone", "nitrate", "ximab", "zumab", "nib", "vastatin", "prazole", "lukast", "grel", "axine", "oxetine", "sartan", "oxacin", "conazole", "cycline", "navir", "tryptan", "ane", "caine", "azine", "barbital", "azolam", "zosin", "sin", "ipramine", "etine", "aline", "idine", "kinase", "plase", "mine", "pril", "mide", "lactone", "mol", "prium", "aluminum", "magnesium", "hydroxide", "zepam", "lam", "caine", "ide", "nium"}; 
	
	private final static PorterStemmer stemmer = new PorterStemmer();
/*	private final static Dictionary dictUSAN = new Dictionary("data/USAN.dict", false); 
	private final static Dictionary dictUSANToken = new Dictionary(true); 
	
	private final static Dictionary dictINN = new Dictionary("data/INN.dict", false); 
	private final static Dictionary dictINNToken = new Dictionary(true);
	
	private final static Dictionary dictEntityToken = new Dictionary("data/entity-token.dict"); 
	
	private final static PorterStemmer stemmer = new PorterStemmer(); 
	
	static {
		for (int i = 0; i < dictUSAN.getNumWords(); i++) {
			String []tokens = dictUSAN.get(i).split("\\s"); 
			
			for (int j = 0; j < tokens.length; j++) {
				char [] charArray = tokens[j].toCharArray(); 
				int prev = 0; 
				for (int k = 0; k < charArray.length; k++) {
					switch (charArray[k]) {
					case '(': 
					case ')': 
					case '-': 
					case ',': 
						if (prev != k) {
							dictUSANToken.add(tokens[j].substring(prev, k)); 
						}
						dictUSANToken.add(tokens[j].substring(k, k + 1)); 
						prev = k + 1; 
						break;
					}
				}
				
				if (prev < tokens[j].length()) {
					dictUSANToken.add(tokens[j].substring(prev, tokens[j].length())); 
				} 
			}
		}
		
		for (int i = 0; i < dictINN.getNumWords(); i++) {
			String []tokens = dictINN.get(i).split("\\s"); 
			
			for (int j = 0; j < tokens.length; j++) {
				char [] charArray = tokens[j].toCharArray(); 
				int prev = 0; 
				for (int k = 0; k < charArray.length; k++) {
					switch (charArray[k]) {
					case '(': 
					case ')': 
					case '-': 
					case '#': 
						if (prev != k) {
							dictINNToken.add(tokens[j].substring(prev, k)); 
						}
						dictINNToken.add(tokens[j].substring(k, k + 1)); 
						prev = k + 1; 
						break;
					}
				}
				
				if (prev < tokens[j].length()) {
					dictINNToken.add(tokens[j].substring(prev, tokens[j].length())); 
				} 
			}
		}
	}
	
	public static boolean bUSANToken(String str) {
		for (int i = 0; i < dictUSANToken.getNumWords(); i++) {
			if (dictUSANToken.contains(str)) {
				return true; 
			}
		}
		
		return false; 
	}
	
	public static boolean bINNNToken(String str) {
		for (int i = 0; i < dictINNToken.getNumWords(); i++) {
			if (dictINNToken.contains(str)) {
				return true; 
			}
		}
		
		return false; 
	}
	
	public static boolean bEntityToken(String str) {
		for (int i = 0; i < dictEntityToken.getNumWords(); i++) {
			if (dictEntityToken.contains(str)) {
				return true; 
			}
		}
		
		return false; 
	}*/
	
	public static boolean bDrugNomencluatureSuffix(String str) {
		for (int i = 0; i < drugNomenclatureSuffix.length; i++) {
			if (str.endsWith(drugNomenclatureSuffix[i])) {
				return true; 
			}
		}
		
		return false; 
	}
	
	public static boolean bElement(String str) {
		Pattern pattern = Pattern.compile(regexElement, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(str);
		return matcher.find()? true: false; 
	}
	
	public static boolean bAminoAcid(String str) {
		Pattern pattern = Pattern.compile(regexAminoAcid, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return true; 
		}
		
		pattern = Pattern.compile(regexAminoAcid2, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(str);
		return matcher.find()? true: false; 
	}
	
	public static boolean bSymbol(String str) {
		if (str.length() > 1) {
			return false; 
		}
		
		for (int i = 0; i < symbol.length; i++) {
			if (str.equals(symbol[i])) {
				return true; 
			}
		}
		
		return false; 
	}
	
	public static boolean isRoman(String str) {
		for (int i = 0; i < Roman.length; i++) {
			if (str.toUpperCase().equals(Roman[i])) {
				return true; 
			}
		}
		
		return false; 
	}
	
	public static int getNumOfDigitals(String str) {
		int count = 0; 
		
		Pattern pattern = Pattern.compile("[0-9]", Pattern.DOTALL);
		for (Matcher matcher = pattern.matcher(str); matcher.find(); count++) {
			; 
		}
		
		return count; 
	}
	
	public static int getNumOfUpperCaseLetters(String str) {
		int count = 0; 
		
		Pattern pattern = Pattern.compile("[A-Z]", Pattern.DOTALL);
		for (Matcher matcher = pattern.matcher(str); matcher.find(); count++) {
			; 
		}
		
		return count; 
	}
	
	public static int getNumOfLowerCaseLetters(String str) {
		int count = 0; 
		
		Pattern pattern = Pattern.compile("[a-z]", Pattern.DOTALL);
		for (Matcher matcher = pattern.matcher(str); matcher.find(); count++) {
			; 
		}
		
		return count; 
	}
	
	public static String stem(String str) {
		return stemmer.stem(str).toLowerCase(); 
	}
	
	public static String casePattern(String str) {
		String []chars = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
				"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", 
				"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", 
				"u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		
		for (int i = 0; i < chars.length; i++) {
			str = str.replaceAll(chars[i] + "+", chars[i]); 
		}
		
		str = str.replaceAll("[A-Z]", "A"); 
		str = str.replaceAll("[a-z]", "a"); 
		str = str.replaceAll("[0-9]", "0"); 
		
		return str; 
	}
}
