package qub;

public class JSONNumber implements JSONSegment
{
    private final String text;
    private final double value;

    private JSONNumber(String text, double value)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        this.text = text;
        this.value = value;
    }

    public static JSONNumber get(long value)
    {
        return new JSONNumber(Longs.toString(value), value);
    }

    public static JSONNumber get(double value)
    {
        return new JSONNumber(Doubles.toString(value), value);
    }

    public static JSONNumber get(String text)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        return new JSONNumber(text, java.lang.Double.parseDouble(text));
    }

    public double getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return JSONSegment.toString(this);
    }

    @Override
    public Result<Integer> toString(IndentedCharacterWriteStream stream)
    {
        return stream.write(this.text);
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONNumber && this.equals((JSONNumber)rhs);
    }

    public boolean equals(JSONNumber rhs)
    {
        return rhs != null &&
            this.value == rhs.value &&
            this.text.equals(rhs.text);
    }
}
