package homework;

import java.util.Map;
import java.util.TreeMap;

public class CustomerService {
    private TreeMap<Customer, String> map = new TreeMap<>();

    public Map.Entry<Customer, String> getSmallest() {
        return getNewMapEntry(map.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        for (Map.Entry<Customer, String> entry: map.entrySet()) {
            if (entry.getKey().getScores() > customer.getScores()) {
                return getNewMapEntry(entry);
            }
        }
        return null;
    }

    public void add(Customer customer, String data) {
        map.put(customer, data);
    }

    private Map.Entry<Customer, String> getNewMapEntry(Map.Entry<Customer, String> entry) {
        Customer key = new Customer(
                entry.getKey().getId(),
                entry.getKey().getName(),
                entry.getKey().getScores());

        return Map.entry(key, entry.getValue());
    }
}