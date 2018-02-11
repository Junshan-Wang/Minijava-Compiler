package minijava.SymbolTable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import EDU.purdue.jtb.syntaxtree.Type;
import minijava.visitor.SymbolTableVisitor;

public class TypeClass extends TypeBasic{
	
	public String name;
	public TypeClass parent;
	public boolean isinited;
	
	public LinkedHashMap<String,TypeVariable> vars;
	public LinkedHashMap<String,TypeMethod> methods;
	
	public TypeClass(String name_){
		this.name=name_;
		this.parent=null;
		this.isinited=true;
		vars=new LinkedHashMap<String,TypeVariable>();
		methods=new LinkedHashMap<String,TypeMethod>();
	}
	
	public TypeMethod getMethod(String MethodName){
		TypeMethod tm=this.methods.get(MethodName);
		if (tm!=null) return tm;
		
		TypeClass parent=this.parent;
		
		while (parent!=null){
			tm=parent.getMethod(MethodName);
			if (tm!=null) return tm;
			parent=parent.parent;
		}
		return null;
		
	}
	
	public TypeMethod addMethod(String MethodName, TypeVariable ret_){
		TypeMethod tm=this.methods.get(MethodName);
		if (tm!=null) return null;
		else{
			tm=new TypeMethod(MethodName, ret_);
			this.methods.put(MethodName, tm);
			return tm;
		}
	}
	
	public TypeVariable addVar(String VarName, String type){
		TypeVariable tv=this.vars.get(VarName);
		if (tv!=null) return null;
		tv=new TypeVariable(VarName, type);
		this.vars.put(VarName, tv);
		return tv;
	}
	
	public TypeVariable addVar(String VarName, minijava.syntaxtree.Type type){
		TypeVariable tv=this.vars.get(VarName);
		if (tv!=null) return null;
		tv=new TypeVariable(VarName, type);
		this.vars.put(VarName, tv);
		return tv;
	}
	
	public int getVarIndex(String Name){
		int index=0;
		for (Iterator it=vars.keySet().iterator();it.hasNext();){
			index++;
			if (Name==it.next().toString()){
				return index;
			}
		}
		return -1;
	}
	
	public int getNumField(){
		int numField=0;
		TypeClass parent=this;
		while(parent!=null){
			numField+=parent.vars.size();
			parent=parent.parent;
		}
		return numField;
	}
	
	public int getMethodIndex(String Name){
		int index=0;
		for (Iterator it=this.methods.keySet().iterator();it.hasNext();){			
			//System.out.println(Name);
			if (Name==it.next().toString()){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public int getNumMethod(){
		int numMethod=0;
		TypeClass parent=this;
		while(parent!=null){
			numMethod+=parent.methods.size();
			parent=parent.parent;
		}
		return numMethod;
	}
}
