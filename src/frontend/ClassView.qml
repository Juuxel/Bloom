import QtQuick 2.15
import QtQuick.Controls 2.15

Flickable {
    property string className
    onClassNameChanged: textDisplay.text = bridge.get_class_contents(className)

    TextArea.flickable: TextArea {
        id: textDisplay
        readOnly: true
    }

    // Set up scroll bars
    ScrollBar.horizontal: ScrollBar {}
    ScrollBar.vertical: ScrollBar {}
}
