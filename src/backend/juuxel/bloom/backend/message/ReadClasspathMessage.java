package juuxel.bloom.backend.message;

import juuxel.bloom.backend.BloomEngine;
import juuxel.bloom.backend.codec.Codec;
import juuxel.bloom.backend.codec.RecordCodecs;

public record ReadClasspathMessage(String input) implements Message {
    public static final String TYPE = "read_classpath";
    public static final Codec<ReadClasspathMessage> CODEC = RecordCodecs.<ReadClasspathMessage>builder()
        .field("input", Codec.STRING, ReadClasspathMessage::input)
        .build(ReadClasspathMessage::new);

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void run(BloomEngine engine) {
        engine.readClasspath(input);
    }
}
