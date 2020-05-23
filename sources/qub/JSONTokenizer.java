package qub;

/**
 * An object that converts a stream of characters into a stream of JSONTokens.
 */
public class JSONTokenizer implements Iterator<JSONToken>
{
    private static final Iterable<JSONToken> literalTokens = Iterable.create(JSONToken.nullToken, JSONToken.falseToken, JSONToken.trueToken);
    private final Iterator<Character> characters;
    private final CharacterList builder;
    private boolean hasStarted;
    private JSONToken current;

    /**
     * Create a new JSONTokenizer from the provided characters.
     * @param characters The characters to convert to JSONTokens.
     */
    private JSONTokenizer(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        this.characters = characters;
        this.builder = CharacterList.create();
    }

    /**
     * Create a new JSONTokenizer from the provided text.
     * @param text The text to convert to JSONTokens.
     * @return The new JSONTokenizer.
     */
    public static JSONTokenizer create(String text)
    {
        PreCondition.assertNotNull(text, "text");

        return JSONTokenizer.create(Strings.iterable(text));
    }

    /**
     * Create a new JSONTokenizer from the provided characters.
     * @param characters The characters to convert to JSONTokens.
     * @return The new JSONTokenizer.
     */
    public static JSONTokenizer create(Iterable<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return JSONTokenizer.create(characters.iterate());
    }

    /**
     * Create a new JSONTokenizer from the provided characters.
     * @param characters The characters to convert to JSONTokens.
     * @return The new JSONTokenizer.
     */
    public static JSONTokenizer create(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return new JSONTokenizer(characters);
    }

    @Override
    public boolean hasStarted()
    {
        return this.hasStarted;
    }

    @Override
    public boolean hasCurrent()
    {
        return this.current != null;
    }

    @Override
    public JSONToken getCurrent()
    {
        PreCondition.assertTrue(this.hasCurrent(), "this.hasCurrent()");

        return this.current;
    }

    @Override
    public boolean next()
    {
        this.characters.ensureHasStarted();
        this.hasStarted = true;

        if (!this.characters.hasCurrent())
        {
            this.current = null;
        }
        else
        {
            switch (this.characters.getCurrent())
            {
                case '{':
                    this.current = JSONToken.leftCurlyBracket;
                    this.characters.next();
                    break;

                case '}':
                    this.current = JSONToken.rightCurlyBracket;
                    this.characters.next();
                    break;

                case '[':
                    this.current = JSONToken.leftSquareBracket;
                    this.characters.next();
                    break;

                case ']':
                    this.current = JSONToken.rightSquareBracket;
                    this.characters.next();
                    break;

                case ':':
                    this.current = JSONToken.colon;
                    this.characters.next();
                    break;

                case ',':
                    this.current = JSONToken.comma;
                    this.characters.next();
                    break;

                case '\n':
                    this.current = JSONToken.newLine;
                    this.characters.next();
                    break;

                case '\r':
                    if (this.characters.next() && this.characters.getCurrent() == '\n')
                    {
                        this.current = JSONToken.carriageReturnNewLine;
                        this.characters.next();
                    }
                    else
                    {
                        this.current = JSONToken.carriageReturn;
                    }
                    break;

                case '\'':
                case '\"':
                    this.current = JSONToken.quotedString(this.readQuotedString());
                    break;

                case ' ':
                case '\t':
                    this.current = JSONToken.whitespace(this.readWhitespace());
                    break;

                case '/':
                    this.current = this.readCommentToken();
                    break;

                default:
                    if (JSONTokenizer.isLetter(this.characters.getCurrent()))
                    {
                        final String tokenText = this.readLiteral();
                        this.current = JSONTokenizer.literalTokens.first((JSONToken token) -> token.getText().equals(tokenText));
                        if (this.current == null)
                        {
                            throw new ParseException("Unrecognized JSONToken literal: " + tokenText);
                        }
                    }
                    else if (this.characters.getCurrent() == '-' || JSONTokenizer.isDigit(this.characters.getCurrent()))
                    {
                        final String tokenText = this.readNumber();
                        this.current = JSONToken.number(tokenText);
                    }
                    else
                    {
                        throw new ParseException("Unrecognized JSONToken start character: " + Strings.escapeAndQuote(this.characters.getCurrent()));
                    }
            }
        }

        return this.hasCurrent();
    }

    /**
     * Get whether or not the provided character is a recognized JSON letter.
     * @param character The character to check.
     * @return Whether or not the provided character is a recognized JSON letter.
     */
    public static boolean isLetter(char character)
    {
        return ('a' <= character && character <= 'z') ||
            ('A' <= character && character <= 'Z');
    }

    /**
     * Get whether or not the provided character is a recognized JSON digit.
     * @param character The character to check.
     * @return Whether or not the provided character is a recognized JSON digit.
     */
    public static boolean isDigit(char character)
    {
        return '0' <= character && character <= '9';
    }

    /**
     * Get whether or not the provided character is a recognized JSON whitespace character.
     * @param character The character to check.
     * @return Whether or not the provided character is a recognized JSON whitespace character.
     */
    public static boolean isWhitespace(char character)
    {
        return ' ' == character || '\t' == character;
    }

    private String readLiteral()
    {
        PreCondition.assertTrue(this.characters.hasCurrent(), "characters.hasCurrent()");
        PreCondition.assertTrue(JSONTokenizer.isLetter(this.characters.getCurrent()), "JSONTokenizer.isLetter(this.characters.getCurrent())");

        this.builder.add(this.characters.takeCurrent().charValue());
        while (this.characters.hasCurrent() && JSONTokenizer.isLetter(this.characters.getCurrent()))
        {
            this.builder.add(this.characters.takeCurrent().charValue());
        }

        final String result = this.builder.toString(true);
        this.builder.clear();

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    private String readQuotedString()
    {
        PreCondition.assertTrue(this.characters.hasCurrent(), "this.characters.hasCurrent()");
        PreCondition.assertOneOf(this.characters.getCurrent(), Iterable.create('\'', '\"'), "this.characters.getCurrent()");

        final char startQuote = this.characters.takeCurrent();
        this.builder.add(startQuote);
        boolean escaped = false;
        boolean foundCloseQuote = false;
        while (this.characters.hasCurrent())
        {
            final char currentCharacter = this.characters.takeCurrent();
            this.builder.add(currentCharacter);
            if (escaped)
            {
                escaped = false;
            }
            else if (currentCharacter == '\\')
            {
                escaped = true;
            }
            else if (currentCharacter == startQuote)
            {
                foundCloseQuote = true;
                break;
            }
        }

        if (!foundCloseQuote)
        {
            throw new ParseException("Missing quoted-string closing quote: " + startQuote);
        }

        final String result = this.builder.toString(true);
        this.builder.clear();

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    private String readNumber()
    {
        PreCondition.assertTrue(this.characters.hasCurrent(), "this.characters.hasCurrent()");
        PreCondition.assertOneOf(this.characters.getCurrent(), Iterable.create('-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'), "this.characters.getCurrent()");

        if (this.characters.getCurrent() == '-')
        {
            this.builder.add(this.characters.takeCurrent().charValue());

            if (!this.characters.hasCurrent() || !JSONTokenizer.isDigit(this.characters.getCurrent()))
            {
                throw new ParseException("Missing digits after number's negative sign: \"-\"");
            }
        }

        while (this.characters.hasCurrent() && JSONTokenizer.isDigit(this.characters.getCurrent()))
        {
            this.builder.add(this.characters.takeCurrent().charValue());
        }

        if (this.characters.hasCurrent() && this.characters.getCurrent() == '.')
        {
            this.builder.add(this.characters.takeCurrent().charValue());
            if (!this.characters.hasCurrent() || !JSONTokenizer.isDigit(this.characters.getCurrent()))
            {
                throw new ParseException("Missing digits after number's decimal point: " + Strings.escapeAndQuote(this.builder.toString(true)));
            }

            do
            {
                this.builder.add(this.characters.takeCurrent().charValue());
            }
            while (this.characters.hasCurrent() && JSONTokenizer.isDigit(this.characters.getCurrent()));
        }

        if (this.characters.hasCurrent() && (this.characters.getCurrent().equals('e') || this.characters.getCurrent().equals('E')))
        {
            this.builder.add(this.characters.takeCurrent().charValue());

            if (!this.characters.hasCurrent() || !(this.characters.getCurrent().equals('-') || this.characters.getCurrent().equals('+') || JSONTokenizer.isDigit(this.characters.getCurrent())))
            {
                throw new ParseException("Missing digits after number's exponent character: " + Strings.escapeAndQuote(this.builder.toString(true)));
            }
            else
            {
                this.builder.add(this.characters.takeCurrent().charValue());
                if ((this.builder.endsWith('-') || this.builder.endsWith('+')) &&
                    (!this.characters.hasCurrent() || !JSONTokenizer.isDigit(this.characters.getCurrent())))
                {
                    throw new ParseException("Missing digits after number's exponent sign character: " + Strings.escapeAndQuote(this.builder.toString(true)));
                }

                while (this.characters.hasCurrent() && JSONTokenizer.isDigit(this.characters.getCurrent()))
                {
                    this.builder.add(this.characters.takeCurrent().charValue());
                }
            }
        }

        final String result = builder.toString(true);
        builder.clear();

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    private String readWhitespace()
    {
        PreCondition.assertTrue(this.characters.hasCurrent(), "this.characters.hasCurrent()");
        PreCondition.assertTrue(JSONTokenizer.isWhitespace(this.characters.getCurrent()), "JSONTokenizer.isWhitespace(this.characters.getCurrent())");

        do
        {
            builder.add(this.characters.takeCurrent().charValue());
        }
        while (this.characters.hasCurrent() && JSONTokenizer.isWhitespace(this.characters.getCurrent()));

        final String result = builder.toString(true);
        builder.clear();

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    private JSONToken readCommentToken()
    {
        PreCondition.assertTrue(this.characters.hasCurrent(), "this.characters.hasCurrent()");
        PreCondition.assertEqual('/', this.characters.getCurrent(), "this.characters.getCurrent()");

        if (!this.characters.next())
        {
            throw new ParseException("Missing comment start sequence second character.");
        }

        JSONTokenType resultType;
        this.builder.add('/');
        if (this.characters.getCurrent() == '/')
        {
            resultType = JSONTokenType.LineComment;
            this.builder.add(this.characters.takeCurrent().charValue());
            while (this.characters.hasCurrent() && this.characters.getCurrent() != '\r' && this.characters.getCurrent() != '\n')
            {
                this.builder.add(this.characters.takeCurrent().charValue());
            }
        }
        else if (this.characters.getCurrent() == '*')
        {
            resultType = JSONTokenType.BlockComment;
            this.builder.add(this.characters.takeCurrent().charValue());
            boolean endSequenceStarted = false;
            boolean ended = false;
            while (this.characters.hasCurrent())
            {
                final char character = this.characters.takeCurrent();
                this.builder.add(character);
                if (endSequenceStarted && character == '/')
                {
                    ended = true;
                    break;
                }
                endSequenceStarted = (character == '*');
            }

            if (!ended)
            {
                throw new ParseException(endSequenceStarted
                    ? "Missing block comment end sequence second character (\"/\")."
                    : "Missing block comment end sequence (\"*/\").");
            }
        }
        else
        {
            throw new ParseException("Unrecognized comment start sequence second character: " + Strings.escapeAndQuote(this.characters.getCurrent()));
        }

        final String resultText = this.builder.toString(true);
        this.builder.clear();

        final JSONToken result = new JSONToken(resultText, resultType);

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
