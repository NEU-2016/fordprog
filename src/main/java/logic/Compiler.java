package logic;

import antlr.JavaBaseVisitor;
import antlr.JavaParser;

import java.util.ArrayList;
import java.util.List;

public class Compiler extends JavaBaseVisitor<Object> {

	private List<String> importName;

	public Compiler() {
		importName = new ArrayList<>();
	}

	@Override
	public Object visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
		return super.visitCompilationUnit(ctx);
	}

	@Override
	public Object visitImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
		importName.add(ctx.qualifiedName().getText());
		return super.visitImportDeclaration(ctx);
	}

	public void printInfo() {
		//imports
		System.out.println("Number of imports : " + importName.size());
		System.out.println(importName);
	}
}
