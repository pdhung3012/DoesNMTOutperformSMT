package visitor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.core.dom.NaiveASTFlattener;

import constanct.PathConstanct;
//import entities.LocalEntity;
//import invocations.MethodEncoderVisitor;
import utils.JavaASTUtil;

public class ExtractAbbreviationVisitor extends ASTVisitor {
	/**
	 * Internal synonym for {@link AST#JLS2}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private static final int JLS2 = AST.JLS2;
	
	/**
	 * Internal synonym for {@link AST#JLS3}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private static final int JLS3 = AST.JLS3;

	/**
	 * Internal synonym for {@link AST#JLS4}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.10
	 */
	private static final int JLS4 = AST.JLS4;

	/**
	 * The string buffer into which the serialized representation of the AST is
	 * written.
	 */
	protected StringBuilder sbCodeTokens;
	protected StringBuilder sbAbbrevTokens;
	protected StringBuilder sbTargetTokens;
	HashMap<String, CompilationUnit> mapCU;
	private String fopInvocationObject;
	ASTParser parser = ASTParser.newParser(AST.JLS4);
	String[] classpath = { PathConstanct.PATH_JAVA_CLASSPATH };
	HashMap<String, String> setSequencesOfMethods;
	private int indent = 0;
	private int numberOfAbbreviation=1;
	

	

	public StringBuilder getSbCodeTokens() {
		return sbCodeTokens;
	}

	public void setSbCodeTokens(StringBuilder sbCodeTokens) {
		this.sbCodeTokens = sbCodeTokens;
	}

	public StringBuilder getSbAbbrevTokens() {
		return sbAbbrevTokens;
	}

	public void setSbAbbrevTokens(StringBuilder sbAbbrevTokens) {
		this.sbAbbrevTokens = sbAbbrevTokens;
	}

	public StringBuilder getSbTargetTokens() {
		return sbTargetTokens;
	}

	public void setSbTargetTokens(StringBuilder sbTargetTokens) {
		this.sbTargetTokens = sbTargetTokens;
	}

	public String getFopInvocationObject() {
		return fopInvocationObject;
	}

	public void setFopInvocationObject(String fopInvocationObject) {
		this.fopInvocationObject = fopInvocationObject;
	}

	/**
	 * Creates a new AST printer.
	 */
	public ExtractAbbreviationVisitor() {
		this.sbCodeTokens = new StringBuilder();
		this.sbAbbrevTokens=new StringBuilder();
		this.sbTargetTokens=new StringBuilder();
	}
	
	public void buildAbbrevAndTargetTokens() {
		String[] arrCodeToken=this.sbCodeTokens.toString().replaceAll("\n", " ").trim().split("\\s+");
		for(int i=0;i<arrCodeToken.length;i++) {
			if(!arrCodeToken[i].trim().isEmpty()) {
				String item=arrCodeToken[i].trim();
				String abbrev=item.substring(0, numberOfAbbreviation);
				sbAbbrevTokens.append(item+" ");
				sbTargetTokens.append(abbrev+" ");
			}
		}
		
		
	}
	
	public void parseProject(String projectLocation,
			String fopInvocationObject, String jdkPath) {
		this.fopInvocationObject = fopInvocationObject;
		setSequencesOfMethods = new LinkedHashMap<String, String>();
		Map<String, String> options = JavaCore.getOptions();
		String[] arrChildJars = utils.FileIO.findAllJarFiles(projectLocation);
		String[] jarPaths = utils.FileIO.combineFilesToArray(jdkPath,
				arrChildJars);
		// String[] jarPaths = { jdkPath };
		// File f = new File(fileLocation);
		String[] filePaths = utils.FileIO.findAllJavaFiles(projectLocation);
		String[] sources = { projectLocation + File.separator };
		// System.out.println(f.getParentFile().getAbsolutePath());
		// System.out.println("jdk :" +this.jdkPath);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setEnvironment(jarPaths, sources, null, true);

		// String strCode=FileIO.readStringFromFile(fileLocation );
		// parser.setSource(strCode.toCharArray());
		mapCU = new LinkedHashMap<String, CompilationUnit>();
//		setArguments = new LinkedHashSet<>();
//		setLocalVariables = new LinkedHashSet<LocalEntity>();
//		setFields = new LinkedHashSet<LocalEntity>();
//		final MethodEncoderVisitor visitor = this;
		parser.createASTs(filePaths, null, new String[] {},
				new FileASTRequestor() {
					@Override
					public void acceptAST(String sourceFilePath,
							CompilationUnit javaUnit) {
						// javaUnit.accept(visitor);
						mapCU.put(sourceFilePath, javaUnit);
					}
				}, null);

	}

	public void parseFile(String fileLocation) {
		try {
//			typeOfTraverse = 1;
//			mapLocalcontextForMethod.clear();
			CompilationUnit cu = mapCU.get(fileLocation);
			cu.accept(this);
//			typeOfTraverse = 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void parseForAbstractingMethodInvocation(String fileLocation) {
		try {
//			typeOfTraverse = 3;
//			isParsingType = true;
			CompilationUnit cu = mapCU.get(fileLocation);
			cu.accept(this);
//			typeOfTraverse = 0;
//			isParsingType = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String getUnresolvedType(Type type) {
		if (type.isArrayType()) {
			ArrayType t = (ArrayType) type;
			return getUnresolvedType(t.getElementType())
					+ getDimensions(t.getDimensions());
		} else if (type.isIntersectionType()) {
			IntersectionType it = (IntersectionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getUnresolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " & " + getUnresolvedType(types.get(i));
			return s;
		} else if (type.isParameterizedType()) {
			ParameterizedType t = (ParameterizedType) type;
			return getUnresolvedType(t.getType());
		} else if (type.isUnionType()) {
			UnionType it = (UnionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getUnresolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " | " + getUnresolvedType(types.get(i));
			return s;
		} else if (type.isNameQualifiedType()) {
			NameQualifiedType qt = (NameQualifiedType) type;
			return qt.getQualifier().getFullyQualifiedName() + "."
					+ qt.getName().getIdentifier();
		} else if (type.isPrimitiveType()) {
			return type.toString();
		} else if (type.isQualifiedType()) {
			QualifiedType qt = (QualifiedType) type;
			return getUnresolvedType(qt.getQualifier()) + "."
					+ qt.getName().getIdentifier();
		} else if (type.isSimpleType()) {
			return type.toString();
		} else if (type.isWildcardType()) {
			WildcardType wt = (WildcardType) type;
			String s = "?";
			if (wt.getBound() != null) {
				if (wt.isUpperBound())
					s += "extends ";
				else
					s += "super ";
				s += getUnresolvedType(wt.getBound());
			}
			return s;
		}

		return null;
	}
	
	private static String getDimensions(int dimensions) {
		String s = "";
		for (int i = 0; i < dimensions; i++)
			s += "[]";
		return s;
	}

	/**
	 * Internal synonym for {@link ClassInstanceCreation#getName()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private Name getName(ClassInstanceCreation node) {
		return node.getName();
	}

	/**
	 * Returns the string accumulated in the visit.
	 *
	 * @return the serialized
	 */
	public String getResult() {
		return this.sbCodeTokens.toString();
	}

	/**
	 * Internal synonym for {@link MethodDeclaration#getReturnType()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private static Type getReturnType(MethodDeclaration node) {
		return node.getReturnType();
	}

	/**
	 * Internal synonym for {@link TypeDeclaration#getSuperclass()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private static Name getSuperclass(TypeDeclaration node) {
		return node.getSuperclass();
	}

	/**
	 * Internal synonym for {@link TypeDeclarationStatement#getTypeDeclaration()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private static TypeDeclaration getTypeDeclaration(TypeDeclarationStatement node) {
		return node.getTypeDeclaration();
	}

	/**
	 * Internal synonym for {@link MethodDeclaration#thrownExceptions()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.10
	 */
	private static List thrownExceptions(MethodDeclaration node) {
		return node.thrownExceptions();
	}

	void printIndent() {
		for (int i = 0; i < this.indent; i++)
			this.sbCodeTokens.append("  "); //$NON-NLS-1$
	}

	/**
	 * Appends the text representation of the given modifier flags, followed by a single space.
	 * Used for JLS2 modifiers.
	 *
	 * @param modifiers the modifier flags
	 */
	void printModifiers(int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			this.sbCodeTokens.append("public ");//$NON-NLS-1$
		}
		if (Modifier.isProtected(modifiers)) {
			this.sbCodeTokens.append("protected ");//$NON-NLS-1$
		}
		if (Modifier.isPrivate(modifiers)) {
			this.sbCodeTokens.append("private ");//$NON-NLS-1$
		}
		if (Modifier.isStatic(modifiers)) {
			this.sbCodeTokens.append("static ");//$NON-NLS-1$
		}
		if (Modifier.isAbstract(modifiers)) {
			this.sbCodeTokens.append("abstract ");//$NON-NLS-1$
		}
		if (Modifier.isFinal(modifiers)) {
			this.sbCodeTokens.append("final ");//$NON-NLS-1$
		}
		if (Modifier.isSynchronized(modifiers)) {
			this.sbCodeTokens.append("synchronized ");//$NON-NLS-1$
		}
		if (Modifier.isVolatile(modifiers)) {
			this.sbCodeTokens.append("volatile ");//$NON-NLS-1$
		}
		if (Modifier.isNative(modifiers)) {
			this.sbCodeTokens.append("native ");//$NON-NLS-1$
		}
		if (Modifier.isStrictfp(modifiers)) {
			this.sbCodeTokens.append("strictfp ");//$NON-NLS-1$
		}
		if (Modifier.isTransient(modifiers)) {
			this.sbCodeTokens.append("transient ");//$NON-NLS-1$
		}
	}

	/**
	 * Appends the text representation of the given modifier flags, followed by a single space.
	 * Used for 3.0 modifiers and annotations.
	 *
	 * @param ext the list of modifier and annotation nodes
	 * (element type: <code>IExtendedModifiers</code>)
	 */
	void printModifiers(List ext) {
		for (Iterator it = ext.iterator(); it.hasNext(); ) {
			ASTNode p = (ASTNode) it.next();
			p.accept(this);
			this.sbCodeTokens.append(" ");//$NON-NLS-1$
		}
	}

	/**
	 * reference node helper function that is common to all
	 * the difference reference nodes.
	 * 
	 * @param typeArguments list of type arguments 
	 */
	private void visitReferenceTypeArguments(List typeArguments) {
		this.sbCodeTokens.append("::");//$NON-NLS-1$
		if (!typeArguments.isEmpty()) {
			this.sbCodeTokens.append('<');
			for (Iterator it = typeArguments.iterator(); it.hasNext(); ) {
				Type t = (Type) it.next();
				t.accept(this);
				if (it.hasNext()) {
					this.sbCodeTokens.append(',');
				}
			}
			this.sbCodeTokens.append('>');
		}
	}
	
	private void visitTypeAnnotations(AnnotatableType node) {
		if (node.getAST().apiLevel() >= AST.JLS8) {
			visitAnnotationsList(node.annotations());
		}
	}

	private void visitAnnotationsList(List annotations) {
		for (Iterator it = annotations.iterator(); it.hasNext(); ) {
			Annotation annotation = (Annotation) it.next();
			annotation.accept(this);
			this.sbCodeTokens.append(' ');
		}
	}
	
	/**
	 * Resets this printer so that it can be used again.
	 */
	public void reset() {
		this.sbCodeTokens.setLength(0);
	}

	/**
	 * Internal synonym for {@link TypeDeclaration#superInterfaces()}. Use to alleviate
	 * deprecation warnings.
	 * @deprecated
	 * @since 3.4
	 */
	private List superInterfaces(TypeDeclaration node) {
		return node.superInterfaces();
	}

	/*
	 * @see ASTVisitor#visit(MethodDeclaration)
	 */
	public boolean visit(MethodDeclaration node) {
		this.sbCodeTokens=new StringBuilder();
		if (node.getBody() != null) {
			node.getBody().accept(this);
		}
		buildAbbrevAndTargetTokens();
		return false;
	}
	/*
	 * @see ASTVisitor#visit(AnnotationTypeDeclaration)
	 * @since 3.1
	 */
	public boolean visit(AnnotationTypeDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		printModifiers(node.modifiers());
		this.sbCodeTokens.append("@interface ");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbCodeTokens.append(" {");//$NON-NLS-1$
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
			BodyDeclaration d = (BodyDeclaration) it.next();
			d.accept(this);
		}
		this.sbCodeTokens.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(AnnotationTypeMemberDeclaration)
	 * @since 3.1
	 */
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		printModifiers(node.modifiers());
		node.getType().accept(this);
		this.sbCodeTokens.append(" ");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbCodeTokens.append("()");//$NON-NLS-1$
		if (node.getDefault() != null) {
			this.sbCodeTokens.append(" default ");//$NON-NLS-1$
			node.getDefault().accept(this);
		}
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(AnonymousClassDeclaration)
	 */
	public boolean visit(AnonymousClassDeclaration node) {
		this.sbCodeTokens.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
			BodyDeclaration b = (BodyDeclaration) it.next();
			b.accept(this);
		}
		this.indent--;
		printIndent();
		this.sbCodeTokens.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ArrayAccess)
	 */
	public boolean visit(ArrayAccess node) {
		node.getArray().accept(this);
		this.sbCodeTokens.append("[");//$NON-NLS-1$
		node.getIndex().accept(this);
		this.sbCodeTokens.append("]");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ArrayCreation)
	 */
	public boolean visit(ArrayCreation node) {
		this.sbCodeTokens.append("new ");//$NON-NLS-1$
		ArrayType at = node.getType();
		int dims = at.getDimensions();
		Type elementType = at.getElementType();
		elementType.accept(this);
		for (Iterator it = node.dimensions().iterator(); it.hasNext(); ) {
			this.sbCodeTokens.append("[");//$NON-NLS-1$
			Expression e = (Expression) it.next();
			e.accept(this);
			this.sbCodeTokens.append("]");//$NON-NLS-1$
			dims--;
		}
		// add empty "[]" for each extra array dimension
		for (int i= 0; i < dims; i++) {
			this.sbCodeTokens.append("[]");//$NON-NLS-1$
		}
		if (node.getInitializer() != null) {
			node.getInitializer().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ArrayInitializer)
	 */
	public boolean visit(ArrayInitializer node) {
		this.sbCodeTokens.append("{");//$NON-NLS-1$
		for (Iterator it = node.expressions().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append("}");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ArrayType)
	 */
	public boolean visit(ArrayType node) {
		if (node.getAST().apiLevel() < AST.JLS8) {
			visitComponentType(node);
			this.sbCodeTokens.append("[]");//$NON-NLS-1$
		} else {
			node.getElementType().accept(this);
			List dimensions = node.dimensions();
			int size = dimensions.size();
			for (int i = 0; i < size; i++) {
				Dimension aDimension = (Dimension) dimensions.get(i);
				aDimension.accept(this);
			}
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(AssertStatement)
	 */
	public boolean visit(AssertStatement node) {
		printIndent();
		this.sbCodeTokens.append("assert ");//$NON-NLS-1$
		node.getExpression().accept(this);
		if (node.getMessage() != null) {
			this.sbCodeTokens.append(" : ");//$NON-NLS-1$
			node.getMessage().accept(this);
		}
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Assignment)
	 */
	public boolean visit(Assignment node) {
		node.getLeftHandSide().accept(this);
		this.sbCodeTokens.append(node.getOperator().toString());
		node.getRightHandSide().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Block)
	 */
	public boolean visit(Block node) {
		this.sbCodeTokens.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.statements().iterator(); it.hasNext(); ) {
			Statement s = (Statement) it.next();
			s.accept(this);
		}
		this.indent--;
		printIndent();
		this.sbCodeTokens.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(BlockComment)
	 * @since 3.0
	 */
	public boolean visit(BlockComment node) {
		printIndent();
		this.sbCodeTokens.append("/* */");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(BooleanLiteral)
	 */
	public boolean visit(BooleanLiteral node) {
		if (node.booleanValue() == true) {
			this.sbCodeTokens.append("true");//$NON-NLS-1$
		} else {
			this.sbCodeTokens.append("false");//$NON-NLS-1$
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(BreakStatement)
	 */
	public boolean visit(BreakStatement node) {
		printIndent();
		this.sbCodeTokens.append("break");//$NON-NLS-1$
		if (node.getLabel() != null) {
			this.sbCodeTokens.append(" ");//$NON-NLS-1$
			node.getLabel().accept(this);
		}
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(CastExpression)
	 */
	public boolean visit(CastExpression node) {
		this.sbCodeTokens.append("(");//$NON-NLS-1$
		node.getType().accept(this);
		this.sbCodeTokens.append(")");//$NON-NLS-1$
		node.getExpression().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(CatchClause)
	 */
	public boolean visit(CatchClause node) {
		this.sbCodeTokens.append("catch (");//$NON-NLS-1$
		node.getException().accept(this);
		this.sbCodeTokens.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(CharacterLiteral)
	 */
	public boolean visit(CharacterLiteral node) {
		this.sbCodeTokens.append(node.getEscapedValue());
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ClassInstanceCreation)
	 */
	public boolean visit(ClassInstanceCreation node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			this.sbCodeTokens.append(".");//$NON-NLS-1$
		}
		this.sbCodeTokens.append("new ");//$NON-NLS-1$
		if (node.getAST().apiLevel() == JLS2) {
			getName(node).accept(this);
		}
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbCodeTokens.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(",");//$NON-NLS-1$
					}
				}
				this.sbCodeTokens.append(">");//$NON-NLS-1$
			}
			node.getType().accept(this);
		}
		this.sbCodeTokens.append("(");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(")");//$NON-NLS-1$
		if (node.getAnonymousClassDeclaration() != null) {
			node.getAnonymousClassDeclaration().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(CompilationUnit)
	 */
	public boolean visit(CompilationUnit node) {
		if (node.getPackage() != null) {
			node.getPackage().accept(this);
		}
		for (Iterator it = node.imports().iterator(); it.hasNext(); ) {
			ImportDeclaration d = (ImportDeclaration) it.next();
			d.accept(this);
		}
		for (Iterator it = node.types().iterator(); it.hasNext(); ) {
			AbstractTypeDeclaration d = (AbstractTypeDeclaration) it.next();
			d.accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ConditionalExpression)
	 */
	public boolean visit(ConditionalExpression node) {
		node.getExpression().accept(this);
		this.sbCodeTokens.append(" ? ");//$NON-NLS-1$
		node.getThenExpression().accept(this);
		this.sbCodeTokens.append(" : ");//$NON-NLS-1$
		node.getElseExpression().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ConstructorInvocation)
	 */
	public boolean visit(ConstructorInvocation node) {
		printIndent();
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbCodeTokens.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(",");//$NON-NLS-1$
					}
				}
				this.sbCodeTokens.append(">");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append("this(");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(");\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ContinueStatement)
	 */
	public boolean visit(ContinueStatement node) {
		printIndent();
		this.sbCodeTokens.append("continue");//$NON-NLS-1$
		if (node.getLabel() != null) {
			this.sbCodeTokens.append(" ");//$NON-NLS-1$
			node.getLabel().accept(this);
		}
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}
	
	/*
	 * @see ASTVisitor#visit(CreationReference)
	 * 
	 * @since 3.10
	 */
	public boolean visit(CreationReference node) {
		node.getType().accept(this);
		visitReferenceTypeArguments(node.typeArguments());
		this.sbCodeTokens.append("new");//$NON-NLS-1$
		return false;
	}

	public boolean visit(Dimension node) {
		List annotations = node.annotations();
		if (annotations.size() > 0)
			this.sbCodeTokens.append(' ');
		visitAnnotationsList(annotations);
		this.sbCodeTokens.append("[]"); //$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(DoStatement)
	 */
	public boolean visit(DoStatement node) {
		printIndent();
		this.sbCodeTokens.append("do ");//$NON-NLS-1$
		node.getBody().accept(this);
		this.sbCodeTokens.append(" while (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbCodeTokens.append(");\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(EmptyStatement)
	 */
	public boolean visit(EmptyStatement node) {
		printIndent();
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(EnhancedForStatement)
	 * @since 3.1
	 */
	public boolean visit(EnhancedForStatement node) {
		printIndent();
		this.sbCodeTokens.append("for (");//$NON-NLS-1$
		node.getParameter().accept(this);
		this.sbCodeTokens.append(" : ");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbCodeTokens.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(EnumConstantDeclaration)
	 * @since 3.1
	 */
	public boolean visit(EnumConstantDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		printModifiers(node.modifiers());
		node.getName().accept(this);
		if (!node.arguments().isEmpty()) {
			this.sbCodeTokens.append("(");//$NON-NLS-1$
			for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
				Expression e = (Expression) it.next();
				e.accept(this);
				if (it.hasNext()) {
					this.sbCodeTokens.append(",");//$NON-NLS-1$
				}
			}
			this.sbCodeTokens.append(")");//$NON-NLS-1$
		}
		if (node.getAnonymousClassDeclaration() != null) {
			node.getAnonymousClassDeclaration().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(EnumDeclaration)
	 * @since 3.1
	 */
	public boolean visit(EnumDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		printModifiers(node.modifiers());
		this.sbCodeTokens.append("enum ");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbCodeTokens.append(" ");//$NON-NLS-1$
		if (!node.superInterfaceTypes().isEmpty()) {
			this.sbCodeTokens.append("implements ");//$NON-NLS-1$
			for (Iterator it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
				Type t = (Type) it.next();
				t.accept(this);
				if (it.hasNext()) {
					this.sbCodeTokens.append(", ");//$NON-NLS-1$
				}
			}
			this.sbCodeTokens.append(" ");//$NON-NLS-1$
		}
		this.sbCodeTokens.append("{");//$NON-NLS-1$
		for (Iterator it = node.enumConstants().iterator(); it.hasNext(); ) {
			EnumConstantDeclaration d = (EnumConstantDeclaration) it.next();
			d.accept(this);
			// enum constant declarations do not include punctuation
			if (it.hasNext()) {
				// enum constant declarations are separated by commas
				this.sbCodeTokens.append(", ");//$NON-NLS-1$
			}
		}
		if (!node.bodyDeclarations().isEmpty()) {
			this.sbCodeTokens.append("; ");//$NON-NLS-1$
			for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
				BodyDeclaration d = (BodyDeclaration) it.next();
				d.accept(this);
				// other body declarations include trailing punctuation
			}
		}
		this.sbCodeTokens.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ExpressionMethodReference)
	 * 
	 * @since 3.10
	 */
	public boolean visit(ExpressionMethodReference node) {
		node.getExpression().accept(this);
		visitReferenceTypeArguments(node.typeArguments());
		node.getName().accept(this);
		return false;
	}	

	/*
	 * @see ASTVisitor#visit(ExpressionStatement)
	 */
	public boolean visit(ExpressionStatement node) {
		printIndent();
		node.getExpression().accept(this);
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(FieldAccess)
	 */
	public boolean visit(FieldAccess node) {
		node.getExpression().accept(this);
		this.sbCodeTokens.append(".");//$NON-NLS-1$
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(FieldDeclaration)
	 */
	public boolean visit(FieldDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		printIndent();
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getType().accept(this);
		this.sbCodeTokens.append(" ");//$NON-NLS-1$
		for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
			VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
			f.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(", ");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ForStatement)
	 */
	public boolean visit(ForStatement node) {
		printIndent();
		this.sbCodeTokens.append("for (");//$NON-NLS-1$
		for (Iterator it = node.initializers().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) this.sbCodeTokens.append(", ");//$NON-NLS-1$
		}
		this.sbCodeTokens.append("; ");//$NON-NLS-1$
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		this.sbCodeTokens.append("; ");//$NON-NLS-1$
		for (Iterator it = node.updaters().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) this.sbCodeTokens.append(", ");//$NON-NLS-1$
		}
		this.sbCodeTokens.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(IfStatement)
	 */
	public boolean visit(IfStatement node) {
		printIndent();
		this.sbCodeTokens.append("if (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbCodeTokens.append(") ");//$NON-NLS-1$
		node.getThenStatement().accept(this);
		if (node.getElseStatement() != null) {
			this.sbCodeTokens.append(" else ");//$NON-NLS-1$
			node.getElseStatement().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ImportDeclaration)
	 */
	public boolean visit(ImportDeclaration node) {
		printIndent();
		this.sbCodeTokens.append("import ");//$NON-NLS-1$
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.isStatic()) {
				this.sbCodeTokens.append("static ");//$NON-NLS-1$
			}
		}
		node.getName().accept(this);
		if (node.isOnDemand()) {
			this.sbCodeTokens.append(".*");//$NON-NLS-1$
		}
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(InfixExpression)
	 */
	public boolean visit(InfixExpression node) {
		node.getLeftOperand().accept(this);
		this.sbCodeTokens.append(' ');  // for cases like x= i - -1; or x= i++ + ++i;
		this.sbCodeTokens.append(node.getOperator().toString());
		this.sbCodeTokens.append(' ');
		node.getRightOperand().accept(this);
		final List extendedOperands = node.extendedOperands();
		if (extendedOperands.size() != 0) {
			this.sbCodeTokens.append(' ');
			for (Iterator it = extendedOperands.iterator(); it.hasNext(); ) {
				this.sbCodeTokens.append(node.getOperator().toString()).append(' ');
				Expression e = (Expression) it.next();
				e.accept(this);
			}
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Initializer)
	 */
	public boolean visit(Initializer node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(InstanceofExpression)
	 */
	public boolean visit(InstanceofExpression node) {
		node.getLeftOperand().accept(this);
		this.sbCodeTokens.append(" instanceof ");//$NON-NLS-1$
		node.getRightOperand().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(IntersectionType)
	 * @since 3.7
	 */
	public boolean visit(IntersectionType node) {
		for (Iterator it = node.types().iterator(); it.hasNext(); ) {
			Type t = (Type) it.next();
			t.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(" & "); //$NON-NLS-1$
			}
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Javadoc)
	 */
	public boolean visit(Javadoc node) {
		printIndent();
		this.sbCodeTokens.append("/** ");//$NON-NLS-1$
		for (Iterator it = node.tags().iterator(); it.hasNext(); ) {
			ASTNode e = (ASTNode) it.next();
			e.accept(this);
		}
		this.sbCodeTokens.append("\n */\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(LabeledStatement)
	 */
	public boolean visit(LabeledStatement node) {
		printIndent();
		node.getLabel().accept(this);
		this.sbCodeTokens.append(": ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(LambdaExpression)
	 */
	public boolean visit(LambdaExpression node) {
		boolean hasParentheses = node.hasParentheses();
		if (hasParentheses)
			this.sbCodeTokens.append('(');
		for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
			VariableDeclaration v = (VariableDeclaration) it.next();
			v.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		if (hasParentheses)
			this.sbCodeTokens.append(')');
		this.sbCodeTokens.append(" -> "); //$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(LineComment)
	 * @since 3.0
	 */
	public boolean visit(LineComment node) {
		this.sbCodeTokens.append("//\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(MarkerAnnotation)
	 * @since 3.1
	 */
	public boolean visit(MarkerAnnotation node) {
		this.sbCodeTokens.append("@");//$NON-NLS-1$
		node.getTypeName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(MemberRef)
	 * @since 3.0
	 */
	public boolean visit(MemberRef node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
		}
		this.sbCodeTokens.append("#");//$NON-NLS-1$
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(MemberValuePair)
	 * @since 3.1
	 */
	public boolean visit(MemberValuePair node) {
		node.getName().accept(this);
		this.sbCodeTokens.append("=");//$NON-NLS-1$
		node.getValue().accept(this);
		return false;
	}

	

	/*
	 * @see ASTVisitor#visit(MethodInvocation)
	 */
	public boolean visit(MethodInvocation node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			this.sbCodeTokens.append(".");//$NON-NLS-1$
		}
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbCodeTokens.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(",");//$NON-NLS-1$
					}
				}
				this.sbCodeTokens.append(">");//$NON-NLS-1$
			}
		}
		node.getName().accept(this);
		this.sbCodeTokens.append("(");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(")");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(MethodRef)
	 * @since 3.0
	 */
	public boolean visit(MethodRef node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
		}
		this.sbCodeTokens.append("#");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbCodeTokens.append("(");//$NON-NLS-1$
		for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
			MethodRefParameter e = (MethodRefParameter) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(")");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(MethodRefParameter)
	 * @since 3.0
	 */
	public boolean visit(MethodRefParameter node) {
		node.getType().accept(this);
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.isVarargs()) {
				this.sbCodeTokens.append("...");//$NON-NLS-1$
			}
		}
		if (node.getName() != null) {
			this.sbCodeTokens.append(" ");//$NON-NLS-1$
			node.getName().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(Modifier)
	 * @since 3.1
	 */
	public boolean visit(Modifier node) {
		this.sbCodeTokens.append(node.getKeyword().toString());
		return false;
	}

	/*
	 * @see ASTVisitor#visit(NameQualifiedType)
	 * @since 3.10
	 */
	public boolean visit(NameQualifiedType node) {
		node.getQualifier().accept(this);
		this.sbCodeTokens.append('.');
		visitTypeAnnotations(node);
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(NormalAnnotation)
	 * @since 3.1
	 */
	public boolean visit(NormalAnnotation node) {
		this.sbCodeTokens.append("@");//$NON-NLS-1$
		node.getTypeName().accept(this);
		this.sbCodeTokens.append("(");//$NON-NLS-1$
		for (Iterator it = node.values().iterator(); it.hasNext(); ) {
			MemberValuePair p = (MemberValuePair) it.next();
			p.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(")");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(NullLiteral)
	 */
	public boolean visit(NullLiteral node) {
		this.sbCodeTokens.append("null");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(NumberLiteral)
	 */
	public boolean visit(NumberLiteral node) {
		this.sbCodeTokens.append(node.getToken());
		return false;
	}

	/*
	 * @see ASTVisitor#visit(PackageDeclaration)
	 */
	public boolean visit(PackageDeclaration node) {
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.getJavadoc() != null) {
				node.getJavadoc().accept(this);
			}
			for (Iterator it = node.annotations().iterator(); it.hasNext(); ) {
				Annotation p = (Annotation) it.next();
				p.accept(this);
				this.sbCodeTokens.append(" ");//$NON-NLS-1$
			}
		}
		printIndent();
		this.sbCodeTokens.append("package ");//$NON-NLS-1$
		node.getName().accept(this);
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ParameterizedType)
	 * @since 3.1
	 */
	public boolean visit(ParameterizedType node) {
		node.getType().accept(this);
		this.sbCodeTokens.append("<");//$NON-NLS-1$
		for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
			Type t = (Type) it.next();
			t.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(">");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ParenthesizedExpression)
	 */
	public boolean visit(ParenthesizedExpression node) {
		this.sbCodeTokens.append("(");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbCodeTokens.append(")");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(PostfixExpression)
	 */
	public boolean visit(PostfixExpression node) {
		node.getOperand().accept(this);
		this.sbCodeTokens.append(node.getOperator().toString());
		return false;
	}

	/*
	 * @see ASTVisitor#visit(PrefixExpression)
	 */
	public boolean visit(PrefixExpression node) {
		this.sbCodeTokens.append(node.getOperator().toString());
		node.getOperand().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(PrimitiveType)
	 */
	public boolean visit(PrimitiveType node) {
		visitTypeAnnotations(node);
		this.sbCodeTokens.append(node.getPrimitiveTypeCode().toString());
		return false;
	}

	/*
	 * @see ASTVisitor#visit(QualifiedName)
	 */
	public boolean visit(QualifiedName node) {
		node.getQualifier().accept(this);
		this.sbCodeTokens.append(".");//$NON-NLS-1$
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(QualifiedType)
	 * @since 3.1
	 */
	public boolean visit(QualifiedType node) {
		node.getQualifier().accept(this);
		this.sbCodeTokens.append(".");//$NON-NLS-1$
		visitTypeAnnotations(node);
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ReturnStatement)
	 */
	public boolean visit(ReturnStatement node) {
		printIndent();
		this.sbCodeTokens.append("return");//$NON-NLS-1$
		if (node.getExpression() != null) {
			this.sbCodeTokens.append(" ");//$NON-NLS-1$
			node.getExpression().accept(this);
		}
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SimpleName)
	 */
	public boolean visit(SimpleName node) {
		this.sbCodeTokens.append(node.getIdentifier());
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SimpleType)
	 */
	public boolean visit(SimpleType node) {
		visitTypeAnnotations(node);
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SingleMemberAnnotation)
	 * @since 3.1
	 */
	public boolean visit(SingleMemberAnnotation node) {
		this.sbCodeTokens.append("@");//$NON-NLS-1$
		node.getTypeName().accept(this);
		this.sbCodeTokens.append("(");//$NON-NLS-1$
		node.getValue().accept(this);
		this.sbCodeTokens.append(")");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SingleVariableDeclaration)
	 */
	public boolean visit(SingleVariableDeclaration node) {
		printIndent();
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getType().accept(this);
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.isVarargs()) {
				if (node.getAST().apiLevel() >= AST.JLS8) {
					List annotations = node.varargsAnnotations();
					if (annotations.size() > 0) {
						this.sbCodeTokens.append(' ');
					}
					visitAnnotationsList(annotations);
				}
				this.sbCodeTokens.append("...");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(" ");//$NON-NLS-1$
		node.getName().accept(this);
		int size = node.getExtraDimensions();
		if (node.getAST().apiLevel() >= AST.JLS8) {
			List dimensions = node.extraDimensions();
			for (int i = 0; i < size; i++) {
				visit((Dimension) dimensions.get(i));
			}
		} else {
			for (int i = 0; i < size; i++) {
				this.sbCodeTokens.append("[]"); //$NON-NLS-1$
			}
		}
		if (node.getInitializer() != null) {
			this.sbCodeTokens.append("=");//$NON-NLS-1$
			node.getInitializer().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(StringLiteral)
	 */
	public boolean visit(StringLiteral node) {
		this.sbCodeTokens.append(node.getEscapedValue());
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SuperConstructorInvocation)
	 */
	public boolean visit(SuperConstructorInvocation node) {
		printIndent();
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			this.sbCodeTokens.append(".");//$NON-NLS-1$
		}
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbCodeTokens.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(",");//$NON-NLS-1$
					}
				}
				this.sbCodeTokens.append(">");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append("super(");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(");\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SuperFieldAccess)
	 */
	public boolean visit(SuperFieldAccess node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			this.sbCodeTokens.append(".");//$NON-NLS-1$
		}
		this.sbCodeTokens.append("super.");//$NON-NLS-1$
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SuperMethodInvocation)
	 */
	public boolean visit(SuperMethodInvocation node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			this.sbCodeTokens.append(".");//$NON-NLS-1$
		}
		this.sbCodeTokens.append("super.");//$NON-NLS-1$
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.sbCodeTokens.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(",");//$NON-NLS-1$
					}
				}
				this.sbCodeTokens.append(">");//$NON-NLS-1$
			}
		}
		node.getName().accept(this);
		this.sbCodeTokens.append("(");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(",");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(")");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SuperMethodReference)
	 * 
	 * @since 3.10
	 */
	public boolean visit(SuperMethodReference node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			this.sbCodeTokens.append('.');
		}
		this.sbCodeTokens.append("super");//$NON-NLS-1$
		visitReferenceTypeArguments(node.typeArguments());
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SwitchCase)
	 */
	public boolean visit(SwitchCase node) {
		if (node.isDefault()) {
			this.sbCodeTokens.append("default :\n");//$NON-NLS-1$
		} else {
			this.sbCodeTokens.append("case ");//$NON-NLS-1$
			node.getExpression().accept(this);
			this.sbCodeTokens.append(":\n");//$NON-NLS-1$
		}
		this.indent++; //decremented in visit(SwitchStatement)
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SwitchStatement)
	 */
	public boolean visit(SwitchStatement node) {
		this.sbCodeTokens.append("switch (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbCodeTokens.append(") ");//$NON-NLS-1$
		this.sbCodeTokens.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.statements().iterator(); it.hasNext(); ) {
			Statement s = (Statement) it.next();
			s.accept(this);
			this.indent--; // incremented in visit(SwitchCase)
		}
		this.indent--;
		printIndent();
		this.sbCodeTokens.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(SynchronizedStatement)
	 */
	public boolean visit(SynchronizedStatement node) {
		this.sbCodeTokens.append("synchronized (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbCodeTokens.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TagElement)
	 * @since 3.0
	 */
	public boolean visit(TagElement node) {
		if (node.isNested()) {
			// nested tags are always enclosed in braces
			this.sbCodeTokens.append("{");//$NON-NLS-1$
		} else {
			// top-level tags always begin on a new line
			this.sbCodeTokens.append("\n * ");//$NON-NLS-1$
		}
		boolean previousRequiresWhiteSpace = false;
		if (node.getTagName() != null) {
			this.sbCodeTokens.append(node.getTagName());
			previousRequiresWhiteSpace = true;
		}
		boolean previousRequiresNewLine = false;
		for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
			ASTNode e = (ASTNode) it.next();
			// Name, MemberRef, MethodRef, and nested TagElement do not include white space.
			// TextElements don't always include whitespace, see <https://bugs.eclipse.org/206518>.
			boolean currentIncludesWhiteSpace = false;
			if (e instanceof TextElement) {
				String text = ((TextElement) e).getText();
				if (text.length() > 0 && ScannerHelper.isWhitespace(text.charAt(0))) {
					currentIncludesWhiteSpace = true; // workaround for https://bugs.eclipse.org/403735
				}
			}
			if (previousRequiresNewLine && currentIncludesWhiteSpace) {
				this.sbCodeTokens.append("\n * ");//$NON-NLS-1$
			}
			previousRequiresNewLine = currentIncludesWhiteSpace;
			// add space if required to separate
			if (previousRequiresWhiteSpace && !currentIncludesWhiteSpace) {
				this.sbCodeTokens.append(" "); //$NON-NLS-1$
			}
			e.accept(this);
			previousRequiresWhiteSpace = !currentIncludesWhiteSpace && !(e instanceof TagElement);
		}
		if (node.isNested()) {
			this.sbCodeTokens.append("}");//$NON-NLS-1$
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TextElement)
	 * @since 3.0
	 */
	public boolean visit(TextElement node) {
		this.sbCodeTokens.append(node.getText());
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ThisExpression)
	 */
	public boolean visit(ThisExpression node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			this.sbCodeTokens.append(".");//$NON-NLS-1$
		}
		this.sbCodeTokens.append("this");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(ThrowStatement)
	 */
	public boolean visit(ThrowStatement node) {
		printIndent();
		this.sbCodeTokens.append("throw ");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TryStatement)
	 */
	public boolean visit(TryStatement node) {
		printIndent();
		this.sbCodeTokens.append("try ");//$NON-NLS-1$
		if (node.getAST().apiLevel() >= JLS4) {
			List resources = node.resources();
			if (!resources.isEmpty()) {
				this.sbCodeTokens.append('(');
				for (Iterator it = resources.iterator(); it.hasNext(); ) {
					VariableDeclarationExpression variable = (VariableDeclarationExpression) it.next();
					variable.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(';');
					}
				}
				this.sbCodeTokens.append(')');
			}
		}
		node.getBody().accept(this);
		this.sbCodeTokens.append(" ");//$NON-NLS-1$
		for (Iterator it = node.catchClauses().iterator(); it.hasNext(); ) {
			CatchClause cc = (CatchClause) it.next();
			cc.accept(this);
		}
		if (node.getFinally() != null) {
			this.sbCodeTokens.append(" finally ");//$NON-NLS-1$
			node.getFinally().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TypeDeclaration)
	 */
	public boolean visit(TypeDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		this.sbCodeTokens.append(node.isInterface() ? "interface " : "class ");//$NON-NLS-2$//$NON-NLS-1$
		node.getName().accept(this);
		if (node.getAST().apiLevel() >= JLS3) {
			if (!node.typeParameters().isEmpty()) {
				this.sbCodeTokens.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeParameters().iterator(); it.hasNext(); ) {
					TypeParameter t = (TypeParameter) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(",");//$NON-NLS-1$
					}
				}
				this.sbCodeTokens.append(">");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(" ");//$NON-NLS-1$
		if (node.getAST().apiLevel() == JLS2) {
			if (getSuperclass(node) != null) {
				this.sbCodeTokens.append("extends ");//$NON-NLS-1$
				getSuperclass(node).accept(this);
				this.sbCodeTokens.append(" ");//$NON-NLS-1$
			}
			if (!superInterfaces(node).isEmpty()) {
				this.sbCodeTokens.append(node.isInterface() ? "extends " : "implements ");//$NON-NLS-2$//$NON-NLS-1$
				for (Iterator it = superInterfaces(node).iterator(); it.hasNext(); ) {
					Name n = (Name) it.next();
					n.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(", ");//$NON-NLS-1$
					}
				}
				this.sbCodeTokens.append(" ");//$NON-NLS-1$
			}
		}
		if (node.getAST().apiLevel() >= JLS3) {
			if (node.getSuperclassType() != null) {
				this.sbCodeTokens.append("extends ");//$NON-NLS-1$
				node.getSuperclassType().accept(this);
				this.sbCodeTokens.append(" ");//$NON-NLS-1$
			}
			if (!node.superInterfaceTypes().isEmpty()) {
				this.sbCodeTokens.append(node.isInterface() ? "extends " : "implements ");//$NON-NLS-2$//$NON-NLS-1$
				for (Iterator it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.sbCodeTokens.append(", ");//$NON-NLS-1$
					}
				}
				this.sbCodeTokens.append(" ");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append("{\n");//$NON-NLS-1$
		this.indent++;
		for (Iterator it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
			BodyDeclaration d = (BodyDeclaration) it.next();
			d.accept(this);
		}
		this.indent--;
		printIndent();
		this.sbCodeTokens.append("}\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TypeDeclarationStatement)
	 */
	public boolean visit(TypeDeclarationStatement node) {
		if (node.getAST().apiLevel() == JLS2) {
			getTypeDeclaration(node).accept(this);
		}
		if (node.getAST().apiLevel() >= JLS3) {
			node.getDeclaration().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TypeLiteral)
	 */
	public boolean visit(TypeLiteral node) {
		node.getType().accept(this);
		this.sbCodeTokens.append(".class");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TypeMethodReference)
	 * 
	 * @since 3.10
	 */
	public boolean visit(TypeMethodReference node) {
		node.getType().accept(this);
		visitReferenceTypeArguments(node.typeArguments());
		node.getName().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(TypeParameter)
	 * @since 3.1
	 */
	public boolean visit(TypeParameter node) {
		if (node.getAST().apiLevel() >= AST.JLS8) {
			printModifiers(node.modifiers());
		}
		node.getName().accept(this);
		if (!node.typeBounds().isEmpty()) {
			this.sbCodeTokens.append(" extends ");//$NON-NLS-1$
			for (Iterator it = node.typeBounds().iterator(); it.hasNext(); ) {
				Type t = (Type) it.next();
				t.accept(this);
				if (it.hasNext()) {
					this.sbCodeTokens.append(" & ");//$NON-NLS-1$
				}
			}
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(UnionType)
	 * @since 3.7
	 */
	public boolean visit(UnionType node) {
		for (Iterator it = node.types().iterator(); it.hasNext(); ) {
			Type t = (Type) it.next();
			t.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append('|');
			}
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(VariableDeclarationExpression)
	 */
	public boolean visit(VariableDeclarationExpression node) {
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getType().accept(this);
		this.sbCodeTokens.append(" ");//$NON-NLS-1$
		for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
			VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
			f.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(", ");//$NON-NLS-1$
			}
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(VariableDeclarationFragment)
	 */
	public boolean visit(VariableDeclarationFragment node) {
		node.getName().accept(this);
		int size = node.getExtraDimensions();
		if (node.getAST().apiLevel() >= AST.JLS8) {
			List dimensions = node.extraDimensions();
			for (int i = 0; i < size; i++) {
				visit((Dimension) dimensions.get(i));
			}
		} else {
			for (int i = 0; i < size; i++) {
				this.sbCodeTokens.append("[]");//$NON-NLS-1$
			}
		}
		if (node.getInitializer() != null) {
			this.sbCodeTokens.append("=");//$NON-NLS-1$
			node.getInitializer().accept(this);
		}
		return false;
	}

	/*
	 * @see ASTVisitor#visit(VariableDeclarationStatement)
	 */
	public boolean visit(VariableDeclarationStatement node) {
		printIndent();
		if (node.getAST().apiLevel() == JLS2) {
			printModifiers(node.getModifiers());
		}
		if (node.getAST().apiLevel() >= JLS3) {
			printModifiers(node.modifiers());
		}
		node.getType().accept(this);
		this.sbCodeTokens.append(" ");//$NON-NLS-1$
		for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
			VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
			f.accept(this);
			if (it.hasNext()) {
				this.sbCodeTokens.append(", ");//$NON-NLS-1$
			}
		}
		this.sbCodeTokens.append(";\n");//$NON-NLS-1$
		return false;
	}

	/*
	 * @see ASTVisitor#visit(WhileStatement)
	 */
	public boolean visit(WhileStatement node) {
		printIndent();
		this.sbCodeTokens.append("while (");//$NON-NLS-1$
		node.getExpression().accept(this);
		this.sbCodeTokens.append(") ");//$NON-NLS-1$
		node.getBody().accept(this);
		return false;
	}

	/*
	 * @see ASTVisitor#visit(WildcardType)
	 * @since 3.1
	 */
	public boolean visit(WildcardType node) {
		visitTypeAnnotations(node);
		this.sbCodeTokens.append("?");//$NON-NLS-1$
		Type bound = node.getBound();
		if (bound != null) {
			if (node.isUpperBound()) {
				this.sbCodeTokens.append(" extends ");//$NON-NLS-1$
			} else {
				this.sbCodeTokens.append(" super ");//$NON-NLS-1$
			}
			bound.accept(this);
		}
		return false;
	}

	/**
	 * @deprecated
	 */
	private void visitComponentType(ArrayType node) {
		node.getComponentType().accept(this);
	}

}
