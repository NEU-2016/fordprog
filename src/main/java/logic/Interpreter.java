package logic;

import antlr.JavaLexer;
import antlr.JavaParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Interpreter {
	public void execute(String filename) throws IOException {
		File f = new File(filename);
		String input = readFile(f);
		parse(input);
	}

	private static String readFile(File f) throws IOException {
		Scanner sc = new Scanner(f);
		StringBuilder sb = new StringBuilder();

		while (sc.hasNextLine()) {
			sb.append(sc.nextLine());
			sb.append("\n");
		}

		sc.close();
		return sb.toString();
	}

	private static boolean parse(String inp) {
		try {
			ANTLRInputStream input = new ANTLRInputStream(inp);
			JavaLexer lexer = new JavaLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JavaParser parser = new JavaParser(tokens);
//			parser.setTrace(true);

			//parser.removeErrorListeners();

			//lexer.addErrorListener(new SimpleErrorListener());
			//parser.addErrorListener(new SimpleErrorListener());

//			System.out.println(inp);
			ParseTree tree = parser.compilationUnit();

			Compiler compiler = new Compiler();
			compiler.visit(tree);
			compiler.getCompileErrorList().forEach(System.out::println);
			System.out.println("Package name: " + compiler.getCompilerInfo().getPackageName());
			System.out.println("Imports: " + compiler.getCompilerInfo().getImportNameList());
			System.out.println("Class names: " + compiler.getCompilerInfo().getClassNameList());

			return true;
		} catch (SecurityException e) {
			System.out.print("Syntax error: ");
			System.out.println(e.getMessage());
			return false;
		}

	}
}
