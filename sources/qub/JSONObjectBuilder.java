package qub;

/**
 * A type that can be used to build a JSON object.
 */
public class JSONObjectBuilder implements Disposable
{
    private final CharacterWriteStream stream;
    private final BasicDisposable disposable;
    private boolean wroteFirstProperty;

    /**
     * Create a new JSONObjectBuilder that will write JSON object characters to the provided
     * CharacterWriteStream.
     * @param stream The stream that the JSON object characters will be written to.
     */
    public JSONObjectBuilder(CharacterWriteStream stream)
    {
        PreCondition.assertNotNull(stream, "stream");
        PreCondition.assertFalse(stream.isDisposed(), "stream.isDisposed()");

        this.stream = stream;
        stream.write('{').await();
        disposable = BasicDisposable.create(() -> stream.write('}').await());
    }

    private void property(String propertyName, Action0 writePropertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(writePropertyValue, "writePropertyValue");

        if (!wroteFirstProperty)
        {
            wroteFirstProperty = true;
        }
        else
        {
            stream.write(',').await();
        }

        stream.write(Strings.escapeAndQuote(propertyName)).await();
        stream.write(':').await();
        writePropertyValue.run();
    }

    private void property(String propertyName, String propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNullAndNotEmpty(propertyValue, "propertyValue");

        property(propertyName, () -> stream.write(propertyValue).await());
    }

    public void booleanProperty(String propertyName, boolean propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        property(propertyName, Booleans.toString(propertyValue));
    }

    public void numberProperty(String propertyName, long propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        property(propertyName, Longs.toString(propertyValue));
    }

    public void numberProperty(String propertyName, double propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        property(propertyName, Doubles.toString(propertyValue));
    }

    public void stringProperty(String propertyName, String propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(propertyValue, "propertyValue");

        property(propertyName, Strings.escapeAndQuote(propertyValue));
    }

    /**
     * Write a null property.
     * @param propertyName The name of the property.
     */
    public void nullProperty(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        property(propertyName, "null");
    }

    /**
     * Write a string or null property.
     * @param propertyName The name of the property.
     * @param propertyValue The value of the property.
     */
    public Result<Void> stringOrNullProperty(String propertyName, String propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return Result.create(() ->
        {
            this.property(propertyName, propertyValue == null ? "null" : Strings.escapeAndQuote(propertyValue));
        });
    }

    public void objectProperty(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        objectProperty(propertyName, (JSONObjectBuilder builder) -> {});
    }

    public void objectProperty(String propertyName, Action1<JSONObjectBuilder> action)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(action, "action");

        property(propertyName, () ->
        {
            final JSONObjectBuilder objectBuilder = new JSONObjectBuilder(stream);
            action.run(objectBuilder);
            objectBuilder.dispose();
        });
    }

    public void arrayProperty(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        arrayProperty(propertyName, (JSONArrayBuilder builder) -> {});
    }

    public void arrayProperty(String propertyName, Action1<JSONArrayBuilder> action)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(action, "action");

        property(propertyName, () ->
        {
            final JSONArrayBuilder arrayBuilder = new JSONArrayBuilder(stream);
            action.run(arrayBuilder);
            arrayBuilder.dispose();
        });
    }

    public void stringArrayProperty(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        stringArrayProperty(propertyName, Iterable.create());
    }

    public void stringArrayProperty(String propertyName, Iterable<String> stringElements)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(stringElements, "stringElements");

        arrayProperty(propertyName, (JSONArrayBuilder builder) ->
        {
            for (final String stringElement : stringElements)
            {
                builder.stringElement(stringElement);
            }
        });
    }

    @Override
    public boolean isDisposed()
    {
        return disposable.isDisposed();
    }

    @Override
    public Result<Boolean> dispose()
    {
        return disposable.dispose();
    }
}
