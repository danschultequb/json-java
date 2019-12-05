package qub;

public class JSONProperty extends JSONSegment
{
    private final Iterable<JSONSegment> segments;

    public JSONProperty(Iterable<JSONSegment> segments)
    {
        this.segments = segments;
    }

    public String getName()
    {
        return getNameSegment().toUnquotedString();
    }

    public JSONQuotedString getNameSegment()
    {
        return (JSONQuotedString)segments.first();
    }

    public JSONToken getColonSegment()
    {
        return segments
                .instanceOf(JSONToken.class)
                .first((JSONToken jsonToken) -> jsonToken.getType() == JSONTokenType.Colon);
    }

    public JSONSegment getValueSegment()
    {
        JSONSegment result = null;
        if (segments.getCount() > 2)
        {
            final JSONSegment lastSegment = segments.last();
            if (lastSegment instanceof JSONToken)
            {
                final JSONToken lastToken = (JSONToken)lastSegment;
                switch (lastToken.getType())
                {
                    case Boolean:
                    case Null:
                    case Number:
                    case QuotedString:
                        result = lastToken;
                        break;
                }
            }
            else
            {
                result = lastSegment;
            }
        }
        return result;
    }

    /**
     * Get the value of this property as a number JSONToken.
     * @return The result of attempting to get the value of this property as a number JSONToken.
     */
    public Result<JSONToken> getNumberTokenValue()
    {
        Result<JSONToken> result;
        final JSONSegment valueSegment = getValueSegment();
        if (valueSegment == null)
        {
            result = Result.error(new NotFoundException("No value was found for the JSONProperty."));
        }
        else if (!(valueSegment instanceof JSONToken && ((JSONToken)valueSegment).getType() == JSONTokenType.Number))
        {
            result = Result.error(new WrongTypeException("Expected the value of the property named " + Strings.escapeAndQuote(getName()) + " to be a number."));
        }
        else
        {
            result = Result.success((JSONToken)valueSegment);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Get the value of this property as a number.
     * @return The result of attempting to get the value of this property as a number.
     */
    public Result<Double> getNumberValue()
    {
        Result<Double> result = getNumberTokenValue()
            .then((JSONToken numberToken) -> java.lang.Double.valueOf(numberToken.toString()));

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    public Result<JSONObject> getObjectValue()
    {
        Result<JSONObject> result;
        final JSONSegment valueSegment = getValueSegment();
        if (valueSegment == null)
        {
            result = Result.error(new NotFoundException("No value was found for the JSONProperty."));
        }
        else if (!(valueSegment instanceof JSONObject))
        {
            result = Result.error(new WrongTypeException("Expected the value of the property named " + Strings.escapeAndQuote(getName()) + " to be an object."));
        }
        else
        {
            result = Result.success((JSONObject)valueSegment);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONProperty && equals((JSONProperty)rhs);
    }

    public boolean equals(JSONProperty rhs)
    {
        return rhs != null && segments.equals(rhs.segments);
    }

    @Override
    public String toString()
    {
        return getCombinedText(segments);
    }

    @Override
    public int getStartIndex()
    {
        return getStartIndex(segments);
    }

    @Override
    public int getAfterEndIndex()
    {
        return getAfterEndIndex(segments);
    }
}