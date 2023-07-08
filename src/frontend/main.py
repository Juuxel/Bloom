import sys
from pathlib import Path

from PySide6.QtCore import QObject, Signal, Slot
from PySide6.QtGui import QGuiApplication
from PySide6.QtQml import QmlElement, QQmlApplicationEngine

import java

QML_IMPORT_NAME = "juuxel.bloom"
QML_IMPORT_MAJOR_VERSION = 1


@QmlElement
class Bridge(QObject):
    decompilationFinished = Signal(str, str, arguments=["className", "classContents"])

    def __init__(self, parent=None):
        super().__init__(parent)
        self.__class_contents = {}

    @Slot(str, result=str)
    def get_class_contents(self, class_name):
        contents = self.__class_contents[class_name]
        if contents is not None:
            return contents
        else:
            return ""

    @Slot(str)
    def open_jar(self, jar):
        backend_handle.send_message({"type": "init_decompiler", "input_path": jar})

    @Slot(str)
    def decompile_class(self, class_name):
        backend_handle.send_message({"type": "decompile", "class_name": class_name})

    @Slot()
    def poll_response(self):
        response = backend_handle.poll_response()

        # No response
        if response == {}:
            return

        print("Polled:", response)
        if response["type"] == "class_content":
            self.__class_contents[response["class_name"]] = response["content"]
            self.decompilationFinished.emit(response["class_name"], response["content"])


if __name__ == "__main__":
    app = QGuiApplication(sys.argv)
    engine = QQmlApplicationEngine()

    global backend_handle
    backend_handle = java.start_backend()

    qml_file = Path(__file__).parent / "view.qml"
    engine.load(qml_file)

    if not engine.rootObjects():
        sys.exit(-1)

    sys.exit(app.exec())
