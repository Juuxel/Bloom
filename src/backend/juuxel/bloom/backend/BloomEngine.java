package juuxel.bloom.backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import juuxel.bloom.backend.codec.Result;
import juuxel.bloom.backend.decompilation.FilteredContextSource;
import juuxel.bloom.backend.decompilation.IpcResultSaver;
import juuxel.bloom.backend.response.ClassContentResponse;
import juuxel.bloom.backend.response.Response;
import juuxel.bloom.backend.util.Pair;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.struct.DirectoryContextSource;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class BloomEngine {
    private final Gson gson;
    private Fernflower ff;
    private File inputFile;

    BloomEngine(Gson gson) {
        this.gson = gson;
    }

    public void initDecompiler(String inputPath) {
        inputFile = new File(inputPath);
    }

    private void setupDecompiler() {
        ff = new Fernflower(new IpcResultSaver(this), Map.of(), new PrintStreamLogger(System.err));
        ff.addLibrary(inputFile);
    }

    private static IContextSource createDirectoryContextSource(File jar) {
        return new DirectoryContextSource(null, jar);
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

    public void decompile(List<Pair<String, String>> classNamesAndPaths) {
        IContextSource base = inputFile.isDirectory() ? createDirectoryContextSource(inputFile) : createJarContextSource(inputFile);
        Set<String> relativePaths = classNamesAndPaths.stream()
            .map(Pair::second)
            .map(Path::of)
            .map(p -> p.isAbsolute() ? inputFile.toPath().relativize(p) : p)
            .map(Path::toString)
            .map(s -> s.replace(File.separator, "/"))
            .collect(Collectors.toSet());

        try {
            setupDecompiler();
            ff.addSource(new FilteredContextSource(base, relativePaths));
            ff.decompileContext();
        } catch (Exception e) {
            var sw = new StringWriter();
            var pw = new PrintWriter(sw);
            pw.println("// Bloom: Could not decompile " + classNamesAndPaths.get(0).first());
            e.printStackTrace(pw);
            sendResponse(new ClassContentResponse(classNamesAndPaths.get(0).first(), sw.toString()));
        } finally {
            if (ff != null) ff.clearContext();
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
