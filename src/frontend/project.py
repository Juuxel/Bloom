from abc import ABC, abstractmethod
from pathlib import Path
from PySide6.QtCore import QObject, QThread, Signal, Slot
from PySide6.QtQml import QmlElement

from class_file.byte_view import ByteView
from class_file.class_file import read_class_file
from class_file.class_tree import ClassTree


QML_IMPORT_NAME = "juuxel.bloom.project"
QML_IMPORT_MAJOR_VERSION = 1


@QmlElement
class Project(QObject):
    def __init__(self, proto, parent=None):
        super().__init__(parent)
        self.class_tree = proto.class_tree
        self.root = proto.root
        self.__class_groups = self.class_tree.get_class_groups()
        self.__class_names_to_paths = {}
        for group in self.__class_groups:
            for node in group:
                if hasattr(node, "path"):
                    self.__class_names_to_paths[node.name] = node.path

    @Slot(result=list)
    def get_class_groups(self):
        groups = self.__class_groups
        result = []
        for group in groups:
            group_data = []
            for node in group:
                if hasattr(node, "path"):  # don't consider external groups
                    group_data.append(node.name)
            if len(group_data) > 0:
                result.append(group_data)
        return result

    @Slot(str, result=str)
    def get_path_for_class(self, name):
        return self.__class_names_to_paths.get(name)


class ProtoProject:
    def __init__(self, root):
        self.class_tree = ClassTree()
        self.root = root


class ProjectRoot(ABC):
    @abstractmethod
    def get_entry(self, path):
        """Return a byte view of the entry at the path or None if not found."""
        pass


class SingleClassRoot(ProjectRoot):
    def __init__(self, class_path, content):
        self.__class_path = class_path
        self.__content = content

    def get_entry(self, path):
        if path == self.__class_path:
            return ByteView(self.__content)
        return None


class DirectoryRoot(ProjectRoot):
    def __init__(self, directory_path):
        self.__directory_path = directory_path

    def get_entry(self, path):
        child = self.__directory_path.joinpath(path)
        if child.exists():
            with child.open(mode="rb") as file:
                return ByteView(file.read())
        return None


class ZipClassRoot(ProjectRoot):
    pass


class ReaderThread(QThread):
    projectReady = Signal(ProtoProject)

    def __init__(self, parent, path, is_dir):
        super().__init__(parent)
        self.__path = path
        self.__is_dir = is_dir

    def run(self):
        if self.__is_dir:
            project = read_project_dir(self.__path)
        else:
            project = read_project_file(self.__path)
        self.projectReady.emit(project)


def read_project(bridge, path, is_dir):
    thread = ReaderThread(bridge, path, is_dir)
    thread.projectReady.connect(bridge.emit_project_scan_finished)
    thread.start()


def read_project_file(path):
    path = Path(path)
    if path.name.lower().endswith(".class"):
        with path.open(mode="rb") as file:
            content = file.read()
            project = ProtoProject(SingleClassRoot(path, content))
            class_file = read_class_file(ByteView(content))
            project.class_tree.read_class(class_file, path)
    else:
        raise ValueError(f"Unknown project type: {path}")
    return project


def read_project_dir(path):
    path = Path(path)
    project = ProtoProject(DirectoryRoot(path))
    for child in _walk(path):
        if child.name.lower().endswith(".class"):
            with child.open(mode="rb") as file:
                byte_view = ByteView(file.read())
                class_file = read_class_file(byte_view)
                project.class_tree.read_class(class_file, child)
    return project


def _walk(directory):
    # see https://stackoverflow.com/a/64915960
    for child in directory.iterdir():
        if child.is_dir():
            yield from _walk(child)
        else:
            yield child
