package scan;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class lexerv2 {
	public static String input;
	public static int counter = 0, newLineCounter = 0, save = 0;
	static List<Token> result = new ArrayList<Token>();

	public static enum Type {
		// Token Types
		LPAREN, RPAREN, STRING, BOOL, CHAR, NUMBER, OPENRD, CLOSERD, OPENSQ, CLOSESQ, OPENCU, CLOSECU, LAMBDA, DEFINE,
		LET, COND, IF, BEGIN, QUOTE;
	}
	
	public static class Token {
		public final Type t;
		public final String c;
		private int posCounter;
		private int lineCounter;

		// add column and line number fields here for location
		public Token(Type t, String c, int posCounter, int lineCounter) {
			this.t = t;
			this.c = c;
			//counter
			this.posCounter = posCounter;
			this.lineCounter = lineCounter;
			

		}

		public String toString() {
			// prints a string that doesnt meet other requirements
		/*	if (t == Type.STRING) {
				return "STRING<" + c + ">";
			} */
			
			return t.toString() + " " + (lineCounter+1) + " : " + (posCounter+1);
		}
	}

	/*
	 * Given a String, and an index, get the token starting at that index
	 */
	public static String location(String s, int i) {
		counter = i;
		int j = i;
		for (; j < s.length();) {
			if (Character.isLetter(s.charAt(j))) {
				j++;
			} else {
				return s.substring(i, j);
			}
		}
		return s.substring(i, j);
	}

	public static List<Token> lex(String s) {
		input = s;

		while (counter < input.length()) {
			if (input.charAt(counter) == '\n') {
				newLineCounter++;
				counter = 0;
			}
			if (input.charAt(counter) == '(' || input.charAt(counter) == ')') {

				// System.out.println("i is" + i);
				recognizeParenthesis(input.charAt(counter), result);
				counter++;
				// babysitting the input
				if (counter >= input.length())
					break;
			}
			// skips whitespaces
			if (Character.isWhitespace(input.charAt(counter))) {
				counter++;
				// babysitting the input
				if (counter >= input.length())
					break;

			}
			// deal with comments, very hard stuff
			if (input.charAt(counter) == ';') {
				counter++;
				if (counter >= input.length())
					break;

			}
			// need to implement this for numbers, strings etc.
			if (input.charAt(counter) >= 65 && input.charAt(counter) <= 90
					|| input.charAt(counter) >= 97 && input.charAt(counter) <= 122) {
				isString();
				/*
				 * String subString = location(input, i); i = subString.length() + i;
				 * result.add(new Token(Type.STRING, subString));
				 */
			}
		}

		return result;

	}

	public static void isString() {
		String string = location(input, counter);
		save = counter;
		counter = string.length() + counter;
		if(!isKeyword(string)) {
			result.add(new Token(Type.STRING, string , save, newLineCounter));
		}
	}
	
	//checks to see if string is a keyword
	public static boolean isKeyword(String s) {
		String[] keywords = new String[] {"lambda", "define", "let", "cond", "if", "begin", "quote"};
		
		if(Arrays.asList(keywords).contains(s)) {
			if(s.equals("lambda")) {
				result.add(new Token(Type.LAMBDA, "lambda", save, newLineCounter));
			}
			if(s.equals("define")) {
				result.add(new Token(Type.DEFINE, "define", save, newLineCounter));
			}
			if(s.equals("let")) {
				result.add(new Token(Type.LET, "let", save, newLineCounter));
			}
			if(s.equals("cond")) {
				result.add(new Token(Type.COND, "cond", save, newLineCounter));
			}
			if(s.equals("if")) {
				result.add(new Token(Type.IF, "if", save, newLineCounter));				
			}
			if(s.equals("begin")) {
				result.add(new Token(Type.BEGIN, "begin", save, newLineCounter));		
			}
			if(s.equals("quote")) {
				result.add(new Token(Type.QUOTE, "quote", save, newLineCounter));
			}
			return true;
		}
		return false;
	}

	// need to make methods like this to recognize the cases
	public static void recognizeParenthesis(char c, List<Token> result) {
		if (c == '(') {
			result.add(new Token(Type.OPENRD, "(", counter, newLineCounter));
		}
		if (c == ')') {
			result.add(new Token(Type.CLOSERD, ")" ,counter, newLineCounter));
		}

	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java Lexer \"((some Scheme) (code to) scanner)\".");
			return;
		}
		List<Token> tokens = lex(args[0]);
		for (Token t : tokens) {
			System.out.println(t);
		}
	}
}