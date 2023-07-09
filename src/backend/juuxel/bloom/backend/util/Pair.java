package juuxel.bloom.backend.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import juuxel.bloom.backend.codec.Codec;
import juuxel.bloom.backend.codec.Result;

public record Pair<A, B>(A first, B second) {
    public static <A> Codec<Pair<A, A>> codec(Codec<A> elementCodec) {
        return codec(elementCodec, elementCodec);
    }

    public static <A, B> Codec<Pair<A, B>> codec(Codec<A> codecA, Codec<B> codecB) {
        return new Codec<>() {
            @Override
            public Result<Pair<A, B>> decode(JsonElement json) {
                return Codec.asJsonArray(json).flatMap(array -> {
                    if (array.size() != 2) {
                        return Result.err("Pairs must have exactly two elements, found " + array.size());
                    }

                    return codecA.decode(array.get(0))
                        .flatMap(first -> codecB.decode(array.get(1))
                            .map(second -> new Pair<>(first, second)));
                });
            }

            @Override
            public Result<JsonElement> encode(Pair<A, B> value) {
                return codecA.encode(value.first)
                    .flatMap(encodedA -> codecB.encode(value.second)
                        .map(encodedB -> {
                            JsonArray array = new JsonArray(2);
                            array.add(encodedA);
                            array.add(encodedB);
                            return array;
                        }));
            }
        };
    }
}
