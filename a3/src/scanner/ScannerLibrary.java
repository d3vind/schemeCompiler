package scanner;

import java.util.ArrayList;
import java.util.List;

public class ScannerLibrary {
	private static int position;
	static List<Token> result = new ArrayList<Token>();
	static int newLineCounter=0;
	
	public static void main(String args[]) {
		position = 0;

		String input = "this is a string #\\744";

		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '#') {
				state_seesNumSign(input, i);
			}
			position++;
		}
		
		System.out.println(result);
	}

	// state to enter when it first sees a #
	public static void state_seesNumSign(String input, int pos) {
		if (position + 1 >= input.length()) {
			System.out.println("WALKED OFF THE END");
		}

		pos++;
		position++;

		// check the next character
		if (input.charAt(position) == 92) {
			// seen a backslash, could be a char literal
			state_charLitBeg(input, pos, pos-1);
		} else if (input.charAt(position) == 'f' || input.charAt(position) == 't') {
			// increment boolean, add to token list at position in line
			result.add(new Token(Type.BOOL, ")" , pos-1, newLineCounter));
		} else {
			System.out.println("Rejection from state_seesNumSign");
			// reject here
			state_rejection(pos);
		}

	}

	// from #, sees a backslash
	public static void state_charLitBeg(String input, int pos, int origin) {
		if (position + 1 >= input.length()) {
			System.out.println("WALKED OFF THE END");
		}

		position++;
		pos++;

		if (input.charAt(position) == 'n') {
			state_charLit_newline(input, pos, origin);
		} else if (input.charAt(position) == 's') {
			state_charLit_space(input, pos, origin);
		} else if (input.charAt(position) == 't') {
			state_charLit_tab(input, pos, origin);
		} else if (input.charAt(position) >= 48 && input.charAt(position) <= 51) {
			// could be a number char literal
			state_charLit_number(input, pos, origin);
		} else if (input.charAt(position) >= 33 && input.charAt(position) <= 126) {
			state_charLit_char(input, pos, origin);
			return;
		}
	}

	// from #\ sees a char
	public static void state_charLit_char(String input, int pos, int origin) {
		if (position + 1 >= input.length()) {
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
			return;
		}

		pos++;
		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
			return;
		} else {
			System.out.println("Rejecting at state_charLit_char");
			state_rejection(pos);
			return;
		}
	}

	// from #\ sees a number
	public static void state_charLit_number(String input, int pos, int origin) {
		if (position + 1 >= input.length()) {
			System.out.println("WALKED OFF THE END");
		}

		if (input.charAt(position) <= 51) { // char is a 0,1,2,3
			state_charLit_octal1(input, pos, origin);
			return;
		}

		pos++;
		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
			return;
		} else {
			System.out.println("Rejecting from state_charLit_number");
			state_rejection(pos);
		}
	}

	// from #\[number], the number could be octal
	public static void state_charLit_octal1(String input, int pos, int origin) {
		if (position + 1 >= input.length()) {
			System.out.println("WALKED OFF THE END");
		}

		pos++;
		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
			return;
		} else if (input.charAt(position) < 55) { // 2nd number is under 7
			state_charLit_octal2(input, pos, origin);
		} else {
			System.out.println("Rejecting from state_charLit_octal1");
			state_rejection(pos);
		}
	}

	// from #\[number][number], might see next bit for octal number
	public static void state_charLit_octal2(String input, int pos, int origin) {
		if (position + 1 >= input.length()) {
			System.out.println("WALKED OFF THE END");
		}

		pos++;
		position++;

		if (input.charAt(position) < 55) { // 3rd number is under 7
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
			// return;
		} else {
			System.out.println("Rejecting from state_charList_octal2");
			// rejection
			state_rejection(pos);
		}
	}

	// from #\, sees beginning of tab
	public static void state_charLit_tab(String input, int pos, int origin) {
		if (position + 1 >= input.length()) {
			System.out.println("WALKED OFF THE END");
		}

		pos++;
		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
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
				pos++;
			} else {
				System.out.println("Rejecting from state_charLit_tab");
				// doesn't say space, must be wrong
				state_rejection(pos);
				return;
			}
		}

		// check if next char is a space, otherwise reject
		if (input.charAt(position) == ' ') {
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
		} else {
			System.out.println("Rejecting at state_charLit_tab");
			// doesn't say newline, must be wrong
			state_rejection(pos);
		}
	}

	// from #\, sees beginning of space
	public static void state_charLit_space(String input, int pos, int origin) {
		if (position + 1 >= input.length()) {
			System.out.println("WALKED OFF THE END");
		}

		pos++;
		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
		}

		String marker = "space";
		int markerPos = 1;

		while (position < input.length() - 1 && input.charAt(position) != ' ' && input.charAt(position) != '\n'
				&& markerPos < marker.length()) {
			if (input.charAt(position) == marker.charAt(markerPos)) {
				// safe, keep reading
				markerPos++;
				position++;
				pos++;
			} else {
				System.out.println("Rejecting at state_charLit_space");
				// doesn't say space, must be wrong
				state_rejection(pos);
				return;
			}
		}

		// check if next char is a space, otherwise reject
		if (input.charAt(position) == ' ') {
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
		} else {
			System.out.println("Rejecting at state_charLit_space");
			// doesn't say newline, must be wrong
			state_rejection(pos);
		}
	}

	// from #\, sees beginning of newline
	public static void state_charLit_newline(String input, int pos, int origin) {
		if (position + 1 >= input.length()) {
			System.out.println("WALKED OFF THE END");
		}

		pos++;
		position++;

		if (input.charAt(position) == ' ') {
			// acceptable single printable character literal
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
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
				pos++;
			} else {
				System.out.println("Rejecting at state_charLit_newline");
				// doesn't say newline, must be wrong
				state_rejection(pos);
				return;
			}
		}

		// check if next char is a space, otherwise reject
		if (input.charAt(position) == ' ') {
			result.add(new Token(Type.CHARLITERAL, ")" , origin, newLineCounter));
		} else {
			System.out.println("Rejecting at state_charLit_newline");
			// doesn't say newline, must be wrong
			state_rejection(pos);
		}
	}

	// rejection state
	public static void state_rejection(int origin) {
		// output the error
		System.out.println("SCANNER ERROR " + newLineCounter + " : " + origin);
	}
}
