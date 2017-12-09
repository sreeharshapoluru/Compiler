package cop5556fa17;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {
	
	public Map<String , ASTNode> symbolTable = new HashMap<String,ASTNode>();
	

		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		
		public boolean isValid(String fileOrURL) {
			try {
				URL url = new URL(fileOrURL);
				url.toURI();
				return true;
			}
			catch(Exception e) {
				System.out.println(e);
				return false;
			}
		}
		

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		if(declaration_Variable.e != null) {
			declaration_Variable.e.visit(this, arg);
		}
		
		if(symbolTable.containsKey(declaration_Variable.name)){
			throw new SemanticException(declaration_Variable.type, "Token already in the symbol table");
		}
		else {
			symbolTable.put(declaration_Variable.name, declaration_Variable);
			
			}
		declaration_Variable.typeName = TypeUtils.getType(declaration_Variable.type);
		if(declaration_Variable.e != null) {
			if(declaration_Variable.typeName == declaration_Variable.e.typeName) {
				;
			}
			else {
				throw new SemanticException(declaration_Variable.firstToken, "The type of the expression and the declared type is not same");
			}
		}
		return declaration_Variable.typeName;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		if(expression_Binary.op == Kind.OP_EQ || expression_Binary.op == Kind.OP_NEQ){
			expression_Binary.typeName = Type.BOOLEAN;
		}
		else if ((expression_Binary.op == Kind.OP_GT || expression_Binary.op == Kind.OP_GE || 
				expression_Binary.op == Kind.OP_LT || expression_Binary.op == Kind.OP_LE) &&
				expression_Binary.e0.typeName == Type.INTEGER) {
			expression_Binary.typeName = Type.BOOLEAN;
		}
		else if((expression_Binary.op == Kind.OP_AND || expression_Binary.op == Kind.OP_OR) &&
				(expression_Binary.e0.typeName == Type.INTEGER || expression_Binary.e0.typeName == 
				Type.BOOLEAN)) {
			expression_Binary.typeName = expression_Binary.e0.typeName;
		}
		else if((expression_Binary.op == Kind.OP_DIV || expression_Binary.op == Kind.OP_MINUS ||
				expression_Binary.op == Kind.OP_MOD || expression_Binary.op == Kind.OP_PLUS ||
				expression_Binary.op == Kind.OP_POWER || expression_Binary.op == Kind.OP_TIMES) &&
				expression_Binary.e0.typeName == Type.INTEGER) {
			expression_Binary.typeName = Type.INTEGER;
		}
		else {
			expression_Binary.typeName = Type.NONE;
		}
		if(expression_Binary.e0.typeName == expression_Binary.e1.typeName && expression_Binary.typeName != Type.NONE) {
			;
		}
		else {
			throw new SemanticException(expression_Binary.firstToken, "The type of e0 anmd e1 are not equal or type of binary expression is null");
		}
		return expression_Binary.typeName;
		
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		expression_Unary.e.visit(this, arg);
		if(expression_Unary.op == Kind.OP_EXCL) {
			if(expression_Unary.e.typeName == Type.BOOLEAN) {
				expression_Unary.typeName = Type.BOOLEAN;
			}
			if(expression_Unary.e.typeName == Type.INTEGER) {
				expression_Unary.typeName = Type.INTEGER;
			}
		}
		else if((expression_Unary.op == Kind.OP_PLUS || expression_Unary.op == Kind.OP_MINUS) &&
				expression_Unary.e.typeName == Type.INTEGER) {
			expression_Unary.typeName = Type.INTEGER;
		}
		else {
			expression_Unary.typeName = Type.NONE;
		}
		if(expression_Unary.typeName != Type.NONE) {
			;
		}
		else {
			throw new SemanticException(expression_Unary.firstToken, "The type of the unary expression is None");
		}
		return expression_Unary.typeName;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if(index.e0.typeName == Type.INTEGER && index.e1.typeName == Type.INTEGER) {
			;
		}
		else {
			throw new SemanticException(index.firstToken, "Either type of e0 or the type of e1 is not integer"+index.firstToken.toString());
		}
		index.setCartesian(!((index.e0.firstToken.kind  == Kind.KW_r && 
				index.e0.getClass() == Expression_PredefinedName.class) && 
				(index.e1.firstToken.kind == Kind.KW_a && 
				index.e1.getClass() == Expression_PredefinedName.class)));
		return index.typeName;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		if(expression_PixelSelector.index != null) {
			expression_PixelSelector.index.visit(this, arg);
		}
		if(symbolTable.containsKey(expression_PixelSelector.name)) {
			Type temp = ((Declaration)symbolTable.get(expression_PixelSelector.name)).typeName;
			if(temp == Type.IMAGE) {
				expression_PixelSelector.typeName = Type.INTEGER;
			}
			else if(expression_PixelSelector.index == null) {
				expression_PixelSelector.typeName = temp;
			}
			else {
				expression_PixelSelector.typeName = Type.NONE;
			}
			if(expression_PixelSelector.typeName != Type.NONE) {
				;
			}
			else {
				throw new SemanticException(expression_PixelSelector.firstToken, "The type of the pixel selector expression is None");
			}
		}
		else {
			throw new SemanticException(expression_PixelSelector.firstToken, "Name is not in the symbol table. Trying to access variable that is not declared");
			
		}
		return expression_PixelSelector.typeName;
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		
		expression_Conditional.condition.visit(this, arg);
		if(expression_Conditional.trueExpression != null) {
			expression_Conditional.trueExpression.visit(this, arg);
		}
		if(expression_Conditional.falseExpression != null) {
			expression_Conditional.falseExpression.visit(this, arg);
		}
		expression_Conditional.typeName = expression_Conditional.trueExpression.typeName;
		if(expression_Conditional.condition.typeName == Type.BOOLEAN && 
				expression_Conditional.trueExpression.typeName == 
				expression_Conditional.falseExpression.typeName) {
			;
		}
		else {
			throw new SemanticException(expression_Conditional.firstToken, "The type of the condition is not boolean or true expression type is not equal to the false expression type");
		}
		
		return expression_Conditional.typeName;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		 if (declaration_Image.xSize != null){
			 declaration_Image.xSize.visit(this, arg);
		 }
		 if (declaration_Image.ySize != null) {
			 declaration_Image.ySize.visit(this, arg);
		 }
		if(declaration_Image.source != null) {
			declaration_Image.source.visit(this, arg);
		}
		
		if(symbolTable.containsKey(declaration_Image.name)) {
			throw new SemanticException(declaration_Image.firstToken, "The identifier has already been declared");
		}
		else {
			symbolTable.put(declaration_Image.name, declaration_Image);
		}
		declaration_Image.typeName = Type.IMAGE;
		if(declaration_Image.xSize != null) {
			if(declaration_Image.ySize != null && declaration_Image.xSize.typeName == Type.INTEGER && declaration_Image.ySize.typeName == Type.INTEGER) {
				;
			}
			else {
				throw new SemanticException(declaration_Image.firstToken, "Either ysize is null or type of xSize is not integer or type of ySize is not integer ");
			}
		}
		
		return declaration_Image.typeName;
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		if(isValid(source_StringLiteral.fileOrUrl)) {
			source_StringLiteral.typeName = Type.URL;
		}
		else {
			source_StringLiteral.typeName = Type.FILE;
		}
		return source_StringLiteral.typeName;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		source_CommandLineParam.paramNum.visit(this, arg);
		source_CommandLineParam.typeName =null;
		if(source_CommandLineParam.paramNum.typeName == Type.INTEGER) {
			;
		}
		else {
			throw new SemanticException(source_CommandLineParam.firstToken, "The type of the source commandline is not integer");
		}
		return source_CommandLineParam.typeName;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		if(symbolTable.containsKey(source_Ident.name)) {
			source_Ident.typeName = ((Declaration)symbolTable.get(source_Ident.name)).typeName;
			if(source_Ident.typeName == Type.FILE || source_Ident.typeName == Type.URL) {
				;
			}
			else {
				throw new SemanticException(source_Ident.firstToken, "The type of source ident is not either file or url");
			}
		}
		else {
			throw new SemanticException(source_Ident.firstToken, "Name is not in the symbol table. Trying to access variable that is not declared"); 
		}
		return source_Ident.typeName;
		
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		if(declaration_SourceSink.source != null) {
			declaration_SourceSink.source.visit(this, arg);
		}
		
		if(symbolTable.containsKey(declaration_SourceSink.name)) {
			throw new SemanticException(declaration_SourceSink.firstToken, "The token has already been declared");
		}
		else {
			symbolTable.put(declaration_SourceSink.name, declaration_SourceSink);
			
		}
		declaration_SourceSink.typeName = TypeUtils.getType(declaration_SourceSink.type);
		if(declaration_SourceSink.source.typeName == declaration_SourceSink.typeName || declaration_SourceSink.source.typeName == null) {
			;
		}
		else {
			throw new SemanticException(declaration_SourceSink.firstToken, "The type of source and the declaration _sourcesink expression are not same");
		}
		return declaration_SourceSink.typeName;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		expression_IntLit.typeName = Type.INTEGER;
		return expression_IntLit.typeName;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.arg.typeName == Type.INTEGER) {
			;
		}
		else {
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "The type of the  is not integer (which is expected)");
		}
		expression_FunctionAppWithExprArg.typeName = Type.INTEGER;
		return expression_FunctionAppWithExprArg.typeName;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		expression_FunctionAppWithIndexArg.arg.visit(this, arg);
		expression_FunctionAppWithIndexArg.typeName = Type.INTEGER;
		return expression_FunctionAppWithIndexArg.typeName;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		expression_PredefinedName.typeName = Type.INTEGER;
		return expression_PredefinedName.typeName;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		statement_Out.sink.visit(this, arg);
		if(symbolTable.containsKey(statement_Out.name)) {
			statement_Out.setDec((Declaration)symbolTable.get(statement_Out.name));
			if((Declaration)symbolTable.get(statement_Out.name) != null) {
				;
			}
			else {
				throw new SemanticException(statement_Out.firstToken, "The declaration of the name is null");
			}
			if(((((Declaration)symbolTable.get(statement_Out.name)).typeName == Type.INTEGER ||
					((Declaration)symbolTable.get(statement_Out.name)).typeName == Type.BOOLEAN) 
					&& statement_Out.sink.typeName == Type.SCREEN) || 
					(((Declaration)symbolTable.get(statement_Out.name)).typeName == Type.IMAGE
					&& (statement_Out.sink.typeName == Type.FILE || 
					statement_Out.sink.typeName == Type.SCREEN))){
				;
				
			}
			else {
				throw new SemanticException(statement_Out.firstToken, "The type of name and sink is not according to the requirements");
			}
			
			
		}
		else {
			throw new SemanticException(statement_Out.firstToken, "Name is not in the symbol table. Trying to access variable that is not declared");
		}
		return statement_Out.typeName;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		statement_In.source.visit(this, arg);
		if(symbolTable.containsKey(statement_In.name)) {
			statement_In.setDec((Declaration)symbolTable.get(statement_In.name));
			/*if((Declaration)symbolTable.get(statement_In.name) != null && symbolTable.get(statement_In.name).typeName == statement_In.source.typeName){
				;
			}
			else {
				throw new SemanticException(statement_In.firstToken, "The declaration of name is null or type of name is not same as the type of source");
			}*/
		}
		else {
			throw new SemanticException(statement_In.firstToken, "Name is not in the symbol table. Trying to access variable that is not declared");
		}
		 return statement_In.typeName;
		
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		statement_Assign.e.visit(this, arg);
		statement_Assign.lhs.visit(this, arg);
		if(statement_Assign.lhs.typeName == statement_Assign.e.typeName) {
		;
		}
		else {
			if(statement_Assign.lhs.typeName == Type.IMAGE && statement_Assign.e.typeName == Type.INTEGER) {
				;
			}
			else {
				
			throw new SemanticException(statement_Assign.firstToken, "lhs type and expression types are not same");
			}
		}
		statement_Assign.setCartesian(statement_Assign.lhs.isCartesian());
		return statement_Assign.typeName;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		if(lhs.index != null) {
			lhs.index.visit(this, arg);
		}
		
		if(symbolTable.containsKey(lhs.name)) {
			lhs.setDec((Declaration)symbolTable.get(lhs.name));
			lhs.typeName = lhs.getDec().typeName;
			if(lhs.index != null) {
			lhs.setCartesian(lhs.index.isCartesian());
			}
		}
		else {
			throw new SemanticException(lhs.firstToken, "Name is not in the symbol table. Trying to access variable that is not declared");
		}
		return lhs.typeName;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		sink_SCREEN.typeName = Type.SCREEN;
		return sink_SCREEN.typeName;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		if(symbolTable.containsKey(sink_Ident.name)) {
		sink_Ident.typeName = ((Declaration)symbolTable.get(sink_Ident.name)).typeName;
		if(sink_Ident.typeName == Type.FILE)
		{
			;
		}
		else {
			throw new SemanticException(sink_Ident.firstToken, " type of the sink_ident is not file");
		}
		}
		else {
			throw new SemanticException(sink_Ident.firstToken, "Name is not in the symbol table. Trying to access variable that is not declared");
		}
		return sink_Ident.typeName;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		expression_BooleanLit.typeName = Type.BOOLEAN;
		return expression_BooleanLit.typeName;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		if(symbolTable.containsKey(expression_Ident.name)){
			expression_Ident.typeName = ((Declaration)symbolTable.get(expression_Ident.name)).typeName;
		}
		else {
			throw new SemanticException(expression_Ident.firstToken, "Name is not in the symbol table. Trying to access variable that is not declared");
		}
		return expression_Ident.typeName;
	}

}
