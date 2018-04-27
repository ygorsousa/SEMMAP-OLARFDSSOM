package br.ygorsousa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class DataReader {
    
    /** file name */
    private String fileName;
    /** separator */
    private final String separator = ",";
    /** index of Attributes to be used */
    private int[] attributesUsed = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
        10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
    
    private ArrayList<Integer> categories;
    
    private ArrayList<String> rotulos;
    
    
    public DataReader(String fileName) {
        this.fileName = fileName;
        this.categories = new ArrayList<Integer>();
        this.rotulos = new ArrayList<String>();
    }

    public void setAttributesUsed(int[] attributesUsed) {
        this.attributesUsed = attributesUsed;
    }
    
    public int getNumAttributes() {
	return this.attributesUsed.length;
    }
    
     public ArrayList<double[]> read() {
        File file = new File(fileName);

        BufferedReader in = null;
        ArrayList<double[]> registros = new ArrayList<double[]>();
        if (file.exists()) {
            try {
                in = new BufferedReader(new FileReader(file));

                String line = in.readLine();
                int count = 0;
                while (line != null) {
                    String fileType = getFileType();
                    if (fileType.equals("arff")) {
                        if (!line.contains("@")) {
                            readRecord(registros, line);
                        } else if (line.contains("@attribute")){
                            String[] aux = line.trim().split(" ");
                            String rotulo = aux[1].trim();
                            if(!rotulo.equals("category")) rotulos.add(rotulo);
                        }
                    } else if (fileType.equals("csv")) {
                        if (count>0){
                            readRecord(registros, line);
                        } else {
                            String[] aux = line.trim().split(",");
                            for (int i = 0; i<aux.length; i++){
                                String rotulo = aux[i].trim();
                                if(!rotulo.equals("category")) rotulos.add(rotulo);
                            }
                        }
                    }
                    line = in.readLine();
                    count++;
                }

            } catch (FileNotFoundException ignored) {
            } catch (IOException e) {
                System.out.println("Error occurred while reading file: "
                        + file + " " + e.getMessage());
            }
        } else {
            return null;
        }

        return registros;
    }
     
     
    private void readRecord(ArrayList<double[]> records,
                            String line) {
        String[] strRecord = line.split(separator);
        double[] record = new double[attributesUsed.length];
        for (int i = 0; i < attributesUsed.length; i++) {
            try {
            	record[i] = Double.parseDouble(strRecord[attributesUsed[i]].trim());
            } catch (NumberFormatException e) {
                System.out.println("Error, reading line "+ line);
                return;
            }
        }
        this.categories.add(Integer.parseInt(strRecord[strRecord.length-1].trim()));
        records.add(record);
    }
    
    
    private String getFileType(){
        String[] fileAux = fileName.split("\\.");
        int tam = fileAux.length;
        return fileAux[tam-1];
    }

    public ArrayList<Integer> getCategories() {
        return this.categories;
    }
    
    public ArrayList<String> getRotulos() {
        return this.rotulos;
    }
}
