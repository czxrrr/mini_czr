package minisql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class split_word {
	/*
	 * this method aims to split sentence to words to analyze its meaning
	 * we will ignore all the things after the first ";",
	 * so we cannot allow two sentences in the same line
	 * one or two spaces or enter will split the sentences
	 * 
	 */
	public static ArrayList<String> my_split(String in){
		ArrayList<String> out = new ArrayList<String>();
		int i=0;
		int len=in.length();
		boolean in_string=false;
		for(i=0;i<len;i++){
			if(in.charAt(i)=='\''){
				in_string=!in_string;
			}
			if(in.charAt(i)==' ' && in_string){
				//把 ‘’ 内的字符串中的空格 替换成@，以免被后面给切分了
				in=in.substring(0,i)+"@"+in.substring(i+1,len);
			}
		}
		i=0;
		while(i<len){
			if(i<len-1 &&(in.substring(i,i+2).equals("<>")||in.substring(i,i+2).equals("<=")||in.substring(i,i+2).equals(">="))){
				in=in.substring(0,i)+" "+in.substring(i,i+2)+" "+in.substring(i+2,len);
				len=len+2;
				i++;
				i++;
				
			}else if(in.charAt(i)==';'||in.charAt(i)=='('||in.charAt(i)==')'||in.charAt(i)==','||in.charAt(i)=='\''||in.charAt(i)=='*'
					||in.charAt(i)=='='||in.charAt(i)=='<'||in.charAt(i)=='>'){
				in=in.substring(0,i)+" "+in.substring(i,i+1)+" "+in.substring(i+1,len);
				len=len+2;
				i++;
			}
			i++;
		}
		for (String temp:in.split("\\s+")){
			out.add(temp);
			//System.out.println(temp);
			if(temp.equals(";")){
				break;
			}
			
		}
		return out;
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
			ArrayList<String> words = my_split(in.toLowerCase());
			System.out.println(words);
			in = scan.nextLine();
		}
	}

}
