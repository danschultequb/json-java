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
    public Result<Integer> toString(IndentedCharacterWriteStream stream)
    {
        PreCondition.assertNotNull(stream, "stream");

        return Result.create(() ->
        {
            int result = 0;

            result += stream.write('[').await();
            boolean firstElement = true;
            for (final JSONSegment element : this.elements)
            {
                if (firstElement)
                {
                    firstElement = false;
                }
                else
                {
                    result += stream.write(',').await();
                }
                result += element.toString(stream).await();
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
