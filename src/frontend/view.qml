import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 2.15

import juuxel.bloom 1.0

ApplicationWindow {
    title: "Bloom"
    width: 640
    height: 480
    visible: true

    Bridge {
        id: bridge
        onDecompilationFinished: (className, content) => {
            classView.className = className;
            stack.currentIndex = classView.StackLayout.index;
        }
    }

    Timer {
        interval: 100
        running: true
        repeat: true
        onTriggered: bridge.poll_response()
    }

    StackLayout {
        width: parent.width
        height: parent.height
        id: stack

        DefaultView {
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
