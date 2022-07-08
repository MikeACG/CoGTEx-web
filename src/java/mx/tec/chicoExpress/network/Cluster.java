/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.tec.chicoExpress.network;

/**
 *
 * @author INTEL
 */
public class Cluster {
    
    private int[] members;
    private int id;
    private int iteration;

    public Cluster(int[] members, int id, int iteration) {
        this.members = members;
        this.id = id;
        this.iteration = iteration;
    }
    
    public int[] getMembers() {
        return members;
    }
    
    public int getId() {
        return id;
    }
    
    public int getIteration() {
        return iteration;
    }
}
