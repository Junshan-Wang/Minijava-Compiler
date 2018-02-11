package kanga.kanga2mips;

import kanga.KangaParser;
import kanga.ParseException;
import kanga.syntaxtree.Node;
import kanga.visitor.MipsGenerateVisitor;

public class Main {
	public static void main(String[] args) {
		Node root=null;
    	try {
    		root = new KangaParser(System.in).Goal();		
    	}
    	catch (ParseException e){
    		e.printStackTrace();
    	}
    	
    	MipsGenerateVisitor mgv=new MipsGenerateVisitor();
    	root.accept(mgv);
    }
}
