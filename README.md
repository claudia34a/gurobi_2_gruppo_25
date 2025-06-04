# Elaborato Gurobi - Parte II

## Dipartimento di Ingegneria dell’Informazione  
**Corso di Ricerca Operativa (INFLT, ETELT)**  
**Anno Accademico 2021/2022**

Elaborato sull’utilizzo del risolutore **Gurobi** – Parte II

---

## Obiettivo
Questo progetto affronta la risoluzione del **Problema del Commesso Viaggiatore (TSP)** su un grafo non orientato completo, utilizzando **Gurobi** attraverso la sua interfaccia Java. L’elaborato è strutturato per rispondere a tre quesiti specifici, con vincoli crescenti di complessità.

---

## Suggerimenti Utili
1. Applicare i concetti di **Programmazione Lineare** e **Programmazione Lineare Intera**.
2. Attenzioni specifiche per il codice Java:
   - Gurobi può restituire risultati numerici come `0.9999999999`: interpretare come `1`.
   - Approssimare ogni valore alla **quarta cifra decimale** per arrotondamento.
   - Le **variabili di surplus** in Gurobi sono **negative**.

---

## Istruzioni di Implementazione

1. Tutti i calcoli devono essere eseguiti tramite **codice Java**.  
   Non sono ammessi calcoli esterni manuali o tramite altri strumenti.
2. È possibile usare qualsiasi classe o metodo dell’interfaccia Java di Gurobi  
    [Gurobi Java API Documentation](https://www.gurobi.com/documentation/9.5/refman/index.html)
3. Il codice sorgente deve:
   - essere contenuto in **una sola classe Java**;
   - essere **commentato chiaramente**, evidenziando le tre sezioni dedicate ai quesiti;
   - stampare a video le risposte ai tre quesiti nel formato richiesto.
4. Accompagnare il codice con una **breve relazione PDF (max 1 pagina)** contenente:
   - il **modello risolto** per il Quesito I;
   - la descrizione della **metodologia** adottata per il Quesito II;
   - le **modifiche apportate** nel Quesito III.

---

## Consegna
- **Scadenza:** 8 Giugno 2022, ore 23:55
- **Modalità di invio:** Caricare su *Comunità Didattica* sotto “Consegna elaborato Gurobi - Parte II”:
  - il file `.java` contenente il codice sorgente
  - il file `.pdf` della relazione
  - L'elaborato sarà considerato **insufficiente** in caso di mancanza di uno dei file o di invio oltre il termine.

---

## Quesiti

### Quesito I
> Trovare la **soluzione ottima** del **TSP** sul grafo `G` (simmetrico).  
> Riportare:
- il **valore della funzione obiettivo**
- il **ciclo ottimo**

### Quesito II
> Implementare una metodologia per **verificare la presenza di un altro ciclo ottimo** con **costo equivalente**.  
> Riportare il ciclo trovato.

### Quesito III
> Modificare il modello del Quesito I per includere i seguenti **vincoli aggiuntivi**:
a. I lati incidenti al vertice `v` abbiano **costo ≤ a%** del costo totale del ciclo  
b. Se viene percorso il lato `(b1, b2)`, il **costo totale < c**  
c. Il lato `(d1, d2)` è **percorribile solo se** lo sono anche `(e1, e2)` e `(f1, f2)`  
d. Se vengono percorsi **tutti** i lati `(g1, g2)`, `(h1, h2)`, `(i1, i2)`, si paga un **costo aggiuntivo** pari a `l`

> Riportare:
- il **valore della nuova funzione obiettivo**
- il **ciclo ottimo aggiornato**

---

## 📁 File di Input
Nel file `.txt` allegato (rinominato con il nome del gruppo) è fornito:
- un elenco di parametri (per il Quesito III)
- la descrizione del grafo `G`

Esempio di formato incluso in fondo al file.

---

## 👥 Gruppo
*(Inserire qui i nomi dei componenti del gruppo)*

---

## 📌 Note Finali
⚠️ Non è consentito contattare il docente o gli assistenti per dubbi teorici o di codice.  
Sono invece ammessi chiarimenti esclusivamente sulla **modalità di consegna**.
