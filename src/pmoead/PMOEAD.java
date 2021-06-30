/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pmoead;

import java.io.IOException;

/**
 *
 * @author 210PC
 */
public class PMOEAD {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    
    public static void main(String[] args) throws IOException{
        Algoritmo a = new Algoritmo();
        a.ReadInstance();
        a.ReadWeightVectors();
        //a.ReadPreferences();
        a.algorithm();    
    }
   
    
    
}
