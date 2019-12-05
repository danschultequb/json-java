package qub;

public class JSONWriteStream implements Disposable
{
    private final CharacterWriteStream writeStream;

    public JSONWriteStream(CharacterWriteStream writeStream)
    {
        this.writeStream = writeStream;
    }

    public Result<?> writeColon()
    {
        return writeStream.write(':');
    }

    public Result<?> writeComma()
    {
        return writeStream.write(',');
    }

    public Result<?> writeLeftCurlyBracket()
    {
        return writeStream.write('{');
    }

    public Result<?> writeRightCurlyBracket()
    {
        return writeStream.write('}');
    }

    public Result<?> writeLeftSquareBracket()
    {
        return writeStream.write('[');
    }

    public Result<?> writeRightSquareBracket()
    {
        return writeStream.write(']');
    }

    public Result<?> writeBoolean(boolean value)
    {
        return writeStream.write(value ? "true" : "false");
    }

    public Result<?> writeNull()
    {
        return writeStream.write("null");
    }

    public Result<?> writeNumber(long value)
    {
        return writeStream.write(Long.toString(value));
    }

    public Result<?> writeNumber(double value)
    {
        return writeStream.write(Double.toString(value));
    }

    public Result<?> writeQuotedString(String unquotedText)
    {
        return writeQuotedString('\"', unquotedText);
    }

    public Result<?> writeQuotedString(char quoteCharacter, String unquotedText)
    {
        return Result.create(() ->
        {
            writeStream.write(quoteCharacter).await();
            if (unquotedText != null && !unquotedText.isEmpty())
            {
                writeStream.write(unquotedText).await();
            }
            writeStream.write(quoteCharacter).await();
        });
    }

    public Result<?> writeObject()
    {
        return writeObject(null);
    }

    public Result<?> writeObject(Action1<JSONObjectWriteStream> writeArrayAction)
    {
        return Result.create(() ->
        {
            writeLeftCurlyBracket().await();
            if (writeArrayAction != null)
            {
                writeArrayAction.run(new JSONObjectWriteStream(this));
            }
            writeRightCurlyBracket().await();
        });

    }

    public Result<?> writeArray()
    {
        return writeArray(null);
    }

    public Result<?> writeArray(Action1<JSONArrayWriteStream> writeArrayAction)
    {
        return Result.create(() ->
        {
            writeLeftSquareBracket().await();
            if (writeArrayAction != null)
            {
                writeArrayAction.run(new JSONArrayWriteStream(this));
            }
            writeRightSquareBracket().await();
        });
    }

    @Override
    public boolean isDisposed()
    {
        return writeStream.isDisposed();
    }

    @Override
    public Result<Boolean> dispose()
    {
        return writeStream.dispose();
    }
}
