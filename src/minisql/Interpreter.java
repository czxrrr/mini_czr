package minisql;

import java.util.ArrayList;
import java.util.Scanner;

public class Interpreter {
	public static void show_help(){
		System.out.println("help");
	}
	
    
    public static String create_clause(ArrayList<String> in )
    {
    	String SQL;
    	if(in.size()<4){
    		SQL="99";
    		System.out.println("illegal create sentences");
    		return SQL;
    	}
    	if(in.get(1).equals("database")&&in.get(3).equals(";")){
    		SQL=new String("00");
    		SQL = SQL+in.get(2);
    		System.out.println("create database"+in.get(2));
    	}
    	//create table a( name char(8) unique, age integer );
    	else if(in.get(1).equals("table")){
    		System.out.println("in table");
    		SQL=new String("01");
    		SQL = SQL+in.get(2);	
    		int len=in.size();
    		int i;
    		if(!in.get(3).equals("(") || !in.get(len-2).equals(")")){
    			System.out.println("missing ( or )");
    			return "99";
    		}
    		int start=4;
    		ArrayList<Field> fields = new ArrayList<Field>();
    		ArrayList<String> fieldString=new ArrayList<String>();
    		String primary=null;
    		for(i=4;i<=len-2;i++){
    			if(in.get(i).equals(",")){
    	    		Field t=new Field();
    				//t.setAll(fieldString);
    				fieldString.clear();
    				//fields.add(t);
    			}else if((in.get(i).equals(")")&& in.get(i+1).equals(";") )){
    				if(fieldString.get(0).equals("primary")){
    					if(fieldString.get(1).equals("key")){
    						if(in.indexOf(fieldString.get(2))!=i-1){
    							primary=fieldString.get(2);
    						}
    					}else{
    						return "99";
    					}
    				}
    			}
    			else{
    				fieldString.add(in.get(i));
    			}	
    		}
    		System.out.println(in.get(2)+fields+primary);
    	}
    	else if(in.get(1).equals("index")){
    		SQL=new String("11");
    		SQL = SQL+in.get(2);
    		create_index_on(in);
       		//System.out.println("create index"+in.get(2));
    	}
    	else{
    		SQL=new String("99");
    		System.out.println("if you want help,please input help;  Do not add any other words");
    	}
    	return SQL;
    }
    
    public static String create_index_on(ArrayList<String> in ){
    	if(in.size()<9){
    		System.out.println("not enough parameters");
    	}else{
    		if(in.get(3).equals("on")){
        		if(in.get(5).equals("(")&&in.get(7).equals(")")){
        			System.out.println("create index"+in.get(4)+"-on-"+in.get(6));
        		}
        		else{
        			System.out.println("missing ( or )");
        		}
        	}
        	else{
        		System.out.println("no keyword on was found");
        	}
    	}
    	
    	return "99";
    }
    
    public static String insert_clause(ArrayList<String> in )
    {
    	
    	String SQL=new String();
    	if(in.size()<6){
    		SQL="99";
    		System.out.println("illegal insert sentences");
    		return SQL;
    	}
    	if(in.get(1).equals("into")&&in.get(3).equals("values")&& in.get(4).equals("(")){
    		SQL=new String ("");
    		SQL = SQL+in.get(2);
    		int i;
    		ArrayList<String> values=new ArrayList<String>();
    		for(i=5;i<in.size()-1;i++){
    			if( i % 2 ==0 ){
    				if(in.get(i).equals(",")){
    					continue;
    				}
    				else{
    					if(in.get(i).equals(")") && in.get(i+1).equals(";")){
    						System.out.println("insert "+in.get(2)+" "+values);
    					}
    					else{
    						SQL="99";
    						return SQL;
    					}
    				}
    			}else{
    				values.add(in.get(i));
    			}
    		}
    	}
    	return SQL;
    }
    
    public static String delete_clause(ArrayList<String> in){
    	String SQL=new String();
    	if(in.size()==4){
    		if(in.get(3).equals(";")){
    			System.out.println("delete");
    			return "delete done";
    		}else{
    			System.out.println("error");
    			SQL="99";
    			return SQL;
    		}
    	}else if(in.size()<=4){
			System.out.println("error");
			SQL="99";
			return SQL;
    	}else if (in.get(3).equals("where")){
    		
    	}
    
    	return "99";
    }
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		String in = scan.nextLine();
		while(true){
			if (in.equals("0")){
				break;
			}
			while(in.indexOf(';')<0){
				in = in + " " + scan.nextLine();
			}
			ArrayList<String> words = split_word.my_split(in);
			System.out.println(input_classify(words));
			in = scan.nextLine();
		}
	}
	
	
	
	private static String input_classify(ArrayList<String> words) {
		String SQL;
		if(words.size()<2){
			System.out.println("not enough words in the sentence");
		}
		if(words.get(0).equals("help")){
			SQL=help_clause(words);
		}
		else if(words.get(0).equals("quit")){
			SQL=quit_clause(words);
		}
		else if(words.get(0).equals("create")){
			SQL=create_clause(words);
		}
		else if(words.get(0).equals("drop")){
			SQL=drop_clause(words);
		}
		else if(words.get(0).equals("insert")){
			SQL=insert_clause(words);
		}
		else if(words.get(0).equals("delete") && words.get(1).equals("from")){
			SQL=delete_clause(words);
		}
//		else if(words.get(0).equals("execfile")){
//			SQL=create_clause(words,1);
//		}
//		else if(words.get(0).equals("select")){
//			SQL=create_clause(words,1);
//		}
		else SQL="99";
		return SQL;
	}


	//quit
	public static String quit_clause(ArrayList<String> in )
	{
		String SQL=new String();
		if(in.get(1).equals(";")){
			System.out.println("bye bye");
			System.exit(0);
		}
		else{
			SQL=new String("99");
			System.out.println("if you want exit,please input quit;  Do not add any other words");
		}
		return SQL;
	}
	//help
	public static String help_clause(ArrayList<String> in )
	{
		String SQL;
		if(in.get(1).equals(";")){
			SQL=new String("80");
			show_help();
		}
		else{
			SQL=new String("99");
			System.out.println("if you want help,please input help;  Do not add any other words");
		}
		return SQL;
	}
    public static String drop_clause(ArrayList<String> in )
    {
    	String SQL;
    	if(in.size()<4){
    		SQL="99";
    		System.out.println("illegal drop sentences");
    		return SQL;
    	}
    	if(in.get(1).equals("database")&&in.get(3).equals(";")){
    		SQL=new String("10");
    		SQL = SQL+in.get(2);
    	}
    	else if(in.get(1).equals("table")&&in.get(3).equals(";")){
    		SQL=new String("11");
    		SQL = SQL+in.get(2);
    	}
    	else if(in.get(1).equals("index")&&in.get(3).equals(";")){
    		SQL=new String("12");
    		SQL = SQL+in.get(2);
    	}
    	else{
    		SQL=new String("99");
    		System.out.println("--if you want help,please input help;  Do not add any other words");
    	}
    	return SQL;
    }

}
