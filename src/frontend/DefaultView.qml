import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 2.15

Pane {
    id: outer

    ColumnLayout {
        Label {
            text: "You haven't opened a project yet."
            anchors.centerIn: outer
        }

        Button {
            text: "Open Folder..."
            onClicked: folderDialog.open()
        }
    }
}
