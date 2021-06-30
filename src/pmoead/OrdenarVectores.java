/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pmoead;

/**
 *
 * @author 210PC
 */
public class OrdenarVectores implements Comparable<OrdenarVectores>{
    double dist;
    int index_i;
    int index_j;
    
    public OrdenarVectores(int d, int i, int j){
    this.dist=d;
    this.index_i=i;
    this.index_j=j;
    }

    @Override
    public int compareTo(OrdenarVectores t) {
        if (dist>t.getDist()) {
            return 1;
        }
        else if (dist==t.getDist()) {
            return 0;
        }
        return -1;
    }
    
    public double getDist(){
    return dist;
    }
    
}
