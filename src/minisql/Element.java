/*over*/
package minisql;

public class Element {
    int type;  //type of element
    int i_element;
    float f_element;
    String s_element;

    public Element(int E){
    	this.i_element = E;
    	type = 1;
    }
    public Element(float E){
    	this.f_element = E;
    	type = 2;
    }
    public Element(String E) {
    	this.s_element = E;
    	type = 3;
    }
    public int compareTo(Element E){
    	if(type == 1)
    		return this.i_element-E.i_element;
    	else if(type == 2)
    		return (int)(this.f_element-E.f_element);
    	else
    		return s_element.compareTo(E.s_element);	
    }
    public int length(){
    	if(type == 1)
    		return 4;  //int
    	else if(type == 2)
    		return 4;  //float
    	else
    		return s_element.length() * 2;  //string
    }   
}