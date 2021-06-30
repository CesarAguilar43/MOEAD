/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pmoead;

import java.util.Arrays;

/**
 *
 * @author 210PC
 */
public class Carteras {
    int costoCartera;
    int[] nProyectos;
    int[] val_Objetivos;
    int totalRestriccionesVioladas;
    int acumuladorestricciones;
    
    public Carteras(int n_objetivos,int n_proyectos){
    this.val_Objetivos=new int[n_objetivos];
    this.nProyectos=new int[n_proyectos];
    this.costoCartera=0;
    this.totalRestriccionesVioladas=0;
    this.acumuladorestricciones=0;
        for (int i = 0; i < val_Objetivos.length; i++) {
            val_Objetivos[i]=0;
        }
        for (int i = 0; i < nProyectos.length; i++) {
            nProyectos[i]=0;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Arrays.hashCode(this.nProyectos);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Carteras other = (Carteras) obj;
        if (!Arrays.equals(this.nProyectos, other.nProyectos)) {
            return false;
        }
        return true;
    }
    
    public Carteras copy(){
    Carteras copia = new Carteras(val_Objetivos.length,nProyectos.length);
        for (int i = 0; i < val_Objetivos.length; i++) {
            copia.val_Objetivos[i] = this.val_Objetivos[i];
        }
        for (int i = 0; i < nProyectos.length; i++) {
            copia.nProyectos[i]=this.nProyectos[i];
        }
       copia.costoCartera = this.costoCartera;
       copia.totalRestriccionesVioladas = this.totalRestriccionesVioladas;
       copia.acumuladorestricciones = this.acumuladorestricciones;
       return copia;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d %d %d", Arrays.toString(nProyectos), Arrays.toString(val_Objetivos),costoCartera, totalRestriccionesVioladas, acumuladorestricciones);
    }
    

    Carteras() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
