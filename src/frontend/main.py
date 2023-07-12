import sys
from pathlib import Path

from PySide6.QtCore import QObject, Property, Signal, Slot, QUrl
from PySide6.QtGui import QGuiApplication
from PySide6.QtQml import QmlElement, QQmlApplicationEngine
from PySide6.QtQuickControls2 import QQuickStyle

import java
from project import Project, read_project

QML_IMPORT_NAME = "juuxel.bloom"
QML_IMPORT_MAJOR_VERSION = 1


@QmlElement
class Bridge(QObject):
    decompilationFinished = Signal(str, str, arguments=["className", "classContents"])
    projectScanFinished = Signal(Project, arguments=["project"])

    def __init__(self, parent=None):
        super().__init__(parent)
        self.__project = None

    @Slot(str)
    def open_jar(self, jar):
        jar = to_path(jar)
        if jar is None:
            return
        backend_handle.send_message({"type": "init_decompiler", "input_path": jar})
        read_project(self, jar, is_dir=False)

    @Slot(str)
    def open_dir(self, directory):
        directory = to_path(directory)
        if directory is None:
            return
        backend_handle.send_message({"type": "init_decompiler", "input_path": directory})
        read_project(self, directory, is_dir=True)

    @Slot(list)
    def decompile_classes(self, class_names_and_paths):
        backend_handle.send_message({
            "type": "decompile",
            "class_names_and_paths": class_names_and_paths
        })

    @Slot()
    def poll_response(self):
        response = backend_handle.poll_response()

        # No response
        if response == {}:
            return

        if response["type"] == "class_content":
            self.__project.set_source(response["class_name"], response["content"])

    @Property(Project)
    def project(self):
        return self.__project

    @project.setter
    def project(self, project):
        self.__project = project

    @Slot(Project)
    def emit_project_scan_finished(self, proto):
        self.__project = Project(proto, self)
        self.projectScanFinished.emit(self.__project)


def to_path(url):
    if isinstance(url, str):
        url = QUrl(url)
    if url.isLocalFile():
        return url.toLocalFile()
    return None


if __name__ == "__main__":
    app = QGuiApplication(sys.argv)
    engine = QQmlApplicationEngine()

    # The default style is a bit broken, let's use the UWP-emulating style
    if sys.platform.startswith("win32"):
        QQuickStyle.setStyle("Universal")

    global backend_handle
    backend_handle = java.start_backend()

    qml_file = Path(__file__).parent / "view.qml"
    engine.load(qml_file)

    if not engine.rootObjects():
        sys.exit(-1)

    sys.exit(app.exec())
