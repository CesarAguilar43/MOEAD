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
public class Proyectos implements Comparable<Proyectos>{
     int costoProyecto;
     int [] pObjetivos;
     float beneficio;
    
    public Proyectos(int numObjetivos){
   this.costoProyecto=0;
   this.pObjetivos= new int[numObjetivos];
   this.beneficio=0;
    }
     public void calcularBeneficio() {
       float suma=(float) 0.0;
         for (int i = 0; i < pObjetivos.length; i++) {
             suma+=pObjetivos[i];
         }
         beneficio=suma/pObjetivos.length;
    }

    @Override
    public int compareTo(Proyectos otro) {
        return Float.compare(beneficio, otro.beneficio);
    }

    @Override
    public String toString() {
        return "Proyectos{" + "costoProyecto=" + costoProyecto + ", pObjetivos=" + pObjetivos + ", beneficio=" + beneficio + '}';
    }
}
