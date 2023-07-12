from PySide6.QtGui import QFont, QSyntaxHighlighter, QTextCharFormat
from PySide6.QtGui import Qt
from pygments import lex
from pygments.lexers import JavaLexer
from pygments.token import Token

STRING_FORMAT = QTextCharFormat()
KEYWORD_FORMAT = QTextCharFormat()
NAME_FORMAT = QTextCharFormat()

STRING_FORMAT.setForeground(Qt.GlobalColor.darkGreen)
KEYWORD_FORMAT.setFontWeight(QFont.Weight.Bold)
NAME_FORMAT.setFontItalic(True)


class HighlighterImpl(QSyntaxHighlighter):
    def __init__(self, parent):
        super().__init__(parent)

    def highlightBlock(self, text):
        tokens = lex(text, JavaLexer())
        start = 0
        for token, value in tokens:
            text_format = get_token_format(token)
            if text_format is not None:
                self.setFormat(start, len(value), text_format)
            start += len(value)


def get_token_format(token):
    if token in Token.String:
        return STRING_FORMAT
    elif token in Token.Keyword:
        return KEYWORD_FORMAT
    elif token in Token.Name.Function or token in Token.Name.Class:
        return NAME_FORMAT
    else:
        return None
