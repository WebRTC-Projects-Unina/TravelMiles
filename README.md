# Web-and-Real-Time-Communications
Web and Real Time Communication @UNINA (a.a. 2024/2025)



# 🌍 Travel Miles
Il progetto consiste in una Web Application dedicata ad un'agenzia di viaggi, progettata per instaurare una community tra gli utenti attraverso il sistema di commenti integrato all'interno delle pagine relative alle città prenotabili.


---

## 🚀 **Tecnologie Utilizzate**

### **Backend**
- **Framework**: [Spring Boot 🍃](https://spring.io/projects/spring-boot)  
  Controller che gestisce la connessione al database, controllo complessità password, hashing delle password, nonché gestione dei commenti e dettagli utente.

- **Database**: [MySQL](https://www.mysql.com/)  
  MySQL è utilizzato per la gestione e persistenza dei dati.

---

### **Frontend**
- **Linguaggio**: [JavaScript](https://developer.mozilla.org/it/docs/Web/JavaScript)  
  JavaScript utilizzato per garantire pagine web dinamiche.

- **Tecnologia**: [AJAX](https://developer.mozilla.org/it/docs/Web/Guide/AJAX)  
  Utilizzo di AJAX per la gestione delle chiamate asincrone tra frontend e backend, rendendo migliore l'esperienza degli utenti.

---

### **Comunicazione in Tempo Reale**
- **WebRTC DataChannel 🌐**: [WebRTC](https://webrtc.org/)  
  Chat real-time **peer-to-peer**  grazie a WebRTC DataChannel con File Transfer.

- **WebSocket Signaling Server**: [SpringBoot WebSocket]([https://webrtc.org/](https://docs.spring.io/spring-framework/reference/web/websocket.html))  
  Signaling Server realizzato attraverso WebSocket gestita dal controller SpringBoot.

---

## ⚙️ **Funzionalità Principali**

1. **Sistema di Commenti**  
   - Gli utenti possono lasciare commenti nelle pagine dedicate alle città prenotabili.
   - Gli utenti possono modificare i loro commenti.
   - Gli utenti possono rispondere ai commenti.

2. **Comunicazione Real-Time**  
   - Utilizzo del **DataChannel WebRTC** per scambiare messaggi tra utenti e consulenti in tempo reale.
   - File Transfer per lo scambio di file tra utenti e consulenti.
  
3. **Signaling Server con WebSocket**  
   - Signaling server realizzato con WebSocket, che permette lo scambio di informazioni SDP e ICE Candidates, al fine di garantire la corretta negoziazione tra i peer per instaurare il DataChannel WebRTC.

4. **Gestione Backend**  
   - Persistenza dei dati realizzata grazie alla connessione tramite JDBC al database MySQL.
   - Hashing delle password prima di salvare sul DB.
   - Controllo complessità password.
  
---

## 📁 **Struttura principale del Progetto**

```plaintext

src/main/java
│
├── SpringBootApplication.java               
├── Controller.java
├── Entities.java/          
├── Repository.java/     
|
src/main/resources
│
├── static/               
│   ├── css/        
│   ├── images/          
│   ├── js/     
│   └── html/         
|
│
├─── application.properties  #file di configurazione
|
pom.xml
```


> **📌 Nota:**  
> Per effettuare il **dump** del database, utilizzare il seguente comando:
> 
> ```bash
> mysqldump -u [utente] -p [nome_database] > [percorso_file_backup].sql
> ```
>
> Per **ripristinare** un backup, utilizzare:
> 
> ```bash
> mysql -u [utente] -p [nome_database] < [percorso_file_backup].sql
> ```

