package juuxel.bloom.backend.codec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public sealed interface Result<T> {
    <U> Result<U> map(Function<? super T, ? extends U> transform);

    <U> Result<U> flatMap(Function<? super T, ? extends Result<U>> transform);

    Result<T> or(Result<T> result);

    default T orElse(T fallback) {
        return fold(x -> x, err -> fallback);
    }

    default T orElseGet(Supplier<? extends T> fallback) {
        return fold(x -> x, err -> fallback.get());
    }

    <X extends Throwable> T orElseThrow(Function<? super String, ? extends X> throwable) throws X;

    <U> U fold(Function<? super T, ? extends U> ifOk, Function<? super String, ? extends U> ifErr);

    Optional<T> asOptional();

    Result<T> mapError(UnaryOperator<String> transform);

    default T get(ResultDsl dsl) throws ResultDsl.Bind {
        return dsl.bind(this);
    }

    static <T> Ok<T> ok(T value) {
        return new Ok<>(value);
    }

    static <T> Err<T> err(String message) {
        return new Err<>(message);
    }

    static <T> Result<List<T>> flip(Iterable<Result<T>> results) {
        List<T> ts = results instanceof Collection<?> c ? new ArrayList<>(c.size()) : new ArrayList<>();

        for (Result<T> result : results) {
            if (result instanceof Result.Ok<T> ok) {
                ts.add(ok.value());
            } else if (result instanceof Result.Err<T> err) {
                return err.cast();
            }
        }

        return Result.ok(ts);
    }

    record Ok<T>(T value) implements Result<T> {
        @Override
        public <U> Result<U> map(Function<? super T, ? extends U> transform) {
            return new Ok<>(transform.apply(value));
        }

        @Override
        public <U> Result<U> flatMap(Function<? super T, ? extends Result<U>> transform) {
            return transform.apply(value);
        }

        @Override
        public Result<T> or(Result<T> result) {
            return this;
        }

        @Override
        public <X extends Throwable> T orElseThrow(Function<? super String, ? extends X> throwable) throws X {
            return value;
        }

        @Override
        public <U> U fold(Function<? super T, ? extends U> ifOk, Function<? super String, ? extends U> ifErr) {
            return ifOk.apply(value);
        }

        @Override
        public Optional<T> asOptional() {
            return Optional.of(value);
        }

        @Override
        public Result<T> mapError(UnaryOperator<String> wrapper) {
            return this;
        }
    }

    record Err<T>(String message) implements Result<T> {
        @SuppressWarnings("unchecked")
        private <U> Result<U> cast() {
            return (Result<U>) this;
        }

        @Override
        public <U> Result<U> map(Function<? super T, ? extends U> transform) {
            return cast();
        }

        @Override
        public <U> Result<U> flatMap(Function<? super T, ? extends Result<U>> transform) {
            return cast();
        }

        @Override
        public Result<T> or(Result<T> result) {
            return result;
        }

        @Override
        public <X extends Throwable> T orElseThrow(Function<? super String, ? extends X> throwable) throws X {
            throw throwable.apply(message);
        }

        @Override
        public <U> U fold(Function<? super T, ? extends U> ifOk, Function<? super String, ? extends U> ifErr) {
            return ifErr.apply(message);
        }

        @Override
        public Optional<T> asOptional() {
            return Optional.empty();
        }

        @Override
        public Result<T> mapError(UnaryOperator<String> transform) {
            return new Err<>(transform.apply(message));
        }
    }
}
