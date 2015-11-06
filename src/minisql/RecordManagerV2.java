package minisql;

import minisql.Conditions;
import minisql.Field;
import minisql.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecordManagerV2{
	public RecordManagerV2() {

    }
	
	public static ArrayList<Record> getRecord(String tableName, ArrayList<Field> fields)throws IOException {
		File recordFile = new File(tableName);
		if (!recordFile.exists()){
			//System.out.println("error:the file was not found"); 
			return null;
		}
		BufferedReader br = new BufferedReader(new FileReader(recordFile));
		String line;
		ArrayList<Record> records=new ArrayList<Record>();
		while( (line = br.readLine()) != null)
		{
			String[] words=line.split(",");
			Record new_record=new Record();
			for(String i:words)
			{
				new_record.attr.add(i);
			}
			records.add(new_record);
		}
		br.close();
		return records;
	}
	public void printRecord(String tableName, ArrayList<Field> fields, ArrayList<Record> records)throws IOException {
		
	}
	public boolean compareRecord(Record record,Conditions con)throws IOException {
		

		return false;
	}
	
	public Response selectRecord(String tableName, ArrayList<Field> fields, ArrayList<Conditions> cons) throws IOException {
		ArrayList<Record> records=getRecord(tableName,fields);
		ArrayList<Record> result=new ArrayList<Record>();
		for(Record i:records)
		{
			boolean flag=true;
			for(Conditions j:cons)
			{
				if(!compareRecord(i,j))
				{
					flag=false;
					break;
				}
			}
			if(flag==true)
			{
				result.add(i);
			}
		}
		if(result.size()==0)
		{
			System.out.println("û�з�������ļ�¼");
		}
		else printRecord(tableName,fields,result);
		
		return new Response(true,""+result.size());
		
	}

	public static Response insertRecord(String tableName, ArrayList<Field> fields, Record rec) throws IOException {
		ArrayList<Record> records=getRecord(tableName,fields);
		boolean flag=false;
		if(records!=null){
			for(Record i:records)
			{
				for(int j=0;j<rec.attr.size();j++)
				if( fields.get(j).getUnique() && i.attr.get(j).equals(rec.attr.get(j)))
				{
					flag=true;
					break;
				}
			}
		}
		if(flag==true)
		{
			//System.out.println("insert fail, the record has exist");
			return new Response(false,"insert  fail, the record has exist"); 
		}
		else
		{
			File recordFile = new File(tableName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(recordFile, true));
			String newRecord= new String();
			for(String i:rec.attr)
			{
				newRecord = newRecord + i + ",";
			}
			newRecord = newRecord + "\n";
			
			bw.write(newRecord);
			bw.close();
			//insertIntoFile();
			//System.out.println("insert success");
			return new Response(true,"insert success");
		}
	}
}