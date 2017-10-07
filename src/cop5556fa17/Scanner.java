/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
  */

package cop5556fa17;


import java.util.ArrayList;
import java.util.Arrays;

public class Scanner {
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, 
		KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, 
		KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, 
		LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}
	// states of the DFA
	public static enum State{
		START/*start state*/, AF_EQU/*=, ==*/,AF_LESS/*<, <=, <-*/, AF_GREAT/*>, >=*/, AF_HIPH/*-, ->*/,AF_EXCL /*!, !=*/,AF_TIMES/* *, ** */, 
		AF_DIV/*/, //comments*/,LINE_TERM/*\r, \n, \r\n */,STR_LIT/*string literal*/, INT_LIT/*integer literal*/, COMM/*comments*/, IDENT/*identifiers*/;
	}
	
	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;
	
	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;
	
	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  



	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
	}


	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		int startPos = 0;
		State state = State.START;
		while(pos <chars.length){
			char ch = chars[pos];
			switch(state){
				case START:{
					startPos = pos;
					switch(ch){
					// singles-------------------------------------------------------------------------
					case '+':{
						tokens.add(new Token(Kind.OP_PLUS,startPos,1,line,posInLine));
						pos++; 
						posInLine = posInLine +(pos-startPos);
					}break; // case +
					case '?':{
						tokens.add(new Token(Kind.OP_Q,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case ?
					case ':':{
						tokens.add(new Token(Kind.OP_COLON,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case :
					case '&':{
						tokens.add(new Token(Kind.OP_AND,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case &
					case '|':{
						tokens.add(new Token(Kind.OP_OR,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case |
					case '@':{
						tokens.add(new Token(Kind.OP_AT,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case @
					case '(':{
						tokens.add(new Token(Kind.LPAREN,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case (
					case ')':{
						tokens.add(new Token(Kind.RPAREN,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case )
					case '[':{
						tokens.add(new Token(Kind.LSQUARE,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case [
					case ']':{
						tokens.add(new Token(Kind.RSQUARE,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case ]
					case ';':{
						tokens.add(new Token(Kind.SEMI,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case ;
					case ',':{
						tokens.add(new Token(Kind.COMMA,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case ,
					case '%':{
						tokens.add(new Token(Kind.OP_MOD,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case %
					case '0':{
						tokens.add(new Token(Kind.INTEGER_LITERAL,startPos,1,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
					}break; // case 0
					case '\n':{
						pos++;
						line++;
						posInLine = 1;
					}break; // case \n
					case '\r':{
						if(chars[pos+1] == '\n'){
							pos++;
							break;
						}
						pos++;
						line++;
						posInLine = 1;
					}break; // case 0
	
					// doubles----------------------------------------------------------------------------
					case '=':{
						state = State.AF_EQU;
						pos++;
						//posInLine = posInLine +(pos-startPos);
					}break; // case =
					case '>':{
						state = State.AF_GREAT;
						pos++;
						//posInLine = posInLine +(pos-startPos);
					}break; // case >
					case '<':{
						state = State.AF_LESS;
						pos++;
						//posInLine = posInLine +(pos-startPos);
					}break; // case <
					case '!':{
						state = State.AF_EXCL;
						pos++;
						//posInLine = posInLine +(pos-startPos);
					}break; // case !
					case '*':{
						state = State.AF_TIMES;
						pos++;
						//posInLine = posInLine +(pos-startPos);
					}break; // case *
					case '/':{
						state = State.AF_DIV;
						pos++;
						//posInLine = posInLine +(pos-startPos);
					}break; // case / 
					case '"':{
						state = State.STR_LIT;
						pos++;
					}break; // case "
					case '-':{
						state = State.AF_HIPH;
						pos++;
						//posInLine = posInLine +(pos-startPos);
					}break; // case -
					case EOFchar:{
						if(pos != chars.length-1){   // in case EOFchar is encountered before end of input
							throw new LexicalException("Illegal termination of the program. EOfChar encountered before the input ends", pos); 
						}
						else{
						pos++;
						}
					}break; // case EOFChar
					
					default:{
						if(Character.isWhitespace(ch)){
							pos++;
							posInLine = posInLine +(pos-startPos);
						}
						else if(Character.isDigit(ch)){
							state = State.INT_LIT;
							pos++;
						}
						else if(Character.isJavaIdentifierStart(ch)){
							state = State.IDENT;
							pos++;
						}
						else{
							throw new LexicalException("Invalid input character.Character not in the grammar", pos);
						}
					}break; //default case
					
					}//switch(ch)
				}break;//state START
				// after ops---------------------------------------------------------------------------------
				case AF_EQU:{
					startPos = pos-1;
					if(chars[pos] == '='){
						tokens.add(new Token(Kind.OP_EQ,startPos,2,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}
					else{
						tokens.add(new Token(Kind.OP_ASSIGN,startPos,1,line,posInLine));
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}
				}break; // state AF_EQU
				case AF_LESS:{
					startPos = pos-1;
					if(chars[pos] == '-'){
						tokens.add(new Token(Kind.OP_LARROW,startPos,2,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
						state = State.START;
						
					}
					else if(chars[pos] == '='){
						tokens.add(new Token(Kind.OP_LE,startPos,2,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}
					else{
						tokens.add(new Token(Kind.OP_LT,startPos,1,line,posInLine));
						posInLine = posInLine +(pos-startPos);
						state = State.START;
						
					}	
				}break; // state AF_LESS
				
				case AF_GREAT:{
					startPos = pos-1;
					if(chars[pos] == '='){
						tokens.add(new Token(Kind.OP_GE,startPos,2,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}
					else{
						tokens.add(new Token(Kind.OP_GT,startPos,1,line,posInLine));
						posInLine = posInLine +(pos-startPos);
						state = State.START;	
					}	
				}break; // state AF_GREAT 				
				
				case AF_HIPH:{
					startPos = pos-1;
					if(chars[pos] == '>'){
						tokens.add(new Token(Kind.OP_RARROW,startPos,2,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}
					else{
						tokens.add(new Token(Kind.OP_MINUS,startPos,1,line,posInLine));
						posInLine = posInLine +(pos-startPos);
						state = State.START;
						
					}	
				}break; // state AF_HIPH
				case AF_EXCL:{
					startPos = pos-1;
					if(chars[pos] == '='){
						tokens.add(new Token(Kind.OP_NEQ,startPos,2,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}
					else{
						tokens.add(new Token(Kind.OP_EXCL,startPos,1,line,posInLine));
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}	
					
				}break; // state AF_EXCL
				case AF_TIMES:{
					startPos = pos-1;
					if(chars[pos] == '*'){
						tokens.add(new Token(Kind.OP_POWER,startPos,2,line,posInLine));
						pos++;
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}
					else{
						tokens.add(new Token(Kind.OP_TIMES,startPos,1,line,posInLine));
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}		
				}break; // state AF_TIMES
				case AF_DIV:{
					startPos = pos-1;
					if(chars[pos] == '/'){
						while(pos < chars.length && chars[pos] != '\n' && chars[pos] != '\r'){
							pos++;
						}
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}
					else{
						tokens.add(new Token(Kind.OP_DIV,startPos,1,line,posInLine));
						posInLine = posInLine +(pos-startPos);
						state = State.START;
					}	
						
				}break; // state AF_DIV
				case STR_LIT:{
					startPos = pos-1;
					while(pos<chars.length && chars[pos]!=34){
						if(chars[pos] == '\\' && (chars[pos+1] == 'b' ||chars[pos+1] == 't' ||chars[pos+1] == 'n' ||chars[pos+1] == 'f' ||
								chars[pos+1] == 'r' ||chars[pos+1] == '\''|| chars[pos+1] == '"' ||chars[pos+1] == '\\' )){
							pos = pos+2;
						}
						else if(chars[pos] == '\\'){
							throw new LexicalException("Invalid string character. Encountered \\ in a string literal type", pos+1);
							
						}
						else if(chars[pos] == '\r' || chars[pos] == '\n'){
							throw new LexicalException("Invalid string character.", pos);
						}
						else{
						pos++;
						}
					}
					
					if(pos == chars.length){
						throw new LexicalException("Invalid string character, couldn't find the \" as the end of the string", chars.length-1);
					}
					else{
					tokens.add(new Token(Kind.STRING_LITERAL,startPos,((pos-startPos)+1),line,posInLine));
					pos++;
					posInLine = posInLine +(pos-startPos);
					
					}
					state = State.START;
				}break; //state STR_LIT 
				case IDENT:{
					startPos = pos-1;
					StringBuilder sb = new StringBuilder();
					sb.append(chars[pos-1]);
					while(Character.isJavaIdentifierPart(chars[pos]) && chars[pos] != '\0'){
						sb.append(chars[pos]);
						pos++;
					}
					
					// keywords--------------------------------------------------------------------------------------------------
					if(sb.toString().equals("x")){
						tokens.add(new Token(Kind.KW_x,startPos,((pos-startPos)),line,posInLine));
						
					}
					else if(sb.toString().equals("X")){
						tokens.add(new Token(Kind.KW_X,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("y")){
						tokens.add(new Token(Kind.KW_y,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("Y")){
						tokens.add(new Token(Kind.KW_Y,startPos,((pos-startPos)),line,posInLine));
					}
					else if (sb.toString().equals("r")){
						tokens.add(new Token(Kind.KW_r,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("R")){
						tokens.add(new Token(Kind.KW_R,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("a")){
						tokens.add(new Token(Kind.KW_a,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("A")){
						tokens.add(new Token(Kind.KW_A,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("Z")){
						tokens.add(new Token(Kind.KW_Z,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("DEF_X")){
						tokens.add(new Token(Kind.KW_DEF_X,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("DEF_Y")){
						tokens.add(new Token(Kind.KW_DEF_Y,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("SCREEN")){
						tokens.add(new Token(Kind.KW_SCREEN,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("cart_x")){
						tokens.add(new Token(Kind.KW_cart_x,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("cart_y")){
						tokens.add(new Token(Kind.KW_cart_y,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("polar_a")){
						tokens.add(new Token(Kind.KW_polar_a,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("polar_r")){
						tokens.add(new Token(Kind.KW_polar_r,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("abs")){
						tokens.add(new Token(Kind.KW_abs,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("sin")){
						tokens.add(new Token(Kind.KW_sin,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("cos")){
						tokens.add(new Token(Kind.KW_cos,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("atan")){
						tokens.add(new Token(Kind.KW_atan,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("log")){
						tokens.add(new Token(Kind.KW_log,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("image")){
						tokens.add(new Token(Kind.KW_image,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("int")){
						tokens.add(new Token(Kind.KW_int,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("boolean")){
						tokens.add(new Token(Kind.KW_boolean,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("url")){
						tokens.add(new Token(Kind.KW_url,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("file")){
						tokens.add(new Token(Kind.KW_file,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("true")){
						tokens.add(new Token(Kind.BOOLEAN_LITERAL,startPos,((pos-startPos)),line,posInLine));
					}
					else if(sb.toString().equals("false")){
						tokens.add(new Token(Kind.BOOLEAN_LITERAL,startPos,((pos-startPos)),line,posInLine));
					}
					// identifiers other than keywords----------------------------------------------------------------------
					else{
						tokens.add(new Token(Kind.IDENTIFIER,startPos,(pos-startPos),line,posInLine));
					}
					posInLine = posInLine +(pos-startPos);
					state = State.START;
				}break; //state IDENT
				case INT_LIT:{
					startPos=pos-1;
					StringBuilder sb = new StringBuilder();
					sb.append(chars[pos-1]);
					while(Character.isDigit(chars[pos])){
						sb.append(chars[pos]);
						pos++;
					}
					try {
						Integer.parseInt(sb.toString());
						
					} catch(NumberFormatException e) {
						
						throw new LexicalException("number is not a valid",startPos);
					}
					
					tokens.add(new Token(Kind.INTEGER_LITERAL,startPos,((pos-startPos)),line,posInLine));
					posInLine = posInLine +(pos-startPos);
					state = State.START;
					
				}break; //state INT_LIT
				default:{
					throw new LexicalException("input is not a valid. character not a part of grammar", pos);
				}
			
			}// switch(state) ends---
			
		}// while loop ends---
		tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
		return this;
	}// scan function ends---

	
	


	/**
	 * Returns true if the internal iterator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}
	
	
	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

} // scanner class ends---
