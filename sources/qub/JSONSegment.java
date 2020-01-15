package qub;

/**
 * A segment within some JSON content.
 */
public interface JSONSegment
{
    /**
     * Write the String representation of this JSONSegment to the provided stream.
     * @param stream The stream to write the String representation of this JSONSegment to.
     * @return The number of characters that were written.
     */
    Result<Integer> toString(IndentedCharacterWriteStream stream);

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

        final InMemoryCharacterStream characterStream = new InMemoryCharacterStream();
        toStringFunction.run(new IndentedCharacterWriteStream(characterStream)).await();
        final String result = characterStream.getText().await();

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }
}
