package cop5556fa17;



import java.util.Arrays;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.SimpleParser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	SimpleParser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public void parse() throws SyntaxException {
		program();
		matchEOF();
	}
	
//---------------------------------------------------------------------------------------------------------------------methods start
	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	void program() throws SyntaxException {
		match(Kind.IDENTIFIER);
		while(t.kind == Kind.KW_int || t.kind == Kind.KW_boolean || t.kind == Kind.KW_image || t.kind == Kind.KW_url || t.kind == Kind.KW_file ||
				t.kind == Kind.IDENTIFIER ){
			if(t.kind == Kind.KW_int || t.kind == Kind.KW_boolean || t.kind == Kind.KW_image || t.kind == Kind.KW_url || t.kind == Kind.KW_file ){
				declaration();
				match(Kind.BOOLEAN_LITERAL.SEMI);
			}
			else{
				statement();
				match(Kind.SEMI);
			}
			
		}
		
		//throw new UnsupportedOperationException();
	}
	
	// Declaration :: =  VariableDeclaration     |    ImageDeclaration   |   SourceSinkDeclaration 
	void declaration() throws SyntaxException {
		if(t.kind == Kind.KW_int || t.kind == Kind.KW_boolean){
			variabledeclaration();
		}
		else if(t.kind == Kind.KW_image){
			imagedeclaration();
		}
		else if(t.kind == Kind.KW_url || t.kind == Kind.KW_file){
			sourcesinkdeclaration();
		}
		else{
			throw new SyntaxException(t, "expected a token of kind VariableDeclaration, ImageDeclaration or SourceSinkDeclaration, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		//throw new UnsupportedOperationException();
	}
	
	// VariableDeclaration  ::=  VarType IDENTIFIER  (  OP_ASSIGN  Expression  | ε )
	void variabledeclaration() throws SyntaxException {
		vartype();
		match(Kind.IDENTIFIER);
		if(t.kind == Kind.OP_ASSIGN){
			match(Kind.OP_ASSIGN);
			expression();
		}
		else{
			;
		}
	}

	
	
	// VarType ::= KW_int | KW_boolean
	void vartype() throws SyntaxException {
		if(t.kind == Kind.KW_int){
			match(Kind.KW_int);
		}
		else if(t.kind == Kind.KW_boolean){
			match(Kind.KW_boolean);
		}
		else{
			throw new SyntaxException(t, "expected a token of kind KW_int or KW_boolean, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}
	
	
	
	// SourceSinkDeclaration ::= SourceSinkType IDENTIFIER  OP_ASSIGN  Source
	void sourcesinkdeclaration() throws SyntaxException {
		sourcesinktype();
		match(Kind.IDENTIFIER);
		match(Kind.OP_ASSIGN);
		source();
	}
	
	// Source ::= STRING_LITERAL  | OP_AT Expression | IDENTIFIER
	void source() throws SyntaxException {
		if(t.kind == Kind.STRING_LITERAL){
			match(Kind.STRING_LITERAL);
		}
		else if(t.kind == Kind.OP_AT){
			match(Kind.OP_AT);
			expression();
		}
		else if(t.kind == Kind.IDENTIFIER){
			match(Kind.IDENTIFIER);
		}
		else{
			throw new SyntaxException(t, "expected a token of kind STRING_LITERAL, OP_AT Expressoin or IDENTIFIER, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}
	
	// SourceSinkType := KW_url | KW_file
	void sourcesinktype() throws SyntaxException {
		if(t.kind == Kind.KW_url){
			match(Kind.KW_url);
		}
		else if(t.kind == Kind.KW_file){
			match(Kind.KW_file);
		}
		else{
			throw new SyntaxException(t, "expected a token of kind KW_url or KW_file, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}

	
	// ImageDeclaration ::=  KW_image  (LSQUARE Expression COMMA Expression RSQUARE | ε) IDENTIFIER ( OP_LARROW Source | ε )
	void imagedeclaration() throws SyntaxException {
		match(Kind.KW_image);
		if(t.kind == Kind.LSQUARE){
			match(Kind.LSQUARE);
			expression();
			match(Kind.COMMA);
			expression();
			match(Kind.RSQUARE);
		}
		else{
			;
		}
		match(Kind.IDENTIFIER);
		if(t.kind == Kind.OP_LARROW){
			match(OP_LARROW);
			source();
		}
		else{
			;
		}
	}

	
	// Statement  ::= AssignmentStatement | ImageOutStatement  | ImageInStatement
	void statement() throws SyntaxException {
		match(Kind.IDENTIFIER);
		if(t.kind == Kind.LSQUARE || t.kind == Kind.OP_ASSIGN){
			assignmentstatement();
		}
		else if(t.kind == Kind.OP_RARROW){
			imageoutstatement();
		}
		else if(t.kind == Kind.OP_LARROW){
			imageinstatement();
		}
		else{
			throw new SyntaxException(t, "expected a token of kind AssignmentStatement, ImageOutStatement or IamgeInStatement, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}
	
	// ImageOutStatement ::= IDENTIFIER OP_RARROW Sink
	void imageoutstatement() throws SyntaxException {
		match(Kind.OP_RARROW);
		sink();
	}
	
	// Sink ::= IDENTIFIER | KW_SCREEN 
	void sink() throws SyntaxException {
		if(t.kind == Kind.IDENTIFIER){
			match(Kind.IDENTIFIER);
			
		}
		else if(t.kind == Kind.KW_SCREEN){
			match(Kind.KW_SCREEN);
		}
		else{
			throw new SyntaxException(t, "expected a token of kind IDENTIFIER or KW_SCREEN, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}
	
	// ImageInStatement ::= OP_LARROW Source
	void imageinstatement() throws SyntaxException {
		match(Kind.OP_LARROW);
		source();
	}
	
	// AssignmentStatement ::= Lhs OP_ASSIGN Expression
	void assignmentstatement() throws SyntaxException {
		lhs();
		match(Kind.OP_ASSIGN);
		expression();
	}

	

	/**
	 * Expression ::=  OrExpression  (OP_Q  Expression OP_COLON Expression | ε )
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	void expression() throws SyntaxException {
		orexpression();
		if(t.kind == Kind.OP_Q){
			match(Kind.OP_Q);
			expression();
			match(OP_COLON);
			expression();
		}
	}
	
	// OrExpression ::= AndExpression   (  OP_OR  AndExpression)*
	void orexpression() throws SyntaxException {
		andexpression();
		while(t.kind == Kind.OP_OR){
			match(Kind.OP_OR);
			andexpression();
		}
	}
	
	// AndExpression ::= EqExpression ( OP_AND  EqExpression )*
	void andexpression() throws SyntaxException {
		eqexpression();
		while(t.kind == Kind.OP_AND){
			match(Kind.OP_AND);
			eqexpression();
		}
		
		//throw new UnsupportedOperationException();
	}

	
	// EqExpression ::= RelExpression  (  (OP_EQ | OP_NEQ )  RelExpression )*
	void eqexpression() throws SyntaxException {
		relexpression();
		while(t.kind == Kind.OP_EQ || t.kind == Kind.OP_NEQ){
			if(t.kind == Kind.OP_EQ){
				match(Kind.OP_EQ);
			}
			else{
				match(Kind.OP_NEQ);
			}
			
			relexpression();
		}
		
		//throw new UnsupportedOperationException();
	}
	
	// RelExpression ::= AddExpression (  ( OP_LT  | OP_GT |  OP_LE  | OP_GE )   AddExpression)*
			void relexpression() throws SyntaxException {
				addexpression();
				while(t.kind == Kind.OP_LT || t.kind == Kind.OP_GT || t.kind == Kind.OP_LE || t.kind == Kind.OP_GE){
					if(t.kind == Kind.OP_LT){
						match(Kind.OP_LT);
					}
					else if (t.kind == Kind.OP_GT){
						match(Kind.OP_GT);
					}
					else if(t.kind == Kind.OP_LE){
						match(Kind.OP_LE);
					}
					else{
						match(Kind.OP_GE);
					}
					addexpression();
				}
				
				//throw new UnsupportedOperationException();
			}
	
	// AddExpression ::= MultExpression   (  (OP_PLUS | OP_MINUS ) MultExpression )*
		void addexpression() throws SyntaxException {
			multexpression();
			while(t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS){
				if(t.kind == Kind.OP_PLUS){
					match(Kind.OP_PLUS);
				}
				else{
					match(Kind.OP_MINUS);
				}
				multexpression();
			}
			
			//throw new UnsupportedOperationException();
		}
	
	// MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV  | OP_MOD ) UnaryExpression )*
	void multexpression() throws SyntaxException {
		unaryexpression();
		while(t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || t.kind == Kind.OP_MOD ){
			if(t.kind == Kind.OP_TIMES){
				match(Kind.OP_TIMES);
			}
			else if(t.kind == Kind.OP_DIV){
				match(Kind.OP_DIV);
			}
			else{
				match(Kind.OP_MOD);
			}
			unaryexpression();
		}
		
		//throw new UnsupportedOperationException();
	}
	
	// UnaryExpression ::= OP_PLUS UnaryExpression | OP_MINUS UnaryExpression | UnaryExpressionNotPlusMinus
	void unaryexpression() throws SyntaxException {
		if(t.kind == Kind.OP_PLUS){
			match(Kind.OP_PLUS);
			unaryexpression();
		}
		else if(t.kind == Kind.OP_MINUS){
			match(Kind.OP_MINUS);
			unaryexpression();
		}
		else if(t.kind == Kind.OP_EXCL || t.kind == Kind.INTEGER_LITERAL || t.kind == Kind.LPAREN || t.kind == Kind.KW_sin || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x ||
				t.kind == Kind.KW_cart_y || t.kind == Kind.KW_polar_a || t.kind == Kind.KW_polar_r || t.kind == Kind.BOOLEAN_LITERAL || t.kind == Kind.IDENTIFIER || t.kind == Kind.KW_x || t.kind == Kind.KW_y || t.kind == Kind.KW_r ||
				t.kind == Kind.KW_a || t.kind == Kind.KW_X || t.kind == Kind.KW_Y || t.kind == Kind.KW_Z || t.kind == Kind.KW_A || t.kind == Kind.KW_R || t.kind == Kind.KW_DEF_X ||
				t.kind == Kind.KW_DEF_Y){
			unaryexpressionnotplusminus();
		}
		else{
			throw new SyntaxException(t, "expected a token of kind OP_PLUS UnaryExpression, OP_MINUS UnaryExpression,....., instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		//throw new UnsupportedOperationException();
	}
	
	// UnaryExpressionNotPlusMinus ::=  OP_EXCL  UnaryExpression  | Primary | IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X | KW_Y | KW_Z |
	void unaryexpressionnotplusminus() throws SyntaxException {
		if(t.kind == Kind.OP_EXCL){
			match(Kind.OP_EXCL);
			unaryexpression();
		}
		else if(t.kind == Kind.INTEGER_LITERAL || t.kind == Kind.LPAREN || t.kind == Kind.KW_sin || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x ||
				t.kind == Kind.KW_cart_y || t.kind == Kind.KW_polar_a || t.kind == Kind.KW_polar_r || t.kind == Kind.BOOLEAN_LITERAL ){
			primary();
		}
		else if(t.kind == Kind.IDENTIFIER){
			identorpixelselectorexpression();
		}
		else if(t.kind == Kind.KW_x){
			match(Kind.KW_x);
		}
		else if(t.kind == Kind.KW_y){
			match(Kind.KW_y);
		}
		else if(t.kind == Kind.KW_r){
			match(Kind.KW_r);
		}
		else if(t.kind == Kind.KW_a){
			match(Kind.KW_a);
		}
		else if(t.kind == Kind.KW_X){
			match(Kind.KW_X);
		}
		else if(t.kind == Kind.KW_Y){
			match(Kind.KW_Y);
		}
		else if(t.kind == Kind.KW_Z){
			match(Kind.KW_Z);
		}
		else if(t.kind == Kind.KW_A){
			match(Kind.KW_A);
		}
		else if(t.kind == Kind.KW_R){
			match(Kind.KW_R);
		}
		else if(t.kind == Kind.KW_DEF_X){
			match(Kind.KW_DEF_X);
		}
		else if(t.kind == Kind.KW_DEF_Y){
			match(Kind.KW_DEF_Y);
		}
		
		else{
			throw new SyntaxException(t, "expected a token of kind OP_EXCL UnaryExpression, Primary, IdentOrPixel ....., instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		
		//throw new UnsupportedOperationException();
	}
	
	// Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication | BOOLEAN_LITERAL
	void primary() throws SyntaxException {
		if(t.kind == Kind.INTEGER_LITERAL){
			match(Kind.INTEGER_LITERAL);
		}
		else if(t.kind == Kind.LPAREN){
			match(Kind.LPAREN);
			expression();
			match(Kind.RPAREN);
		}
		else if(t.kind == Kind.KW_sin || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x ||
				t.kind == Kind.KW_cart_y || t.kind == Kind.KW_polar_a || t.kind == Kind.KW_polar_r){
			functionapplication();
		}
		else if(t.kind == Kind.BOOLEAN_LITERAL){
			match(Kind.BOOLEAN_LITERAL);
		}
		else{
			throw new SyntaxException(t, "expected a token of kind INTEGER_LITERAL, LPAREN Expression RPAREN ....., instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		
		//throw new UnsupportedOperationException();
	}
	
	
	// IdentOrPixelSelectorExpression::=  IDENTIFIER (LSQUARE Selector RSQUARE   | ε)
	void identorpixelselectorexpression() throws SyntaxException {
		match(IDENTIFIER);
		if(t.kind == Kind.LSQUARE){
			match(Kind.LSQUARE);
			selector();
			match(Kind.RSQUARE);
		}
		else{
			;
		}
		
		//throw new UnsupportedOperationException();
	}
	
	// FunctionApplication ::= FunctionName LPAREN Expression RPAREN | FunctionName  LSQUARE Selector RSQUARE
	void functionapplication() throws SyntaxException {
		functionname();
		if(t.kind == Kind.LPAREN || t.kind == Kind.LSQUARE){
			if(t.kind == Kind.LPAREN){
				match(Kind.LPAREN);
				expression();
				match(Kind.RPAREN);
			}
			else{
				match(Kind.LSQUARE);
				selector();
				match(Kind.RSQUARE);
			}
		}
		else{
			throw new SyntaxException(t, "expected a token of kind LPAREN or LSQUARE, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line); 
		}
		
		//throw new UnsupportedOperationException();
	}
	
	// Lhs::= LSQUARE LhsSelector RSQUARE | ε
	void lhs() throws SyntaxException {
		if(t.kind == Kind.LSQUARE){
			match(Kind.LSQUARE);
			lhsselector();
			match(Kind.RSQUARE);
		}
		else{
			;
		}
		//throw new UnsupportedOperationException();
	}

	
	// FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | KW_polar_r
	void functionname() throws SyntaxException {
		if(t.kind == Kind.KW_sin){
			match(Kind.KW_sin);
		}
		else if(t.kind == Kind.KW_cos){
			match(Kind.KW_cos);
		}
		else if(t.kind == Kind.KW_atan){
			match(Kind.KW_atan);
		}
		else if(t.kind == Kind.KW_abs){
			match(Kind.KW_abs);
		}
		else if(t.kind == Kind.KW_cart_x){
			match(Kind.KW_cart_x);
		}
		else if(t.kind == Kind.KW_cart_y){
			match(Kind.KW_cart_y);
		}
		else if(t.kind == Kind.KW_cos){
			match(Kind.KW_cos);
		}else if(t.kind == Kind.KW_polar_a){
			match(Kind.KW_polar_a);
		}
		else if(t.kind == Kind.KW_polar_r){
			match(Kind.KW_polar_r);
		}
		else{
			throw new SyntaxException(t, "expected a token of kind KW_sin or KW_cos or KW_atan ....., instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		//throw new UnsupportedOperationException();
	}

	
	
	// LhsSelector ::= LSQUARE  ( XySelector  | RaSelector  )   RSQUARE
	void lhsselector() throws SyntaxException {
		match(Kind.LSQUARE);
		if(t.kind == Kind.KW_x || t.kind == Kind.KW_r ){
			if(t.kind == Kind.KW_x){
				xyselector();
			}
			else{
				raselector();
			}
		}
		else{
			throw new SyntaxException(t, "expected a Xyselector or RaSelector, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
		}
		match(Kind.RSQUARE);
		//throw new UnsupportedOperationException();
	}

	
	// XySelector ::= KW_x COMMA KW_y
	void xyselector() throws SyntaxException {
		match(Kind.KW_x);
		match(Kind.COMMA);
		match(Kind.KW_y);
		//throw new UnsupportedOperationException();
	}
	
	// RaSelector ::= KW_r COMMA KW_A
	void raselector() throws SyntaxException {
		match(Kind.KW_r);
		match(Kind.COMMA);
		match(Kind.KW_A);
		//throw new UnsupportedOperationException();
	}
	
	// Selector ::=  Expression COMMA Expression
	void selector() throws SyntaxException {
		expression();
		match(Kind.COMMA);
		expression();
		//throw new UnsupportedOperationException();
	}
	
	// match function - checks if the token is of the valid kind.
	void match(Kind kind) throws SyntaxException{
		if(t.kind == kind){
			t = scanner.nextToken();
		}
		else{
			throw new SyntaxException(t, "expected a token -  "+kind +" here, but got token"+t+" instead.\n Line num:"+t.line+"\nposition num:"+t.pos_in_line);
		}
	}


//--------------------------------------------------------------------------------------------------------------------------------------methods end
	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
