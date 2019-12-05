package qub;

public class JSONArrayBuilder implements Disposable
{
    private final CharacterWriteStream stream;
    private final BasicDisposable disposable;
    private boolean wroteFirstElement;

    public JSONArrayBuilder(CharacterWriteStream stream)
    {
        PreCondition.assertNotNull(stream, "stream");
        PreCondition.assertFalse(stream.isDisposed(), "stream.isDisposed()");

        this.stream = stream;
        stream.write('[').await();
        disposable = BasicDisposable.create(() -> stream.write(']').await());
    }

    private void element(Action0 writeElementValue)
    {
        PreCondition.assertNotNull(writeElementValue, "writeElementValue");

        if (!wroteFirstElement)
        {
            wroteFirstElement = true;
        }
        else
        {
            stream.write(',').await();
        }

        writeElementValue.run();
    }

    private void element(String elementValue)
    {
        PreCondition.assertNotNullAndNotEmpty(elementValue, "elementValue");

        element(() -> stream.write(elementValue).await());
    }

    public void booleanElement(boolean elementValue)
    {
        element(Booleans.toString(elementValue));
    }

    public void numberElement(long elementValue)
    {
        element(Longs.toString(elementValue));
    }

    public void numberElement(double propertyValue)
    {
        element(Doubles.toString(propertyValue));
    }

    public void stringElement(String elementValue)
    {
        PreCondition.assertNotNull(elementValue, "elementValue");

        element(Strings.escapeAndQuote(elementValue));
    }

    public void nullElement()
    {
        element("null");
    }

    public void objectElement()
    {
        objectElement((JSONObjectBuilder builder) -> {});
    }

    public void objectElement(Action1<JSONObjectBuilder> action)
    {
        PreCondition.assertNotNull(action, "action");

        element(() ->
        {
            final JSONObjectBuilder objectBuilder = new JSONObjectBuilder(stream);
            action.run(objectBuilder);
            objectBuilder.dispose();
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
