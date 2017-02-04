package com.ypc.abc;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 2016/12/26.
 */

public class ReadCsv {
    private static final String baseDir=android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String foldername="SensorData";
    private String [] data;
    CSVReader reader;
    public ReadCsv(String filename){
       // String filePath=baseDir+ File.separator+foldername+File.separator+filename;
        String filePath=baseDir+ File.separator+filename;
        File f=new File(filePath);
        if(f.exists() && !f.isDirectory()){
            try {
                reader=new CSVReader(new FileReader(f));
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public String[] readNext() {
        if (reader == null)
            return null;
        else {
            try{
                return reader.readNext();
            }
            catch (IOException e){
                return null;
            }
        }
    }
    public void closeFile(){
        try{
            reader.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
