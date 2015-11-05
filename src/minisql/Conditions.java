package minisql;

public class Conditions {
	public String op;
	public Field field;
	public String value; 
	//public float f_value;
	//public int i_value;
	public Conditions(String a, String op, String b) {
		//if(!existField(a)){
			//胡彬 你要把这个existField 函数补全。。。。
			//System.out.println("the Field "+a+"doesn't exist");
			
		//}
		this.op=op;
		this.value=b;
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
