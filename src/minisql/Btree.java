package minisql;

import java.io.*;
import java.util.*;

public class Btree {
	String table;  //table name
	String index;  //index name
	String atrib;  //attribute name
	int length;    //the length of attribute
	int type;      //attribute byte
	long count;    //�����ֵ��Ŀ
	
	int height;    //height of tree
	long root_ads;  //position of root node��ڵ��ַ
	long node_num;  //the number of nodes
	long null_pointer; //�սڵ�ָ��
	
	BufferManager buffer;  /*�鿴BufferManager.java*/
	
	public Btree(String index_name, BufferManager buffer_name){
		index = index_name;  //����
		buffer = buffer_name;
		try{
			long c = 0;
			int length;
			byte[] name;
		    RandomAccessFile indexlog = new RandomAccessFile(new File("indexlog.log"),"rws");
		    //����ȡ
		    while(c < indexlog.length()){
		    	indexlog.seek(c);
		    	length = indexlog.read();
		    	if(length == 0){
		    		c += 1533;
		    		continue;
		    	}
		    	name = new byte[length * 2];
		    	indexlog.read(name);
		    	if(index.equalsIgnoreCase(new String(name, "UTF16")))  //
		    		break;
		    	c += 1533; 
		    }
		    if(c > indexlog.length()){
		    	indexlog.close();
		    }
		    else{
		    	indexlog.seek(c+511);
		    	length = indexlog.read();
		    	name = new byte[length * 2];
		    	indexlog.read(name);
		    	 table = new String(name, "UTF16");
	    	        indexlog.seek(c+1022);
	    	        length = indexlog.read();
	    	        name = new byte[length*2];
	    	        indexlog.read(name);
	    	        atrib = new String(name, "UTF16");
	    	        RandomAccessFile indexfile = new RandomAccessFile(new File(index),"rws");
	    	        count = indexfile.readLong();
	    	        type = indexfile.read();
	    	        this.length = indexfile.read()*2;
	    	        root_ads = indexfile.readLong();
	    	        height = indexfile.readInt();
	    	        node_num = indexfile.readLong();
	    	        null_pointer = indexfile.readLong();
	    	        indexfile.close();
		    }
		}
		catch(Exception e){
    		System.out.println("ERROR:can not build B plus tree");
    	}
	}
	public Bnode btCreateNode(boolean ileaf, int type, int length, long parent){
	    	byte[] temp = new byte[4096];
	    	Block bl = new Block(index, (null_pointer-1024)/4096, 1024 , temp);
	    	buffer.Lst.addFirst(bl);
	    	Bnode n = new Bnode(ileaf, type, length, parent, bl);
	    	node_num++;
	    	null_pointer += 4096;
	    	
	    	if(null_pointer == 5120)
	    		n.position.add(new Long(-1));
	    	
	    	n.update();
	    	return n;
	    	}
	public boolean btInsert(Element E, long P){
		Bnode L;
    	if(root_ads == 0){
    		L = btCreateNode(true, type, length, 0);
    		height++;
    		root_ads = L.get_pos();
    	}
    	else{
    		L = btInsertfind(E);
    		if(L.find(E) >= 0){
    			System.out.println("ERROR:it has been exists");
    			return false;
    		}
    	}
    	if(L.index_no < L.max){
    		L.insert(E,P);
    	}
    	else{
    		Bnode newnode = btCreateNode(true, type, length, L.parent);
    		node_num++;
    		L.insert(E,P);
            int half = (L.index_no+1)/2;
            for(int i=half; i <= L.max; ++i){
            	newnode.position.add(L.position.get(i));
            	newnode.key.add(L.key.get(i));
            	newnode.index_no++;
            }
            newnode.position.add(L.position.get(L.max+1));
            for(int i=half;i<=L.max; i++){
            	L.position.removeLast();
            	L.key.removeLast();
            	L.index_no--;
            }
            L.position.removeLast();
            L.position.addLast(new Long(newnode.get_pos()));
            Element minK = newnode.key.getFirst();
            L.update();
            newnode.update();
            btInsertParent(L,minK,newnode);
    	}
    	count++;
    	btUpdate();
    	return true;
    }
	public boolean btDelete(Element K){
	    	Bnode N = btInsertfind(K);
	    	if(N.find(K)<0)
	    		return false;
	    	btDeleteEntry(N,K);
	    	count--;
	    	btUpdate();
	    	return true;
	    }
	public Bnode btInsertfind(Element E) {
	    	Bnode n = null;
	    	try{
	    	    buffer.GetBlock(new File(index),(root_ads-1024)/4096 , 1024);
	    	    n = new Bnode(buffer.Lst.getFirst());
	    	    long pos = 0;
	            while(!n.iLeaf){
	        	    pos = n.position.get(n.findChild(E)).longValue();
	        	    buffer.GetBlock(new File(index),(pos-1024)/4096 , 1024);
	    	        n = new Bnode(buffer.Lst.getFirst());
	            }
	        }catch(Exception e){
	        	System.out.println("wrong search");
	        }
	        return n;
	    }
	public LinkedList<Long> findLess(Element K){
		Bnode n = null;
    	try{
    		buffer.GetBlock(new File(index), (root_ads-1024)/4096, 1024);
    		n = new Bnode(buffer.Lst.getFirst());
    		long pos = 0;
    		while(!n.iLeaf){
    			pos = n.position.getFirst().longValue();
    			buffer.GetBlock(new File(index), (pos-1024)/4096, 1024);
    			n = new Bnode(buffer.Lst.getFirst());
    		}
    	}
    	catch(Exception e){
    		System.out.println("wrong search");
    	}
    	LinkedList<Long> list = new LinkedList<Long>();
    	Bnode b = btInsertfind(K);
    	int m = b.findLess(K);
    	while(b.blk.blk_no != n.blk.blk_no){
    		for(int i = 0; i < n.index_no; i++)
    			list.add(n.position.get(i));
    		buffer.GetBlock(new File(index), (n.position.getLast().longValue()-1024)/4096, 1024);
    		n = new Bnode(buffer.Lst.getFirst());
    	}
    	for(int j = 0; j < m; j++){
    		list.add(b.position.get(j));
    	}
    	return list;
	}
	public LinkedList<Long> findMore(Element K){
		Bnode n = null;
    	try{
    		buffer.GetBlock(new File(index), (root_ads-1024)/4096, 1024);
    		n = new Bnode(buffer.Lst.getFirst());
    		long pos = 0;
    		while(!n.iLeaf){
    			pos = n.position.getFirst().longValue();
    			buffer.GetBlock(new File(index), (pos-1024)/4096, 1024);
    			n = new Bnode(buffer.Lst.getFirst());
    		}
    	}
    	catch(Exception e){
    		System.out.println("wrong search");
    	}
    	LinkedList<Long> list = new LinkedList<Long>();
    	Bnode d = btInsertfind(K);
    	int m = d.findLess(K);
    	while(d.blk.blk_no != n.blk.blk_no){
    		for(int i = 0; i < n.index_no; i++)
    			list.add(n.position.get(i));
    		buffer.GetBlock(new File(index), (n.position.getLast().longValue()-1024)/4096, 1024);
    		n = new Bnode(buffer.Lst.getFirst());
    	}
    	for(int j = 0; j < m; j++){
    		list.add(d.position.get(j));
    	}
    	return list;
	}
    public LinkedList<Long> findBetween(Element K1, Element K2){  
    	if(K1.compareTo(K2)>0){
    		Element Tmp = K1;
    		K1 = K2;
    		K2 = Tmp;
    	}
    	Bnode n1 = btInsertfind(K1);
    	Bnode n2 = btInsertfind(K2);
    	int p1 = n1.findMore(K1);
    	int p2 = n2.findLess(K2);
    	LinkedList<Long> list = new LinkedList<Long>();
    	int p = p1;
        while(n1.blk.blk_no!=n2.blk.blk_no){
    	    for(int i=p;i<n1.index_no;i++)
    	    	list.add(n1.position.get(i));
    	    buffer.GetBlock(new File(index), (n1.position.getLast().longValue()-1024)/4096, 1024);
    	    n1 = new Bnode(buffer.Lst.getFirst());
        }
        for(int i = 0; i < p2; i++)
        	list.add(n2.position.get(i));
        return list;
    }
    public long btFindMinAddr(){
    	Bnode n = null;
    	try{
    		buffer.GetBlock(new File(index), (root_ads-1024)/4096, 1024);
    		n = new Bnode(buffer.Lst.getFirst());
    		long pos = 0;
    		while(!n.iLeaf){
    			pos = n.position.getFirst().longValue();
    			buffer.GetBlock(new File(index), (pos-1024)/4096, 1024);
    			n = new Bnode(buffer.Lst.getFirst());
    		}
    	}
    	catch(Exception e){
    		System.out.println("wrong search");
    	}
    	return n.position.getFirst().longValue();
    }
    public long btFind(Element E){
    	Bnode b = btInsertfind(E);
    	long a;
    	if(b==null)
    		return -1;
    	if(b.find(E)< 0)
    	   a = -1;
    	else
    	   a = b.position.get(b.find(E)).longValue();
    	return a;
    }
    public void btUpdate(){
    	try{
    	    RandomAccessFile indexfile = new RandomAccessFile(new File(index),"rws");
    	    indexfile.writeLong(count);
    	    indexfile.write(type);
    	    indexfile.write(length/2);
    	    indexfile.writeLong(root_ads);
    	    indexfile.writeInt(height);
    	    indexfile.writeLong(node_num);
    	    indexfile.writeLong(null_pointer);
    	    indexfile.close();
    	}catch(Exception e){
    		System.out.println("error: B plus tree fail to update!!!");
    	}
    }  
    public Bnode btGetNode(long pos){
    	Bnode b = null;
    	try{
    	    buffer.GetBlock(new File(index),(pos-1024)/4096 , 1024);
    	    b = new Bnode(buffer.Lst.getFirst());
    	}catch(Exception e){
    		System.out.println("Error:fail to fetch block");
    	}
    	return b;
    }
    public void btDeleteEntry(Bnode N, Element E){
    	N.delete(E);
    	if(N.get_pos()==root_ads&&N.index_no==0){
    		root_ads = N.position.get(0);
    		N.position.removeFirst();
    		N.update();
    		btUpdate();
    		height--;
    	}
    	else if(N.index_no+1<(N.max+2)/2&&N.get_pos()!=root_ads){
    		Bnode parent = btGetNode(N.parent);
    		node_num--;
    		int add = parent.find(N.get_pos());
    		Bnode brotherN;
    		Element newE;
    		if(add>0){
    			brotherN = btGetNode(parent.position.get(add-1));
    			newE = parent.key.get(add-1);
    		}
    		else{
    			brotherN = btGetNode(parent.position.get(add+1));
    			newE = parent.key.get(add);
    		}
    		if(N.index_no+brotherN.index_no<= N.max){
    			if(add==0){
    				Bnode temp = brotherN;
    			    brotherN = N;
    			    N = temp;
    			}
    			if(!N.iLeaf){
    				brotherN.key.addLast(newE);
    				brotherN.index_no++;
    				for(int i = 0;i < N.index_no ;i++){
    					brotherN.position.addLast(N.position.get(i));
    					Bnode tempnode = btGetNode(brotherN.position.getLast().longValue());
    					tempnode.parent = brotherN.get_pos();
    					tempnode.update();
    					brotherN.key.addLast(N.key.get(i));
    					brotherN.index_no++;
    				}
    				brotherN.position.addLast(N.position.get(N.index_no));
    				Bnode tempnode = btGetNode(brotherN.position.getLast().longValue());
    				tempnode.parent = brotherN.get_pos();
    				tempnode.update();
    				brotherN.update();
    			}
    			else{
    				for(int i = 0;i < N.index_no ;i++){
    					brotherN.position.add(brotherN.index_no, N.position.get(i));
    					brotherN.key.addLast(N.key.get(i));
    					brotherN.index_no++;
    				}
    				brotherN.position.set(brotherN.index_no, N.position.get(N.index_no));	
    				brotherN.update();
    			}
    			if(add==0){
    				parent.position.set(add+1,parent.position.get(add));
    			}
    			else{
    				parent.position.set(add,parent.position.get(add-1));
    			}
    			brotherN.update();
    			parent.update();
    			btDeleteEntry(parent, newE);
    		}
    		else{
    			if(add>0){
    				if(!N.iLeaf){
    					Long Pm = brotherN.position.getLast();
    					Element Km_1= brotherN.key.getLast();
    					brotherN.position.removeLast();
    					brotherN.key.removeLast();
    					brotherN.index_no--;
    					N.position.addFirst(Pm);
    					N.key.addFirst(newE);
    					N.index_no++;
    					Bnode tempNode = btGetNode(Pm.longValue());
    					tempNode.parent = N.get_pos();
    					tempNode.update();
    					parent.key.set(parent.find(newE),Km_1);
    					N.update();
    					brotherN.update();
    					parent.update();
    				}
    				else{
    					Element Km_1 = brotherN.key.getLast();
    					Long Pm_1 = brotherN.position.get(brotherN.index_no-1);
    					brotherN.position.remove(brotherN.index_no-1);
    					brotherN.key.removeLast();
    					brotherN.index_no--;
    				    N.position.addFirst(Pm_1);
    				    N.key.addFirst(Km_1);
    				    N.index_no++;
    				    parent.key.set(parent.find(newE),Km_1);
    				    parent.update();
    				    N.update();
    				    brotherN.update();
    				}
    			}
    			else{
    				if(!N.iLeaf){
    					Long Pm = brotherN.position.getFirst();
    					Element Km_1= brotherN.key.getFirst();
    					brotherN.position.removeFirst();
    					brotherN.key.removeFirst();
    					brotherN.index_no--;
    					N.position.addLast(Pm);
    					N.key.addLast(newE);
    					N.index_no++;
    					Bnode tempNode = btGetNode(Pm.longValue());
    					tempNode.parent = N.get_pos();
    					tempNode.update();
    					parent.key.set(parent.find(newE),Km_1);
    					N.update();
    					brotherN.update();
    					parent.update();
    				}
    				else{
    					Element Km_1 = brotherN.key.getFirst();
    					Long Pm_1 = brotherN.position.getFirst();
    					brotherN.position.removeFirst();
    					brotherN.key.removeFirst();
    					brotherN.index_no--;
    				    N.position.add(N.index_no, Pm_1);
    				    N.key.addLast(Km_1);
    				    N.index_no++;
    				    parent.key.set(parent.find(newE),brotherN.key.getFirst());
    				    parent.update();
    				    N.update();
    				    brotherN.update();
    				}
    			}
    		}
    	}
    }
    public void btInsertParent(Bnode N, Element E, Bnode newN){
    	if(N.get_pos() == root_ads){
    		Bnode rootNode = btCreateNode(false, type, length, 0);
    		rootNode.position.add(N.get_pos() );
    		rootNode.position.add(newN.get_pos());
    		rootNode.key.add(E);
    		rootNode.index_no++;
    		height++;
    		node_num++;
    		rootNode.update();
    		N.parent = newN.parent = rootNode.get_pos();
    		N.update();
    		newN.update();
    		root_ads = rootNode.get_pos();
    		return;
    	}
        Bnode P = btGetNode(N.parent);
        if(P.index_no<P.max){
        	P.insert(E,newN.get_pos(),true);
        	P.update();
        }
        else{
        	P.insert(E,newN.get_pos(),true);
        	int half = (P.max+1)/2;
        	Bnode newT = btCreateNode(false,type,length,P.parent);
        	node_num++;
        	for(int i=half;i<=P.max ; i++){
        		newT.position.add(P.position.get(i));
        		newT.key.add(P.key.get(i));
        		newT.index_no++;
        	}
        	newT.position.add(P.position.get(P.max+1));
        	for(int i=half; i<=P.max ; i++){
        		P.position.removeLast();
        		P.key.removeLast();
        		P.index_no--;
        	}
        	P.position.removeLast();
        	Element newE = P.key.getLast();
        	P.key.removeLast();
        	P.index_no--;
        	P.update();
        	newT.update();
        	for(int i=0;i<=newT.index_no;i++){
        		Bnode b = btGetNode(newT.position.get(i));
        		b.parent = newT.get_pos();
        		b.update();
        	}
        	btInsertParent(P,newE,newT);
        }
    }
}
