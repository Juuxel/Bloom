package juuxel.bloom.backend.message;

import juuxel.bloom.backend.BloomEngine;
import juuxel.bloom.backend.codec.Codec;
import juuxel.bloom.backend.codec.RecordCodecs;

import java.util.Set;

public record DecompileMessage(Set<String> classPaths) implements Message {
    public static final String TYPE = "decompile";
    public static final Codec<DecompileMessage> CODEC = RecordCodecs.<DecompileMessage>builder()
        .field("class_paths", Codec.STRING.set(), DecompileMessage::classPaths)
        .build(DecompileMessage::new);

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void run(BloomEngine engine) {
        engine.decompile(classPaths);
    }
}
