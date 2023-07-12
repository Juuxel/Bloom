import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 2.15

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
