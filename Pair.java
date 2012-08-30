
public class Pair {
	private int a;
	private int b;
	
	
	Pair(int x, int y)
	{
		a=x;
		b=y;
		
	}
	
	public boolean equals(Object pair){
		return (((Pair) pair).first()==this.a && ((Pair) pair).second()==this.b);
	}
	
	public int hashCode(){
		return 0;
	}
	
	int first() { return a; }
	int second() { return b; }
	
	void set(int x, int y)
	{
		a=x;
		b=y;
	}
	
	public String toString(){
		return "X: "+this.a + " Y:"+this.b;
	}
	
}
