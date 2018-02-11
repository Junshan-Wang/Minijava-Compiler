package minijava.typecheck;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.syntaxtree.Node;
import minijava.visitor.SymbolTableVisitor;
import minijava.visitor.TypeCheckVisitor;

public class Main {
	public static void main(String [] args) {
		Node root = null;
		boolean print=false; //是否打印错误信息
		try {
			root = new MiniJavaParser(System.in).Goal();
			//System.out.println("Program parsed successfully");
		}
		catch (ParseException e) {
			System.out.println(e.toString());
			System.exit(1);
		}

		// 建立符号表，判断是否重定义，是否有循环继承，是否有重载
		SymbolTableVisitor stv = new SymbolTableVisitor();
		root.accept(stv,null);
		if (stv.error.size()!=0) {
			System.out.println("Type error");
			
			if (print){
				for (int i=0;i<stv.error.size();i++){
					System.out.println(stv.error.get(i));
				}			
			}
			System.exit(1);
		}
		
		// 类型检查，检查各种类型是否匹配
		TypeCheckVisitor tcv = new TypeCheckVisitor(stv);
		root.accept(tcv,null);
		if (tcv.error.size()!=0) {
			System.out.println("Type error");
			
			if (print){
				for (int i=0;i<tcv.error.size();i++){
					System.out.println(tcv.error.get(i));
				}
			}
			
			System.exit(1);
		} else {
			System.out.println("Program type checked successfully");

		}
	}
}
