package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.Assignment.Operator;
import org.eclipse.jdt.internal.core.dom.NaiveASTFlattener;



public class JavaASTUtil {

	public static String Info_Code_To_Attach="Info_Code_To_Attach";
	public static String Info_Fully_Qualified_Name="Info_Fully_Qualified_Name";
	
	@SuppressWarnings("rawtypes")
	public static ASTNode parseSource(String source, int kind) {
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setCompilerOptions(options);
		parser.setSource(source.toCharArray());
		parser.setKind(kind);
		parser.setResolveBindings(false);
		parser.setBindingsRecovery(true);
		ASTNode ast = parser.createAST(null);
		return ast;
	}

	@SuppressWarnings("rawtypes")
	public static ASTNode getMethodNode(CompilationUnit cu,
			String methodSignature) {
		ASTNode ast = null;

		return ast;
	}

	public static final HashMap<String, String> infixExpressionLables = new HashMap<>(),
			assignmentLabels = new HashMap<>();
	protected static final String PROPERTY_CONDITIONAL_OPERATOR = "CC";

	static {
		// Arithmetic Operators
		infixExpressionLables.put(InfixExpression.Operator.DIVIDE.toString(),
				"<a>");
		infixExpressionLables.put(InfixExpression.Operator.MINUS.toString(),
				"<a>");
		infixExpressionLables.put(InfixExpression.Operator.PLUS.toString(),
				"<a>");
		infixExpressionLables.put(
				InfixExpression.Operator.REMAINDER.toString(), "<a>");
		infixExpressionLables.put(InfixExpression.Operator.TIMES.toString(),
				"<a>");
		// Equality and Relational Operators
		infixExpressionLables.put(InfixExpression.Operator.EQUALS.toString(),
				"<r>");
		infixExpressionLables.put(InfixExpression.Operator.GREATER.toString(),
				"<r>");
		infixExpressionLables.put(
				InfixExpression.Operator.GREATER_EQUALS.toString(), "<r>");
		infixExpressionLables.put(InfixExpression.Operator.LESS.toString(),
				"<r>");
		infixExpressionLables.put(
				InfixExpression.Operator.LESS_EQUALS.toString(), "<r>");
		infixExpressionLables.put(
				InfixExpression.Operator.NOT_EQUALS.toString(), "<r>");
		// Conditional Operators
		infixExpressionLables.put(
				InfixExpression.Operator.CONDITIONAL_AND.toString(), "<c>");
		infixExpressionLables.put(
				InfixExpression.Operator.CONDITIONAL_OR.toString(), "<c>");
		// Bitwise and Bit Shift Operators
		infixExpressionLables.put(InfixExpression.Operator.AND.toString(),
				"<b>");
		infixExpressionLables
				.put(InfixExpression.Operator.OR.toString(), "<b>");
		infixExpressionLables.put(InfixExpression.Operator.XOR.toString(),
				"<b>");
		infixExpressionLables.put(
				InfixExpression.Operator.LEFT_SHIFT.toString(), "<b>");
		infixExpressionLables.put(
				InfixExpression.Operator.RIGHT_SHIFT_SIGNED.toString(), "<b>");
		infixExpressionLables
				.put(InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED.toString(),
						"<b>");

		assignmentLabels.put(Assignment.Operator.ASSIGN.toString(), "=");
		// Arithmetic Operators
		assignmentLabels.put(Assignment.Operator.DIVIDE_ASSIGN.toString(),
				"<a>");
		assignmentLabels
				.put(Assignment.Operator.MINUS_ASSIGN.toString(), "<a>");
		assignmentLabels.put(Assignment.Operator.PLUS_ASSIGN.toString(), "<a>");
		assignmentLabels.put(Assignment.Operator.REMAINDER_ASSIGN.toString(),
				"<a>");
		assignmentLabels
				.put(Assignment.Operator.TIMES_ASSIGN.toString(), "<a>");
		// Bitwise and Bit Shift Operators
		assignmentLabels.put(Assignment.Operator.BIT_AND_ASSIGN.toString(),
				"<b>");
		assignmentLabels.put(Assignment.Operator.BIT_OR_ASSIGN.toString(),
				"<b>");
		assignmentLabels.put(Assignment.Operator.BIT_XOR_ASSIGN.toString(),
				"<b>");
		assignmentLabels.put(Assignment.Operator.LEFT_SHIFT_ASSIGN.toString(),
				"<b>");
		assignmentLabels
				.put(Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN.toString(),
						"<b>");
		assignmentLabels.put(
				Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN.toString(),
				"<b>");
	}

	@SuppressWarnings("rawtypes")
	public static ASTNode parseSource(String source, String path, String name,
			String[] classpaths) {
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		String srcDir = getSrcDir(source, path, name);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setCompilerOptions(options);
		parser.setEnvironment(
				classpaths == null ? new String[] {} : classpaths,
				new String[] { srcDir }, new String[] { "UTF-8" }, true);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setSource(source.toCharArray());
		parser.setUnitName(name);
		ASTNode ast = parser.createAST(null);
		return ast;
	}

	public static Expression parseExpression(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource(source.toCharArray());
		ASTNode ast = parser.createAST(null);
		if (isExpression(ast, source))
			return (Expression) ast;
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static String getSrcDir(String source, String path, String name) {
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setCompilerOptions(options);
		parser.setSource(source.toCharArray());
		ASTNode ast = parser.createAST(null);
		CompilationUnit cu = (CompilationUnit) ast;
		String srcDir = path;
		if (cu.getPackage() != null) {
			String p = cu.getPackage().getName().getFullyQualifiedName();
			int end = path.length() - p.length() - 1 - name.length();
			if (end > 0)
				srcDir = path.substring(0, end);
		} else {
			int end = path.length() - name.length();
			if (end > 0)
				srcDir = path.substring(0, end);
		}
		return srcDir;
	}

	public static String getSource(ASTNode node) {
		NaiveASTFlattener flatterner = new NaiveASTFlattener();
		node.accept(flatterner);
		return flatterner.getResult();
	}

	public static boolean isLiteral(int astNodeType) {
		return ASTNode.nodeClassForType(astNodeType).getSimpleName()
				.endsWith("Literal");
	}

	public static boolean isLiteral(ASTNode node) {
		int type = node.getNodeType();
		if (type == ASTNode.BOOLEAN_LITERAL
				|| type == ASTNode.CHARACTER_LITERAL
				|| type == ASTNode.NULL_LITERAL
				|| type == ASTNode.NUMBER_LITERAL
				|| type == ASTNode.STRING_LITERAL)
			return true;
		if (type == ASTNode.PREFIX_EXPRESSION) {
			PrefixExpression pe = (PrefixExpression) node;
			return isLiteral(pe.getOperand());
		}
		if (type == ASTNode.POSTFIX_EXPRESSION) {
			PostfixExpression pe = (PostfixExpression) node;
			return isLiteral(pe.getOperand());
		}
		if (type == ASTNode.PARENTHESIZED_EXPRESSION) {
			ParenthesizedExpression pe = (ParenthesizedExpression) node;
			return isLiteral(pe.getExpression());
		}

		return false;
	}

	public static boolean isPublic(MethodDeclaration declaration) {
		for (int i = 0; i < declaration.modifiers().size(); i++) {
			Modifier m = (Modifier) declaration.modifiers().get(i);
			if (m.isPublic())
				return true;
		}
		return false;
	}

	public static String getParameters(MethodDeclaration method) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < method.parameters().size(); i++) {
			SingleVariableDeclaration d = (SingleVariableDeclaration) (method
					.parameters().get(i));
			String type = getSimpleType(d.getType());
			sb.append("\t" + type);
		}
		sb.append("\t)");
		return sb.toString();
	}
	
	public static String getASTNodeInfo(ASTNode node, String infoType){
		String strResult="";
		try{
			
			if(node instanceof VariableDeclarationStatement){
				VariableDeclarationStatement objNode=(VariableDeclarationStatement) node;
				if(infoType.equals(JavaASTUtil.Info_Code_To_Attach)){
					VariableDeclarationFragment fragment0=(VariableDeclarationFragment)objNode.fragments().get(0);
					strResult=fragment0.getName().getIdentifier();
				} else if(infoType.equals(JavaASTUtil.Info_Fully_Qualified_Name)){
					ITypeBinding tb = null;
					Type type=objNode.getType();
					
					if (type != null) {
						tb = type.resolveBinding();
						if (tb != null) {
							strResult = tb.getQualifiedName();
							
							if(strResult.equals("int[]")||strResult.equals("int")){
								strResult="java.lang.int[]";
							}
						}
					}
				}
			} 
			else if(node instanceof SingleVariableDeclaration){
				SingleVariableDeclaration objNode=(SingleVariableDeclaration) node;
				if(infoType.equals(JavaASTUtil.Info_Code_To_Attach)){					
					strResult=objNode.getName().getIdentifier();
				} else if(infoType.equals(JavaASTUtil.Info_Fully_Qualified_Name)){
					ITypeBinding tb = null;
					Type type=objNode.getType();
//					LogUtil.writeLog("strresult "+node.getClass());
					if (type != null) {
						tb = type.resolveBinding();
						if (tb != null) {
							strResult = tb.getQualifiedName();
							
							if(strResult.equals("int[]")||strResult.equals("int")){
								strResult="java.lang.int[]";
							}
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return strResult;
	}

	public static String buildNameWithParameters(MethodDeclaration method) {
		return method.getName().getIdentifier() + "\t" + getParameters(method);
	}

	public static String buildNameWithParameters(IMethodBinding mb) {
		return mb.getName() + "\t" + getParameters(mb);
	}

	public static String getFullParameters(MethodDeclaration method) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < method.parameters().size(); i++) {
			SingleVariableDeclaration d = (SingleVariableDeclaration) (method
					.parameters().get(i));
			String type = getSimpleType(d.getType());
			sb.append("\t" + type);
		}
		sb.append("\t)");
		return sb.toString();
	}

	public static String buildAllSigIngo(MethodDeclaration method) {
		Type type = method.getReturnType2();
		ITypeBinding tb = null;
		String typeReturn = null;
		if (type != null) {
			tb = type.resolveBinding();
			if (tb != null) {
				typeReturn = tb.getName();

			}
		}
		String strResult = method.getName().getIdentifier() + "#"
				+ method.getModifiers() + "#void#" + getParameters(method);
		if (typeReturn != null) {
			strResult = method.getName().getIdentifier() + "#"
					+ method.getModifiers() + "#" + typeReturn + "#"
					+ getParameters(method);

		}
		return strResult;
	}
	
	public static String buildAllSigIngo(MethodInvocation method) {
		IMethodBinding ibind=method.resolveMethodBinding();
		if(ibind==null) return "";
		StringBuilder sbResult=new StringBuilder();
		try{
			ITypeBinding typeReceiver=ibind.getDeclaringClass();
			ITypeBinding[] arrParams=ibind.getParameterTypes();
			String strReceiver=typeReceiver!=null?typeReceiver.getQualifiedName():"";
			sbResult.append(ibind.getModifiers());
			sbResult.append("#");
			sbResult.append(strReceiver);
			sbResult.append("#");
			sbResult.append(ibind.getName());
			sbResult.append("#(");
			if(arrParams!=null){
				for(int i=0;i<arrParams.length;i++){
					String strParam=arrParams[i]!=null?arrParams[i].getQualifiedName():"";
					sbResult.append(strParam);
					if(i!=arrParams.length-1){
						sbResult.append(",");
					}
				}
			}
			
			sbResult.append(")");
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		
		return sbResult.toString();
	}

	public static String buildFullAllSigIngo(MethodDeclaration method) {
		Type type = method.getReturnType2();
		ITypeBinding tb = null;
		String typeReturn = null;
		if (type != null) {
			tb = type.resolveBinding();
			if (tb != null) {
				typeReturn = tb.getQualifiedName();

			}
		}
		String strResult = method.getName().getIdentifier() + "#"
				+ method.getModifiers() + "#void#" + getFullParameters(method);
		if (typeReturn != null) {
			strResult = method.getName().getIdentifier() + "#"
					+ method.getModifiers() + "#" + typeReturn + "#"
					+ getFullParameters(method);

		}
		return strResult;
	}

	

//	public static MethodItem getMethodItemFromAPIDictionary(
//			String currentPackage, String currentClass, String content) {
//		MethodItem item = new MethodItem();
//		String[] arrContent = content.split("#");
//		if (arrContent.length >= 4) {
//			item.setShortReturnType(StringUtil.getNameFromFQN(arrContent[0]));
//			item.setFullReturnType(arrContent[0]);
//			item.setIdentifier(arrContent[1]);
//			item.setPackageName(currentPackage);
//			item.setClassName(currentClass);
//			
//			ArrayList<String> arrShortParams = new ArrayList<String>();
//			ArrayList<String> arrFullParams = new ArrayList<String>();
//			if (!arrContent[2].trim().isEmpty()) {
//				String[] arrParams = arrContent[2].split("_");
//
//				for (int i = 0; i < arrParams.length; i++) {
//
//					arrShortParams.add(StringUtil.getNameFromFQN(arrParams[i]));
//					arrFullParams.add(arrParams[i]);
//
//				}
//
//			}
//			item.setShortParamType(arrShortParams);
//			item.setFullParamType(arrFullParams);
//			item.setStatic(Boolean.valueOf(arrContent[3]));
//			return item;
//		}
//		return null;
//
//	}
	
	

	private static String getParameters(IMethodBinding mb) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < mb.getParameterTypes().length; i++) {
			ITypeBinding tb = mb.getParameterTypes()[i].getTypeDeclaration();
			String type = tb.getName();
			sb.append("\t" + type);
		}
		sb.append("\t)");
		return sb.toString();
	}

	public static String getSimpleType(VariableDeclarationFragment f) {
		String dimensions = "";
		for (int i = 0; i < f.getExtraDimensions(); i++)
			dimensions += "[]";
		ASTNode p = f.getParent();
		if (p instanceof FieldDeclaration)
			return getSimpleType(((FieldDeclaration) p).getType()) + dimensions;
		if (p instanceof VariableDeclarationStatement)
			return getSimpleType(((VariableDeclarationStatement) p).getType())
					+ dimensions;
		if (p instanceof VariableDeclarationExpression)
			return getSimpleType(((VariableDeclarationExpression) p).getType())
					+ dimensions;
		throw new UnsupportedOperationException("Get type of a declaration!!!");
	}

	public static String getSimpleType(Type type) {
		if (type.isArrayType()) {
			ArrayType t = (ArrayType) type;
			String pt = getSimpleType(t.getElementType());
			for (int i = 0; i < t.getDimensions(); i++)
				pt += "[]";
			return pt;
			// return type.toString();
		} else if (type.isParameterizedType()) {
			ParameterizedType t = (ParameterizedType) type;
			return getSimpleType(t.getType());
		} else if (type.isPrimitiveType()) {
			String pt = type.toString();
			/*
			 * if (pt.equals("byte") || pt.equals("short") || pt.equals("int")
			 * || pt.equals("long") || pt.equals("float") ||
			 * pt.equals("double")) return "number";
			 */
			return pt;
		} else if (type.isQualifiedType()) {
			QualifiedType t = (QualifiedType) type;
			return t.getName().getIdentifier();
		} else if (type.isSimpleType()) {
			String pt = type.toString();
			/*
			 * if (pt.equals("Byte") || pt.equals("Short") ||
			 * pt.equals("Integer") || pt.equals("Long") || pt.equals("Float")
			 * || pt.equals("Double")) return "number";
			 */
			return pt;
		} else if (type.isUnionType()) {
			UnionType ut = (UnionType) type;
			String s = getSimpleType((Type) ut.types().get(0));
			for (int i = 1; i < ut.types().size(); i++)
				s += "|" + getSimpleType((Type) ut.types().get(i));
			return s;
		} else if (type.isWildcardType()) {
			// WildcardType t = (WildcardType) type;
			System.err
					.println("ERROR: Declare a variable with wildcard type!!!");
			//System.exit(0);
		}
		System.err.println("ERROR: Declare a variable with unknown type!!!");
	//	System.exit(0);
		return null;
	}

	public static String getSimpleType(Type type, HashSet<String> typeParameters) {
		if (type.isArrayType()) {
			ArrayType t = (ArrayType) type;
			String pt = getSimpleType(t.getElementType(), typeParameters);
			for (int i = 0; i < t.getDimensions(); i++)
				pt += "[]";
			return pt;
			// return type.toString();
		} else if (type.isParameterizedType()) {
			ParameterizedType t = (ParameterizedType) type;
			return getSimpleType(t.getType(), typeParameters);
		} else if (type.isPrimitiveType()) {
			return type.toString();
		} else if (type.isQualifiedType()) {
			QualifiedType t = (QualifiedType) type;
			return t.getName().getIdentifier();
		} else if (type.isSimpleType()) {
			if (typeParameters.contains(type.toString()))
				return "Object";
			return type.toString();
		} else if (type.isUnionType()) {
			UnionType ut = (UnionType) type;
			String s = getSimpleType((Type) ut.types().get(0), typeParameters);
			for (int i = 1; i < ut.types().size(); i++)
				s += "|"
						+ getSimpleType((Type) ut.types().get(i),
								typeParameters);
			return s;
		} else if (type.isWildcardType()) {
			// WildcardType t = (WildcardType) type;
			System.err
					.println("ERROR: Declare a variable with wildcard type!!!");
			System.exit(0);
		}
		System.err.println("ERROR: Declare a variable with unknown type!!!");
		System.exit(0);
		return null;
	}

	public static String getSimpleName(Name name) {
		if (name.isSimpleName())
			return name.toString();
		QualifiedName qn = (QualifiedName) name;
		return qn.getName().getIdentifier();
	}

	public static String getInfixOperator(Operator operator) {
		if (operator == Operator.ASSIGN)
			return null;
		String op = operator.toString();
		return op.substring(0, op.length() - 1);
	}

	public static TypeDeclaration getType(TypeDeclaration td, String name) {
		for (TypeDeclaration inner : td.getTypes())
			if (inner.getName().getIdentifier().equals(name))
				return inner;
		return null;
	}

	public static boolean isDeprecated(MethodDeclaration method) {
		Javadoc doc = method.getJavadoc();
		if (doc != null) {
			for (int i = 0; i < doc.tags().size(); i++) {
				TagElement tag = (TagElement) doc.tags().get(i);
				if (tag.getTagName() != null
						&& tag.getTagName().toLowerCase().equals("@deprecated"))
					return true;
			}
		}
		return false;
	}

	public static int countLeaves(ASTNode node) {
		class LeaveCountASTVisitor extends ASTVisitor {
			private Stack<Integer> numOfChildren = new Stack<Integer>();
			private int numOfLeaves = 0;

			public LeaveCountASTVisitor() {
				numOfChildren.push(0);
			}

			@Override
			public void preVisit(ASTNode node) {
				int n = numOfChildren.pop();
				numOfChildren.push(n + 1);
				numOfChildren.push(0);
			}

			@Override
			public void postVisit(ASTNode node) {
				int n = numOfChildren.pop();
				if (n == 0)
					numOfLeaves++;
			}
		}
		;
		LeaveCountASTVisitor v = new LeaveCountASTVisitor();
		node.accept(v);
		return v.numOfLeaves;
	}

	public static ArrayList<String> tokenizeNames(ASTNode node) {
		return new ASTVisitor() {
			private ArrayList<String> names = new ArrayList<>();

			@Override
			public boolean visit(org.eclipse.jdt.core.dom.SimpleName node) {
				names.add(node.getIdentifier());
				return false;
			};
		}.names;
	}

	public static String buildLabel(InfixExpression.Operator operator) {
		return infixExpressionLables.get(operator.toString());
	}

	public static String getAssignOperator(Operator operator) {
		return assignmentLabels.get(operator.toString());
	}

	

	public static boolean isMethodInvocation(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource(source.toCharArray());
		ASTNode ast = parser.createAST(null);
		if (!(ast instanceof MethodInvocation))
			return false;
		return isExpression(ast, source);
	}

	public static boolean isExpression(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource(source.toCharArray());
		ASTNode ast = parser.createAST(null);
		return isExpression(ast, source);
	}

	private static boolean isExpression(ASTNode ast, String source) {
		if (ast == null || !(ast instanceof Expression)
				|| ast.toString().length() < source.length() / 2)
			return false;
		ErrorCheckVisitor v = new ErrorCheckVisitor();
		ast.accept(v);
		return !v.hasError;
	}

	private static class ErrorCheckVisitor extends ASTVisitor {
		public boolean hasError = false;

		@Override
		public boolean preVisit2(ASTNode node) {
			if ((node.getFlags() & ASTNode.MALFORMED) != 0
					|| (node.getFlags() & ASTNode.RECOVERED) != 0)
				hasError = true;
			return !hasError;
		}
	}

	public static String getKey(Name name) {
		IBinding b = name.resolveBinding();
		if (b == null)
			return null;
		if (b instanceof ITypeBinding)
			return b.getKey();
		if (name.isSimpleName()) {
			return b.getKey();
		} else if (name.isQualifiedName()) {
			QualifiedName qn = (QualifiedName) name;
			return getKey(qn.getQualifier()) + "."
					+ qn.getName().getIdentifier();
		}
		return null;
	}

	public static Expression negate(Expression ex) {
		Expression e = (Expression) ASTNode.copySubtree(ex.getAST(), ex);
		if (e instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) e;
			InfixExpression.Operator op = ie.getOperator();
			if (op == InfixExpression.Operator.EQUALS)
				op = InfixExpression.Operator.NOT_EQUALS;
			else if (op == InfixExpression.Operator.NOT_EQUALS)
				op = InfixExpression.Operator.EQUALS;
			else if (op == InfixExpression.Operator.GREATER)
				op = InfixExpression.Operator.LESS_EQUALS;
			else if (op == InfixExpression.Operator.GREATER_EQUALS)
				op = InfixExpression.Operator.LESS;
			else if (op == InfixExpression.Operator.LESS)
				op = InfixExpression.Operator.GREATER_EQUALS;
			else if (op == InfixExpression.Operator.LESS_EQUALS)
				op = InfixExpression.Operator.GREATER;
			else if (op == InfixExpression.Operator.CONDITIONAL_AND) {
				op = InfixExpression.Operator.CONDITIONAL_OR;
				List<?> list = ie.structuralPropertiesForType();
				negate(list, ie);
			} else if (op == InfixExpression.Operator.CONDITIONAL_OR) {
				op = InfixExpression.Operator.CONDITIONAL_AND;
				List<?> list = ie.structuralPropertiesForType();
				negate(list, ie);
			} else {
				PrefixExpression pe = e.getAST().newPrefixExpression();
				ParenthesizedExpression paren = e.getAST()
						.newParenthesizedExpression();
				paren.setExpression(e);
				pe.setOperand(paren);
			}
			ie.setOperator(op);
			return e;
		}
		if (e instanceof PrefixExpression)
			return (Expression) ASTNode.copySubtree(e.getAST(),
					((PrefixExpression) e).getOperand());
		PrefixExpression pe = e.getAST().newPrefixExpression();
		pe.setOperator(PrefixExpression.Operator.NOT);
		if (e instanceof MethodInvocation)
			pe.setOperand(e);
		else if (e instanceof ParenthesizedExpression)
			return negate(((ParenthesizedExpression) e).getExpression());
		else {
			ParenthesizedExpression paren = e.getAST()
					.newParenthesizedExpression();
			paren.setExpression(e);
			pe.setOperand(paren);
		}
		return pe;
	}

	private static void negate(List<?> list, ASTNode node) {
		for (int i = 0; i < list.size(); i++) {
			StructuralPropertyDescriptor curr = (StructuralPropertyDescriptor) list
					.get(i);
			Object child = node.getStructuralProperty(curr);
			if (child instanceof Expression)
				node.setStructuralProperty(curr, negate((Expression) child));
			else if (child instanceof List) {
				List<Object> sub = (List<Object>) child;
				for (int j = 0; j < sub.size(); j++) {
					Object obj = sub.get(j);
					if (obj instanceof Expression)
						sub.set(j, (Object) negate((Expression) obj));
				}
				// node.setStructuralProperty(curr, sub);
			}
		}
	}

	public static Expression normalize(Expression e) {
		if (e instanceof PrefixExpression) {
			PrefixExpression pe = (PrefixExpression) e;
			if (pe.getOperator() == PrefixExpression.Operator.NOT)
				return negate(pe.getOperand());
		}
		return e;
	}

	public static boolean isInTrueBranch(ASTNode node, IfStatement p) {
		ASTNode temp = node;
		while (temp.getParent() != p)
			temp = temp.getParent();
		return temp == p.getThenStatement();
	}

	public static void replace(ASTNode oldNode, ASTNode newNode) {
		ASTNode parent = oldNode.getParent();
		StructuralPropertyDescriptor location = oldNode.getLocationInParent();
		if (location.isChildProperty()) {
			parent.setStructuralProperty(location, newNode);
		}
		if (location.isChildListProperty()) {
			List<ASTNode> list = ((List<ASTNode>) parent
					.getStructuralProperty(location));
			int index = list.indexOf(oldNode);
			list.set(index, newNode);
		}
	}

}
