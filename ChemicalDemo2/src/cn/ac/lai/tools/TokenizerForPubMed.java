package cn.ac.lai.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.util.Span;
import opennlp.*;
import cn.ac.lai.tools.SentSpliterForPubMed;
import cn.ac.lai.tools.Tokenizer;
import cn.ac.lai.tools.TokenizerForPubMed;
import cn.ac.lai.utils.AnnotatedEntity;
import cn.ac.lai.utils.TextPolicy;
import cn.ac.lai.utils.TokenEntitySpan;
import cn.ac.lai.utils.TokenSpan;
import cn.ac.lai.utils.Util;


public class TokenizerForPubMed {
	
	private final static String[] aminoAcid3 = { "Ala", "Arg", "Asn", "Asp",
			"Cys", "Gln", "Glu", "Gly", "His", "Ile", "Leu", "Lys", "Met",
			"Phe", "Pro", "Ser", "Thr", "Trp", "Tyr", "Val" };

	private final static String[] VB = { "B1", "B2", "B3", "B4", "B5", "B6",
			"B7", "B9", "B12", "B13", "B15", "B17" };

	private final static String[] metal = { "Li", "Na", "K", "Rb", "Cs", "Fr",
			"Be", "Mg", "Ca", "Sr", "Ba", "Ra", "Sc", "Ti", "V", "Cr", "Mn",
			"Fe", "Co", "Ni", "Cu", "Zn", "Y", "Zr", "Nb", "Mo", "Tc", "Ru",
			"Rh", "Pd", "Ag", "Cd", "B", "Si", "Ge", "As", "Sb", "Te", "Al",
			"Ga", "In", "Sn", "Sb", "Te", "Tl", "Pb", "Bi", "Po", "Au" };

	static public Span[] spanDetect(String str, Span[] spans) {
		List<Span> newSpans = new ArrayList<Span>();

		for (int i = 0; i < spans.length; i++) {
			String token = spans[i].getCoveredText(str).toString();

			newSpans.addAll(Util.add(Util.split(token), spans[i].getStart()));
		}

		// 纠正被拆分开的计量单位
		newSpans = correctUnit(str, newSpans);

		// 把开头和结尾的数字分离出来
		newSpans = splitNumber(str, newSpans);

		// 处理一种特殊情形
		for (int i = 0; i < newSpans.size() - 1; i++) {
			String currToken = newSpans.get(i).getCoveredText(str).toString();
			String nextToken = newSpans.get(i + 1).getCoveredText(str)
					.toString();

			if (!Character.isDigit(nextToken.charAt(0))
					|| (nextToken.length() == 1)) {
				continue;
			}

			String[] str3 = { "Fe2S", "Fe3S", "Fe4S" };
			for (int j = 0; j < str3.length; j++) {
				if (currToken.equals(str3[j])) {
					Span e = newSpans.get(i + 1);
					newSpans.set(i + 1,
							new Span(e.getStart(), e.getStart() + 1));
					newSpans.add(i + 2, new Span(newSpans.get(i + 1).getEnd(),
							e.getEnd()));
					break;
				}
			}
		}

		// 把数字与计量单位分离
		newSpans = splitUnit(str, newSpans);

		// 氨基酸三字母缩写异常
		String[] prefixAmino = { "p" };
		String[] suffixAmino = { "AP" };
		newSpans = splitPrefix(str, newSpans, prefixAmino, aminoAcid3);
		newSpans = splitSuffix(str, newSpans, suffixAmino, aminoAcid3);

		// 常见前后缀异常
		String[] prefixCommon = { "OBJECTIVE", "CONCLUSIONS", "dietary",
				"nano", "anti" };
		String[] suffixCommon = { "protective" };
		newSpans = splitPrefix(str, newSpans, prefixCommon);
		newSpans = splitSuffix(str, newSpans, suffixCommon);

		// 维生素B异常
		String[] suffixVB = { "is" };
		newSpans = splitSuffix(str, newSpans, suffixVB, VB);

		// 金属元素
		String[] suffixMetal = { "SOD", "NPs", "BL" };
		newSpans = splitSuffix(str, newSpans, suffixMetal, metal);

		// 把缩写的复数形式分离
		newSpans = splitPlural(str, newSpans);

		String[] prevIgnore = { "ONE", "ones" };
		String[] rearIgnore = { "were", "in" };
		newSpans = splitPair(str, newSpans, prevIgnore, rearIgnore, true);

		String[] prev = { "H", "GABA", "spermine", "Spermine", "oxo", "Oxo",
				"AUC", "n", "mM", "rh", "nitropheny", "Nitropheny" };
		String[] rear = { "MRS", "ergic", "x", "x", "anions", "anions",
				"glucose", "TiO", "KCl", "N", "l", "l" };
		newSpans = splitPair(str, newSpans, prev, rear, false);

		return newSpans.toArray(new Span[newSpans.size()]);
	}

	private static List<Span> splitPrefix(String str, List<Span> spans,
			String[] prefix) {
		for (int i = 0; i < spans.size(); i++) {
			String token = spans.get(i).getCoveredText(str).toString();

			for (int j = 0; j < prefix.length; j++) {
				if (token.length() <= prefix[j].length()) {
					continue;
				}

				if (token.substring(0, prefix[j].length()).equalsIgnoreCase(
						prefix[j])) {
					Span e = spans.get(i);
					spans.set(i, new Span(e.getStart(), e.getStart()
							+ prefix[j].length()));
					spans.add(i + 1,
							new Span(spans.get(i).getEnd(), e.getEnd()));
					break;
				}
			}
		}

		return spans;
	}

	private static List<Span> splitPrefix(String str, List<Span> spans,
			String[] prefix, String[] focus) {
		for (int i = 0; i < spans.size(); i++) {
			String token = spans.get(i).getCoveredText(str).toString();

			for (int j = 0; j < focus.length; j++) {
				for (int k = 0; k < prefix.length; k++) {
					if (!token.equals(prefix[k] + focus[j])) {
						continue;
					}

					Span e = spans.get(i);
					spans.set(i, new Span(e.getStart(), e.getStart()
							+ prefix[k].length()));
					spans.add(i + 1,
							new Span(spans.get(i).getEnd(), e.getEnd()));
				}
			}
		}
		return spans;
	}

	private static List<Span> splitSuffix(String str, List<Span> spans,
			String[] suffix) {
		for (int i = 0; i < spans.size(); i++) {
			String token = spans.get(i).getCoveredText(str).toString();

			for (int j = 0; j < suffix.length; j++) {
				if (token.length() <= suffix[j].length()) {
					continue;
				}

				if (token.substring(token.length() - suffix[j].length())
						.equalsIgnoreCase(suffix[j])) {
					Span e = spans.get(i);
					spans.set(
							i,
							new Span(e.getStart(), e.getEnd()
									- suffix[j].length()));
					spans.add(i + 1,
							new Span(spans.get(i).getEnd(), e.getEnd()));
					break;
				}
			}
		}

		return spans;
	}

	private static List<Span> splitSuffix(String str, List<Span> spans,
			String[] suffix, String[] focus) {
		for (int i = 0; i < spans.size(); i++) {
			String token = spans.get(i).getCoveredText(str).toString();

			for (int j = 0; j < focus.length; j++) {
				for (int k = 0; k < suffix.length; k++) {
					if (!token.equals(focus[j] + suffix[k])) {
						continue;
					}

					Span e = spans.get(i);
					spans.set(
							i,
							new Span(e.getStart(), e.getStart()
									+ focus[j].length()));
					spans.add(i + 1,
							new Span(spans.get(i).getEnd(), e.getEnd()));
				}
			}
		}

		return spans;
	}

	private static List<Span> splitPair(String str, List<Span> spans,
			String[] prev, String[] rear, boolean isIgnoreCase) {
		if (prev.length != rear.length) {
			return spans;
		}

		for (int i = 0; i < spans.size(); i++) {
			String token = spans.get(i).getCoveredText(str).toString();

			for (int j = 0; j < prev.length; j++) {
				if ((isIgnoreCase && !token.equalsIgnoreCase(prev[j] + rear[j]))
						|| (!isIgnoreCase && !token.equals(prev[j] + rear[j]))) {
					continue;
				}

				spans.set(i, new Span(spans.get(i).getStart(), spans.get(i)
						.getStart() + prev[j].length()));
				spans.add(i + 1, new Span(spans.get(i).getEnd(), spans.get(i)
						.getEnd() + rear[j].length()));
			}
		}

		return spans;
	}

	// 把开头和结尾的数字分离出来
	private static List<Span> splitNumber(String str, List<Span> spans) {
		// 结尾
		for (int i = 0; i < spans.size(); i++) {
			Pattern pattern = Pattern.compile("[a-zA-Z]+(\\d+)$",
					Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(spans.get(i).getCoveredText(str));
			if (matcher.find()) {
				Span e = spans.remove(i);
				spans.add(i,
						new Span(e.getStart(), e.getEnd()
								- matcher.group(1).length()));
				spans.add(i + 1, new Span(spans.get(i).getEnd(), e.getEnd()));
			}
		}

		// 开头
		for (int i = 0; i < spans.size(); i++) {
			Pattern pattern = Pattern.compile("^(\\d+)[a-zA-Z]+",
					Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(spans.get(i).getCoveredText(str));
			if (matcher.find()) {
				Span e = spans.remove(i);
				spans.add(i, new Span(e.getStart(), e.getStart()
						+ matcher.group(1).length()));

				spans.add(i + 1, new Span(spans.get(i).getEnd(), e.getEnd()));
			}
		}

		return spans;
	}

	// 把数字与计量单位分离
	private static List<Span> splitUnit(String str, List<Span> spans) {
		for (int i = 0; i < spans.size(); i++) {
			String regex = "^(\\d+)(nm|kg|mg|years|year|days|day|months|month|hours|hour|minutes|minute|seconds|second|µm|µg|cm|bp|ng|hz)$";
			Pattern pattern = Pattern.compile(regex, Pattern.DOTALL
					| Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(spans.get(i).getCoveredText(str));
			if (matcher.find()) {
				spans.set(i, new Span(spans.get(i).getStart(), spans.get(i)
						.getStart() + matcher.group(1).length()));

				spans.add(i + 1, new Span(spans.get(i).getEnd(), spans.get(i)
						.getEnd() + matcher.group(2).length()));
			}
		}

		return spans;
	}

	// 把缩写的复数形式分离
	private static List<Span> splitPlural(String str, List<Span> spans) {
		for (int i = 0; i < spans.size(); i++) {
			String token = spans.get(i).getCoveredText(str).toString();
			if (token.length() < 3) {
				continue;
			}

			if (token.endsWith("s")
					&& token.substring(0, token.length() - 1).matches("[A-Z]+")) {
				Span e = spans.get(i);

				spans.set(i,
						new Span(e.getStart(), e.getStart() + token.length()
								- 1));
				spans.add(i + 1, new Span(spans.get(i).getEnd(), spans.get(i)
						.getEnd() + 1));
			}
		}

		return spans;
	}

	// 纠正被拆分开的计量单位
	private static List<Span> correctUnit(String str, List<Span> spans) {
		for (int i = 0; i < spans.size() - 1; i++) {
			String currToken = spans.get(i).getCoveredText(str).toString();
			String nextToken = spans.get(i + 1).getCoveredText(str).toString();

			if (!currToken.equals("μ")) {
				continue;
			}

			String[] units = { "g", "m", "mol" };
			for (int j = 0; j < units.length; j++) {
				if (nextToken.startsWith(units[j])
						&& nextToken.length() > units[j].length()) {
					Span e = spans.get(i + 1);
					spans.set(i + 1, new Span(e.getStart(), e.getStart()
							+ units[j].length()));
					spans.add(i + 2,
							new Span(spans.get(i + 1).getEnd(), e.getEnd()));
					break;
				}
			}
		}

		return spans;
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
//装逼进度查看神器
	public static void showPercentage(double up,double down){
		double i=up;
		double j = down;
		
		//display percents
		NumberFormat format = new DecimalFormat("0.00");
		System.out.println(format.format(i / j*100) + 
				"% works have been done!!");
		
	}
	public static void main(String[] args) throws IOException {
		String str = "An input sample sentence.";
		String inputFile = "data/brown/HarmonizedMergeExFileName.token";
		String outputFile = "data/brown/Harmonized2Brown.token";

		double nLine = 0d;
		double sum=463d;
		TokenizerForPubMed getSpan = new TokenizerForPubMed();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFile), "UTF-8"));
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outputFile,true));
//分词，分词brown聚类所需的格式
		for (String nextLine; ((nextLine = reader.readLine()) != null); nLine++) {
			List<TokenSpan> tokenSpans = getSpan.getTokenSpan(nextLine);
			for (TokenSpan tokenSpan : tokenSpans) {
				List<Span> spans = tokenSpan.getSpans();
				for (Span span : spans) {
					int startPos = span.getStart();
					int endPos = span.getEnd();
					String token = nextLine.substring(startPos, endPos);
					bufWriter.write(token+" ");
				}

			}
			bufWriter.write("\n");
			showPercentage(nLine, sum);

		}

		/*
		 * for (String nextLine; ((nextLine = reader.readLine()) != null);
		 * nLine++) {
		 * 
		 * 
		 * List<TokenSpan> tokenSpans1 = getSpan.getTokenSpan(nextLine);
		 * List<String> tokenTypes = tes.getTokenTypes(); for (int j = 0; j <
		 * tokenSpans1.size(); j++) { String token =
		 * tokenSpans1.get(j).(nextLine).toString(); }
		 * 
		 * }
		 */
		System.out.println("done");

	}

}
