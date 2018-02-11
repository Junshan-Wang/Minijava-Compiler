package spiglet.spiglet2kanga;

public class Interval implements Comparable<Interval>{
	public int tempID;
	int begin;
	int end;
	
	public Interval(int tempID_, int begin_) {
		this.tempID=tempID_;
		this.begin=begin_;
	}
	
	public int compareTo(Interval other) {
		if (begin == other.begin) return end - other.end;
		else return begin - other.begin;
	}
}
