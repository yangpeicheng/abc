package com.ypc.abc;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 2016/11/22.
 */

public class WriteCsv {
    private static final String baseDir=android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String foldername="SensorData";
    private String [] data;
    CSVWriter writer;
    public WriteCsv(String filename){
        String filePath=baseDir+ File.separator+foldername+File.separator+filename;
        File f=new File(filePath);
        if(f.exists() && !f.isDirectory()){
            f.delete();
            try {
                writer=new CSVWriter(new FileWriter(filePath,true));
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void writeData(float[] value){
        data=new String[value.length];
        for(int i=0;i<value.length;i++){
            data[i]=String.valueOf(value[i]);
        }
        writer.writeNext(data);
    }
    public void closeFile(){
        try{
           writer.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
