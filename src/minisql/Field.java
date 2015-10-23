package minisql;

import java.util.ArrayList;

public class Field {
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
	public String setAll(ArrayList<String> words){
		int i;
		if(words.size()<=2){
			System.out.println("not enough words ");
			return "99";
		}
		if(!words.get(1).equals("char") && !words.get(1).equals("int") && !words.get(1).equals("float")){
			System.out.println("please specify the type of all fields (only int char float are supported)");
			return "99";
		}
		if(words.size()<=6){
			if( words.get(1).equals("char") && words.get(2).equals("(") && words.get(4).equals(")") ){
				setType("char");
				setLen(Integer.parseInt(words.get(3)));
				setName(words.get(0));
				if(words.size()==6){
					if(words.get(5).equals(unique)){
						this.unique=true;
					}
					else{
						System.out.println("I cannot know your meaning, do you mean unique");
						return "99";
					}
				}
				return "99";
			}
			if(words.get(1).equals("int")){
				setType("int");
				setName(words.get(0));
				if(words.size()==3){
					if(words.get(2).equals(unique)){
						this.unique=true;
					}
					else{
						System.out.println("I cannot know your meaning, do you mean unique");
						return "99";
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
						System.out.println("I cannot know your meaning, do you mean unique");
						return "99";
					}
				}
			}
		}
		return "99";
	}
}
