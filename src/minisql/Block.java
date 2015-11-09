package minisql;
/*over*/
class Block {
    public String f_name;   //文件名
    public long blk_no;   //　文件块号
    public long f_head;  //　文件头大小
    public byte[] block;   //　块内容，4096个字节大小
    public int pos;
    public Block() {  //block初始化
    	f_name = null;
    	blk_no = 0;
    	f_head = 0;    	
    	block = null;
    	pos = 0;
    }
    public Block(String filename, long blockno, long fileheader, byte[] ck) {
    	f_name = filename;
    	blk_no = blockno;
    	f_head = fileheader;
        block = ck;
        pos = 0;
    }
    public void search_pos(int position){
    	pos = position;
    }
    public byte[] get_Byte()
    {
    	return block;
    }
    public int read(){
    	int i = block[pos];
    	i = i&0x000000FF;  
    	pos++;
    	return i;
    }
    public void write(int a){
    	block[pos] = (byte)a;
    	pos++;
    }
    public void read(byte[] a){
    	for(int i = 0 ;i < a.length; ++i){
    		a[i] = block[pos];
    		pos++;
    	}
    }
    public void read(byte[] a,int lth){
    	for(int i = 0; i < a.length; ++i){
    		a[i] = block[pos+i];
    	}
    	pos += lth;
    }
    public void writeByte(byte a){
    	block[pos] = a;
    	pos++;
    }
    public void writeBytes(byte[] a){
    	for(int i = 0; i<a.length ; i++){
    		block[pos+i] = a[i];
    	}
    	pos += a.length;
    }
    public void readFully(byte[] b){
    	for(int i = 0; i < b.length; ++i){
    		b[i] = block[pos];
    		pos++;
    	}
    }
    public void write_o(int lth){  //写0
    	for(int i = 0; i < lth; ++i){
    		pos++;
    		block[pos] = 0;
    	} 		
    }
    public int readInt(){  //temVal
    	int a = 0;
        for(int i = 0; i < 4; ++i) {
           int tmpVal = (block[pos+i] << (8 * (3-i))); 
           switch (i) {
            case 0:
                tmpVal = tmpVal & 0xFF000000;
                break;
            case 1:
                tmpVal = tmpVal & 0x00FF0000;
                break;
            case 2:
                tmpVal = tmpVal & 0x0000FF00;
                break;
            case 3:
                tmpVal = tmpVal & 0x000000FF;
                break;
           }
           a = a | tmpVal;
        }
        pos += 4 ;
    	return a;
    }
    public void writeInt(int a){
    	block[pos]=(byte)((a&0xFF000000) >>> 24);  //无符号右移,高位补0
    	block[pos+1]=(byte)((a&0x00FF0000) >>> 16);
    	block[pos+2]=(byte)((a&0x0000FF00) >>> 8);
    	block[pos+3]=(byte)(a&0x000000FF);
    	pos += 4 ;
    }
    public float readFloat(){
    	int a = 0;
    	for(int i = 0; i < 4; ++i) {
           int tmpVal = (block[pos+i] << (8 * (3-i)));
           switch (i) {
            case 0:
                tmpVal = tmpVal & 0xFF000000;
                break;
            case 1:
                tmpVal = tmpVal & 0x00FF0000;
                break;
            case 2:
                tmpVal = tmpVal & 0x0000FF00;
                break;
            case 3:
                tmpVal = tmpVal & 0x000000FF;
                break;
           }
           a = a | tmpVal;
        }
        pos += 4 ;
    	return Float.intBitsToFloat(a);  //
    }
    public void writeFloat(float a){
    	int tmp;
    	tmp = Float.floatToRawIntBits(a);
    	for(int i = 0; i < 4; ++i){
    		block[pos+3-i] = (byte)(tmp&0xff);   
            tmp = tmp>>>8;   
    	}
    	pos += 4;
    }
    public long readLong(){
    	long a = 0;
    	for(int i = 0; i < 8; ++i, ++pos){
    		a = a<<8;  //left shift
    		a = a | (block[pos]&0xFF);
    	}
    	return a;
    }
    public void writeLong(long a){
    	for(int i = 0; i < 8; ++i){
    		block[pos-i+7]=(byte)(a&0xff);
    		a = a>>>8;
    	}
    	pos += 8;
    }
    public boolean readBoolean(){
    	if(block[pos] != 0){
    		pos++;
    		return true;
    	}
    	else{
    		pos++;
    		return false;
    	}
    }
    public void writeBoolean(boolean a){
    	if(a == false)
    		block[pos]=0;
    	else
    		block[pos]=1;
    	pos++;
    }
    public void writeString(String a){
    	byte[] b = a.getBytes();
    	for(int i = 0; i < b.length; i++){
    		block[pos+2*i] = 0;
    		block[pos+2*i+1] = b[i];
    	}
    	pos += b.length * 2;
    }
    public void writeString(String a, int lth){
    	byte[] b = a.getBytes();
    	for(int i = 0; i < b.length ;++i){
    		block[pos+2*i] = 0;
    		block[pos+2*i+1] = b[i];
    	}
    	for(int i=b.length*2; i < lth; ++i)
    		block[pos+i]=0;
    	pos += lth;
    }
}