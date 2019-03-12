import java.util.List;
import java.util.ArrayList;


public class Lexer {
    public static enum Type {
        //Token Types
        LPAREN, RPAREN, STRING, BOOL, CHAR, NUMBER, OPENRD, CLOSERD, OPENSQ, CLOSESQ, OPENCU, CLOSECU, LAMBDA, DEFINE, LET, COND, IF, BEGIN, QUOTE;
    }
    public static class Token {
        public final Type t;
        public final String c;

        // add column and line number fields here for location
        public Token(Type t, String c) {
            this.t = t;
            this.c = c;

        }

        public String toString() {
          //prints a string that doesnt meet other requirements
            if(t == Type.STRING) {
                return "STRING<" + c + ">";
            }
            return t.toString();
        }
    }

    /*
     * Given a String, and an index, get the token starting at that index
     */
    public static String location(String s, int i) {
        int j = i;
        for( ; j < s.length(); ) {
            if(Character.isLetter(s.charAt(j))) {
                j++;
            } else {
                return s.substring(i, j);
            }
        }
        return s.substring(i, j);
    }




    public static List<Token> lex(String input) {
        List<Token> result = new ArrayList<Token>();
        for(int i = 0; i < input.length(); ) {

          if (CharUtils.isParenthesis(input.charAt(i))) {
            recognizeParenthesis(input.charAt(i));
        }
      }
}
public void recognizeParenthesis(char j) {
  char g = j;

  if( g == "()"){
    result.add(new Token(Type.OPENRD, "("));
    i++;
    break;
  }

}


            /*switch(input.charAt(i)) {
              //covers brackets
            case '(':
                result.add(new Token(Type.OPENRD, "("));
                i++;
                break;
            case ')':
                result.add(new Token(Type.CLOSERD, ")"));
                i++;
                break;

            case '{':
                result.add(new Token(Type.OPENCU, "{"));
                i++;

            case '}':
                result.add(new Token(Type.CLOSECU, "}"));
                i++;

            case '[':
                result.add(new Token(Type.OPENSQ, "["));
                i++;

            case ']':
                result.add(new Token(Type.CLOSESQ, "]"));
                i++;


            default:
                if(Character.isWhitespace(input.charAt(i))) {
                    i++;

                } else {
                  //need to implement this for numbers, strings etc.
                    String atom = location(input, i);
                    i += atom.length();
                    result.add(new Token(Type.ATOM, atom));
                }
                break;
            }
        }
        return result;
    }
    */
/*
//probably need to make methods to cover all cases
    public static void recognizeParenthesis() {

      if (Character == '(') {
      result.add(new Token(Type.OPENRD, "("));
        i++;    }
    if (character == ')') {
          result.add(new Token(Type.CLOSERD, ")"));
            i++;    }

}
*/

    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Usage: java Lexer \"((some Scheme) (code to) scanner)\".");
            return;
        }
        List<Token> tokens = lex(args[0]);
        for(Token t : tokens) {
            System.out.println(t);
        }
    }
}
