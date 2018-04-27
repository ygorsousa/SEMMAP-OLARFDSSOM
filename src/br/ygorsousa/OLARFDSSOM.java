package br.ygorsousa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.ygorsousa.util.GeneralUtils;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class OLARFDSSOM {
    private double maxNodeNumber;
    private double c; 
    private double eb;
    private double en;
    private double dsbeta;
    private double epsilonds;    
    private double maxcomp;
    private double lp;
    private double at; // Limiar de ativação
    private int nwins;
    
    private ArrayList<double[]> data;
    private int numDimensoes;
    private Grafo grafo;
    private ArrayList<VerticeSOM> vertices;
    
    public OLARFDSSOM(double maxNodeNumber, double c, double eb, double en, double dsbeta, double epsilonds,
            double maxcomp, double lp, double at){
        this.maxNodeNumber = maxNodeNumber;
        this.c = c;
        this.eb = eb;
        this.en = en;
        this.dsbeta = dsbeta;
        this.epsilonds = epsilonds;
        this.maxcomp = maxcomp;
        this.lp = lp;
        this.at = at;
        this.nwins = 1;
        this.data = new ArrayList();
    }
    
    
    public void categorizar (VerticeMap verticeMap){
        if (verticeMap!=null){
            double[] padrao = verticeMap.getObjetos();
            int posicao = data.size();
            data.add(padrao);
            if (grafo==null){
                this.numDimensoes = verticeMap.getObjetos().length;
                grafo = new Grafo();
                VerticeSOM vertice = this.novoVertice(padrao, 0);
                //vertice.getVencedores().add(verticeMap);
                grafo.inserirVertice(vertice);
                verticeMap.setCategoria(vertice);
            } else {
                VerticeSOM vertice = this.calcularNodoVencedor(padrao);
                double melhorAtivacao = this.calcularAtivacao(vertice, padrao);
                if(melhorAtivacao<this.at && this.vertices.size()<this.maxNodeNumber){
                    //Adiciona novo nodo
                    VerticeSOM novoVertice = this.novoVertice(padrao, (int) (this.lp*nwins));
                    novoVertice.getVencedores().add(posicao);
                    this.grafo.inserirVertice(novoVertice);
                    this.atualizarConexoes(novoVertice);
                    verticeMap.setCategoria(novoVertice);
                
                } else if (melhorAtivacao>=this.at){
                    //Atualiza nodo vencedor
                    this.atualizarNodo(vertice, padrao, this.eb);
                    vertice.incrementarVitorias();
                    vertice.getVencedores().add(posicao);
                    verticeMap.setCategoria(vertice);
                
                    ArrayList<Adjacencia> vizinhos = this.grafo.getListaAdjacencia(vertice);
                    for (int j=1; j< vizinhos.size(); j++){
                        VerticeSOM vizinho = (VerticeSOM) vizinhos.get(j).getVerticeFim();
                        this.atualizarNodo(vizinho, padrao, this.en);
                    }
                }
                if (nwins >= this.maxcomp){
                    removerPerdedoresResetVitorias();
                    atualizarTodasConexoes();
                    nwins = 0;
                }
                nwins++;
            }
        }
    }
    
    public void atualizarCategoria(VerticeMap verticeMap){
        if (verticeMap!=null){
            double[] padrao = verticeMap.getObjetos();
            if (grafo!=null){
                VerticeSOM vertice = this.calcularNodoVencedor(padrao);
                verticeMap.setCategoria(vertice);
            }
            
        }
    }
    
    public void atualizarCategorias(ArrayList<VerticeMap> verticesTotal){
        if(verticesTotal != null){
            for(int i=0; i<verticesTotal.size(); i++){
                this.atualizarCategoria(verticesTotal.get(i));
            }
        }
    }
    
    private void redistribuirPadroes(ArrayList<Integer> vencedores){
        for(int i=0; i<vencedores.size(); i++){
            double[] padrao = data.get(vencedores.get(i));
            VerticeSOM vertice = this.calcularNodoVencedor(padrao);
            vertice.getVencedores().add(vencedores.get(i));
        }
    }
    
    
    private VerticeSOM novoVertice(double[] padrao, int vitorias){
        double[] relevancias = new double[this.numDimensoes];
        double[] variacaoMedia = new double[this.numDimensoes];
        double[] centro = new double[this.numDimensoes];
        for(int i=0; i<this.numDimensoes; i++){
            relevancias[i] = 1;
            variacaoMedia[i] = 0;
            centro[i] = padrao[i];
        }
        VerticeSOM vertice = new VerticeSOM(relevancias, variacaoMedia, centro, vitorias);
        if(this.vertices==null){
            this.vertices = new ArrayList<VerticeSOM>();
        }
        this.vertices.add(vertice);
        return vertice;
    }
    
    private void removerPerdedoresResetVitorias(){
        int i=0;
        while(i<vertices.size() && vertices.size()>1){
            VerticeSOM vertice = vertices.get(i);
            if(vertice.getVitorias() < (double)(this.lp*this.maxcomp)){
                ArrayList<Integer> vencedores = vertice.getVencedores();
                vertices.remove(i);
                grafo.removerVertice(vertice);
                this.redistribuirPadroes(vencedores);
            } else {
                i++;
            }
        }
    }
    
    private void atualizarConexoes(VerticeSOM vertice){
        for (int i=0; i<vertices.size(); i++){
            VerticeSOM vertice2 = vertices.get(i);
            if(vertice!=vertice2){
                double result = this.normaDiferenca(vertice.getRelevancias(), vertice2.getRelevancias());
                double valor = (double)(((double)Math.sqrt(this.numDimensoes))*this.c);
                if(result<valor){
                //if(result<this.c){
                    grafo.inserirAresta(vertice, vertice2);
                } else {
                    grafo.removerAresta(vertice, vertice2);
                }
            }
        }   
    }
    
    private void atualizarTodasConexoes(){
        for (int i=0; i<vertices.size(); i++) {
            VerticeSOM vertice = vertices.get(i);
            for (int j=0; j<vertices.size(); j++){
                VerticeSOM vertice2 = vertices.get(j);
                if(vertice!=vertice2){
                    double result = this.normaDiferenca(vertice.getRelevancias(), vertice2.getRelevancias());
                    double valor = (double)(((double)Math.sqrt(this.numDimensoes))*this.c);
                    if(result<valor){
                    //if(result<this.c){
                        this.grafo.inserirAresta(vertice, vertice2);
                    } else {
                        this.grafo.removerAresta(vertice, vertice2);
                    }
                }
            }
        }
    }
    
    private void atualizarNodo(VerticeSOM vertice, double[] padrao, double e){
        
        //Atualiza variação média
        double[] variacaoMedia = vertice.getVariacaoMedia();
        double[] centro = vertice.getCentro();
        for (int i = 0; i < padrao.length; i++) {
            double distance = (double)Math.abs((double)(padrao[i] - centro[i]));
            variacaoMedia[i] = (double)((1.0 - e*this.dsbeta) * variacaoMedia[i]) + 
                    (double)(e*this.dsbeta*distance);
        }
        vertice.setVariacaoMedia(variacaoMedia);
        
        double maxVariacao = vertice.getMaxVariacaoMedia();
        double minVariacao = vertice.getMinVariacaoMedia();
        double[] relevancias = vertice.getRelevancias();
        
        // Atualiza relevâncias
        for (int i = 0; i < padrao.length; i++) {
            if((maxVariacao - minVariacao) != 0){
                double aux = (double)(vertice.getVariacaoMedia()[i] - vertice.getMediaVariacaoMedia())/
                        (double)((maxVariacao - minVariacao)*this.epsilonds);
                relevancias[i] = (double)(1.0/(1.0 + (double)Math.pow(Math.E, (double)aux)));
            } else {
                relevancias[i] = 1.0;
            }
        }
        vertice.setRelevancias(relevancias);
        
        // Atualiza centro
        for(int i=0; i<padrao.length; i++){
            centro[i] = centro[i] + e * (padrao[i] - centro[i]);
        }
        vertice.setCentro(centro);
    }
    
    private VerticeSOM calcularNodoVencedor(double[] padrao){
        VerticeSOM vertice = null;
        double maiorAtivacao = -1;
        for(int i = 0; i< vertices.size(); i++){
            VerticeSOM vAux = vertices.get(i);
            double ativacao = this.calcularAtivacao(vAux, padrao);
            if(ativacao>maiorAtivacao){
                maiorAtivacao = ativacao;
                vertice = vAux;
            }
        }
        return vertice;
    }
    
    private double calcularAtivacao(VerticeSOM vertice, double[] padrao){
        
        double dist = this.calcularDistancia(vertice, padrao);
        //double[] relevancias = vertice.getRelevancias();
        //double part1 = (double)dist;
        //double part2 = (double)((double)(norma(relevancias)*norma(relevancias)) + 0.0000001);
        
        //return (double)(1.0/(double)(1.0 + (double)(part1/part2)));
        double somaRelevancias = (double)vertice.getSomaRelevancias();
        return (double)((somaRelevancias)/(double)(dist + somaRelevancias + 0.0000001));
    }
    
    private double norma(double[] vetor){
        double sum = 0.0;
        for (int i = 0; i < vetor.length; i++){
            sum = sum + (double)(vetor[i] * vetor[i]);
        }
        return (double)Math.sqrt(sum);
    }
    
    private double normaDiferenca(double[] vetor1, double[] vetor2){
        double sum = 0.0;
        if(vetor1.length == vetor2.length){
            for (int i = 0; i < vetor1.length; i++){
                sum = sum + (double)((vetor1[i] - vetor2[i]) * (vetor1[i] - vetor2[i]));
            }
        }
        return (double)Math.sqrt(sum);
    }
    
    private double calcularDistancia (VerticeSOM vertice, double[] padrao){
        double dist = 0;
        double[] relevancias = vertice.getRelevancias();
        double[] centro = vertice.getCentro();

        for (int i = 0; i < padrao.length; i++) {
            dist = dist + (double)(relevancias[i]*(double)((padrao[i] - centro[i])*(padrao[i] - centro[i])));
        }
        return (double)Math.sqrt(dist);
    }
    
    
    public void escreverResultadosClusters(ArrayList<VerticeMap> verticesTotal, String path, String filename){
        String result = ""+vertices.size()+"\t"+this.numDimensoes+"\n";
        
        for (int i=0; i<vertices.size(); i++){
            VerticeSOM vertice = vertices.get(i);
            result = result + i + "\t";
            double[] relevancias = vertice.getRelevancias();
            for(int j=0; j<relevancias.length; j++){
                result = result + relevancias[j] + "\t";
            }
            result = result + "\n";
        }
        
        for(int i=0; i<verticesTotal.size(); i++){
            VerticeMap verticeMap = verticesTotal.get(i);
            VerticeSOM vertice = verticeMap.getCategoria();
            int j = 0; 
            boolean encontrou = false;
            while (j<vertices.size() && !encontrou){
                VerticeSOM vertice2 = vertices.get(j);
                if (vertice == vertice2){
                    encontrou = true;
                }
                if (!encontrou) j++;
            }
            result = result + i + "\t"+ j + "\n";
        }
        
        try {
            Files.write(Paths.get(path +GeneralUtils.removeExtension(filename)+".results"), result.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void imprimirMatrizConfusao(int ctgSize, ArrayList<Integer> categorias, ArrayList<VerticeMap> verticesMapa){
        if(categorias.size() == verticesMapa.size()){
            /*int tam = 0;
            HashSet<Integer> ctg = new HashSet();
            for(int i=0; i<categorias.size(); i++){
                if(!ctg.contains(categorias.get(i))){
                    ctg.add(categorias.get(i));
                    tam++;
                }
            }*/
            int tam = ctgSize;
        
            int[][] matrizConfusao = new int[vertices.size()][tam];
        
            for(int i=0; i<verticesMapa.size(); i++){
                VerticeMap verticeMap = verticesMapa.get(i);
                VerticeSOM categoria = verticeMap.getCategoria();
                boolean encontrou = false;
                int j=0;
                while(j<vertices.size() && !encontrou){
                    if(categoria.equals(vertices.get(j))){
                        encontrou = true;
                    }
                    if (!encontrou) j++;
                }
                matrizConfusao[j][categorias.get(i)] = matrizConfusao[j][categorias.get(i)] + 1;
            }
            int[] somaLinhas = new int[vertices.size()];
            int[] somaColunas = new int[tam];
            String out = "cluster\\class\t|";
            for(int i=0; i<tam; i++){
                out = out + "\tcla" + i;
            }
            out = out + "\t| Sum\n"; 
            for(int i=0; i<vertices.size(); i++){
                out = out + "clu" + i + "\t\t|";
                for(int j=0; j<tam; j++){
                    out = out + "\t" + matrizConfusao[i][j];
                    somaLinhas[i] = somaLinhas[i] + matrizConfusao[i][j];
                    somaColunas[j] = somaColunas[j] + matrizConfusao[i][j];
                }
                out = out + "\t| " + somaLinhas[i] + "\n";
            }
            out = out + "Sums\t\t|";
            int soma = 0;
            for(int i=0; i<tam; i++){
                out = out + "\t" + somaColunas[i];
                soma = soma + somaColunas[i];
            }
            out = out + "\t| " + soma + "\n";
            System.out.println(out);
        }
    }

    public double getMaxNodeNumber() {
        return maxNodeNumber;
    }

    public void setMaxNodeNumber(double maxNodeNumber) {
        this.maxNodeNumber = maxNodeNumber;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getEb() {
        return eb;
    }

    public void setEb(double eb) {
        this.eb = eb;
    }

    public double getEn() {
        return en;
    }

    public void setEn(double en) {
        this.en = en;
    }

    public double getDsbeta() {
        return dsbeta;
    }

    public void setDsbeta(double dsbeta) {
        this.dsbeta = dsbeta;
    }

    public double getEpsilonds() {
        return epsilonds;
    }

    public void setEpsilonds(double epsilonds) {
        this.epsilonds = epsilonds;
    }

    public double getMaxcomp() {
        return maxcomp;
    }

    public void setMaxcomp(double maxcomp) {
        this.maxcomp = maxcomp;
    }

    public double getLp() {
        return lp;
    }

    public void setLp(double lp) {
        this.lp = lp;
    }

    public double getAt() {
        return at;
    }

    public void setAt(double at) {
        this.at = at;
    }

    public int getNumDimensoes() {
        return numDimensoes;
    }

    public Grafo getGrafo() {
        return grafo;
    }

    public ArrayList<VerticeSOM> getVertices() {
        return vertices;
    }
    
}
