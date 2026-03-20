## Plan: Refactor Java Kata Checkout

Analizzare e rifattorizzare in modo incrementale il modulo legacy di pricing Java preservando il comportamento esistente, aggiungendo un safety net di test prima dei cambi strutturali e introducendo il nuovo customer type `partner` senza aumentare la complessita del flusso principale. L'approccio raccomandato e: caratterizzazione del comportamento attuale, estrazione mirata delle responsabilita dal metodo `calculateTotalCents`, quindi integrazione delle nuove regole `partner` nelle componenti estratte.

**Steps**
1. Mappare il comportamento attuale di `LegacyCheckoutCalculator.calculateTotalCents(Order)` in [java-kata/src/main/java/kata/LegacyCheckoutCalculator.java](java-kata/src/main/java/kata/LegacyCheckoutCalculator.java), separando sconti, shipping e tax.
2. Identificare e documentare le regole implicite da preservare: cap al 40%, esclusione `employee` dal Black Friday, soglie di free shipping sul discounted subtotal, eccezioni fiscali per `vip` e `TAXFREE`.
3. Creare un safety net con test caratterizzanti sui casi legacy esistenti prima di qualunque refactoring. Questo step blocca i successivi.
4. Estrarre dal metodo centrale responsabilita semplici e focalizzate:
   - logica sconto per customer type
   - logica coupon e promozioni stagionali
   - calcolo shipping basato su country e discounted subtotal
   - calcolo tax
5. Mantenere `calculateTotalCents(Order)` come orchestratore lineare, senza aggiungere nuove catene condizionali principali.
6. Integrare il nuovo customer type `partner` nelle responsabilita estratte con queste regole:
   - sconto base 12%
   - free shipping con discounted subtotal >= 15000
   - coupon `PARTNER5` valido solo per `partner` e subtotal >= 12000
   - a Black Friday extra sconto 3% invece di 5%
7. Verificare che il comportamento legacy resti invariato fuori dallo scope `partner` e che l'estensione futura di customer type o promozioni richieda modifiche locali, non nel flusso principale.

**Relevant files**
- [java-kata/src/main/java/kata/LegacyCheckoutCalculator.java](java-kata/src/main/java/kata/LegacyCheckoutCalculator.java) - contiene tutta la logica legacy da caratterizzare e poi semplificare
- [java-kata/src/main/java/kata/Order.java](java-kata/src/main/java/kata/Order.java) - contratto di input da lasciare invariato
- [.github/copilot-instructions.md](.github/copilot-instructions.md) - vincoli funzionali e architetturali da rispettare

**Verification**
1. Aggiungere test per customer type esistenti: `vip`, `premium` sotto e sopra soglia, `employee`, `regular`, `new`.
2. Aggiungere test per coupon esistenti: `SAVE10`, `VIPONLY`, `BULK`, `FREESHIP`, `TAXFREE`, con casi validi e non validi.
3. Aggiungere test per interazioni critiche:
   - Black Friday non applicato a `employee`
   - surcharge shipping US a Black Friday
   - free shipping basato su discounted subtotal
   - cap sconto al 40%
   - eccezione tax `vip` in IT
   - surcharge shipping `employee` fuori IT
4. Aggiungere test specifici per `partner` sui quattro nuovi requisiti.
5. Eseguire la suite Java e confrontare i risultati dei casi legacy per confermare l'assenza di regressioni.

**Decisions**
- Incluso: analisi del comportamento osservato, safety net, direzione di refactoring e supporto `partner`.
- Escluso: riscrittura completa del modulo.
- Vincolo chiave: non cambiare il contratto input/output e non introdurre nuova complessita nel flusso principale.
- Strategia consigliata: refactoring incrementale guidato da test, non redesign totale.

---

## Risultato dell'Attività di Reingegnerizzazione (Fase 2)

L'obiettivo di semplificare l'aggiunta di nuovi customer type è stato raggiunto applicando il **Pattern Strategy** basato su un Enum in Java.

**Azioni completate:**
1. **Creazione dell'Enum `CustomerType`**: Estratta tutta la logica "specifica per cliente" in un file dedicato ([java-kata/src/main/java/kata/CustomerType.java](../java-kata/src/main/java/kata/CustomerType.java)). I vari elementi (`VIP`, `PREMIUM`, `EMPLOYEE`, `PARTNER`, `REGULAR`) implementano metodi specifici polimorfici:
   - `baseDiscount()`
   - `couponDiscount()` (islando le logiche dei coupon che dipendono dal customer, es. VIPONLY, PARTNER5)
   - `blackFridayDiscount()`
   - `hasFreeShipping()`
   - `shippingSurcharge()`
   - `taxOverridePercent()`
2. **Deresponsabilizzazione di `LegacyCheckoutCalculator`**: Rimosse le lunghe catene di `if/else` legate ai Customer. Il calcolatore ora si limita a recuperare l'istanza corretta di `CustomerType` e a delegare ad essa il calcolo delle componenti di business che dipendono dal tipo di cliente, combinandole con le regole orizzontali e globali.
3. **Migliorata l'Estensibilità**: Aggiungere un nuovo cliente in futuro (es. `AFFILIATE` o `WHOLESALE`) richiederà unicamente l'aggiunta di un nuovo valore all'Enum `CustomerType` definendo le sue peculiarità in sovrascrittura (`@Override`). **Zero** modifiche saranno necessarie nel corpo di `LegacyCheckoutCalculator`.
4. **Validazione Mantenuta**: Test suite `LegacyCheckoutCalculatorTest` eseguita con successo, confermando zero regressioni architetturali né alterazione dei contratti in I/O.