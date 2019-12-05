package qub;

public class JSONQuotedString extends JSONCloseableToken
{
    public JSONQuotedString(String text, int startIndex, boolean closed)
    {
        super(text, startIndex, JSONTokenType.QuotedString, closed);
    }

    public String toUnquotedString()
    {
        final String text = toString();
        final int textLength = text.length();
        return text.substring(1, textLength - (isClosed() ? 1 : 0));
    }
}