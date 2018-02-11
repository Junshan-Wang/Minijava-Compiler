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

public class TypeCheckVisitor extends GJDepthFirst<TypeVariable,TypeBasic>{

public SymbolTableVisitor symbolTable;
public boolean flag=false;
public Vector<String> error;
public TypeCheckVisitor(SymbolTableVisitor stv){
	symbolTable=stv;
	error=new Vector<String>();
}
	
	
public TypeVariable visit(NodeList n, TypeBasic argu) {
   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      if (e.nextElement().accept(this,argu)==null)
    	  return null;
   }
   return TypeVariable.OTHER;
}

public TypeVariable visit(NodeListOptional n, TypeBasic argu) {
   if ( n.present() ) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         if (e.nextElement().accept(this,argu)==null)
        	 return null;
      }
   }
      return TypeVariable.OTHER;
}

public TypeVariable visit(NodeOptional n, TypeBasic argu) {
   if ( n.present() )
	   if (n.node.accept(this,argu)==null)
		   return null;
   
   return TypeVariable.OTHER;
}

public TypeVariable visit(NodeSequence n, TypeBasic argu) {
   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      if (e.nextElement().accept(this,argu)==null)
    	  return null;
   }
   return TypeVariable.OTHER;
}

public TypeVariable visit(NodeToken n, TypeBasic argu) { return null; }

//
// User-generated visitor methods below
//

/**
 * f0 -> MainClass()
 * f1 -> ( TypeDeclaration() )*
 * f2 -> <EOF>
 */
public TypeVariable visit(Goal n, TypeBasic argu) {
	if (n.f0.accept(this, argu)==null) return null;
	return n.f1.accept(this,argu);
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
public TypeVariable visit(MainClass n, TypeBasic argu) {
	TypeClass tc=symbolTable.classes.get(n.f1.f0.toString());
	TypeMethod tm=tc.getMethod("main");
	tm.parent=tc;
	return n.f15.accept(this, tm);
}

/**
 * f0 -> ClassDeclaration()
 *       | ClassExtendsDeclaration()
 */
public TypeVariable visit(TypeDeclaration n, TypeBasic argu) {
	return n.f0.accept(this, argu);
}

/**
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> ( VarDeclaration() )*
 * f4 -> ( MethodDeclaration() )*
 * f5 -> "}"
 */
public TypeVariable visit(ClassDeclaration n, TypeBasic argu) {  
	TypeClass tc=symbolTable.classes.get(n.f1.f0.toString());
    return n.f4.accept(this, tc);
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
public TypeVariable visit(ClassExtendsDeclaration n, TypeBasic argu) {
	TypeClass tc=symbolTable.classes.get(n.f1.f0.toString());
    return n.f6.accept(this, tc);
}

/**
 * f0 -> Type()
 * f1 -> Identifier()
 * f2 -> ";"
 */
public TypeVariable visit(VarDeclaration n, TypeBasic argu) {
	TypeVariable _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return _ret;
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
public TypeVariable visit(MethodDeclaration n, TypeBasic argu) {
   TypeMethod tm=((TypeClass)argu).getMethod(n.f2.f0.toString());
   tm.parent=(TypeClass)argu;
   
   if (n.f8.accept(this, tm)==null) return null;
   if (n.f10.accept(this, tm)==null) return null;

   
   TypeVariable ex=tm.ret;
   TypeVariable id=n.f10.accept(this,tm);
   
   if (id==null || ex==null) {
	   error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": return value doesn't match");
	   return null;
   }
   else if (id.type==ex.type){
		return TypeVariable.OTHER;
   }
   else {
	   if (symbolTable.classes.get(id.type)==null) {
		   error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": return value doesn't match");
		   return null;
	   }
	   TypeClass parent=symbolTable.classes.get(id.type).parent;
	   while(parent!=null){
		   if (parent.name==ex.type) return TypeVariable.OTHER;
		   else parent=parent.parent;
	   }
   		error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": return value doesn't match");
   		return null;
   }
}

/**
 * f0 -> FormalParameter()
 * f1 -> ( FormalParameterRest() )*
 */
public TypeVariable visit(FormalParameterList n, TypeBasic argu) {
	TypeVariable _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return _ret;
}

/**
 * f0 -> Type()
 * f1 -> Identifier()
 */
public TypeVariable visit(FormalParameter n, TypeBasic argu) {
	TypeVariable _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return _ret;
}

/**
 * f0 -> ","
 * f1 -> FormalParameter()
 */
public TypeVariable visit(FormalParameterRest n, TypeBasic argu) {
	TypeVariable _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return _ret;
}

/**
 * f0 -> ArrayType()
 *       | BooleanType()
 *       | IntegerType()
 *       | Identifier()
 */
public TypeVariable visit(Type n, TypeBasic argu) {
	flag=true;
	TypeVariable ret=n.f0.accept(this, argu);
	flag=false;
	return ret;
}

/**
 * f0 -> "int"
 * f1 -> "["
 * f2 -> "]"
 */
public TypeVariable visit(ArrayType n, TypeBasic argu) {
   return TypeVariable.ARRAY;
}

/**
 * f0 -> "boolean"
 */
public TypeVariable visit(BooleanType n, TypeBasic argu) {
   return TypeVariable.BOOLEAN;
}

/**
 * f0 -> "int"
 */
public TypeVariable visit(IntegerType n, TypeBasic argu) {
   return TypeVariable.INTEGER;
}

/**
 * f0 -> Block()
 *       | AssignmentStatement()
 *       | ArrayAssignmentStatement()
 *       | IfStatement()
 *       | WhileStatement()
 *       | PrintStatement()
 */
public TypeVariable visit(Statement n, TypeBasic argu) {
   return n.f0.accept(this, argu);
}

/**
 * f0 -> "{"
 * f1 -> ( Statement() )*
 * f2 -> "}"
 */
public TypeVariable visit(Block n, TypeBasic argu) {
   return n.f1.accept(this, argu);
}

/**
 * f0 -> Identifier()
 * f1 -> "="
 * f2 -> Expression()
 * f3 -> ";"
 */
public TypeVariable visit(AssignmentStatement n, TypeBasic argu) {
   TypeVariable ex=n.f0.accept(this, argu);
   TypeVariable id=n.f2.accept(this, argu);
   n.f3.accept(this, argu);
   
   if (id==null || ex==null) { //某一边是null
	   //error.add("Line "+n.f0.f0.beginLine+" Column "+n.f0.f0.beginColumn+": assignment doesn't match");
	   return null;
   }
   else if (id.type==ex.type){ //如果匹配直接返回
		return TypeVariable.OTHER;
   }
   else { //否则判断是否赋值者是否是被赋值者的子类
	   if (symbolTable.classes.get(id.type)==null) {
		   error.add("Line "+n.f0.f0.beginLine+" Column "+n.f0.f0.beginColumn+": assignment doesn't match");
		   return null;
	   }
	   TypeClass parent=symbolTable.classes.get(id.type).parent;
	   while(parent!=null){
		   if (parent.name==ex.type) return TypeVariable.OTHER;
		   else parent=parent.parent;
	   }
   		error.add("Line "+n.f0.f0.beginLine+" Column "+n.f0.f0.beginColumn+": assignment doesn't match");
   		return null;
   }
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
public TypeVariable visit(ArrayAssignmentStatement n, TypeBasic argu) {
	TypeVariable id = n.f0.accept(this,argu);
	TypeVariable index = n.f2.accept(this,argu);
	TypeVariable exp = n.f5.accept(this,argu);

	if (id.type == "array" && index.type == "integer" && exp.type == "integer")
		return TypeVariable.OTHER;

	error.add("Line "+n.f0.f0.beginLine+" Column "+n.f0.f0.beginColumn+": array assignment doesn't match");
	return null;
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
public TypeVariable visit(IfStatement n, TypeBasic argu) {
	TypeVariable exp=n.f2.accept(this, argu);
	TypeVariable stat1=n.f4.accept(this, argu);
	TypeVariable stat2=n.f6.accept(this, argu);
	
	if (exp.type == "boolean" && stat1 != null && stat2 != null)
		return TypeVariable.OTHER;

	if(exp.type!="boolean") error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": if statement doesn't match");
	return null;
}

/**
 * f0 -> "while"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> Statement()
 */
public TypeVariable visit(WhileStatement n, TypeBasic argu) {
	TypeVariable  exp=n.f2.accept(this, argu);
	TypeVariable stat=n.f4.accept(this, argu);
	if (exp.type=="boolean" && stat!=null) return TypeVariable.OTHER;
	
	error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": while statement doesn't match");
	return null;
}

/**
 * f0 -> "System.out.println"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> ";"
 */
public TypeVariable visit(PrintStatement n, TypeBasic argu) {
	if (n.f2.accept(this,argu)==null)
		return null;
	else if (n.f2.accept(this, argu).type == "integer")
	   return TypeVariable.OTHER;

   error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": Print error");
   return null;
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
public TypeVariable visit(Expression n, TypeBasic argu) {
	return n.f0.accept(this, argu);
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "&&"
 * f2 -> PrimaryExpression()
 */
public TypeVariable visit(AndExpression n, TypeBasic argu) {
   if (n.f0.accept(this, argu).type=="boolean" && n.f2.accept(this, argu).type=="boolean")
	   return TypeVariable.BOOLEAN;
   
   error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": And error");
   return null;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "<"
 * f2 -> PrimaryExpression()
 */
public TypeVariable visit(CompareExpression n, TypeBasic argu) {
	if (n.f0.accept(this, argu).type=="integer" && n.f2.accept(this, argu).type=="integer")
		return TypeVariable.BOOLEAN;
	
	error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": Compare error");
	return null;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "+"
 * f2 -> PrimaryExpression()
 */
public TypeVariable visit(PlusExpression n, TypeBasic argu) {
	if (n.f0.accept(this, argu).type=="integer" && n.f2.accept(this, argu).type=="integer")
		return TypeVariable.INTEGER;
	
	error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": Plus error");
	return null;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "-"
 * f2 -> PrimaryExpression()
 */
public TypeVariable visit(MinusExpression n, TypeBasic argu) {
	if (n.f0.accept(this, argu).type=="integer" && n.f2.accept(this, argu).type=="integer")
		return TypeVariable.INTEGER;
	
	error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": Minus error");
	return null;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "*"
 * f2 -> PrimaryExpression()
 */
public TypeVariable visit(TimesExpression n, TypeBasic argu) {
	if (n.f0.accept(this, argu).type=="integer" && n.f2.accept(this, argu).type=="integer")
		return TypeVariable.INTEGER;
	
	error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": Times error");
	return null;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "["
 * f2 -> PrimaryExpression()
 * f3 -> "]"
 */
public TypeVariable visit(ArrayLookup n, TypeBasic argu) {
	if (n.f0.accept(this, argu).type=="array" && n.f2.accept(this, argu).type=="integer")
		return TypeVariable.INTEGER;
	
	error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": Array error");
	return null;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "."
 * f2 -> "length"
 */
public TypeVariable visit(ArrayLength n, TypeBasic argu) {
   if (n.f0.accept(this, argu).type=="array")
	   return TypeVariable.INTEGER;
   
   error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": Array length error");
   return null;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "."
 * f2 -> Identifier()
 * f3 -> "("
 * f4 -> ( ExpressionList() )?
 * f5 -> ")"
 */
public TypeVariable visit(MessageSend n, TypeBasic argu) {
	//判断是否有f0的类
	TypeVariable pri=n.f0.accept(this, argu);	
	if (pri==null || pri.type=="integer" || pri.type=="array" || pri.type=="boolean"){
		error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the class");
		return null;
	}	
	TypeClass tc=symbolTable.classes.get(pri.type);
	if (tc==null){
		error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the class");
		return null;
	}
	
	//判断f0类是否有f2方法
	TypeMethod tm=(tc).getMethod(n.f2.f0.toString());
	if (tm==null){
		error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the method");
		return null;
	}
		
	Collection<TypeVariable> pars=tm.pars.values();	
	if (n.f4.present()){
		if (pars==null || pars.isEmpty()){
			error.add("Line "+n.f3.beginLine+" Column "+n.f3.beginColumn+": can't find the method");
			return null;
		}

		LinkedList<TypeVariable> queue=new LinkedList<TypeVariable>(pars);
		ExpressionList list=(ExpressionList)n.f4.node;
		
		if (list.f0.accept(this,argu) == null) return null;

		TypeVariable id=list.f0.accept(this,argu);
		TypeVariable ex=queue.remove();

		if (id.type!=ex.type){		
			if (symbolTable.classes.get(id.type)==null) {
				error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the method");
				return null;
			}
			TypeClass parent=symbolTable.classes.get(id.type).parent;
			while(parent!=null){
				if (parent.name==ex.type) break;
				else parent=parent.parent;
			}
		    if (parent==null) {
		    	error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the method");
		    	return null;
		    }
		 }
		
		for (Node node : list.f1.nodes) {
			if (node.accept(this,argu) == null)
				return null;
	
			if (queue.isEmpty()){
				error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the method");
				return null;
			}
				
			id=node.accept(this,argu);
			ex=queue.remove();
			if (id.type!=ex.type){		
				if (symbolTable.classes.get(id.type)==null) return null;
				TypeClass parent=symbolTable.classes.get(id.type).parent;
				while(parent!=null){
					if (parent.name==ex.type) break;
					else parent=parent.parent;
				}
				if (parent==null) {
				    error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the method");
				    return null;
				 }
			}
		}	

		if ( !queue.isEmpty() ) {
			error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the method");
			return null;
		}

	} else if ( !pars.isEmpty() ) {
		error.add("Line "+n.f1.beginLine+" Column "+n.f1.beginColumn+": can't find the method");
		return null;	
	}
	
	return tm.ret;
}

/**
 * f0 -> Expression()
 * f1 -> ( ExpressionRest() )*
 */
public TypeVariable visit(ExpressionList n, TypeBasic argu) {
   if (n.f0.accept(this, argu)==null) return null;
   return n.f1.accept(this, argu);
}

/**
 * f0 -> ","
 * f1 -> Expression()
 */
public TypeVariable visit(ExpressionRest n, TypeBasic argu) {
   return n.f1.accept(this, argu);
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
public TypeVariable visit(PrimaryExpression n, TypeBasic argu) {
   return n.f0.accept(this, argu);

}

/**
 * f0 -> <INTEGER_LITERAL>
 */
public TypeVariable visit(IntegerLiteral n, TypeBasic argu) {
   return TypeVariable.INTEGER;
}

/**
 * f0 -> "true"
 */
public TypeVariable visit(TrueLiteral n, TypeBasic argu) {
   return TypeVariable.BOOLEAN;
}

/**
 * f0 -> "false"
 */
public TypeVariable visit(FalseLiteral n, TypeBasic argu) {
   return TypeVariable.BOOLEAN;
}

/**
 * f0 -> <IDENTIFIER>
 */
public TypeVariable visit(Identifier n, TypeBasic argu) {
	String name=n.f0.toString();

	if (flag==true){ //开辟新空间时需要
		TypeClass tc=symbolTable.classes.get(name);
		if (tc==null){
			error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": can't find the class");
			return null;
		}
		return new TypeVariable(null,name);
	}
	else { //判断identifier是否存在
		if (argu instanceof TypeMethod){
			TypeVariable id=((TypeMethod)argu).vars.get(name);
			if (id!=null)
				return id;
			
			id=((TypeMethod)argu).pars.get(name);
			if (id!=null) return id;
			
			TypeClass tc=((TypeMethod)argu).parent;
			while(tc!=null){
				id=tc.vars.get(name);
				if (id!=null) return id;
				tc=tc.parent;				
			}
		}
		else if (argu instanceof TypeClass){
			TypeVariable id=((TypeClass)argu).vars.get(name);
			if (id!=null) return id;
			
			TypeClass tc=((TypeClass)argu).parent;
			while(tc!=null){
				id=tc.vars.get(name);
				if (id!=null) return id;
				tc=tc.parent;				
			}
		}
		else {
			error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": Variable doesn't exit");
			return null;
		}
	}
	error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": Variable doesn't exit");
	return null;
}

/**
 * f0 -> "this"
 */
public TypeVariable visit(ThisExpression n, TypeBasic argu) {
	if (argu instanceof TypeClass)
		return new TypeVariable(null,((TypeClass)argu).name);
	else
		return new TypeVariable(null,((TypeMethod)argu).parent.name);
}

/**
 * f0 -> "new"
 * f1 -> "int"
 * f2 -> "["
 * f3 -> Expression()
 * f4 -> "]"
 */
public TypeVariable visit(ArrayAllocationExpression n, TypeBasic argu) {
   if (n.f3.accept(this, argu).type!="integer"){
	   error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": array allocation doesn't match");
	   return null;
   }
   return TypeVariable.ARRAY;
}

/**
 * f0 -> "new"
 * f1 -> Identifier()
 * f2 -> "("
 * f3 -> ")"
 */
public TypeVariable visit(AllocationExpression n, TypeBasic argu) {
   
   flag=true;
   TypeVariable id=n.f1.accept(this, argu);
   if (id==null || id.type=="integer" || id.type=="array" || id.type=="boolean") {
	   error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": can't allocattion");
	   return null;
   }
   flag=false;
   return id;
}

/**
 * f0 -> "!"
 * f1 -> Expression()
 */
public TypeVariable visit(NotExpression n, TypeBasic argu) {
   if (n.f1.accept(this, argu).type=="boolean")
	   return TypeVariable.BOOLEAN;
   
   error.add("Line "+n.f0.beginLine+" Column "+n.f0.beginColumn+": Expression should be boolean type");
   return null;
}

/**
 * f0 -> "("
 * f1 -> Expression()
 * f2 -> ")"
 */
public TypeVariable visit(BracketExpression n, TypeBasic argu) {
   return n.f1.accept(this, argu);
}

}
