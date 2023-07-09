import struct


def read_u1(byte_view) -> int:
    read = byte_view.read(size=1)
    if len(read) < 1:
        raise ValueError("u1 not available")
    return int.from_bytes(read, byteorder="big")


def read_u2(byte_view) -> int:
    read = byte_view.read(size=2)
    if len(read) < 2:
        raise ValueError("u2 not available")
    return int.from_bytes(read, byteorder="big")


def read_u4(byte_view) -> int:
    read = byte_view.read(size=4)
    if len(read) < 4:
        raise ValueError("u4 not available")
    return int.from_bytes(read, byteorder="big")


def read_u8(byte_view) -> int:
    read = byte_view.read(size=8)
    if len(read) < 8:
        raise ValueError("u8 not available")
    return int.from_bytes(read, byteorder="big")


def read_f32(byte_view) -> float:
    read = byte_view.read(size=2)
    if len(read) < 2:
        raise ValueError("f32 not available")
    return struct.unpack(">f", read)[0]


def read_f64(byte_view) -> float:
    read = byte_view.read(size=4)
    if len(read) < 4:
        raise ValueError("f64 not available")
    return struct.unpack(">d", read)[0]
