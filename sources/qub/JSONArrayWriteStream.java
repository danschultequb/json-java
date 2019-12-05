package qub;

public class JSONArrayWriteStream
{
    private final JSONWriteStream writeStream;
    private boolean wroteFirstElement;

    public JSONArrayWriteStream(JSONWriteStream writeStream)
    {
        this.writeStream = writeStream;
    }

    private void writeElementBeforeValue()
    {
        if (!wroteFirstElement)
        {
            wroteFirstElement = true;
        }
        else
        {
            writeStream.writeComma();
        }
    }

    public void writeNull()
    {
        writeElementBeforeValue();
        writeStream.writeNull();
    }

    public void writeBoolean(boolean propertyValue)
    {
        writeElementBeforeValue();
        writeStream.writeBoolean(propertyValue);
    }

    public void writeNumber(long propertyValue)
    {
        writeElementBeforeValue();
        writeStream.writeNumber(propertyValue);
    }

    public void writeNumber(double propertyValue)
    {
        writeElementBeforeValue();
        writeStream.writeNumber(propertyValue);
    }

    public void writeQuotedString(String unquotedPropertyValue)
    {
        writeElementBeforeValue();
        writeStream.writeQuotedString(unquotedPropertyValue);
    }

    public void writeObject(Action1<JSONObjectWriteStream> writeObjectAction)
    {
        writeElementBeforeValue();
        writeStream.writeObject(writeObjectAction);
    }

    public void writeArray(Action1<JSONArrayWriteStream> writeArrayAction)
    {
        writeElementBeforeValue();
        writeStream.writeArray(writeArrayAction);
    }
}
