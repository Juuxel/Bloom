package juuxel.bloom.backend.message;

import juuxel.bloom.backend.BloomEngine;
import juuxel.bloom.backend.codec.Codec;
import juuxel.bloom.backend.codec.RecordCodecs;

public record DecompileMessage(String className) implements Message {
    public static final String TYPE = "decompile";
    public static final Codec<DecompileMessage> CODEC = RecordCodecs.<DecompileMessage>builder()
        .field("class_name", Codec.STRING, DecompileMessage::className)
        .build(DecompileMessage::new);

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void run(BloomEngine engine) {
        engine.decompile(className);
    }
}
