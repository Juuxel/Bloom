import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

Pane {
    ColumnLayout {
        Label {
            text: qsTr("You haven't opened a project yet.")
        }

        Button {
            text: qsTr("Open Folder...")
            onClicked: folderDialog.open()
        }

        Button {
            text: qsTr("Open File...")
            onClicked: fileDialog.open()
        }
    }
}
