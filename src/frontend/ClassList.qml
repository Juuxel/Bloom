import QtQuick 2.15
import QtQuick.Controls 2.15

ListView {
    model: ListModel {
        // Empty by default
    }

    delegate: Button {
        text: classes[0]
    }

    ScrollBar.horizontal: ScrollBar {}
    ScrollBar.vertical: ScrollBar {}
}
