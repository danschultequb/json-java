package qub;

public class JSONTokenizer implements Iterator<JSONToken>
{
    private final Lexer lexer;
    private final int firstTokenStartIndex;
    private final Action1<Issue> onIssue;
    private boolean hasStarted;
    private JSONToken current;

    public JSONTokenizer(String text)
    {
        this(text, 0);
    }

    public JSONTokenizer(String text, int firstTokenStartIndex)
    {
        this(text, firstTokenStartIndex, (Action1<Issue>)null);
    }

    public JSONTokenizer(String text, List<Issue> issues)
    {
        this(text, 0, issues);
    }

    public JSONTokenizer(String text, int firstTokenStartIndex, final List<Issue> issues)
    {
        this(text, firstTokenStartIndex, getAddToListAction(issues));
    }

    public JSONTokenizer(String text, int firstTokenStartIndex, Action1<Issue> onIssue)
    {
        this(new StringIterator(text), firstTokenStartIndex, onIssue);
    }

    public JSONTokenizer(Iterator<Character> characters, List<Issue> issues)
    {
        this(characters, 0, getAddToListAction(issues));
    }

    public JSONTokenizer(Iterator<Character> characters, int firstTokenStartIndex, Action1<Issue> onIssue)
    {
        this(new Lexer(characters), firstTokenStartIndex, onIssue);
    }

    public JSONTokenizer(Lexer lexer, int firstTokenStartIndex, Action1<Issue> onIssue)
    {
        this.lexer = lexer;
        this.firstTokenStartIndex = firstTokenStartIndex;
        this.onIssue = onIssue;
    }

    private void addIssue(Issue issue)
    {
        if (onIssue != null)
        {
            onIssue.run(issue);
        }
    }

    @Override
    public boolean hasStarted()
    {
        return hasStarted;
    }

    @Override
    public boolean hasCurrent()
    {
        return current != null;
    }

    @Override
    public JSONToken getCurrent()
    {
        return current;
    }

    @Override
    public boolean next()
    {
        if (!hasStarted)
        {
            hasStarted = true;

            if (!lexer.hasStarted())
            {
                lexer.next();
            }
        }

        if (lexer.hasCurrent())
        {
            int tokenStartIndex = hasCurrent() ? getCurrent().getAfterEndIndex() : firstTokenStartIndex;
            switch (lexer.getCurrent().getType())
            {
                case LeftCurlyBracket:
                    current = JSONToken.leftCurlyBracket(tokenStartIndex);
                    lexer.next();
                    break;

                case RightCurlyBracket:
                    current = JSONToken.rightCurlyBracket(tokenStartIndex);
                    lexer.next();
                    break;

                case LeftSquareBracket:
                    current = JSONToken.leftSquareBracket(tokenStartIndex);
                    lexer.next();
                    break;

                case RightSquareBracket:
                    current = JSONToken.rightSquareBracket(tokenStartIndex);
                    lexer.next();
                    break;

                case Colon:
                    current = JSONToken.colon(tokenStartIndex);
                    lexer.next();
                    break;

                case Comma:
                    current = JSONToken.comma(tokenStartIndex);
                    lexer.next();
                    break;

                case Letters:
                    switch (lexer.getCurrent().toString().toLowerCase())
                    {
                        case "true":
                            if (!lexer.getCurrent().toString().equals("true"))
                            {
                                addIssue(JSONIssues.shouldBeLowercased(tokenStartIndex, 4));
                            }
                            current = JSONToken.booleanToken(lexer.getCurrent().toString(), tokenStartIndex);
                            break;

                        case "false":
                            if (!lexer.getCurrent().toString().equals("false"))
                            {
                                addIssue(JSONIssues.shouldBeLowercased(tokenStartIndex, 5));
                            }
                            current = JSONToken.booleanToken(lexer.getCurrent().toString(), tokenStartIndex);
                            break;

                        case "null":
                            if (!lexer.getCurrent().toString().equals("null"))
                            {
                                addIssue(JSONIssues.shouldBeLowercased(tokenStartIndex, 4));
                            }
                            current = JSONToken.nullToken(lexer.getCurrent().toString(), tokenStartIndex);
                            break;

                        default:
                            current = JSONToken.unrecognized(lexer.getCurrent().toString(), tokenStartIndex);
                            break;
                    }
                    lexer.next();
                    break;

                case SingleQuote:
                case DoubleQuote:
                    final Lex startQuote = lexer.takeCurrent();
                    final StringBuilder quotedStringText = new StringBuilder(startQuote.toString());

                    boolean foundEndQuote = false;
                    boolean escaped = false;

                    while (!foundEndQuote && lexer.hasCurrent())
                    {
                        quotedStringText.append(lexer.getCurrent().toString());
                        if (escaped)
                        {
                            escaped = false;
                        }
                        else if (lexer.getCurrent().getType() == LexType.Backslash)
                        {
                            escaped = true;
                        }
                        else if (lexer.getCurrent().getType() == startQuote.getType())
                        {
                            foundEndQuote = true;
                        }
                        lexer.next();
                    }

                    if (!foundEndQuote) {
                        addIssue(JSONIssues.missingEndQuote(startQuote.toString(), tokenStartIndex, quotedStringText.length()));
                    }

                    current = JSONToken.quotedString(quotedStringText.toString(), tokenStartIndex, foundEndQuote);
                    break;

                case Space:
                case Tab:
                case CarriageReturn:
                    final StringBuilder whitespaceText = new StringBuilder(lexer.takeCurrent().toString());
                    while (lexer.hasCurrent() && lexer.getCurrent().isWhitespace())
                    {
                        whitespaceText.append(lexer.takeCurrent().toString());
                    }
                    current = JSONToken.whitespace(whitespaceText.toString(), tokenStartIndex);
                    break;

                case NewLine:
                case CarriageReturnNewLine:
                    current = JSONToken.newLine(lexer.takeCurrent().toString(), tokenStartIndex);
                    break;

                case Dash:
                case Digits:
                case Period:
                    final StringBuilder numberText = new StringBuilder();

                    if (lexer.getCurrent().getType() == LexType.Dash)
                    {
                        // Negative sign
                        numberText.append(lexer.takeCurrent().toString());
                    }

                    if (!lexer.hasCurrent())
                    {
                        addIssue(JSONIssues.missingWholeNumberDigits(tokenStartIndex, numberText.length()));
                    }
                    else if (lexer.getCurrent().getType() != LexType.Digits)
                    {
                        addIssue(JSONIssues.expectedWholeNumberDigits(tokenStartIndex + numberText.length(), lexer.getCurrent().getLength()));
                    }
                    else
                    {
                        numberText.append(lexer.takeCurrent().toString());
                    }

                    if (lexer.hasCurrent() && lexer.getCurrent().getType() == LexType.Period)
                    {
                        // Decimal point
                        final int decimalPointStartIndex = tokenStartIndex + numberText.length();
                        numberText.append(lexer.takeCurrent().toString());

                        if (!lexer.hasCurrent())
                        {
                            addIssue(JSONIssues.missingFractionalNumberDigits(decimalPointStartIndex, 1));
                        }
                        else if (lexer.getCurrent().getType() != LexType.Digits)
                        {
                            addIssue(JSONIssues.expectedFractionalNumberDigits(lexer.getCurrent().getStartIndex(), lexer.getCurrent().getLength()));
                        }
                        else
                        {
                            // Fractional number digits
                            numberText.append(lexer.takeCurrent().toString());
                        }
                    }

                    if (lexer.hasCurrent() && lexer.getCurrent().toString().equalsIgnoreCase("e"))
                    {
                        // e
                        final int eStartIndex = tokenStartIndex + numberText.length();
                        if (lexer.getCurrent().toString().equals("E"))
                        {
                            addIssue(JSONIssues.shouldBeLowercased(eStartIndex, 1));
                        }
                        numberText.append(lexer.takeCurrent().toString());

                        if (!lexer.hasCurrent())
                        {
                            addIssue(JSONIssues.missingExponentNumberDigits(eStartIndex, 1));
                        }
                        else
                        {
                            final int exponentSignOrDigitsStartIndex = tokenStartIndex + numberText.length();
                            if (lexer.getCurrent().getType() == LexType.Dash || lexer.getCurrent().getType() == LexType.Plus)
                            {
                                // Exponent number sign
                                numberText.append(lexer.takeCurrent().toString());
                            }

                            if (!lexer.hasCurrent())
                            {
                                addIssue(JSONIssues.missingExponentNumberDigits(exponentSignOrDigitsStartIndex, 1));
                            }
                            else if (lexer.getCurrent().getType() != LexType.Digits)
                            {
                                addIssue(JSONIssues.expectedExponentNumberDigits(tokenStartIndex + numberText.length(), lexer.getCurrent().getLength()));
                            }
                            else
                            {
                                // Exponent number digits
                                numberText.append(lexer.takeCurrent().toString());
                            }
                        }
                    }

                    current = JSONToken.number(numberText.toString(), tokenStartIndex);
                    break;

                case ForwardSlash:
                    final StringBuilder commentText = new StringBuilder(lexer.takeCurrent().toString());
                    if (!lexer.hasCurrent())
                    {
                        addIssue(JSONIssues.missingCommentSlashOrAsterisk(tokenStartIndex, 1));
                        current = JSONToken.unrecognized(commentText.toString(), tokenStartIndex);
                    }
                    else
                    {
                        switch (lexer.getCurrent().getType())
                        {
                            case ForwardSlash:
                                do
                                {
                                    commentText.append(lexer.takeCurrent().toString());
                                }
                                while (lexer.hasCurrent() && !lexer.getCurrent().isNewLine());
                                current = JSONToken.lineComment(commentText.toString(), tokenStartIndex);
                                break;

                            case Asterisk:
                                commentText.append(lexer.takeCurrent().toString());
                                boolean previousCharacterWasAsterisk = false;
                                boolean commentClosed = false;
                                while (lexer.hasCurrent() && !commentClosed)
                                {
                                    switch (lexer.getCurrent().getType())
                                    {
                                        case Asterisk:
                                            previousCharacterWasAsterisk = true;
                                            break;

                                        case ForwardSlash:
                                            commentClosed = previousCharacterWasAsterisk;
                                            break;

                                        default:
                                            previousCharacterWasAsterisk = false;
                                            break;
                                    }
                                    commentText.append(lexer.takeCurrent().toString());
                                }
                                current = JSONToken.blockComment(commentText.toString(), tokenStartIndex, commentClosed);
                                break;

                            default:
                                addIssue(JSONIssues.expectedCommentSlashOrAsterisk(lexer.getCurrent().getStartIndex(), lexer.getCurrent().getLength()));
                                current = JSONToken.unrecognized(commentText.toString(), tokenStartIndex);
                                break;
                        }
                    }

                    break;

                default:
                    current = JSONToken.unrecognized(lexer.takeCurrent().toString(), tokenStartIndex);
                    break;
            }
        }
        else
        {
            current = null;
        }

        return hasCurrent();
    }

    private static Action1<Issue> getAddToListAction(final List<Issue> issues)
    {
        return issues == null ? null : issues::add;
    }
}