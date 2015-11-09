package minisql;

import java.io.*;
import java.util.*;

public class IndexManager {
	BufferManager buffer;
	
	public IndexManager(BufferManager buf){
		buffer = buf;
	}
	public boolean c_index(String tableName, ArrayList<Field> fields){
		int type = 0;
		long count;//��¼��Ŀ
		long blk_num; //�����Ŀ
		int record_num; //ÿ����ļ�¼
		int length; //ÿ����¼�ĳ���
    	int attrecord_number=0;
    	try{
    		RandomAccessFile i_file =   //�����ļ�
    			new RandomAccessFile(new File(tableName),"rws");  /*interpreter.java*/
    		i_file.writeLong(0);
    		for(int i = 0; i < fields.size(); ++i){            /*interpreter.java*/
    			if(tableName.equalsIgnoreCase(fields.get(i).getName())){
    				attrecord_number = i;
    				break;
    			}
    		}
    		int ii=9;
    		//i_file.write(fields.get(ii).getType());
    		i_file.write(fields.get(ii).getLen()/2);
    		i_file.writeLong(0);
    		i_file.writeInt(0);
    		i_file.writeLong(0);
    		i_file.writeLong(1024);
    		i_file.close();
    		RandomAccessFile r_file = new RandomAccessFile(new File(tableName),"r");  //��¼�ļ�
    		count = r_file.readLong();
    		blk_num= r_file.readLong();
    		record_num = r_file.readInt();
    		length = r_file.readInt();
    		r_file.close();
    	}catch(Exception e){
    		return false;
    	}
    	Btree btree = new Btree(tableName, buffer);
    	int current = 0;
    	int offset = 1;
    	long pos = 0;
    	for(int i=0;i<attrecord_number;i++)
    		offset = offset+1+fields.get(i).getLen();
        //type = inter.atttype[attrecord_number];
        File recordfile = new File(tableName);
        if(type == 1){
        	int key;
        	for(int i=0;i<blk_num-1;i++){
        		buffer.GetBlock(recordfile, i, 1024);
        		Block b = buffer.Lst.getFirst();
        		current = 0;
        		for(int j= 0;j<record_num ; j++){
        			b.search_pos(current);
        		    if(!b.readBoolean()){
        		    	current = current + length;
        		    	continue;
        		    }
        			b.search_pos(current+offset+1);
        			key = b.readInt();
        			pos = i*4096+1024+current;
        			btree.btInsert(new Element(key),pos);
        			current = current + length;
        		}
            }
        	if(blk_num==0)
            	return true;
            buffer.GetBlock(recordfile,blk_num-1,1024);
            Block b = buffer.Lst.getFirst();
            int rest = (int)(count - (blk_num-1)*record_num);
            current = 0;
        	for(int j= 0;j<rest ; j++){
        		b.search_pos(current);
        		if(!b.readBoolean()){
        		    current = current + length;
        		    continue;
        		}  
        		b.search_pos(current+offset+1);
        		key = b.readInt();
        		pos = (blk_num-1)*4096+1024+current;
        		btree.btInsert(new Element(key),pos);
        		current = current + length;
        	}
        }
        else if(type == 2){
        	float key;
        	for(int i=0;i<blk_num-1;i++){
        		buffer.GetBlock(recordfile, i, 1024);
        		Block b = buffer.Lst.getFirst();
        		current = 0;
        		for(int j= 0;j<record_num ; j++){
        			b.search_pos(current);
        		    if(!b.readBoolean()){
        		    	current = current + length;
        		    	continue;
        		    }  
        			b.search_pos(current+offset+1);
        			key = b.readFloat();
        			pos = i*4096+1024+current;
        			btree.btInsert(new Element(key),pos);
        			current = current + length;
        		}
            }
        	if(blk_num==0)
            	return true;
            buffer.GetBlock(recordfile,blk_num-1,1024);
            Block b = buffer.Lst.getFirst();
            int rest = (int)(count - (blk_num-1)*record_num);
            current = 0;
        	for(int j= 0;j<rest ; j++){
        		b.search_pos(current);
        		if(!b.readBoolean()){
        		    current = current + length;
        		    continue;
        		}  
        		b.search_pos(current+offset+1);
        		key = b.readFloat();
        		pos = (blk_num-1)*4096+1024+current;
        		btree.btInsert(new Element(key),pos);
        		current = current + length;
        	}
        }
        else if(type == 3){
        	String key=null;
        	for(int i=0;i<blk_num-1;i++){
        		buffer.GetBlock(recordfile, i, 1024);
        		Block b = buffer.Lst.getFirst();
        		current = 0;
        		for(int j= 0;j<record_num; j++){
        			b.search_pos(current);
        		    if(!b.readBoolean()){
        		    	current = current + length;
        		    	continue;
        		    }  
        			b.search_pos(current+offset);
        			int len = b.read()*2;
        			byte[] name = new byte[len];
        			b.read(name);
        			try{
        		        key = new String(name, "UTF16");
        	      	}catch(Exception e){}
        			pos = i*4096+1024+current;
        			btree.btInsert(new Element(key),pos);
        			current = current + length;
        		}
            }
            if(blk_num==0)
            	return true;
            buffer.GetBlock(recordfile,blk_num-1,1024);
            Block b = buffer.Lst.getFirst();
            int rest = (int)(count - (blk_num-1)*record_num);
            current = 0;
        	for(int j= 0;j<rest ; j++){
        		b.search_pos(current);
        		if(!b.readBoolean()){
        		    current = current + length;
        		    continue;
        		}  
        	    b.search_pos(current+offset);
        	    int len = b.read()*2;
        		byte[] name = new byte[len];
        		b.read(name);
        		try{
        		    key = new String(name, "UTF16");
        		}catch(Exception e){}
        		pos = (blk_num-1)*4096+1024+current;
        		btree.btInsert(new Element(key),pos);
        		current = current + length;
        	}
        }
    	return true;
	}
	public boolean in_index(String indexfile,Element e, long pos){
		Btree btree = new Btree(indexfile, buffer);
        return btree.btInsert(e, pos);
    }

	public long findPos(String indexfile, Element e){
	    	Btree btree = new Btree(indexfile, buffer);
	    	return btree.btFind(e);
	    }
	public long findMinPos(String indexfile){
	    	Btree btree = new Btree(indexfile, buffer);
	    	return btree.btFindMinAddr();
	    }
	public LinkedList<Long> findPosition(String indexfile, Element e1, Element e2){
	    	Btree btree = new Btree(indexfile, buffer);
	    	return btree.findBetween(e1, e2);
	    }
	public LinkedList<Long> findLessPos(String indexfile, Element e){
	    	Btree btree = new Btree(indexfile, buffer);
	    	return btree.findLess(e);
	    }
	public LinkedList<Long> findMorePos(String indexfile, Element e){
	    	Btree bte = new Btree(indexfile, buffer);
	    	return bte.findMore(e);
	    }
	public boolean delete(String index, String key, int type){
		Element E=null;
		if(type == 1) //to int
			E = new Element(Integer.parseInt(key));
		else if(type == 2)  //to float
	    	E = new Element(Float.parseFloat(key));
		else
	    	E = new Element(key);  //to string
	    Btree btree = new Btree(index, buffer);
	    return btree.btDelete(E);
	    }
}
