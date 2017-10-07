/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
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

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

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
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n ;; ";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNext(scanner, SEMI, 5, 1, 2, 3);
		checkNextIsEOF(scanner);
	}
	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "\" greetings  ";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
	
	// testing all operators--------------------------------------------------------------------------
		@Test
		public void allOperatorsTest() throws LexicalException {
			String input = "= > < ! ? : == != >= <= & | + - * / % ** @ -> <-";
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.OP_ASSIGN, 0, 1, 1, 1);
			checkNext(scanner, Kind.OP_GT, 2, 1, 1, 3);
			checkNext(scanner, Kind.OP_LT, 4, 1, 1, 5);
			checkNext(scanner, Kind.OP_EXCL, 6, 1, 1, 7);
			checkNext(scanner, Kind.OP_Q, 8, 1, 1, 9);
			checkNext(scanner, Kind.OP_COLON, 10, 1, 1, 11);
			checkNext(scanner, Kind.OP_EQ, 12, 2, 1, 13);
			checkNext(scanner, Kind.OP_NEQ, 15, 2, 1, 16);
			checkNext(scanner, Kind.OP_GE, 18, 2, 1, 19);
			checkNext(scanner, Kind.OP_LE, 21, 2, 1, 22);
			checkNext(scanner, Kind.OP_AND, 24, 1, 1, 25);
			checkNext(scanner, Kind.OP_OR, 26, 1, 1, 27);
			checkNext(scanner, Kind.OP_PLUS, 28, 1, 1, 29);
			checkNext(scanner, Kind.OP_MINUS, 30, 1, 1, 31);
			checkNext(scanner, Kind.OP_TIMES, 32, 1, 1, 33);
			checkNext(scanner, Kind.OP_DIV, 34, 1, 1, 35);
			checkNext(scanner, Kind.OP_MOD, 36, 1, 1, 37);
			checkNext(scanner, Kind.OP_POWER, 38, 2, 1, 39);
			checkNext(scanner, Kind.OP_AT, 41, 1, 1, 42);
			checkNext(scanner, Kind.OP_RARROW, 43, 2, 1, 44);
			checkNext(scanner, Kind.OP_LARROW, 46, 2, 1, 47);
			checkNextIsEOF(scanner);
		}
	
	//testing all separators--------------------------------------------------------------------------
	@Test
	public void allSeparatorsTest() throws LexicalException {
		String input = "()[];,";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.LPAREN,0,1,1,1);
		checkNext(scanner, Kind.RPAREN,1,1,1,2);
		checkNext(scanner, Kind.LSQUARE,2,1,1,3);
		checkNext(scanner, Kind.RSQUARE,3,1,1,4);
		checkNext(scanner, Kind.SEMI,4,1,1,5);
		checkNext(scanner, Kind.COMMA,5,1,1,6);
		checkNextIsEOF(scanner);
	}

	// testing all boolean literals-----------------------------------------------------------------------
	@Test
	public void booleanTest() throws LexicalException {
		String input = "true false";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1);
		checkNext(scanner, BOOLEAN_LITERAL, 5, 5, 1, 6);
		checkNextIsEOF(scanner);
	}
	
	// testing integer literals---------------------------------------------------------------------------
	@Test
	public void integerLiteralTest() throws LexicalException {
		String input = "1256 99999";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.INTEGER_LITERAL, 0, 4, 1, 1);
		checkNext(scanner, Kind.INTEGER_LITERAL, 5, 5, 1, 6);
		checkNextIsEOF(scanner);
	}
	@Test
	public void integerLiteralOutofRangeTest() throws LexicalException {
		String input = "2147483648";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}
	@Test
	public void integerLiteralAndZeroTest() throws LexicalException {
		String input = "0323 12456";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, Kind.INTEGER_LITERAL, 1, 3, 1, 2);
		checkNext(scanner, Kind.INTEGER_LITERAL, 5, 5, 1, 6);
		checkNextIsEOF(scanner);
	}
	@Test
	public void integerLiteralLeadingZeroesTest() throws LexicalException {
		String input = "000012456";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, Kind.INTEGER_LITERAL, 1, 1, 1, 2);
		checkNext(scanner, Kind.INTEGER_LITERAL, 2, 1, 1, 3);
		checkNext(scanner, Kind.INTEGER_LITERAL, 3, 1, 1, 4);
		checkNext(scanner, Kind.INTEGER_LITERAL, 4, 5, 1, 5);
		checkNextIsEOF(scanner);
	}
	

	//testing all keywords--------------------------------------------------------------------------------------------------------
	@Test
	public void allKeywordsTest() throws LexicalException {
		String input = "x X y Y r R a A Z DEF_X DEF_Y SCREEN cart_x cart_y polar_a polar_r abs sin cos atan log image int boolean url file";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.KW_x, 0, 1, 1, 1);
		checkNext(scanner, Kind.KW_X, 2, 1, 1, 3);
		checkNext(scanner, Kind.KW_y, 4, 1, 1, 5);
		checkNext(scanner, Kind.KW_Y, 6, 1, 1, 7);
		checkNext(scanner, Kind.KW_r, 8, 1, 1, 9);
		checkNext(scanner, Kind.KW_R, 10, 1, 1, 11);
		checkNext(scanner, Kind.KW_a, 12, 1, 1, 13);
		checkNext(scanner, Kind.KW_A, 14, 1, 1, 15);
		checkNext(scanner, Kind.KW_Z, 16, 1, 1, 17);
		checkNext(scanner, Kind.KW_DEF_X, 18, 5, 1, 19);
		checkNext(scanner, Kind.KW_DEF_Y, 24, 5, 1, 25);
		checkNext(scanner, Kind.KW_SCREEN, 30, 6, 1, 31);
		checkNext(scanner, Kind.KW_cart_x, 37, 6, 1, 38);
		checkNext(scanner, Kind.KW_cart_y, 44, 6, 1, 45);
		checkNext(scanner, Kind.KW_polar_a, 51, 7, 1, 52);
		checkNext(scanner, Kind.KW_polar_r, 59, 7, 1, 60);
		checkNext(scanner, Kind.KW_abs, 67, 3, 1, 68);
		checkNext(scanner, Kind.KW_sin, 71, 3, 1, 72);
		checkNext(scanner, Kind.KW_cos, 75, 3, 1, 76);
		checkNext(scanner, Kind.KW_atan, 79, 4, 1, 80);
		checkNext(scanner, Kind.KW_log, 84, 3, 1, 85);
		checkNext(scanner, Kind.KW_image, 88, 5, 1, 89);
		checkNext(scanner, Kind.KW_int, 94, 3, 1, 95);
		checkNext(scanner, Kind.KW_boolean, 98, 7, 1, 99);
		checkNext(scanner, Kind.KW_url, 106, 3, 1, 107);
		checkNext(scanner, Kind.KW_file, 110, 4, 1, 111);
		checkNextIsEOF(scanner);
	}
	  
	// testing strings-----------------------------------------------------------------------------
	@Test
	public void allStringsTest() throws LexicalException {
		String input = "\"abcdefg\\b\\t\\n\\f\\r\\\"\\'\\\\\"";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, STRING_LITERAL,0,25,1,1);
		checkNextIsEOF(scanner);
		
	}
	@Test
	public void stringbackslashTest() throws LexicalException {
		String input = "\"abcdefg \\ hijklmn \"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}
	@Test
	public void stringQuotesTest() throws LexicalException {
		String input = "\"abcdefg \" hijklmn \"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}
	@Test
	public void stringWithSlashrTest() throws LexicalException {
		String input = "\"abcdefg \r hijklmn \"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}
	@Test
	public void stringwithSlashnTest() throws LexicalException {
		String input = "\"abcdefg \n hijklmn \"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}
	
	//random test cases--------------------------------------------------------------------
	@Test
	public void randomTest1() throws LexicalException {
		String input = "0123 _asde $asdf12 123dfr asd23 x Y SCREEN \n ( \"this is a string\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.INTEGER_LITERAL,0,1,1,1);
		checkNext(scanner, Kind.INTEGER_LITERAL,1,3,1,2);
		checkNext(scanner, Kind.IDENTIFIER,5,5,1,6);
		checkNext(scanner, Kind.IDENTIFIER,11,7,1,12);
		checkNext(scanner, Kind.INTEGER_LITERAL,19,3,1,20);
		checkNext(scanner, Kind.IDENTIFIER,22,3,1,23);
		checkNext(scanner, Kind.IDENTIFIER,26,5,1,27);
		checkNext(scanner, Kind.KW_x,32,1,1,33);
		checkNext(scanner, Kind.KW_Y,34,1,1,35);
		checkNext(scanner, Kind.KW_SCREEN,36,6,1,37);
		checkNext(scanner, Kind.LPAREN,45,1,2,2);
		checkNext(scanner, Kind.STRING_LITERAL,47,18,2,4);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void randomTest2() throws LexicalException {
		String input = "\"abcdefg \\0 hijkl\"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}
	@Test
	public void randomTest3() throws LexicalException {
		String input = "===";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.OP_EQ,0,2,1,1);
		checkNext(scanner, Kind.OP_ASSIGN,2,1,1,3);
		checkNextIsEOF(scanner);
	}
	@Test
	public void randomTest4() throws LexicalException {
		String input = "a\tb";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.KW_a,0,1,1,1);
		checkNext(scanner, Kind.IDENTIFIER,2,1,1,3);
		checkNextIsEOF(scanner);
	}
	@Test
	public void randomTest5() throws LexicalException {
		String input = "abcdef # ";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}
	
	
	
	
	
	
	
	
	
	
	

}