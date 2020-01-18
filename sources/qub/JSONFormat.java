package qub;

/**
 * Options that can be set when creating a formatted JSON string.
 */
public class JSONFormat
{
    private static final char[] whitespaceCharacters = new char[] { ' ', '\t', '\r', '\n' };

    private static final String newLinePropertyName = "newLine";
    private static final String singleIndentPropertyName = "singleIndent";
    private static final String afterPropertySeparatorPropertyName = "afterPropertySeparator";

    /**
     * A consise JSON format that removes all unnecessary whitespace.
     */
    public static final JSONFormat consise = JSONFormat.create()
        .setNewLine("")
        .setSingleIndent("")
        .setAfterPropertySeparator("");

    /**
     * A prettier JSON format that uses a 2-space indentation and a "\n" newline.
     */
    public static final JSONFormat pretty = JSONFormat.create()
        .setNewLine("\n")
        .setSingleIndent("  ")
        .setAfterPropertySeparator(" ");

    private String newLine;
    private String singleIndent;
    private String afterPropertySeparator;

    private JSONFormat()
    {
        this.newLine = "";
        this.singleIndent = "";
        this.afterPropertySeparator = "";
    }

    /**
     * Create a new JSONFormat object.
     * @return A new JSONFormat object.
     */
    public static JSONFormat create()
    {
        return new JSONFormat();
    }

    /**
     * Get the string that will be used to mark a new line.
     * @return The string that will be used to mark a new line.
     */
    public String getNewLine()
    {
        return this.newLine;
    }

    /**
     * Set the string that will be used to mark a new line.
     * @param newLine The string that will be used to mark a new line.
     * @return This object for method chaining.
     */
    public JSONFormat setNewLine(String newLine)
    {
        PreCondition.assertNotNull(newLine, "newLine");
        PreCondition.assertContainsOnly(newLine, JSONFormat.whitespaceCharacters, "newLine");

        this.newLine = newLine;
        return this;
    }

    /**
     * Get a single indentation string.
     * @return A single indentation string.
     */
    public String getSingleIndent()
    {
        return this.singleIndent;
    }

    /**
     * Set the string that will be used as a single indentation.
     * @param singleIndent The string that will be used as a single indentation.
     * @return This object for method chaining.
     */
    public JSONFormat setSingleIndent(String singleIndent)
    {
        PreCondition.assertNotNull(singleIndent, "singleIndent");
        PreCondition.assertContainsOnly(singleIndent, JSONFormat.whitespaceCharacters, "singleIndent");

        this.singleIndent = singleIndent;
        return this;
    }

    /**
     * Get the string that will be inserted between a property's seprator and value.
     * @return The string that will be inserted between a property's seprator and value.
     */
    public String getAfterPropertySeparator()
    {
        return this.afterPropertySeparator;
    }

    /**
     * Set the string that will be inserted between a property's seprator and value.
     * @param afterPropertySeparator The string that will be inserted between a property's
     *                               separator and value.
     * @return This object for method chaining.
     */
    public JSONFormat setAfterPropertySeparator(String afterPropertySeparator)
    {
        PreCondition.assertNotNull(afterPropertySeparator, "afterPropertySeparator");
        PreCondition.assertContainsOnly(afterPropertySeparator, JSONFormat.whitespaceCharacters, "afterPropertySeparator");

        this.afterPropertySeparator = afterPropertySeparator;
        return this;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONFormat && this.equals((JSONFormat)rhs);
    }

    public boolean equals(JSONFormat rhs)
    {
        return rhs != null &&
            this.newLine.equals(rhs.newLine) &&
            this.singleIndent.equals(rhs.singleIndent) &&
            this.afterPropertySeparator.equals(rhs.afterPropertySeparator);
    }

    /**
     * Convert this object to a JSONObject.
     * @return The JSONObject that represents this object.
     */
    public JSONObject toJson()
    {
        return JSONObject.create()
            .setString(JSONFormat.newLinePropertyName, Strings.escape(this.newLine))
            .setString(JSONFormat.singleIndentPropertyName, Strings.escape(this.singleIndent))
            .setString(JSONFormat.afterPropertySeparatorPropertyName, Strings.escape(this.afterPropertySeparator));
    }

    @Override
    public String toString()
    {
        return toJson().toString();
    }
}
