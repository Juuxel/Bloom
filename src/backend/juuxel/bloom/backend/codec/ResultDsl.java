package juuxel.bloom.backend.codec;

public final class ResultDsl {
    private static final ResultDsl INSTANCE = new ResultDsl();

    private ResultDsl() {
    }

    public <T> T bind(Result<T> result) throws Bind {
        return result.orElseThrow(Bind::new);
    }

    public static <T> Result<T> execute(Resultable<T> block) {
        try {
            return Result.ok(block.execute(INSTANCE));
        } catch (Bind bind) {
            return Result.err(bind.getMessage());
        }
    }

    public interface Resultable<T> {
        T execute(ResultDsl c) throws Bind;
    }

    static final class Bind extends Throwable {
        private Bind(String message) {
            super(message);
        }
    }
}
