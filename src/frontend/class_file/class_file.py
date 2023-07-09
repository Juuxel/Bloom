from .attributes import augment_attributes, read_attribute_info
from .constant_pool import read_constant_pool
from .data_input import *


def read_class_file(byte_view, augment=True):
    magic = read_u4(byte_view)
    if magic != 0xCAFEBABE:
        raise ValueError(f"Not a class file: magic number was {magic}")
    minor_version = read_u2(byte_view)
    major_version = read_u2(byte_view)
    constant_pool_count = read_u2(byte_view)
    constant_pool = read_constant_pool(byte_view, constant_pool_count)
    access_flags = read_u2(byte_view)
    this_class = read_u2(byte_view)
    super_class = read_u2(byte_view)
    interfaces_count = read_u2(byte_view)
    interfaces = [read_u2(byte_view) for _ in range(0, interfaces_count)]
    fields_count = read_u2(byte_view)
    fields = [read_field_or_method_info(byte_view) for _ in range(0, fields_count)]
    methods_count = read_u2(byte_view)
    methods = [read_field_or_method_info(byte_view) for _ in range(0, methods_count)]
    attributes_count = read_u2(byte_view)
    attributes = [read_attribute_info(byte_view) for _ in range(0, attributes_count)]

    if augment:
        augment_attributes(constant_pool, attributes)
        for field in fields:
            augment_attributes(constant_pool, field["attributes"])
        for method in methods:
            augment_attributes(constant_pool, method["attributes"])

    return {
        "magic": magic,
        "minor_version": minor_version,
        "major_version": major_version,
        "constant_pool_count": constant_pool_count,
        "constant_pool": constant_pool,
        "access_flags": access_flags,
        "this_class": this_class,
        "super_class": super_class,
        "interfaces_count": interfaces_count,
        "interfaces": interfaces,
        "fields_count": fields_count,
        "fields": fields,
        "methods_count": methods_count,
        "methods": methods,
        "attributes_count": attributes_count,
        "attributes": attributes
    }


def read_field_or_method_info(byte_view):
    access_flags = read_u2(byte_view)
    name_index = read_u2(byte_view)
    descriptor_index = read_u2(byte_view)
    attributes_count = read_u2(byte_view)
    attributes = [read_attribute_info(byte_view) for _ in range(0, attributes_count)]
    return {
        "access_flags": access_flags,
        "name_index": name_index,
        "descriptor_index": descriptor_index,
        "attributes_count": attributes_count,
        "attributes": attributes
    }
