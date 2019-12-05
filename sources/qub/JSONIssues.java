package qub;

public class JSONIssues
{
    public static Issue missingEndQuote(String quote, int startIndex, int length)
    {
        return Issue.error("Missing end quote (" + quote + ").", startIndex, length);
    }

    public static Issue missingWholeNumberDigits(int startIndex, int length)
    {
        return Issue.error("Missing whole number digits.", startIndex, length);
    }

    public static Issue expectedWholeNumberDigits(int startIndex, int length)
    {
        return Issue.error("Expected whole number digits.", startIndex, length);
    }

    public static Issue missingFractionalNumberDigits(int startIndex, int length)
    {
        return Issue.error("Missing fractional number digits.", startIndex, length);
    }

    public static Issue expectedFractionalNumberDigits(int startIndex, int length)
    {
        return Issue.error("Expected fractional number digits.", startIndex, length);
    }

    public static Issue shouldBeLowercased(int startIndex, int length)
    {
        return Issue.error("Should be lowercased.", startIndex, length);
    }

    public static Issue missingExponentNumberDigits(int startIndex, int length)
    {
        return Issue.error("Missing exponent number digits.", startIndex, length);
    }

    public static Issue expectedExponentNumberDigits(int startIndex, int length)
    {
        return Issue.error("Expected exponent number digits.", startIndex, length);
    }

    public static Issue missingCommentSlashOrAsterisk(int startIndex, int length)
    {
        return Issue.error("Missing comment forward slash ('/') or asterisk ('*').", startIndex, length);
    }

    public static Issue expectedCommentSlashOrAsterisk(int startIndex, int length)
    {
        return Issue.error("Expected comment forward slash ('/') or asterisk ('*').", startIndex, length);
    }

    public static Issue expectedEndOfFile(int startIndex, int length)
    {
        return expectedEndOfFile(new Span(startIndex, length));
    }

    public static Issue expectedEndOfFile(Span span)
    {
        return Issue.error("Expected end of file.", span);
    }

    public static Issue missingClosingRightSquareBracket(int startIndex, int length)
    {
        return missingClosingRightSquareBracket(new Span(startIndex, length));
    }

    public static Issue missingClosingRightSquareBracket(Span span)
    {
        return Issue.error("Missing closing right square bracket (']').", span);
    }

    public static Issue expectedCommaOrClosingRightSquareBracket(int startIndex, int length)
    {
        return expectedCommaOrClosingRightSquareBracket(new Span(startIndex, length));
    }

    public static Issue expectedCommaOrClosingRightSquareBracket(Span span)
    {
        return Issue.error("Expected comma (',') or closing right square bracket (']').", span);
    }

    public static Issue expectedArrayElementOrClosingRightSquareBracket(int startIndex, int length)
    {
        return expectedArrayElementOrClosingRightSquareBracket(new Span(startIndex, length));
    }

    public static Issue expectedArrayElementOrClosingRightSquareBracket(Span span)
    {
        return Issue.error("Expected array element or closing right square bracket (']').", span);
    }

    public static Issue expectedArrayElement(int startIndex, int length)
    {
        return expectedArrayElement(new Span(startIndex, length));
    }

    public static Issue expectedArrayElement(Span span)
    {
        return Issue.error("Expected array element.", span);
    }

    public static Issue expectedCommaOrClosingRightCurlyBracket(int startIndex, int length)
    {
        return expectedCommaOrClosingRightCurlyBracket(new Span(startIndex, length));
    }

    public static Issue expectedCommaOrClosingRightCurlyBracket(Span span)
    {
        return Issue.error("Expected comma (',') or closing right curly bracket ('}').", span);
    }

    public static Issue expectedPropertyName(int startIndex, int length)
    {
        return expectedPropertyName(new Span(startIndex, length));
    }

    public static Issue expectedPropertyName(Span span)
    {
        return Issue.error("Expected property name.", span);
    }

    public static Issue expectedPropertyNameOrClosingRightCurlyBracket(int startIndex, int length)
    {
        return expectedPropertyNameOrClosingRightCurlyBracket(new Span(startIndex, length));
    }

    public static Issue expectedPropertyNameOrClosingRightCurlyBracket(Span span)
    {
        return Issue.error("Expected property name or closing right curly bracket ('}').", span);
    }

    public static Issue missingClosingRightCurlyBracket(int startIndex, int length)
    {
        return missingClosingRightCurlyBracket(new Span(startIndex, length));
    }

    public static Issue missingClosingRightCurlyBracket(Span span)
    {
        return Issue.error("Missing closing right curly bracket ('}').", span);
    }

    public static Issue missingColon(Span span)
    {
        return Issue.error("Missing colon (':').", span);
    }

    public static Issue expectedColon(int startIndex, int length)
    {
        return expectedColon(new Span(startIndex, length));
    }

    public static Issue expectedColon(Span span)
    {
        return Issue.error("Expected colon (':').", span);
    }

    public static Issue missingPropertyValue(Span span)
    {
        return Issue.error("Missing property value.", span);
    }

    public static Issue expectedPropertyValue(int startIndex, int length)
    {
        return expectedPropertyValue(new Span(startIndex, length));
    }

    public static Issue expectedPropertyValue(Span span)
    {
        return Issue.error("Expected property value.", span);
    }
}