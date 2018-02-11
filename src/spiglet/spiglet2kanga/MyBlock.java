package spiglet.spiglet2kanga;

import java.util.*;

public class MyBlock {
	public String label;
	
	public int begin;
	public int end;
	public LinkedHashMap<Integer,MyStatement> stats;
	
	public BitSet def;
	public BitSet use;
	public BitSet in;
	public BitSet out;									//基本块出口的活跃变量
	
	//public HashSet<MyBlock> pre;		
	public HashSet<MyBlock> suc;						//基本块的后继
	
	public MyBlock(int begin_) {
		this.begin=begin_;
		this.label=null;
		def=new BitSet();
		use=new BitSet();
		in=new BitSet();
		out=new BitSet();
		stats=new LinkedHashMap<Integer,MyStatement>();
		suc=new HashSet<MyBlock>();
	}
	
	public MyBlock(int begin_, String label_) {
		this.begin=begin_;
		this.label=label_;
		def=new BitSet();
		use=new BitSet();
		in=new BitSet();
		out=new BitSet();
		stats=new LinkedHashMap<Integer,MyStatement>();
		suc=new HashSet<MyBlock>();
	}
	
	public void exit(int end_) {
		this.end=end_;
	}
	
	public void addStat(MyStatement stat) {
		stats.put(stat.line, stat);
	}
	
	//基本块内部的活跃分析，由于没有跳转语句，只需要从下到上分析一次即可
	public void livenessAnalyse() {
		this.use=(BitSet)this.out.clone();
		ListIterator<Map.Entry<Integer,MyStatement>> i=new ArrayList<Map.Entry<Integer,MyStatement>>(stats.entrySet()).listIterator(stats.size());
		//i.previous();
		MyStatement cs=i.previous().getValue();
		this.use.or(cs.use);
		cs.live=(BitSet)this.use.clone();
		while(i.hasPrevious()) {
			cs=i.previous().getValue();
			this.use.andNot(cs.def);
			this.use.or(cs.use);
			this.def.or(cs.def);
			cs.live=(BitSet)this.use.clone();
		}
	}
}
