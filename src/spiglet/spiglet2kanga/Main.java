package spiglet.spiglet2kanga;

import spiglet.ParseException;
import spiglet.SpigletParser;
import spiglet.syntaxtree.Node;
import spiglet.visitor.CreateGraphVisitor;
import spiglet.visitor.Spiglet2kangaVisitor;

public class Main {
	public static void main(String[] args) {
		Node root=null;
    	try {
    		root = new SpigletParser(System.in).Goal();		
    	}
    	catch (ParseException e){
    		e.printStackTrace();
    	}
    	
    	CreateGraphVisitor cgv=new CreateGraphVisitor();
    	root.accept(cgv);
    	
    	Spiglet2kangaVisitor skv=new Spiglet2kangaVisitor(cgv.procedures);
    	root.accept(skv);
    
    	System.out.println(skv.output);
    }
}
