package qub;

public abstract class JSONSegment
{
    public abstract String toString();

    public abstract int getStartIndex();

    public abstract int getAfterEndIndex();

    public int getLength()
    {
        return getAfterEndIndex() - getStartIndex();
    }

    public Span getSpan()
    {
        final int startIndex = getStartIndex();
        final int afterEndIndex = getAfterEndIndex();
        return new Span(startIndex, afterEndIndex - startIndex);
    }

    @Override
    public abstract boolean equals(Object rhs);

    protected static int getStartIndex(Iterable<JSONSegment> segments)
    {
        return segments.first().getStartIndex();
    }

    protected static int getAfterEndIndex(Iterable<JSONSegment> segments)
    {
        return !segments.any() ? 0 : segments.last().getAfterEndIndex();
    }

    protected static String getCombinedText(Iterable<JSONSegment> segments)
    {
        final StringBuilder builder = new StringBuilder();
        for (final JSONSegment segment : segments)
        {
            builder.append(segment.toString());
        }
        return builder.toString();
    }
}