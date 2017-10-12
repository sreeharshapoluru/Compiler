package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cop5556fa17.AST.*;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

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

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}
	
//---------------------------------------------------------------------------------------------------------------------methods start
	Program program() throws SyntaxException {
		Token firstToken = t;
		Token name = t;
		match(Kind.IDENTIFIER);
		ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		while(t.kind == Kind.KW_int || t.kind == Kind.KW_boolean || t.kind == Kind.KW_image || t.kind == Kind.KW_url || t.kind == Kind.KW_file ||
				t.kind == Kind.IDENTIFIER ){
			if(t.kind == Kind.KW_int || t.kind == Kind.KW_boolean || t.kind == Kind.KW_image || t.kind == Kind.KW_url || t.kind == Kind.KW_file ){
				ASTNode astNode = declaration();
				match(Kind.SEMI);
				decsAndStatements.add(astNode);
			}
			else{
				ASTNode astNode = statement();
				match(Kind.SEMI);
				decsAndStatements.add(astNode);
			}
			
		}
		return new Program(firstToken, name, decsAndStatements);
		//throw new UnsupportedOperationException();
	}
	
	// Declaration :: =  VariableDeclaration     |    ImageDeclaration   |   SourceSinkDeclaration 
	Declaration declaration() throws SyntaxException {
		Token firstToken = t;
		if(t.kind == Kind.KW_int || t.kind == Kind.KW_boolean){
			Declaration d = variabledeclaration();
			return d;
		}
		else if(t.kind == Kind.KW_image){
			Declaration d =imagedeclaration();
			return d;
		}
		else if(t.kind == Kind.KW_url || t.kind == Kind.KW_file){
			Declaration d = sourcesinkdeclaration();
			return d;
		}
		else{
			throw new SyntaxException(t, "expected a token of kind VariableDeclaration, ImageDeclaration or SourceSinkDeclaration, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		//throw new UnsupportedOperationException();
	}
	
	// VariableDeclaration  ::=  VarType IDENTIFIER  (  OP_ASSIGN  Expression  | ε )
	Declaration_Variable variabledeclaration() throws SyntaxException {
		Token firstToken = t;
		Token type = vartype();
		Token name = t;
		match(Kind.IDENTIFIER);
		if(t.kind == Kind.OP_ASSIGN){
			match(Kind.OP_ASSIGN);
			Expression e = expression();
			return new Declaration_Variable(firstToken, type, name, e);
		}
		else{
			return new Declaration_Variable(firstToken, type, name, null);
		}
	}

	
	
	// VarType ::= KW_int | KW_boolean
	Token vartype() throws SyntaxException {
		if(t.kind == Kind.KW_int){
			Token returnToken = t;
			match(Kind.KW_int);
			return returnToken;
		}
		else if(t.kind == Kind.KW_boolean){
			Token returnToken = t;
			match(Kind.KW_boolean);
			return returnToken;
		}
		else{
			throw new SyntaxException(t, "expected a token of kind KW_int or KW_boolean, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}
	
	
	
	// SourceSinkDeclaration ::= SourceSinkType IDENTIFIER  OP_ASSIGN  Source
	Declaration_SourceSink sourcesinkdeclaration() throws SyntaxException {
		Token firstToken = t;
		Token type = sourcesinktype();
		Token name = t;
		match(Kind.IDENTIFIER);
		match(Kind.OP_ASSIGN);
		Source source = source();
		return new Declaration_SourceSink(firstToken, type, name, source);
	}
	
	// Source ::= STRING_LITERAL  | OP_AT Expression | IDENTIFIER
	Source source() throws SyntaxException {
		Token firstToken = t;
		if(t.kind == Kind.STRING_LITERAL){
			Token fileOrUrl = t;
			match(Kind.STRING_LITERAL);
			return new Source_StringLiteral(firstToken, fileOrUrl.getText());
		}
		else if(t.kind == Kind.OP_AT){
			match(Kind.OP_AT);
			Expression paramNum = expression();
			return new Source_CommandLineParam(firstToken, paramNum);
			
		}
		else if(t.kind == Kind.IDENTIFIER){
			Token name = t;
			match(Kind.IDENTIFIER);
			return new Source_Ident(firstToken, name);
		}
		else{
			throw new SyntaxException(t, "expected a token of kind STRING_LITERAL, OP_AT Expressoin or IDENTIFIER, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}
	
	// SourceSinkType := KW_url | KW_file
	Token sourcesinktype() throws SyntaxException {
		if(t.kind == Kind.KW_url){
			Token returnToken = t;
			match(Kind.KW_url);
			return returnToken;
		}
		else if(t.kind == Kind.KW_file){
			Token returnToken = t;
			match(Kind.KW_file);
			return returnToken;
		}
		else{
			throw new SyntaxException(t, "expected a token of kind KW_url or KW_file, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}

	
	// ImageDeclaration ::=  KW_image  (LSQUARE Expression COMMA Expression RSQUARE | ε) IDENTIFIER ( OP_LARROW Source | ε )
	Declaration_Image imagedeclaration() throws SyntaxException {
		Token firstToken = t;
		Expression xSize = null, ySize = null;
		Token name = null;
		Source source = null;
		match(Kind.KW_image);
		if(t.kind == Kind.LSQUARE){
			match(Kind.LSQUARE);
			xSize = expression();
			match(Kind.COMMA);
			ySize = expression();
			match(Kind.RSQUARE);
			
		}
		else{
			;
		}
		name = t;
		match(Kind.IDENTIFIER);
		if(t.kind == Kind.OP_LARROW){
			match(OP_LARROW);
			source = source();
		}
		else{
			;
		}
		return new Declaration_Image(firstToken, xSize, ySize, name, source);
	}

	
	// Statement  ::= AssignmentStatement | ImageOutStatement  | ImageInStatement
	Statement statement() throws SyntaxException {
		Token firstToken = t;
		match(Kind.IDENTIFIER);
		if(t.kind == Kind.LSQUARE || t.kind == Kind.OP_ASSIGN){
			Statement s = assignmentstatement(firstToken);
			return s;
		}
		else if(t.kind == Kind.OP_RARROW){
			Statement s = imageoutstatement(firstToken);
			return s;
		}
		else if(t.kind == Kind.OP_LARROW){
			Statement s = imageinstatement(firstToken);
			return s;
		}
		else{
			throw new SyntaxException(t, "expected a token of kind AssignmentStatement, ImageOutStatement or IamgeInStatement, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}
	
	// ImageOutStatement ::= IDENTIFIER OP_RARROW Sink
	Statement_Out imageoutstatement(Token sentToken) throws SyntaxException {
		Token firstToken =sentToken;
		Token name = firstToken;
		match(Kind.OP_RARROW);
		Sink sink = sink();
		return new Statement_Out(firstToken, name, sink);
	}
	
	// Sink ::= IDENTIFIER | KW_SCREEN 
	Sink sink() throws SyntaxException {
		Token firstToken = t;
		if(t.kind == Kind.IDENTIFIER){
			Token name = t;
			match(Kind.IDENTIFIER);
			return new Sink_Ident(firstToken, name);
			
		}
		else if(t.kind == Kind.KW_SCREEN){
			match(Kind.KW_SCREEN);
			return new Sink_SCREEN(firstToken);
		}
		else{
			throw new SyntaxException(t, "expected a token of kind IDENTIFIER or KW_SCREEN, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
	}
	
	// ImageInStatement ::= OP_LARROW Source
	Statement_In imageinstatement(Token sentToken) throws SyntaxException {
		Token firstToken = sentToken;
		Token name =t;
		match(Kind.OP_LARROW);
		Source source = source();
		return new Statement_In(firstToken, name, source);
	}
	
	// AssignmentStatement ::= Lhs OP_ASSIGN Expression
	Statement_Assign assignmentstatement(Token sentToken) throws SyntaxException {
		Token firstToken = sentToken;
		LHS lhs = lhs(sentToken);
		match(Kind.OP_ASSIGN);
		Expression e = expression();
		return new Statement_Assign(firstToken, lhs, e);
	}

	

	/**
	 * Expression ::=  OrExpression  (OP_Q  Expression OP_COLON Expression | ε )
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	public Expression expression() throws SyntaxException {
		Token firstToken = t;
		Expression condition =orexpression();
		if(t.kind == Kind.OP_Q){
			match(Kind.OP_Q);
			Expression trueExpression = expression();
			match(OP_COLON);
			Expression falseExpression = expression();
			return new Expression_Conditional(firstToken, condition, trueExpression, falseExpression);
		}
		else{
			return new Expression_Conditional(firstToken, condition, null, null);
		}
			
	}
	
	// OrExpression ::= AndExpression   (  OP_OR  AndExpression)*
	Expression orexpression() throws SyntaxException {
		Token firstToken = t;
		Token op = null;
		Expression e0 = null, e1 = null;
		e0 = andexpression();
		while(t.kind == Kind.OP_OR){
			op =t;
			match(Kind.OP_OR);
			e1 = andexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		
	}
	
	// AndExpression ::= EqExpression ( OP_AND  EqExpression )*
	Expression andexpression() throws SyntaxException {
		Token firstToken = t;
		Token op = null;
		Expression e0 = null, e1 = null;
		e0 = eqexpression();
		while(t.kind == Kind.OP_AND){
			op =t;
			match(Kind.OP_AND);
			e1 = eqexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		
		//throw new UnsupportedOperationException();
	}

	
	// EqExpression ::= RelExpression  (  (OP_EQ | OP_NEQ )  RelExpression )*
	Expression eqexpression() throws SyntaxException {
		Token firstToken = t;
		Token op = null;
		Expression e0 = null, e1 = null;
		e0 = relexpression();
		while(t.kind == Kind.OP_EQ || t.kind == Kind.OP_NEQ){
			if(t.kind == Kind.OP_EQ){
				op =t;
				match(Kind.OP_EQ);
			}
			else{
				op =t;
				match(Kind.OP_NEQ);
			}
			
			e1 = relexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		
		//throw new UnsupportedOperationException();
	}
	
	// RelExpression ::= AddExpression (  ( OP_LT  | OP_GT |  OP_LE  | OP_GE )   AddExpression)*
			Expression relexpression() throws SyntaxException {
				Token firstToken = t;
				Token op = null;
				Expression e0 = null, e1 = null;
				e0 = addexpression();
				while(t.kind == Kind.OP_LT || t.kind == Kind.OP_GT || t.kind == Kind.OP_LE || t.kind == Kind.OP_GE){
					if(t.kind == Kind.OP_LT){
						op =t;
						match(Kind.OP_LT);
					}
					else if (t.kind == Kind.OP_GT){
						op =t;
						match(Kind.OP_GT);
					}
					else if(t.kind == Kind.OP_LE){
						op =t;
						match(Kind.OP_LE);
					}
					else{
						op =t;
						match(Kind.OP_GE);
					}
					e1 = addexpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				}
				return e0;
				
				//throw new UnsupportedOperationException();
			}
	
	// AddExpression ::= MultExpression   (  (OP_PLUS | OP_MINUS ) MultExpression )*
		Expression addexpression() throws SyntaxException {
			Token firstToken = t;
			Token op = null;
			Expression e0 = null, e1 = null;
			e0 = multexpression();
			while(t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS){
				if(t.kind == Kind.OP_PLUS){
					op = t;
					match(Kind.OP_PLUS);
				}
				else{
					op = t;
					match(Kind.OP_MINUS);
				}
				e1 = multexpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
			}
			return e0;
			
			//throw new UnsupportedOperationException();
		}
	
	// MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV  | OP_MOD ) UnaryExpression )*
	Expression multexpression() throws SyntaxException {
		Token firstToken = t;
		Token op = null;
		Expression e0 = null, e1= null;
		e0 = unaryexpression();
		while(t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || t.kind == Kind.OP_MOD ){
			if(t.kind == Kind.OP_TIMES){
				op = t;
				match(Kind.OP_TIMES);
			}
			else if(t.kind == Kind.OP_DIV){
				op = t;
				match(Kind.OP_DIV);
			}
			else{
				op = t;
				match(Kind.OP_MOD);
			}
			e1 = unaryexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		
		//throw new UnsupportedOperationException();
	}
	
	// UnaryExpression ::= OP_PLUS UnaryExpression | OP_MINUS UnaryExpression | UnaryExpressionNotPlusMinus
	Expression unaryexpression() throws SyntaxException {
		Token firstToken = t;
		Token op = null;
		if(t.kind == Kind.OP_PLUS){
			op = t;
			match(Kind.OP_PLUS);
			Expression e =unaryexpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else if(t.kind == Kind.OP_MINUS){
			op = t;
			match(Kind.OP_MINUS);
			Expression e = unaryexpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else if(t.kind == Kind.OP_EXCL || t.kind == Kind.INTEGER_LITERAL || t.kind == Kind.LPAREN || t.kind == Kind.KW_sin || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x ||
				t.kind == Kind.KW_cart_y || t.kind == Kind.KW_polar_a || t.kind == Kind.KW_polar_r || t.kind == Kind.BOOLEAN_LITERAL || t.kind == Kind.IDENTIFIER || t.kind == Kind.KW_x || t.kind == Kind.KW_y || t.kind == Kind.KW_r ||
				t.kind == Kind.KW_a || t.kind == Kind.KW_X || t.kind == Kind.KW_Y || t.kind == Kind.KW_Z || t.kind == Kind.KW_A || t.kind == Kind.KW_R || t.kind == Kind.KW_DEF_X ||
				t.kind == Kind.KW_DEF_Y){
			Expression e =unaryexpressionnotplusminus();
			return e;
		}
		else{
			throw new SyntaxException(t, "expected a token of kind OP_PLUS UnaryExpression, OP_MINUS UnaryExpression,....., instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		//throw new UnsupportedOperationException();
	}
	
	// UnaryExpressionNotPlusMinus ::=  OP_EXCL  UnaryExpression  | Primary | IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X | KW_Y | KW_Z |
	Expression unaryexpressionnotplusminus() throws SyntaxException {
		Token firstToken = t;
		if(t.kind == Kind.OP_EXCL){
			Token op = t;
			match(Kind.OP_EXCL);
			Expression e = unaryexpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else if(t.kind == Kind.INTEGER_LITERAL || t.kind == Kind.LPAREN || t.kind == Kind.KW_sin || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x ||
				t.kind == Kind.KW_cart_y || t.kind == Kind.KW_polar_a || t.kind == Kind.KW_polar_r || t.kind == Kind.BOOLEAN_LITERAL ){
			Expression e = primary();
			return e;
		}
		else if(t.kind == Kind.IDENTIFIER){
			Expression e = identorpixelselectorexpression();
			return e;
		}
		else if(t.kind == Kind.KW_x){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_x);
			return e;
		}
		else if(t.kind == Kind.KW_y){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_y);
			return e;
		}
		else if(t.kind == Kind.KW_r){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_r);
			return e;
		}
		else if(t.kind == Kind.KW_a){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_a);
			return e;
		}
		else if(t.kind == Kind.KW_X){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_X);
			return e;
		}
		else if(t.kind == Kind.KW_Y){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_Y);
			return e;
		}
		else if(t.kind == Kind.KW_Z){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_Z);
			return e;
		}
		else if(t.kind == Kind.KW_A){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_A);
			return e;
		}
		else if(t.kind == Kind.KW_R){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_R);
			return e;
		}
		else if(t.kind == Kind.KW_DEF_X){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_DEF_X);
			return e;
		}
		else if(t.kind == Kind.KW_DEF_Y){
			Expression e = new Expression_PredefinedName(firstToken, t.kind);
			match(Kind.KW_DEF_Y);
			return e;
		}
		
		else{
			throw new SyntaxException(t, "expected a token of kind OP_EXCL UnaryExpression, Primary, IdentOrPixel ....., instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		
		//throw new UnsupportedOperationException();
	}
	
	// Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication | BOOLEAN_LITERAL
	Expression primary() throws SyntaxException {
		Token firstToken = t;
		if(t.kind == Kind.INTEGER_LITERAL){
			Expression e = new Expression_IntLit(firstToken, t.intVal());
			match(Kind.INTEGER_LITERAL);
			return e;
		}
		else if(t.kind == Kind.LPAREN){
			match(Kind.LPAREN);
			Expression e = expression();
			match(Kind.RPAREN);
			return e;
		}
		else if(t.kind == Kind.KW_sin || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x ||
				t.kind == Kind.KW_cart_y || t.kind == Kind.KW_polar_a || t.kind == Kind.KW_polar_r){
			Expression e = functionapplication();
			return e;
		}
		else if(t.kind == Kind.BOOLEAN_LITERAL){
			Expression e = new Expression_BooleanLit(firstToken, t.getText().equals("true"));
			match(Kind.BOOLEAN_LITERAL);
			return e;
		}
		else{
			throw new SyntaxException(t, "expected a token of kind INTEGER_LITERAL, LPAREN Expression RPAREN ....., instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
			
		}
		
		//throw new UnsupportedOperationException();
	}
	
	
	// IdentOrPixelSelectorExpression::=  IDENTIFIER (LSQUARE Selector RSQUARE   | ε)
	Expression identorpixelselectorexpression() throws SyntaxException {
		Token firstToken = t;
		match(IDENTIFIER);
		if(t.kind == Kind.LSQUARE){
			match(Kind.LSQUARE);
			Index i = selector();
			match(Kind.RSQUARE);
			return new Expression_PixelSelector(firstToken, firstToken, i);
		}
		else{
			 return new Expression_Ident(firstToken, firstToken);
		}
		
		//throw new UnsupportedOperationException();
	}
	
	// FunctionApplication ::= FunctionName LPAREN Expression RPAREN | FunctionName  LSQUARE Selector RSQUARE
	Expression functionapplication() throws SyntaxException {
		Token firstToken = t;
		Kind function = t.kind;
		functionname();
		if(t.kind == Kind.LPAREN || t.kind == Kind.LSQUARE){
			if(t.kind == Kind.LPAREN){
				match(Kind.LPAREN);
				Expression e  = expression();
				match(Kind.RPAREN);
				return new Expression_FunctionAppWithExprArg(firstToken, function, e);
			}
			else{
				match(Kind.LSQUARE);
				Index i = selector();
				match(Kind.RSQUARE);
				return new Expression_FunctionAppWithIndexArg(firstToken, function, i);
			}
		}
		else{
			throw new SyntaxException(t, "expected a token of kind LPAREN or LSQUARE, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line); 
		}
		//throw new UnsupportedOperationException();
	}
	
	// Lhs::= LSQUARE LhsSelector RSQUARE | ε
	LHS lhs(Token sentToken) throws SyntaxException {
		Token firstToken = sentToken;
		if(t.kind == Kind.LSQUARE){
			match(Kind.LSQUARE);
			Index i = lhsselector();
			match(Kind.RSQUARE);
			return new LHS(firstToken, firstToken, i);
		}
		else{
			return new LHS(firstToken, firstToken, null);
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
	Index lhsselector() throws SyntaxException {
		Index i =null;
		match(Kind.LSQUARE);
		if(t.kind == Kind.KW_x || t.kind == Kind.KW_r ){
			if(t.kind == Kind.KW_x){
				i = xyselector();
			}
			else{
				i = raselector();
			}
		}
		else{
			throw new SyntaxException(t, "expected a Xyselector or RaSelector, instead got"+t+"\nLine num:"+t.line+"\nposition num:"+t.pos_in_line);
		}
		match(Kind.RSQUARE);
		return i;
		//throw new UnsupportedOperationException();
	}

	
	// XySelector ::= KW_x COMMA KW_y
	Index xyselector() throws SyntaxException {
		Token firstToken = t;
		Expression e0 =null, e1 = null;
		e0 = new Expression_PredefinedName(firstToken, t.kind);
		match(Kind.KW_x);
		match(Kind.COMMA);
		e1 = new Expression_PredefinedName(firstToken, t.kind);
		match(Kind.KW_y);
		return new Index(firstToken,e0,e1);
		//throw new UnsupportedOperationException();
	}
	
	// RaSelector ::= KW_r COMMA KW_A
	Index raselector() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null, e1 = null;
		e0 = new Expression_PredefinedName(firstToken, t.kind);
		match(Kind.KW_r);
		match(Kind.COMMA);
		e1 = new Expression_PredefinedName(firstToken, t.kind);
		match(Kind.KW_A);
		return new Index(firstToken,e0,e1);
		
		//throw new UnsupportedOperationException();
	}
	
	// Selector ::=  Expression COMMA Expression
	Index selector() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null, e1 = null;
		e0 = expression();
		match(Kind.COMMA);
		e1 = expression();
		return new Index(firstToken,e0,e1);
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
