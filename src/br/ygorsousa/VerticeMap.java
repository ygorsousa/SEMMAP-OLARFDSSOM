package br.ygorsousa;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class VerticeMap extends Vertice{
    
    private double[] relevancias;
    private double[] variacaoMedia;
    private double[] coordenadas;
    private double[] objetos;
    private double[] somaObjetos;
    private VerticeSOM categoria;
    
    public VerticeMap(){}
    
    public VerticeMap(double[] coordenadas, double[] objetos, double[] somaObjetos){
        this.coordenadas = coordenadas;
        this.objetos = objetos;
        this.somaObjetos = somaObjetos;
    }
    
    public VerticeMap(double[] relevancias, double[] variacaoMedia, double[] coordenadas, double[] objetos, double[] somaObjetos){
        this.relevancias = relevancias;
        this.coordenadas = coordenadas;
        this.variacaoMedia = variacaoMedia;
        this.objetos = objetos;
        this.somaObjetos = somaObjetos;
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

    public double[] getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(double[] centro) {
        this.coordenadas = centro;
    }

    public double[] getObjetos() {
        return objetos;
    }

    public void setObjetos(double[] objetos) {
        this.objetos = objetos;
    }

    public double[] getSomaObjetos() {
        return somaObjetos;
    }

    public void setSomaObjetos(double[] somaObjetos) {
        this.somaObjetos = somaObjetos;
    }

    public VerticeSOM getCategoria() {
        return categoria;
    }

    public void setCategoria(VerticeSOM categoria) {
        this.categoria = categoria;
    }
}

