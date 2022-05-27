import gurobi.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    //creazione variabili xij del modello
    private static GRBVar[][] aggiungiVariabilix(GRBModel model, int nVertici) throws GRBException {
        GRBVar[][] xij = new GRBVar[nVertici][nVertici];

        for (int i = 0; i < vertiveV; i++) {

            for (int j = 0; j < nVertici; j++) {
                xij[i][j] = model.addVar(0, 1, 0, GRB.INTEGER, "x" + i + j);
            }
        }
        return xij;
    }

    //creazione variabili uij del modello
    private static GRBVar[] aggiungiVariabiliu(GRBModel model, int nVertici) throws GRBException {
        GRBVar[] u = new GRBVar[nVertici];

        for (int j = 0; j <= nVertici-2; j++) {
            u[j] = model.addVar(1, (nVertici-1), 0, GRB.INTEGER, "u"+j);
        }

        return u;
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

        for(int j=0; j<nVertici; j++){
            GRBLinExpr expr = new GRBLinExpr();
            for(int i=0; i<nVertici; i++){
                if(i!=j) {
                    expr.addTerm(1, xij[i][j]);
                }
            }
            model.addConstr(expr, GRB.EQUAL, 1, "vincolo1 " + j);
        }

    }

    private static void aggiungiVincolo2(GRBModel model, GRBVar[][] xij,int nVertici) throws GRBException {

        for (int i = 0; i < nVertici; i++){
            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 0; j < nVertici; j++){
                if(i!=j) {
                    expr.addTerm(1, xij[i][j]);
                }
            }
            model.addConstr(expr, GRB.EQUAL, 1, "vincolo2 " + i );
        }
    }

    private static void aggiungiVincolo3(GRBModel model, GRBVar[][] xij,int nVertici, GRBVar [] u) throws GRBException {
        int cost = (nVertici - 1);
        GRBLinExpr expr5 = new GRBLinExpr();
        expr5.addConstant(cost);
        for (int i = 1; i < nVertici; i++){
            int indicei = i-1;
            for (int j = 1; j < nVertici; j++){
                int indicej = j-1;
                if(i!=j) {
                    GRBLinExpr expr = new GRBLinExpr();
                    expr.addTerm(nVertici, xij[i][j]);
                    expr.addTerm(1, u[indicei]);
                    expr.addTerm(-1, u[indicej]);
                    //System.out.println(i+","+j+" "+expr.getCoeff(i));
                    model.addConstr(expr, GRB.LESS_EQUAL,expr5, "vincolo di con ui e uj" + j);
                }
            }
        }
    }

    private static void aggiungiVincolo4(GRBModel model,GRBVar[][] xij, int [][] cij, int nVertici,double val_fobj  ){
        GRBLinExpr funzione_obiettivo_vincolo = new GRBLinExpr();
        GRBLinExpr expr5 = new GRBLinExpr();
        expr5.addConstant(val_fobj);
        for (int i = 0; i < nVertici; i++) {
            for (int j = 0; j < nVertici; j++) {
                if (i != j) {
                    funzione_obiettivo_vincolo.addTerm(cij[i][j], xij[i][j]);

                }
            }
        }
        try {
            model.addConstr(funzione_obiettivo_vincolo, GRB.EQUAL, expr5, "vincolo funzione obiettivo" );
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    private static void aggiungiVincoloB(GRBModel model, GRBVar[][] xij, int [][] cij,int c, int nVertici, int [] latob, int l, GRBVar y, double m)throws GRBException{
        GRBLinExpr expr = new GRBLinExpr();
        GRBLinExpr funzione_obiettivo = new GRBLinExpr();
        int s=latob[0];
        int t=latob[1];
        expr.addTerm(c, xij[s][t]);
        expr.addConstant(m);
        expr.addTerm(-m, xij[s][t]);

        for (int i = 0; i < nVertici; i++){
            for (int j = 0; j < nVertici; j++){
                if(i!=j)
                    funzione_obiettivo.addTerm(cij[i][j], xij[i][j]);
            }
        }
        funzione_obiettivo.addTerm(l, y);

        model.addConstr(funzione_obiettivo, GRB.LESS_EQUAL,expr, "vincoloB" );

    }


    public static void main (String[]args){
        int n_vertici = 40;
        int[][] cij = new int[n_vertici][n_vertici];//il percorso è simmetrico -> cij=cji

        int verticeV = 2;
        int a=7; //vincolo1: il costo dei lati incidenti al vertice v sia al massimo il a% del costo totale del ciclo
        int [] latob ={6, 25};
        int latoc=114;
        int[] latod = {36, 1 };
        int[] latoe = {13, 10};
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
                    //n_vertici= riga[1];
                    //cij=new int[riga[1]][riga[1]];
                    iniziaLettura = true;
                }
                next = filebuf.readLine();
            }
            filebuf.close(); // chiude il file
        } catch (IOException e) {
            System.out.println(e);
        }

        GRBEnv env;
        try {
            env = new GRBEnv("elaborato1Gurobi.log");
            //env.set(GRB.IntParam.Presolve, 0);
            //env.set(GRB.IntParam.Method, 0);

            GRBModel model = new GRBModel(env);

//-------------- AGGIUNTA VARIABILI -----------------------------------
            GRBVar[][] xij = aggiungiVariabilix(model, n_vertici); //variabile binaria  ed è uguale a 1 se l'arco (i,j) appartiene al circuito, altrimenti xij=0
            GRBVar[] u = aggiungiVariabiliu(model, n_vertici);
// -------------- FUNZIONE OBIETTIVO ----------------------------------
            aggiungiFunzioneObiettivo(model, cij, xij, n_vertici);


//-------------------- VINCOLI ----------------------------------------
            aggiungiVincolo1(model, xij, n_vertici);
            aggiungiVincolo2(model, xij, n_vertici);
            aggiungiVincolo3(model, xij, n_vertici, u);

            model.optimize();   //ottimizzazione
//----------------------------------------------------------------------


            System.out.println("\nGRUPPO gruppo_25");
            System.out.println("Componenti: Brognoli e Agosti\n\n");
//------------------------------- QUESITO 1 ---------------------------------------------------------------------------
            System.out.println("QUESITO I:");
            System.out.println("funzione obiettivo = " + model.get(GRB.DoubleAttr.ObjVal));
            System.out.print( "ciclo ottimo 1 = [");




        } catch (GRBException e){
            e.printStackTrace();
        }



    }
}