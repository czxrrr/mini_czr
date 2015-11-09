package minisql;

import java.util.*;

public class Bnode {
	int index_no;  //the number of index
	int type;  //type of entry
	int length; //length of entry
	int max;
	
	boolean iLeaf; // CLARIFY if it is a leaf
	long parent; //parent of node
	
	Block blk; /*Block.java*/
	
	LinkedList<Element> key;  
	LinkedList<Long> position; // the address in file for every entry
     
	public Bnode(Block block){  //i代表此节点有多少个索引项，s代表此节点是否为叶节点
		blk = block;
		blk.search_pos(0); //从文件的第0个位置开始检索
		
		index_no = blk.readInt(); //读取下一个整数
		type = blk.read(); //读取类型
		length = blk.read() * 2; //读取长度
		max = 4073 / (8 + length);  
		iLeaf = blk.readBoolean();//判断是否是叶节点
		parent = blk.readLong();
		
		if(type == 1){   //int类型
			key = new LinkedList<Element>();
			position = new LinkedList<Long>();
		}
		else if(type == 2){  //float 类型
			key = new LinkedList<Element>();
			position = new LinkedList<Long>();
		}
		else if(type == 3){  //string类型
			key = new LinkedList<Element>();
			position = new LinkedList<Long>();
		}
	}
	public Bnode(boolean m, int i, int j, long p, Block b){
		this.iLeaf = m;
		this.index_no = 0;
		this.type = i;
		this.length = j;
		this.parent = p;
		this.blk = b;
		this.max = 4073 / (8 + length); 
		key = new LinkedList<Element>();
    	position = new LinkedList<Long>();
	}
	public boolean Isfull(){   //clarify it is full
	    return max <= index_no;
	}
	public long get_pos(){
		return blk.blk_no * 4096 + blk.f_head;
	}

	public void update(){
		blk.search_pos(0);//从文件的第0个位置开始检索
    	blk.writeInt(index_no);
    	blk.writeBoolean(iLeaf);
       	blk.write(type);
    	blk.write(length/2);
    	blk.writeLong(parent);
    	int tmp=index_no;
    	
    	if(max < index_no)
    		tmp = max;
    	if(type == 1){
    		for(int i = 0; i < tmp; ++i){
    			blk.writeLong(position.get(i).longValue());//int
    			blk.writeInt(key.get(i).i_element);//
    		}
    		if(tmp > 0)
    			blk.writeLong(position.get(tmp).longValue());//
    	}
    	else if(type == 2){
    		for(int i = 0; i < tmp; ++i){
    			blk.writeLong(position.get(i).longValue());//float
    			blk.writeFloat(key.get(i).f_element);
    		}
    		if(tmp > 0)
    			blk.writeLong(position.get(tmp).longValue());//
    	}
    	else if(type == 3){
    		for(int i = 0; i < tmp; ++i){                     //string
    			blk.writeLong(position.get(i).longValue());
    			blk.writeString(key.get(i).s_element);
    			int sub = length - key.get(i).s_element.length() * 2;//
    			for(int j = 0; j < sub; ++j){
    				blk.write(0);
    			}
    	    }
    		if(tmp > 0)
    			blk.writeLong(position.get(tmp).longValue());//
    	}
	blk.search_pos(0);	
	}
	public int insert(Element E, long pos){ //insert element
		int i, j;
		if(E.type != type){
			System.out.println("Error,don't match data type!!!");
		    return -1;
		}
		for(i = 0; i < index_no; ++i)  //寻找是否存在i
			if(  (j=E.compareTo(key.get(i))) < 0  ) break;
			else if(j == 0)
				return 0;
		
		position.add(i, new Long(pos)); 
		key.add(i, E);
		index_no++;
		update();
		return 1;
	}
	public int insert(Element E, long pos, boolean root){ //插入关键字
		int i, j;
		if(E.type != type){
			System.out.println("Error,don't match data type!!!");
		    return -1;
		}
		for(i = 0; i < index_no; ++i)  //寻找是否存在i
			if(  (j=E.compareTo(key.get(i))) < 0  )
				break;
			else if(j == 0)
				return 0;
		if(root)
			position.add(i+1, new Long(pos));
		else
			position.add(i, new Long(pos));
		
		key.add(i, E);
		index_no++;
		update();
		return 1;
	}    
	public int delete(Element E){
		if(E.type != type){
			System.out.println("Error,don't match data type!!!");
		    return -1;
		}
		if(key.isEmpty()){
			System.out.println("Error,null node!!!");
			return -1;
		}
		for(int i = 0; i < index_no; ++i)
			if(E.compareTo(key.get(i)) == 0){
				key.remove(i);
				position.remove(i);
				index_no--;
				update();
				return i;
			}
		System.out.println("Error,there is no such element in this node!!!");
		return -1;
	}

	public int find(Element E){
		int i;
		if(E.type != type){
			System.out.println("Error,don't match data type!!!");
		    return -1;
		}
		for(i = 0; i < index_no; ++i)
			if(E.compareTo(key.get(i)) == 0) 
				break;
		if(i == index_no) 	
			return -1;
		
		return i;	
	}
	public int find(long p){
    	int i;
		for(i = 0; i < index_no; ++i)
			if(p-position.get(i).longValue()== 0) 
				break; 
		
		if(i == (index_no + 1)){
			System.out.println("can not find data");
			return -1;
		}
		return i;	
    }
	public int findChild(Element E){
		int i;
		if(E.type != type){
			System.out.println("Error,don't match data type!!!");
		    return -1;
		}
		for(i = 0; i < index_no; ++i)
			if(E.compareTo(key.get(i)) < 0) break;
		return i;	
	}
	public int findMore(Element E){
		int i;
		if(E.type != type){
			System.out.println("Error,don't match data type!!!");
		    return -1;
		}
		for(i = 0; i < index_no; ++i)
			if(E.compareTo(key.get(i)) < 0) break;
		return i;	
	}
	public int findLess(Element E){
		int i;
		if(E.type != type){
			System.out.println("Error,don't match data type!!!");//数据类型不匹配
		    return -1;
		}
		for(i = 0; i < index_no; ++i)
			if(E.compareTo(key.get(i)) <= 0) break;
		return i;	
	}
}
