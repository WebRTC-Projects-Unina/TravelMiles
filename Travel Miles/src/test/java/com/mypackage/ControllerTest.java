package com.mypackage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.Arrays;
import com.mypackage.controller.Controller;
import com.mypackage.entity.Account;
import com.mypackage.entity.City;
import com.mypackage.entity.Consulenti;
import com.mypackage.entity.Utenti;
import com.mypackage.repository.AccountRepository;
import com.mypackage.repository.CityRepository;
import com.mypackage.repository.ConsulentiRepository;
import com.mypackage.repository.UtentiRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtentiRepository utentiRepository;

    @MockBean
    private AccountRepository accountRepository;
    
    @MockBean
    private CityRepository cityRepository;
    
    @MockBean
    private ConsulentiRepository consulentiRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void testRegisterUser_Success() throws Exception {
        when(utentiRepository.existsByUsername("user123")).thenReturn(false);
        when(utentiRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(accountRepository.existsByUsername("user123")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        mockMvc.perform(post("/register")
                .param("username", "user123")
                .param("nome", "Mario")
                .param("cognome", "Rossi")
                .param("nickname", "mrossi")
                .param("email", "mario@example.com")
                .param("password", "PasswordLunga1234!"))
                .andExpect(status().isFound())
                .andExpect(content().string("Utente registrato con successo!"));
    }



	@Test
	void testLoginUser_Success() throws Exception {

		Utenti mockUser = new Utenti("user123", "Mario", "Rossi", "mrossi", "mario@example.com");
	    Account mockAccount = new Account("user123", "hashedPassword");
	
	    when(utentiRepository.findByEmail("mario@example.com")).thenReturn(mockUser);
	    when(accountRepository.findByUsername("user123")).thenReturn(mockAccount);
	    when(passwordEncoder.matches("PasswordLunga1234!", "hashedPassword")).thenReturn(true);
	
	    mockMvc.perform(post("/login")
	            .param("email", "mario@example.com")
	            .param("password", "PasswordLunga1234!"))
	            .andExpect(status().isFound());
	}
	
	@Test
	void testLoginUser_Failure_PasswordMismatch() throws Exception {
	    Utenti mockUser = new Utenti("user123", "Mario", "Rossi", "mrossi", "mario@example.com");
	    Account mockAccount = new Account("user123", "hashedPassword");
	
	    when(utentiRepository.findByEmail("mario@example.com")).thenReturn(mockUser);
	    when(accountRepository.findByUsername("user123")).thenReturn(mockAccount);
	    when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);
	
	    mockMvc.perform(post("/login")
	            .param("email", "mario@example.com")
	            .param("password", "wrongPassword"))
	            .andExpect(status().isFound())
	            .andExpect(content().string("Password errata."));
	}
	
	@Test
	void testGetCities() throws Exception {

		City city1 = new City("Parigi", "/images/parigi.jpg");
	    City city2 = new City("Tokyo", "/images/tokyo.jpg");

	    when(cityRepository.findAll()).thenReturn(Arrays.asList(city1, city2));

	    mockMvc.perform(get("/cities"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$[0].cityName").value("Parigi"))
	            .andExpect(jsonPath("$[0].imagePath").value("/images/parigi.jpg"))
	            .andExpect(jsonPath("$[1].cityName").value("Tokyo"))
	            .andExpect(jsonPath("$[1].imagePath").value("/images/tokyo.jpg"));
	}

	
	@Test
	void testModificaCampo_Success() throws Exception {
	    Utenti mockUser = new Utenti("user123", "Mario", "Rossi", "mrossi", "user@example.com");
	
	    when(utentiRepository.findByUsername("user123")).thenReturn(mockUser);
	
	    mockMvc.perform(post("/modifica")
	            .param("campo", "nome")
	            .param("valore", "Luigi")
	            .param("username", "user123"))
	            .andExpect(status().isOk())
	            .andExpect(content().string("Campo modificato con successo!"));
	
	    assertEquals("Luigi", mockUser.getNome());
	}
	
	@Test
	void testIsValidPassword() throws Exception {
	    Method isValidPassword = Controller.class.getDeclaredMethod("isValidPassword", String.class);
	    isValidPassword.setAccessible(true);
	
	    assertTrue((Boolean) isValidPassword.invoke(new Controller(), "PasswordLunghissima1234!"));
	    assertFalse((Boolean) isValidPassword.invoke(new Controller(), "weakpassword"));
	}
	
	
	@Test
	void testRegisterConsultant_Success() throws Exception {
	    when(consulentiRepository.existsByUsername("consultant123")).thenReturn(false);
	    when(consulentiRepository.existsByEmail("consultant@example.com")).thenReturn(false);
	    when(accountRepository.existsByUsername("consultant123")).thenReturn(false);
	    when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
	
	    mockMvc.perform(post("/registerConsultant")
	            .param("username", "consultant123")
	            .param("nome", "Luigi")
	            .param("cognome", "Bianchi")
	            .param("email", "consultant@example.com")
	            .param("password", "PasswordLunga1234!"))
	            .andExpect(status().isFound())
	            .andExpect(content().string("Registrazione avvenuta con successo!"));
	}
	

	
	@Test
	void testLoginConsultant_Success() throws Exception {
	    Consulenti mockConsultant = new Consulenti("consultant123", "Luigi", "Bianchi", "consultant@example.com");
	    Account mockAccount = new Account("consultant123", "hashedPassword");
	
	    when(consulentiRepository.findByEmail("consultant@example.com")).thenReturn(mockConsultant);
	    when(accountRepository.findByUsername("consultant123")).thenReturn(mockAccount);
	    when(passwordEncoder.matches("PasswordLunga1234!", "hashedPassword")).thenReturn(true);
	
	    mockMvc.perform(post("/loginConsultant")
	            .param("email", "consultant@example.com")
	            .param("password", "PasswordLunga1234!"))
	            .andExpect(status().isFound());
	}
	
	@Test
	void testLoginConsultant_Failure_PasswordMismatch() throws Exception {
	    Consulenti mockConsultant = new Consulenti("consultant123", "Luigi", "Bianchi", "consultant@example.com");
	    Account mockAccount = new Account("consultant123", "hashedPassword");
	
	    when(consulentiRepository.findByEmail("consultant@example.com")).thenReturn(mockConsultant);
	    when(accountRepository.findByUsername("consultant123")).thenReturn(mockAccount);
	    when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);
	
	    mockMvc.perform(post("/loginConsultant")
	            .param("email", "consultant@example.com")
	            .param("password", "wrongPassword"))
	            .andExpect(status().isFound())
	            .andExpect(content().string("Password errata."));
	}
	
	@Test
	void testGetUserDetailsFromSession() throws Exception {
	    MockHttpSession session = new MockHttpSession();
	    session.setAttribute("nome", "Mario");
	    session.setAttribute("cognome", "Rossi");
	    session.setAttribute("nickname", "mrossi");
	    session.setAttribute("email", "mario@example.com");
	    session.setAttribute("username", "user123");
	
	    mockMvc.perform(get("/get-user-details").session(session))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.nome").value("Mario"))
	            .andExpect(jsonPath("$.cognome").value("Rossi"))
	            .andExpect(jsonPath("$.nickname").value("mrossi"))
	            .andExpect(jsonPath("$.email").value("mario@example.com"))
	            .andExpect(jsonPath("$.username").value("user123"));
	}
	
	@Test
	void testModificaCampoConsulenti_Success() throws Exception {
	    Consulenti mockConsultant = new Consulenti("consultant123", "Luigi", "Bianchi", "consultant@example.com");
	
	    when(consulentiRepository.findByUsername("consultant123")).thenReturn(mockConsultant);
	
	    mockMvc.perform(post("/modifica-consulenti")
	            .param("campo", "email")
	            .param("valore", "newemail@example.com")
	            .param("username", "consultant123"))
	            .andExpect(status().isOk())
	            .andExpect(content().string("Campo modificato con successo!"));
	
	    assertEquals("newemail@example.com", mockConsultant.getEmail());
	}
	
	@Test
	void testModificaCampoConsulenti_Failure_InvalidField() throws Exception {
	    mockMvc.perform(post("/modifica-consulenti")
	            .param("campo", "invalidField")
	            .param("valore", "testValue")
	            .param("username", "consultant123"))
	            .andExpect(status().isBadRequest())
	            .andExpect(content().string("Utente non trovato."));
	}
	
	@Test
	void testPostComment_Success() throws Exception {
	    City mockCity = new City("Barcellona", "/images/barcellona.jpg");
	
	    when(cityRepository.findByCityName("Rome")).thenReturn(mockCity);
	
	    MockHttpSession session = new MockHttpSession();
	    session.setAttribute("username", "user123");
	
	    mockMvc.perform(post("/cityComments/Rome/addComment")
	            .session(session)
	            .param("commentText", "Bellissima città!"))
	            .andExpect(status().isOk())
	            .andExpect(content().string("Commento inserito con successo!"));
	}
	
	@Test
	void testPostComment_Failure_CityNotFound() throws Exception {
	    when(cityRepository.findByCityName("UnknownCity")).thenReturn(null);
	
	    MockHttpSession session = new MockHttpSession();
	    session.setAttribute("username", "user123");
	
	    mockMvc.perform(post("/cityComments/UnknownCity/addComment")
	            .session(session)
	            .param("commentText", "Commento di test"))
	            .andExpect(status().isNotFound())
	            .andExpect(content().string("Città non trovata."));
	}


}