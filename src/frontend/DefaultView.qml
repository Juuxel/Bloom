import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

Pane {
    ColumnLayout {
        Label {
            text: "You haven't opened a project yet."
        }

        Button {
            text: "Open Folder..."
            onClicked: folderDialog.open()
        }
    }
}
