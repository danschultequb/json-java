package qub;

public class JSONNull implements JSONSegment
{
    public static final JSONNull segment = new JSONNull();

    private JSONNull()
    {
    }

    @Override
    public String toString()
    {
        return JSONSegment.toString(this);
    }

    @Override
    public Result<Integer> toString(IndentedCharacterWriteStream stream, JSONFormat format)
    {
        PreCondition.assertNotNull(stream, "stream");
        PreCondition.assertNotNull(format, "format");

        return stream.write("null");
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONNull;
    }
}
