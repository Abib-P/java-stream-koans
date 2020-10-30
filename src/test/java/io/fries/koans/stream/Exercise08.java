package io.fries.koans.stream;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;

class Exercise08 extends OnlineStore {

    /**
     * Create a set of item names that are in {@code Customer.wantsToBuy} but not on sale in any shop.
     */
    @Test
    void items_not_on_sale() {
        Stream<Customer> customerStream = mall.getCustomers().stream();
        Stream<Shop> shopStream = mall.getShops().stream();

        List<String> itemListOnSale = shopStream
                .flatMap(shop -> shop.getItems()
                .stream())
                .map(Item::getName)
                .collect(Collectors.toList());

        Set<String> itemSetNotOnSale = customerStream
                .flatMap(customer -> customer.getWantsToBuy().stream())
                .map(Item::getName)
                .filter(not(itemListOnSale::contains))
                .collect(Collectors.toSet());

        assertThat(itemSetNotOnSale).hasSize(3);
        assertThat(itemSetNotOnSale).containsExactlyInAnyOrder("bag", "pants", "coat");
    }

    /**
     * Create a customer's name list including who are having enough money to buy all items they want which is
     * on sale.
     * Items that are not on sale can be counted as 0 money cost.
     * If there is several same items with different prices, customer can choose the cheapest one.
     */
    @Test
    void having_enough_money() {
        Stream<Customer> customerStream = mall.getCustomers().stream();
        Stream<Shop> shopStream = mall.getShops().stream();

        List<Item> onSale = shopStream
                .flatMap(shop -> shop.getItems().stream())
                .distinct()
                .collect(Collectors.toList());

        Function<Item,Integer> bestItemPrice = item -> onSale.stream()
                .filter(onSaleItem -> onSaleItem.getName().equals(item.getName()))
                .min(Comparator.comparing(Item::getPrice))
                .map(Item::getPrice)
                .orElse(0);

        Predicate<Customer> havingEnoughMoney = customer -> customer
                .getWantsToBuy()
                .stream()
                .map(bestItemPrice)
                .reduce(Integer::sum)
                .map(price -> price <= customer.getBudget())
                .orElse(true);

        List<String> customerNameList = customerStream.filter(havingEnoughMoney).map(Customer::getName).collect(Collectors.toList());

        assertThat(customerNameList).hasSize(7);
        assertThat(customerNameList).containsExactly("Joe", "Patrick", "Chris", "Kathy", "Alice", "Andrew", "Amy");
    }
}
