package qub;

/**
 * A segment within some JSON content.
 */
public interface JSONSegment
{
    /**
     * Get the String representation of this JSONSegment using the provided format.
     * @param format The format to use when converting this JSONSegment to a string.
     * @return The number of characters that were written.
     */
    default String toString(JSONFormat format)
    {
        PreCondition.assertNotNull(format, "format");

        return JSONSegment.toString((IndentedCharacterWriteStream stream) -> this.toString(stream, format));
    }

    /**
     * Write the String representation of this JSONSegment to the provided stream.
     * @param stream The stream to write the String representation of this JSONSegment to.
     * @return The number of characters that were written.
     */
    default Result<Integer> toString(IndentedCharacterWriteStream stream)
    {
        return this.toString(stream, JSONFormat.consise);
    }

    /**
     * Write the String representation of this JSONSegment to the provided stream.
     * @param stream The stream to write the String representation of this JSONSegment to.
     * @param format The format to use when converting this JSONSegment to a string.
     * @return The number of characters that were written.
     */
    Result<Integer> toString(IndentedCharacterWriteStream stream, JSONFormat format);

    /**
     * Get the String representation of the provided JSONSegment.
     * @param segment The JSONSegment to get the String representation of.
     * @return The String representation of the provided JSONSegment.
     */
    static String toString(JSONSegment segment)
    {
        return JSONSegment.toString((Function1<IndentedCharacterWriteStream,Result<Integer>>)segment::toString);
    }

    static String toString(Function1<IndentedCharacterWriteStream,Result<Integer>> toStringFunction)
    {
        PreCondition.assertNotNull(toStringFunction, "toStringFunction");

        final InMemoryCharacterToByteStream characterStream = InMemoryCharacterToByteStream.create();
        final IndentedCharacterWriteStream indentedStream = IndentedCharacterWriteStream.create(characterStream);
        toStringFunction.run(indentedStream).await();
        final String result = characterStream.getText().await();

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    static String toString(Function2<IndentedCharacterWriteStream,JSONFormat,Result<Integer>> toStringFunction)
    {
        PreCondition.assertNotNull(toStringFunction, "toStringFunction");

        return JSONSegment.toString((IndentedCharacterWriteStream indentedStream) -> toStringFunction.run(indentedStream, JSONFormat.consise));
    }
}
