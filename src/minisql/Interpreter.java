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
   
    
    
    //===================delete================
    public static String delete_clause(ArrayList<String> in){
    	String SQL=new String();
    	if(in.size()==4){
    		if(in.get(3).equals(";")){
    			System.out.println("Delete success");
    			return "00";
    		}else{
    			System.out.println("Delete error: unknown error");
    			SQL="99";
    			return SQL;
    		}
    	}else if(in.size()<4){
			System.out.println("Delete error:incomplete delete sentence");
			SQL="99";
			return SQL;
    	}else if (in.get(3).equals("where")){
    		int bracket=0;
    		int i=4;
    		ArrayList<Conditions> cons=new ArrayList<Conditions>();
    		while(i<in.size()){
    			if(in.get(i).equals(";")){
    				break;
    			}
    			if(in.get(i).equals("and")){
    				i++;
    				continue;
    			}
    			if(in.get(i).equals("(")){
    				bracket++;	
    			}
    			else if(in.get(i).equals(")")){
    				bracket++;
    				if(bracket<0){
    					System.out.println("Delete error: ( and ) are not right;"); 
        				return "99";
    				}
    			}
    			else{
    				//System.out.println(in.size()+i);
    				if(i+2<in.size()){
    					if(!KeyWord.isKeyword(in.get(i)) && !KeyWord.isKeyword(in.get(i+2)) && KeyWord.isOp(in.get(i+1)) ){
    						cons.add(new Conditions(in.get(i),in.get(i+1),in.get(i+2)));
    						System.out.println("chenggong");
    						i=i+3;
    						if(in.get(i).equals("and")){
    							i++;
    							continue;
    						}else if(in.get(i).equals(";")){
    							break;
    						}
    						
    					}else if(i+4<in.size()){
    						System.out.println(!KeyWord.isKeyword(in.get(i)));
    						System.out.println(!KeyWord.isKeyword(in.get(i+3) ));
    						System.out.println(KeyWord.isOp(in.get(i+1)));
    						System.out.println(!in.get(i+2).equals("\'"));
    						if(!KeyWord.isKeyword(in.get(i)) && !KeyWord.isKeyword(in.get(i+3)) && KeyWord.isOp(in.get(i+1))&& in.get(i+2).equals("\'") && in.get(i+4).equals("\'") ){
        						cons.add(new Conditions(in.get(i),in.get(i+1),in.get(i+3)));
        						System.out.println("chenggong");
        						i=i+5;
        						if(in.get(i).equals("and")){
        							i++;
        							continue;
        						}else if(in.get(i).equals(";")){
        							break;
        						}
        					}else{
        						System.out.println("1Delete error: one or more conditions are not complete;"); 
            					return "99";
        					}
    					}else{
    						System.out.println("Delete error: one or more conditions are not complete;"); 
        					return "99";
    					}
    				}else{
    					System.out.println("Delete error: one or more conditions are not complete;"); 
    					return "99";
    				}
    			}    		
    		i++;
    		}
    		if(bracket<0){
    			System.out.println("Delete error: ( and ) are not right;"); 
				return "99";
    		}else{
    			System.out.println("条件被提取成功");
    			return "00";
    		}
    		
    	}
    	return "99";
    }
    
    
    //===================select================
    public static String select_clause(ArrayList<String> in){
    	//判断表名存在  且不是关键字
    	String SQL=new String();
    	if(in.size()==5){
    		if(in.get(4).equals(";")){
    			System.out.println("Select success");
    			return "00";
    		}else{
    			System.out.println("Select error: unknown error ");
    			SQL="99";
    			return SQL;
    		}
    	}else if(in.size()<5){
			System.out.println("Select error:incomplete select sentence");
			SQL="99";
			return SQL;
    	}else if (in.get(4).equals("where")){
    		int bracket=0;
    		int i=5;
    		ArrayList<Conditions> cons=new ArrayList<Conditions>();
    		while(i<in.size()){
    			if(in.get(i).equals(";")){
    				break;
    			}
    			if(in.get(i).equals("and")){
    				i++;
    				continue;
    			}
    			if(in.get(i).equals("(")){
    				bracket++;	
    			}
    			else if(in.get(i).equals(")")){
    				bracket++;
    				if(bracket<0){
    					System.out.println("Select error: ( and ) are not right;"); 
        				return "99";
    				}
    			}
    			else{
    				//System.out.println(in.size()+i);
    				if(i+2<in.size()){
    					if(!KeyWord.isKeyword(in.get(i)) && !KeyWord.isKeyword(in.get(i+2)) && KeyWord.isOp(in.get(i+1)) ){
    						cons.add(new Conditions(in.get(i),in.get(i+1),in.get(i+2)));
    						System.out.println("chenggong");
    						i=i+3;
    						if(in.get(i).equals("and")){
    							i++;
    							continue;
    						}else if(in.get(i).equals(";")){
    							break;
    						}
    						
    					}else if(i+4<in.size()){
    						System.out.println(!KeyWord.isKeyword(in.get(i)));
    						System.out.println(!KeyWord.isKeyword(in.get(i+3) ));
    						System.out.println(KeyWord.isOp(in.get(i+1)));
    						System.out.println(!in.get(i+2).equals("\'"));
    						if(!KeyWord.isKeyword(in.get(i)) && !KeyWord.isKeyword(in.get(i+3)) && KeyWord.isOp(in.get(i+1))&& in.get(i+2).equals("\'") && in.get(i+4).equals("\'") ){
        						cons.add(new Conditions(in.get(i),in.get(i+1),in.get(i+3)));
        						System.out.println("chenggong");
        						i=i+5;
        						if(in.get(i).equals("and")){
        							i++;
        							continue;
        						}else if(in.get(i).equals(";")){
        							break;
        						}
        					}else{
        						System.out.println("Select error: one or more conditions are not complete;"); 
            					return "99";
        					}
    					}else{
    						System.out.println("Select error: one or more conditions are not complete;"); 
        					return "99";
    					}
    				}else{
    					System.out.println("Select error: one or more conditions are not complete;"); 
    					return "99";
    				}
    			}    		
    		i++;
    		}
    		if(bracket<0){
    			System.out.println("Select error: ( and ) are not right;"); 
				return "99";
    		}else{
    			System.out.println("条件被提取成功");
    			return "00";
    		}
    		
    	}
    	return "99";
    }
    
    //========exeeeee=================
    public static String execfile_clause(ArrayList<String> in){
    	//for every sentence input_classify();
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
		}else if(words.get(0).equals("delete")) {
			System.out.println("Delete error: 'from' was missing");
			SQL="99";
		}
		else if(words.get(0).equals("execfile")){
			SQL=execfile_clause(words);
		}
		else if(words.get(0).equals("select")&& words.get(1).equals("*")&& words.get(2).equals("from")){
			SQL=select_clause(words);
		}
		else if(words.get(0).equals("select")&& words.get(1).equals("*")){
			System.out.println("Select error: 'from' was missing");
			SQL="99";
		}
		else if(words.get(0).equals("select")&& words.get(2).equals("from")){
			System.out.println("Delete error: '*' was missing");
			SQL="99";
		}
		else {
			SQL="99";
			System.out.println("sorry, I don't know what you mean");
		}
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
//delete from a where a = 3 and b = 5 ;