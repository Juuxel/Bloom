package juuxel.bloom.backend.message;

import juuxel.bloom.backend.BloomEngine;
import juuxel.bloom.backend.Communicable;

public interface Message extends Communicable {
    void run(BloomEngine engine);

    default boolean shouldExit() {
        return false;
    }
}
