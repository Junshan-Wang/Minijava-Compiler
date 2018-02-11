package minijava.typecheck;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.syntaxtree.*;
import minijava.visitor.*;
//import minijava.syntaxtree.Node;

public class Typecheck {

   public static void main(String [] args) {
		Node root = null;
		try {
			root = new MiniJavaParser(System.in).Goal();
			//System.out.println("Program parsed successfully");
		}
		catch (ParseException e) {
			System.out.println(e.toString());
			System.exit(1);
		}

		// Build symbol table
		SymbolTableVisitor stv = new SymbolTableVisitor();
		root.accept(stv,null);
		if (stv.error.size()!=0) {
			System.out.println("Type error");
			/*
			System.out.println(stv.error.size());
			for (int i=0;i<stv.error.size();i++){
				System.out.println(stv.error.get(i));
			}
			*/
			System.exit(1);
		}
		
		// Type-check
		TypeCheckVisitor tcv = new TypeCheckVisitor(stv);
		root.accept(tcv,null);
		if (tcv.error.size()!=0) {
			System.out.println("Type error");
			/*
			System.out.println(tcv.error.size());
			for (int i=0;i<tcv.error.size();i++){
				System.out.println(tcv.error.get(i));
			}
			*/
			System.exit(1);
		} else {
			System.out.println("Program type checked successfully");

		}
     
   }

}