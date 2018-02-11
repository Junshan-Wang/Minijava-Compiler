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
	public BitSet out;									//��������ڵĻ�Ծ����
	
	//public HashSet<MyBlock> pre;		
	public HashSet<MyBlock> suc;						//������ĺ��
	
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
	
	//�������ڲ��Ļ�Ծ����������û����ת��䣬ֻ��Ҫ���µ��Ϸ���һ�μ���
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
