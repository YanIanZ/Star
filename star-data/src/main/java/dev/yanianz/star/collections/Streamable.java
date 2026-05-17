package dev.yanianz.star.collections;

import java.util.stream.Stream;

@FunctionalInterface
public interface Streamable<T> {

    Stream<T> stream();

}
