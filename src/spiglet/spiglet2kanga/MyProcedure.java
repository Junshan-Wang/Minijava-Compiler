package spiglet.spiglet2kanga;

import java.util.*;

public class MyProcedure {
	public String name;
	public LinkedHashMap<Integer, MyBlock> blocks;			//过程的基本块
	public LinkedHashMap<String, MyBlock> labelBlocks;		//保存标号和基本块的对应关系
	public int maxPars;										//过程中的最大参数
	public int stackSize;									//使用的栈
	public HashSet<Integer> callStats;						//过程中调用其他过程的行数
	public ArrayList<Interval> variables;					//保存过程局部变量的活跃区间：区间内有调用其他过程，需要被保存
	public Vector<BitSet> live;								//保存过程中所有变量的实时活跃情况
	public int spillSize;									//记录保存变量在栈中的起始位置
	public int saveSize;									//保存用到的s寄存器的个数
	
	public String []register;								//每个TEMP对应的寄存器（如果有）
	public String []location;								//每个TEMP对应在栈中的位置（如果有）
	
	 
	public MyProcedure(String name_) {
		this.name=name_;
		blocks=new LinkedHashMap<Integer, MyBlock>();
		labelBlocks=new LinkedHashMap<String, MyBlock>();
		maxPars=0;
		stackSize=0;
		callStats=new HashSet<Integer>();
		live=new Vector<BitSet>();
	}
	
	public void addBlck(MyBlock block) {
		blocks.put(block.begin, block);
		labelBlocks.put(block.label, block);
	}

	//写回跳转语句时跳转的目标
	public void writeBack(HashMap<MyBlock, Integer> nextLabelList, HashMap<MyBlock, String> jumpLabelList) {
		int nextLabel;
		String jumpLabel;
		for (MyBlock cb : nextLabelList.keySet()) {
			nextLabel=nextLabelList.get(cb);
			MyBlock target=this.blocks.get(nextLabel);
			cb.suc.add(target);
		}
		for (MyBlock cb :jumpLabelList.keySet()) {
			jumpLabel=jumpLabelList.get(cb);
			MyBlock target=this.labelBlocks.get(jumpLabel);
			cb.suc.add(target);
		}
	}
	
	//活性分析，写基本块内分析，再过程整体分析，最后基本块内再分析一次
	public void livenessAnalyse() {
		
		for (MyBlock cb : blocks.values()) {
			cb.livenessAnalyse();
		}
				
		Boolean converge;
		while(true) {
			converge=true;
			ListIterator<Map.Entry<Integer,MyBlock>> i=new ArrayList<Map.Entry<Integer,MyBlock>>(blocks.entrySet()).listIterator(blocks.size());
			while(i.hasPrevious()) {
				MyBlock cb=i.previous().getValue();
				BitSet last=(BitSet)cb.out.clone();
				for (MyBlock suc : cb.suc) 
					cb.out.or(suc.in);
				cb.in.or(cb.out);
				cb.in.andNot(cb.def);
				cb.in.or(cb.use);
				
				if (!cb.out.equals(last)) {
					converge=false;
				}
			}
			if (converge) break;
		}
		
		for (MyBlock cb : blocks.values()) {
			cb.livenessAnalyse();
			for (MyStatement cs : cb.stats.values()) {
				live.add(cs.line, cs.live);
			}
		}
	}

	//得到每个变量的活性区间
	public HashMap<Integer, Interval> getInterval() {
		HashMap<Integer, Interval> intervals=new HashMap<Integer, Interval>();
		for (MyBlock cb : blocks.values()) {
			for (MyStatement cs : cb.stats.values()) {
				BitSet cl=cs.live;
				Interval interval;
				for (int i=0;i<cl.size();++i) {
					if (cl.get(i)==true) {
						if (intervals.containsKey(i)==false) {
							interval=new Interval(i,cs.line);
							intervals.put(i, interval);
						}
						else {
							intervals.get(i).end=cs.line;
						}
					}
				}
			}
		}
		return intervals;
	}

	
	public void LSRegisterAllocation() {
		
		HashMap<Integer, Interval> intervalMap=getInterval();
		
		int maxTemp=0;
		for (int i : intervalMap.keySet()) {
			if (i>maxTemp) maxTemp=i;
		}
		
		register=new String[maxTemp+1];
		location=new String[maxTemp+1];
		Vector<String> freeReg=new Vector<String>();
		Vector<Interval> active=new Vector<Interval>();
		
		//局部变量和临时变量分类
		variables = new ArrayList<Interval>();		
		ArrayList<Interval> temporary = new ArrayList<Interval>();
		for(Interval interval : intervalMap.values()) {
			temporary.add(interval);
			for (int j : callStats) {
				if (interval.begin<j && interval.end>j) {
					variables.add(interval);
					temporary.remove(interval);
					break;
				}
			}
		}
		Collections.sort(variables);
		Collections.sort(temporary);
		
		//需要保存的局部变量，分配s寄存器
		for (int i=0;i<8;++i) freeReg.add("s"+i);				
		for (Interval interval : variables) {
			int start=interval.begin;
			Interval j;
			if (!freeReg.isEmpty()) {
				register[interval.tempID]=freeReg.remove(0);
				active.add(interval);
			}
			else {
				Interval spill=active.firstElement();
				for (int i=0;i<active.size();++i) {
					j=active.get(i);
					if (j.end>spill.end) spill=j;
				}
				if (spill.end>interval.end) {
					register[interval.tempID]=register[spill.tempID];
					register[spill.tempID]=null;
					location[spill.tempID]=Integer.toString(stackSize++);
					active.remove(spill);
					active.add(interval);
				}
				else {
					location[interval.tempID]=Integer.toString(stackSize++);
				}
			}
		}
		//spillSize=stackSize;
		saveSize=8-freeReg.size();
		
		
		//临时变量，分配t寄存器
		freeReg.clear();
		for (int i=9;i>=0;--i) freeReg.add(0,"t"+i);
		active.clear();
		for (Interval interval : temporary) {
			int start=interval.begin;
			Interval j;
			for (int i=0;i<active.size();++i) {
				j=active.get(i);
				if (j.end<start) {
					active.remove(j);
					freeReg.add(0, register[j.tempID]);
				}
			}
			
			if (!freeReg.isEmpty()) {
				register[interval.tempID]=freeReg.remove(0);
				active.add(interval);
			}
			else {
				Interval spill=active.firstElement();
				for (int i=0;i<active.size();++i) {
					j=active.get(i);
					if (j.end>spill.end) spill=j;
				}
				if (spill.end>interval.end) {
					register[interval.tempID]=register[spill.tempID];
					register[spill.tempID]=null;
					location[spill.tempID]=Integer.toString(stackSize++);
					active.remove(spill);
					active.add(interval);
				}
				else {
					location[interval.tempID]=Integer.toString(stackSize++);
				}
			}
		}

		spillSize=stackSize;
		stackSize+=saveSize;
	}	
	
}
