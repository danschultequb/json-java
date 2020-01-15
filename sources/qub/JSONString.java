package qub;

public class JSONString implements JSONSegment
{
    private final String text;
    private final char quote;

    private JSONString(String text, char quote)
    {
        PreCondition.assertNotNull(text, "text");

        this.text = text;
        this.quote = quote;
    }

    public static JSONString getFromQuoted(String quotedText)
    {
        PreCondition.assertNotNullAndNotEmpty(quotedText, "quotedText");
        PreCondition.assertTrue(Strings.isQuoted(quotedText), "Strings.isQuoted(quotedText)");

        final char quote = quotedText.charAt(0);
        final String text = Strings.unquote(quotedText);
        return JSONString.get(text, quote);
    }

    public static JSONString get(String text)
    {
        PreCondition.assertNotNull(text, "text");

        return JSONString.get(text, '\"');
    }

    public static JSONString get(String text, char quote)
    {
        PreCondition.assertNotNull(text, "unquotedText");

        return new JSONString(text, quote);
    }

    public String getValue()
    {
        return this.text;
    }

    public char getQuote()
    {
        return this.quote;
    }

    @Override
    public String toString()
    {
        return JSONSegment.toString(this);
    }

    @Override
    public Result<Integer> toString(IndentedCharacterWriteStream stream)
    {
        return Result.create(() ->
        {
            int result = 0;

            result += stream.write(this.quote).await();
            result += stream.write(this.text).await();
            result += stream.write(this.quote).await();

            return result;
        });
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONString && this.equals((JSONString)rhs);
    }

    public boolean equals(JSONString rhs)
    {
        return rhs != null &&
            this.text.equals(rhs.text) &&
            this.quote == rhs.quote;
    }
}
