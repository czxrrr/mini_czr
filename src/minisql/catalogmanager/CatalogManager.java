package minisql.catalogmanager;

import minisql.Field;
import minisql.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUBIN on 2015/10/23.
 */
public class CatalogManager {
    public CatalogManager() {

    }

    public static Response createTable(String tableName, ArrayList<Field> fields, int primaryKeyPosition) throws IOException {
        if (isTableExist(tableName)) {
            return new Response(false, "table has existed!");
        }
        File catalogFile = new File("CatalogInfo");
        BufferedWriter bufferedReader = new BufferedWriter(new FileWriter(catalogFile, true));

        String newTable = tableName;
        for (Field field : fields) {
            //表的信息的格式：tablename,fieldname/type/length/unique,......,fieldname/type/length/unique,k(k is primary key)
            String newField = field.getName() + "/" + field.getType() + "/" + field.getLen() + "/" + (field.getUnique() ? "1" : "0");
            newTable = newTable + "," + newField;
        }
        newTable = newTable + "," + primaryKeyPosition;

        bufferedReader.write(newTable + "\n");

        bufferedReader.close();

        return new Response(true);

    }

    public static boolean isTableExist(String tableName) throws IOException {
        File file = new File("CatalogInfo");
        if (!file.exists())
            file.createNewFile();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line, tbname;
        while ((line = bufferedReader.readLine()) != null) {
            //表的信息的格式：tablename,fieldname/type/length/unique,......,fieldname/type/length/unique,k(k is primary key)
            tbname = line.split(",")[0];
            if (tbname.equals(tableName)) {
                bufferedReader.close();
                return true;
            }
        }
        bufferedReader.close();
        return false;
    }

    public static Response dropTable(String tableName) throws IOException {
        if (!isTableExist(tableName)) {
            return new Response(false, "table not exists!");
        }
        File catalogFile = new File("CatalogInfo");
        File tempFile = new File("CatalogInfoTemp");

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(catalogFile));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            //tablename,fieldname/type/length/unique,......,fieldname/type/length/unique,k(k is primary key)
            String tbName = line.split(",")[0];
            if (tbName.equals(tableName)) {
                continue;   //消去一行table记录
            }
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();

        tempFile.renameTo(catalogFile);

        return new Response(true);
    }

    public static boolean isFieldExist(String tableName,String fieldName) throws IOException {
        File file = new File("CatalogInfo");
        if (!file.exists())
            file.createNewFile();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String temp, items[];
        while ((temp = bufferedReader.readLine()) != null) {
            //表的信息的格式：tablename,fieldname/type/length/unique,......,fieldname/type/length/unique,k(k is primary key)
            items = temp.split(",");
            if (items[0].equals(tableName)) {

                for (int i = 1; i < items.length-1; i = i+1) {
                    String fieldInfo[] = items[i].split("/");
                    if (fieldInfo[0].equals(fieldName)){
                        bufferedReader.close();
                        return true;
                    }
                }
            }
        }
        bufferedReader.close();
        return false;
    }

    public static Response createIndex(String tableName, String fieldName, String indexName) throws IOException {
        if (isIndexExist(tableName, fieldName)|isIndexExist(indexName)) {
            return new Response(false, "Index has existed!");
        }

        if (!isFieldExist(tableName,fieldName)){
            return new Response(false,"Field doesn't exist!");
        }

        if (!isFieldUnique(tableName,fieldName)){
            return new Response(false,"Field isn't unique!");
        }

        File indexInfo = new File("CatalogIndexInfo");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(indexInfo, true));

        bufferedWriter.write(tableName + "," + fieldName + "," + indexName);
        bufferedWriter.newLine();
        bufferedWriter.close();
        return new Response(true);
    }

    private static boolean isFieldUnique(String tableName,String fieldName) throws IOException {

        if (isFieldExist(tableName,fieldName)) {

            ArrayList<Field> fields = readTableFields(tableName);
            for (Field field : fields) {
                if (field.getName().equals(fieldName)) {
                    return field.getUnique();
                }
            }
        }
        return false;
    }

    public static boolean isIndexExist(String tableName, String fieldName) throws IOException {
        File indexFile = new File("CatalogIndexInfo");
        if (!indexFile.exists())
            indexFile.createNewFile();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(indexFile));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            //表的信息的格式：tablename,fieldname,indexname
            String indexInfo[] = line.split(",");
            if (indexInfo[0].equals(tableName) && indexInfo[1].equals(fieldName)) {
                bufferedReader.close();
                return true;
            }
        }
        bufferedReader.close();
        return false;
    }

    public static boolean isIndexExist(String indexName) throws IOException {
        File indexFile = new File("CatalogIndexInfo");
        if (!indexFile.exists())
            indexFile.createNewFile();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(indexFile));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            //表的信息的格式：tablename,fieldname,indexname
            String indexInfo[] = line.split(",");
            if (indexInfo[2].equals(indexName)) {
                bufferedReader.close();
                return true;
            }
        }
        bufferedReader.close();
        return false;
    }

    public static Response dropIndex(String indexName) throws IOException {
        if (!isIndexExist(indexName)) {
            return new Response(false, "index not exists!");
        }
        File catalogFile = new File("CatalogIndexInfo");
        File tempFile = new File("CatalogIndexInfoTemp");

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(catalogFile));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            //表的信息的格式：tablename,fieldname,indexname
            String indexName2 = line.split(",")[2];
            if (indexName2.equals(indexName)) {
                continue;   //消去一行index记录
            }
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();

        tempFile.renameTo(catalogFile);

        return new Response(true);
    }

    public static ArrayList<Field> readTableFields(String tableName) throws IOException {
        if (!isTableExist(tableName)) {
            return null;
        }
        File catalogFile = new File("CatalogInfo");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(catalogFile));

        ArrayList<Field> fields = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            //表的信息的格式：tablename,fieldname/type/length/unique,......,fieldname/type/length/unique,k(k is primary key)
            String tbName = line.split(",")[0];
            if (tbName.equals(tableName)) {
                String tableInfo[] = line.split(",");
                for (int i = 1; i < tableInfo.length-1; i++) {
                    //每个field的格式：fieldname/type/length/unique
                    String fieldInfo[] = tableInfo[i].split("/");

                    Field field = new Field();
                    field.setName(fieldInfo[0]);
                    field.setType(fieldInfo[1]);
                    field.setLen(Integer.parseInt(fieldInfo[2]));
                    field.setUnique((fieldInfo[3].equals("1")));
                    fields.add(field);
                }

            }
        }

        bufferedReader.close();

        return fields;
    }

    public static void main(String args[]) throws IOException {
        Field field = new Field("char",8,"name",false);
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(field);

        //CatalogManager.createTable("hello",fields,0);
        System.out.print(CatalogManager.isTableExist("hello"));

        System.out.print(CatalogManager.isIndexExist("myindex"));
        System.out.print(isFieldExist("hello","name"));
                CatalogManager.createIndex("hello", "name", "myindex");
        System.out.print(CatalogManager.isIndexExist("myindex"));
    }
}




