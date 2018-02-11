package minijava.typecheck;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.syntaxtree.Node;
import minijava.visitor.SymbolTableVisitor;
import minijava.visitor.TypeCheckVisitor;

public class Main {
	public static void main(String [] args) {
		Node root = null;
		boolean print=false; //�Ƿ��ӡ������Ϣ
		try {
			root = new MiniJavaParser(System.in).Goal();
			//System.out.println("Program parsed successfully");
		}
		catch (ParseException e) {
			System.out.println(e.toString());
			System.exit(1);
		}

		// �������ű��ж��Ƿ��ض��壬�Ƿ���ѭ���̳У��Ƿ�������
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
		
		// ���ͼ�飬�����������Ƿ�ƥ��
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
