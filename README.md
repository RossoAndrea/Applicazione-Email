# Applicazione Email Client-Server Java
Questa applicazione implementa un sistema di posta elettronica client-server sviluppato interamente in Java. Il progetto permette la gestione, l’invio e la ricezione di email tramite un’interfaccia desktop intuitiva e una comunicazione di rete efficiente.

## Funzionalità principali
- Gestione email: invio, ricezione, visualizzazione e cancellazione di messaggi.
- Autenticazione utenti: login sicuro per ogni utente.
- Server multithreaded: gestione concorrente di più client tramite thread pool configurabile.
- Mutua esclusione: accesso sicuro alle caselle di posta tramite meccanismi di lock custom.
- Interfaccia grafica: client desktop realizzato con JavaFX.
- Protocollo JSON-over-socket: comunicazione tra client e server tramite messaggi JSON per garantire interoperabilità ed estendibilità.
## Tecnologie utilizzate
- Java
- JavaFX
- Socket TCP
- JSON
- Maven
## Avvio rapido
1. Clona la repository.
2. Compila ed esegui il server.
3. Compila ed esegui il client.
4. Effettua il login e inizia a utilizzare il sistema di posta elettronica.
## Note
Il progetto è pensato per scopi didattici e può essere esteso facilmente per includere nuove funzionalità o integrare sistemi di autenticazione avanzata.
