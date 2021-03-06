package io.fries.koans.stream;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static org.assertj.core.api.Assertions.assertThat;

class Exercise09 extends OnlineStore {

    /**
     * Implement a {@link Collector} which can create a String with comma separated names shown in the assertion.
     * The collector will be used by serial stream.
     */
    @Test
    void simplest_string_join() {
        List<Customer> customerList = mall.getCustomers();

        Supplier<StringBuilder> supplier = StringBuilder::new;
        BiConsumer<StringBuilder, String> accumulator = (result,newName) -> result.append( result.length() > 0 ? ","+newName :newName );
        BinaryOperator<StringBuilder> combiner = StringBuilder::append;
        Function<StringBuilder, String> finisher = StringBuilder::toString;

        Collector<String, ?, String> toCsv = new SimpleCollector<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                Collections.emptySet()
        );
        String nameAsCsv = customerList.stream().map(Customer::getName).collect(toCsv);
        assertThat(nameAsCsv).isEqualTo("Joe,Steven,Patrick,Diana,Chris,Kathy,Alice,Andrew,Martin,Amy");
    }

    <T> Collector<T, ?, List<T>> toList() {
        Supplier<List<T>> supplier = ArrayList::new;
        BiConsumer<List<T>, T> accumulator = List::add;
        BinaryOperator<List<T>> combiner = (first, second) -> Stream.concat(first.stream(),second.stream()).collect(Collectors.toList());
        Function<List<T>, List<T>> finisher = UnaryOperator.identity();

        return new SimpleCollector<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                Collections.emptySet()
        );
    }

    @Test
    void simplest_To_List_join() {
        List<Customer> customerList = mall.getCustomers();

        List<String> nameAsCsv = customerList.stream().map(Customer::getName).collect(toList());
        assertThat(nameAsCsv).containsExactly("Joe","Steven","Patrick","Diana","Chris","Kathy","Alice","Andrew","Martin","Amy");
    }

    /**
     * Implement a {@link Collector} which can create a {@link Map} with keys as item and values as {@link Set}
     * of customers who are wanting to buy that item.
     * The collector will be used by parallel stream.
     */
    @Test
    void map_keyed_by_items() {
        List<Customer> customerList = mall.getCustomers();

        Supplier<Object> supplier = null;
        BiConsumer<Object, Customer> accumulator = null;
        BinaryOperator<Object> combiner = null;
        Function<Object, Map<String, Set<String>>> finisher = null;

        Collector<Customer, ?, Map<String, Set<String>>> toItemAsKey = new SimpleCollector<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                EnumSet.of(CONCURRENT, IDENTITY_FINISH)
        );
        Map<String, Set<String>> itemMap = customerList.stream().parallel().collect(toItemAsKey);
        assertThat(itemMap.get("plane")).containsExactlyInAnyOrder("Chris");
        assertThat(itemMap.get("onion")).containsExactlyInAnyOrder("Patrick", "Amy");
        assertThat(itemMap.get("ice cream")).containsExactlyInAnyOrder("Patrick", "Steven");
        assertThat(itemMap.get("earphone")).containsExactlyInAnyOrder("Steven");
        assertThat(itemMap.get("plate")).containsExactlyInAnyOrder("Joe", "Martin");
        assertThat(itemMap.get("fork")).containsExactlyInAnyOrder("Joe", "Martin");
        assertThat(itemMap.get("cable")).containsExactlyInAnyOrder("Diana", "Steven");
        assertThat(itemMap.get("desk")).containsExactlyInAnyOrder("Alice");
    }

    /**
     * Create a {@link String} of "n"th bit ON.
     * For example:
     * "1" will be "1"
     * "3" will be "001"
     * "5" will be "00001"
     * "1,3,5" will be "10101"
     * "1-3" will be "111"
     * "7,1-3,5" will be "1110101"
     */
    @Test
    void bit_list_to_bit_string() {
        String bitList = "22-24,9,42-44,11,4,46,14-17,5,2,38-40,33,50,48";

        Collector<String, ?, String> toBitString = null;

        String bitString = Arrays.stream(bitList.split(",")).collect(toBitString);
        assertThat(bitString).isEqualTo("01011000101001111000011100000000100001110111010101");
    }
}
