package minisql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Interpreter {
	//=========ok================
	public static void show_help(){
		System.out.println("help");
	}
	
   
    public static Response create_clause(ArrayList<String> in ) throws IOException
    {
    	if(in.size()<4){
    		return new Response(false,"please say what you want to create");
    	}
    	else if(in.get(1).equals("table")){
    		//System.out.println("in table");
    		int len=in.size();
    		int i;
    		if(!in.get(3).equals("(") || !in.get(len-2).equals(")")){
    			return new Response(false,"missing ( or )");
    		}
    		int start=4;
    		if(in.size()<6){
    			return new Response(false,"no table name or no field name");
    		}
    		ArrayList<Field> fields = new ArrayList<Field>();
    		ArrayList<String> fieldString=new ArrayList<String>();
    		String primary=null;
    		for(i=4;i<=len-2;i++){
    			if(in.get(i).equals(",")){
					Field t=new Field();
    				if(t.setAll(fieldString).isSuccess()==false){
    					return new Response(false,"fields was not specified correctly");
    				}
    				fieldString.clear();
    				fields.add(t);
    			}else if((in.get(i).equals(")")&& in.get(i+1).equals(";") )){
    				if(fieldString.size()>=2 && fieldString.get(0).equals("primary")){
    					if(fieldString.get(1).equals("key")){
    						if(in.indexOf(fieldString.get(2))!=i-1){
    							primary=fieldString.get(2);
    						}
    					}else{
    						return new Response(false,"primary was found, but no 'key' in the sentence");
    					}
    				}
    				else{
    					if(fieldString.size()>0){
    						Field t=new Field();
            				if(t.setAll(fieldString).isSuccess()==false){
            					return new Response(false,"fields was not specified correctly");
            				}
            				fieldString.clear();
            				fields.add(t);
    					}
    					
    				}
    			}
    			else{
    				fieldString.add(in.get(i));
    			}	
    		}
    		int primary_position=-1;
    		if(primary==null){
    			primary_position=-1;
    		}
    		else{
    			int j;
    			for(j=0;j<fields.size();j++){
    				if(fields.get(j).getName().equals(primary)){
    					primary_position=j;
    					fields.get(j).setUnique(true);
    				}
    			}
    		}
    		return CatalogManager.createTable(in.get(2),fields,primary_position);
    	}
    	else if(in.get(1).equals("index")){
    		return create_index_on(in);
       		
    	}
    	else{
    		return new Response(false,"please say what you want to create");
    	}
    }
    
    // done
    // create index XXX on table_name(field_name);
    public static Response create_index_on(ArrayList<String> in ) throws IOException{
    	if(in.size()<9){
    		return new Response(false, "not enough parameters");
    	}else{
    		if(in.get(3).equals("on")){
        		if(in.get(5).equals("(")&&in.get(7).equals(")")){
        			if(!KeyWord.isKeyword(in.get(2),in.get(4),in.get(6))){
        				return CatalogManager.createIndex(in.get(4), in.get(6), in.get(2));
        			}else{
        				return new Response(false, "the name of index or table or field should not be keyword");
        			}
        		}
        		else{
        			return new Response(false, "missing ( or ) or fields name");
        		}
        	}
        	else{
        		return new Response(false, "no keyword \"on\" was found");
        	}
    	}
    }
    
    public static Response insert_clause(ArrayList<String> in ) throws IOException
    {
    	
    	String SQL=new String();
    	if(in.size()<6){
    		return new Response(false,"illegal insert sentences");
    	}
    	else if(in.get(1).equals("into")&&in.get(3).equals("values")&& in.get(4).equals("(")){
    		int i;
    		ArrayList<String> values=new ArrayList<String>();
    		for(i=5;i<in.size()-1;i++){
    			if( i % 2 ==0 ){
    				if(in.get(i).equals(",")){
    					continue;
    				}
    				else{
    					if(in.get(i).equals(")") && in.get(i+1).equals(";")){
    						//System.out.println("insert "+in.get(2)+" "+values);
    						ArrayList<Field> field=CatalogManager.readTableFields(in.get(2));
    						Record rec=new Record(values);
    						return RecordManagerV2.insertRecord(in.get(2),field,rec);
    					}
    					else{
    						return new Response(false,"no ) or ; was found");
    					}
    				}
    			}else{
    				values.add(in.get(i));
    			}
    		}
    		return new Response(false,"parameters is not enough");
    	}else{
    		return new Response(false,"illegal insert sentence");
    	}
    }
   
    
    
    //===================delete================
    public static Response delete_clause(ArrayList<String> in) throws IOException{
    	String SQL=new String();
    	if(in.size()==4){
    		if(in.get(3).equals(";")){
    			ArrayList<Field> fields=CatalogManager.readTableFields(in.get(2));
    			return RecordManagerV3.deleteRecord(in.get(2), fields, null);	
    		}else{
    			return new Response(false,"Delete error: unknown error");
    		}
    	}else if(in.size()<4){
    		return new Response(false,"Delete error:incomplete delete sentence");

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
    					return new Response(false,"Delete error: ( and ) are not right;"); 
    				}
    			}
    			else{
    				//System.out.println(in.size()+i);
    				if(i+2<in.size()){
    					if(!KeyWord.isKeyword(in.get(i)) && !KeyWord.isKeyword(in.get(i+2)) && KeyWord.isOp(in.get(i+1)) ){
    						cons.add(new Conditions(in.get(2),in.get(i),in.get(i+1),in.get(i+2)));
    						//System.out.println("chenggong");
    						i=i+3;
    						if(in.get(i).equals("and")){
    							i++;
    							continue;
    						}else if(in.get(i).equals(";")){
    							break;
    						}
    						
    					}else if(i+4<in.size()){
//    						System.out.println(!KeyWord.isKeyword(in.get(i)));
//    						System.out.println(!KeyWord.isKeyword(in.get(i+3) ));
//    						System.out.println(KeyWord.isOp(in.get(i+1)));
//    						System.out.println(!in.get(i+2).equals("\'"));
    						if(!KeyWord.isKeyword(in.get(i)) && !KeyWord.isKeyword(in.get(i+3)) && KeyWord.isOp(in.get(i+1))&& in.get(i+2).equals("\'") && in.get(i+4).equals("\'") ){
        						cons.add(new Conditions(in.get(2),in.get(i),in.get(i+1),in.get(i+3)));
        						System.out.println("chenggong");
        						i=i+5;
        						if(in.get(i).equals("and")){
        							i++;
        							continue;
        						}else if(in.get(i).equals(";")){
        							break;
        						}
        					}else{
        						return new Response(false,"Delete error: one or more conditions are not complete;"); 

        					}
    					}else{
    						return new Response(false,"Delete error: one or more conditions are not complete;"); 
    					}
    				}else{
    					return new Response(false,"Delete error: one or more conditions are not complete;"); 
    				}
    			}    		
    		i++;
    		}
    		if(bracket<0){
    			return new Response(false,"Delete error: ( and ) are not right;"); 
    		}else{
    			ArrayList<Field> field=CatalogManager.readTableFields(in.get(2));
    			int ii;
    			for(Conditions c: cons){
    				boolean flag=false;
    				for(Field f:field){
    					if(c.field.getName().equals(f.getName())){
    						flag=true;
    						break;
    					}
    				}
    				if(flag==false){
    					return new Response(false,"no such field name");
    				}
    			}
    			
    			return RecordManagerV3.deleteRecord(in.get(2),field,cons);
    		}
    		
    	}
    	return new Response(false,"Delete error");

    }
    
    
    
    //===================select================
    public static Response select_clause(ArrayList<String> in) throws IOException{
    	//判断表名存在  且不是关键字
    	if(in.size()==5){
    		if(in.get(4).equals(";")){
    			ArrayList<Field> fields=CatalogManager.readTableFields(in.get(3));
    			RecordManagerV3.selectRecord(in.get(3), fields, null);
    			return new Response(true,"select success");
    		}else{
    			return new Response(false,"Select error: unknown error ");
    		}
    	}else if(in.size()<5){
    		return new Response(true,"Select error:incomplete select sentence");
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
    					return new Response(false,"Select error: ( and ) are not right;"); 
    				}
    			}
    			else{
    				//System.out.println(in.size()+i);
    				if(i+2<in.size()){
    					if(!KeyWord.isKeyword(in.get(i)) && !KeyWord.isKeyword(in.get(i+2)) && KeyWord.isOp(in.get(i+1)) ){
    						cons.add(new Conditions(in.get(3),in.get(i),in.get(i+1),in.get(i+2)));
    						//System.out.println("chenggong");
    						i=i+3;
    						if(in.get(i).equals("and")){
    							i++;
    							continue;
    						}else if(in.get(i).equals(";")){
    							break;
    						}
    						
    					}else if(i+4<in.size()){
    						//System.out.println(!KeyWord.isKeyword(in.get(i)));
    						//System.out.println(!KeyWord.isKeyword(in.get(i+3) ));
    						//System.out.println(KeyWord.isOp(in.get(i+1)));
    						//System.out.println(!in.get(i+2).equals("\'"));
    						if(!KeyWord.isKeyword(in.get(i)) && !KeyWord.isKeyword(in.get(i+3)) && KeyWord.isOp(in.get(i+1))&& in.get(i+2).equals("\'") && in.get(i+4).equals("\'") ){
        						cons.add(new Conditions(in.get(3),in.get(i),in.get(i+1),in.get(i+3)));
        						System.out.println("chenggong");
        						i=i+5;
        						if(in.get(i).equals("and")){
        							i++;
        							continue;
        						}else if(in.get(i).equals(";")){
        							break;
        						}
        					}else{
        						return new Response(false,"Select error: one or more conditions are not complete;"); 
            					
        					}
    					}else{
    						return new Response(false,"Select error: one or more conditions are not complete;"); 
 
    					}
    				}else{
    					return new Response(false,"Select error: one or more conditions are not complete;"); 
    					
    				}
    			}    		
    		i++;
    		}
    		if(bracket<0){
    			return new Response(false,"Select error: ( and ) are not right;"); 

    		}else{
    			//System.out.println("条件被提取成功");
    			ArrayList<Field> field=CatalogManager.readTableFields(in.get(3));
    			int ii;
    			for(Conditions c: cons){
    				boolean flag=false;
    				for(Field f:field){
    					if(c.field.getName().equals(f.getName())){
    						flag=true;
    						break;
    					}
    				}
    				if(flag==false){
    					return new Response(false,"no such field name");
    				}
    			}
    			return RecordManagerV3.selectRecord(in.get(3), field, cons);
    		}
    		
    	}
    	return new Response(false,"Select error :syntax error");
    }
    

    
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		String in;
		while(true){
			System.out.print(">>");
			in = scan.nextLine();
			while(in.indexOf(';')<0){	//一直读，读到有分号结束
				in = in + " " + scan.nextLine();
			}
			ArrayList<String> words = split_word.my_split(in);
			System.out.println(input_classify(words).getInfo());
		}
	}
	
	
	
	private static Response input_classify(ArrayList<String> words) throws IOException {
		if(words.size()<2){		
			return new Response(false, "not enough words in the sentence");
		}else if(words.get(0).equals("help")){
			return help_clause(words);
		}
		else if(words.get(0).equals("quit")){
			return quit_clause(words);
		}
		else if(words.get(0).equals("create")){
			return create_clause(words);
		}
		else if(words.get(0).equals("drop")){
			return drop_clause(words);
		}
		else if(words.get(0).equals("insert")){
			return insert_clause(words);
		}
		else if(words.get(0).equals("delete") && words.get(1).equals("from")){
			return delete_clause(words);
		}else if(words.get(0).equals("delete")) {
			return new Response(false,"Delete error: 'from' was missing");
		}
		else if(words.get(0).equals("execfile")){
			execfile(words);
			return new Response(true,"execfile done");
		}
		else if(words.get(0).equals("select")&& words.get(1).equals("*")&& words.get(2).equals("from")){
			return select_clause(words);
		}
		else {
			return new Response(false, "Syntax error!");
		}
	}
	
	
	//execfile
	//done 
	public static void execfile(ArrayList<String> in) throws IOException{
		if(!in.get(2).equals(";")){
			System.out.println("error:please input \" execfile filename;   \"");
		}
		File file= new File(in.get(1));
		if (!file.exists()){
			System.out.println("error:the file was not found"); 
			return;
		}
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
	    String inline;
	    while ((inline = bufferedReader.readLine()) != null) {
  			if (inline.equals("0")){
  				break;
  			}
  			while(inline.indexOf(';')<0){
  				inline = inline + " " + bufferedReader.readLine();
  			}
	  	
  			ArrayList<String> words = split_word.my_split(inline);
  			System.out.println(">>"+input_classify(words).getInfo());
	    }
      	bufferedReader.close();
	}

	//quit
	//done
	public static Response quit_clause(ArrayList<String> in )
	{
		if(in.get(1).equals(";")){
			System.out.println(">>bye bye");
			System.exit(0);
			return new Response(false, "if you want exit,please input quit;  Do not add any other words");
		}
		else{
			return new Response(false, "if you want exit,please input quit;  Do not add any other words");
		}
	}
	
	//help;
	//done
	public static Response help_clause(ArrayList<String> in )
	{
		if(in.get(1).equals(";")){
			show_help();
			return new Response(true, "help has shown");
			
		}
		else{
			return new Response(false, "if you want help,please input help;  Do not add any other words");
		}
	}
	
	
	// drop table xxx;
	// drop index xxx;
	// done
    public static Response drop_clause(ArrayList<String> in ) throws IOException
    {
    	if(in.size()<4){
    		return new Response(false, "illegal drop sentences");
    	}
    	if(in.get(1).equals("table")&&in.get(3).equals(";")){
    		if(KeyWord.isKeyword(in.get(2))){
    			return new Response(false, "table name cannot be a keyword");
    		}else{
    		return CatalogManager.dropTable(in.get(2));
    		}
    	}
    	else if(in.get(1).equals("index")&&in.get(3).equals(";")){
    		if(KeyWord.isKeyword(in.get(2))){
    			return new Response(false, "index name cannot be a keyword");
    		}else{
    		return CatalogManager.dropIndex(in.get(2));
    		}
    	}
    	else{
    		return new Response(false, "please tell what you want to drop");
    	}
    }
}
//delete from a where a = 3 and b = 5 ;