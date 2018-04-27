package br.ygorsousa;

import java.util.ArrayList;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class VerticeSOM extends Vertice{
    private double[] relevancias;
    private double[] variacaoMedia;
    private double[] centro;
    private int vitorias;
    private ArrayList<Integer> vencedores;
    
    public VerticeSOM(){}
    
    public VerticeSOM(double[] relevancias, double[] variacaoMedia, double[] centro, int vitorias){
        this.relevancias = relevancias;
        this.centro = centro;
        this.variacaoMedia = variacaoMedia;
        this.vitorias = vitorias;
        this.vencedores = new ArrayList();
    }
    
    public double getMaxVariacaoMedia(){
        double max = 0.0;
        for (int i=0; i<this.variacaoMedia.length; i++){
            if(variacaoMedia[i]>max){
                max = variacaoMedia[i];
            }
        }
        return max;
    }
    
    public double getMinVariacaoMedia(){
        double min = variacaoMedia[0];
        for (int i=1; i<this.variacaoMedia.length; i++){
            if(variacaoMedia[i]<min){
                min = variacaoMedia[i];
            }
        }
        return min;
    }
    
    public double getMediaVariacaoMedia(){
        double soma = 0.0;
        for (int i=1; i<this.variacaoMedia.length; i++){
            soma = soma + variacaoMedia[i];
        }
        return (double)(soma/(double)variacaoMedia.length);
    }
    
    public double getSomaRelevancias(){
        double soma = 0.0;
        for (int i=1; i<this.relevancias.length; i++){
            soma = soma + relevancias[i];
        }
        return soma;
    }

    public double[] getRelevancias() {
        return relevancias;
    }

    public void setRelevancias(double[] relevancias) {
        this.relevancias = relevancias;
    }

    public double[] getVariacaoMedia() {
        return variacaoMedia;
    }

    public void setVariacaoMedia(double[] variacaoMedia) {
        this.variacaoMedia = variacaoMedia;
    }

    public double[] getCentro() {
        return centro;
    }

    public void setCentro(double[] centro) {
        this.centro = centro;
    }

    public int getVitorias() {
        return vitorias;
    }

    public void setVitorias(int vitorias) {
        this.vitorias = vitorias;
    }
    
    public ArrayList<Integer> getVencedores(){
        return this.vencedores;
    }
    
    public void setVencedores(ArrayList<Integer> vencedores){
        this.vencedores = vencedores;
    }
    
    public void incrementarVitorias(){
        this.vitorias = this.vitorias + 1;
    }
    
}
