package cn.ac.lai.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import opennlp.tools.util.Span;
import cn.ac.lai.converter.CRFFeatures;
import cn.ac.lai.tools.SentSpliterForPubMed;
import cn.ac.lai.tools.Tokenizer;
import cn.ac.lai.tools.TokenizerForPubMed;

public class Util {
	public static int countOccurrenceOf(String str, String findStr) {
		int count = 0;
		for (int lastIndex = 0; lastIndex != -1;) {
			lastIndex = str.indexOf(findStr, lastIndex);

			if (lastIndex != -1) {
				count++;
				lastIndex += findStr.length();
			}
		}

		return count;
	}

	public static int countOccurrenceOf(String str, char c) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == c) {
				count++;
			}
		}

		return count;
	}

	public static boolean isMatch(String str) {
		char[] charArray = str.toCharArray();
		Stack<Character> stack = new Stack<Character>();
		for (char c : charArray) {
			if (c == '(' || c == '[' || c == '}') {
				stack.push(c);
			}

			if (c == ')' || c == ']' || c == '}') {
				if (stack.isEmpty()) {
					return false;
				}

				Character cc = stack.pop();

				switch (c) {
				case ')':
					if (cc != '(') {
						return false;
					}
					break;
				case ']':
					if (cc != '[') {
						return false;
					}
					break;
				case '}':
					if (cc != '{') {
						return false;
					}
					break;
				}
			}
		}

		return stack.isEmpty() ? true : false;
	}

	public static Span[] add(Span[] spans, int offset) {
		ArrayList<Span> newSpans = new ArrayList<Span>();

		for (int i = 0; i < spans.length; i++) {
			Span e = new Span(spans[i], offset);
			newSpans.add(e);
		}

		Span[] ret = new Span[newSpans.size()];
		return newSpans.toArray(ret);
	}

	public static List<Span> add(List<Span> spans, int offset) {
		List<Span> newSpans = new ArrayList<Span>();

		for (int i = 0; i < spans.size(); i++) {
			Span e = new Span(spans.get(i), offset);
			newSpans.add(e);
		}

		return newSpans;
	}

	public static boolean isContatins(Span s1, Span s2) {
		return ((s1.getStart() <= s2.getStart()) && (s1.getEnd() >= s2
				.getStart())) ? true : false;
	}

	// 将类似的符号进行合并
	public static String merge(String str) {
		str = str.replaceAll("（", "(");
		str = str.replaceAll("）", ")");
		str = str.replaceAll("〈|≪|＜", "<");
		str = str.replaceAll("〉|≫|﹥", ">");
		str = str.replaceAll("•|∙|⋅|●|˙", "·");
		str = str.replaceAll("〖", "[");
		str = str.replaceAll("〗", "]");
		str = str.replaceAll("⁺", "+");
		str = str.replaceAll("—|–|‒|−|⁻|₋|̄|¯|―|‑", "-");
		str = str.replaceAll("∼|˜", "~");
		str = str.replaceAll("∗|¤", "*");
		str = str.replaceAll("`|´|’|′|‘", "'");
		str = str.replaceAll("″|̃|“|”", "\"");
		str = str.replaceAll("¦", "|");
		str = str.replaceAll("═", "=");
		str = str.replaceAll("≃|≅", "≈");
		str = str.replaceAll("≧", "≥");
		str = str.replaceAll("≦", "≤");
		str = str.replaceAll("¿|？|�", "?");
		str = str.replaceAll("¡", "!");
		str = str.replaceAll("∶|：", ":");
		str = str.replaceAll("；", ";");
		str = str.replaceAll("、|，|‚", ",");
		str = str.replaceAll("|。", ".");
		str = str.replaceAll("„", "∥");
		str = str.replaceAll("∕", "/");
		str = str.replaceAll("∝", "∞");
		str = str.replaceAll("º|∘", "°");
		str = str.replaceAll("ˆ", "^");

		str = str.replaceAll("ß", "β");
		str = str.replaceAll("ε|έ", "ɛ");
		str = str.replaceAll("∑", "Σ");
		str = str.replaceAll("ф|Ф", "Φ");
		str = str.replaceAll("∆|△", "Δ");
		str = str.replaceAll("ϑ", "θ");
		str = str.replaceAll("µ", "μ");

		str = str.replaceAll("¥", "￥");

		str = str.replaceAll("⁰|₀", "0");
		str = str.replaceAll("¹|₁", "1");
		str = str.replaceAll("₂|²", "2");
		str = str.replaceAll("₃|³", "3");
		str = str.replaceAll("₄|⁴", "4");
		str = str.replaceAll("₅|⁵", "5");
		str = str.replaceAll("₆", "6");
		str = str.replaceAll("₇|⁷", "7");
		str = str.replaceAll("₈|⁸", "8");
		str = str.replaceAll("⁹|₉", "9");

		str = str.replaceAll("8̊", "8°");

		str = str.replaceAll("à|á|â|ä|ã|ā|ȧ|ă|ǎ|ạ|ą|å|å|ǻ|ā|å|â", "a");
		str = str.replaceAll("À|Á|Â|Ä|Ã|Ā|Ȧ|Ă|Ǎ|Ạ|Å|Α|А|Ǻ|Å", "A");
		str = str.replaceAll("ć|ĉ|ċ|č|ç|¢|ɕ", "c");
		str = str.replaceAll("Ć|Ĉ|Ċ|Č|Ç|С|c̄", "C");
		str = str.replaceAll("ḑ|đ", "d");
		str = str.replaceAll("Ď|Đ", "D");
		str = str.replaceAll("è|é|ê|ë|ẽ|ē|ė|ĕ|ě|ȩ|ȩ|ę|ę", "e");
		str = str.replaceAll("È|É|Ê|Ë|Ẽ|Ē|Ė|Ĕ|Ě|Ȩ|Ę", "E");
		str = str.replaceAll("ƒ", "f");
		str = str.replaceAll("ǵ|ĝ|ḡ|ġ|ğ|ǧ|ǥ|ģ", "g");
		str = str.replaceAll("Ǵ|Ĝ|Ḡ|Ġ|Ğ|Ǧ|Ǥ|Ģ", "G");
		str = str.replaceAll("ĥ|ḩ|ħ|h̄", "h");
		str = str.replaceAll("Ĥ|Ḩ|Ħ", "H");
		str = str.replaceAll("ì|í|î|ï|ĩ|ī|ı|ĭ|į|і|ï|í", "i");
		str = str.replaceAll("Ì|Í|Î|Ï|Ī|İ|Ĭ|Į|Ι|І", "I");
		str = str.replaceAll("ĵ|ј", "j");
		str = str.replaceAll("Ĵ|Ј", "J");
		str = str.replaceAll("ķ|k̄", "k");
		str = str.replaceAll("Ķ", "K");
		str = str.replaceAll("ł|ĺ|ľ|ḷ|ḻ|ļ|ɬ|ℓ", "l");
		str = str.replaceAll("Ł|Ĺ|Ľ|Ḷ|Ḻ|Ļ", "L");
		str = str.replaceAll("m̸", "m");
		str = str.replaceAll("Μ", "M");
		str = str.replaceAll("ń|ñ|ň|ņ|n̄", "n");
		str = str.replaceAll("Ń|Ñ|Ň|Ņ", "N");
		str = str.replaceAll("ò|ó|ô|ö|õ|ō|ŏ|ő|ǫ|ø|ǿ|∅", "o");
		str = str.replaceAll("Ò|Ó|Ô|Ö|Õ|Ō|Ŏ|Ő|Ǫ|Ø", "O");
		str = str.replaceAll("Р", "P");
		str = str.replaceAll("ŕ|ř|ŗ", "r");
		str = str.replaceAll("Ŕ|Ř|Ŗ", "R");
		str = str.replaceAll("ś|ŝ|š|ş|ʂ|s̄", "s");
		str = str.replaceAll("Ś|Ŝ|Š|Ş", "S");
		str = str.replaceAll("ṫ|ť|ṭ|ṯ|ţ|ŧ", "t");
		str = str.replaceAll("Ṫ|Ť|Ṭ|Ṯ|Ţ|Ŧ|Τ", "T");
		str = str.replaceAll("ù|ú|û|ü|ũ|ū|ŭ|ű|ų|ů|ū", "u");
		str = str.replaceAll("Ù|Ú|Û|Ü|Ũ|Ū|Ŭ|Ű|Ų|Ů", "U");
		str = str.replaceAll("ⅴ", "v");
		str = str.replaceAll("Ⅴ", "V");
		str = str.replaceAll("ŵ", "w");
		str = str.replaceAll("Ŵ", "W");
		str = str.replaceAll("ý|ÿ|ŷ", "y");
		str = str.replaceAll("Ý|Ÿ|Ŷ", "Y");
		str = str.replaceAll("ź|ż|ż|ž", "z");
		str = str.replaceAll("Ź|Ż|Ž", "Z");

		str = str.replaceAll("|||||||||| ||||||", " ");
		str = str.replaceAll("⟩|⩾|⩽|⟨|□|𝒸|𝒮|︁|⁎|⁡|〈|⎕|⧹|⧸", "⧧");

		return str;
	}

	public static List<Span> split(String str) {
		List<Span> spans = new ArrayList<Span>();
		char[] charArray = str.toCharArray();

		int prev = 0;
		for (int i = 0; i < charArray.length; i++) {
			switch (charArray[i]) {
			case '(':
			case ')':
			case '[':
			case ']':
			case '{':
			case '}':
			case '>':
			case '<':
			case '≤':
			case '≥':
			case ',':
			case '.':
			case '/':
			case '\\':
			case '\'':
			case '™':
			case '@':
			case '·':
			case '®':
			case '©':
			case '"':
			case ':':
			case '=':
			case '≠':
			case '≡':
			case '≈':
			case '+':
			case '-':
			case '?':
			case '_':
			case '|':
			case '↑':
			case '↓':
			case '→':
			case '←':
			case '↘':
			case '↙':
			case '↖':
			case '↗':
			case '↔':
			case '⇌':
			case 'α':
			case 'β':
			case 'γ':
			case 'δ':
			case 'ϵ':
			case 'ɛ':
			case 'ζ':
			case 'η':
			case 'θ':
			case 'ι':
			case 'κ':
			case 'λ':
			case 'μ':
			case 'ν':
			case 'ξ':
			case 'π':
			case 'ρ':
			case 'σ':
			case 'τ':
			case 'υ':
			case 'φ':
			case 'ϕ':
			case 'χ':
			case 'ψ':
			case 'ω':
			case '∂':
			case 'Γ':
			case 'Δ':
			case 'Θ':
			case 'Λ':
			case 'Ξ':
			case 'Σ':
			case 'Φ':
			case 'Ψ':
			case 'Ω':
			case '±':
			case '~':
			case '*':
			case '%':
			case '‰':
			case '⧧':
			case '°':
			case '#':
			case '&':
			case '^':
			case ';':
			case '!':
			case '£':
			case '€':
			case '￥':
			case '$':
			case '×':
			case '‡':
			case '†':
			case '…':
			case '∞':
			case '♂':
			case '♀':
			case '˙':
			case '½':
			case '¼':
			case '¾':
			case '‴':
			case '∇':
			case '√':
			case '∃':
			case '∥':
			case '⊂':
			case '∈':
			case '¶':
			case '⊥':
			case '⋯':
			case '∧':
			case '∨':
			case '∫':
			case '⇋':
			case '≔':
			case 'ш':
			case '■':
			case 'ː':
				if (prev != i) {
					spans.add(new Span(prev, i));
				}
				spans.add(new Span(i, i + 1));
				prev = i + 1;
				break;
			}
		}

		if (prev < str.length()) {
			spans.add(new Span(prev, str.length()));
		}

		return spans;
	}

	public static List<TokenEntitySpan> getTokenEntitySpan(String str,
			List<AnnotatedEntity> entities) {
		List<TokenEntitySpan> list = new ArrayList<TokenEntitySpan>();

		Span[] sentSpans = SentSpliterForPubMed.spanDetect(str);
		for (Span s : sentSpans) {
			String sent = s.getCoveredText(str).toString();

			Span[] tokenSpans = Tokenizer.tokenizePos(sent);
			tokenSpans = TokenizerForPubMed.spanDetect(str,
					Util.add(tokenSpans, s.getStart()));

			TokenEntitySpan tokenEntity = new TokenEntitySpan(sent,
					new ArrayList<Span>(Arrays.asList(tokenSpans)));
			tokenEntity = setTokenEntitySpan(str, tokenEntity, entities);
			list.add(tokenEntity);
		}

		return list;
	}

	public static List<TokenSpan> getTokenSpan(String str) {
		List<TokenSpan> list = new ArrayList<TokenSpan>();

		Span[] sentSpans = SentSpliterForPubMed.spanDetect(str);
		for (Span s : sentSpans) {
			String sent = s.getCoveredText(str).toString();

			Span[] tokenSpans = Tokenizer.tokenizePos(sent);
			tokenSpans = TokenizerForPubMed.spanDetect(str,
					Util.add(tokenSpans, s.getStart()));

			list.add(new TokenSpan(sent, new ArrayList<Span>(Arrays
					.asList(tokenSpans))));
		}

		return list;
	}

	public static TokenEntitySpan setTokenEntitySpan(String str,
			TokenEntitySpan tokenEntity, List<AnnotatedEntity> entities) {
		List<Span> entitySpans = new ArrayList<Span>();
		List<Span> tokenSpans = tokenEntity.getTokenSpans();

		for (AnnotatedEntity e : entities) {
			Span entityOffset = e.getOffset();

			int idxStart = -1;
			for (int j = tokenSpans.size() - 1; j >= 0; j--) {
				if (Util.isContatins(
						tokenSpans.get(j),
						new Span(entityOffset.getStart(), entityOffset
								.getStart() + 1))) {
					idxStart = j;
					break;
				}
			}

			int idxEnd = -1;
			for (int j = 0; j < tokenSpans.size(); j++) {
				if (Util.isContatins(tokenSpans.get(j),
						new Span(entityOffset.getEnd(),
								entityOffset.getEnd() + 1))) {
					idxEnd = j;
					break;
				}
			}

			if ((idxStart == -1) || (idxEnd == -1)) {
				continue;
			}

			tokenEntity.addEntityTypes(e.getClassName());
			Span entity = new Span(tokenSpans.get(idxStart).getStart(),
					tokenSpans.get(idxEnd).getEnd());
			entitySpans.add(entity);
			tokenEntity.addTokenSpansOfEntity(entity);
			// tokenEntity.setTokenTypes(idxStart + 1, idxEnd, e.getClassName()+
			// "-I");
			// tokenEntity.setTokenTypes(idxEnd, idxEnd + 1, e.getClassName()+
			// "-E");
			// tokenEntity.setTokenTypes(idxStart, idxStart + 1,
			// e.getClassName()+ "-B");
			tokenEntity.setTokenTypes(idxStart + 1, idxEnd, "-I");
			tokenEntity.setTokenTypes(idxEnd, idxEnd + 1, "-E");
			tokenEntity.setTokenTypes(idxStart, idxStart + 1, "-B");
		}

		tokenEntity.setEntitySpans(entitySpans);

		return tokenEntity;
	}

	public static String toStringFromInstance(String token) {
		StringBuilder sb = new StringBuilder();
		sb.append(token + "\t").append(CRFFeatures.stem(token) + "\t")
				.append(CRFFeatures.bAminoAcid(token) + "\t")
				.append(CRFFeatures.bElement(token) + "\t")
				.append(CRFFeatures.bSymbol(token) + "\t")
				.append(CRFFeatures.isRoman(token) + "\t")
				.append(CRFFeatures.getNumOfDigitals(token) + "\t")
				.append(CRFFeatures.getNumOfUpperCaseLetters(token) + "\t")
				.append(CRFFeatures.getNumOfLowerCaseLetters(token) + "\t")
				.append(token.length() + "\t")
				.append(CRFFeatures.casePattern(token));
		// .append(CRFFeatures.bDrugNomencluatureSuffix(token) + "\t")
		// .append(CRFFeatures.bUSANToken(token) + "\t")
		// .append(CRFFeatures.bINNNToken(token) + "\t")

		return sb.toString();
	}

	public static String toStringFromID(String id, Span s, String source) {
		StringBuilder sb = new StringBuilder();
		sb.append(id + "\t").append(source + "\t")
				.append(s.getStart() + "\t" + s.getEnd());

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println("# 0.689353".matches("^#\\s\\d\\?\\s0.\\d{6}$"));

		System.out.println("# 0 1.000000".matches("^#\\s\\d\\s1.0{6}$"));

		System.out.println(Util
				.countOccurrenceOf(
						" <i>m</i>-MitoPhB(OH)<sub>2</sub>O<sup>?-</sup><sub>",
						"<sub>"));

		System.out.println("done.");
	}
}
