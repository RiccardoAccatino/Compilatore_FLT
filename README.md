# Compilatore FLT (Linguaggio 'ac' verso 'dc')

Questo progetto consiste nell'implementazione completa di un compilatore per il linguaggio didattico **`ac`** ("adding calculator"), sviluppato in linguaggio Java. Il compilatore prende in input codice sorgente scritto in `ac` e genera in output codice eseguibile per **`dc`** (desktop calculator), una calcolatrice basata su macchina a stack.

Il progetto è stato sviluppato per il corso universitario di *Fondamenti, Linguaggi e Traduttori (FLT)*.

## 🛠 Architettura del Compilatore

Il compilatore è strutturato in una pipeline classica a più fasi, ciascuna incapsulata in un proprio package specifico:

1. **Analisi Lessicale (`scanner`)**
   - Legge il file sorgente carattere per carattere.
   - Riconosce i lessemi e genera un flusso di `Token` (parole chiave, identificatori, costanti, operatori).
   - Gestisce e segnala errori lessicali tramite `LexicalException`.

2. **Analisi Sintattica e Costruzione AST (`parser` e `ast`)**
   - Analizzatore sintattico a discesa ricorsiva predittivo (Top-Down LL).
   - Verifica che la sequenza di token rispetti la grammatica del linguaggio `ac`.
   - Costruisce l'**Abstract Syntax Tree (AST)** gerarchico, composto da nodi specifici (es. `NodeAssign`, `NodeBinOp`, `NodeDecl`, ecc.).
   - Gestisce e segnala errori di sintassi tramite `SyntacticException`.

3. **Symbol Table (`symbolTable`)**
   - Mantiene lo stato delle variabili dichiarate durante l'analisi.
   - Memorizza attributi associati a ciascun identificatore, come il tipo semantico (`LangType`) e il registro della macchina `dc` (caratteri da 'a' a 'z') su cui operare.

4. **Analisi Semantica (`visitor.TypeCheckingVisitor`)**
   - Utilizza il pattern **Visitor** per navigare l'AST.
   - Verifica che le variabili siano dichiarate prima del loro utilizzo.
   - Effettua il controllo dei tipi (Type Checking) garantendo la coerenza tra tipi interi e float.
   - Promuove le operazioni quando necessario (es. convertendo l'operazione di divisione standard in `DIV_FLOAT` se sono coinvolti operandi float).

5. **Generazione del Codice (`visitor.CodeGeneratorVisitor`)**
   - Utilizza un secondo **Visitor** per navigare l'AST già validato semanticamente.
   - Assegna dinamicamente registri (da `a` a `z`) alle variabili tramite la classe `Registri`.
   - Traduce i nodi dell'albero in istruzioni postfix per la macchina a stack `dc` (istruzioni come `sa`, `la`, `+`, `p`, `P`, `5 k / 0 k`).

## 📁 Struttura del Repository

```text
src/
├── ast/             # Nodi dell'Abstract Syntax Tree e tipi enumerati
├── parser/          # Logica del Parser a discesa ricorsiva
├── scanner/         # Analizzatore Lessicale
├── symbolTable/     # Gestione della Tabella dei Simboli (Attributes)
├── token/           # Definizione dei Token e TokenType
├── visitor/         # Interfaccia IVisitor e le sue implementazioni
└── test/            # Suite di Unit Test e file sorgenti di test (.txt)
