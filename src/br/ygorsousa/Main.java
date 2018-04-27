package br.ygorsousa;

import java.util.ArrayList;
import br.ygorsousa.util.DataReader;
import br.ygorsousa.util.GeneralUtils;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class Main {
    
    public static void main(String[] args) {
        double maxNodeNumberL = 10000;
        
        double ebL = 0.011776426;
        double enL = 0.007630646;
        double dsbetaL = 0.016263271;
        double epsilondsL = 0.078087203;   
        //double cL = 0.127992075;
        //double cL = 0.0163;
        double cL = 0.030168021;
        double maxcompL = 34.616710667;
        double lpL = 0.191412335;
        double atL = 0.987982820; // Limiar de ativação
        
        OLARFDSSOM olarfdssom = new OLARFDSSOM(maxNodeNumberL, cL, ebL, enL, dsbetaL, epsilondsL, maxcompL, lpL, atL);
        
        ArrayList<VerticeMap> verticesTotal = new ArrayList();
        ArrayList<Integer> categoriasTotal = new ArrayList();
        
        String[] fileNamesTodos = {"path_cold_freiburg_1_cloudy_1_20_semCTG.arff", "path_cold_freiburg_1_night_1_20_semCTG.arff", "path_cold_freiburg_1_night_3_20_semCTG.arff", "path_cold_freiburg_2_cloudy_1_20_semCTG.arff", "path_cold_freiburg_2_cloudy_2_20_semCTG.arff", "path_cold_freiburg_2_cloudy_3_20_semCTG.arff", "path_cold_freiburg_2_night_1_20_semCTG.arff", "path_cold_freiburg_2_night_2_20_semCTG.arff", "path_cold_freiburg_2_night_3_20_semCTG.arff", "path_cold_saarbrucken_1_cloudy_1_20_semCTG.arff", "path_cold_saarbrucken_1_cloudy_2_20_semCTG.arff", "path_cold_saarbrucken_1_cloudy_3_20_semCTG.arff", "path_cold_saarbrucken_2_cloudy_1_20_semCTG.arff", "path_cold_saarbrucken_2_cloudy_2_20_semCTG.arff", "path_cold_saarbrucken_2_cloudy_3_20_semCTG.arff", "path_cold_saarbrucken_2_night_1_20_semCTG.arff", "path_cold_saarbrucken_2_night_2_20_semCTG.arff", "path_cold_saarbrucken_2_night_3_20_semCTG.arff", "path_cold_saarbrucken_3_cloudy_1_20_semCTG.arff", "path_cold_saarbrucken_3_cloudy_2_20_semCTG.arff", "path_cold_saarbrucken_3_cloudy_3_20_semCTG.arff", "path_cold_saarbrucken_4_cloudy_1_20_semCTG.arff", "path_cold_saarbrucken_4_cloudy_2_20_semCTG.arff", "path_cold_saarbrucken_4_cloudy_3_20_semCTG.arff"};        
        String[] fileNamesTodos3 = {"path_cold_freiburg_1_cloudy_1_20_semCTG.arff", "path_cold_freiburg_1_night_1_20_semCTG.arff", "path_cold_freiburg_1_night_3_20_semCTG.arff", "path_cold_freiburg_2_cloudy_1_20_semCTG.arff", "path_cold_freiburg_2_cloudy_2_20_semCTG.arff", "path_cold_freiburg_2_cloudy_3_20_semCTG.arff", "path_cold_saarbrucken_1_cloudy_1_20_semCTG.arff", "path_cold_saarbrucken_1_cloudy_2_20_semCTG.arff", "path_cold_saarbrucken_1_cloudy_3_20_semCTG.arff", "path_cold_saarbrucken_2_cloudy_1_20_semCTG.arff", "path_cold_saarbrucken_2_cloudy_2_20_semCTG.arff", "path_cold_saarbrucken_2_cloudy_3_20_semCTG.arff", "path_cold_saarbrucken_3_cloudy_1_20_semCTG.arff", "path_cold_saarbrucken_3_cloudy_2_20_semCTG.arff", "path_cold_saarbrucken_3_cloudy_3_20_semCTG.arff", "path_cold_saarbrucken_4_cloudy_1_20_semCTG.arff", "path_cold_saarbrucken_4_cloudy_2_20_semCTG.arff", "path_cold_saarbrucken_4_cloudy_3_20_semCTG.arff"};
        
        String path = "/home/ygor/Databases";
        String pathAll = path + "/COLD/CSV-Doff/ARFF/";
        String pathTrueVertices = path + "/COLD/CSV-Vertices-Doff-ORIG-SIM/ARFF/";
        
        String[] fileNames = fileNamesTodos3;
        GeneralUtils.embaralhar(fileNames);
        int ctgSize = 11;
        
        for (int i=0; i<fileNames.length; i++){
            // Realiza leitura do ground truth dos vertices
            DataReader dataReader = new DataReader(pathTrueVertices + fileNames[i].replace("path", "vertices"));
            int[] attributesUsed = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
            dataReader.setAttributesUsed(attributesUsed);
            dataReader.read();
            categoriasTotal.addAll(dataReader.getCategories());
            //-- 
            
            dataReader = new DataReader(pathAll + fileNames[i]);
            
            
            double maxNodeNumber = 10000;
            double eb = 0.013933267;  
            double at = 0.553947076;
            double limiarSoma = (double)Math.round(4.942991505);
            
            SEMMAP semMap = new SEMMAP(maxNodeNumber, eb, at, dataReader.read(), 
                    dataReader.getNumAttributes(), limiarSoma, dataReader.getRotulos(), olarfdssom);
            
            
            semMap.executar();
            //semMap.exportarVerticesFinaisCSV(path +"/"+GeneralUtils.removeExtension(fileNames[i]).replace("path", "vertices")+".csv");
            semMap.desenharGrafo(path + "/" +GeneralUtils.removeExtension(fileNames[i]).replace("path", "vertices")+".jpg", true);
            
            // Imprime as coordenadas dos vertices
            ArrayList<VerticeMap> vertices = semMap.getVertices();
            System.out.println("\nCoordenadas "+(i+1)+" - "+fileNames[i]);
            System.out.println("Nº de Vertices:" + vertices.size());
            for (int j=0; j<vertices.size(); j++){
                System.out.println(j+" "+vertices.get(j).getCoordenadas()[0]+", "+vertices.get(j).getCoordenadas()[1]);
            }
            //--
            verticesTotal.addAll(vertices);
        }
        
        olarfdssom.atualizarCategorias(verticesTotal);
        olarfdssom.escreverResultadosClusters(verticesTotal, path + "/COLD/ARFF/", "vertices_cold_20_semCTG.arff");
        olarfdssom.imprimirMatrizConfusao(ctgSize, categoriasTotal, verticesTotal);
        
        System.out.println("- Finished -");
    }
}
