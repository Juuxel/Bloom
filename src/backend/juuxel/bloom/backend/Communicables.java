package juuxel.bloom.backend;

import juuxel.bloom.backend.codec.Codec;
import juuxel.bloom.backend.codec.Result;
import juuxel.bloom.backend.message.DecompileMessage;
import juuxel.bloom.backend.message.ExitMessage;
import juuxel.bloom.backend.message.InitDecompilerMessage;
import juuxel.bloom.backend.message.ReadClasspathMessage;
import juuxel.bloom.backend.response.ClassContentResponse;

import java.util.HashMap;
import java.util.Map;

public final class Communicables {
    private static final Map<String, Codec<? extends Communicable>> CODECS;

    static {
        Map<String, Codec<? extends Communicable>> codecs = new HashMap<>();
        codecs.put(ExitMessage.TYPE, ExitMessage.CODEC);
        codecs.put(InitDecompilerMessage.TYPE, InitDecompilerMessage.CODEC);
        codecs.put(DecompileMessage.TYPE, DecompileMessage.CODEC);
        codecs.put(ReadClasspathMessage.TYPE, ReadClasspathMessage.CODEC);
        codecs.put(ClassContentResponse.TYPE, ClassContentResponse.CODEC);
        CODECS = Map.copyOf(codecs);
    }

    public static Result<Codec<? extends Communicable>> getCodec(String type) {
        var codec = CODECS.get(type);
        return codec != null ? Result.ok(codec) : Result.err("Unknown message type: " + type);
    }
}
