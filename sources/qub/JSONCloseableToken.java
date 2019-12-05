package qub;

public class JSONCloseableToken extends JSONToken
{
    private final boolean closed;

    public JSONCloseableToken(String text, int startIndex, JSONTokenType type, boolean closed)
    {
        super(text, startIndex, type);

        this.closed = closed;
    }

    public boolean isClosed()
    {
        return closed;
    }
}