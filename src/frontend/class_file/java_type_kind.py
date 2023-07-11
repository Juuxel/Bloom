from enum import Enum


class JavaTypeKind(Enum):
    CLASS = "class"
    ENUM = "enum"
    RECORD = "record"
    INTERFACE = "interface"
    ANNOTATION = "annotation"


def get_type_kind(class_file):
    is_interface = (class_file["access_flags"] & 0x0200) != 0
    if is_interface:
        interfaces = class_file["interfaces"]
        for interface_index in interfaces:
            interface_info = class_file["constant_pool"][interface_index]
            name_index = interface_info["name_index"]
            interface_name = class_file["constant_pool"][name_index]["text"]
            if interface_name == "java/lang/annotation/Annotation":
                return JavaTypeKind.ANNOTATION
        return JavaTypeKind.INTERFACE
    else:
        match class_file["super_class"]:
            case "java/lang/Enum":
                return JavaTypeKind.ENUM
            case "java/lang/Record":
                return JavaTypeKind.RECORD
            case _:
                return JavaTypeKind.CLASS
