from enum import IntEnum

from mutf8 import decode_modified_utf8

from .data_input import *


class ConstantPool:
    def __init__(self, entries):
        self.__entries = entries

    def __len__(self):
        return len(self.__entries)

    def __getitem__(self, key):
        # constant pool keys go from 1 to its length
        return self.__entries[key - 1]

    def __repr__(self):
        return repr(self.__dict__)


class ConstantTag(IntEnum):
    CONSTANT_Utf8 = 1
    CONSTANT_Integer = 3
    CONSTANT_Float = 4
    CONSTANT_Long = 5
    CONSTANT_Double = 6
    CONSTANT_Class = 7
    CONSTANT_String = 8
    CONSTANT_Fieldref = 9
    CONSTANT_Methodref = 10
    CONSTANT_InterfaceMethodref = 11
    CONSTANT_NameAndType = 12
    CONSTANT_MethodHandle = 15
    CONSTANT_MethodType = 16
    CONSTANT_Dynamic = 17
    CONSTANT_InvokeDynamic = 18
    CONSTANT_Module = 19
    CONSTANT_Package = 20


def read_constant_pool(byte_view, constant_pool_count):
    entries = []
    for i in range(0, constant_pool_count - 1):
        entries.append(read_constant_pool_entry(byte_view))
    return ConstantPool(entries)


def read_constant_pool_entry(byte_view):
    tag = read_u1(byte_view)
    match tag:
        case ConstantTag.CONSTANT_Utf8:
            return read_constant_utf8_info(byte_view)
        case ConstantTag.CONSTANT_Integer:
            return read_constant_integer_info(byte_view)
        case ConstantTag.CONSTANT_Float:
            return read_constant_float_info(byte_view)
        case ConstantTag.CONSTANT_Long:
            return read_constant_long_info(byte_view)
        case ConstantTag.CONSTANT_Double:
            return read_constant_double_info(byte_view)
        case ConstantTag.CONSTANT_Class:
            return read_constant_class_info(byte_view)
        case ConstantTag.CONSTANT_String:
            return read_constant_string_info(byte_view)
        case ConstantTag.CONSTANT_Fieldref | ConstantTag.CONSTANT_Methodref | ConstantTag.CONSTANT_InterfaceMethodref:
            return read_constant_ref_info(byte_view, tag)
        case ConstantTag.CONSTANT_NameAndType:
            return read_name_and_type_info(byte_view)
        case ConstantTag.CONSTANT_MethodHandle:
            return read_method_handle_info(byte_view)
        case ConstantTag.CONSTANT_MethodType:
            return read_method_type_info(byte_view)
        case ConstantTag.CONSTANT_Dynamic | ConstantTag.CONSTANT_InvokeDynamic:
            return read_dynamic_info(byte_view, tag)
        case ConstantTag.CONSTANT_Module:
            return read_module_info(byte_view)
        case ConstantTag.CONSTANT_Package:
            return read_package_info(byte_view)


def read_constant_utf8_info(byte_view):
    length = read_u2(byte_view)
    b = byte_view.read(size=length)
    if len(b) < length:
        raise ValueError(f"Malformed MUTF-8 string: not enough bytes for length {length}")
    text = decode_modified_utf8(b)
    return {"tag": ConstantTag.CONSTANT_Utf8, "length": length, "bytes": b, "text": text}


def read_constant_integer_info(byte_view):
    b = read_u4(byte_view)
    return {"tag": ConstantTag.CONSTANT_Integer, "bytes": b}


def read_constant_float_info(byte_view):
    b = read_u4(byte_view)
    return {"tag": ConstantTag.CONSTANT_Float, "bytes": b}


def read_constant_long_info(byte_view):
    high_bytes = read_u4(byte_view)
    low_bytes = read_u4(byte_view)
    return {"tag": ConstantTag.CONSTANT_Long, "high_bytes": high_bytes, "low_bytes": low_bytes}


def read_constant_double_info(byte_view):
    high_bytes = read_u4(byte_view)
    low_bytes = read_u4(byte_view)
    return {"tag": ConstantTag.CONSTANT_Double, "high_bytes": high_bytes, "low_bytes": low_bytes}


def read_constant_class_info(byte_view):
    name_index = read_u2(byte_view)
    return {"tag": ConstantTag.CONSTANT_Class, "name_index": name_index}


def read_constant_string_info(byte_view):
    string_index = read_u2(byte_view)
    return {"tag": ConstantTag.CONSTANT_String, "string_index": string_index}


def read_constant_ref_info(byte_view, tag):
    class_index = read_u2(byte_view)
    name_and_type_index = read_u2(byte_view)
    return {"tag": tag, "class_index": class_index, "name_and_type_index": name_and_type_index}


def read_name_and_type_info(byte_view):
    name_index = read_u2(byte_view)
    descriptor_index = read_u2(byte_view)
    return {"tag": ConstantTag.CONSTANT_NameAndType, "name_index": name_index, "descriptor_index": descriptor_index}


def read_method_handle_info(byte_view):
    reference_kind = read_u1(byte_view)
    reference_index = read_u2(byte_view)
    return {
        "tag": ConstantTag.CONSTANT_MethodHandle,
        "reference_kind": reference_kind,
        "reference_index": reference_index
    }


def read_method_type_info(byte_view):
    descriptor_index = read_u2(byte_view)
    return {"tag": ConstantTag.CONSTANT_MethodType, "descriptor_index": descriptor_index}


def read_dynamic_info(byte_view, tag):
    bootstrap_method_attr_index = read_u2(byte_view)
    name_and_type_index = read_u2(byte_view)
    return {
        "tag": tag,
        "bootstrap_method_attr_index": bootstrap_method_attr_index,
        "name_and_type_index": name_and_type_index
    }


def read_module_info(byte_view):
    name_index = read_u2(byte_view)
    return {"tag": ConstantTag.CONSTANT_Module, "name_index": name_index}


def read_package_info(byte_view):
    name_index = read_u2(byte_view)
    return {"tag": ConstantTag.CONSTANT_Package, "name_index": name_index}
