package juuxel.bloom.backend.message;

import juuxel.bloom.backend.BloomEngine;
import juuxel.bloom.backend.codec.Codec;

public final class ExitMessage implements Message {
    public static final String TYPE = "exit";
    public static final Codec<ExitMessage> CODEC = Codec.unit(new ExitMessage());

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void run(BloomEngine engine) {
    }

    @Override
    public boolean shouldExit() {
        return true;
    }
}
