package br.ygorsousa;

/**
 *
 * @author ygor (ycns@cin.ufpe.br)
 */
public class Adjacencia {
    private Vertice verticeFim;

    public Adjacencia(Vertice verticeFim) {
        this.verticeFim = verticeFim;
    }

    public Vertice getVerticeFim() {
        return verticeFim;
    }

    public void setVerticeFim(Vertice verticeFim) {
        this.verticeFim = verticeFim;
    }

}
