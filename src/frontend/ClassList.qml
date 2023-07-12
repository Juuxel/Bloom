import QtQuick
import QtQuick.Controls

TreeView {
    id: root
    signal classSelected

    selectionModel: ItemSelectionModel {}
    delegate: TreeViewDelegate {
        onClicked: {
            // TODO: Reimplement this
            // const request = bridge.project.init_decompilation();
            // for (const index in classes) {
            //     request.add_class(classes[index]);
            // }
            // request.submit();
            // root.classSelected();
            // classView.className = classes[0];
        }
    }
}
