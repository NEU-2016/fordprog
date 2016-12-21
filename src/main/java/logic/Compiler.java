package logic;

import antlr.JavaBaseVisitor;
import antlr.JavaParser;

import java.util.List;
import java.util.stream.Collectors;

public class Compiler extends JavaBaseVisitor<Object> {

	private CompilerInfo compilerInfo;
	private List<String> compileErrorList;

	public Compiler() {
		this.compilerInfo = new CompilerInfo();
	}

	@Override
	public Object visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
		//packageDeclaration
		if (ctx.packageDeclaration() == null) {
			compilerInfo.setPackageName("No package name");
		} else {
			compilerInfo.setPackageName(ctx.packageDeclaration().qualifiedName().getText());
		}
		//importDeclaration
		if (ctx.importDeclaration() == null) {
			compilerInfo.getImportNameList().add("No imports");
		} else {
			List<JavaParser.ImportDeclarationContext> importDeclarationContextList = ctx.importDeclaration();
			List<String> importQualifiedNames = importDeclarationContextList.stream()
					.map(JavaParser.ImportDeclarationContext::qualifiedName)
					.map(JavaParser.QualifiedNameContext::getText)
					.collect(Collectors.toList());
			if (importQualifiedNames.stream().distinct().count() != importQualifiedNames.size()) {
				System.out.println("Compilation error! Duplicate imports!");
			}
			compilerInfo.setImportNameList(importQualifiedNames);
		}

		return super.visitCompilationUnit(ctx);
	}

	public CompilerInfo getCompilerInfo() {
		return compilerInfo;
	}

	public void setCompilerInfo(CompilerInfo compilerInfo) {
		this.compilerInfo = compilerInfo;
	}
}
