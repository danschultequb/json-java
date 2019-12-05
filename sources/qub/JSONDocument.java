package qub;

/**
 * The parsed segements of a JSON document. This object includes all of the textual details of the
 * original document, such as where each segment was located (character offset).
 */
public class JSONDocument
{
    private final Iterable<JSONSegment> segments;

    /**
     * Create a new JSONDocument from the provided JSONSegments.
     * @param segments The parsed JSONSegments of the original document.
     */
    public JSONDocument(Iterable<JSONSegment> segments)
    {
        this.segments = segments;
    }

    /**
     * Get the parsed JSONSegments from the original document.
     * @return The parsed JSONSegments from the original document.
     */
    public Iterable<JSONSegment> getSegments()
    {
        return segments;
    }

    /**
     * Get the root of this JSON document.
     * @return The root of this JSON document.
     */
    public Result<JSONSegment> getRoot()
    {
        final JSONSegment rootSegment = segments.first((JSONSegment segment) ->
        {
            return segment instanceof JSONObject || segment instanceof JSONArray;
        });
        return rootSegment != null
            ? Result.success(rootSegment)
            : Result.error(new NotFoundException("No root was found in the JSON document."));
    }

    /**
     * Get the root of this JSON document if it is a JSON array.
     * @return The root of this JSON document if it is a JSON array.
     */
    public Result<JSONArray> getRootArray()
    {
        return getRoot()
            .then((JSONSegment root) ->
            {
                if (!(root instanceof JSONArray))
                {
                    throw new WrongTypeException("Expected the root of the JSON document to be an array.");
                }
                return (JSONArray)root;
            });
    }

    /**
     * Get the root of this JSON document if it is a JSON object.
     * @return The root of this JSON document if it is a JSON object.
     */
    public Result<JSONObject> getRootObject()
    {
        return getRoot()
            .thenResult((JSONSegment root) ->
            {
                return root instanceof JSONObject
                    ? Result.success((JSONObject)root)
                    : Result.error(new WrongTypeException("Expected the root of the JSON document to be an object."));
            });
    }

    @Override
    public String toString()
    {
        return JSONSegment.getCombinedText(segments);
    }

    /**
     * Get the number of characters that made up the original document.
     * @return The number of characters that made up the original document.
     */
    public int getLength()
    {
        return JSONSegment.getAfterEndIndex(segments);
    }
}
