package juuxel.bloom.backend.message;

import juuxel.bloom.backend.BloomEngine;
import juuxel.bloom.backend.codec.Codec;
import juuxel.bloom.backend.codec.RecordCodecs;

public record InitDecompilerMessage(String inputPath) implements Message {
    public static final String TYPE = "init_decompiler";
    public static final Codec<InitDecompilerMessage> CODEC = RecordCodecs.<InitDecompilerMessage>builder()
        .field("input_path", Codec.STRING, InitDecompilerMessage::inputPath)
        .build(InitDecompilerMessage::new);

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void run(BloomEngine engine) {
        engine.initDecompiler(inputPath);
    }
}
