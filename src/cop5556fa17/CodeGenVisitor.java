package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


import cop5556fa17.Scanner.Kind;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
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
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
// import cop5556fa17.image.ImageFrame;
// import cop5556fa17.image.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	

	//  Program ::=  name (Declaration | Statement)*
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// ---------------static start ---------------------------------------------------------------------
		FieldVisitor fv = cw.visitField(ACC_STATIC, "x", "I", null, false);
		fv.visitEnd();
	
		fv = cw.visitField(ACC_STATIC, "y", "I", null, false);
		fv.visitEnd();
	
		fv = cw.visitField(ACC_STATIC, "X", "I", null, false);
		fv.visitEnd();

		fv = cw.visitField(ACC_STATIC, "Y", "I", null, false);
		fv.visitEnd();
		
		fv = cw.visitField(ACC_STATIC, "r", "I", null, false);
		fv.visitEnd();
	
		fv = cw.visitField(ACC_STATIC, "a", "I", null, false);
		fv.visitEnd();
	
		fv = cw.visitField(ACC_STATIC, "A", "I", null, false);
		fv.visitEnd();
	
		fv = cw.visitField(ACC_STATIC, "R", "I", null, false);
		fv.visitEnd();
		
		fv = cw.visitField(ACC_STATIC, "DEF_X", "I", null, 256);
		fv.visitEnd();
		
		fv = cw.visitField(ACC_STATIC, "DEF_Y", "I", null, 256);
		fv.visitEnd();
		
		fv = cw.visitField(ACC_STATIC, "Z", "I", null, 0xFFFFFF);
		fv.visitEnd();
	
		//----------------------static end-----------------------------------------------------------------
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
	
		//terminate construction of main method
				mv.visitEnd();
				
		//terminate class construction
				cw.visitEnd();

		//generate classfile as byte array and return
				return cw.toByteArray();
	}
	
	//  Declaration_Variable ::=  Type name (Expression | ε )
	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		
		if(declaration_Variable.typeName == Type.BOOLEAN) {
			
			FieldVisitor fv = cw.visitField(ACC_STATIC, declaration_Variable.name, "Z", null, false);
			fv.visitEnd();
		}
		if(declaration_Variable.typeName == Type.INTEGER) {
			FieldVisitor fv = cw.visitField(ACC_STATIC, declaration_Variable.name, "I", null, 0);
			fv.visitEnd();
		}
		
		if(declaration_Variable.e != null) {
			declaration_Variable.e.visit(this, arg);
			if(declaration_Variable.e.typeName == Type.INTEGER) {
				mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, "I");
				
			}
			if(declaration_Variable.e.typeName == Type.BOOLEAN) {
				mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, "Z");
			}
		}
		return null;
	}

	//  Expression_Binary ::= Expression0 op Expression1

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		Label geTrue = new Label();
		Label gtTrue = new Label();
		Label leTrue = new Label();
		Label ltTrue = new Label();
		Label eqTrue = new Label();
		Label neqTrue = new Label();
		Label afterLabel = new Label();
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		if(expression_Binary.op == Kind.OP_GE) {
			mv.visitJumpInsn(IF_ICMPGE, geTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, afterLabel);
			mv.visitLabel(geTrue);
			mv.visitInsn(ICONST_1);
			
		}
		if(expression_Binary.op == Kind.OP_GT) {
			mv.visitJumpInsn(IF_ICMPGT, gtTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, afterLabel);
			mv.visitLabel(gtTrue);
			mv.visitInsn(ICONST_1);
		}
		if(expression_Binary.op == Kind.OP_LE) {
			mv.visitJumpInsn(IF_ICMPLE, leTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, afterLabel);
			mv.visitLabel(leTrue);
			mv.visitInsn(ICONST_1);
		}
		if(expression_Binary.op == Kind.OP_LT) {
			mv.visitJumpInsn(IF_ICMPLT, ltTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, afterLabel);
			mv.visitLabel(ltTrue);
			mv.visitInsn(ICONST_1);
		}
		if(expression_Binary.op == Kind.OP_EQ) {
			mv.visitJumpInsn(IF_ICMPEQ, eqTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, afterLabel);
			mv.visitLabel(eqTrue);
			mv.visitInsn(ICONST_1);
		}
		if(expression_Binary.op == Kind.OP_NEQ) {
			mv.visitJumpInsn(IF_ICMPNE, neqTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, afterLabel);
			mv.visitLabel(neqTrue);
			mv.visitInsn(ICONST_1);
		}
		mv.visitLabel(afterLabel);
		if(expression_Binary.op == Kind.OP_AND) {
			mv.visitInsn(IAND);
			
		}
		if(expression_Binary.op == Kind.OP_OR) {
			mv.visitInsn(IOR);
		}
		if(expression_Binary.op == Kind.OP_DIV) {
			mv.visitInsn(IDIV);
		}
		if(expression_Binary.op == Kind.OP_MINUS) {
			mv.visitInsn(ISUB);
		}
		if(expression_Binary.op == Kind.OP_MOD) {
			mv.visitInsn(IREM);
		}
		if(expression_Binary.op == Kind.OP_PLUS) {
			mv.visitInsn(IADD);
		}
		if(expression_Binary.op == Kind.OP_TIMES) {
			mv.visitInsn(IMUL);
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.getType());
		return null;
	}

	//  Expression_Unary ::= op Expression
	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		 
		Integer xorVar = new Integer(0x7FFFFFFF);
		if(expression_Unary.op == Kind.OP_EXCL) {
			expression_Unary.e.visit(this, arg);
			if(expression_Unary.typeName == Type.BOOLEAN) {
				
				mv.visitInsn(ICONST_1);
				mv.visitInsn(IXOR);
			}
			if(expression_Unary.typeName == Type.INTEGER) {
				
				mv.visitLdcInsn(xorVar);
				mv.visitInsn(IXOR);
			}
		}
		if(expression_Unary.op == Kind.OP_PLUS) {
			expression_Unary.e.visit(this, arg);
	
		}
		if(expression_Unary.op == Kind.OP_MINUS) {
			expression_Unary.e.visit(this, arg);
			mv.visitInsn(INEG);
			
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.getType());
		return null;
	}

	// Index ::= Expression0 Expression1
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if(index.isCartesian()) {
			;
		}
		else {
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
			
		}
		return null;
	}

	//  Expression_PixelSelector ::=   name Index
	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name, ImageSupport.ImageDesc);
		expression_PixelSelector.index.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig, false);
		return null;
	}

	//  Expression_Conditional ::=  Expressioncondition Expressiontrue Expressionfalse
	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		Label jumpToFalse = new Label();
		Label jumpAfterFalse = new Label();
		expression_Conditional.condition.visit(this, arg);
		mv.visitJumpInsn(IFEQ, jumpToFalse);
		expression_Conditional.trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, jumpAfterFalse);
		mv.visitLabel(jumpToFalse);
		expression_Conditional.falseExpression.visit(this, arg);
		mv.visitLabel(jumpAfterFalse);
		// CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.trueExpression.getType());
		return null;
	}


	//  Declaration_Image  :: = name (  xSize ySize | ε) Source	
	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		
		FieldVisitor fv = cw.visitField(ACC_STATIC, declaration_Image.name, ImageSupport.ImageDesc, null, null);
		fv.visitEnd();
		if(declaration_Image.source != null) {
			declaration_Image.source.visit(this, arg);
			 if(declaration_Image.xSize != null && declaration_Image.ySize != null) {
				 declaration_Image.xSize.visit(this, arg);
				 mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)"+ImageSupport.IntegerDesc, false);
				 declaration_Image.ySize.visit(this, arg);
				 mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			 }
			 else {
				 mv.visitInsn(ACONST_NULL);
				 mv.visitInsn(ACONST_NULL);
				 
			 }
			 mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			
		}
		else {
			if(declaration_Image.xSize != null && declaration_Image.ySize != null) {
				declaration_Image.xSize.visit(this, arg);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig, false);
			}
			else {
				mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
				mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig, false);
				
			}
			
		}
		mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name, ImageSupport.ImageDesc);
		return null;
	}
	
  
	//  Source_StringLiteral ::=  fileOrURL
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		mv.visitLdcInsn(source_StringLiteral.fileOrUrl);
				return null;
	}

	
	//  Source_CommandLineParam  ::= ExpressionparamNum
	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD,0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
	}

	//  Source_Ident ::= name
	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, ImageSupport.StringDesc);
		return null;
	}


	//  Declaration_SourceSink  ::= Type name  Source
	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		FieldVisitor fv = cw.visitField(ACC_STATIC, declaration_SourceSink.name, ImageSupport.StringDesc, null, null);
		fv.visitEnd();
		if(declaration_SourceSink.source != null) {
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.name, ImageSupport.StringDesc);
			
		}
		
		return null;
	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_IntLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
		return null;
	}

	//  Expression_FunctionAppWithExprArg ::=  function Expression
	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.function == Kind.KW_abs) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "abs", RuntimeFunctions.absSig, false);
		}
		if(expression_FunctionAppWithExprArg.function == Kind.KW_log) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "log", RuntimeFunctions.logSig, false);
		}
		
		return null;
	}

	
	//  Expression_FunctionAppWithIndexArg ::=   function Index
	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		if(expression_FunctionAppWithIndexArg.function == Kind.KW_cart_x) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
		}
		if(expression_FunctionAppWithIndexArg.function == Kind.KW_cart_y) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		if(expression_FunctionAppWithIndexArg.function == Kind.KW_polar_a) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
		}
		if(expression_FunctionAppWithIndexArg.function == Kind.KW_polar_r) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
		}
		
		return null;
	}

	//  Expression_PredefinedName ::=  predefNameKind
	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		if(expression_PredefinedName.kind == Kind.KW_x) {
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_y) {
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_X) {
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_Y) {
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_r) {
			mv.visitFieldInsn(GETSTATIC, className, "r", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_a) {
			mv.visitFieldInsn(GETSTATIC, className, "a", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_A) {
			mv.visitFieldInsn(GETSTATIC, className, "A", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_R) {
			mv.visitFieldInsn(GETSTATIC, className, "R", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_DEF_X) {
			mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_DEF_Y) {
			mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
		}
		else if(expression_PredefinedName.kind == Kind.KW_Z) {
			mv.visitFieldInsn(GETSTATIC, className, "Z", "I");
		}
		else {
			;
		}
		
		return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	//  Statement_Out ::= name Sink
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		String type = null;
		if(statement_Out.getDec().typeName == Type.INTEGER) {
			type = "I";
		}
		if(statement_Out.getDec().typeName == Type.BOOLEAN) {
			type="Z";
		}
		if(statement_Out.getDec().typeName == Type .IMAGE) {
			type = ImageSupport.ImageDesc;
		}
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, type);
		CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().typeName);
		if(statement_Out.getDec().typeName == Type.BOOLEAN) {
			
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V",false);
			
		}
		if(statement_Out.getDec().typeName == Type.INTEGER) {
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V",false);
			
		}
		if(statement_Out.getDec().typeName == Type.IMAGE) {
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, ImageSupport.ImageDesc);
			statement_Out.sink.visit(this, arg);
		}
		
		return null;
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	
	//  Statement_In ::= name Source
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		statement_In.source.visit(this, arg);
		if(statement_In.getDec().typeName == Type.INTEGER) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I",false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "I");
		}
		if(statement_In.getDec().typeName == Type.BOOLEAN) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z",false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "Z");
		}
		if(statement_In.getDec().typeName == Type.IMAGE) {
			if(((Declaration_Image)statement_In.getDec()).xSize != null && ((Declaration_Image)statement_In.getDec()).ySize != null) {
				((Declaration_Image)statement_In.getDec()).xSize.visit(this, arg);
				 mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				 ((Declaration_Image)statement_In.getDec()).ySize.visit(this, arg);
				 mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			 }
			 else {
				 mv.visitInsn(ACONST_NULL);
				 mv.visitInsn(ACONST_NULL);
				 
			 }
			 mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			 mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, ImageSupport.ImageDesc);
		}
		return null;
	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	//@Override
	//public Object visitStatement_Transform(Statement_Assign statement_Assign, Object arg) throws Exception {
		//TODO  (see comment)
		// throw new UnsupportedOperationException();
	//}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	//  LHS ::= name Index
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		if(lhs.typeName == Type.INTEGER || lhs.typeName == Type.BOOLEAN) {
			if(lhs.typeName == Type.INTEGER) {
				mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
				
			}
			else {
				mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
			}
			
		}
		if(lhs.typeName == Type.IMAGE) {
			mv.visitFieldInsn(GETSTATIC, className, lhs.name, ImageSupport.ImageDesc);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false);
		}
		return null;
	}
	

	//  Sink_SCREEN ::= SCREEN
	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeFrame", ImageSupport.makeFrameSig, false);
		mv.visitInsn(POP);
		return null;
	}

	//  Sink_Ident ::= name
	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, ImageSupport.StringDesc);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig, false);
		
		return null;
	}

	//  Expression_BooleanLit ::=  value
	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_BooleanLit.value);
		//throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	//  Expression_Ident  ::=   name
	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		
		if(expression_Ident.typeName == Type.BOOLEAN) {
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "Z");
		}
		if(expression_Ident.typeName == Type.INTEGER) {
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "I");
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.getType());
		return null;
	}

	//  Statement_Assign ::=  LHS  Expression
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		if(statement_Assign.lhs.typeName == Type.BOOLEAN || statement_Assign.lhs.typeName == Type.INTEGER) {
		statement_Assign.e.visit(this, arg);
		statement_Assign.lhs.visit(this, arg);
	}
		if(statement_Assign.lhs.typeName == Type.IMAGE) {
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitInsn(DUP);
			Label firstLoop = new Label();
			Label secondLoop = new Label();
			Label firstEndLabel = new Label();
			Label secondEndLabel = new Label();
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "X", "I");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "Y", "I");
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "R", "I");
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "A", "I");
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			mv.visitLabel(firstLoop);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
			mv.visitJumpInsn(IF_ICMPGE, firstEndLabel);
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			mv.visitLabel(secondLoop);
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitJumpInsn(IF_ICMPGE, secondEndLabel);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "r", "I");
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "a", "I");
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
			mv.visitInsn(ICONST_1);
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			mv.visitJumpInsn(GOTO,secondLoop);
			mv.visitLabel(secondEndLabel);
			mv.visitInsn(ICONST_1);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			mv.visitJumpInsn(GOTO,firstLoop);
			mv.visitLabel(firstEndLabel);
		
			
		}
		
		
		return null;
	}

}
