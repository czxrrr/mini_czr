package minisql;

import java.io.IOException;

public class Conditions {
	public String op;
	public Field field;
	public String value; 
	//public float f_value;
	//public int i_value;
	public Conditions(String tablename,String a, String op, String b) throws IOException {
		for(Field f:CatalogManager.readTableFields(tablename) ){
			if (f.getName().equals(a)){
				this.field=new Field(f);
				break;
			}
		}
		this.op=op;
		this.value=b;
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
