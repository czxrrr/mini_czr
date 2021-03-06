package minisql;

import minisql.Conditions;
import minisql.Field;
import minisql.Response;

import java.io.*;
import java.util.ArrayList;

public class RecordManagerV3{
	public RecordManagerV3() {

    }
	
	
	public static void printRecord(ArrayList<Field> fields, ArrayList<Record> records)throws IOException {
		if(fields==null){
			System.out.println("no records");
			return;
		}
		for(Field i:fields)
		{
			System.out.print(i.getName());
			System.out.print("\t");
		}
		System.out.print("\n");
		for(Record i:records)
		{
			for(String j:i.attr)
			{
				System.out.print(j);
				System.out.print("\t");
			}
			System.out.print("\n");
		}
		
	}
	
	
	public static boolean compareRecord(ArrayList<Field> fields,Record record,Conditions con)throws IOException {
		
		int row=-1;
		int ii;
		for(ii=0;ii<fields.size();ii++){
			if(fields.get(ii).getName().equals(con.field.getName())){
				row=ii;
				break;
			}
		}
		if (row ==-1){
			return false;
		}
		String s1,s2,op;
		int comp=0;
		s1=record.attr.get(row);
		s2=con.value;
		op=con.op;
		if(con.field.getType().equals("int"))
		{
			comp=Integer.parseInt(s1)-Integer.parseInt(s2);
		}
		else if(con.field.getType().equals("float"))
		{
			comp=(int)(Float.parseFloat(s1)-Float.parseFloat(s2));
		}
		else if(con.field.getType().equals("char"))
		{
			comp=s1.compareTo(s2);
		}
		
		switch(op)
		{
			case "=":
			{
				return comp==0;
			}
			case "<>":
			{
				return comp!=0;
			}
			case "<":
			{
				return comp<0;
			}
			case ">":
			{
				return comp>0;
			}
			case "<=":
			{
				return comp<=0;
			}
			case ">=":
			{
				return comp>=0;
			}
			default:
			{
				return false;
			}
		}
	}
	
	
	public static Response selectRecord(String tableName, ArrayList<Field> fields, ArrayList<Conditions> cons) throws IOException {
		
		ArrayList<Record> records=RecordManagerV2.getRecord(tableName,fields);
		if (records==null){
			return new Response(false,"file was not found");
		}
		ArrayList<Record> result=new ArrayList<Record>();
		if(records!=null){
		for(Record i:records)
		{
			boolean flag=true;
			if(cons!=null)
			for(Conditions j:cons)
			{
				if(!compareRecord(fields,i,j))
				{
					flag=false;
					break;
				}
			}
			if(flag==true)
			{
				result.add(i);
			}
		}}
		if(result.size()==0)
		{
			System.out.println("no records was found ");
		}
		else 
		{
			System.out.println(result.size()+" records was selected");
			printRecord(fields,result);
		}
		
		return new Response(true,"insert "+result.size());
		
	}
	
	
	@SuppressWarnings("resource")
	public static Response deleteRecord(String tableName, ArrayList<Field> fields, ArrayList<Conditions> cons) throws IOException {
		
		ArrayList<Record> records=RecordManagerV2.getRecord(tableName,fields);
		if (records==null){
			return new Response(false,"error:file was not found");
		}
		File tempFile = new File(tableName+"Temp");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
		int cnt=0;

		for(Record i:records)
		{
			boolean flag=true;
			if(cons!=null){
				for(Conditions j:cons)
				{
					if(!compareRecord(fields,i,j))
					{
						flag=false;
						break;
					}
				}
			}

			if(flag==true)
			{
				cnt++;
			}
			else
			{
				String newRecord= new String();
				for(String j:i.attr)
				{
					newRecord = newRecord + j + ",";
				}
				newRecord = newRecord + "\n";
				
				bw.write(newRecord);
			}
		}
		bw.close();
		tempFile.renameTo(new File(tableName));
		
		if(cnt==0)
		{
			//System.out.println("no record was deleted");
		}
		else
		{
			//System.out.println(cnt+"record was deleted");
		}
		
		return new Response(true,cnt +"record(s) were deleted");
		
	}
	public static void main(String[] args) throws IOException{
		selectRecord("ab",CatalogManager.readTableFields("bb"),null);
		
	}

}