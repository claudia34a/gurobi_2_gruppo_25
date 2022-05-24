import gurobi.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    //creazione variabili xij del modello
    private static GRBVar[][] aggiungiVariabili(GRBModel model, int verticeV, int arcoA) throws GRBException {
        GRBVar[][] xij = new GRBVar[verticeV][arcoA];

        for (int i = 0; i < vertiveV; i++) {

            for (int j = 0; j < arcoA; j++) {
                xij[i][j] = model.addVar(0, GRB.INFINITY, 0, GRB.CONTINUOUS, "x" + i + j);
            }
        }
        return xij;
    }

    //creazione variabile per funzione obiettivo linearizzata (modulo linearizzato)
    private static GRBVar aggiungiVariabileFunzioneObiettivo(GRBModel model) throws GRBException {
        GRBVar W;
        W = model.addVar(-GRB.INFINITY, GRB.INFINITY, 0, GRB.CONTINUOUS, "W");
        return W;
    }

    //aggiunta funzione obiettivo al modello (modulo linearizzato)
    private static void aggiungiFunzioneObiettivo(GRBModel model, GRBVar w) throws GRBException {
        GRBLinExpr funzione_obiettivo = new GRBLinExpr();
        // W = |a-b|
        //**** AGGIUNTA FUNZIONE OBIETTIVO ****
        funzione_obiettivo.addTerm(1.0, w);
        model.setObjective(funzione_obiettivo);
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
    }

    private static void aggiungiVincoliFunzioneObiettivoAus(GRBModel model, GRBVar w, GRBVar[][] xij, int[][] P, GRBVar[] yh, GRBVar[] aus) throws GRBException {

        for (int i = 0; i < P.length; i++) {
            for (int j = 0; j < P[0].length / 2; j++) {
                obj.addTerm(P[i][j], xij[i][j]);
                obj1.addTerm((-1) * P[i][j], xij[i][j]);
            }

            for (int j = (P[0].length / 2) + 1; j < P[0].length; j++) {
                obj.addTerm((-1) * ([i][j]), xij[i][j]);
                obj1.addTerm((P[i][j]), xij[i][j]);
            }
        }
    }

        public static void main (String[]args){

            int[][] xij; //variabile binaria  ed è uguale a 1 se l'arco (i,j) appartiene al circuito, altrimenti xij=0
            int[][] cij;//il percorso è simmetrico -> cij=cji
            int arcoA;

            int verticeV = 2;
            int a=7; //vincolo1: il costo dei lati incidenti al vertice v sia al massimo il a% del costo totale del ciclo
            int [] latob ={6, 25};
            int[] latoc = {36, 1 };
            int[] latod = {13, 10};
            int[] latoe= {13, 10};
            int[] latof = {32, 2};
            int[] latog = {2, 8};
            int[] latoh = {16, 39};
            int[] latoi = {7, 9};
            int latol = 2;


            String nome_file = "coppia25.txt";

            try {
                // apre il file in lettura
                BufferedReader filebuf =
                        new BufferedReader(new FileReader(nome_file));
                boolean iniziaLettura = false;
                String next;
                next = filebuf.readLine();

                while (next != null) {
                    String[] riga;
                    if (iniziaLettura) { // se non e' finito il file
                        riga = (next.split(" "));
cij[]
                    }else if (next.contains("Vertici")) {
                        iniziaLettura = true;
                    }
                    next = filebuf.readLine();
                }
                ;

                filebuf.close(); // chiude il file

            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

