package ru.otus.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CreateClientServlet extends HttpServlet {
    private static final String CLIENT_CREATE_PAGE_TEMPLATE = "create-client.html";

    private final DBServiceClient dbServiceClient;
    private final TemplateProcessor templateProcessor;

    public CreateClientServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(CLIENT_CREATE_PAGE_TEMPLATE, null));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String phones = request.getParameter("phones");

        List<Phone> phoneList = Arrays.stream(phones.split(","))
                .map(Phone::new)
                .toList();

        Client client = new Client(name);
        client.setAddress(new Address(address));
        client.setPhones(phoneList);
        dbServiceClient.saveClient(client);

        response.sendRedirect("/client");
    }
}
