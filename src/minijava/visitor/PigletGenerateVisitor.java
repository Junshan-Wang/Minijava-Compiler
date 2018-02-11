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
import minijava.visitor.GJVisitor;

/**
* Provides default methods which visit each node in the tree in depth-first
* order.  Your visitors may extend this class.
*/
public class PigletGenerateVisitor extends GJDepthFirst<String,TypeBasic> {

	private SymbolTableVisitor symbolTable;		//符号表
	private TypeClass currentClass;				//保存所在的类
	private TypeMethod currentMethod;			//保存当前所在的函数
	private boolean typeIdentifier=false;		//识别标识符判断需要进行的处理
	private String aClass=null;					//暂时保存类名
	private String update=null;					//是否需要更新内存
	
	private int indentation=0;					//缩进
	private int regCount=0;						//当前寄存器
	private int labelCount=0;					//当前的label
	
	public PigletGenerateVisitor(SymbolTableVisitor stv){
		symbolTable=stv;
	}

	private String regName(){
		regCount++;
		return "TEMP "+regCount;
	}
	private String label(){
		labelCount++;
		return "L"+labelCount;
	}
	
	
	private void printx(String str){
		for (int i=0;i<indentation;++i){
			str="  "+str;
		}		
		System.out.println(str);
	}
	
		
//
// Auto class visitors--probably don't need to be overridden.
// 
public String visit(NodeList n, TypeBasic argu) {
   TypeBasic _ret=null;
   int _count=0;
   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this,argu);
      _count++;
   }
   return null;
}

public String visit(NodeListOptional n, TypeBasic argu) {
   if ( n.present() ) {
      TypeBasic _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return null;
   }
   else
      return null;
}

public String visit(NodeOptional n, TypeBasic argu) {
   if ( n.present() )
      return n.node.accept(this,argu);
   else
      return null;
}

public String visit(NodeSequence n, TypeBasic argu) {
   TypeBasic _ret=null;
   int _count=0;
   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this,argu);
      _count++;
   }
   return null;
}

public String visit(NodeToken n, TypeBasic argu) { return null; }

//
// User-generated visitor methods below
//

/**
 * f0 -> MainClass()
 * f1 -> ( TypeDeclaration() )*
 * f2 -> <EOF>
 */
public String visit(Goal n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return null;
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
public String visit(MainClass n, TypeBasic argu) {
   printx("MAIN");
   indentation++;
   
   regCount=0;
   n.f15.accept(this,argu);
   
   indentation--;
   printx("END");
   
   return null;
}

/**
 * f0 -> ClassDeclaration()
 *       | ClassExtendsDeclaration()
 */
public String visit(TypeDeclaration n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return null;
}

/**
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> ( VarDeclaration() )*
 * f4 -> ( MethodDeclaration() )*
 * f5 -> "}"
 */
public String visit(ClassDeclaration n, TypeBasic argu) {	
	
	currentClass=symbolTable.classes.get(n.f1.f0.toString());
    n.f4.accept(this, argu);
    currentClass=null;
    
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
public String visit(ClassExtendsDeclaration n, TypeBasic argu) {
	
	currentClass=symbolTable.classes.get(n.f1.f0.toString());
    n.f6.accept(this, argu);
    currentClass=null;
    
    return null;
}

/**
 * f0 -> Type()
 * f1 -> Identifier()
 * f2 -> ";"
 */
public String visit(VarDeclaration n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
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
public String visit(MethodDeclaration n, TypeBasic argu) {
	//在Piglet中函数名为类名+_+函数名，[]保存参数个数
	//进入新的函数寄存器计数清零，然后依次保存当前对象的地址、参数、本地变量
	
	TypeMethod tm=currentClass.methods.get(n.f2.f0.toString());
	
	currentMethod=tm;
	
	printx("");
	printx(currentClass.name+"_"+currentMethod.name+"["+(currentMethod.pars.size()+1)+"]");
	printx("BEGIN");	
	indentation++;
	
	regCount=tm.pars.size()+tm.vars.size();
	
	n.f8.accept(this,argu);
	
	String ret=n.f10.accept(this,argu);
	
	printx("RETURN "+ret);
	indentation--;
	printx("END");
	
	currentMethod=null;
	
	return null;
}

/**
 * f0 -> FormalParameter()
 * f1 -> ( FormalParameterRest() )*
 */
public String visit(FormalParameterList n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return null;
}

/**
 * f0 -> Type()
 * f1 -> Identifier()
 */
public String visit(FormalParameter n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return null;
}

/**
 * f0 -> ","
 * f1 -> FormalParameter()
 */
public String visit(FormalParameterRest n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return null;
}

/**
 * f0 -> ArrayType()
 *       | BooleanType()
 *       | IntegerType()
 *       | Identifier()
 */
public String visit(Type n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return null;
}

/**
 * f0 -> "int"
 * f1 -> "["
 * f2 -> "]"
 */
public String visit(ArrayType n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   n.f2.accept(this, argu);
   return null;
}

/**
 * f0 -> "boolean"
 */
public String visit(BooleanType n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return null;
}

/**
 * f0 -> "int"
 */
public String visit(IntegerType n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   return null;
}

/**
 * f0 -> Block()
 *       | AssignmentStatement()
 *       | ArrayAssignmentStatement()
 *       | IfStatement()
 *       | WhileStatement()
 *       | PrintStatement()
 */
public String visit(Statement n, TypeBasic argu) {  
   return n.f0.accept(this, argu);
}

/**
 * f0 -> "{"
 * f1 -> ( Statement() )*
 * f2 -> "}"
 */
public String visit(Block n, TypeBasic argu) {
   return n.f1.accept(this, argu);
}

/**
 * f0 -> Identifier()
 * f1 -> "="
 * f2 -> Expression()
 * f3 -> ";"
 */
public String visit(AssignmentStatement n, TypeBasic argu) {
	//等号赋值，分别读取左右两边表达式结果存储的寄存器，在用MOVE赋值
	//如果被复制的变量存在内存中，则要更新内存的值，更新的Piglet代码存在update中
	
	update=null;
	
	String id=n.f0.accept(this,argu);
	if (update!=null){
		String ex=n.f2.accept(this,argu);
		printx("MOVE "+id+" "+ex);
		printx(update);
	}
	else{
		String ex=n.f2.accept(this,argu);
		printx("MOVE "+id+" "+ex);
		//printx(update);
	}		
	
	update=null;

	return null;
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
public String visit(ArrayAssignmentStatement n, TypeBasic argu) {
	//数组赋值，根据数组的基地址和索引得到元素的地址，再用HSTORE存储
	
	String id=n.f0.accept(this,argu);
	String index=n.f2.accept(this,argu);
	
	String offset=regName();
	String bytes=regName();
	
	String len=regName();
	String outofbound=label();
	String endArray=label();
	printx("HLOAD "+len+" "+id+" 0");
	printx("CJUMP LT "+index+" "+len+" "+outofbound);
	
	printx("MOVE "+offset+" PLUS "+index+" 1");
	printx("MOVE "+offset+" TIMES "+offset+" 4");
	printx("MOVE "+bytes+" PLUS "+id+" "+offset);
	
	String ex=n.f5.accept(this,argu);
	
	printx("HSTORE "+bytes+" 0 "+ex);
	
	printx("JUMP "+endArray);	
	printx(outofbound);
	printx("ERROR");
	printx(endArray);
	printx("NOOP");
	
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
public String visit(IfStatement n, TypeBasic argu) {
	//条件跳转，用到NOOP
	
	String exp=n.f2.accept(this,argu);
	String elseLabel=label();
	String endLabel=label();
	
	printx("CJUMP "+exp+" "+elseLabel);
	
	n.f4.accept(this,argu);
	
	printx("JUMP "+endLabel);
	printx(elseLabel);
	
	n.f6.accept(this,argu);
	
	printx(endLabel);
	printx("NOOP");
	
	return null;
}

/**
 * f0 -> "while"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> Statement()
 */
public String visit(WhileStatement n, TypeBasic argu) {
	String whileLabel=label();
	String endLabel=label();
	
	printx(whileLabel);
	
	String exp=n.f2.accept(this,argu);
	
	printx("CJUMP "+exp+" "+endLabel);
	
	n.f4.accept(this,argu);
	
	printx("JUMP "+whileLabel);
	printx(endLabel);
	printx("NOOP");
	
	return null;
}

/**
 * f0 -> "System.out.println"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> ";"
 */
public String visit(PrintStatement n, TypeBasic argu) {
	String exp=n.f2.accept(this,argu);
	printx("PRINT "+exp);
	
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
public String visit(Expression n, TypeBasic argu) {
   return n.f0.accept(this, argu);
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "&&"
 * f2 -> PrimaryExpression()
 */
public String visit(AndExpression n, TypeBasic argu) {
	String zeroLabel=label();
	String endLabel=label();
	
	String exp1=n.f0.accept(this,argu);
	printx("CJUMP "+exp1+" "+zeroLabel);
	
	String exp2=n.f2.accept(this,argu);
	
	String ansReg=regName();
	
	printx("MOVE "+ansReg+" "+exp2);
	printx("JUMP "+endLabel);
	printx(zeroLabel);
	printx("MOVE "+ansReg+" 0");
	printx(endLabel);
	
	return ansReg;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "<"
 * f2 -> PrimaryExpression()
 */
public String visit(CompareExpression n, TypeBasic argu) {
	String exp1=n.f0.accept(this,argu);
	String exp2=n.f2.accept(this,argu);
	String ansReg="LT "+exp1+" "+exp2;
	
	return ansReg;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "+"
 * f2 -> PrimaryExpression()
 */
public String visit(PlusExpression n, TypeBasic argu) {
	String ansReg="PLUS "+n.f0.accept(this,argu)+" "+n.f2.accept(this,argu);
	return ansReg;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "-"
 * f2 -> PrimaryExpression()
 */
public String visit(MinusExpression n, TypeBasic argu) {
	String ansReg="MINUS "+n.f0.accept(this,argu)+" "+n.f2.accept(this,argu);
	return ansReg;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "*"
 * f2 -> PrimaryExpression()
 */
public String visit(TimesExpression n, TypeBasic argu) {
	String ansReg="TIMES "+n.f0.accept(this,argu)+" "+n.f2.accept(this,argu);
	return ansReg;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "["
 * f2 -> PrimaryExpression()
 * f3 -> "]"
 */
public String visit(ArrayLookup n, TypeBasic argu) {
	//获得数组元素，同样根据基地址和索引获得元素
	
	String exp=n.f0.accept(this,argu);
	String index=n.f2.accept(this,argu);
	
	String offset=regName();
	String bytes=regName();
	
	String len=regName();
	String outofbound=label();
	String endArray=label();
	printx("HLOAD "+len+" "+exp+" 0");
	printx("CJUMP LT "+index+" "+len+" "+outofbound);
	
	printx("MOVE "+offset+" PLUS "+index+" 1");
	printx("MOVE "+offset+" TIMES "+offset+" 4");
	printx("MOVE "+bytes+" PLUS "+exp+" "+offset);
	printx("HLOAD "+bytes+" "+bytes+" 0");
	printx("JUMP "+endArray);
	
	printx(outofbound);
	printx("ERROR");
	printx(endArray);
	printx("NOOP");
	
	return bytes;
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "."
 * f2 -> "length"
 */
public String visit(ArrayLength n, TypeBasic argu) {
	String exp=n.f0.accept(this,argu);
	String len=regName();
	printx("HLOAD "+len+" "+exp+" 0");
	
	return len; 
}

/**
 * f0 -> PrimaryExpression()
 * f1 -> "."
 * f2 -> Identifier()
 * f3 -> "("
 * f4 -> ( ExpressionList() )?
 * f5 -> ")"
 */
public String visit(MessageSend n, TypeBasic argu) {
	//方法调用，最麻烦的部分
	//获得对象的地址，计算函数名的地址，然后得到各参数所在的寄存器或值
	
	String pri=n.f0.accept(this,argu);
	String priName=aClass;
	
	String idName=n.f2.f0.toString();
	String address=regName();
	
	TypeClass tc=symbolTable.classes.get(priName);
	TypeMethod tm=tc.methods.get(idName);	
	TypeClass parent=tc;
	int index=0;
	while(tm==null){
		index+=parent.methods.size();
		parent=parent.parent;
		if (parent==null) break;
		tm=parent.methods.get(idName);
	}
	index+=parent.getMethodIndex(idName);
	index*=4;
	String funcs=regName();
	printx("HLOAD "+funcs+" "+pri+" "+0);
	printx("HLOAD "+address+" "+funcs+" "+index);
	
	String params=pri;
	if (n.f4.present()){
		ExpressionList list=(ExpressionList)n.f4.node;
	
		params+=" "+list.f0.accept(this,argu);
		for (Node node:list.f1.nodes){
			params+=" "+node.accept(this,argu);
		}
	}
	
	String ansReg=regName();
	printx("MOVE "+ansReg+" CALL "+address+" ("+params+")");
	
	return ansReg;
}

/**
 * f0 -> Expression()
 * f1 -> ( ExpressionRest() )*
 */
public String visit(ExpressionList n, TypeBasic argu) {
   TypeBasic _ret=null;
   n.f0.accept(this, argu);
   n.f1.accept(this, argu);
   return null;
}

/**
 * f0 -> ","
 * f1 -> Expression()
 */
public String visit(ExpressionRest n, TypeBasic argu) {  
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
public String visit(PrimaryExpression n, TypeBasic argu) {
	return n.f0.accept(this,argu);
}

/**
 * f0 -> <INTEGER_LITERAL>
 */
public String visit(IntegerLiteral n, TypeBasic argu) {
	return n.f0.toString();
}

/**
 * f0 -> "true"
 */
public String visit(TrueLiteral n, TypeBasic argu) {
	return "1";
}

/**
 * f0 -> "false"
 */
public String visit(FalseLiteral n, TypeBasic argu) {
	return "0";
}

/**
 * f0 -> <IDENTIFIER>
 */
public String visit(Identifier n, TypeBasic argu) {
	String id=n.f0.toString();
	
	if (typeIdentifier){
		aClass=id;
		return id;
	}
		
	//参数
	TypeVariable tv=currentMethod.pars.get(id);
	if (tv!=null){
		aClass=tv.type;
		String ansReg="TEMP "+currentMethod.getIndex(id);
		return ansReg;
	}
	
	//本地变量
	tv=currentMethod.vars.get(id);
	if (tv!=null){
		aClass=tv.type;
		String ansReg="TEMP "+currentMethod.getIndex(id);
		return ansReg;
	}
	
	//类的变量
	tv=currentClass.vars.get(id);	
	TypeClass parent=currentClass;
	int index=0;
	while(tv==null){
		index+=parent.vars.size();
		parent=parent.parent;
		if (parent==null) break;
		tv=parent.vars.get(id);
	}
	index+=parent.getVarIndex(id);
	index*=4;
	aClass=tv.type;
	String ansReg=regName();
	printx("HLOAD "+ansReg+" "+"TEMP 0 "+index);
	update="HSTORE TEMP 0 "+index+" "+ansReg;
	return ansReg;
}

/**
 * f0 -> "this"
 */
public String visit(ThisExpression n, TypeBasic argu) {
	aClass=currentClass.name;
	return "TEMP 0";
}

/**
 * f0 -> "new"
 * f1 -> "int"
 * f2 -> "["
 * f3 -> Expression()
 * f4 -> "]"
 */
public String visit(ArrayAllocationExpression n, TypeBasic argu) {
	//数组初始化，返回数组地址，第一个字节存数组长度
	
	String exp=n.f3.accept(this,argu);
	String ansReg=regName();
	String allocate=regName();
	printx("MOVE "+allocate+" PLUS "+exp+" 1");
	printx("MOVE "+allocate+" TIMES "+allocate+" 4");	
	printx("MOVE "+ansReg+" HALLOCATE "+allocate);
	printx("HSTORE "+ansReg+" 0 "+exp);
	
	//初始化数组所有元素为0
	String pointer=regName();
	String initial=label();
	String endInit=label();
	printx("MOVE "+pointer+" "+ansReg);
	printx(initial);
	printx("CJUMP LT "+pointer+" "+allocate+" "+endInit);
	printx("MOVE "+pointer+" PLUS "+pointer+" 4");
	printx("HSTORE "+pointer+" 0 0");
	printx("JUMP "+initial);
	printx(endInit);
	printx("NOOP");
	
	return ansReg;
}

/**
 * f0 -> "new"
 * f1 -> Identifier()
 * f2 -> "("
 * f3 -> ")"
 */
public String visit(AllocationExpression n, TypeBasic argu) {
	//实例化对象
	//获得类，然后分配对象需要的空间，包括类的变量和方法，存储方法名
	//返回对象地址
	
	typeIdentifier=true;
	String id=n.f1.accept(this,argu);
	typeIdentifier=false;
	
	TypeClass tc=symbolTable.classes.get(id);
	int numField=tc.getNumField();
	
	String ansReg=regName();
	printx("MOVE "+ansReg+" HALLOCATE "+(numField+1)*4);
	
	int numMethod=tc.getNumMethod();
	int i=0;
	String funcs=regName();
	printx("MOVE "+funcs+" HALLOCATE "+(numMethod)*4);
	while(tc!=null){
		for (TypeMethod tm:tc.methods.values()){
			printx("HSTORE "+funcs+" "+(i*4)+" "+id+"_"+tm.name);
			i++;
		}
		tc=tc.parent;
	}
	printx("HSTORE "+ansReg+" 0 "+funcs);
	
	return ansReg;
}

/**
 * f0 -> "!"
 * f1 -> Expression()
 */
public String visit(NotExpression n, TypeBasic argu) {
	String exp=n.f1.accept(this,argu);
	
	String ansReg=regName();
	String notLabel=label();
	String endLabel=label();
	
	printx("CJUMP "+exp+" "+notLabel);
	printx("MOVE "+ansReg+" 0");
	printx("JUMP "+endLabel);
	printx(notLabel);
	printx("MOVE "+ansReg+" 1");
	printx(endLabel);
	printx("NOOP");
	
	return ansReg;
}

/**
 * f0 -> "("
 * f1 -> Expression()
 * f2 -> ")"
 */
public String visit(BracketExpression n, TypeBasic argu) {
	return n.f1.accept(this,argu);
}

}
