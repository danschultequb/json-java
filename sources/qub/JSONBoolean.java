package qub;

/**
 * A boolean JSON value.
 */
public class JSONBoolean implements JSONSegment
{
    public final static JSONBoolean trueSegment = new JSONBoolean(true);
    public final static JSONBoolean falseSegment = new JSONBoolean(false);

    private final boolean value;

    private JSONBoolean(boolean value)
    {
        this.value = value;
    }

    /**
     * Get a JSONBoolean object with the provided value.
     * @param value The value.
     * @return A JSONBoolean object with the provided value.
     */
    public static JSONBoolean get(boolean value)
    {
        return value ? JSONBoolean.trueSegment : JSONBoolean.falseSegment;
    }

    /**
     * Get the boolean value of this object.
     * @return The boolean value of this object.
     */
    public boolean getValue()
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
        return stream.write(Booleans.toString(this.value));
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONBoolean && this.equals((JSONBoolean)rhs);
    }

    public boolean equals(JSONBoolean rhs)
    {
        return rhs != null && this.value == rhs.value;
    }
}
