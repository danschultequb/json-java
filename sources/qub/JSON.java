package qub;

/**
 * A collection of methods for interacting with JSON.
 */
public interface JSON
{
    /**
     * Parse the provided text into a JSON document.
     * @param text The text to parse.
     * @return The parsed JSONDocument.
     */
    static JSONDocument parse(String text)
    {
        final StringIterator characters = new StringIterator(text);
        return parse(characters);
    }

    static JSONDocument parse(String text, List<Issue> issues)
    {
        final StringIterator characters = new StringIterator(text);
        return parse(characters, issues);
    }

    static JSONDocument parse(Iterator<Character> characters)
    {
        return parse(characters, null);
    }

    static JSONDocument parse(Iterator<Character> characters, List<Issue> issues)
    {
        final List<JSONSegment> documentSegments = new ArrayList<>();

        final JSONTokenizer tokenizer = new JSONTokenizer(characters, issues);
        tokenizer.next();

        boolean foundRootSegment = false;
        while (tokenizer.hasCurrent())
        {
            final JSONSegment segment = parseSegment(tokenizer, issues);
            documentSegments.add(segment);

            if (segment instanceof JSONObject || segment instanceof JSONArray)
            {
                if (!foundRootSegment)
                {
                    foundRootSegment = true;
                }
                else
                {
                    addIssue(issues, JSONIssues.expectedEndOfFile(segment.getSpan()));
                }
            }
            else {
                final JSONToken token = (JSONToken)segment;
                switch (token.getType())
                {
                    case NewLine:
                    case Whitespace:
                    case LineComment:
                    case BlockComment:
                        break;

                    default:
                        if (!foundRootSegment)
                        {
                            foundRootSegment = true;
                        }
                        else
                        {
                            addIssue(issues, JSONIssues.expectedEndOfFile(token.getSpan()));
                        }
                        break;
                }
            }
        }

        return new JSONDocument(documentSegments);
    }

    static JSONSegment parseSegment(JSONTokenizer tokenizer, List<Issue> issues)
    {
        JSONSegment result;

        switch (tokenizer.getCurrent().getType())
        {
            case LeftCurlyBracket:
                result = parseObject(tokenizer, issues);
                break;

            case LeftSquareBracket:
                result = parseArray(tokenizer, issues);
                break;

            default:
                result = tokenizer.takeCurrent();
                break;
        }

        return result;
    }

    static JSONObject parseObject(String text)
    {
        return parseObject(text, 0);
    }

    static JSONObject parseObject(String text, int firstTokenStartIndex)
    {
        JSONObject result = null;

        final JSONTokenizer tokenizer = new JSONTokenizer(text, firstTokenStartIndex);
        if (tokenizer.next() && tokenizer.getCurrent().getType() == JSONTokenType.LeftCurlyBracket)
        {
            result = parseObject(tokenizer, null);
        };

        return result;
    }

    static JSONObject parseObject(JSONTokenizer tokenizer, List<Issue> issues)
    {
        final JSONToken leftCurlyBracket = tokenizer.takeCurrent();
        final List<JSONSegment> objectSegments = List.create(leftCurlyBracket);

        boolean foundRightCurlyBracket = false;
        boolean propertyNameAllowed = true;
        boolean commaAllowed = false;
        boolean rightCurlyBracketAllowed = true;
        while (!foundRightCurlyBracket && tokenizer.hasCurrent())
        {
            final JSONToken token = tokenizer.getCurrent();
            switch (token.getType())
            {
                case QuotedString:
                    if (!propertyNameAllowed)
                    {
                        addIssue(issues, JSONIssues.expectedCommaOrClosingRightCurlyBracket(token.getSpan()));
                    }

                    objectSegments.add(parseProperty(tokenizer, issues));

                    propertyNameAllowed = false;
                    commaAllowed = true;
                    rightCurlyBracketAllowed = true;
                    break;

                case RightCurlyBracket:
                    objectSegments.add(token);
                    foundRightCurlyBracket = true;
                    if (!rightCurlyBracketAllowed)
                    {
                        addIssue(issues, JSONIssues.expectedPropertyName(token.getSpan()));
                    }
                    tokenizer.next();
                    break;

                case NewLine:
                case Whitespace:
                case LineComment:
                case BlockComment:
                    objectSegments.add(token);
                    tokenizer.next();
                    break;

                case Comma:
                    objectSegments.add(token);
                    if (!commaAllowed)
                    {
                        if (!rightCurlyBracketAllowed)
                        {
                            addIssue(issues, JSONIssues.expectedPropertyName(token.getSpan()));
                        }
                        else
                        {
                            addIssue(issues, JSONIssues.expectedPropertyNameOrClosingRightCurlyBracket(token.getSpan()));
                        }
                    }
                    propertyNameAllowed = true;
                    commaAllowed = false;
                    rightCurlyBracketAllowed = false;
                    tokenizer.next();
                    break;

                default:
                    objectSegments.add(token);
                    if (propertyNameAllowed)
                    {
                        if (rightCurlyBracketAllowed)
                        {
                            addIssue(issues, JSONIssues.expectedPropertyNameOrClosingRightCurlyBracket(token.getSpan()));
                        }
                        else
                        {
                            addIssue(issues, JSONIssues.expectedPropertyName(token.getSpan()));

                            // If I get an unexpected segment after a comma, then allow a right
                            // curly bracket for the next segment.
                            rightCurlyBracketAllowed = true;
                        }
                    }
                    else
                    {
                        addIssue(issues, JSONIssues.expectedCommaOrClosingRightCurlyBracket(token.getSpan()));
                    }
                    tokenizer.next();
                    break;
            }
        }

        if (!foundRightCurlyBracket)
        {
            addIssue(issues, JSONIssues.missingClosingRightCurlyBracket(leftCurlyBracket.getSpan()));
        }

        return new JSONObject(objectSegments);
    }

    static JSONProperty parseProperty(String text)
    {
        return parseProperty(text, 0);
    }

    static JSONProperty parseProperty(String text, int startIndex)
    {
        JSONProperty result = null;

        final JSONTokenizer tokenizer = new JSONTokenizer(text, startIndex);
        if (tokenizer.next() && tokenizer.getCurrent().getType() == JSONTokenType.QuotedString)
        {
            result = parseProperty(tokenizer, null);
        }

        return result;
    }

    static JSONProperty parseProperty(JSONTokenizer tokenizer, List<Issue> issues)
    {
        final JSONToken propertyName = tokenizer.takeCurrent();
        final List<JSONSegment> propertySegments = List.create(propertyName);

        skipWhitespace(tokenizer, propertySegments);

        if (!tokenizer.hasCurrent())
        {
            addIssue(issues, JSONIssues.missingColon(propertyName.getSpan()));
        }
        else
        {
            final JSONToken colon = tokenizer.getCurrent();
            if (colon.getType() != JSONTokenType.Colon)
            {
                addIssue(issues, JSONIssues.expectedColon(colon.getSpan()));
            }
            else
            {
                propertySegments.add(colon);
                tokenizer.next();

                skipWhitespace(tokenizer, propertySegments);

                if (!tokenizer.hasCurrent())
                {
                    addIssue(issues, JSONIssues.missingPropertyValue(colon.getSpan()));
                }
                else
                {
                    final JSONToken propertyValueFirstToken = tokenizer.getCurrent();
                    switch (propertyValueFirstToken.getType())
                    {
                        case Boolean:
                        case Null:
                        case QuotedString:
                        case Number:
                        case LineComment:
                        case BlockComment:
                            propertySegments.add(propertyValueFirstToken);
                            tokenizer.next();
                            break;

                        case LeftCurlyBracket:
                            propertySegments.add(parseObject(tokenizer, issues));
                            break;

                        case LeftSquareBracket:
                            propertySegments.add(parseArray(tokenizer, issues));
                            break;

                        default:
                            addIssue(issues, JSONIssues.expectedPropertyValue(propertyValueFirstToken.getSpan()));
                            break;
                    }
                }
            }
        }

        return new JSONProperty(propertySegments);
    }

    static void skipWhitespace(JSONTokenizer tokenizer, List<JSONSegment> segments)
    {
        while (tokenizer.hasCurrent() && tokenizer.getCurrent().getType() == JSONTokenType.Whitespace)
        {
            segments.add(tokenizer.takeCurrent());
        }
    }

    static JSONArray parseArray(String text)
    {
        return parseArray(text, 0);
    }

    static JSONArray parseArray(String text, int startIndex)
    {
        JSONArray result = null;

        final JSONTokenizer tokenizer = new JSONTokenizer(text, startIndex);
        if (tokenizer.next() && tokenizer.getCurrent().getType() == JSONTokenType.LeftSquareBracket)
        {
            result = parseArray(tokenizer, null);
        }

        return result;
    }

    static JSONArray parseArray(JSONTokenizer tokenizer, List<Issue> issues)
    {
        final JSONToken leftSquareBracket = tokenizer.takeCurrent();
        final List<JSONSegment> arraySegments = List.create(leftSquareBracket);

        boolean foundRightSquareBracket = false;
        boolean rightSquareBracketAllowed = true;
        boolean elementAllowed = true;
        boolean commaAllowed = false;
        while (!foundRightSquareBracket && tokenizer.hasCurrent())
        {
            final JSONToken token = tokenizer.getCurrent();
            switch (token.getType())
            {
                case Null:
                case Boolean:
                case QuotedString:
                case Number:
                    if (!elementAllowed) {
                        addIssue(issues, JSONIssues.expectedCommaOrClosingRightSquareBracket(token.getSpan()));
                    }

                    arraySegments.add(token);
                    tokenizer.next();

                    elementAllowed = false;
                    commaAllowed = true;
                    rightSquareBracketAllowed = true;
                    break;

                case LeftCurlyBracket:
                    if (!elementAllowed) {
                        addIssue(issues, JSONIssues.expectedCommaOrClosingRightSquareBracket(token.getSpan()));
                    }

                    arraySegments.add(parseObject(tokenizer, issues));

                    elementAllowed = false;
                    commaAllowed = true;
                    rightSquareBracketAllowed = true;
                    break;

                case LeftSquareBracket:
                    if (!elementAllowed) {
                        addIssue(issues, JSONIssues.expectedCommaOrClosingRightSquareBracket(token.getSpan()));
                    }

                    arraySegments.add(parseArray(tokenizer, issues));

                    elementAllowed = false;
                    commaAllowed = true;
                    rightSquareBracketAllowed = true;
                    break;

                case Comma:
                    if (!commaAllowed) {
                        if (rightSquareBracketAllowed) {
                            addIssue(issues, JSONIssues.expectedArrayElementOrClosingRightSquareBracket(token.getSpan()));
                        }
                        else {
                            addIssue(issues, JSONIssues.expectedArrayElement(token.getSpan()));
                        }
                    }

                    arraySegments.add(token);
                    tokenizer.next();

                    elementAllowed = true;
                    commaAllowed = false;
                    rightSquareBracketAllowed = false;
                    break;

                case RightSquareBracket:
                    if (!rightSquareBracketAllowed) {
                        addIssue(issues, JSONIssues.expectedArrayElement(token.getSpan()));
                    }

                    foundRightSquareBracket = true;
                    arraySegments.add(token);
                    tokenizer.next();
                    break;

                case NewLine:
                case Whitespace:
                case LineComment:
                case BlockComment:
                    arraySegments.add(token);
                    tokenizer.next();
                    break;

                default:
                    if (elementAllowed) {
                        if (rightSquareBracketAllowed) {
                            addIssue(issues, JSONIssues.expectedArrayElementOrClosingRightSquareBracket(token.getSpan()));
                        }
                        else {
                            addIssue(issues, JSONIssues.expectedArrayElement(token.getSpan()));
                        }
                    }
                    else {
                        addIssue(issues, JSONIssues.expectedCommaOrClosingRightSquareBracket(token.getSpan()));
                    }

                    arraySegments.add(token);
                    tokenizer.next();

                    elementAllowed = false;
                    commaAllowed = true;
                    rightSquareBracketAllowed = true;
                    break;
            }
        }

        if (!foundRightSquareBracket)
        {
            addIssue(issues, JSONIssues.missingClosingRightSquareBracket(leftSquareBracket.getSpan()));
        }

        return new JSONArray(arraySegments);
    }

    static void addIssue(List<Issue> issues, Issue issue)
    {
        if (issues != null)
        {
            issues.add(issue);
        }
    }

    static JSONObject object(Action1<JSONObjectBuilder> action)
    {
        PreCondition.assertNotNull(action, "action");

        final InMemoryCharacterStream stream = new InMemoryCharacterStream();
        object(stream, action);
        final JSONObject result = parseObject(stream.getText().await());

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    static void object(CharacterWriteStream writeStream, Action1<JSONObjectBuilder> action)
    {
        PreCondition.assertNotNull(writeStream, "writeStream");
        PreCondition.assertFalse(writeStream.isDisposed(), "writeStream.isDisposed()");
        PreCondition.assertNotNull(action, "action");

        final JSONObjectBuilder objectBuilder = new JSONObjectBuilder(writeStream);
        action.run(objectBuilder);
        objectBuilder.dispose();
    }

    static JSONArray array(Action1<JSONArrayBuilder> action)
    {
        PreCondition.assertNotNull(action, "action");

        final InMemoryCharacterStream stream = new InMemoryCharacterStream();
        array(stream, action);
        final JSONArray result = parseArray(stream.getText().await());

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    static void array(CharacterWriteStream writeStream, Action1<JSONArrayBuilder> action)
    {
        PreCondition.assertNotNull(writeStream, "writeStream");
        PreCondition.assertFalse(writeStream.isDisposed(), "writeStream.isDisposed()");
        PreCondition.assertNotNull(action, "action");

        final JSONArrayBuilder arrayBuilder = new JSONArrayBuilder(writeStream);
        action.run(arrayBuilder);
        arrayBuilder.dispose();
    }
}
