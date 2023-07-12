import QtQuick 2.15
import QtQuick.Controls 2.15

ListView {
    id: root
    signal classSelected

    model: ListModel {
        // Empty by default
    }

    delegate: Button {
        text: classes[0]
        onClicked: {
            const request = bridge.project.init_decompilation();
            for (const index in classes) {
                request.add_class(classes[index]);
            }
            request.submit();
            root.classSelected();
            classView.className = classes[0];
        }
    }

    ScrollBar.horizontal: ScrollBar {}
    ScrollBar.vertical: ScrollBar {}
}
