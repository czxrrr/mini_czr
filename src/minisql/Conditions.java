package minisql;

public class Conditions {
	public String op;
	public Field field;
	public String s_value; 
	public float f_value;
	public int i_value;
	public Conditions(String a, String op, String b) {
		//if(!existField(a)){
			//胡彬 你要把这个existField 函数补全。。。。
			//System.out.println("the Field "+a+"doesn't exist");
			
		//}
		this.op=op;
		this.s_value=b;
		// TODO Auto-generated constructor stub
	}
	public Conditions(String a, String op, float b) {
		//if(!existField(a)){
			//胡彬 你要把这个existField 函数补全。。。。
			//System.out.println("the Field "+a+"doesn't exist");
			
		//}
		this.op=op;
		this.f_value=b;
		// TODO Auto-generated constructor stub
	}
	public Conditions(String a, String op, int b) {
		//if(!existField(a)){
			//胡彬 你要把这个existField 函数补全。。。。
			//System.out.println("the Field "+a+"doesn't exist");
			
		//}
		this.op=op;
		this.i_value=b;
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
