package ru.otus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.crm.models.Address;
import ru.otus.crm.models.Client;
import ru.otus.crm.models.Phone;
import ru.otus.crm.services.ClientServiceDB;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ClientController {
    private final ClientServiceDB clientServiceDB;

    public ClientController(ClientServiceDB clientServiceDB) {
        this.clientServiceDB = clientServiceDB;
    }

    @GetMapping({"/", "/client/list"})
    public String clientsListView(Model model) {
        List<Client> clients = clientServiceDB.findAll();
        model.addAttribute("clients", clients);
        return "clientsList";
    }

    @GetMapping("/client/create")
    public String clientCreateView(Model model) {
        model.addAttribute("pageName", "Создание клиента:");
        model.addAttribute("buttonName", "Создать");
        model.addAttribute("client", new Client());
        return "clientCreateOrUpdate";
    }

    @GetMapping("/client/update/{id}")
    public String clientUpdateView(@PathVariable Long id, Model model) {
        model.addAttribute("pageName", "Обновление клиента:");
        model.addAttribute("buttonName", "Обновить");
        model.addAttribute("client", clientServiceDB.getClient(id).orElse(new Client()));
        return "clientCreateOrUpdate";
    }

    @PostMapping("/client/save")
    public RedirectView clientSave(@RequestParam(value = "id", required = false) Long id,
                                   @RequestParam("name") String name,
                                   @RequestParam("address") String address,
                                   @RequestParam("phones") String phones) {

        Client client = Client.builder()
                .id(id)
                .name(name)
                .address(new Address(address, null))
                .phones(Arrays.stream(phones.split(","))
                            .map(Phone::new)
                            .collect(Collectors.toSet()))
                .build();

        clientServiceDB.saveClient(client);
        return new RedirectView("/", true);
    }

    @GetMapping("/client/delete/{id}")
    public RedirectView clientDelete(@PathVariable("id") Long id) {
        clientServiceDB.deleteClient(id);
        return new RedirectView("/", true);
    }
}