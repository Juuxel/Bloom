import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

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
