package juuxel.bloom.backend.message;

import juuxel.bloom.backend.BloomEngine;
import juuxel.bloom.backend.codec.Codec;
import juuxel.bloom.backend.codec.RecordCodecs;
import juuxel.bloom.backend.util.Pair;

import java.util.List;

public record DecompileMessage(List<Pair<String, String>> classNamesAndPaths) implements Message {
    public static final String TYPE = "decompile";
    public static final Codec<DecompileMessage> CODEC = RecordCodecs.<DecompileMessage>builder()
        .field("class_names_and_paths", Pair.codec(Codec.STRING).list(), DecompileMessage::classNamesAndPaths)
        .build(DecompileMessage::new);

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void run(BloomEngine engine) {
        engine.decompile(classNamesAndPaths);
    }
}
