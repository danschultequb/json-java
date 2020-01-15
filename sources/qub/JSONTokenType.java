package qub;

/**
 * The type of a JSONToken.
 */
public enum JSONTokenType
{
    LeftCurlyBracket,
    RightCurlyBracket,
    LeftSquareBracket,
    RightSquareBracket,
    Colon,
    Comma,
    Null,
    NewLine,
    QuotedString,
    Boolean,
    Number,
    Whitespace,
    LineComment,
    BlockComment
}
