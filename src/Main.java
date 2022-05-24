import gurobi.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    //creazione variabili xij del modello
    private static GRBVar[][] aggiungiVariabili(GRBModel model, int nVertici) throws GRBException {
        GRBVar[][] xij = new GRBVar[nVertici][nVertici];

        for (int i = 0; i < vertiveV; i++) {

            for (int j = 0; j < nVertici; j++) {
                xij[i][j] = model.addVar(0, 1, 0, GRB.INTEGER, "x" + i + j);
            }
        }
        return xij;
    }

    //aggiunta funzione obiettivo al modello
    private static void aggiungiFunzioneObiettivo(GRBModel model, int [][] cij, GRBVar [][] xij, int nVertici) throws GRBException {
        GRBLinExpr funzione_obiettivo = new GRBLinExpr();

        for (int i = 0; i < nVertici; i++){
            for (int j = 0; j < nVertici; j++){
                if(i!=j)
                    funzione_obiettivo.addTerm(cij[i][j], xij[i][j]);
            }
        }
        model.setObjective(funzione_obiettivo);
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
    }



    private static void aggiungiVincolo1(GRBModel model, GRBVar[][] xij,int nVertici) throws GRBException {

        for (int i = 0; i < nVertici; i++){
            for (int j = 0; j < nVertici; j++){
                if(i!=j) {
                    GRBLinExpr expr = new GRBLinExpr();
                    expr.addTerm(1, xij[i][j]);
                    model.addConstr(expr, GRB.EQUAL, 1, "vincolo " + i + j);
                }
            }
        }
    }

    private static void aggiungiVincolo2(GRBModel model, GRBVar[][] xij,int nVertici) throws GRBException {

        for (int j = 0; j < nVertici; j++){
            for (int i = 0; i < nVertici; i++){
                if(i!=j) {
                    GRBLinExpr expr = new GRBLinExpr();
                    expr.addTerm(1, xij[i][j]);
                    model.addConstr(expr, GRB.EQUAL, 1, "vincolo " + i + j);
                }
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
                // apre in lettura
                BufferedReader filebuf = new BufferedReader(new FileReader(nome_file));
                boolean iniziaLettura = false;
                String next;
                next = filebuf.readLine();

                while (next != null) {
                    String[] riga;
                    if (iniziaLettura) { // se non e' finito il file
                        riga = next.split(" ");
                        cij[Integer.parseInt(riga[0])][Integer.parseInt(riga[1])] = Integer.parseInt(riga[2]);
                        cij[Integer.parseInt(riga[1])][Integer.parseInt(riga[0])] = Integer.parseInt(riga[2]);
                    } else if (next.contains("Vertici")) {
                        //riga = (next.split(" "));
                        //cij=new int[riga[1]][riga[1]];
                        iniziaLettura = true;
                    }
                    next = filebuf.readLine();
                }

                filebuf.close(); // chiude il file

            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

