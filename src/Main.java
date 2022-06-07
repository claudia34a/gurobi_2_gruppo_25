import gurobi.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    //creazione variabili xij
    private static GRBVar[][] aggiungiVariabilix(GRBModel model, int nVertici) throws GRBException {
        GRBVar[][] xij = new GRBVar[nVertici][nVertici];

        for (int i = 0; i < nVertici; i++) {

            for (int j = 0; j < nVertici; j++) {
                xij[i][j] = model.addVar(0, 1, 0, GRB.BINARY, "x" + i + j);
            }
        }
        return xij;
    }

    //creazione variabili u
    private static GRBVar[] aggiungiVariabiliu(GRBModel model, int nVertici) throws GRBException {
        GRBVar[] u = new GRBVar[nVertici];

        for (int j = 0; j <= nVertici-2; j++) {
            u[j] = model.addVar(1, (nVertici-1), 0, GRB.INTEGER, "u"+j);
        }

        return u;
    }

    // creazione variabili z utilizzate vincolo [d] terza richiesta
    private static GRBVar[] aggiungiVariabiliz(GRBModel model, int nVertici) throws GRBException {
        GRBVar[] z = new GRBVar[nVertici];

        for (int j = 0; j < 4; j++) {
            z[j] = model.addVar(0, 1, 0, GRB.BINARY, "z" +  j);
        }

        return z;
    }

    // aggiunta funzione obiettivo modello principale
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

    // aggiunta funzione obiettivo modello terza richiesta
    private static void aggiungiFunzioneObiettivo(GRBModel model, int [][] cij, GRBVar [][] xij, int nVertici, GRBVar y, int l) throws GRBException {
        GRBLinExpr funzione_obiettivo = new GRBLinExpr();
        //sommatoria Cij * Xij
        for (int i = 0; i < nVertici; i++){
            for (int j = 0; j < nVertici; j++){
                if(i!=j)
                    funzione_obiettivo.addTerm(cij[i][j], xij[i][j]);
            }
        }
        funzione_obiettivo.addTerm(l, y);
        model.setObjective(funzione_obiettivo);
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
    }

    //vincolo 1: sommatoria Xij = 1    j fissato
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

    // vincolo 2: sommatoria Xij = 1    i fissato
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

    //vincolo u: Ui - Uj + (n-1)Xij <= n - 2
    private static void aggiungiVincolo3(GRBModel model, GRBVar[][] xij,int nVertici, GRBVar [] u) throws GRBException {
        int cost = (nVertici - 2);
        GRBLinExpr expr5 = new GRBLinExpr();
        expr5.addConstant(cost);
        for (int i = 1; i < nVertici; i++){
            int indicei = i-1;
            for (int j = 1; j < nVertici; j++){
                int indicej = j-1;
                if(i!=j) {
                    GRBLinExpr expr = new GRBLinExpr();
                    expr.addTerm((nVertici -1), xij[i][j]);
                    expr.addTerm(1, u[indicei]);
                    expr.addTerm(-1, u[indicej]);
                    model.addConstr(expr, GRB.LESS_EQUAL,expr5, "vincolo di con ui e uj" + j);
                }
            }
        }
    }

    //seconda richiesta, vincolo: valore funzione obiettivo modelloII = valore otimo del primo modello
    private static void aggiungiVincolo4(GRBModel model,GRBVar[][] xij, int [][] cij, int nVertici,double val_fobj )throws GRBException{
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
        model.addConstr(funzione_obiettivo_vincolo, GRB.EQUAL, expr5, "vincolo funzione obiettivo" );
    }

    //terza richiesta, punto a: il costo dei lati incidenti al vertice v sia al massimo il a% del costo totale del ciclo;
    private static void aggiungiVincoloA(GRBModel model, GRBVar[][] xij, int [][] cij, int nVertici, int a, int v)throws GRBException{

        GRBLinExpr expr = new GRBLinExpr();
        GRBLinExpr funzione_obiettivo = new GRBLinExpr();

        for (int i=0; i<nVertici; i++){
            expr.addTerm(cij[v][i], xij[v][i]);
            expr.addTerm(cij[i][v], xij[i][v]);
        }

        for (int i = 0; i < nVertici; i++){
            for (int j = 0; j < nVertici; j++){
                if(i!=j)
                    funzione_obiettivo.addTerm((double)a*cij[i][j]/100, xij[i][j]);
            }
        }

        model.addConstr(expr, GRB.LESS_EQUAL, funzione_obiettivo, "vincoloA_vj" );
    }

    //terza richiesta, vincolo b: se il lato (b1, b2) viene percorso, il costo del ciclo ottimo sia inferiore a c;
    private static void aggiungiVincoloB(GRBModel model, GRBVar[][] xij, int [][] cij,int c, int nVertici, int [] latob, double m)throws GRBException{
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

        model.addConstr(funzione_obiettivo, GRB.LESS_EQUAL,expr, "vincoloB" );

    }

    //terza richiesta vincolo c: il lato (d1, d2) sia percorribile se e solo se sono percorsi anche i lati (e1, e2) e (f1, f2);
    private static void aggiungiVincoloC(GRBModel model, GRBVar[][] xij,  int [] latoe, int []latof, int [] latod)throws GRBException{
        GRBLinExpr expr = new GRBLinExpr();
        GRBLinExpr expr5 = new GRBLinExpr();

        expr.addTerm(1, xij[latoe[0]][latoe[1]]);
        expr.addTerm(1, xij[latof[0]][latof[1]]);


        expr5.addTerm(2, xij[latod[0]][latod[1]]);

        model.addConstr(expr, GRB.GREATER_EQUAL,expr5, "vincoloC" );

    }

    //terza richiesta, vincolo d: nel caso in cui i lati (g1, g2), (h1, h2) e (i1, i2) vengano tutti percorsi, si debba pagare un costo aggiuntivo pari a l
    private static void aggiungiVincoloD(GRBModel model, GRBVar[]z, GRBVar [][]xij, int [] latog, int[] latoh, int[] latoi)throws GRBException{
        GRBLinExpr expr = new GRBLinExpr();
        GRBLinExpr expr5 = new GRBLinExpr();

        expr.addTerm(1, xij[latog[0]][latog[1]]);   //xij lato g
        expr.addTerm(1, xij[latoh[0]][latoh[1]]);   //xij lato h
        expr.addTerm(1, xij[latoi[0]][latoi[1]]);   //xij lato i

        // Xg+Xh+Xi = 0 Z0+  Z1 + 2 Z2 + 3 Z3
        for (int i = 0; i < 4; i++){
            expr5.addTerm(i, z[i]);
        }

        model.addConstr(expr, GRB.EQUAL,expr5, "vincoloD" );

        GRBLinExpr exprz = new GRBLinExpr();
        //  Z0+ Z1 + Z2 + Z3 = 1
        for (int i = 0; i < 4; i++){
            exprz.addTerm(1, z[i]);
            model.addConstr(exprz, GRB.EQUAL,1, "vincoloD-BIS" );//la sommatoria delle z deve fare 1
        }

    }


    public static void main (String[]args){
        int n_vertici = 40;
        int[][] cij = new int[n_vertici][n_vertici];//il percorso è simmetrico -> cij=cji

        int verticeV = 2;
        int a=7; //vincolo1: il costo dei lati incidenti al vertice v sia al massimo il a% del costo totale del ciclo
        int [] latob ={6, 25};
        int c=114; //vincolo b) se il latob viene percorso, il costo del ciclo ottimo sia inferiore a c
        int[] latod = {36, 1 };
        int[] latoe = {13, 10};
        int[] latof = {32, 2};
        int[] latog = {2, 8};
        int[] latoh = {16, 39};
        int[] latoi = {7, 9};
        int l = 2;      //costo aggiuntivo

        String nome_file = "coppia25.txt";
//----------------------LETTURA DATI DA FILE-------------------
        try {
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
                    iniziaLettura = true;
                }
                next = filebuf.readLine();
            }
            filebuf.close(); // chiude il file
        } catch (IOException e) {
            System.out.println(e);
        }
//------------------------------------------------------------------------------
        GRBEnv env;
        try {
            env = new GRBEnv("elaborato1Gurobi.log");

//*************************** QUESITO 1 ********************************************************************************
            GRBModel model = new GRBModel(env);

    //-------------- AGGIUNTA VARIABILI -----------------------------------
            GRBVar[][] xij = aggiungiVariabilix(model, n_vertici); //variabile binaria  ed è uguale a 1 se l'arco (i,j) appartiene al circuito, altrimenti xij=0
            GRBVar[] u = aggiungiVariabiliu(model, n_vertici);  //variabili fittizie per ordinare i nodi che vengono visitati;

    // -------------- FUNZIONE OBIETTIVO ----------------------------------
            aggiungiFunzioneObiettivo(model, cij, xij, n_vertici);

    //-------------------- VINCOLI ----------------------------------------
            aggiungiVincolo1(model, xij, n_vertici);
            aggiungiVincolo2(model, xij, n_vertici);
            aggiungiVincolo3(model, xij, n_vertici, u);

            model.optimize();   //ottimizzazione
    //----------------------------------------------------------------------

//*************************** QUESITO 2 ********************************************************************************
            double objval= model.get(GRB.DoubleAttr.ObjVal);

            GRBModel model_II = new GRBModel(env);

            GRBVar[][] xij_II = aggiungiVariabilix(model_II, n_vertici);
            GRBVar[] u_II = aggiungiVariabiliu(model_II, n_vertici);

            aggiungiFunzioneObiettivo(model_II, cij, xij_II, n_vertici);

            aggiungiVincolo1(model_II, xij_II, n_vertici);
            aggiungiVincolo2(model_II, xij_II, n_vertici);
            aggiungiVincolo3(model_II, xij_II, n_vertici, u_II);

            aggiungiVincolo4(model_II, xij_II,cij,n_vertici, objval);

            //creazione vincolo per trovare una soluzione diversa dalla soluzione trovata nel mod0ello "quesito 1"
            //almeno una xij diversa => sommatoria delle xij che erano ad 1 <= numero nodi - 1
            GRBLinExpr expr = new GRBLinExpr();
            int k=0;
            int r;
            do{
                for(r=0; r<n_vertici; r++){
                    if(xij[k][r].get(GRB.DoubleAttr.X)!=0){
                        expr.addTerm(1, xij_II[k][r]);
                        break;
                    }
                }
                k=r;
            }while(r!=0);

            model_II.addConstr(expr, GRB.LESS_EQUAL,(n_vertici-1), "vincoloC" );
            model_II.optimize();

//*************************** QUESITO 3 ********************************************************************************
            GRBModel model_III = new GRBModel(env);

            GRBVar[][] xij_III = aggiungiVariabilix(model_III, n_vertici);
            GRBVar[] u_III = aggiungiVariabiliu(model_III, n_vertici);
            GRBVar[] z_III =aggiungiVariabiliz(model_III,n_vertici);
            aggiungiFunzioneObiettivo(model_III, cij, xij_III, n_vertici, z_III[3], l);

            aggiungiVincolo1(model_III, xij_III, n_vertici);
            aggiungiVincolo2(model_III, xij_III, n_vertici);
            aggiungiVincolo3(model_III, xij_III, n_vertici, u_III);
            aggiungiVincoloA(model_III, xij_III, cij, n_vertici, a, verticeV);

            //---------- calcolo M --------
            /*
             * vincolo:  f <= cy + (1-y) M
             * come valore di M posso prendere:
             *  - max f ....risolvere il modello
             *  - valore molto grande deciso arbitrariamente
             *  - sommo il costo massimo di ogni riga della matrice dei costi (costi sulla diagonale = 0)
             */
            double M=0;
            double max;
            //sommo il costo massimo di ogni riga della matrice dei costi (costi sulla diagonale = 0)
            for(int i=0; i<n_vertici; i++){
                max=0;
                for(int j=0; j<n_vertici; j++){
                    if(cij[i][j]>max){
                        max=cij[i][j];
                    }
                }
                M+=max;
            }
            aggiungiVincoloB(model_III, xij_III, cij,c,  n_vertici,latob, M);
            aggiungiVincoloC(model_III,xij_III, latoe,latof, latod);
            aggiungiVincoloD(model_III, z_III, xij_III, latog,  latoh, latoi);

            model_III.optimize();



//************************** OUTPUT ******************************************************************************
            int i=0;
            int j;
            System.out.println("\n\n\nGRUPPO gruppo_25");
            System.out.println("Componenti: Brognoli e Agosti\n\n");

//------------------------------- QUESITO 1 ---------------------------------------------------------------------------
            System.out.println("QUESITO I:");
            System.out.println("funzione obiettivo = " + model.get(GRB.DoubleAttr.ObjVal));
            System.out.print( "ciclo ottimo 1 = [");
            do{
                for(j=0; j<n_vertici; j++){
                    if(xij[i][j].get(GRB.DoubleAttr.X)!=0){
                        System.out.print(i+", ");
                        break;
                    }
                }
                i=j;
            }while(j!=0);
            System.out.print("0]");
            System.out.println("\n");

//---------------------QUESITO II--------------------------------------------------------------------------------------
            System.out.println("QUESITO II:");
            System.out.print( "ciclo ottimo 2 = [");
            i=0;
            do{
                for(j=0; j<n_vertici; j++){
                    if(xij_II[i][j].get(GRB.DoubleAttr.X)!=0){
                        System.out.print(i+", ");
                        break;
                    }
                }
                i=j;
            }while(j!=0);

            System.out.print("0]");
            System.out.println("\n");


//---------------------QUESITO III--------------------------------------------------------------------------------------
            System.out.println("QUESITO III:");
            System.out.println("funzione obiettivo = " + model_III.get(GRB.DoubleAttr.ObjVal));
            System.out.print( "ciclo ottimo 3 = [");

            int p=0;
            int q;

            do{
                for(q=0; q<n_vertici; q++){
                    if(xij_III[p][q].get(GRB.DoubleAttr.X)!=0){
                        System.out.print(p+", ");
                        break;
                    }
                }
                p=q;
            }while(q!=0);
            System.out.print("0]");
            System.out.println();

        } catch (GRBException e){
            e.printStackTrace();
        }
    }
}