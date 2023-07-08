package juuxel.bloom.backend.decompilation;

import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public final class FilteredContextSource implements IContextSource {
    private final IContextSource parent;
    private final Set<String> classes;

    public FilteredContextSource(IContextSource parent, Set<String> classes) {
        this.parent = parent;
        this.classes = classes;
    }

    @Override
    public String getName() {
        return parent.getName() + " <- " + classes;
    }

    @Override
    public Entries getEntries() {
        var parentEntries = parent.getEntries();
        var classes = parentEntries.classes()
            .stream()
            .filter(entry -> this.classes.contains(entry.basePath()))
            .toList();
        return new Entries(classes, parentEntries.directories(), parentEntries.others(), parentEntries.childContexts());
    }

    @Override
    public InputStream getInputStream(String resource) throws IOException {
        if (resource.endsWith(".class")) {
            String className = removeClassSuffix(resource);
            if (!classes.contains(className)) {
                return null;
            }
        }

        return parent.getInputStream(resource);
    }

    private static String removeClassSuffix(String resource) {
        return resource.substring(0, resource.length() - ".class".length());
    }

    @Override
    public IOutputSink createOutputSink(IResultSaver saver) {
        return parent.createOutputSink(saver);
    }
}
