package minisql;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BufferManager {
    LinkedList<Block> Lst;
    public BufferManager() {
    	Lst = new LinkedList<Block>();
    }
    public int GetBlock(File file, long page, long head){
     	try{
     	int inbuffer=this.isInBuffer(file,page);
     	if(inbuffer!=-1){
     	     Block temp=Lst.get(inbuffer);
     	     Lst.remove(inbuffer);
     	     Lst.addFirst(temp);
     	}
     	else{
     	long block_addr = head + page*4096;
    	RandomAccessFile temp_file = new RandomAccessFile(file, "r");
    	temp_file.seek(block_addr);
    	byte[] context = new byte[4096];
    	try{
    		temp_file.readFully(context, 0, 4096);
        }catch(EOFException e){
    		System.out.println("文件已经结尾");
    		return -1;
    	}
    	Block block = new Block(file.toString(),page, head,context);
    	Lst.addFirst(block);
    	if(Lst.size()>100)
    	{
    		for(int i=0; i<Lst.size()-100;i++){
    		    Block tempchange = Lst.get(100+i);
    		    this.resetFile(i+100,new File(tempchange.f_name),tempchange.blk_no, tempchange.f_head);
    		}
    		int j=Lst.size();
    		for(int i=0; i<j-100 ; i++){
    			Lst.removeLast();
    		}	
    	}
        temp_file.close();
        }
     	}catch(Exception e){
     		return -1;
     	}
        return 1;
        
    }
    public void deleteBlock(String table){
    	for(int i=0; i<Lst.size();i++){
    		if(Lst.get(i).f_name.equals(table)){
    			Lst.remove(i);
    			i--;
    		}
    	}
    }
    public void resetFile(int i,File file, long page, long head)
    {
    	try{
    	long block_addr = head + page*4096;
    	RandomAccessFile temp_file = new RandomAccessFile(file, "rw");
    	temp_file.seek(block_addr);
    	Block context = (Block)Lst.get(i);
    	temp_file.write(context.get_Byte(),0,4096);
    	temp_file.close();
    	}catch(Exception e){
    	}
    }
    public int isInBuffer(File file, long page) throws Exception{
    	Block temp;
    	for(int i=0; i<Lst.size(); i++)
    		if((temp=Lst.get(i)).f_name.equals(file.toString())&&temp.blk_no==page )
    			return i;
    	return -1;
    }
    public void quit(){
    	for(int i=0; i<Lst.size();i++){
    	    Block tempchange = Lst.get(i);
    	    this.resetFile(i,new File(tempchange.f_name),tempchange.blk_no, tempchange.f_head);
    	}
    	int j=Lst.size();
    	for(int i=0; i<j ; i++){
    		Lst.removeFirst();
    	}
    	System.out.print("quit successfully");
    	System.exit(0);
    }
}
