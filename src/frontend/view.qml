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
        onDecompilationFinished: (className, content) => classView.refresh()
        onProjectScanFinished: project => {
            for (const group of project.get_class_groups()) {
                classList.model.append({ classes: group });
            }

            stack.currentIndex = 1;
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

    StackLayout {
        id: stack
        width: parent.width
        height: parent.height

        DefaultView {
            Layout.fillWidth: true
            Layout.fillHeight: true
        }

        RowLayout {
            Layout.fillWidth: true
            Layout.fillHeight: true

            // Sidebar
            ClassList {
                id: classList
                width: 250
                Layout.fillHeight: true
                onClassSelected: projectStack.currentIndex = classView.StackLayout.index
            }

            // Centre
            StackLayout {
                Layout.fillWidth: true
                Layout.fillHeight: true
                id: projectStack

                Label {
                    text: "open something lol"
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
}
