package juuxel.bloom.backend.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.function.Function;

public interface Codec<T> {
    Codec<String> STRING = new Codec<>() {
        @Override
        public Result<String> decode(JsonElement json) {
            return asJsonPrimitive(json).flatMap(Codec::asString);
        }

        @Override
        public Result<JsonElement> encode(String value) {
            return Result.ok(new JsonPrimitive(value));
        }
    };

    Result<T> decode(JsonElement json);
    Result<JsonElement> encode(T value);

    static <T> Codec<T> unit(T value) {
        return new Codec<>() {
            @Override
            public Result<T> decode(JsonElement json) {
                return Result.ok(value);
            }

            @Override
            public Result<JsonElement> encode(T value) {
                return Result.ok(JsonNull.INSTANCE);
            }
        };
    }

    default <U> Codec<U> xmap(Function<T, U> tu, Function<U, T> ut) {
        return new Codec<>() {
            @Override
            public Result<U> decode(JsonElement json) {
                return Codec.this.decode(json).map(tu);
            }

            @Override
            public Result<JsonElement> encode(U value) {
                return Codec.this.encode(ut.apply(value));
            }
        };
    }

    default <U> Codec<U> dispatch(String typeKey, Function<T, Result<? extends Codec<? extends U>>> codecGetter, Function<U, T> typeGetter) {
        return new Codec<>() {
            @Override
            public Result<U> decode(JsonElement json) {
                return ResultDsl.execute(c -> {
                    JsonObject obj = asJsonObject(json).get(c);
                    T type = get(obj, typeKey)
                        .flatMap(Codec.this::decode)
                        .get(c);
                    return codecGetter.apply(type).get(c)
                        .decode(obj).get(c);
                });
            }

            @SuppressWarnings("unchecked")
            @Override
            public Result<JsonElement> encode(U value) {
                return ResultDsl.execute(c -> {
                    T type = typeGetter.apply(value);
                    Codec<U> codec = (Codec<U>) codecGetter.apply(type).get(c);
                    JsonObject json = codec.encode(value)
                        .flatMap(Codec::asJsonObject)
                        .get(c);
                    json.add(typeKey, Codec.this.encode(type).get(c));
                    return json;
                });
            }
        };
    }

    static Result<JsonObject> asJsonObject(JsonElement json) {
        if (json.isJsonObject()) {
            return Result.ok(json.getAsJsonObject());
        } else {
            return Result.err("Not a JSON object: " + json);
        }
    }

    static Result<JsonPrimitive> asJsonPrimitive(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return Result.ok(json.getAsJsonPrimitive());
        } else {
            return Result.err("Not a JSON primitive: " + json);
        }
    }

    static Result<String> asString(JsonPrimitive json) {
        if (json.isString()) {
            return Result.ok(json.getAsString());
        } else {
            return Result.err("Not a JSON string: " + json);
        }
    }

    static Result<JsonElement> get(JsonObject json, String key) {
        if (json.has(key)) {
            return Result.ok(json.get(key));
        } else {
            return Result.err("Missing required key: " + key);
        }
    }
}
