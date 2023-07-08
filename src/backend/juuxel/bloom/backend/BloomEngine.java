package juuxel.bloom.backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import juuxel.bloom.backend.codec.Result;
import juuxel.bloom.backend.decompilation.FilteredContextSource;
import juuxel.bloom.backend.decompilation.IpcResultSaver;
import juuxel.bloom.backend.response.Response;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IContextSource;

import java.io.File;
import java.util.Map;
import java.util.Set;

public final class BloomEngine {
    private final Gson gson;
    private Fernflower ff;
    private File inputFile;

    BloomEngine(Gson gson) {
        this.gson = gson;
    }

    public void initDecompiler(String inputPath) {
        ff = new Fernflower(new IpcResultSaver(this), Map.of(), new PrintStreamLogger(System.err));
        inputFile = new File(inputPath);
        ff.addLibrary(inputFile);
    }

    private static IContextSource createJarContextSource(File jar) {
        try {
            Class<?> c = Class.forName("org.jetbrains.java.decompiler.struct.JarContextSource");
            var ctor = c.getDeclaredConstructor(IBytecodeProvider.class, File.class);
            ctor.setAccessible(true);
            return (IContextSource) ctor.newInstance(null, jar);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void decompile(String className) {
        IContextSource base = createJarContextSource(inputFile);
        ff.addSource(new FilteredContextSource(base, Set.of(className)));

        try {
            System.err.println("Starting decomp");
            ff.decompileContext();
            System.err.println("Finished decomp");
        } finally {
            ff.clearContext();
        }
    }

    public void readClasspath(String input) {
        ff.addLibrary(new File(input));
    }

    public void sendResponse(Response r) {
        Result<JsonElement> json = Communicable.CODEC.encode(r);

        if (json instanceof Result.Ok<JsonElement> ok) {
            System.out.println(gson.toJson(ok.value()));
            System.out.flush();
        }
    }
}
