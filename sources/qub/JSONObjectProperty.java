package qub;

/**
 * A property within a JSON object.
 */
public class JSONObjectProperty implements MapEntry<String,JSONSegment>
{
    private final String name;
    private final JSONSegment value;

    private JSONObjectProperty(String name, JSONSegment value)
    {
        PreCondition.assertNotNullAndNotEmpty(name, "name");
        PreCondition.assertNotNull(value, "value");

        this.name = name;
        this.value = value;
    }

    public static JSONObjectProperty create(String name, boolean value)
    {
        return JSONObjectProperty.create(name, JSONBoolean.get(value));
    }

    public static JSONObjectProperty create(String name, long value)
    {
        return JSONObjectProperty.create(name, JSONNumber.get(value));
    }

    public static JSONObjectProperty create(String name, double value)
    {
        return JSONObjectProperty.create(name, JSONNumber.get(value));
    }

    public static JSONObjectProperty create(String name, String value)
    {
        PreCondition.assertNotNull(value, "value");

        return JSONObjectProperty.create(name, JSONString.get(value));
    }

    public static JSONObjectProperty create(String name, JSONSegment value)
    {
        return new JSONObjectProperty(name, value);
    }

    /**
     * Get the name of this property.
     * @return The name of this property.
     */
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getKey()
    {
        return this.getName();
    }

    @Override
    public JSONSegment getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return JSONSegment.toString((Function1<IndentedCharacterWriteStream,Result<Integer>>)this::toString);
    }

    public Result<Integer> toString(IndentedCharacterWriteStream stream)
    {
        PreCondition.assertNotNull(stream, "stream");
        PreCondition.assertNotDisposed(stream, "stream");

        return Result.create(() ->
        {
            int result = 0;

            result += stream.write(Strings.quote(this.name)).await();
            result += stream.write(':').await();
            result += this.value.toString(stream).await();

            PostCondition.assertGreaterThanOrEqualTo(result, 5, "result");

            return result;
        });
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONObjectProperty && this.equals((JSONObjectProperty)rhs);
    }

    public boolean equals(JSONObjectProperty rhs)
    {
        return rhs != null &&
            this.name.equals(rhs.name) &&
            this.value.equals(rhs.value);
    }
}
