package juuxel.bloom.backend;

import juuxel.bloom.backend.codec.Codec;

public interface Communicable {
    Codec<Communicable> CODEC = Codec.STRING.dispatch("type", Communicables::getCodec, Communicable::getType);

    String getType();
}
