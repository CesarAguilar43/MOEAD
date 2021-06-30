/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pmoead;

import java.util.Comparator;

/**
 *
 * @author 210PC
 */
public class ElectreIII implements Comparator<Carteras> {

   
    float[] w;// peso del criterio i
    float[] p;//umbral de preferencia (el DM prefiere una alternativa sobre la otra)
    float[] q;//umbral de indiferencia (el DM le es indiferente ante dos alternativas)
    float[] v;//umbral de veto (hace bloqueo en la relación de superacion entre dos alternativas y se opone fuertemente a la afirmacion de aSb)
    double ε = 0.1;//asegura la relación de indiferencia
    double λ = 0.67;//Determina el nivel de exigencia de la relacion de sobreclasificacion
    double β = 0.2;//asegura la relación de preferencia estricta o la k-preferencia

    public ElectreIII(int tamaño) {
        this.w = new float[tamaño];
        this.p = new float[tamaño];
        this.q = new float[tamaño];
        this.v = new float[tamaño];
    }

    /**
     *
     * @param a
     * @param b
     * @return xPy = -1, xIy = 0, xQy = 1, xKy = 2, xRy = 3, x-y = 4
     *
     */
    @Override
    public int compare(Carteras a, Carteras b) {
        //xPy
        int valor = Algoritmo.dominancia(a, b);
        boolean xPy = false;
        boolean xIy = false;
        if (valor == -1) {
            xPy = true;
            return -1;
        }
        float sigmaxy = calculoSigma(a, b);
        float sigmayx = calculoSigma(b, a);
        if (sigmaxy >= λ && sigmayx < 0.5) {
            xPy = true;
            return -1;
        }
        if (sigmaxy >= λ && (0.5 <= sigmaxy && sigmaxy < λ) && (sigmaxy - sigmayx) >= β) {
            xPy = true;
            return -1;
        }
        //xIy
        if (sigmaxy >= λ && sigmayx >= λ) {
            xIy = true;
            return 0;
        }
        if (Math.abs(sigmaxy - sigmayx) < ε) {
            xIy = true;
            return 0;
        }
        //xQy
        if (sigmaxy >= λ && sigmaxy > sigmayx) {
            return 1;
        }
        if (!(xPy && xIy)) {
            return 1;
        }
        //xKy
        if (0.5 < sigmaxy && sigmaxy < λ) {
            return 2;
        }
        if (sigmayx < 0.5) {
            return 2;
        }
        if ((sigmaxy - sigmayx) >= ε) {
            return 2;
        }
        //xRy
        if (sigmaxy < 0.5) {
            return 3;
        }
        if (sigmayx < 0.5) {
            return 3;
        }
        //x-y No preferencia
        return 4;
    }

//    public float concordancia_ij(Carteras a, Carteras b) {
//        
//        return 0;
//    }
//
//    public float discordancia_ij(Carteras a, Carteras b) {
//        return 0;
//    }

    public float calculoConcordancia(float a, float b) {
        return 0;
    }

    public float calculoDiscordancia(float a, float b) {
        return 0;
    }

    public float calculoSigma(Carteras a, Carteras b) {
        float concordancia=(float) 0.0;
        float discordancia =(float) 0.0;
        
        for (int i = 0; i < a.val_Objetivos.length; i++) {
            
        }
        return 0;
    }

}
