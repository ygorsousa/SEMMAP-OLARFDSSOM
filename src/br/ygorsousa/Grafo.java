package br.ygorsousa;

import java.util.ArrayList;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class Grafo {
    private int numeroVertices;
    private int numeroArestas;
    private boolean digrafo = false;
    private int idCount;
    
    private final ArrayList<ArrayList> listasAdjacencias;

    // O Grafo foi implementado com a sua representação em Listas de Adjacências.
    public Grafo() {
        this.numeroVertices = 0;
        this.idCount = 0;
        listasAdjacencias = new ArrayList<ArrayList>();
    }
    
    public void inserirVertice(Vertice vertice){
        vertice.setId(this.idCount);
        ArrayList<Adjacencia> adjacencias = new ArrayList<Adjacencia>();
        adjacencias.add(new Adjacencia(vertice));
        listasAdjacencias.add(adjacencias);
        this.numeroVertices++;
        this.idCount++;
    }
    
    public void removerVertice(Vertice vertice){
        
        if (!isDigrafo()){
            for (int i = 0; i < listasAdjacencias.size(); i++) {
                ArrayList<Adjacencia> adjacencias = listasAdjacencias.get(i);
                boolean encontrou = false;
                for(int j=1; j<adjacencias.size() && !encontrou; j++){
                    Adjacencia adjacencia = adjacencias.get(j);
                    if (adjacencia.getVerticeFim() == vertice) {
                        adjacencias.remove(j);
                        encontrou = true;
                    }
                }
            }
        }
        
        boolean encontrou = false;
        for (int i = 0; i < listasAdjacencias.size() && !encontrou; i++) {
            ArrayList<Adjacencia> adjacencias = listasAdjacencias.get(i);
            Adjacencia adjacencia = adjacencias.get(0);
            if (adjacencia.getVerticeFim() == vertice) {
                listasAdjacencias.remove(i);
                encontrou = true;
            }
        }
        this.numeroVertices--;
    }
    
    public void inserirAresta(Vertice verticeInicio, Vertice verticeFim){
        for (int i = 0; i < listasAdjacencias.size(); i++) {
            ArrayList<Adjacencia> adjacencias = listasAdjacencias.get(i);
            Adjacencia adjacencia = adjacencias.get(0);
            if (!isDigrafo()) {
                if (adjacencia.getVerticeFim() == verticeInicio) {
                    boolean jaExiste = false;
                    for(int j=1; j<adjacencias.size() && !jaExiste; j++){
                        Adjacencia adj = adjacencias.get(j);
                        if(adj.getVerticeFim()==verticeFim){
                            jaExiste = true;
                        }
                    }
                    if (!jaExiste) adjacencias.add(new Adjacencia(verticeFim));
                } else if (adjacencia.getVerticeFim() == verticeFim) {
                    boolean jaExiste = false;
                    for(int j=1; j<adjacencias.size() && !jaExiste; j++){
                        Adjacencia adj = adjacencias.get(j);
                        if(adj.getVerticeFim()==verticeInicio){
                            jaExiste = true;
                        }
                    }
                    if (!jaExiste) adjacencias.add(new Adjacencia(verticeInicio));
                }
            } else {
                if (adjacencia.getVerticeFim() == verticeInicio) {
                    boolean jaExiste = false;
                    for(int j=1; j<adjacencias.size() && !jaExiste; j++){
                        Adjacencia adj = adjacencias.get(j);
                        if(adj.getVerticeFim()==verticeFim){
                            jaExiste = true;
                        }
                    }
                    if (!jaExiste) adjacencias.add(new Adjacencia(verticeFim));
                }
            }
        }
        this.numeroArestas++;
    }
    
    public void removerAresta(Vertice verticeInicio, Vertice verticeFim){
        for (int i = 0; i < listasAdjacencias.size(); i++) {
            ArrayList<Adjacencia> adjacencias = listasAdjacencias.get(i);
            Adjacencia adjacencia = adjacencias.get(0);
            if (!isDigrafo()) {
                if (adjacencia.getVerticeFim() == verticeInicio) {
                    boolean encontrou = false;
                    for (int j=1; j<adjacencias.size() && !encontrou; j++){
                        Adjacencia adj = adjacencias.get(j);
                        if(adj.getVerticeFim()==verticeFim){
                            adjacencias.remove(j);
                            encontrou = true;
                        }
                    }
                } else if (adjacencia.getVerticeFim() == verticeFim) {
                    boolean encontrou = false;
                    for (int j=1; j<adjacencias.size() && !encontrou; j++){
                        Adjacencia adj = adjacencias.get(j);
                        if(adj.getVerticeFim()==verticeInicio){
                            adjacencias.remove(j);
                            encontrou = true;
                        }
                    }
                }
            } else {
                if (adjacencia.getVerticeFim() == verticeInicio) {
                    boolean encontrou = false;
                    for (int j=1; j<adjacencias.size() && !encontrou; j++){
                        Adjacencia adj = adjacencias.get(j);
                        if(adj.getVerticeFim()==verticeFim){
                            adjacencias.remove(j);
                            encontrou = true;
                        }
                    }
                }
            }
        }
    }   
    
    
    public ArrayList<Adjacencia> getListaAdjacencia(Vertice vertice){
        boolean encontrou = false;
        ArrayList<Adjacencia> adjacencias = null;
        for (int i = 0; i < listasAdjacencias.size() && !encontrou; i++) {
            adjacencias = listasAdjacencias.get(i);
            if (adjacencias.get(0).getVerticeFim() == vertice) {
                encontrou = true;
            }
        }
	return adjacencias;
    }
    
    public ArrayList<ArrayList> getListasAdjacencias(){
        return this.listasAdjacencias;
    }
    

    public boolean isDigrafo() {
        return digrafo;
    }

    public void setDigrafo(boolean digrafo) {
        this.digrafo = digrafo;
    }

    public int getNumeroVertices() {
        return numeroVertices;
    }

    public int getNumeroArestas() {
        return numeroArestas;
    }
}
