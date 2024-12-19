package com.mypackage.controller;

import com.mypackage.entity.Utenti;
import com.mypackage.entity.WeatherService;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.util.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import com.mypackage.entity.Consulenti;
import com.mypackage.entity.Account;
import com.mypackage.entity.City;
import com.mypackage.entity.CityComments;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.mypackage.repository.UtentiRepository;
import com.mypackage.repository.ConsulentiRepository;
import com.mypackage.repository.AccountRepository;
import com.mypackage.repository.CityCommentsRepository;
import com.mypackage.repository.CityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class Controller {

    @Autowired
    private UtentiRepository utentiRepository;

    @Autowired
    private ConsulentiRepository consulentiRepository;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private CityCommentsRepository cityCommentsRepository;
    
    
    @Autowired
    private WeatherService weatherService;
     
    
    private boolean isValidPassword(String password) {
        
        String specialChars = "!£$%&/()=?^'[]@#*";
        boolean hasSpecialChar = false;

        for (char c : specialChars.toCharArray()) {
            if (password.indexOf(c) >= 0) {
                hasSpecialChar = true;
                break;
            }
        }

        return password.length() > 15 && hasSpecialChar;
    }
    
    
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
        @RequestParam("username") String username,
        @RequestParam("nome") String nome,
        @RequestParam("cognome") String cognome,
        @RequestParam("nickname") String nickname,
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        HttpServletResponse response
    ) throws IOException {

        
        if (utentiRepository.existsByUsername(username) || utentiRepository.existsByEmail(email)) {
            response.sendRedirect("/problem.html?error=Username o email gi%C3%A0 esistenti.");
            return ResponseEntity.badRequest().body("Username o email gi%C3%A0 esistenti.");
        }

        
        if (accountRepository.existsByUsername(username)) {
            response.sendRedirect("/problem.html?error=Username%20gi%C3%A0%20presente%20in%20Account.");
            return ResponseEntity.badRequest().body("Username già presente in Account.");
        }

       
        if (!isValidPassword(password)) {
            response.sendRedirect("/problem.html?error=La%20password%20deve%20contenere%20almeno%20uno%20dei%20caratteri%20speciali%3A%20%21%C2%A3%24%25%26%2F%28%29%3D%3F%5E%27%5B%5D%40%23%2A%20e%20deve%20essere%20lunga%20pi%C3%B9%20di%2015%20caratteri.");
            return ResponseEntity.badRequest().body("La password deve contenere almeno uno dei caratteri speciali: !£$%&/()=?^'[]@#* e deve essere lunga più di 15 caratteri.");
        }

        //Creo un'istanza di BCryptPasswordEncoder per fare l'hashing della password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password); //Calcolo l'hash della password

       
        Account account = new Account(username, hashedPassword);
        accountRepository.save(account);

       
        Utenti utenti = new Utenti(username, nome, cognome, nickname, email);
        utentiRepository.save(utenti);

       
        response.sendRedirect("/login.html");
        return ResponseEntity.ok("Utente registrato con successo!");
    }



     
    @PostMapping("/registerConsultant")
    public ResponseEntity<String> registerConsultant(
        @RequestParam("username") String username,
        @RequestParam("nome") String nome,
        @RequestParam("cognome") String cognome,
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        HttpServletResponse response
    ) throws IOException {
        
        
        if (consulentiRepository.existsByUsername(username) || consulentiRepository.existsByEmail(email)) {
            response.sendRedirect("/problem.html?error=Username%20o%20email%20gi%C3%A0%20esistenti.");
            return ResponseEntity.badRequest().body("Username o email gi%C3%A0 esistenti.");
        }
        
       
        if (accountRepository.existsByUsername(username)) {
            response.sendRedirect("/problem.html?error=Username%20gi%C3%A0%20presente%20in%20Account.");
            return ResponseEntity.badRequest().body("Username già presente in Account.");
        }

        //Validazione della password
        if (!isValidPassword(password)) {
            String errorMessage = "La password deve contenere almeno uno dei caratteri speciali: !£$%&/()=?^'[]@#* e deve essere lunga più di 15 caratteri.";
            String encodedMessage = java.net.URLEncoder.encode(errorMessage, "UTF-8");
            response.sendRedirect("/problem.html?error=" + encodedMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password); //Calcolo l'hash della password

        
        Account account = new Account(username, hashedPassword);
        accountRepository.save(account);

     
        Consulenti consulenti = new Consulenti(username, nome, cognome, email);
        consulentiRepository.save(consulenti);

    
        response.sendRedirect("/loginConsultant.html");
        return ResponseEntity.ok("Registrazione avvenuta con successo!");
    }


    
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        HttpServletResponse response,
        HttpServletRequest request
    ) throws IOException {

        Utenti utenti = utentiRepository.findByEmail(email);

        if (utenti == null) {
        	response.sendRedirect("/problem.html?error=Email non registrata");
            return ResponseEntity.badRequest().body("Email non registrata.");
        }

        String username = utenti.getUsername();

        Account account = accountRepository.findByUsername(username);
        
        if (account == null) {
            response.sendRedirect("/problem.html?error=Account non trovato.");
            return ResponseEntity.badRequest().body("Account non trovato.");
        }
        
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(password, account.getPassword())) {
            response.sendRedirect("/problem.html?error=Password errata.");
            return ResponseEntity.badRequest().body("Password errata.");
        }

        try {
        	request.getSession().setAttribute("username", username);
            response.sendRedirect("/home.html?username=" + username);
     
            return ResponseEntity.ok("Redirecting to home...");
        } catch (IOException e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body("Errore durante il reindirizzamento.");
        }
    }
    
    
    

    @PostMapping("/loginConsultant")
    public ResponseEntity<String> loginConsultant(
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        HttpServletResponse response,
        HttpServletRequest request
    ) throws IOException {
        Consulenti consulenti = consulentiRepository.findByEmail(email);

        if (consulenti == null) {
        	response.sendRedirect("/problem.html?error=Email non registrata");
            return ResponseEntity.badRequest().body("Email non registrata.");
        }

        String username = consulenti.getUsername();

        Account account = accountRepository.findByUsername(username);
        
        if (account == null) {
            response.sendRedirect("/problem.html?error=Account non trovato.");
            return ResponseEntity.badRequest().body("Account non trovato.");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //Confronto la password inserita con l'hash salvato nel database
        if (!passwordEncoder.matches(password, account.getPassword())) {
            response.sendRedirect("/problem.html?error=Password errata.");
            return ResponseEntity.badRequest().body("Password errata.");
        }
        

        try {
        	request.getSession().setAttribute("username", username);
            response.sendRedirect("/home-consultant.html");
     
            return ResponseEntity.ok("Redirecting to home...");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore durante il reindirizzamento.");
        }
    }    
    


    
    
    
    @GetMapping("/user-details")
    public ResponseEntity<String> getUserDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            response.sendRedirect("/problem.html?error=Errore durante l'ottenimento dell'username.");
            return ResponseEntity.status(500).body("Errore durante l'ottenimento dell'username");
        }

        Utenti utenti = utentiRepository.findByUsername(username);

        if (utenti == null) {
            response.sendRedirect("/problem.html?error=Errore durante l'ottenimento dei dettagli dell'utente.");
            return ResponseEntity.status(500).body("Errore durante l'ottenimento dei dettagli dell'utente");
        }

        request.getSession().setAttribute("nome", utenti.getNome());
        request.getSession().setAttribute("cognome", utenti.getCognome());
        request.getSession().setAttribute("nickname", utenti.getNickname());
        request.getSession().setAttribute("email", utenti.getEmail());

        response.sendRedirect("/user-details.html");
        return ResponseEntity.ok("Dettagli dell'utente ottenuti con successo");
    }

    
    @GetMapping("/consultant-details")
    public ResponseEntity<String> getConsultantDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            response.sendRedirect("/problem.html?error=Errore durante l'ottenimento del consulente.");
            return ResponseEntity.status(500).body("Errore durante l'ottenimento del consulente");
        }

        Consulenti consulenti = consulentiRepository.findByUsername(username);

        if (consulenti == null) {
            response.sendRedirect("/problem.html?error=Errore durante l'ottenimento dei dettagli del consulente.");
            return ResponseEntity.status(500).body("Errore durante l'ottenimento dei dettagli del consulente");
        }

        request.getSession().setAttribute("nome", consulenti.getNome());
        request.getSession().setAttribute("cognome", consulenti.getCognome());
        request.getSession().setAttribute("email", consulenti.getEmail());

        response.sendRedirect("/consultant-details.html");
        return ResponseEntity.ok("Dettagli del consulente ottenuti con successo");
    }


    @GetMapping("/get-user-details")
    @ResponseBody //Il valore di ritorno sarà scritto nel corpo della risposta
    public Map<String, String> getUserDetailsFromSession(HttpServletRequest request) {
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("nome", (String) request.getSession().getAttribute("nome"));
        userDetails.put("cognome", (String) request.getSession().getAttribute("cognome"));
        userDetails.put("nickname", (String) request.getSession().getAttribute("nickname"));
        userDetails.put("email", (String) request.getSession().getAttribute("email"));
        userDetails.put("username", (String) request.getSession().getAttribute("username"));

        return userDetails;
    }

    @GetMapping("/get-username")
    @ResponseBody
    public String getUsername(HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        System.out.println("Chiamata a /get-username. Username dalla sessione: " + username);
        return username != null ? username : "";  //Operatore ternario->  Condizione ? espressioneSeVera : espressioneSeFalsa;
    }
    
    
    @GetMapping("/get-consultant-details")
    @ResponseBody //Il valore di ritorno sarà scritto nel corpo della risposta
    public Map<String, String> getConsultantDetailsFromSession(HttpServletRequest request) {
        Map<String, String> consultantDetails = new HashMap<>();
        consultantDetails.put("nome", (String) request.getSession().getAttribute("nome"));
        consultantDetails.put("cognome", (String) request.getSession().getAttribute("cognome"));
        consultantDetails.put("email", (String) request.getSession().getAttribute("email"));
        consultantDetails.put("username", (String) request.getSession().getAttribute("username")); // Non visibile

        return consultantDetails;
    }

    @GetMapping("/get-consultant")
    @ResponseBody
    public String getUsernameConsultant(HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        return username != null ? username : "";  //Operatore ternario->  Condizione ? espressioneSeVera : espressioneSeFalsa;
    }

    

    @PostMapping("/modifica")
    public ResponseEntity<String> modificaCampo(
            @RequestParam("campo") String campo,
            @RequestParam("valore") String valore,
            @RequestParam("username") String username) {

      
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username non valido.");
        }

     
        Utenti utenti = utentiRepository.findByUsername(username);
        if (utenti == null) {
            return ResponseEntity.badRequest().body("Utente non trovato.");
        }

        switch (campo) {
            case "nome":
                utenti.setNome(valore);
                break;
            case "cognome":
                utenti.setCognome(valore);
                break;
            case "nickname":
                utenti.setNickname(valore);
                break;
            case "email":
                utenti.setEmail(valore);
                break;
            case "password":
                
                if (!isValidPassword(valore)) {
                    return ResponseEntity.badRequest().body("Password non valida.");
                }
                //Creo un'istanza di BCryptPasswordEncoder per fare l'hashing della password
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(valore); // Calcola l'hash della password
            
                utenti.getAccount().setPassword(hashedPassword);
                
                break;
            default:
                return ResponseEntity.badRequest().body("Campo non valido.");
        }


        utentiRepository.save(utenti);

        return ResponseEntity.ok("Campo modificato con successo!");
    }


    @PostMapping("/modifica-consulenti")
    public ResponseEntity<String> modificaCampoConsulenti(
            @RequestParam("campo") String campo,
            @RequestParam("valore") String valore,
            @RequestParam("username") String username) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username non valido.");
        }

        Consulenti consulenti = consulentiRepository.findByUsername(username);

        if (consulenti == null) {
            return ResponseEntity.badRequest().body("Utente non trovato.");
        }

        switch (campo) {
            case "nome":
                consulenti.setNome(valore);
                break;
            case "cognome":
                consulenti.setCognome(valore);
                break;
            case "email":
                consulenti.setEmail(valore);
                break;
            case "password":
                
                if (!isValidPassword(valore)) {
                    return ResponseEntity.badRequest().body("Password non valida.");
                }
                //Creo un'istanza di BCryptPasswordEncoder per fare l'hashing della password
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(valore); // Calcola l'hash della password
            
                consulenti.getAccount().setPassword(hashedPassword);
                
                break;
            default:
                return ResponseEntity.badRequest().body("Campo non valido.");
        }


        consulentiRepository.save(consulenti);

        return ResponseEntity.ok("Campo modificato con successo!");
    }


    //Endpoint per ottenere tutte le città e i percorsi delle immagini
    @GetMapping("/cities")
    public List<City> getCities() {
        return cityRepository.findAll(); //Restituisce l'elenco delle città in formato JSON
    }
    
    @GetMapping("/cityComments/{cityName}/comments")
    public List<CityComments> getCommentsByCity(@PathVariable String cityName) {
        return cityCommentsRepository.findMainCommentsByCity(cityName);
    }

    @GetMapping("/cityComments/{cityName}/replies")
    public List<CityComments> getRepliesByCity(@PathVariable String cityName) {
        return cityCommentsRepository.findRepliesByParentCommentId(cityName);
    }

    
    
    
    @PostMapping("/cityComments/{cityName}/addComment")
    public ResponseEntity<String> postComment(
            @PathVariable String cityName,
            @RequestParam("commentText") String commentText,
            HttpServletRequest request) {

        //Recupero l'username dalla sessione
        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non sei autenticato.");
        }

      
        City city = cityRepository.findByCityName(cityName);
        if (city == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Città non trovata.");
        }

     
        CityComments cityComment = new CityComments();
        cityComment.setUsername(username);
        cityComment.setCity(city);
        cityComment.setCommentText(commentText);
        cityCommentsRepository.save(cityComment);

        return ResponseEntity.ok("Commento inserito con successo!");
    }
    
    
    
    @PostMapping("/cityComments/{cityName}/addReply/{parentCommentId}")
    public ResponseEntity<String> postReply(
            @PathVariable String cityName,
            @PathVariable Integer parentCommentId,
            @RequestParam("commentText") String commentText,
            HttpServletRequest request) {

       
        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non sei autenticato.");
        }

      
        City city = cityRepository.findByCityName(cityName);
        if (city == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Città non trovata.");
        }

        //Trovo il commento principale a cui rispondere
        CityComments parentComment = cityCommentsRepository.findById(parentCommentId).orElse(null);
        if (parentComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Commento principale non trovato.");
        }

        //Creo e salvo la risposta
        CityComments replyComment = new CityComments();
        replyComment.setUsername(username);
        replyComment.setCity(city);
        replyComment.setCommentText(commentText);
        replyComment.setParentComment(parentComment); //Imposto il commento principale come "genitore"
        cityCommentsRepository.save(replyComment);

        return ResponseEntity.ok("Risposta inserita con successo!");
    }
    


    @PutMapping("/cityComments/{commentId}/edit")
    public ResponseEntity<String> editComment(
            @PathVariable Integer commentId,
            @RequestBody Map<String, String> requestBody,
            HttpSession session) {

        String commentText = requestBody.get("commentText");

      
        if (commentText == null || commentText.isEmpty()) {
            return ResponseEntity.badRequest().body("Il testo del commento non può essere vuoto.");
        }

        CityComments comment = cityCommentsRepository.findById(commentId).orElse(null);
        
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Commento non trovato.");
        }

        String currentUsername = (String) session.getAttribute("username");

        if (!currentUsername.equals(comment.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non hai i permessi per modificare questo commento.");
        }

        comment.setCommentText(commentText);
        cityCommentsRepository.save(comment);
        return ResponseEntity.ok("Commento modificato con successo.");
    }

    
    
    
    
    @Configuration
    @EnableWebSocket
    public class WebSocketConfig implements WebSocketConfigurer {

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(new SignalingHandler(), "/signaling")
                    .setAllowedOrigins("*")
                    .addInterceptors(new CustomHandshakeInterceptor());
        }
    }
    
    
    public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        	
            String query = request.getURI().getQuery(); //Ottengo la query string dall'URI, ad esempio "?role=consulente"
            if (query != null) {   
             attributes.put("role", query.split("=")[1]); //divido la stringa in due parti separata dal simbolo "=" e prendo la seconda parte [1] ad esempio -> "role=consulente" -> prendo solo consulente
                        								 //In questo modo associo l'attributo "ruolo" ad ogni sessione per poi  usarlo in afterConnectionEstablished
            }

            return super.beforeHandshake(request, response, wsHandler, attributes);
        }
    }

    
    public class SignalingHandler extends TextWebSocketHandler {
        private final Set<WebSocketSession> consultants = new HashSet<>(); //Vogliamo un set perché non devono esserci duplicati
        private final Queue<WebSocketSession> waitingUsers = new LinkedList<>(); //Una sorta di coda, preleviamo il primo utente libero per assegnarlo ad un consulente
        private final Map<WebSocketSession, WebSocketSession> sessionPairs = new HashMap<>(); //HashMap chiave-valore

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            String role = (String) session.getAttributes().get("role");
            System.out.println("Connessione stabilita con ruolo: " + role);

            if ("consulente".equals(role)) {
                consultants.add(session); //Se il ruolo è consulente allora aggiungo la sessione al set
                System.out.println("Consulente aggiunto: " + session.getId());
                
                
                 //Controllo se ci sono utenti in attesa (se è entrato prima un utente e poi il consulente)
                if (!waitingUsers.isEmpty()) {
                    WebSocketSession userSession = waitingUsers.poll(); //Prelevo il primo utente in attesa
                    sessionPairs.put(userSession, session); //Associo l'utente al consulente
                    sessionPairs.put(session, userSession); //Poichè l'associazione è bidirezionale  faccio anche il viceversa

                   
                    userSession.sendMessage(new TextMessage("{\"type\":\"info\",\"message\":\"Connesso a un consulente.\"}"));
                    session.sendMessage(new TextMessage("{\"type\":\"info\",\"message\":\"Un utente si è connesso.\"}"));

                    System.out.println("Pairing utente " + userSession.getId() + " con consulente " + session.getId());
                }
                
            } else if ("utente".equals(role)) {
                System.out.println("Utente connesso: " + session.getId());
                WebSocketSession consultantSession = consultants.stream().findFirst().orElse(null); //Se un utente si è connesso cerco *il primo* consulente disponibile
                
                if (consultantSession != null) {
                    consultants.remove(consultantSession); //Rimuovo il consulente dal set peché è stato assegnato ad un utente
                    sessionPairs.put(session, consultantSession); //Aggiorno l'hashmap sessionPairs -> sessioneUtente(session) + sessioneConsulente(assignedConsultant)
                    sessionPairs.put(consultantSession, session); //Lo faccio due volte per avere un accoppiamento bidirezionale

                    session.sendMessage(new TextMessage("{\"type\":\"info\",\"message\":\"Connesso a un consulente.\"}"));
                    consultantSession.sendMessage(new TextMessage("{\"type\":\"info\",\"message\":\"Un utente si è connesso.\"}"));

                    System.out.println("Pairing utente " + session.getId() + " con consulente " + consultantSession.getId());
                } else {
                    waitingUsers.add(session); //Inserisco l'utente nella coda di attesa
                    session.sendMessage(new TextMessage("{\"type\":\"info\",\"message\":\"In attesa di un consulente.\"}"));
                    System.out.println("Nessun consulente disponibile. Utente in attesa.");
                }
            }
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            WebSocketSession pairedSession = sessionPairs.get(session); //Recupero il valore associato alla chiave, ad esempio ( key:userSession -> value:consultantSession)
            if (pairedSession != null && pairedSession.isOpen()) {
                System.out.println("Inoltro messaggio da " + session.getId() + " a " + pairedSession.getId());
                pairedSession.sendMessage(message); //Se la sessione era di un Utente -> Consulente allora invio il msg al Consulente e viceversa (flusso bidirezionale)
            }
        }

        

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            System.out.println("Connessione chiusa: " + session.getId());
            
            WebSocketSession pairedSession = sessionPairs.remove(session);//Quando una connessione viene chiusa, cerco la sessione accoppiata (pairedSession) e la rimuovo dalla hasmap
			                                                              //pairedSession avrà duqnue la sessione duale a quella chiusa
            if (pairedSession != null) {
                sessionPairs.remove(pairedSession);  //se la sessione chiusa aveva una sessione accoppiata allora la rimuovo dall'hasmap
                pairedSession.sendMessage(new TextMessage("{\"type\":\"info\",\"message\":\"La connessione è stata chiusa.\"}"));
                pairedSession.close(); //chiudo la sessione accoppiata 
            }

            consultants.remove(session);
            waitingUsers.remove(session);
        }
    }

    
    
    @GetMapping("/api/weather")
    public String getWeather(@RequestParam String city) {
        return weatherService.getWeather(city);
    }



}
