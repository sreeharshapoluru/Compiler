package cop5556fa17;

import static org.junit.Assert.*;
import cop5556fa17.AST.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}



	/**
	 * Simple test case with an empty program.  This test 
	 * expects an SyntaxException because all legal programs must
	 * have at least an identifier
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	
	/** Another example.  This is a legal program and should pass when 
	 * your parser is implemented.
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	

	/**
	 * This example invokes the method for expression directly. 
	 * Effectively, we are viewing Expression as the start
	 * symbol of a sub-language.
	 *  
	 * Although a compiler will always call the parse() method,
	 * invoking others is useful to support incremental development.  
	 * We will only invoke expression directly, but 
	 * following this example with others is recommended.  
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression1() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------test cases
	@Test
	public void expression2() throws SyntaxException, LexicalException {
		String input = "99+32 * 67";
		show(input);
		Scanner scanner = new Scanner(input).scan();    
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	@Test
    public void expression3() throws LexicalException, SyntaxException {
        String input = "x?y:z";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        //show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.expression();
    }
	@Test
    public void allexpressions() throws LexicalException, SyntaxException {
        String input = "sin(x/y) abs(x) atan(DEF_X) cos(y) cart_x(x+y) + A + a b x y poalr_a , poalr_r";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        //show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.expression();
    }
	@Test
    public void expression4() throws LexicalException, SyntaxException {
        String input = "a:b:c";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.expression();
    }
	@Test
	public void expression5() throws SyntaxException, LexicalException {
		String input =  "x/(X+y*Y-atan(x/y)*sin(x)+2)"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	@Test
	public void expression6() throws SyntaxException, LexicalException {
		String input =  "a+b"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	@Test
	public void expression7() throws SyntaxException, LexicalException {
		String input =  "Har!sha a/c+d"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	
	@Test
	public void tCase1() throws SyntaxException, LexicalException {
		String input = "12345";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	@Test
	public void tCase2() throws SyntaxException, LexicalException {
		String input = "prog image(gif)imagein <- imageout ->"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		//parser.parse();
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	}

	
	

