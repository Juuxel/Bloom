from .byte_view import ByteView
from .data_input import *


def read_attribute_info(byte_view):
    attribute_name_index = read_u2(byte_view)
    attribute_length = read_u4(byte_view)
    info = byte_view.read(size=attribute_length)
    if len(info) < attribute_length:
        raise ValueError(f"Could not read attribute: wanted {attribute_length} bytes, found {len(info)}")
    return {
        "attribute_name_index": attribute_name_index,
        "attribute_length": attribute_length,
        "info": info
    }


def augment_attributes(constant_pool, attributes):
    for attribute in attributes:
        augment_attribute(constant_pool, attribute)


def augment_attribute(constant_pool, attribute):
    name = constant_pool[attribute["attribute_name_index"]]["text"]
    match name:
        case "InnerClasses":
            augment_inner_classes_attribute(constant_pool, attribute)
        case "EnclosingMethod":
            augment_enclosing_method_attribute(constant_pool, attribute)


def augment_inner_classes_attribute(constant_pool, attribute):
    if constant_pool[attribute["attribute_name_index"]]["text"] != "InnerClasses":
        raise ValueError(f"Attribute {attribute} is not InnerClasses")
    byte_view = ByteView(attribute["info"])
    number_of_classes = read_u2(byte_view)
    classes = []
    for _ in range(0, number_of_classes):
        inner_class_info_index = read_u2(byte_view)
        outer_class_info_index = read_u2(byte_view)
        inner_name_index = read_u2(byte_view)
        inner_class_access_flags = read_u2(byte_view)
        classes.append({
            "inner_class_info_index": inner_class_info_index,
            "outer_class_info_index": outer_class_info_index,
            "inner_name_index": inner_name_index,
            "inner_class_access_flags": inner_class_access_flags
        })
    attribute["number_of_classes"] = number_of_classes
    attribute["classes"] = classes


def augment_enclosing_method_attribute(constant_pool, attribute):
    if constant_pool[attribute["attribute_name_index"]]["text"] != "EnclosingMethod":
        raise ValueError(f"Attribute {attribute} is not EnclosingMethod")
    byte_view = ByteView(attribute["info"])
    attribute["class_index"] = read_u2(byte_view)
    attribute["method_index"] = read_u2(byte_view)
