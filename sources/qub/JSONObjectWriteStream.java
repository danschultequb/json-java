package qub;

public class JSONObjectWriteStream
{
    private final JSONWriteStream writeStream;
    private boolean wroteFirstProperty;

    public JSONObjectWriteStream(JSONWriteStream writeStream)
    {
        this.writeStream = writeStream;
    }

    private void writePropertyBeforeValue(String unquotedPropertyName)
    {
        if (!wroteFirstProperty)
        {
            wroteFirstProperty = true;
        }
        else
        {
            writeStream.writeComma();
        }

        writeStream.writeQuotedString(unquotedPropertyName);
        writeStream.writeColon();
    }

    public void writeNullProperty(String unquotedPropertyName)
    {
        writePropertyBeforeValue(unquotedPropertyName);
        writeStream.writeNull();
    }

    public void writeBooleanProperty(String unquotedPropertyName, boolean propertyValue)
    {
        writePropertyBeforeValue(unquotedPropertyName);
        writeStream.writeBoolean(propertyValue);
    }

    public void writeNumberProperty(String unquotedPropertyName, long propertyValue)
    {
        writePropertyBeforeValue(unquotedPropertyName);
        writeStream.writeNumber(propertyValue);
    }

    public void writeNumberProperty(String unquotedPropertyName, double propertyValue)
    {
        writePropertyBeforeValue(unquotedPropertyName);
        writeStream.writeNumber(propertyValue);
    }

    public void writeQuotedStringProperty(String unquotedPropertyName, String unquotedPropertyValue)
    {
        writePropertyBeforeValue(unquotedPropertyName);
        writeStream.writeQuotedString(unquotedPropertyValue);
    }

    public void writeObjectProperty(String unquotedPropertyName, Action1<JSONObjectWriteStream> writeObjectAction)
    {
        writePropertyBeforeValue(unquotedPropertyName);
        writeStream.writeObject(writeObjectAction);
    }

    public void writeArrayProperty(String unquotedPropertyName, Action1<JSONArrayWriteStream> writeArrayAction)
    {
        writePropertyBeforeValue(unquotedPropertyName);
        writeStream.writeArray(writeArrayAction);
    }
}
