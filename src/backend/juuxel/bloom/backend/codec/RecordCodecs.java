package juuxel.bloom.backend.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

public final class RecordCodecs {
    public static <R> RecordCodecBuilder<R, R> builder() {
        return new RecordCodecBuilder<>(null);
    }

    public static final class RecordCodecBuilder<F, R> {
        private final Field<R, ?> tail;

        private RecordCodecBuilder(Field<R, ?> tail) {
            this.tail = tail;
        }

        public <A> RecordCodecBuilder<Function<A, F>, R> field(String name, Codec<A> codec, Function<R, A> getter) {
            return new RecordCodecBuilder<>(new Field<>(name, codec, getter, tail));
        }

        public Codec<R> build(F factory) {
            return new Codec<>() {
                @SuppressWarnings({"unchecked", "rawtypes"})
                @Override
                public Result<R> decode(JsonElement json) {
                    return ResultDsl.execute(c -> {
                        var obj = Codec.asJsonObject(json).get(c);
                        Object f = factory;

                        for (Field<R, ?> field : iterate(tail)) {
                            var fieldJson = Codec.get(obj, field.name).get(c);
                            var fieldValue = field.codec.decode(fieldJson)
                                .mapError(m -> "Could not decode field " + field.name + ": " + m)
                                .get(c);
                            f = ((Function) f).apply(fieldValue);
                        }

                        return (R) f;
                    });
                }

                @Override
                public Result<JsonElement> encode(R value) {
                    return ResultDsl.execute(c -> {
                        JsonObject json = new JsonObject();

                        for (Field<R, ?> field : iterate(tail)) {
                            JsonElement fieldJson = field.encode(value)
                                .mapError(m -> "Could not encode field " + field.name + ": " + m)
                                .get(c);
                            json.add(field.name, fieldJson);
                        }

                        return json;
                    });
                }
            };
        }
    }

    private static <R> Iterable<Field<R, ?>> iterate(Field<R, ?> field) {
        return field != null ? field : Set.of();
    }

    private record Field<R, A>(
        String name,
        Codec<A> codec,
        Function<R, A> getter,
        Field<R, ?> next
    ) implements Iterable<Field<R, ?>> {
        Result<JsonElement> encode(R record) {
            return codec.encode(getter.apply(record));
        }

        @Override
        public Iterator<Field<R, ?>> iterator() {
            return new Iterator<>() {
                private Field<R, ?> field = Field.this;

                @Override
                public boolean hasNext() {
                    return field != null;
                }

                @Override
                public Field<R, ?> next() {
                    var result = field;
                    field = field.next;
                    return result;
                }
            };
        }
    }
}
