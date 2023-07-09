import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Dialogs
import QtQuick.Layouts 2.15

import juuxel.bloom 1.0

ApplicationWindow {
    title: "Bloom"
    width: 800
    height: 600
    visible: true

    Bridge {
        id: bridge
        onDecompilationFinished: (className, content) => {
            classView.className = className;
            stack.currentIndex = classView.StackLayout.index;
        }
        onProjectScanFinished: project => {
            for (const group of project.get_class_groups()) {
                classList.model.append({ classes: group });
            }
        }
    }

    Timer {
        interval: 100
        running: true
        repeat: true
        onTriggered: bridge.poll_response()
    }

    FolderDialog {
        id: folderDialog
        onAccepted: bridge.open_dir(selectedFolder)
    }

    RowLayout {
        width: parent.width
        height: parent.height

        // Sidebar
        ClassList {
            id: classList
            width: 250
            Layout.fillHeight: true
        }

        // Centre
        StackLayout {
            Layout.fillWidth: true
            Layout.fillHeight: true
            id: stack

            DefaultView {
                Layout.fillWidth: true
                Layout.fillHeight: true
            }

            ClassView {
                id: classView
                Layout.fillWidth: true
                Layout.fillHeight: true
            }
        }
    }
}
