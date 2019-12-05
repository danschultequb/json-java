package qub;

public class JSONArray extends JSONSegment
{
    private final Iterable<JSONSegment> segments;

    public JSONArray(Iterable<JSONSegment> segments)
    {
        this.segments = segments;
    }

    public JSONToken getLeftSquareBracket()
    {
        return (JSONToken)segments.first();
    }

    public JSONToken getRightSquareBracket()
    {
        JSONToken result = null;

        final JSONSegment lastSegment = segments.last();
        if (lastSegment instanceof JSONToken)
        {
            final JSONToken lastToken = (JSONToken)lastSegment;
            if (lastToken.getType() == JSONTokenType.RightSquareBracket)
            {
                result = lastToken;
            }
        }

        return result;
    }

    public Indexable<JSONSegment> getElements()
    {
        final List<JSONSegment> result = new ArrayList<>();

        Iterable<JSONSegment> innerSegments = segments.skip(1);
        if (getRightSquareBracket() != null)
        {
            innerSegments = innerSegments.skipLast(1);
        }

        boolean expectingElement = true;
        for (final JSONSegment segment : innerSegments)
        {
            if (segment instanceof JSONToken)
            {
                final JSONToken token = (JSONToken)segment;
                switch (token.getType())
                {
                    case Boolean:
                    case Null:
                    case Number:
                    case QuotedString:
                        result.add(token);
                        expectingElement = false;
                        break;

                    case Comma:
                        if (expectingElement)
                        {
                            result.add(null);
                        }
                        else
                        {
                            expectingElement = true;
                        }
                        break;
                }
            }
            else
            {
                result.add(segment);
                expectingElement = false;
            }
        }

        if (result.any() && expectingElement)
        {
            result.add(null);
        }

        return result;
    }

    public int getElementCount()
    {
        return getElements().getCount();
    }

    /**
     * Get the element at the provided index.
     * @param index The index of the element to return.
     * @return The element at the provided index.
     */
    public JSONSegment getElement(int index)
    {
        return getElements().get(index);
    }

    /**
     * Get the object value of the element at the provided index.
     * @param index The index of the element to return.
     * @return The object value of the element at the provided index.
     */
    public Result<JSONObject> getObjectElement(int index)
    {
        final JSONSegment element = getElement(index);
        return element instanceof JSONObject
            ? Result.success((JSONObject)element)
            : Result.error(new WrongTypeException("Expected the value of the element at index " + index + " to be an object."));
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONArray && equals((JSONArray)rhs);
    }

    public boolean equals(JSONArray rhs)
    {
        return rhs != null &&
            segments.equals(rhs.segments);
    }

    @Override
    public String toString()
    {
        return JSONSegment.getCombinedText(segments);
    }

    @Override
    public int getStartIndex()
    {
        return JSONSegment.getStartIndex(segments);
    }

    @Override
    public int getAfterEndIndex()
    {
        return JSONSegment.getAfterEndIndex(segments);
    }
}
