package spiglet.spiglet2kanga;

import java.util.BitSet;

public class MyStatement {
	public int line;
	
	public BitSet def;		//����ֵ
	public BitSet use;			//��ʹ��
	
	public BitSet live;			//��Ծ
	
	public MyStatement(int line_) {
		this.line=line_;
		def=new BitSet();
		use=new BitSet();
		live=new BitSet();
	}
}
