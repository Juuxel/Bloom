import QtQuick 2.15
import QtQuick.Controls 2.15

ListView {
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
        }
    }

    ScrollBar.horizontal: ScrollBar {}
    ScrollBar.vertical: ScrollBar {}
}
