package juuxel.bloom.backend.decompilation;

import juuxel.bloom.backend.BloomEngine;
import juuxel.bloom.backend.response.ClassContentResponse;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.util.jar.Manifest;

public final class IpcResultSaver implements IResultSaver {
    private final BloomEngine engine;

    public IpcResultSaver(BloomEngine engine) {
        this.engine = engine;
    }

    @Override
    public void saveFolder(String path) {
    }

    @Override
    public void copyFile(String source, String path, String entryName) {
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        engine.sendResponse(new ClassContentResponse(qualifiedName, content));
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {
    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {
    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
        engine.sendResponse(new ClassContentResponse(qualifiedName, content));
    }

    @Override
    public void closeArchive(String path, String archiveName) {
    }
}
