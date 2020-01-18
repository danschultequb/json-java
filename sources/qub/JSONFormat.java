package qub;

/**
 * Options that can be set when creating a formatted JSON string.
 */
public class JSONFormat
{
    private String singleIndent;

    private JSONFormat()
    {
        this.singleIndent = "  ";
    }

    public static JSONFormat create()
    {
        return new JSONFormat();
    }
}
