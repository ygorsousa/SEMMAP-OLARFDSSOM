package br.ygorsousa.util;

import java.util.Random;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class GeneralUtils {
    
    public static String removeExtension(String fileName) {
        int dotPos = fileName.lastIndexOf(".");
        return fileName.substring(0, dotPos);
    }
    
    public static void embaralhar(String[] v) {
	Random random = new Random();
		
	for (int i=0; i < (v.length - 1); i++) {

            //sorteia um índice
            int j = random.nextInt(v.length); 
			
            //troca o conteúdo dos índices i e j do vetor
            String temp = v[i];
            v[i] = v[j];
            v[j] = temp;
	}	
    }   
    
}
