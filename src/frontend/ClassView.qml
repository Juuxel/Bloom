import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 2.15

StackLayout {
    property string className
    onClassNameChanged: refresh()
    signal refresh
    onRefresh: {
        if (bridge.project.has_source(className)) {
            textDisplay.text = bridge.project.get_source(className)
            currentIndex = 1
        } else {
            currentIndex = 0
        }
    }

    ColumnLayout {
        Label {
            text: "Decompiling class..."
        }
        BusyIndicator {
        }
    }

    Flickable {
        TextArea.flickable: TextArea {
            id: textDisplay
            readOnly: true
        }

        // Set up scroll bars
        ScrollBar.horizontal: ScrollBar {}
        ScrollBar.vertical: ScrollBar {}
    }
}
