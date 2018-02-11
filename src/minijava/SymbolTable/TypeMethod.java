package minijava.SymbolTable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import EDU.purdue.jtb.syntaxtree.Type;

public class TypeMethod extends TypeBasic{

	public String name;
	public TypeVariable ret;
	public TypeClass parent;
		
	public LinkedHashMap<String,TypeVariable> pars;
	public LinkedHashMap<String,TypeVariable> vars;
		
	public TypeMethod(String name_, TypeVariable ret_){
		this.name=name_;
		this.ret=ret_;
		this.parent=null;
			
		pars=new LinkedHashMap<String,TypeVariable>();
		vars=new LinkedHashMap<String,TypeVariable>();
	}
	
	public TypeVariable addPar(String ParName, String type){
		TypeVariable tv=this.pars.get(ParName);
		if (tv!=null) return null;
		tv=new TypeVariable(ParName, type);
		this.pars.put(ParName, tv);
		return tv;
	}
	
	public TypeVariable addPar(String ParName, minijava.syntaxtree.Type type){
		TypeVariable tv=this.pars.get(ParName);
		if (tv!=null) return null;
		tv=new TypeVariable(ParName, type);
		this.pars.put(ParName, tv);
		return tv;
	}
	
	public TypeVariable addVar(String VarName, String type){
		TypeVariable t1=this.pars.get(VarName);
		TypeVariable t2=this.vars.get(VarName);
		if (t1!=null) return null;
		if (t2!=null) return null;
		t2=new TypeVariable(VarName, type);
		this.vars.put(VarName, t2);
		return t2;
	}
	
	public TypeVariable addVar(String VarName, minijava.syntaxtree.Type type){
		TypeVariable t1=this.pars.get(VarName);
		TypeVariable t2=this.vars.get(VarName);
		if (t1!=null) return null;
		if (t2!=null) return null;
		t2=new TypeVariable(VarName, type);
		this.vars.put(VarName, t2);
		return t2;
	}
	
	public int getIndex(String Name){
		int index=0;
		for (Iterator it=this.pars.keySet().iterator();it.hasNext();){
			index++;
			if (Name==it.next()){
				return index;
			}
		}
		for (Iterator it=this.vars.keySet().iterator();it.hasNext();){
			index++;
			if (Name==it.next()){
				return index;
			}
		}
		return -1;
	}
		
}
