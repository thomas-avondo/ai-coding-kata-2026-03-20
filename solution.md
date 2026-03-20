# Solution

## Attivita di ingegnerizzazione svolte

1. Analisi del codice legacy Java in [java-kata/src/main/java/kata/LegacyCheckoutCalculator.java](java-kata/src/main/java/kata/LegacyCheckoutCalculator.java) e [java-kata/src/main/java/kata/Order.java](java-kata/src/main/java/kata/Order.java) rispetto ai vincoli descritti in [.github/copilot-instructions.md](.github/copilot-instructions.md).
2. Identificazione dei punti di rischio principali del flusso legacy: ordine di calcolo discount -> shipping -> tax, cap sconto al 40%, eccezioni Black Friday, soglie di free shipping sul discounted subtotal e regole fiscali dipendenti da paese e customer type.
3. Creazione di una suite di test caratterizzanti in [java-kata/src/test/java/kata/LegacyCheckoutCalculatorTest.java](java-kata/src/test/java/kata/LegacyCheckoutCalculatorTest.java) per fissare il comportamento legacy e coprire i nuovi requisiti del customer type `partner`.
4. Rifattorizzazione incrementale di [java-kata/src/main/java/kata/LegacyCheckoutCalculator.java](java-kata/src/main/java/kata/LegacyCheckoutCalculator.java) tramite estrazione di metodi privati dedicati a:
   - calcolo sconto base per customer type
   - calcolo sconto coupon
   - calcolo sconto Black Friday
   - calcolo shipping
   - calcolo tax
5. Introduzione del nuovo customer type `partner` senza cambiare il contratto di input/output:
   - sconto base 12%
   - coupon `PARTNER5` valido solo per partner con subtotal >= 12000
   - extra sconto Black Friday al 3% invece del 5%
   - free shipping con discounted subtotal >= 15000
6. Mantenimento del metodo `calculateTotalCents(Order)` come orchestratore lineare, riducendo la complessita locale senza riscrivere il modulo da zero.
7. Adeguamento del toolchain del progetto da Java 25 a Java 17 in [java-kata/build.gradle](java-kata/build.gradle) per poter eseguire la validazione nell'ambiente disponibile, come richiesto.
8. **Reingegnerizzazione e pattern Strategy**: Evoluzione finale con la creazione dell'enum `CustomerType` ([java-kata/src/main/java/kata/CustomerType.java](java-kata/src/main/java/kata/CustomerType.java)). Spostata la logica di calcolo specifica nei relativi enum member polimorfici:
   - `baseDiscount()`
   - `couponDiscount()`
   - `blackFridayDiscount()`
   - `hasFreeShipping()`
   - `shippingSurcharge()`
   - `taxOverridePercent()`
   In questo modo, la classe `LegacyCheckoutCalculator` viene pulita dalle lunghe code condizionali e resa estensibile per futuri Customer Type senza modifiche.
9. Scrittura dei file di supporto richiesti:
   - [prompts/analisys_prompt_result(ask-GPT-5.4).md](prompts/analisys_prompt_result(ask-GPT-5.4).md)
   - [prompts/solution-plan.md](prompts/solution-plan.md)

## Stato di validazione

- Il progetto e stato preparato per essere eseguito con Java 17.
- La suite di test JUnit copre sia il comportamento legacy rilevante sia i nuovi scenari `partner`.
- Il passo finale e l'esecuzione di `gradlew test` su Java 17 per confermare il completamento operativo del task.