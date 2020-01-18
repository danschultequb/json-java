package qub;

/**
 * A JSON array.
 */
public class JSONArray implements JSONSegment, List<JSONSegment>
{
    private final List<JSONSegment> elements;

    private JSONArray(List<JSONSegment> elements)
    {
        PreCondition.assertNotNull(elements, "elements");

        this.elements = elements;
    }

    public static JSONArray create(JSONSegment... elements)
    {
        PreCondition.assertNotNull(elements, "elements");

        return JSONArray.create(Indexable.create(elements));
    }

    public static JSONArray create(Iterable<JSONSegment> elements)
    {
        PreCondition.assertNotNull(elements, "elements");

        return new JSONArray(List.create(elements));
    }

    @Override
    public JSONSegment get(int index)
    {
        return this.elements.get(index);
    }

    @Override
    public Iterator<JSONSegment> iterate()
    {
        return this.elements.iterate();
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

        stream.setSingleIndent(format.getSingleIndent());

        final String newLine = format.getNewLine();
        final boolean hasNewLine = !Strings.isNullOrEmpty(newLine);

        return Result.create(() ->
        {
            int result = 0;

            result += stream.write('[').await();
            stream.increaseIndent();
            try
            {
                boolean wroteElement = false;
                for (final JSONSegment element : this.elements)
                {
                    if (!wroteElement)
                    {
                        wroteElement = true;
                    }
                    else
                    {
                        result += stream.write(',').await();
                    }
                    if (hasNewLine)
                    {
                        result += stream.write(newLine).await();
                    }
                    result += element.toString(stream, format).await();
                }
                if (hasNewLine && wroteElement)
                {
                    result += stream.write(newLine).await();
                }
            }
            finally
            {
                stream.decreaseIndent();
            }
            result += stream.write(']').await();

            PostCondition.assertGreaterThanOrEqualTo(result, 2, "result");

            return result;
        });
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONArray && this.equals((JSONArray)rhs);
    }

    public boolean equals(JSONArray rhs)
    {
        return rhs != null && this.elements.equals(rhs.elements);
    }

    @Override
    public JSONArray insert(int insertIndex, JSONSegment value)
    {
        PreCondition.assertBetween(0, insertIndex, this.getCount(), "insertIndex");
        PreCondition.assertNotNull(value, "value");

        this.elements.insert(insertIndex, value);
        return this;
    }

    @Override
    public JSONSegment removeAt(int index)
    {
        PreCondition.assertIndexAccess(index, this.getCount(), "index");

        return this.elements.removeAt(index);
    }

    @Override
    public JSONArray set(int index, JSONSegment value)
    {
        PreCondition.assertIndexAccess(index, this.getCount(), "index");
        PreCondition.assertNotNull(value, "value");

        this.elements.set(index, value);
        return this;
    }
}
