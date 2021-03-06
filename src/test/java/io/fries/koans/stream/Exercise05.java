package io.fries.koans.stream;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Exercise05 extends OnlineStore {

    /**
     * Create a list of customer names by using {@link Stream#collect} and {@link Collectors#toList}.
     */
    @Test
    void name_list() {
        List<Customer> customerList = mall.getCustomers();

        List<String> nameList = customerList.stream().map(Customer::getName).collect(Collectors.toList());

        assertThat(nameList).containsExactly(
                "Joe", "Steven", "Patrick", "Diana", "Chris", "Kathy", "Alice", "Andrew", "Martin", "Amy"
        );
    }

    /**
     * Create a set of customer age by using {@link Stream#collect} and {@link Collectors#toSet}.
     */
    @Test
    void age_set() {
        List<Customer> customerList = mall.getCustomers();

        Set<Integer> ageSet = customerList.stream().map(Customer::getAge).collect(Collectors.toSet());

        assertThat(ageSet).hasSize(9);
        assertThat(ageSet).containsExactlyInAnyOrder(21, 22, 26, 27, 28, 32, 35, 36, 38);
    }

    /**
     * Create a csv string of customer names in brackets "[]" by using {@link Collectors#joining}.
     */
    @Test
    void name_in_csv() {
        List<Customer> customerList = mall.getCustomers();

        String string = customerList.stream().map(Customer::getName).collect(Collectors.joining(",","[","]"));

        assertThat(string).isEqualTo("[Joe,Steven,Patrick,Diana,Chris,Kathy,Alice,Andrew,Martin,Amy]");
    }

    /**
     * Get the oldest customer by using {@link Collectors#maxBy}.
     * Don't use any intermediate operations.
     */
    @Test
    void oldest_customer() {
        List<Customer> customerList = mall.getCustomers();

        Optional<Customer> oldestCustomer = customerList.stream().collect(Collectors.maxBy(Comparator.comparing(Customer::getAge)));

        assertThat(oldestCustomer.get()).isEqualTo(customerList.get(3));
    }

    /**
     * Create a map of age as key and number of customers as value using {@link Collectors#groupingBy}
     * and {@link Collectors#counting}.
     */
    @Test
    void age_distribution() {
        List<Customer> customerList = mall.getCustomers();

        Map<Integer, Long> ageDistribution = customerList.stream().collect(Collectors.groupingBy(Customer::getAge,Collectors.counting()));

        assertThat(ageDistribution).hasSize(9);
        ageDistribution.forEach((age, numberOfCustomers) -> {
            if (age.equals(22)) {
                assertThat(numberOfCustomers).isEqualTo(2L);
            } else {
                assertThat(numberOfCustomers).isEqualTo(1L);
            }
        });
    }
}
