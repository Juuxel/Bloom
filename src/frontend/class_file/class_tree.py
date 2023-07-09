class ClassTreeNode:
    def __init__(self, name):
        self.name = name
        self.outer = None
        self.__inner = []

    def add_child(self, child):
        self.__inner.append(child)
        child.outer = self

    def create_node_group(self):
        group = []
        self.__add_to_node_group(group)
        return group

    def __add_to_node_group(self, group):
        if self not in group:
            group.append(self)
            for inner in self.__inner:
                inner.__add_to_node_group(group)


class ClassTree:
    def __init__(self):
        self.__nodes = {}

    def __get_node(self, name):
        if name in self.__nodes:
            node = self.__nodes[name]
        else:
            node = ClassTreeNode(name)
            self.__nodes[name] = node
        return node

    def read_class(self, class_file, path):
        constant_pool = class_file["constant_pool"]

        this_name = constant_pool[constant_pool[class_file["this_class"]]["name_index"]]["text"]
        self.__get_node(this_name).path = path

        for attribute in class_file["attributes"]:
            name = constant_pool[attribute["attribute_name_index"]]["text"]
            match name:
                case "InnerClasses":
                    for entry in attribute["classes"]:
                        inner_class_info_index = entry["inner_class_info_index"]
                        inner_class_info = constant_pool[inner_class_info_index]
                        inner_class_name = constant_pool[inner_class_info["name_index"]]["text"]
                        outer_class_info_index = entry["outer_class_info_index"]
                        if outer_class_info_index == 0:
                            continue
                        outer_class_info = constant_pool[outer_class_info_index]
                        outer_class_name = constant_pool[outer_class_info["name_index"]]["text"]
                        self.__get_node(outer_class_name).add_child(self.__get_node(inner_class_name))
                case "EnclosingMethod":
                    enclosing_class_info = constant_pool[attribute["class_index"]]
                    enclosing_class_name = constant_pool[enclosing_class_info["name_index"]]["text"]
                    self.__get_node(enclosing_class_name).add_child(self.__get_node(this_name))

    def get_class_groups(self):
        groups = []
        for node in self.__nodes.values():
            if node.outer is None:
                groups.append(node.create_node_group())
        return groups
