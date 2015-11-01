package minisql;

import java.util.ArrayList;

public class KeyWord {
	static ArrayList<String> keys=new ArrayList<String>();
	
	public static boolean isKeyword(String a,String b,String c){
		keys.add("create");
		keys.add("primary");
		keys.add("key");
		keys.add("drop");
		keys.add("index");
		keys.add("on");
		keys.add("delete");
		keys.add("from");
		keys.add("select");
		keys.add("where");
		keys.add("and");
		keys.add("or");
		keys.add("*");
		keys.add("(");
		keys.add(")");
		keys.add("<");
		keys.add("<>");
		keys.add(">");
		keys.add("<=");
		keys.add(">=");
		keys.add("=");
		keys.add(";");
		keys.add("'");
		//keys.add("delete");
		return (keys.indexOf(a)>=0 ||keys.indexOf(b)>=0||keys.indexOf(c)>=0);
	}
	
	
	public static boolean isKeyword(String a){
		keys.add("create");
		keys.add("primary");
		keys.add("key");
		keys.add("drop");
		keys.add("index");
		keys.add("on");
		keys.add("delete");
		keys.add("from");
		keys.add("select");
		keys.add("where");
		keys.add("and");
		keys.add("or");
		keys.add("*");
		keys.add("(");
		keys.add(")");
		keys.add("<");
		keys.add("<>");
		keys.add(">");
		keys.add("<=");
		keys.add(">=");
		keys.add("=");
		keys.add(";");
		keys.add("'");
		//keys.add("delete");
		return (keys.indexOf(a)>=0);
	}
	public static boolean isOp(String a){
		keys.add("<");
		keys.add("<>");
		keys.add(">");
		keys.add("<=");
		keys.add(">=");
		keys.add("=");
		//keys.add("delete");
		return (keys.indexOf(a)>=0);
	}
	public static void main(String[] args){
		System.out.println(isOp("="));
		System.out.println(isKeyword("="));
		System.out.println("\'");
	}
}
