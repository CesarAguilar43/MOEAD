/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pmoead;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author 210PC
 */
public class Algoritmo {
// Una solucion A = 3, 5 , 2
    //Solucion B = 2 , 6 , 1
    //Solucion c = 1, 0 , 1

    float mediaBeneficio = (float) 0.0;
    int presupuesto;//presupuesto total del problema
    int n_objetivos;//numero total de objetivos por proyecto
    int n_proyectos;//numero total de proyectos
    int numIteraciones = 100;//Numero de generaciones en total del algoritmo.
    int T = 3;//Los "T" vecinos mas cercanos a considerar.
    float probaMutacion = 0.05f;//porcentaje de la mutacion.
    int N;//numero de vectores de peso "N"
    int poblacion = 25;//Tamaño de la poblacion
    double vectores_peso[][];// Matriz con los vectores de peso de N*objetivos
    double distancia_vectores_peso[][];//Matriz que contiene el calculo de la distancia Euclidiana de N*N
    int B[][];//Contiene los indices de los "T" vectores mas cercanos a los vectores de peso de N*T
    int[] Z;//Vector que contendra el mejor valor encontrado de cada objetivo
    ArrayList<Carteras> soluciones;
    ArrayList<Carteras> EP;
    //Carteras y;//Solucion
    Proyectos[] proyectos;//Vector de la clase Proyectos que contendra cada proyecto asociado con su costo y el valor de cada objetivo;
    Random rdm;
    int contador = 0;
    Proyectos[] proyectosordenados;
//  ******************************************Datos de preferencias**********************************************************************
    ElectreIII preferencias;
    float[][] credibilidad;
    float[][] matrizC;
    float[][] matrizD;

// *************************************************************************************************************************************
    public void ReadInstance() throws FileNotFoundException, IOException {
        String cadena;
        String temp[];
        try {
            FileReader fr = new FileReader("prueba2.txt");
            BufferedReader br = new BufferedReader(fr);
            presupuesto = Integer.parseInt(br.readLine());//Se recupera el presupuesto total
            // System.out.println(presupuesto);
            n_objetivos = Integer.parseInt(br.readLine());//Se recuepra el numero de objetivos
            n_proyectos = Integer.parseInt(br.readLine());
            //proyectos = new int[n_proyectos][n_objetivos + 1];
            proyectos = new Proyectos[n_proyectos];

            for (int i = 0; i < n_proyectos; i++) {
                cadena = br.readLine();
                temp = cadena.split(" ");
                proyectos[i] = new Proyectos(n_objetivos);
                proyectos[i].costoProyecto = Integer.parseInt(temp[0]);
                for (int j = 0; j < n_objetivos; j++) {
                    proyectos[i].pObjetivos[j] = Integer.parseInt(temp[j + 1]);
                }
                proyectos[i].calcularBeneficio();
            }
        } catch (IOException e1) {
        }
        if (proyectos != null) {
            proyectosordenados = proyectos.clone();
            Arrays.sort(proyectosordenados, Collections.reverseOrder());
            for (Proyectos v : proyectosordenados) {
                mediaBeneficio += v.beneficio;
                //System.out.println(v);
            }
            mediaBeneficio = mediaBeneficio / proyectosordenados.length;
        }
    }

    public void ReadWeightVectors() throws FileNotFoundException, IOException {
        try {
            FileReader fr = new FileReader("vectores.txt");
            BufferedReader br = new BufferedReader(fr);
            N = Integer.parseInt(br.readLine());
            n_objetivos = Integer.parseInt(br.readLine());
            String cadena;
            vectores_peso = new double[N][n_objetivos];
            for (int i = 0; i < N; i++) {
                cadena = br.readLine();
                String temp[] = cadena.split(" ");
                for (int j = 0; j < n_objetivos; j++) {
                    vectores_peso[i][j] = Double.parseDouble(temp[j]);
                }
            }
        } catch (IOException e1) {
        }
    }

    public void ReadPreferences() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("preferencias.txt");
        BufferedReader br = new BufferedReader(fr);
        int num_preferencias;
        num_preferencias = Integer.parseInt(br.readLine());
        System.out.println(num_preferencias);
        if (num_preferencias != n_objetivos) {
            System.out.println("Error: El numero de objetivos es diferente al tamaño de los vectores de preferencia");
        } else {
            String cadena;
            preferencias = new ElectreIII(num_preferencias);
            cadena = br.readLine();
            String temp[];
            float suma = (float) 0.0;
            //Umbral de pesos
            for (int i = 0; i < num_preferencias; i++) {
                temp = cadena.split(" ");
                preferencias.w[i] = Float.parseFloat(temp[i]);
                System.out.println(preferencias.w[i] + " ");
                suma += preferencias.w[i];
            }
            if (suma != 1) {
                System.out.println("Error: La suma de los pesos debe de ser igual a 1");
            } else {
                cadena = br.readLine();
                //Umbral de Indiferencia
                for (int i = 0; i < num_preferencias; i++) {
                    temp = cadena.split(" ");
                    preferencias.p[i] = Float.parseFloat(temp[i]);
                    //System.out.print(preferencias.u[i] + " ");
                }
                //Umbral de preveto
                cadena = br.readLine();
                for (int i = 0; i < num_preferencias; i++) {
                    temp = cadena.split(" ");
                    preferencias.q[i] = Float.parseFloat(temp[i]);
                }
                //Umbral de veto
                cadena = br.readLine();
                //Umbral de Veto
                for (int i = 0; i < num_preferencias; i++) {
                    temp = cadena.split(" ");
                    preferencias.v[i] = Float.parseFloat(temp[i]);
                }
            }
        }
    }

    public void algorithm() {
        //Ejecucion de todos los pasos del algoritmo MOEA/D básico por Qingfu Zhang y Hui Li, 2007.
        //1.1 Set EP = 0
        EP = new ArrayList();
        //1.2 Cumputar la distancia Euclidiana entre dos vectores de peso y trabajar con los "T" vectores de peso mas cercanos de B.
        EuclidianDistance();
        //1.2.1 Contener los indices de los "T" vectores mas cercanos en la matriz B.
        IndexWeightVectors();
        //1.3 Generar una poblacion inicial por un metodo random o un metodo en especifico
        InitializePopulation();
        //1.4 Inicializar z que contendra los mejores valores de cada objetivo
        InitializeZ();
        //Paso 2) Actualizar
//        Print();
        //System.out.println("hola");
        for (int i = 0; i < numIteraciones; i++) {
            for (int j = 0; j < N; j++) {
//                System.out.println(solucion.size());
                //2.1 Reproduccion, Seleccionar al azar dos indices, k y l de B(i) y generar una nueva solucion y de x^k x^y usando operadores geneticos
                Carteras y = Reproduction(j);
//                System.out.println(y.costoCartera + "--- Reproduccion " + j);
                //2.2 Reparacion o mejora, se aplica un metodo de reparacion o mejora para producir y'.   
//                System.out.println("antes" + y);
                y = Improvment(y);
//                System.out.println("despues" + y);
//                System.out.println(y.costoCartera + "---- Improvment " + j);
                //2.3 Actualizar Z, con la nueva solucion y comparando los objetivos con los valores del vector Z
                UpdateZ(y);
                //2.4 Actualizar el vecindario con las soluciones vecinas mediante Tchebycheff 
                UpdateNeighboring(j, y);
                //Actualizar EP, Remover de EP todas las soluciones dominadas por y
                UpdateEP(y);
                //UpdateEP_preference();
            }
//            solucion.clear();
//            System.out.println("iteracion" + i);
        }
//        InitializeMatrix();

//                System.out.println("Corrida "+contador);
//                PrintEP();
        System.out.println("EP tamaño: " + EP.size());
//        PrintEP();
        for (Carteras cartera : EP) {
            System.out.println(cartera);
        }
        System.out.println("Soluciones");
//        for (Carteras cartera : soluciones) {
//            System.out.println("Antes: "+cartera);
//            Carteras c;
//            c=Improvment(cartera);
//            System.out.println("Despues: "+c);
//        }

//        for (int i = 0; i < soluciones.size(); i++) {
//            Carteras cartera = soluciones.get(i);
//            System.out.println("Antes: "+cartera);
//            Carteras c;
//            c=Improvment(cartera);
//            System.out.println("Despues: "+c);
//        }
    }

    public void Print() {
        System.out.println("Soluciones Iniciales");
//        System.out.println("Cromosoma\t Obj1\t Objt2\t Objt3\t Objt4\t Costo Cartera");
        for (int i = 0; i < soluciones.size(); i++) {
            for (int j = 0; j < n_proyectos; j++) {
                System.out.print(soluciones.get(i).nProyectos[j] + "   ");
                //System.out.print(solucion.get(i).val_Objetivos[j]+"\t");
            }
            for (int j = 0; j < n_objetivos; j++) {
                System.out.print(soluciones.get(i).val_Objetivos[j] + " \t");
            }
            System.out.print(soluciones.get(i).costoCartera);
            System.out.println("");
        }

    }

    public void PrintEP() {
        System.out.println("Soluciones EP");
        for (int i = 0; i < EP.size(); i++) {
            for (int j = 0; j < n_proyectos; j++) {
                System.out.print(EP.get(i).nProyectos[j] + "   ");
                //System.out.print(solucion.get(i).val_Objetivos[j]+"\t");
            }
            for (int j = 0; j < n_objetivos; j++) {
                System.out.print(EP.get(i).val_Objetivos[j] + "\t");
            }
            System.out.print(EP.get(i).costoCartera);
            System.out.println("");
        }

    }

    public void ImprimirVectores() {
        for (double[] distancia_vectores_peso1 : distancia_vectores_peso) {
            for (int j = 0; j < distancia_vectores_peso1.length; j++) {
                System.out.print(distancia_vectores_peso1[j] + " ");
            }
            System.out.println("");
        }
    }

    public void EuclidianDistance() {
        distancia_vectores_peso = new double[N][N];
        double distTemp = 0.0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < n_objetivos; k++) {
                    distTemp += Math.pow(vectores_peso[i][k] - vectores_peso[j][k], 2);
                    distancia_vectores_peso[i][j] = Math.sqrt(distTemp);
                }
            }
        }
    }

    public void IndexWeightVectors() {
        //OrdenarVectores[][] vectorDistancias = new OrdenarVectores[N][N];
        double[] vectorDistancias;
        double[][] vectorIndicesDistancias;
        B = new int[N][T];
        //B[10][3]

        for (int i = 0; i < N; i++) {
            vectorDistancias = distancia_vectores_peso[i];//Declarar un vector que contendra las distancias
            //System.out.print(vectorDistancias[i]+" ");
            vectorIndicesDistancias = Bubble(vectorDistancias);
            for (int j = 0; j < T; j++) {
                B[i][j] = (int) vectorIndicesDistancias[j][1];
            }

        }
    }

    public double[][] Bubble(double[] vectorDistancias) {
        double[][] vectorIndicesDistancias = new double[N][2];
        int[] indices = new int[N];
        double aux;
        boolean bandera;
        int index;
        for (int i = 0; i < N; i++) {
            indices[i] = i;
        }
        do {
            bandera = false;
            for (int i = 0; i < vectorDistancias.length - 1; i++) {
                if (vectorDistancias[i] > vectorDistancias[i + 1]) {
                    aux = vectorDistancias[i];
                    vectorDistancias[i] = vectorDistancias[i + 1];
                    vectorDistancias[i + 1] = aux;

                    index = indices[i];
                    indices[i] = indices[i + 1];
                    indices[i + 1] = index;

                    bandera = true;
                }
            }
        } while (bandera);

        for (int i = 0; i < N; i++) {
            vectorIndicesDistancias[i][0] = vectorDistancias[i];
            vectorIndicesDistancias[i][1] = indices[i];
        }
        return vectorIndicesDistancias;
    }

    public int[] ClearSolution(int solucion[]) {
        for (int i = 0; i < solucion.length; i++) {
            solucion[i] = 0;
        }
        return solucion;
    }

    public void InitializePopulation() {
        soluciones = new ArrayList();
        for (int i = 0; i < poblacion; i++) {
            Carteras sol = new Carteras(n_objetivos, n_proyectos);
            rdm = new Random();
            double probabilidad = 0.2;
            do {
                //ResetSolution(sol);
                for (int j = 0; j < n_proyectos; j++) {
                    if (rdm.nextDouble() > probabilidad) {
                        sol.nProyectos[j] = 1;
                        for (int k = 0; k < n_objetivos; k++) {
                            sol.val_Objetivos[k] += proyectosordenados[j].pObjetivos[k];
                        }
                        sol.costoCartera += proyectosordenados[j].costoProyecto;
                    }
                }

            } while (presupuesto < sol.costoCartera);
            soluciones.add(sol.copy());

        }
    }

    public int[] ResetChromosome(int[] cromosoma) {

        for (int i = 0; i < n_proyectos; i++) {
            cromosoma[i] = 0;
        }
        return cromosoma;
    }

    public void ResetSolution(Carteras solucion) {
        solucion.costoCartera = 0;
        for (int i = 0; i < n_proyectos; i++) {
            solucion.nProyectos[i] = 0;
        }
        for (int i = 0; i < n_objetivos; i++) {
            solucion.val_Objetivos[i] = 0;
        }
    }

    public void InitializeZ() {
        Z = new int[n_objetivos];
        for (int i = 0; i < Z.length; i++) {
            Z[i] = 0;
        }
        for (int i = 0; i < soluciones.size(); i++) {
            for (int j = 0; j < n_objetivos; j++) {
                if (soluciones.get(i).val_Objetivos[j] > Z[j]) {
                    Z[j] = soluciones.get(i).val_Objetivos[j];
                }
            }
        }
    }

    public Carteras Reproduction(int i) {
        int k;
        int l;
        int xk;
        int xl;
        k = rdm.nextInt(T);
        l = rdm.nextInt(T);
        xk = B[i][k];
        xl = B[i][l];
        // Carteras y = Crossover(soluciones.get(xk), soluciones.get(xl)); 
        Carteras y = cruzaUniforme(soluciones.get(xk), soluciones.get(xl));
        Mutation(y);
        Evaluacion(y);
        return y;
    }

    public int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public Carteras Crossover(Carteras carteraxk, Carteras carteraxl) {

        int punto;
        punto = getRandomNumber(1, proyectos.length - 1);
        Carteras y;
        y = new Carteras(n_objetivos, n_proyectos);
        for (int i = 0; i <= punto; i++) {
            y.nProyectos[i] = carteraxk.nProyectos[i];
        }
//        System.out.println(y.costoCartera);

        for (int i = punto + 1; i < n_proyectos; i++) {
            y.nProyectos[i] = carteraxl.nProyectos[i];
        }
//        System.out.println(y.costoCartera);
        return y;
    }

    public Carteras cruzaUniforme(Carteras a, Carteras b) {
        Carteras y;
        y = new Carteras(n_objetivos, n_proyectos);
        for (int i = 0; i < n_proyectos; i++) {
            if (rdm.nextDouble() < 0.5) {
                y.nProyectos[i] = a.nProyectos[i];
            } else {
                y.nProyectos[i] = b.nProyectos[i];
            }
        }
        return y;
    }

    public void Evaluacion(Carteras solucion) {
        int costo = 0;
        int obj[] = new int[n_objetivos];
        for (int i = 0; i < n_objetivos; i++) {
            obj[i] = 0;
        }
        for (int i = 0; i < solucion.nProyectos.length; i++) {
            if (solucion.nProyectos[i] == 1) {
                costo += proyectosordenados[i].costoProyecto;
                for (int j = 0; j < n_objetivos; j++) {
                    obj[j] += proyectosordenados[i].pObjetivos[j];
                }
            }
        }
        if (costo > presupuesto) {
            solucion.totalRestriccionesVioladas = 1;
            solucion.acumuladorestricciones += presupuesto - costo;
        } else {
            solucion.totalRestriccionesVioladas = 0;
            solucion.acumuladorestricciones = 0;
        }
        solucion.costoCartera = costo;
        solucion.val_Objetivos = obj;
    }

    public void Mutation(Carteras y) {
        double bit;

        //System.out.println(bit);
        if (Math.random() <= probaMutacion) {
            for (int i = 0; i < n_proyectos; i++) {
                bit = Math.random();
                if (bit < 0.5) {
                    if (y.nProyectos[i] == 1) {
                        y.nProyectos[i] = 0;
                    } else {
                        y.nProyectos[i] = 1;
                    }
                }
            }
        }

    }

    public Carteras Improvment(Carteras y) {
        int pos;
        int costo = y.costoCartera;
        if (y.totalRestriccionesVioladas > 0) {
            //Reparacion hace que la solucion sea factible con respecto al costo
            while (costo > presupuesto) {
                for (int i = 0; i < n_proyectos; i++) {
                    if (rdm.nextDouble() < 0.5 && y.nProyectos[i] == 1) {
                        y.nProyectos[i] = 0;
                        costo = costo - proyectosordenados[i].costoProyecto;
                    }
                }
            }
//Mejora con respecto al beneficio del proyecto a la medio de los proyectos
            for (int i = 0; i < n_proyectos; i++) {
                if (y.nProyectos[i] == 0 && proyectosordenados[i].beneficio >= mediaBeneficio) {
                    int temporal = costo + proyectosordenados[i].costoProyecto;
                    if (temporal <= presupuesto) {
                        y.nProyectos[i] = 1;
                        costo = temporal;
                    }

                }
            }

            //System.out.println(y);
        } else {
            for (int i = 0; i < n_proyectos; i++) {
                if (y.nProyectos[i] == 0 && proyectosordenados[i].beneficio >= mediaBeneficio) {
                    int temporal = costo + proyectosordenados[i].costoProyecto;
                    if (temporal <= presupuesto) {
                        y.nProyectos[i] = 1;
                        costo = temporal;
                    }

                }
            }
        }
        //System.out.println("despues"+y);
        Evaluacion(y);

        return y;
    }

    public void UpdateZ(Carteras y) {
        for (int i = 0; i < n_objetivos; i++) {
            if (y.val_Objetivos[i] > Z[i]) {
                Z[i] = y.val_Objetivos[i];
            }
        }
    }
// B[i] contener los indices de lamba mas cercanos
//T =3
    //
    public void UpdateNeighboring(int i, Carteras y) {
        int indice;
        double gteX;
        double gteY;
        for (int j = 0; j < T; j++) {
            indice = B[i][j];
            gteY = GTE(indice, y.val_Objetivos);//Calculo de Tchebycheff a la solucion y 
            gteX = GTE(indice, soluciones.get(indice).val_Objetivos);//Calculo de Tchebycheff a la soluciones cercanas del vecindario

            if (gteY <= gteX) {
                //soluciones.remove(indice);                
                soluciones.set(indice, y);
            }
        }
    }

    public double GTE(int i, int[] objetivos) {
        double[] Tchebycheff = new double[n_objetivos];
        double gte = 0;
        //System.out.println(gte);
        for (int j = 0; j < n_objetivos; j++) {
            Tchebycheff[j] = Math.abs((objetivos[j] - Z[j]) * distancia_vectores_peso[i][j]);
            if (Tchebycheff[j] > gte) {
                gte = Tchebycheff[j];
                //System.out.println(gte);
            }
        }
        return gte;
    }

    //Calculo de dominancia retorna 0 si las soluciones A y B no son dominadas entre ellas, retorna -1 cuando la solucion A domina a la solucion B, retorna -1 cuando la solucion B domina 
    //a la solucion A
    public static int dominancia(Carteras a, Carteras b) {
        boolean AdominaB = false;
        boolean BdominaA = false;
        if (a.totalRestriccionesVioladas > 0 || b.totalRestriccionesVioladas > 0) {
            if (a.acumuladorestricciones > b.acumuladorestricciones) {
                return -1;
            }
            if (b.acumuladorestricciones > a.acumuladorestricciones) {
                return 1;
            }
        }
        for (int i = 0; i < a.val_Objetivos.length; i++) {
            if (a.val_Objetivos[i] > b.val_Objetivos[i]) {
                AdominaB = true;
            } else if (b.val_Objetivos[i] > a.val_Objetivos[i]) {
                BdominaA = true;
            }
        }
        if (AdominaB == BdominaA) {
            return 0;
        }
        if (AdominaB) {
            return -1;
        }
        return 1;
    }

    public void UpdateEP(Carteras y) {

        //int cont = 0;
        if (EP.isEmpty()) {
            EP.add(y.copy());
        } else {
            if (!EP.contains(y)) {
                Iterator<Carteras> iterator = EP.iterator();
                boolean agregar = true;
                while (iterator.hasNext()) {
                    Carteras B = iterator.next();
                    int valor = dominancia(y, B);
                    if (valor == -1) {
                        iterator.remove();
                    } else if (valor == 1) {
                        agregar = false;
                        //System.out.println("Checar");
                    }
                }
                if (agregar) {
                    EP.add(y.copy());
                }
            }
        }
    }
}
