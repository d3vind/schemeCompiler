package scanner;

public class Token {
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


