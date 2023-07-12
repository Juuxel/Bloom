from enum import IntEnum, auto
import operator

from PySide6.QtCore import QAbstractItemModel, QByteArray, QModelIndex, QObject, Qt


class PackageTreeRole(IntEnum):
    Classes = Qt.ItemDataRole.UserRole + 1
    Type = auto()


class PackageTreeModel(QAbstractItemModel):
    def __init__(self, header, parent):
        super().__init__(parent)
        self.__root = TreeItem(columns=[QObject.tr(text, None) for text in header], group=[])

    def index(self, row, column, parent=QModelIndex()):
        if not self.hasIndex(row, column, parent):
            return QModelIndex()

        if not parent.isValid():
            parent_item = self.__root
        else:
            parent_item = parent.internalPointer()

        child_item = parent_item.child(row)
        if child_item is not None:
            return self.createIndex(row, column, child_item)
        return QModelIndex()

    def parent(self, index=QModelIndex()):
        if not index.isValid():
            return QModelIndex()

        child_item = index.internalPointer()
        parent_item = child_item.parent

        if parent_item == self.__root:
            return QModelIndex()
        return self.createIndex(parent_item.row(), 0, parent_item)

    def rowCount(self, parent=QModelIndex()):
        if parent.column() > 0:
            return 0
        if not parent.isValid():
            parent_item = self.__root
        else:
            parent_item = parent.internalPointer()
        return parent_item.child_count()

    def columnCount(self, parent=QModelIndex()):
        if parent.isValid():
            value = parent.internalPointer()
            if value is not None:
                return value.column_count()
        return self.__root.column_count()

    def data(self, index, role=Qt.ItemDataRole.DisplayRole.value):
        if not index.isValid():
            return None
        item = index.internalPointer()
        if role != Qt.ItemDataRole.DisplayRole.value:
            return item.data(role)
        return item.column_data(index.column())

    def root(self):
        return self.__root

    def headerData(self, section, orientation, role=Qt.ItemDataRole.DisplayRole):
        if orientation == Qt.Orientation.Horizontal and role == Qt.ItemDataRole.DisplayRole:
            return self.__root.column_data(section)
        return None

    def flags(self, index):
        if not index.isValid():
            return Qt.ItemFlag.NoItemFlags
        return super().flags(index)

    def roleNames(self):
        roles = dict(super().roleNames())
        for role in PackageTreeRole:
            roles[role.value] = QByteArray(role.name.lower().encode("UTF-8"))
        return roles


class TreeItem:
    def __init__(self, columns, group, parent=None):
        self.__columns = columns
        self.__group = group
        self.parent = parent
        self.__children = []

    def add_child(self, child):
        self.__children.append(child)

    def child_count(self):
        return len(self.__children)

    def child(self, row):
        if 0 <= row < len(self.__children):
            return self.__children[row]
        return None

    def row(self):
        if self.parent is not None:
            return self.parent.__children.index(self)
        return 0

    def column_count(self):
        return len(self.__columns)

    def column_data(self, index):
        if 0 <= index < len(self.__columns):
            return self.__columns[index]
        return None

    def data(self, role):
        match role:
            case PackageTreeRole.Classes:
                return [node.name for node in self.__group]
            case PackageTreeRole.Type:
                # TODO: Integrate the type kind thing
                return "class" if len(self.__group) > 0 else "package"


def split_off_package_name(class_name):
    try:
        # this is a lastIndexOf in python: https://stackoverflow.com/a/63834895
        last_slash = len(class_name) - 1 - operator.indexOf(reversed(class_name), "/")
        return class_name[:last_slash], class_name[last_slash + 1:]
    except ValueError:
        return None, class_name


def create_package_tree_model(class_tree, parent):
    model = PackageTreeModel(["Name"], parent)
    root = model.root()
    class_groups = class_tree.get_class_groups()
    package_items = {}
    for group in class_groups:
        group = list(filter(lambda node: hasattr(node, "path"), group))
        if len(group) == 0:
            continue
        head_class = group[0].name
        package_name, simple_name = split_off_package_name(head_class)
        if package_name is not None:
            if package_name not in package_items:
                parent = TreeItem([package_name], group=[], parent=root)
                root.add_child(parent)
                package_items[package_name] = parent
            else:
                parent = package_items[package_name]
        else:
            parent = root
        item = TreeItem([simple_name], group, parent)
        parent.add_child(item)
    return model
