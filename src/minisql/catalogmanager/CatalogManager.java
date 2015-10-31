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

    public Response createTable(String tableName, ArrayList<Field> fields, int primaryKeyPosition) throws IOException {
        if (isTableExist(tableName)) {
            return new Response(false, "table has existed!");
        }
        File catalogFile = new File("CatalogInfo");
        BufferedWriter bufferedReader = new BufferedWriter(new FileWriter(catalogFile, true));

        String newTable = tableName;
        for (Field field : fields) {
            /*
            cataloginfo ∏Ò Ω:
            tablename,fieldname/type/length/unique,......,0/k(primary key)
             */
            String newField = field.getName() + "/" + field.getType() + "/" + field.getLen() + "/" + (field.getUnique() ? "1" : "0");
            newTable = newTable + "," + newField;
        }
        newTable = newTable + "0/" + primaryKeyPosition;

        bufferedReader.write(newTable + "\n");

        bufferedReader.close();

        return new Response(true);

    }

    public boolean isTableExist(String tableName) throws IOException {
        File file = new File("CatalogInfo");
        if (!file.exists())
            file.createNewFile();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String temp, items[];
        while ((temp = bufferedReader.readLine()) != null) {
            items = temp.split(",");
            if (items[0].equals(tableName)) {
                bufferedReader.close();
                return true;
            }
        }
        bufferedReader.close();
        return false;
    }

    public Response dropTable(String tableName) throws IOException {
        if (!isTableExist(tableName)) {
            return new Response(false, "table not exists!");
        }
        File catalogFile = new File("CatalogInfo");
        File tempFile = new File("CatalogInfoTemp");

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(catalogFile));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String tbName = line.split(",")[0];
            if (tbName.equals(tableName)) {
                continue;
            }
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();

        tempFile.renameTo(catalogFile);

        return new Response(true);
    }

    public Response createIndex(String tableName, String fieldName, String indexName) throws IOException {
        if (isIndexExist(tableName, fieldName)) {
            return new Response(false, "Index has existed!");
        }


        File indexInfo = new File("CatalogIndexInfo");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(indexInfo, true));

        bufferedWriter.write(tableName + "," + fieldName + "," + indexName);
        bufferedWriter.newLine();
        bufferedWriter.close();
        return new Response(true);
    }

    public boolean isIndexExist(String tableName, String fieldName) throws IOException {
        File indexInfo = new File("CatalogIndexInfo");
        if (!indexInfo.exists())
            indexInfo.createNewFile();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(indexInfo));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String items[] = line.split(",");
            if (items[0].equals(tableName) && items[1].equals(fieldName)) {
                bufferedReader.close();
                return true;
            }
        }
        bufferedReader.close();
        return false;
    }

    public boolean isIndexExist(String indexName) throws IOException {
        File indexInfo = new File("CatalogIndexInfo");
        if (!indexInfo.exists())
            indexInfo.createNewFile();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(indexInfo));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String items[] = line.split(",");
            if (items[2].equals(indexName)) {
                bufferedReader.close();
                return true;
            }
        }
        bufferedReader.close();
        return false;
    }

    public Response dropIndex(String indexName) throws IOException {
        if (!isTableExist(indexName)) {
            return new Response(false, "index not exists!");
        }
        File catalogFile = new File("CatalogIndexInfo");
        File tempFile = new File("CatalogIndexInfoTemp");

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(catalogFile));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String indexName2 = line.split(",")[2];
            if (indexName2.equals(indexName)) {
                continue;
            }
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();

        tempFile.renameTo(catalogFile);

        return new Response(true);
    }

    public ArrayList<Field> readTableFields(String tableName) throws IOException {
        if (!isTableExist(tableName)) {
            return null;
        }
        File catalogFile = new File("CatalogInfo");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(catalogFile));

        ArrayList<Field> fields = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String tbName = line.split(",")[0];
            if (tbName.equals(tableName)) {
                String tableInfo[] = line.split(",");
                for (int i = 1; i < tableInfo.length; ) {
                    Field field = new Field();
                    field.setName(tableInfo[i]);
                    field.setType(tableInfo[i + 1]);
                    //fieldname/type/length/unique
                    field.setLen(Integer.parseInt(tableInfo[i + 2]));
                    field.setUnique((tableInfo[i + 3].equals("1")));
                    fields.add(field);
                    i = i + 4;
                }

            }
        }

        bufferedReader.close();

        return fields;
    }


}
