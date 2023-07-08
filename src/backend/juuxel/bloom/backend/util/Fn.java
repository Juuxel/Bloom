package juuxel.bloom.backend.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class Fn {
    public static <A, B, C> Function2C<A, B, C> curry(BiFunction<A, B, C> fn) {
        return a -> b -> fn.apply(a, b);
    }

    public static <A, B, C, D> Function3C<A, B, C, D> curry(Function3<A, B, C, D> fn) {
        return a -> b -> c -> fn.apply(a, b, c);
    }

    public static <A, B, C, D, E> Function4C<A, B, C, D, E> curry(Function4<A, B, C, D, E> fn) {
        return a -> b -> c -> d -> fn.apply(a, b, c, d);
    }

    public static <A, B, C> Function2C<B, A, C> reverse(BiFunction<A, B, C> fn) {
        return reverse(curry(fn));
    }

    public static <A, B, C, D> Function3C<C, B, A, D> reverse(Function3<A, B, C, D> fn) {
        return reverse(curry(fn));
    }

    public static <A, B, C, D, E> Function4C<D, C, B, A, E> reverse(Function4<A, B, C, D, E> fn) {
        return reverse(curry(fn));
    }

    public static <A, B, C> Function2C<B, A, C> reverse(Function2C<A, B, C> fn) {
        return b -> a -> fn.apply(a).apply(b);
    }

    public static <A, B, C, D> Function3C<C, B, A, D> reverse(Function3C<A, B, C, D> fn) {
        return c -> b -> a -> fn.apply(a).apply(b).apply(c);
    }

    public static <A, B, C, D, E> Function4C<D, C, B, A, E> reverse(Function4C<A, B, C, D, E> fn) {
        return d -> c -> b -> a -> fn.apply(a).apply(b).apply(c).apply(d);
    }

    @FunctionalInterface
    public interface Function3<A, B, C, D> {
        D apply(A a, B b, C c);
    }

    @FunctionalInterface
    public interface Function4<A, B, C, D, E> {
        E apply(A a, B b, C c, D d);
    }

    @FunctionalInterface
    public interface Function2C<A, B, C> extends Function<A, Function<B, C>> {
    }

    @FunctionalInterface
    public interface Function3C<A, B, C, D> extends Function<A, Function<B, Function<C, D>>> {
    }

    @FunctionalInterface
    public interface Function4C<A, B, C, D, E> extends Function<A, Function<B, Function<C, Function<D, E>>>> {
    }
}
