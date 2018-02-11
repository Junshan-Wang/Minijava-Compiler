package minijava.SymbolTable;

import EDU.purdue.jtb.syntaxtree.Type;
import minijava.syntaxtree.Identifier;
import minijava.visitor.SymbolTableVisitor;

public class TypeVariable extends TypeBasic{

	public String name;
	public String type;

	public static TypeVariable ARRAY=new TypeVariable(null,"array");
	public static TypeVariable BOOLEAN=new TypeVariable(null,"boolean");
	public static TypeVariable INTEGER=new TypeVariable(null,"integer");
	public static TypeVariable OTHER=new TypeVariable(null,"other");
	
	public TypeVariable(String name_, String type_){
		this.name=name_;
		this.type=type_;
	}
	public TypeVariable(String name_, minijava.syntaxtree.Type t){
		this.name=name_;
		switch (t.f0.which){
		case 0:
			this.type="array";
			break;
		case 1:
			this.type="boolean";
			break;
		case 2:
			this.type="integer";
			break;
		case 3:
			this.type=((Identifier)t.f0.choice).f0.toString();
		}
	}
}
