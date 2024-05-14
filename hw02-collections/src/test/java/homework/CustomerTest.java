package homework;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerTest {
    @Test
    @DisplayName("Проверяем, что класс Customer не сломан")
    void setterCustomerTest() {
        String expectedName = "updatedName";
        String name = "nameVas";
        Customer customer = new Customer(1, name, 2);

        customer.setName(expectedName);
        assertThat(customer.getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Объект Customer как ключ в карте")
    void customerAsKeyTest() {
        final long customerId = 1L;
        Customer customer = new Customer(customerId, "Ivan", 233);
        Map<Customer, String> map = new HashMap<>();

        String expectedData = "data";
        map.put(customer, expectedData);

        long newScore = customer.getScores() + 10;
        String factData = map.get(new Customer(customerId, "IvanChangedName", newScore));
        assertThat(factData).isEqualTo(expectedData);


        long newScoreSecond = customer.getScores() + 20;
        customer.setScores(newScoreSecond);
        String factDataSecond = map.get(customer);
        assertThat(factDataSecond).isEqualTo(expectedData);
    }

    @Test
    @DisplayName("Сортировка по полю score, итерация по возрастанию")
    void scoreSortingTest() {
        Customer customer1 = new Customer(1, "Ivan", 233);
        Customer customer2 = new Customer(2, "Petr", 11);
        Customer customer3 = new Customer(3, "Pavel", 888);

        CustomerService customerService = new CustomerService();
        customerService.add(customer1, "Data1");
        customerService.add(customer2, "Data2");
        customerService.add(customer3, "Data3");

        Map.Entry<Customer, String> smallestScore = customerService.getSmallest();
        assertThat(smallestScore.getKey()).isEqualTo(customer2);

        Map.Entry<Customer, String> middleScore =
                customerService.getNext(new Customer(10, "Key", 20));
        assertThat(middleScore.getKey()).isEqualTo(customer1);
        middleScore.getKey().setScores(10000);
        middleScore.getKey().setName("Vasy");

        Map.Entry<Customer, String> biggestScore = customerService.getNext(customer1);
        assertThat(biggestScore.getKey()).isEqualTo(customer3);

        Map.Entry<Customer, String> notExists =
                customerService.getNext(new Customer(100, "Not exists", 20000));
        assertThat(notExists).isNull();
    }

    @Test
    @DisplayName("Модификация коллекции")
    void mutationTest() {
        Customer customer1 = new Customer(1, "Ivan", 233);
        Customer customer2 = new Customer(2, "Petr", 11);
        Customer customer3 = new Customer(3, "Pavel", 888);

        CustomerService customerService = new CustomerService();
        customerService.add(customer1, "Data1");
        customerService.add(
                new Customer(customer2.getId(), customer2.getName(), customer2.getScores()),
                "Data2"
        );
        customerService.add(customer3, "Data3");

        Map.Entry<Customer, String> smallestScore = customerService.getSmallest();
        smallestScore.getKey().setName("Vasyl");

        assertThat(customerService.getSmallest().getKey().getName()).isEqualTo(customer2.getName());
    }

    @Test
    @DisplayName("Возвращание в обратном порядке")
    void reverseOrderTest() {
        Customer customer1 = new Customer(1, "Ivan", 233);
        Customer customer2 = new Customer(2, "Petr", 11);
        Customer customer3 = new Customer(3, "Pavel", 888);

        CustomerReverseOrder customerReverseOrder = new CustomerReverseOrder();
        customerReverseOrder.add(customer1);
        customerReverseOrder.add(customer2);
        customerReverseOrder.add(customer3);

        Customer customerLast = customerReverseOrder.take();
        assertThat(customerLast).usingRecursiveComparison().isEqualTo(customer3);

        Customer customerMiddle = customerReverseOrder.take();
        assertThat(customerMiddle).usingRecursiveComparison().isEqualTo(customer2);

        Customer customerFirst = customerReverseOrder.take();
        assertThat(customerFirst).usingRecursiveComparison().isEqualTo(customer1);
    }
}