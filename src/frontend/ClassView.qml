import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 2.15

StackLayout {
    property string className
    onClassNameChanged: {
        textDisplay.text = bridge.get_class_contents(className)
        currentIndex = 1
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
