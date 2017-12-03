package cop5556fa17;

import static org.junit.Assert.assertEquals;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.AST.Program;
import cop5556fa17.CodeGenUtils.DynamicClassLoader;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.TypeCheckVisitor.SemanticException;

public class CodeGenVisitorTest {
	
	static boolean doPrint = true;
	static boolean doCreateFile = false;

	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private boolean devel = true;
	private boolean grade = true;
	
	
	/**
	 * Generates bytecode for given input.
	 * Throws exceptions for Lexical, Syntax, and Type checking errors
	 * 
	 * @param input   String containing source code
	 * @return        Generated bytecode
	 * @throws Exception
	 */
	byte[] genCode(String input) throws Exception {
		
		//scan, parse, and type check
		Scanner scanner = new Scanner(input);
		show(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show(program);

		//generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel,grade,null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		
		//output the generated bytecode
		show(CodeGenUtils.bytecodeToString(bytecode));
		
		//write byte code to file 
		if (doCreateFile) {
			String name = ((Program) program).name;
			String classFileName = "bin/" + name + ".class";
			OutputStream output = new FileOutputStream(classFileName);
			output.write(bytecode);
			output.close();
			System.out.println("wrote classfile to " + classFileName);
		}
		
		//return generated classfile as byte array
		return bytecode;
	}
	
	/**
	 * Run main method in given class
	 * 
	 * @param className    
	 * @param bytecode    
	 * @param commandLineArgs  String array containing command line arguments, empty array if none
	 * @throws Exception
	 */
	void runCode(String className, byte[] bytecode, String[] commandLineArgs) throws Exception {
		RuntimeLog.initLog(); //initialize log used for grading.
		DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		Class<?> testClass = loader.define(className, bytecode);
		Class[] argTypes = {commandLineArgs.getClass()};
		Method m = testClass.getMethod("main", argTypes );
		show("Output from " + m + ":");  //print name of method to be executed
		Object passedArgs[] = {commandLineArgs};  //create array containing params, in this case a single array.
		m.invoke(null, passedArgs);	
	}
	

	@Test
	public void emptyProg() throws Exception {
		String prog = "emptyProg";	
		String input = prog;
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n "+RuntimeLog.globalLog);
		assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	
	@Test
	public void prog1() throws Exception {
		String prog = "prog1";
		String input = prog + "\nint g;\ng = 3;\ng -> SCREEN; ";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;3;3;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void prog2() throws Exception {
		String prog = "prog2";
		String input = prog  + "\nboolean g;\ng = true;\ng -> SCREEN;\ng = false;\ng -> SCREEN;";	
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;true;true;false;false;leaving main;",RuntimeLog.globalLog.toString() );
	}
	
	@Test
	public void prog3() throws Exception {
		//scan, parse, and type check the program
		String prog = "prog3";
		String input = prog
				+ " boolean g;\n"
				+ "g <- @ 0;\n"
				+ "g -> SCREEN;\n"
				+ "int h;\n"
				+ "h <- @ 1;\n"
				+ "h -> SCREEN;";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"true", "55"}; //create command line argument array to initialize params, none in this case		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;0;true;1;55;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void prog4() throws Exception {
		//scan, parse, and type check the program
		String prog = "prog4";
		String input = prog
				+ " boolean g;\n"
				+ "g <- @ 0;\n"
				+ "g -> SCREEN;\n"
				+ "int h;\n"
				+ "h <- @ 1;\n"
				+ "h -> SCREEN;\n"
				+ "int k;\n"
				+ "k <- @ 2;\n"
				+ "k -> SCREEN;\n"
				+ "int chosen;"
				+ "chosen = g ? h : k;\n"
				+ "chosen -> SCREEN;"
				;	
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"true", "34", "56"}; //create command line argument array to initialize params, none in this case		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;0;true;1;34;2;56;true;34;34;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	
	@Test
	public void unaryExpr() throws Exception {
		String prog = "unaryExpr";
		String input = prog + 
				"\nboolean g = false;\n" +
				"g -> SCREEN;\n" +
				"g = !g;\n"
				+ "g -> SCREEN;";
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; 
		runCode(prog, bytecode, commandLineArgs);		
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;false;false;false;true;true;leaving main;",RuntimeLog.globalLog.toString());
										
	}
	
	@Test
	public void testcase1() throws Exception {
		String prog = "testcase1";
		String input = prog + 
				"\n boolean t;\n" + 
				"int n;\n" + 
				"int abcd;\n" + 
				"abcd <- @ 0;\n" + 
				"n <- @ 1;\n" + 
				"t <- @ 2;\n" + 
				"boolean s=((abcd+n)>-56 & t);\n" + 
				"s -> SCREEN;";
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"1","-55","true"}; 
		runCode(prog, bytecode, commandLineArgs);		
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;0;1;2;1;-55;-54;56;-56;true;true;true;true;leaving main;",RuntimeLog.globalLog.toString());								
	}
	
	@Test
	public void testcase2() throws Exception {
		String prog = "testcase2";
		String input = prog + 
				"\n boolean t;\n" + 
				"int n=1;\n" + 
				"int abcd=1;\n" + 
				"int z=5;\n" + 
				"boolean s=((n<0) & (z>1) | (z+n<=2) & (abcd+n>=5) | (z==5) | (z*n!=10) & (abcd-n==0) & (z/5==1) | (z%5==0) );\n" + 
				"s -> SCREEN;";
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; 
		runCode(prog, bytecode, commandLineArgs);		
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;1;1;5;1;0;false;5;1;true;false;5;1;6;2;false;1;1;2;5;false;false;false;5;5;true;true;5;1;5;10;true;1;1;0;0;true;true;5;5;1;1;true;true;true;5;5;0;0;true;true;true;leaving main;",RuntimeLog.globalLog.toString());
										
	}
	
	@Test
	public void testcase3() throws Exception {
		String prog = "testcase3";
		String input = prog + 
				"\n boolean s=(!true & !false);\n" + 
				"s -> SCREEN;";
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; 
		runCode(prog, bytecode, commandLineArgs);		
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;true;false;false;true;false;false;leaving main;",RuntimeLog.globalLog.toString());
										
	}
	
	@Test
	public void testcase4() throws Exception {
		String prog = "testcase4";
		String input = prog + 
				"\n int s=!1;\n" + 
				"s -> SCREEN;";
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"55","2"}; 
		runCode(prog, bytecode, commandLineArgs);		
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;1;2147483646;2147483646;leaving main;",RuntimeLog.globalLog.toString());
										
	}
	
	@Test
	public void testcase5() throws Exception {
		String prog = "testcase5";
		String input = prog + 
				"\n int s=!1;\n" + 
				"s -> SCREEN;";
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; 
		runCode(prog, bytecode, commandLineArgs);		
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;1;2147483646;2147483646;leaving main;",RuntimeLog.globalLog.toString());
										
	}
	
	@Test
	public void testcase6() throws Exception {
		String prog = "testcase6";
		String input = prog + 
				"\n boolean t;\n" + 
				"int n; \n"+
				"n<- @ 1;\n" + 
				"int abcd;\n"+
				"abcd <- @ 2;\n" + 
				"int z=5;\n" + 
				"boolean s=(!n & !false);\n" + 
				"s -> SCREEN;";
		thrown.expect(SemanticException.class);
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"55","true"}; 
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
										
	}
	
	@Test
	public void testcase7() throws Exception {
		String prog = "testcase7";
		String input = prog + 
				" \n boolean n=true;\n" + 
				"int abcd=1;\n" + 
				"int z=5;\n" + 
				"boolean s=((n<0) & (z>1));\n" + 
				"s -> SCREEN; ";
		thrown.expect(SemanticException.class);
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; 
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);							
	}
	
	@Test
	public void testcase8() throws Exception {
		String prog = "testcase8";
		String input = prog + 
				" \n boolean s=((false<=true) & (true>=false) ); \n" + 
				"s -> SCREEN; ";
		thrown.expect(SemanticException.class);
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; 
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);							
	}
	
	@Test
	public void testcase9() throws Exception {
		String prog = "testcase9";
		String input = prog + 
				" \n boolean s=(true?:); \n" + 
				"s -> SCREEN; ";
		thrown.expect(SyntaxException.class);
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; 
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);							
	}
	
	@Test
	public void testcase10() throws Exception {
		String prog = "testcase10";
		String input = prog + 
				" \n boolean s=(true?:false); \n" + 
				"s -> SCREEN; ";
		thrown.expect(SyntaxException.class);
		show(input);
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; 
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);							
	}


}
