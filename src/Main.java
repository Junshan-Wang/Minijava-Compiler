import java.io.ByteArrayInputStream;

import kanga.KangaParser;
import kanga.visitor.MipsGenerateVisitor;
import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.syntaxtree.Node;
import minijava.visitor.SpigletGenerateVisitor;
import minijava.visitor.SymbolTableVisitor;
import minijava.visitor.TypeCheckVisitor;
import spiglet.SpigletParser;
import spiglet.visitor.CreateGraphVisitor;
import spiglet.visitor.Spiglet2kangaVisitor;

public class Main {
	public static void main(String[] args) throws spiglet.ParseException, minijava.ParseException, kanga.ParseException {

		Node rootm = null;
		boolean print=false; //是否打印错误信息
		try {
			rootm = new MiniJavaParser(System.in).Goal();
			//System.out.println("Program parsed successfully");
		}
		catch (ParseException e) {
			System.out.println(e.toString());
			System.exit(1);
		}

		// 建立符号表，判断是否重定义，是否有循环继承，是否有重载
		SymbolTableVisitor stv = new SymbolTableVisitor();
		rootm.accept(stv,null);
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
		rootm.accept(tcv,null);
		if (tcv.error.size()!=0) {
			System.out.println("Type error");
			
			if (print){
				for (int i=0;i<tcv.error.size();i++){
					System.out.println(tcv.error.get(i));
				}
			}
			
			System.exit(1);
		} else {
			//System.out.println("Program type checked successfully");
		}
		
		//生成Spiglet代码
		SpigletGenerateVisitor spg=new SpigletGenerateVisitor(stv);
		rootm.accept(spg,null);
		
		//System.out.println(spg.output);
		
		//生成kanga代码
		ByteArrayInputStream spigletCode = new ByteArrayInputStream(spg.output.getBytes());
		
		spiglet.syntaxtree.Node roots=null;
    	roots = new SpigletParser(spigletCode).Goal();
    	
    	CreateGraphVisitor cgv=new CreateGraphVisitor();
    	roots.accept(cgv);
    	
    	Spiglet2kangaVisitor skv=new Spiglet2kangaVisitor(cgv.procedures);
    	roots.accept(skv);
    	
    	//System.out.println(skv.output);
    	
    	//生成mips代码
    	ByteArrayInputStream kangaCode = new ByteArrayInputStream(skv.output.getBytes());
    	
    	kanga.syntaxtree.Node rootk=null;
    	rootk = new KangaParser(kangaCode).Goal();
    	
    	MipsGenerateVisitor mgv=new MipsGenerateVisitor();
    	rootk.accept(mgv);
    	
	}
}
