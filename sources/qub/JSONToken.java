package qub;

public class JSONToken extends JSONSegment
{
    private final String text;
    private final int startIndex;
    private final JSONTokenType type;

    public JSONToken(String text, int startIndex, JSONTokenType type)
    {
        this.text = text;
        this.startIndex = startIndex;
        this.type = type;
    }

    @Override
    public String toString()
    {
        return text;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONToken && equals((JSONToken)rhs);
    }

    public boolean equals(JSONToken rhs)
    {
        return rhs != null &&
            text.equals(rhs.text) &&
            startIndex == rhs.startIndex &&
            type == rhs.type;
    }

    @Override
    public int getStartIndex()
    {
        return startIndex;
    }

    @Override
    public int getLength()
    {
        return text == null ? 0 : text.length();
    }

    @Override
    public int getAfterEndIndex()
    {
        return getStartIndex() + getLength();
    }

    public JSONTokenType getType()
    {
        return type;
    }

    public static JSONToken leftCurlyBracket(int startIndex)
    {
        return new JSONToken("{", startIndex, JSONTokenType.LeftCurlyBracket);
    }

    public static JSONToken rightCurlyBracket(int startIndex)
    {
        return new JSONToken("}", startIndex, JSONTokenType.RightCurlyBracket);
    }

    public static JSONToken leftSquareBracket(int startIndex)
    {
        return new JSONToken("[", startIndex, JSONTokenType.LeftSquareBracket);
    }

    public static JSONToken rightSquareBracket(int startIndex)
    {
        return new JSONToken("]", startIndex, JSONTokenType.RightSquareBracket);
    }

    public static JSONToken colon(int startIndex)
    {
        return new JSONToken(":", startIndex, JSONTokenType.Colon);
    }

    public static JSONToken comma(int startIndex)
    {
        return new JSONToken(",", startIndex, JSONTokenType.Comma);
    }

    public static JSONToken nullToken(String text, int startIndex)
    {
        return new JSONToken(text, startIndex, JSONTokenType.Null);
    }

    public static JSONToken newLine(String text, int startIndex)
    {
        return new JSONToken(text, startIndex, JSONTokenType.NewLine);
    }

    public static JSONQuotedString quotedString(String text, int startIndex, boolean closed)
    {
        return new JSONQuotedString(text, startIndex, closed);
    }

    public static JSONToken number(String text)
    {
        return number(text, 0);
    }

    public static JSONToken number(String text, int startIndex)
    {
        return new JSONToken(text, startIndex, JSONTokenType.Number);
    }

    public static JSONToken booleanToken(String text, int startIndex)
    {
        return new JSONToken(text, startIndex, JSONTokenType.Boolean);
    }

    public static JSONToken booleanToken(String text)
    {
        return booleanToken(text, 0);
    }

    public static JSONToken whitespace(String text, int startIndex)
    {
        return new JSONToken(text, startIndex, JSONTokenType.Whitespace);
    }

    public static JSONToken lineComment(String text, int startIndex)
    {
        return new JSONToken(text, startIndex, JSONTokenType.LineComment);
    }

    public static JSONCloseableToken blockComment(String text, int startIndex, boolean closed)
    {
        return new JSONCloseableToken(text, startIndex, JSONTokenType.BlockComment, closed);
    }

    public static JSONToken unrecognized(String text, int startIndex)
    {
        return new JSONToken(text, startIndex, JSONTokenType.Unrecognized);
    }
}