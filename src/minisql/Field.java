package minisql;

import java.util.ArrayList;

public class Field {
	//你们只需要 使用带  getXXX ，其他的是我自己用的，你们不用管
	private String type;
	private int len=0;
	private boolean unique=false;
	private String name;
	public void setType(String type){
		this.type=type;
	}
	public void setLen(int len){
		this.len=len;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setUnique(boolean unique){
		this.unique=unique;
	}
	
	
	public String getType(){
		return this.type;
	}
	public String getName(){
		return this.name;
	}
	public boolean getUnique(){
		return this.unique;
	}
	public int getLen(){
		return this.len;
	}
	public Response setAll(ArrayList<String> words){
		int i;
		if(words.size()<2){
			return new Response(false,"no field name or type");
		}
		else if(!words.get(1).equals("char") && !words.get(1).equals("int") && !words.get(1).equals("float")){
			return new Response(false,"please specify the type of all fields (only int char float are supported)");
		}
		else if(words.size()<=6){
			if( words.get(1).equals("char") && words.get(2).equals("(") && words.get(4).equals(")") ){
				setType("char");
				setLen(Integer.parseInt(words.get(3)));
				setName(words.get(0));
				if(words.size()==6){
					if(words.get(5).equals(unique)){
						this.unique=true;
					}
					else{
						return new Response(false,"I cannot know your meaning, do you mean unique");
					}
				}
				return new Response(false,"I cannot know your meaning, do you mean unique");
			}
			if(words.get(1).equals("int")){
				setType("int");
				setName(words.get(0));
				if(words.size()==3){
					if(words.get(2).equals(unique)){
						this.unique=true;
					}
					else{
						return new Response(false,"I cannot know your meaning, do you mean unique");
					}
				}
			}
			if(words.get(1).equals("float")){
				setType("float");
				setName(words.get(0));
				if(words.size()==3){
					if(words.get(2).equals(unique)){
						this.unique=true;
					}
					else{
						return new Response(false,"I cannot know your meaning, do you mean unique");
					}
				}
			}
		}else{
			return new Response(false,"too many words, I know understand what you mean in the field "+words.get(0));
		}
		return new Response(false,"some thing wrong has happened in the field"+words.get(0));
	}

	public Field(String type,int len,String name,boolean unique){
		this.type = type;
		this.len = len;
		this.name = name;
		this.unique = unique;
	}
	public Field(){

	}

}
