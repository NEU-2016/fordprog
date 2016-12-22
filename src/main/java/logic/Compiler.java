package logic;

import antlr.JavaBaseVisitor;
import antlr.JavaParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Compiler extends JavaBaseVisitor<Object> {

	private CompilerInfo compilerInfo;
	private List<String> compileErrorList;

	public Compiler() {
		this.compilerInfo = new CompilerInfo();
		this.compileErrorList = new ArrayList<>();
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
				compileErrorList.add("Compilation error! Duplicate imports!");
			}
			compilerInfo.setImportNameList(importQualifiedNames);
		}

		//classDeclaration
		List<JavaParser.TypeDeclarationContext> classDeclarationContextList = ctx.typeDeclaration();
		List<String> classQualifiedNames = classDeclarationContextList.stream()
				.map(JavaParser.TypeDeclarationContext::classDeclaration)
				.map(JavaParser.ClassDeclarationContext::Identifier)
				.map(TerminalNode::getText)
				.collect(Collectors.toList());
		if (classQualifiedNames.stream().distinct().count() != classQualifiedNames.size()) {
			compileErrorList.add("Compilation error! Duplicate classes!");
		}
		if (classDeclarationContextList.stream().filter(cd -> cd.classDeclaration().typeType() == null).findAny().isPresent()) {
			compileErrorList.add("Compilation error! Unextended class!");
		}
		compilerInfo.setClassNameList(classQualifiedNames);

		//one and only one main method
		boolean isThereMain = false;
		for (JavaParser.TypeDeclarationContext cd : classDeclarationContextList) {
			for (JavaParser.ClassBodyDeclarationContext cbd : cd.classDeclaration().classBody().classBodyDeclaration()) {
				if (cbd.memberDeclaration().methodDeclaration() != null) {
					if (cbd.memberDeclaration().methodDeclaration().Identifier().getText().equals("main")) {
						for (JavaParser.ModifierContext m : cbd.modifier()) {
							if (m.getText().contains("static")) {
								isThereMain = true;
							}
						}
					}
				}
			}
		}
		if (!isThereMain) {
			compileErrorList.add("Compilation error! No static main method!");
		}
		return super.visitCompilationUnit(ctx);
	}

	@Override
	public Object visitClassBody(JavaParser.ClassBodyContext ctx) {
		//unique constructors
		List<JavaParser.MemberDeclarationContext> constructorDeclarationList = ctx.classBodyDeclaration().stream()
				.map(JavaParser.ClassBodyDeclarationContext::memberDeclaration)
				.filter(md -> md.constructorDeclaration() != null)
				.collect(Collectors.toList());
		if (constructorDeclarationList.size() > 1) {
			compileErrorList.add("Compilation error! Too many constructors!");
		}

		//unique method names
		List<JavaParser.MemberDeclarationContext> methodDeclarationList = ctx.classBodyDeclaration().stream()
				.map(JavaParser.ClassBodyDeclarationContext::memberDeclaration)
				.filter(md -> md.methodDeclaration() != null)
				.collect(Collectors.toList());
		List<String> methodNames = methodDeclarationList.stream()
				.map(JavaParser.MemberDeclarationContext::methodDeclaration)
				.map(JavaParser.MethodDeclarationContext::Identifier)
				.map(TerminalNode::getText)
				.collect(Collectors.toList());
		if (methodNames.stream().distinct().count() != methodDeclarationList.size()) {
			compileErrorList.add("Compilation error! Duplicate methods!");
		}

		//unique field names
		List<JavaParser.MemberDeclarationContext> fieldDeclarationList = ctx.classBodyDeclaration().stream()
				.map(JavaParser.ClassBodyDeclarationContext::memberDeclaration)
				.filter(md -> md.fieldDeclaration() != null)
				.collect(Collectors.toList());
		List<JavaParser.VariableDeclaratorsContext> variableDeclarators = fieldDeclarationList.stream()
				.map(JavaParser.MemberDeclarationContext::fieldDeclaration)
				.map(JavaParser.FieldDeclarationContext::variableDeclarators)
				.collect(Collectors.toList());
		if (variableDeclarators.size() > 0) {
			List<String> fieldNames = variableDeclarators.get(0).variableDeclarator().stream()
					.map(JavaParser.VariableDeclaratorContext::variableDeclaratorId)
					.map(JavaParser.VariableDeclaratorIdContext::Identifier)
					.map(TerminalNode::getText)
					.collect(Collectors.toList());
			if (fieldNames.stream().distinct().count() != fieldDeclarationList.size()) {
				compileErrorList.add("Compilation error! Duplicate fields!");
			}
		}

		return super.visitClassBody(ctx);
	}

	@Override
	public Object visitBlock(JavaParser.BlockContext ctx) {
		//unique field names in blocks
		List<JavaParser.VariableDeclaratorsContext> variableDeclarators = ctx.blockStatement().stream()
				.filter(b -> b.localVariableDeclarationStatement() != null)
				.map(JavaParser.BlockStatementContext::localVariableDeclarationStatement)
				.map(JavaParser.LocalVariableDeclarationStatementContext::localVariableDeclaration)
				.map(JavaParser.LocalVariableDeclarationContext::variableDeclarators)
				.collect(Collectors.toList());
		if (variableDeclarators.size() > 0) {
			List<String> fieldNames = variableDeclarators.get(0).variableDeclarator().stream()
					.map(JavaParser.VariableDeclaratorContext::variableDeclaratorId)
					.map(JavaParser.VariableDeclaratorIdContext::Identifier)
					.map(TerminalNode::getText)
					.collect(Collectors.toList());
			if (fieldNames.stream().distinct().count() != variableDeclarators.size()) {
				compileErrorList.add("Compilation error! Duplicate fields!");
			}
		}


		return super.visitBlock(ctx);
	}

	@Override
	public Object visitStatementExpression(JavaParser.StatementExpressionContext ctx) {
		//Print() trigger
		JavaParser.ExpressionContext exp = ctx.expression();
		if (exp.expression().get(0).getText().equals("Println")) {
			if (exp.expressionList() != null) {
				if (exp.expressionList().expression().size() > 0) {
					System.out.println("Static print called in code, printed: " + exp.expressionList().expression().get(0).getText());
				}
			} else {
				System.out.println("Static print called in code, printed \\n!");
			}
		}
		return super.visitStatementExpression(ctx);
	}

	public CompilerInfo getCompilerInfo() {
		return compilerInfo;
	}

	public void setCompilerInfo(CompilerInfo compilerInfo) {
		this.compilerInfo = compilerInfo;
	}

	public List<String> getCompileErrorList() {
		return compileErrorList;
	}

	public void setCompileErrorList(List<String> compileErrorList) {
		this.compileErrorList = compileErrorList;
	}
}
