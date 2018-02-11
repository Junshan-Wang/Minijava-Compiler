package minijava.visitor;

import java.util.*;

import minijava.SymbolTable.TypeBasic;
import minijava.SymbolTable.TypeClass;
import minijava.SymbolTable.TypeMethod;
import minijava.SymbolTable.TypeVariable;
import minijava.syntaxtree.*;
import minijava.syntaxtree.Goal;
import minijava.syntaxtree.IntegerLiteral;
import minijava.syntaxtree.Node;
import minijava.syntaxtree.NodeList;
import minijava.syntaxtree.NodeListOptional;
import minijava.syntaxtree.NodeOptional;
import minijava.syntaxtree.NodeSequence;
import minijava.syntaxtree.NodeToken;
import minijava.visitor.GJDepthFirst;
public class SymbolTableVisitor extends GJDepthFirst<TypeBasic,TypeBasic>{

public HashMap<String,TypeClass> classes=new HashMap<String,TypeClass>();
public Vector<String> error;
public SymbolTableVisitor(){
	error=new Vector<String>();
}

public TypeBasic visit(NodeList n, TypeBasic argu) {
   TypeBasic _ret=null;
   int _count=0;
   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this,argu);
      _count++;
   }
   return _ret;
}

public TypeBasic visit(NodeListOptional n, TypeBasic argu) {
   if ( n.present() ) {
      TypeBasic _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }
   else
      return null;
}

public TypeBasic visit(NodeOptional n, TypeBasic argu) {
   if ( n.present() )
      return n.node.accept(this,argu);
   else
      return null;
}

public TypeBasic visit(NodeSequence n, TypeBasic argu) {
   TypeBasic _ret=null;
   int _count=0;
   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this,argu);
      _count++;
   }
   return _ret;
}

public TypeBasic visit(NodeToken n, TypeBasic argu) { return null; }

//
// User-generated visitoTypeBasic methods below
//

/**
 * f0 -> MainClass()
 * f1 -> ( TypeDeclaration() )*
 * f2 -> <EOF>
 */
public TypeBasic visit(Goal n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);

   //判断是否有未初始化的类
   for (TypeClass ct:classes.values()){
	   if (!ct.isinited){
		   error.add("Line "+n.f0.f0.beginLine+" Column "+n.f0.f0.beginColumn+": class not initialized");
		   return null;
	   }
   }
   
   //判断是否有循环继承
   for (TypeClass tc : classes.values()){
	   int i=0;
	   int num=classes.size()-1;
	   TypeClass parent=tc.parent;
	   while(parent!=null && i<num){
		   if (tc==parent){
			   error.add("CIRCULATION");
			   return null;
		   }
		   parent=parent.parent;
		   i++;
	   }
   }
   
   //判断是否有方法重载
   for (TypeClass tc:classes.values()){
	   TypeClass parent=tc.parent;
	   while(parent!=null){
		   for (TypeMethod tm:tc.methods.values()){
			   TypeMethod ptm=parent.methods.get(tm.name);
			   if (ptm!=null){
				   Iterator<TypeVariable> itr=ptm.pars.values().iterator();
				   for (TypeVariable t:tm.pars.values()){
					   if (!itr.hasNext() || t.type!=itr.next().type){
						   error.add("Overload");
						   return null;
					   }				
				   }
				   if (itr.hasNext()){
					   error.add("Overload");
					   return null;
				   }
				   if (tm.ret.type!=ptm.ret.type){
					   error.add("Overload");
					   return null;
				   }
			   }
		   }
		   parent=parent.parent;
	   }
   }
   
   return _ret;
}

/**
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> "public"
 * f4 -> "static"
 * f5 -> "void"
 * f6 -> "main"
 * f7 -> "("
 * f8 -> "String"
 * f9 -> "["
 * f10 -> "]"
 * f11 -> Identifier()
 * f12 -> ")"
 * f13 -> "{"
 * f14 -> ( VarDeclaration() )*
 * f15 -> ( Statement() )*
 * f16 -> "}"
 * f17 -> "}"
 */
public TypeBasic visit(MainClass n, TypeBasic argu) {
   
   String name=n.f1.f0.toString();
   TypeClass tc=classes.get(name);
   if (tc==null){
	   tc=new TypeClass(name);
	   classes.put(name, tc);
   }
   else if (!tc.isinited){
	   tc.isinited=true;
   }
   else{
	   error.add("Line "+n.f0.beginLine+" Column "+n.f1.f0.beginColumn+": Initialize the same class more than onece");
	   return null;
   }

   TypeMethod tm=tc.addMethod("main", null);
   tm.parent=tc;
   TypeVariable tv=tm.addPar(n.f11.f0.toString(), "string");
   if (tv==null) {
	   error.add("expect paramas");
	   return null;
   }
   
   n.f14.accept(this, tm);

   return null;
}

/**
 * f0 -> ClassDeclaration()
 *       | ClassExtendsDeclaration()
 */
public TypeBasic visit(TypeDeclaration n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> ( VarDeclaration() )*
 * f4 -> ( MethodDeclaration() )*
 * f5 -> "}"
 */
public TypeBasic visit(ClassDeclaration n, TypeBasic argu) {
	String name=n.f1.f0.toString();
	TypeClass tc=classes.get(name);
	if (tc==null){
		tc=new TypeClass(name);
		classes.put(name, tc);
	}
	else if (!tc.isinited){
		tc.isinited=true;
	}
	else{
		error.add("Line "+n.f0.beginLine+" Column "+n.f1.f0.beginColumn+": Initialize the same class more than once");
		return null;
	}

    n.f3.accept(this, tc);
    n.f4.accept(this, tc);
    return null;
}

/**
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "extends"
 * f3 -> Identifier()
 * f4 -> "{"
 * f5 -> ( VarDeclaration() )*
 * f6 -> ( MethodDeclaration() )*
 * f7 -> "}"
 */
public TypeBasic visit(ClassExtendsDeclaration n, TypeBasic argu) {
	String name=n.f1.f0.toString();
	TypeClass tc=classes.get(name);
	if (tc==null){
		tc=new TypeClass(name);
		classes.put(name, tc);
	}
	else if (!tc.isinited){
		tc.isinited=true;
	}
	else{
		error.add("Line "+n.f0.beginLine+" Column "+n.f1.f0.beginColumn+": Initialize the same class more than onece");
		return null;
	}
	
	String parentname=n.f3.f0.toString();
	TypeClass parent=classes.get(parentname);
	if (parent == null){
		parent=new TypeClass(parentname);
		parent.isinited=false;
		classes.put(parentname, parent);
	}
	tc.parent=parent;
	
	n.f5.accept(this,tc);
	n.f6.accept(this,tc);
	
	return null;
}

/**
 * f0 -> Type()
 * f1 -> Identifier()
 * f2 -> ";"
 */
public TypeBasic visit(VarDeclaration n, TypeBasic argu) {
	if (argu instanceof TypeMethod){
		if (((TypeMethod)argu).addVar(n.f1.f0.toString(), n.f0)==null){
			error.add("Line "+n.f1.f0.beginLine+" Column "+n.f1.f0.beginColumn+": Initialize variable more than onec");
			return null;
		}
	}
	else if (argu instanceof TypeClass){ 
		if (((TypeClass)argu).addVar(n.f1.f0.toString(), n.f0)==null){
			error.add("Line "+n.f1.f0.beginLine+" Column "+n.f1.f0.beginColumn+": Initialize variable more than onec");
			return null;
		}
	}
	return null;
}

/**
 * f0 -> "public"
 * f1 -> Type()
 * f2 -> Identifier()
 * f3 -> "("
 * f4 -> ( FormalParameterList() )?
 * f5 -> ")"
 * f6 -> "{"
 * f7 -> ( VarDeclaration() )*
 * f8 -> ( Statement() )*
 * f9 -> "return"
 * f10 -> Expression()
 * f11 -> ";"
 * f12 -> "}"
 */
public TypeBasic visit(MethodDeclaration n, TypeBasic argu) {
	TypeVariable ret=new TypeVariable(null, n.f1);
	TypeMethod tm=((TypeClass)argu).addMethod(n.f2.f0.toString(), ret);
	if (tm==null){
		error.add("Line "+n.f2.f0.beginLine+" Column "+n.f2.f0.beginColumn+": Initialize the same function more than once");
		return null;
	}
	
	n.f4.accept(this, tm);
	n.f7.accept(this, tm);
   
	return null;
}

/**
 * f0 -> FormalParameter()
 * f1 -> ( FormalParameterRest() )*
 */
public TypeBasic visit(FormalParameterList n, TypeBasic argu) {
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return null;
}

/**
 * f0 -> Type()
 * f1 -> Identifier()
 */
public TypeBasic visit(FormalParameter n, TypeBasic argu) {
   if (((TypeMethod)argu).addPar(n.f1.f0.toString(), n.f0)==null)
	   error.add("Line "+n.f1.f0.beginLine+" Column "+n.f1.f0.beginColumn+": Initialize param more than one time");
   return null;
}

/**
 * f0 -> ","
 * f1 -> FormalParameter()
 */
public TypeBasic visit(FormalParameterRest n, TypeBasic argu) {
   n.f1.accept(this, argu);
   return null;
}

/**
 * f0 -> ArrayType()
 *       | BooleanType()
 *       | IntegerType()
 *       | Identifier()
 */
public TypeBasic visit(Type n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "int"
 * f1 -> "["
 * f2 -> "]"
 */
public TypeBasic visit(ArrayType n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "boolean"
 */
public TypeBasic visit(BooleanType n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "int"
 */
public TypeBasic visit(IntegerType n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> Block()
 *       | AssignmentStatement()
 *       | ArrayAssignmentStatement()
 *       | IfStatement()
 *       | WhileStatement()
 *       | PrintStatement()
 */
public TypeBasic visit(Statement n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "{"
 * f1 -> ( Statement() )*
 * f2 -> "}"
 */
public TypeBasic visit(Block n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

/**
 * f0 -> Identifier()
 * f1 -> "="
 * f2 -> Expression()
 * f3 -> ";"
 */
public TypeBasic visit(AssignmentStatement n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   return _ret;
}

/**
 * f0 -> Identifier()
 * f1 -> "["
 * f2 -> Expression()
 * f3 -> "]"
 * f4 -> "="
 * f5 -> Expression()
 * f6 -> ";"
 */
public TypeBasic visit(ArrayAssignmentStatement n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   n.f4.accept(this, argu);
   n.f5.accept(this, argu);
   n.f6.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "if"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> Statement()
 * f5 -> "else"
 * f6 -> Statement()
 */
public TypeBasic visit(IfStatement n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   n.f4.accept(this, argu);
   n.f5.accept(this, argu);
   n.f6.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "while"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> Statement()
 */
public TypeBasic visit(WhileStatement n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   n.f4.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "System.out.println"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> ";"
 */
public TypeBasic visit(PrintStatement n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   n.f4.accept(this, argu);
   return _ret;
}

/**
 * f0 -> AndExpression()
 *       | CompareExpression()
 *       | PlusExpression()
 *       | MinusExpression()
 *       | TimesExpression()
 *       | ArrayLookup()
 *       | ArrayLength()
 *       | MessageSend()
 *       | PrimaryExpression()
 */
public TypeBasic visit(Expression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "&&"
 * f2 -> PrimaryExpression()
 */
public TypeBasic visit(AndExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "<"
 * f2 -> PrimaryExpression()
 */
public TypeBasic visit(CompareExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "+"
 * f2 -> PrimaryExpression()
 */
public TypeBasic visit(PlusExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "-"
 * f2 -> PrimaryExpression()
 */
public TypeBasic visit(MinusExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "*"
 * f2 -> PrimaryExpression()
 */
public TypeBasic visit(TimesExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "["
 * f2 -> PrimaryExpression()
 * f3 -> "]"
 */
public TypeBasic visit(ArrayLookup n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   return _ret;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "."
 * f2 -> "length"
 */
public TypeBasic visit(ArrayLength n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "."
 * f2 -> Identifier()
 * f3 -> "("
 * f4 -> ( ExpressionList() )?
 * f5 -> ")"
 */
public TypeBasic visit(MessageSend n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   n.f4.accept(this, argu);
   n.f5.accept(this, argu);
   return _ret;
}

/**
 * f0 -> Expression()
 * f1 -> ( ExpressionRest() )*
 */
public TypeBasic visit(ExpressionList n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return _ret;
}

/**
 * f0 -> ","
 * f1 -> Expression()
 */
public TypeBasic visit(ExpressionRest n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return _ret;
}

/**
 * f0 -> IntegerLiteral()
 *       | TrueLiteral()
 *       | FalseLiteral()
 *       | Identifier()
 *       | ThisExpression()
 *       | ArrayAllocationExpression()
 *       | AllocationExpression()
 *       | NotExpression()
 *       | BracketExpression()
 */
public TypeBasic visit(PrimaryExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> <INTEGER_LITERAL>
 */
public TypeBasic visit(IntegerLiteral n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "true"
 */
public TypeBasic visit(TrueLiteral n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "false"
 */
public TypeBasic visit(FalseLiteral n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> <IDENTIFIER>
 */
public TypeBasic visit(Identifier n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "this"
 */
public TypeBasic visit(ThisExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "new"
 * f1 -> "int"
 * f2 -> "["
 * f3 -> Expression()
 * f4 -> "]"
 */
public TypeBasic visit(ArrayAllocationExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   n.f4.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "new"
 * f1 -> Identifier()
 * f2 -> "("
 * f3 -> ")"
 */
public TypeBasic visit(AllocationExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "!"
 * f1 -> Expression()
 */
public TypeBasic visit(NotExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return _ret;
}

/**
 * f0 -> "("
 * f1 -> Expression()
 * f2 -> ")"
 */
public TypeBasic visit(BracketExpression n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
}

}

