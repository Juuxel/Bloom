class ByteView:
    def __init__(self, b):
        self.__bytes = b
        self.__index = 0

    def read(self, size=1):
        result = self.__bytes[self.__index:self.__index + size]
        self.__index += size
        return result
