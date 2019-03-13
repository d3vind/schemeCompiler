/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Laciphina Cui (scui)
 * Devin Delaney (delaney)
 * Maria Jessen (jessen)
 * 
 * VERY IMPORTANT NOTE - the instructions said that characer literals would be
 * denoted by #\, whereas the Scheme class notes used \#.
 * WE WENT BY THE SPECIFICATION IN THE ASSIGNMENT INSTRUCTIONS.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package scanner;

import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Scanner {
	public static String input;
	public static int position = 0, newLineCounter = 0, save = 0;
	static List<Token> result = new ArrayList<Token>();

	/*
	 * Given a String, and an index, get the substring of the token
	 */
	public static String location(String s, int i) {
		position = i;
		int j = i;
		
		//String subReturn = "";
		
		for (; j < s.length();) {
			if (Character.isLetter(s.charAt(j))) {
				j++;
			} else {
				return s.substring(i, j);
			}
		}
		return s.substring(i, j);
	}

	public static void lex(String s) {
		input = s;

		while (position < input.length()) {
			if (input.charAt(position) == '(' || input.charAt(position) == ')') {

				// System.out.println("i is" + i);
				recognizeParenthesis(input.charAt(position), result);
				position++;
				// babysitting the input
				if (position >= input.length())
					break;
			} else if (input.charAt(position) == '[' || input.charAt(position) == ']') {
				recognizeSquareBrackets(input.charAt(position), result);
				position++;
				if (position >= input.length())
					break;
			} else if (input.charAt(position) == '{' || input.charAt(position) == '}') {
				recognizeCurlyBrackets(input.charAt(position), result);
				position++;
				if (position >= input.length())
					break;
			} else if (input.charAt(position) == '\'') {
				result.add(new Token(Type.CHARLITERAL, ")", position, newLineCounter));
			}
			// skips whitespaces
			else if (Character.isWhitespace(input.charAt(position))) {
				position++;
				// babysitting the input
				if (position >= input.length())
					break;

			}
			// deal with comments, very hard stuff
			else if (input.charAt(position) == ';') {
				position++;
				if (position >= input.length())
					break;

			}
			else if (input.charAt(position)>=48 && input.charAt(position)<=57) {
				if(!isNumber(input)) {
					state_rejection(position, position, input);
				}
			}
			// need to implement this for numbers, strings etc.
			else if (input.charAt(position) >= 65 && input.charAt(position) <= 90
					|| input.charAt(position) >= 97 && input.charAt(position) <= 122) {
				isString();
				/*
				 * String subString = location(input, i); i = subString.length() + i;
				 * result.add(new Token(Type.INDENTIFIER, subString));
				 */
			} else if (input.charAt(position) == '#') {
				state_seesNumSign(input);
			} else {
				// TODO: remove this, it's just a placeholder
				position++;
			}
		}

		// return result;

	}

	public static void isString() {
		String string = location(input, position);
		save = position;
		position = string.length() + position;
		if (!isKeyword(string)) {
			result.add(new Token(Type.IDENTIFIER, string, save, newLineCounter));
		}
	}

	// checks to see if string is a keyword
	public static boolean isKeyword(String s) {
		String[] keywords = new String[] { "lambda", "define", "let", "cond", "if", "begin", "quote" };

		if (Arrays.asList(keywords).contains(s)) {
			if (s.equals("lambda")) {
				result.add(new Token(Type.LAMBDA, "lambda", save, newLineCounter));
			}
			if (s.equals("define")) {
				result.add(new Token(Type.DEFINE, "define", save, newLineCounter));
			}
			if (s.equals("let")) {
				result.add(new Token(Type.LET, "let", save, newLineCounter));
			}
			if (s.equals("cond")) {
				result.add(new Token(Type.COND, "cond", save, newLineCounter));
			}
			if (s.equals("if")) {
				result.add(new Token(Type.IF, "if", save, newLineCounter));
			}
			if (s.equals("begin")) {
				result.add(new Token(Type.BEGIN, "begin", save, newLineCounter));
			}
			if (s.equals("quote")) {
				result.add(new Token(Type.QUOTE, "quote", save, newLineCounter));
			}
			return true;
		}
		return false;
	}

	// need to make methods like this to recognize the cases
	public static void recognizeSquareBrackets(char c, List<Token> result) {
		if (c == '[') {
			result.add(new Token(Type.OPENSQ, "[", position, newLineCounter));
		}
		if (c == ']') {
			result.add(new Token(Type.CLOSESQ, "]", position, newLineCounter));
		}

	}

	// need to make methods like this to recognize the cases
	public static void recognizeCurlyBrackets(char c, List<Token> result) {
		if (c == '{') {
			result.add(new Token(Type.OPENCU, "{", position, newLineCounter));
		}
		if (c == '}') {
			result.add(new Token(Type.CLOSECU, "}", position, newLineCounter));
		}

	}

	// need to make methods like this to recognize the cases
	public static void recognizeParenthesis(char c, List<Token> result) {
		if (c == '(') {
			result.add(new Token(Type.OPENRD, "(", position, newLineCounter));
		}
		if (c == ')') {
			result.add(new Token(Type.CLOSERD, ")", position, newLineCounter));
		}

	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage: java Lexer \"((some Scheme) (code to) scanner)\".");
			System.exit(-1);
		}

		java.util.Scanner inputFile = new java.util.Scanner(new File(args[0]));		
		String line = inputFile.nextLine();
		System.out.println(line);
		lex(line);

		while (inputFile.hasNext()) {
			line = inputFile.nextLine();
			System.out.println(line);

			position = 0;

			newLineCounter++;

			lex(line);
		}
		//close input
		inputFile.close();

		for (Token t : result) {
			System.out.println(t);
		}
	}

	// state to enter when it first sees a #
	public static void state_seesNumSign(String input) {
		if (position + 1 >= input.length()) {
			state_rejection(position, position, input);
		}

		position++;

		// check the next character
		if (input.charAt(position) == 92) {
			// seen a backslash, could be a char literal
			state_charLitBeg(input, position - 1);
		} else if (input.charAt(position) == 'f' || input.charAt(position) == 't') {
			// increment boolean, add to token list at position in line
			result.add(new Token(Type.BOOL, ")", position - 1, newLineCounter));
		} else {
			System.out.println("Rejection from state_seesNumSign");
			// reject here
			state_rejection(position, position, input);
		}

	}

	// from #, sees a backslash
	public static void state_charLitBeg(String input, int origin) {
		if (position + 1 >= input.length()) {
			state_rejection(origin, position, input);
		}

		position++;

		if (input.charAt(position) == 'n') {
			state_charLit_newline(input, origin);
		} else if (input.charAt(position) == 's') {
			state_charLit_space(input, origin);
		} else if (input.charAt(position) == 't') {
			state_charLit_tab(input, origin);
		} else if (input.charAt(position) >= 48 && input.charAt(position) <= 51) {
			// could be a number char literal
			state_charLit_number(input, origin);
		} else if (input.charAt(position) >= 33 && input.charAt(position) <= 126) {
			state_charLit_char(input, origin);
			return;
		}
	}

	// from #\ sees a char
	public static void state_charLit_char(String input, int origin) {
		if (position + 1 >= input.length()) {
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		} else {
			state_rejection(origin, position, input);
			return;
		}
	}

	// from #\ sees a number
	public static void state_charLit_number(String input, int origin) {
		if (position + 1 >= input.length()) {
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		if (input.charAt(position) <= 51) { // char is a 0,1,2,3
			state_charLit_octal1(input, origin);
			return;
		}

		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		} else {
			state_rejection(origin, position, input);
		}
	}

	// from #\[number], the number could be octal
	public static void state_charLit_octal1(String input, int origin) {
		if (position + 1 >= input.length()) {
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		} else if (input.charAt(position) < 55) { // 2nd number is under 7
			state_charLit_octal2(input, origin);
		} else {
			state_rejection(origin, position, input);
		}
	}

	// from #\[number][number], might see next bit for octal number
	public static void state_charLit_octal2(String input, int origin) {
		if (position + 1 >= input.length()) {
			state_rejection(origin, position, input);
		}

		position++;

		if (input.charAt(position) < 55) { // 3rd number is under 7
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			// return;
		} else {
			// rejection
			state_rejection(origin, position, input);
		}
	}

	// from #\, sees beginning of tab
	public static void state_charLit_tab(String input, int origin) {
		if (position + 1 >= input.length()) { // just a t char literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		String marker = "tab";
		int markerPos = 1;

		while (position < input.length() - 1 && input.charAt(position) != ' ' && input.charAt(position) != '\n'
				&& markerPos < marker.length()) {
			if (input.charAt(position) == marker.charAt(markerPos)) {
				// safe, keep reading
				markerPos++;
				position++;
			} else {
				// doesn't say space, must be wrong
				state_rejection(origin, position, input);
				return;
			}
		}

		// check if next char is a space, otherwise reject
		if (input.charAt(position) == ' ') {
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
		} else {
			// doesn't say newline, must be wrong
			state_rejection(origin, position, input);
		}
	}

	// from #\, sees beginning of space
	public static void state_charLit_space(String input, int origin) {
		if (position + 1 >= input.length()) { // just an s char literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		String marker = "space";
		int markerPos = 1;

		while (position < input.length() - 1 && input.charAt(position) != ' ' && input.charAt(position) != '\n'
				&& markerPos < marker.length()) {
			if (input.charAt(position) == marker.charAt(markerPos)) {
				// safe, keep reading
				markerPos++;
				position++;
			} else {
				// doesn't say space, must be wrong
				state_rejection(origin, position, input);
				return;
			}
		}

		// check if next char is a space, otherwise reject
		if (input.charAt(position) == ' ') {
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
		} else {
			// doesn't say newline, must be wrong
			state_rejection(origin, position, input);
		}
	}

	// from #\, sees beginning of newline
	public static void state_charLit_newline(String input, int origin) {
		if (position + 1 >= input.length()) { // just an n char literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		}

		String marker = "newline";
		int markerPos = 1;

		while (position < input.length() - 1 && input.charAt(position) != ' ' && input.charAt(position) != '\n'
				&& markerPos < marker.length()) {
			if (input.charAt(position) == marker.charAt(markerPos)) {
				// safe, keep reading
				markerPos++;
				position++;
			} else {
				// doesn't say newline, must be wrong
				state_rejection(origin, position, input);
				return;
			}
		}

		// check if next char is a space, otherwise reject
		if (input.charAt(position) == ' ') {
			result.add(new Token(Type.CHARLITERAL, ")", origin, newLineCounter));
			return;
		} else {
			// doesn't say newline, must be wrong
			state_rejection(origin, position, input);
		}
	}

	// rejection state
	public static void state_rejection(int origin, int end, String input) {
		char endOfToken = input.charAt(end);

		while (end < input.length() && endOfToken != ' ' && endOfToken != '\n') {
			endOfToken = input.charAt(end);
			end++;
		}

		String error = input.substring(origin, end);

		// output the error
		System.out
				.println("LEXICAL ERROR [" + (newLineCounter + 1) + " : " + origin + "] Invalid token '" + error + "'");
		System.exit(-1);
	}

	public static boolean isNumber(String s) {
		boolean decimal = false, fraction = false, hex = false, signed = false, binary = false, expon = false;
		//System.out.println("reaches here2");
		// checking which kind of number it is
		if (!isDigit(s.charAt(position)) && s.length() < 2)
			return false;
		if (s.charAt(position) == '+' || s.charAt(position) == '-') {
			signed = true;
			if (signed)
				position++;
			if (!isDigit(s.charAt(1)))
				return false;
		} else if (s.charAt(position) == '0' && s.charAt(position + 1) == 'x') {
			hex = true;
			position += 2;
			if (!isDigit(s.charAt(1)))
				return false;
		} else if (s.charAt(position) == '0' && s.charAt(position + 1) == 'b') {
			binary = true;
			position += 2;
			if (!isDigit(s.charAt(1)))
				return false;
		}

		// checking to make sure the type of number determined above is correct
		for (; position < s.length(); position++) {
			//System.out.println(s + " " + position);
			//System.out.println("i is " + i);
			if(s.charAt(position) == ' ')
				break;
			if (binary) {
				if (s.charAt(position) != '1' && s.charAt(position) != '0')
					return false;
				continue;
			} else if (hex) {
				if (!(isDigit(s.charAt(position)) || isHexLetter(s.charAt(position))))
					return false;
				continue;
			}
			if (!decimal && !fraction) {
				// System.out.println("3");
				if (s.charAt(position) == '/') {
					fraction = true;
					if (s.length() <= position - 1)
						return false;
					else if (!isDigit(s.charAt(position + 1)))
						return false;
					continue;
				} else if (s.charAt(position) == '.') {
					decimal = true;
					if (s.length() <= position - 1)
						return false;
					else if (!isDigit(s.charAt(position + 1)))
						return false;
					continue;
				}
			}
			if (!expon) {
				if (s.charAt(position) == 'e' || s.charAt(position) == 'E')
					expon = true;
				if (expon) {
					// System.out.println("2");
					System.out.println("i is " + position + " s.length() is " + s.length());
					if (position + 1 >= s.length())
						return false;

					else if (s.charAt(position + 1) == '+' || s.charAt(position + 1) == '-') {
						position++;
						if (!isDigit(s.charAt(position + 2)))
							return false;
					} else if (s.length() <= position - 1)
						return false;
					else if (!isDigit(s.charAt(position + 1))) {
						return false;
					}
					continue;
				}
			}
			if (isDigit(s.charAt(position))) {
				// System.out.println("1");
				continue;
			} 
			else {
				break;
			}

		}
		result.add(new Token(Type.NUMBER, "number", save, newLineCounter));
		//System.out.println("reaches here");
		return true;
	}

	// checking if its a digit or letter
	public static boolean isDigit(char x) {
		if (x >= '0' && x <= '9')
			return true;
		return false;
	}

	public static boolean isHexLetter(char x) {
		if ((x >= 'a' && x <= 'f') || (x >= 'A' && x <= 'F'))
			return true;
		return false;
	}
}