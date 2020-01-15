package qub;

/**
 * A collection of functions for interacting with JSON content.
 */
public interface JSON
{
    /**
     * The default indentation String when formatting a JSON document.
     */
    String defaultSingleIndent = "  ";

    /**
     * Parse a JSONSegment from the provided text.
     * @param text The text to parse into a JSONSegment.
     * @return The parsed JSONSegment.
     */
    static Result<JSONSegment> parse(String text)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        return JSON.parse(Strings.iterable(text));
    }

    /**
     * Parse a JSONSegment from the provided characters.
     * @param characters The characters to parse into a JSONSegment.
     * @return The parsed JSONSegment.
     */
    static Result<JSONSegment> parse(Iterable<Character> characters)
    {
        PreCondition.assertNotNullAndNotEmpty(characters, "characters");

        return JSON.parse(characters.iterate());
    }

    /**
     * Parse a JSONSegment from the provided characters.
     * @param characters The characters to parse into a JSONSegment.
     * @return The parsed JSONSegment.
     */
    static Result<JSONSegment> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return JSON.parse(JSON.createTokenizer(characters));
    }

    /**
     * Parse a JSONSegment from the provided JSONTokenizer.
     * @param tokenizer The tokenizer that produces JSONTokens.
     * @return The parsed JSONSegment.
     */
    static Result<JSONSegment> parse(JSONTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");

        return Result.create(() ->
        {
            JSON.ensureHasStarted(tokenizer);

            if (!tokenizer.hasCurrent())
            {
                throw new ParseException("No JSON tokens found.");
            }

            JSONSegment result;
            switch (tokenizer.getCurrent().getType())
            {
                case LeftCurlyBracket:
                    result = JSON.parseObject(tokenizer).await();
                    break;

                case LeftSquareBracket:
                    result = JSON.parseArray(tokenizer).await();
                    break;

                case Boolean:
                    result = JSONBoolean.get(JSONToken.falseToken != JSON.takeCurrent(tokenizer));
                    break;

                case Null:
                    result = JSONNull.segment;
                    JSON.next(tokenizer);
                    break;

                case Number:
                    result = JSONNumber.get(JSON.takeCurrent(tokenizer).getText());
                    break;

                case QuotedString:
                    result = JSONString.getFromQuoted(JSON.takeCurrent(tokenizer).getText());
                    break;

                default:
                    throw new ParseException("Unexpected JSON token: " + tokenizer.getCurrent());
            }

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    /**
     * Parse a JSONObject from the provided text.
     * @param text The text to parse into a JSONObject.
     * @return The parsed JSONObject.
     */
    static Result<JSONObject> parseObject(String text)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        return JSON.parseObject(Strings.iterable(text));
    }

    /**
     * Parse a JSONObject from the provided characters.
     * @param characters The characters to parse into a JSONObject.
     * @return The parsed JSONObject.
     */
    static Result<JSONObject> parseObject(Iterable<Character> characters)
    {
        PreCondition.assertNotNullAndNotEmpty(characters, "characters");

        return JSON.parseObject(characters.iterate());
    }

    /**
     * Parse a JSONObject from the provided characters.
     * @param characters The characters to parse into a JSONObject.
     * @return The parsed JSONObject.
     */
    static Result<JSONObject> parseObject(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return JSON.parseObject(JSON.createTokenizer(characters));
    }

    /**
     * Parse a JSONObject from the provided JSONTokenizer.
     * @param tokenizer The tokenizer that produces JSONTokens.
     * @return The parsed JSONObject.
     */
    static Result<JSONObject> parseObject(JSONTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");
        PreCondition.assertTrue(tokenizer.hasCurrent(), "tokenizer.hasCurrent()");
        PreCondition.assertEqual(JSONToken.leftCurlyBracket, tokenizer.getCurrent(), "tokenizer.getCurrent()");

        return Result.create(() ->
        {
            final JSONToken leftCurlyBracket = JSON.takeCurrent(tokenizer);

            final List<JSONObjectProperty> properties = List.create();
            JSONToken rightCurlyBracket = null;
            boolean expectProperty = true;
            while (tokenizer.hasCurrent() && rightCurlyBracket == null)
            {
                switch (tokenizer.getCurrent().getType())
                {
                    case QuotedString:
                        if (!expectProperty)
                        {
                            throw new ParseException("Expected object property separator (',') or right curly bracket ('}').");
                        }
                        properties.add(JSON.parseObjectProperty(tokenizer).await());
                        expectProperty = false;
                        break;

                    case Comma:
                        if (expectProperty)
                        {
                            if (properties.any())
                            {
                                throw new ParseException("Expected quoted-string object property name.");
                            }
                            else
                            {
                                throw new ParseException("Expected quoted-string object property name or right curly bracket ('}').");
                            }
                        }
                        JSON.next(tokenizer);
                        expectProperty = true;
                        break;

                    case RightCurlyBracket:
                        if (properties.any() && expectProperty)
                        {
                            throw new ParseException("Expected quoted-string object property name.");
                        }
                        rightCurlyBracket = JSON.takeCurrent(tokenizer);
                        break;

                    default:
                        if (properties.any())
                        {
                            if (expectProperty)
                            {
                                throw new ParseException("Expected quoted-string object property name.");
                            }
                            else
                            {
                                throw new ParseException("Expected object property separator (',') or right curly bracket ('}').");
                            }
                        }
                        else
                        {
                            throw new ParseException("Expected quoted-string object property name or right curly bracket ('}').");
                        }
                }
            }

            if (properties.any() && expectProperty)
            {
                throw new ParseException("Missing object property.");
            }
            else if (rightCurlyBracket == null)
            {
                throw new ParseException("Missing object right curly bracket ('}').");
            }

            return JSONObject.create(properties);
        });
    }

    /**
     * Parse a JSONObjectProperty from the provided text.
     * @param text The text to parse into a JSONObjectProperty.
     * @return The parsed JSONObjectProperty.
     */
    static Result<JSONObjectProperty> parseObjectProperty(String text)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        return JSON.parseObjectProperty(Strings.iterable(text));
    }

    /**
     * Parse a JSONObjectProperty from the provided characters.
     * @param characters The characters to parse into a JSONObjectProperty.
     * @return The parsed JSONObjectProperty.
     */
    static Result<JSONObjectProperty> parseObjectProperty(Iterable<Character> characters)
    {
        PreCondition.assertNotNullAndNotEmpty(characters, "characters");

        return JSON.parseObjectProperty(characters.iterate());
    }

    /**
     * Parse a JSONObjectProperty from the provided characters.
     * @param characters The characters to parse into a JSONObjectProperty.
     * @return The parsed JSONObjectProperty.
     */
    static Result<JSONObjectProperty> parseObjectProperty(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return JSON.parseObjectProperty(JSON.createTokenizer(characters));
    }

    /**
     * Parse a JSONObjectProperty from the provided JSONTokenizer.
     * @param tokenizer The tokenizer that produces JSONTokens.
     * @return The parsed JSONObjectProperty.
     */
    static Result<JSONObjectProperty> parseObjectProperty(JSONTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");
        PreCondition.assertTrue(tokenizer.hasCurrent(), "tokenizer.hasCurrent()");
        PreCondition.assertEqual(JSONTokenType.QuotedString, tokenizer.getCurrent().getType(), "tokenizer.getCurrent().getType()");

        return Result.create(() ->
        {
            final String quotedPropertyName = JSON.takeCurrent(tokenizer).getText();
            final String propertyName = quotedPropertyName.substring(1, quotedPropertyName.length() - 1);

            if (!tokenizer.hasCurrent())
            {
                throw new ParseException("Missing object property name and value separator (':').");
            }
            else if (tokenizer.getCurrent().getType() != JSONTokenType.Colon)
            {
                throw new ParseException("Expected object property name and value separator (':').");
            }

            JSONSegment propertyValue;
            if (!JSON.next(tokenizer))
            {
                throw new ParseException("Missing object property value.");
            }
            else
            {
                switch (tokenizer.getCurrent().getType())
                {
                    case Comma:
                        throw new ParseException("Expected object property value.");

                    case Boolean:
                    case Null:
                    case Number:
                    case QuotedString:
                    case LeftCurlyBracket:
                    case LeftSquareBracket:
                        propertyValue = JSON.parse(tokenizer).await();
                        break;

                    default:
                        throw new ParseException("Unexpected object property value token: " + Strings.escapeAndQuote(tokenizer.getCurrent()));
                }
            }

            return JSONObjectProperty.create(propertyName, propertyValue);
        });
    }

    /**
     * Parse a JSONArray from the provided text.
     * @param text The text to parse into a JSONArray.
     * @return The parsed JSONArray.
     */
    static Result<JSONArray> parseArray(String text)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        return JSON.parseArray(Strings.iterable(text));
    }

    /**
     * Parse a JSONArray from the provided characters.
     * @param characters The characters to parse into a JSONArray.
     * @return The parsed JSONArray.
     */
    static Result<JSONArray> parseArray(Iterable<Character> characters)
    {
        PreCondition.assertNotNullAndNotEmpty(characters, "characters");

        return JSON.parseArray(characters.iterate());
    }

    /**
     * Parse a JSONArray from the provided characters.
     * @param characters The characters to parse into a JSONArray.
     * @return The parsed JSONArray.
     */
    static Result<JSONArray> parseArray(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return JSON.parseArray(JSON.createTokenizer(characters));
    }

    /**
     * Parse a JSONArray from the provided JSONTokenizer.
     * @param tokenizer The tokenizer that produces JSONTokens.
     * @return The parsed JSONArray.
     */
    static Result<JSONArray> parseArray(JSONTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");
        PreCondition.assertTrue(tokenizer.hasCurrent(), "tokenizer.hasCurrent()");
        PreCondition.assertEqual(JSONToken.leftSquareBracket, tokenizer.getCurrent(), "tokenizer.getCurrent()");

        return Result.create(() ->
        {
            final JSONToken leftSquareBracket = JSON.takeCurrent(tokenizer);

            final List<JSONSegment> elements = List.create();

            JSONToken rightSquareBracket = null;
            boolean expectElement = true;
            while (tokenizer.hasCurrent() && rightSquareBracket == null)
            {
                switch (tokenizer.getCurrent().getType())
                {
                    case Boolean:
                    case Null:
                    case Number:
                    case QuotedString:
                    case LeftCurlyBracket:
                    case LeftSquareBracket:
                        if (!expectElement)
                        {
                            throw new ParseException("Expected array element separator (',') or right square bracket (']').");
                        }
                        elements.add(JSON.parse(tokenizer).await());
                        expectElement = false;
                        break;

                    case Comma:
                        if (expectElement)
                        {
                            throw new ParseException("Expected array element.");
                        }
                        JSON.next(tokenizer);
                        expectElement = true;
                        break;

                    case RightSquareBracket:
                        if (elements.any() && expectElement)
                        {
                            throw new ParseException("Expected array element.");
                        }
                        rightSquareBracket = JSON.takeCurrent(tokenizer);
                        expectElement = false;
                        break;

                    default:
                        throw new ParseException("Unexpected array element token: " + Strings.escapeAndQuote(tokenizer.getCurrent()));
                }
            }

            if (elements.any() && expectElement)
            {
                throw new ParseException("Missing array element.");
            }
            else if (rightSquareBracket == null)
            {
                throw new ParseException("Missing array right square bracket (']').");
            }

            return JSONArray.create(elements);
        });
    }

    static JSONTokenizer createTokenizer(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        final JSONTokenizer result = JSONTokenizer.create(characters);
        JSON.next(result);

        PostCondition.assertNotNull(result, "result");
        PostCondition.assertTrue(result.hasStarted(), "result.hasStarted()");

        return result;
    }

    static boolean shouldSkipCurrentToken(JSONTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");
        PreCondition.assertTrue(tokenizer.hasCurrent(), "tokenizer.hasCurrent()");

        boolean result;
        switch (tokenizer.getCurrent().getType())
        {
            case BlockComment:
            case LineComment:
            case NewLine:
            case Whitespace:
                result = true;
                break;

            default:
                result = false;
                break;
        }

        return result;
    }

    static boolean next(JSONTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");

        boolean result = tokenizer.next();
        while (result && JSON.shouldSkipCurrentToken(tokenizer))
        {
            result = tokenizer.next();
        }

        return result;
    }

    static void ensureHasStarted(JSONTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");

        if (!tokenizer.hasStarted() || (tokenizer.hasCurrent() && JSON.shouldSkipCurrentToken(tokenizer)))
        {
            JSON.next(tokenizer);
        }

        PostCondition.assertTrue(tokenizer.hasStarted(), "tokenizer.hasStarted()");
    }

    static JSONToken takeCurrent(JSONTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");
        PreCondition.assertTrue(tokenizer.hasCurrent(), "tokenizer.hasCurrent()");

        final JSONToken result = tokenizer.getCurrent();
        JSON.next(tokenizer);

        return result;
    }
}
