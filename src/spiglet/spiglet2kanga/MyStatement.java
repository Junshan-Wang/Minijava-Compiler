package spiglet.spiglet2kanga;

import java.util.BitSet;

public class MyStatement {
	public int line;
	
	public BitSet def;		//被赋值
	public BitSet use;			//被使用
	
	public BitSet live;			//活跃
	
	public MyStatement(int line_) {
		this.line=line_;
		def=new BitSet();
		use=new BitSet();
		live=new BitSet();
	}
}
