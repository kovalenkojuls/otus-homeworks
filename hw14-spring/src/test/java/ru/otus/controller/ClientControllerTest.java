package ru.otus.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.crm.models.Address;
import ru.otus.crm.models.Client;
import ru.otus.crm.models.Phone;
import ru.otus.crm.services.ClientServiceDB;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientServiceDB clientServiceDB;

    @Test
    void clientsListViewTest() throws Exception {
        Client clientFirst = Client.builder()
                .id(1L)
                .name("Mark")
                .address(new Address("Arbat", 1L))
                .phones(Set.of(new Phone(1L, "88008008080", 1L)))
                .build();

        Client clientSecond = Client.builder()
                .id(2L)
                .name("Den")
                .address(new Address("Lenina", 2L))
                .phones(Set.of(new Phone(2L, "88008008080", 2L)))
                .build();
        
        List<Client> clients = List.of(clientFirst, clientSecond);

        when(clientServiceDB.findAll()).thenReturn(clients);
        mockMvc.perform(get("/client/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientsList"))
                .andExpect(model().attribute("clients", clients));

        verify(clientServiceDB, times(1)).findAll();
    }

    @Test
    void clientCreateViewTest() throws Exception {
        mockMvc.perform(get("/client/create"))
                .andExpect(status().isOk());
    }

    @Test
    void clientUpdateViewTest() throws Exception {
        Client client = Client.builder()
                .id(1L)
                .name("Mark")
                .address(new Address("Arbat", 1L))
                .phones(Set.of(new Phone(1L, "88008008080", 1L)))
                .build();

        when(clientServiceDB.getClient(1L)).thenReturn(java.util.Optional.of(client));

        mockMvc.perform(get("/client/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientCreateOrUpdate"))
                .andExpect(model().attribute("client", client));

        verify(clientServiceDB, times(1)).getClient(1L);
    }

    @Test
    void clientSaveTest() throws Exception {
        Client client = Client.builder()
                .name("Mark")
                .address(new Address("Arbat", null))
                .phones(Set.of(new Phone(null, "88008008080", null)))
                .build();

        mockMvc.perform(post("/client/save")
                        .param("name", "Mark")
                        .param("address", "Arbat")
                        .param("phones", "88008008080"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(clientServiceDB, times(1)).saveClient(client);
    }

    @Test
    void clientDeleteTest() throws Exception {
        mockMvc.perform(get("/client/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(clientServiceDB, times(1)).deleteClient(1L);
    }
}