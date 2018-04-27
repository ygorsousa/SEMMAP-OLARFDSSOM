package br.ygorsousa;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class SEMMAP {
    
    private double maxNodeNumber;
    private double eb;
    private double at; // Limiar de ativação
    private double limiarSoma;
    
    private ArrayList<double[]> data;
    private int numAttributes;
    private Grafo grafo;
    private ArrayList<String> rotulos;
    private ArrayList<VerticeMap> vertices;
    private VerticeMap ultimoVencedor;
    private OLARFDSSOM olarfdssom;
    private boolean treinarCategorizador;
    
    private Graph visualizationGraph; // Objeto utilizado para visualizar o grafo
    
    public SEMMAP(double maxNodeNumber, double eb, double at, ArrayList<double[]> data, int numAttributes, double limiarSoma, ArrayList<String> rotulos, OLARFDSSOM olarfdssom){
        this.maxNodeNumber = maxNodeNumber;
        this.eb = eb;
        this.at = at;
        this.data = data;
        this.numAttributes = numAttributes;
        this.limiarSoma = limiarSoma;
        this.rotulos = rotulos;
        this.olarfdssom = olarfdssom;
        inicializarGrafo();
        this.treinarCategorizador = true;
        
        // Objeto de visualização do Grafo
        visualizationGraph = new MultiGraph("NEWSOM-Graph");
    }
    
    private void inicializarGrafo(){
        grafo = new Grafo();
        VerticeMap vertice = this.novoVertice(data.get(0));
        grafo.inserirVertice(vertice);
        ultimoVencedor = vertice; 
    }
    
     
    private VerticeMap novoVertice(double[] padrao){
        double[] coordenadas = new double[2];
        int tam = coordenadas.length;
        double[] objetos = new double[padrao.length - tam];
        double[] somaObjetos = new double[padrao.length - tam];
                
        for(int i=0; i<padrao.length; i++){
            if(i<tam){
                coordenadas[i] = padrao[i];
            } else {
                somaObjetos[i - tam] = padrao[i];
                objetos[i - tam] = this.calcularLog(somaObjetos[i - tam]);
            }
        }
        
        VerticeMap vertice = new VerticeMap(coordenadas, objetos, somaObjetos);
        if(this.vertices==null){
            this.vertices = new ArrayList<VerticeMap>();
        }
        this.vertices.add(vertice);
        return vertice;
    }
    
    public void executar(){
        for(int i=1; i<data.size(); i++){
            double[] padrao = data.get(i);
            VerticeMap vertice = this.calcularNodoVencedor(padrao);
            double melhorAtivacao = this.calcularAtivacao(vertice, padrao);
            if(melhorAtivacao<this.at && this.vertices.size()<this.maxNodeNumber){
                //Adiciona novo nodo
                VerticeMap novoVertice = this.novoVertice(padrao);
                grafo.inserirVertice(novoVertice);
                grafo.inserirAresta(novoVertice, ultimoVencedor);
                if (treinarCategorizador) olarfdssom.categorizar(ultimoVencedor);
                ultimoVencedor = novoVertice;
                
            } else if (melhorAtivacao>=this.at){
                //Atualiza nodo vencedor
                this.atualizarNodo(vertice, padrao);
                
                if (vertice!=ultimoVencedor){
                    grafo.inserirAresta(vertice, ultimoVencedor);
                    if (treinarCategorizador) olarfdssom.categorizar(ultimoVencedor);
                }
                ultimoVencedor = vertice;
            }
        }
    }
    
    
    private void atualizarNodo(VerticeMap vertice, double[] padrao){
        // - Atualização Coordenadas
        double[] coordenadas = vertice.getCoordenadas();
        
        for(int i=0; i<coordenadas.length; i++){
            coordenadas[i] = coordenadas[i] + this.eb * (padrao[i] - coordenadas[i]);
        }
        vertice.setCoordenadas(coordenadas);
        
        // - Atualização Soma Objetos
        int tam = coordenadas.length;
        double[] somaObjetos = vertice.getSomaObjetos();
        for (int i = 0; i < somaObjetos.length; i++) {
            somaObjetos[i] = somaObjetos[i] + padrao[i+tam];
            if (somaObjetos[i]>this.limiarSoma) somaObjetos[i] = this.limiarSoma;
        }
        vertice.setSomaObjetos(somaObjetos);
        
        // - Atualização objetos
        double[] objetos = vertice.getObjetos();
        for (int i = 0; i < objetos.length; i++) {
            objetos[i] = this.calcularLog(somaObjetos[i]);
        }
        vertice.setObjetos(objetos);
    }
    
    private VerticeMap calcularNodoVencedor(double[] padrao){
        VerticeMap vertice = null;
        double maiorAtivacao = -1;
        for(int i = 0; i< vertices.size(); i++){
            VerticeMap vAux = vertices.get(i);
            double ativacao = this.calcularAtivacao(vAux, padrao);
            if(ativacao>maiorAtivacao){
                maiorAtivacao = ativacao;
                vertice = vAux;
            }
        }
        return vertice;
    }
    
    private double calcularAtivacao(VerticeMap vertice, double[] padrao){
        
        double dist = this.calcularDistancia(vertice, padrao);
        return (double)1.0/(double)(1.0 + dist);
    }
    
    private double norma(double[] vetor){
        double sum = 0.0;
        for (int i = 0; i < vetor.length; i++){
            sum = sum + (double)(vetor[i] * vetor[i]);
        }
        return (double)Math.sqrt(sum);
    }
    
    private double calcularDistancia (VerticeMap vertice, double[] padrao){
        double dist = 0;
        double[] coordenadas = vertice.getCoordenadas();

        for (int i = 0; i < coordenadas.length; i++) {
            dist = dist + (double)((padrao[i] - coordenadas[i])*(padrao[i] - coordenadas[i]));
        }
        return (double)dist;
    }
    
    private double calcularLog(double valor){
        return (double)(Math.log(1 + valor)/Math.log(this.limiarSoma + 1));
    }

    public double getMaxNodeNumber() {
        return maxNodeNumber;
    }

    public void setMaxNodeNumber(double maxNodeNumber) {
        this.maxNodeNumber = maxNodeNumber;
    }

    public double getEb() {
        return eb;
    }

    public void setEb(double eb) {
        this.eb = eb;
    }

    public double getAt() {
        return at;
    }

    public void setAt(double at) {
        this.at = at;
    }

    public ArrayList<double[]> getData() {
        return data;
    }

    public void setData(ArrayList<double[]> data) {
        this.data = data;
    }

    public int getNumAttributes() {
        return numAttributes;
    }

    public void setNumAttributes(int numAttributes) {
        this.numAttributes = numAttributes;
    }

    public ArrayList<VerticeMap> getVertices() {
        return vertices;
    }
    
    public boolean getTreinarCategorizador() {
        return this.treinarCategorizador;
    }

    public void setTreinarCategorizador(boolean treinarCategorizador) {
        this.treinarCategorizador = treinarCategorizador;
    }

    public double getLimiarSoma() {
        return limiarSoma;
    }

    public void setLimiarSoma(double limiarSoma) {
        this.limiarSoma = limiarSoma;
    }
    
    public ArrayList<String> getRotulos() {
        return rotulos;
    }

    public void setRotulos(ArrayList<String> rotulos) {
        this.rotulos = rotulos;
    }
    
    public void exportarVerticesFinaisCSV(String path){
        String delimitador = ",";
        String novaLinha = "\n";
        
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path);
            
            for (int i=2; i<rotulos.size(); i++){
                fileWriter.append(rotulos.get(i));
                if (i==(rotulos.size()-1)) {
                    fileWriter.append(delimitador);
                    fileWriter.append("category");
                }
                else fileWriter.append(delimitador);
            }
            fileWriter.append(novaLinha);
            
            for (int i=0; i<vertices.size(); i++){
                VerticeMap vertice = vertices.get(i);
                double[] objetos = vertice.getObjetos();
                for (int j=0; j<objetos.length; j++){
                    //DecimalFormat formatter = new DecimalFormat("#.########", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                    //formatter.setRoundingMode(RoundingMode.DOWN);
                    //String s = formatter.format(objetos[j]);
                    
                    fileWriter.append(String.valueOf(objetos[j]));
                    if (j==(objetos.length-1)) {
                        fileWriter.append(delimitador);
                        fileWriter.append("0");
                    }
                    else fileWriter.append(delimitador);
                }
                fileWriter.append(novaLinha);
            }
            
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error while flushing/closing fileWriter !!!");
            e.printStackTrace();
        } catch (Exception e){
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        }
        
    }
    
    public void atualizarDesenhoGrafo(boolean colorido){
        ArrayList<ArrayList> listasAdjacencias = grafo.getListasAdjacencias();
        ArrayList<VerticeSOM> categorias = new ArrayList();
        HashMap<VerticeSOM, String> hashMap = new HashMap();
        for (int i=0; i<vertices.size(); i++){
            VerticeMap vertice = vertices.get(i);
            olarfdssom.atualizarCategoria(vertice);
            VerticeSOM categoria = vertice.getCategoria();
            if(!categorias.contains(categoria)){
                categorias.add(categoria);
            }
        }
        float nCategorias = categorias.size();
        float fator = 1.0f/nCategorias;
        float m = 0;
        for (int i = 0; i < listasAdjacencias.size(); i++) {
            ArrayList<Adjacencia> adjacencias = listasAdjacencias.get(i);
            Adjacencia adjacencia = adjacencias.get(0);
            VerticeMap vertice = (VerticeMap) adjacencia.getVerticeFim();
            VerticeSOM categoria = vertice.getCategoria();
            String cor = hashMap.get(categoria);
            if(cor==null){
                Color c = Color.getHSBColor(m, 0.9f, 0.9f);
                cor = "fill-color: rgb("+c.getRed()+","+c.getGreen()+","+c.getBlue()+");";
                hashMap.put(categoria, cor);
                m = m + fator;
            }
            Node no;
            if (visualizationGraph.getNode(String.valueOf(i))==null){
                visualizationGraph.addNode(String.valueOf(i));
            }
            no = visualizationGraph.getNode(String.valueOf(i));
            //if(no.getAttribute("ui.label")==null) no.addAttribute("ui.label", String.valueOf(i));
            if(no.getAttribute("x")==null) no.addAttribute("x", vertice.getCoordenadas()[0]);
            else no.changeAttribute("x", vertice.getCoordenadas()[0]);
            if(no.getAttribute("y")==null) no.addAttribute("y", vertice.getCoordenadas()[1]);
            else no.changeAttribute("y", vertice.getCoordenadas()[1]);
            if(no.getAttribute("centro")==null) no.addAttribute("centro", vertice);
            else no.changeAttribute("centro", vertice);
            if(colorido){
                if(no.getAttribute("ui.style")==null) no.addAttribute("ui.style", cor);
                else no.changeAttribute("ui.style", cor);
            } else {
                cor = "fill-color: rgb(0,0,250);";
                if(no.getAttribute("ui.style")==null) no.addAttribute("ui.style", cor);
                else no.changeAttribute("ui.style", cor);
            }
        }
        for (int i = 0; i < listasAdjacencias.size(); i++) {
            ArrayList<Adjacencia> adjacencias = listasAdjacencias.get(i);
            for (int j=1; j<adjacencias.size(); j++){
                Adjacencia adjacencia = adjacencias.get(j);
                Vertice vertice = adjacencia.getVerticeFim();
                int posicaoVertice = -1;
                for (int h = 0; h < listasAdjacencias.size() && posicaoVertice==-1; h++) {
                    ArrayList<Adjacencia> adjacencias2 = listasAdjacencias.get(h);
                    Adjacencia adja = adjacencias2.get(0);
                    Vertice ver = adja.getVerticeFim();
                    if(vertice == ver){
                        posicaoVertice = h;
                    }
                }
                if(posicaoVertice!=-1 && visualizationGraph.getEdge(""+i+posicaoVertice+"")==null 
                        && visualizationGraph.getEdge(""+posicaoVertice+i+"")==null) {
                    visualizationGraph.addEdge(""+i+posicaoVertice+"", String.valueOf(i), 
                            String.valueOf(posicaoVertice));
                }
            }
        }
    }
    
    public void desenharGrafo(boolean colorido){
        Viewer viewer = visualizationGraph.display();
        viewer.disableAutoLayout();
        atualizarDesenhoGrafo(colorido);
    }
    
    public void desenharGrafo(String path, boolean colorido){
        Viewer viewer = visualizationGraph.display();
        viewer.disableAutoLayout();
        atualizarDesenhoGrafo(colorido);
        visualizationGraph.addAttribute("ui.screenshot", path);
    }
    
    
}
