package qub;

public class JSONString implements JSONSegment
{
    private final String text;
    private final char quote;

    private JSONString(String text, char quote)
    {
        PreCondition.assertNotNull(text, "text");
        PreCondition.assertFalse(text.contains("\n"), "text.contains(\"\\n\")");

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
        PreCondition.assertNotNull(text, "text");

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
    public Result<Integer> toString(IndentedCharacterWriteStream stream, JSONFormat format)
    {
        PreCondition.assertNotNull(stream, "stream");
        PreCondition.assertNotNull(format, "format");

        return Result.create(() ->
        {
            int result = 0;

            result += stream.write(this.quote).await();

            final int textLength = this.text.length();
            int startIndex = 0;
            int quoteIndex = this.text.indexOf(this.quote);
            while (startIndex < textLength)
            {
                if (quoteIndex == -1)
                {
                    result += stream.write(this.text.substring(startIndex)).await();
                    startIndex = textLength;
                }
                else
                {
                    result += stream.write(this.text.substring(startIndex, quoteIndex)).await();
                    result += stream.write('\\').await();
                    result += stream.write(this.quote).await();

                    startIndex = quoteIndex + 1;
                    quoteIndex = this.text.indexOf(this.quote, startIndex);
                }
            }

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
