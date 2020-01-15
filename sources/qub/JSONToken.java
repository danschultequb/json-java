package qub;

/**
 * A token within JSON content.
 */
public class JSONToken
{
    private final String text;
    private final JSONTokenType type;

    /**
     * Create a new JSONToken object with the provided text and type.
     * @param text The type that the JSONToken was parsed from.
     * @param type The type of the JSONToken.
     */
    public JSONToken(String text, JSONTokenType type)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");
        PreCondition.assertNotNull(type, "type");

        this.text = text;
        this.type = type;
    }

    /**
     * Get the text of this JSONToken.
     * @return The text of this JSONToken.
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * Get the type of this JSONToken.
     * @return The type of this JSONToken.
     */
    public JSONTokenType getType()
    {
        return this.type;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONToken && this.equals((JSONToken)rhs);
    }

    /**
     * Get whether or not this JSONToken is equal to the provided JSONToken.
     * @param rhs The JSONToken to compare this JSONToken against.
     * @return Whether or not this JSONToken is equal to the provided JSONToken.
     */
    public boolean equals(JSONToken rhs)
    {
        return rhs != null &&
            this.text.equals(rhs.text) &&
            this.type == rhs.type;
    }

    @Override
    public String toString()
    {
        return this.text;
    }

    /**
     * A left curly bracket ('{') JSONToken.
     */
    public static final JSONToken leftCurlyBracket = new JSONToken("{", JSONTokenType.LeftCurlyBracket);
    /**
     * A right curly bracket ('}') JSONToken.
     */
    public static final JSONToken rightCurlyBracket = new JSONToken("}", JSONTokenType.RightCurlyBracket);
    /**
     * A left square bracket ('[') JSONToken.
     */
    public static final JSONToken leftSquareBracket = new JSONToken("[", JSONTokenType.LeftSquareBracket);
    /**
     * A right square bracket (']') JSONToken.
     */
    public static final JSONToken rightSquareBracket = new JSONToken("]", JSONTokenType.RightSquareBracket);
    /**
     * A colon (':') JSONToken.
     */
    public static final JSONToken colon = new JSONToken(":", JSONTokenType.Colon);
    /**
     * A comma (',') JSONToken.
     */
    public static final JSONToken comma = new JSONToken(",", JSONTokenType.Comma);
    /**
     * A null ("null") JSONToken.
     */
    public static final JSONToken nullToken = new JSONToken("null", JSONTokenType.Null);
    /**
     * A newline ('\n') JSONToken.
     */
    public static final JSONToken newLine = new JSONToken("\n", JSONTokenType.NewLine);
    /**
     * A carriage-return newline ("\r\n") JSONToken.
     */
    public static final JSONToken carriageReturnNewLine = new JSONToken("\r\n", JSONTokenType.NewLine);
    /**
     * A carriage-return ("\r") JSONToken.
     */
    public static final JSONToken carriageReturn = new JSONToken("\r", JSONTokenType.NewLine);
    /**
     * A whitespace JSONToken.
     * @param text The text of the whitespace.
     * @return The whitespace JSONToken.
     */
    public static JSONToken whitespace(String text)
    {
        return new JSONToken(text, JSONTokenType.Whitespace);
    }
    /**
     * A quoted-string ("\"hello\"") JSONToken.
     * @param quotedText The text of the quoted string (including the quotes).
     * @return The quoted-string JSONToken.
     */
    public static JSONToken quotedString(String quotedText)
    {
        return new JSONToken(quotedText, JSONTokenType.QuotedString);
    }
    /**
     * A boolean ("true" or "false") JSONToken.
     */
    public static JSONToken booleanToken(boolean value)
    {
        return value ? JSONToken.trueToken : JSONToken.falseToken;
    }
    /**
     * A false ("false") JSONToken.
     */
    public static final JSONToken falseToken = new JSONToken("false", JSONTokenType.Boolean);
    /**
     * A true ("true") JSONToken.
     */
    public static final JSONToken trueToken = new JSONToken("true", JSONTokenType.Boolean);
    /**
     * A number ("12.345") JSONToken.
     * @param text The text of the number.
     * @return The number JSONToken.
     */
    public static JSONToken number(String text)
    {
        return new JSONToken(text, JSONTokenType.Number);
    }
    /**
     * A number ("12.345") JSONToken.
     * @param value The value of the number.
     * @return The number JSONToken.
     */
    public static JSONToken number(long value)
    {
        return JSONToken.number(Longs.toString(value));
    }
    /**
     * A number ("12.345") JSONToken.
     * @param value The value of the number.
     * @return The number JSONToken.
     */
    public static JSONToken number(double value)
    {
        return JSONToken.number(Doubles.toString(value));
    }

    /**
     * A line comment (// comment text) JSONToken.
     * @param text The text of the line comment (not including the terminating newline or carriage
     *             return).
     * @return The line comment JSONToken.
     */
    public static JSONToken lineComment(String text)
    {
        return new JSONToken(text, JSONTokenType.LineComment);
    }

    /**
     * A block comment JSONToken.
     * @param text The text of the block comment.
     * @return The block comment JSONToken.
     */
    public static JSONToken blockComment(String text)
    {
        return new JSONToken(text, JSONTokenType.BlockComment);
    }
}
