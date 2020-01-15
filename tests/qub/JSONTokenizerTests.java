package qub;

public interface JSONTokenizerTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(JSONTokenizer.class, () ->
        {
            runner.testGroup("create(String)", () ->
            {
                final Action2<String,Throwable> createErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> JSONTokenizer.create(text).toList(),
                            expected);
                    });
                };

                createErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                createErrorTest.run("n", new ParseException("Unrecognized JSONToken literal: n"));
                createErrorTest.run("nu", new ParseException("Unrecognized JSONToken literal: nu"));
                createErrorTest.run("nul", new ParseException("Unrecognized JSONToken literal: nul"));
                createErrorTest.run("NULL", new ParseException("Unrecognized JSONToken literal: NULL"));
                createErrorTest.run("nulls", new ParseException("Unrecognized JSONToken literal: nulls"));
                createErrorTest.run("t", new ParseException("Unrecognized JSONToken literal: t"));
                createErrorTest.run("tr", new ParseException("Unrecognized JSONToken literal: tr"));
                createErrorTest.run("tru", new ParseException("Unrecognized JSONToken literal: tru"));
                createErrorTest.run("TRUE", new ParseException("Unrecognized JSONToken literal: TRUE"));
                createErrorTest.run("trues", new ParseException("Unrecognized JSONToken literal: trues"));
                createErrorTest.run("f", new ParseException("Unrecognized JSONToken literal: f"));
                createErrorTest.run("fa", new ParseException("Unrecognized JSONToken literal: fa"));
                createErrorTest.run("fal", new ParseException("Unrecognized JSONToken literal: fal"));
                createErrorTest.run("fals", new ParseException("Unrecognized JSONToken literal: fals"));
                createErrorTest.run("FALSE", new ParseException("Unrecognized JSONToken literal: FALSE"));
                createErrorTest.run("falses", new ParseException("Unrecognized JSONToken literal: falses"));
                createErrorTest.run("'hello", new ParseException("Missing quoted-string closing quote: '"));
                createErrorTest.run("\"hello", new ParseException("Missing quoted-string closing quote: \""));
                createErrorTest.run("@", new ParseException("Unrecognized JSONToken start character: \"@\""));
                createErrorTest.run("!", new ParseException("Unrecognized JSONToken start character: \"!\""));
                createErrorTest.run("#", new ParseException("Unrecognized JSONToken start character: \"#\""));
                createErrorTest.run("+", new ParseException("Unrecognized JSONToken start character: \"+\""));
                createErrorTest.run("-", new ParseException("Missing digits after number's negative sign: \"-\""));
                createErrorTest.run("- ", new ParseException("Missing digits after number's negative sign: \"-\""));
                createErrorTest.run("-s", new ParseException("Missing digits after number's negative sign: \"-\""));
                createErrorTest.run("-.", new ParseException("Missing digits after number's negative sign: \"-\""));
                createErrorTest.run("-e", new ParseException("Missing digits after number's negative sign: \"-\""));
                createErrorTest.run("-E", new ParseException("Missing digits after number's negative sign: \"-\""));
                createErrorTest.run(".", new ParseException("Unrecognized JSONToken start character: \".\""));
                createErrorTest.run(".5", new ParseException("Unrecognized JSONToken start character: \".\""));
                createErrorTest.run("0.", new ParseException("Missing digits after number's decimal point: \"0.\""));
                createErrorTest.run("200.", new ParseException("Missing digits after number's decimal point: \"200.\""));
                createErrorTest.run("200. ", new ParseException("Missing digits after number's decimal point: \"200.\""));
                createErrorTest.run("200.e", new ParseException("Missing digits after number's decimal point: \"200.\""));
                createErrorTest.run("200.E", new ParseException("Missing digits after number's decimal point: \"200.\""));
                createErrorTest.run("200.-", new ParseException("Missing digits after number's decimal point: \"200.\""));
                createErrorTest.run("200.+", new ParseException("Missing digits after number's decimal point: \"200.\""));
                createErrorTest.run("10e", new ParseException("Missing digits after number's exponent character: \"10e\""));
                createErrorTest.run("10E", new ParseException("Missing digits after number's exponent character: \"10E\""));
                createErrorTest.run("10ef", new ParseException("Missing digits after number's exponent character: \"10e\""));
                createErrorTest.run("10Ef", new ParseException("Missing digits after number's exponent character: \"10E\""));
                createErrorTest.run("10e-", new ParseException("Missing digits after number's exponent sign character: \"10e-\""));
                createErrorTest.run("10e-f", new ParseException("Missing digits after number's exponent sign character: \"10e-\""));
                createErrorTest.run("10e+", new ParseException("Missing digits after number's exponent sign character: \"10e+\""));
                createErrorTest.run("10e+f", new ParseException("Missing digits after number's exponent sign character: \"10e+\""));
                createErrorTest.run("/", new ParseException("Missing comment start sequence second character."));
                createErrorTest.run("/a", new ParseException("Unrecognized comment start sequence second character: \"a\""));
                createErrorTest.run("/\n", new ParseException("Unrecognized comment start sequence second character: \"\\n\""));
                createErrorTest.run("/*", new ParseException("Missing block comment end sequence (\"*/\")."));
                createErrorTest.run("/**", new ParseException("Missing block comment end sequence second character (\"/\")."));

                final Action2<String,Indexable<JSONToken>> createTest = (String text, Indexable<JSONToken> expectedTokens) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final JSONTokenizer tokenizer = JSONTokenizer.create(text);
                        test.assertFalse(tokenizer.hasStarted());
                        test.assertFalse(tokenizer.hasCurrent());
                        test.assertThrows(tokenizer::getCurrent,
                            new PreConditionFailure("this.hasCurrent() cannot be false."));

                        final int expectedTokenCount = expectedTokens.getCount();
                        for (int i = 0; i < expectedTokenCount; ++i)
                        {
                            test.assertTrue(tokenizer.next());
                            test.assertTrue(tokenizer.hasStarted());
                            test.assertTrue(tokenizer.hasCurrent());
                            test.assertEqual(expectedTokens.get(i), tokenizer.getCurrent());
                        }

                        test.assertFalse(tokenizer.next());
                        test.assertTrue(tokenizer.hasStarted());
                        test.assertFalse(tokenizer.hasCurrent());
                        test.assertThrows(tokenizer::getCurrent,
                            new PreConditionFailure("this.hasCurrent() cannot be false."));
                    });
                };

                createTest.run("", List.create());

                createTest.run("{", List.create(JSONToken.leftCurlyBracket));
                createTest.run("}", List.create(JSONToken.rightCurlyBracket));
                createTest.run("[", List.create(JSONToken.leftSquareBracket));
                createTest.run("]", List.create(JSONToken.rightSquareBracket));
                createTest.run(":", List.create(JSONToken.colon));
                createTest.run(",", List.create(JSONToken.comma));
                createTest.run("null", List.create(JSONToken.nullToken));
                createTest.run("\n", List.create(JSONToken.newLine));
                createTest.run("\r\n", List.create(JSONToken.carriageReturnNewLine));
                createTest.run("\r", List.create(JSONToken.carriageReturn));
                createTest.run("\"hello\"", List.create(JSONToken.quotedString("\"hello\"")));
                createTest.run("\"Dan's\"", List.create(JSONToken.quotedString("\"Dan's\"")));
                createTest.run("\"She said, \\\"Hello!\\\".\"", List.create(JSONToken.quotedString("\"She said, \\\"Hello!\\\".\"")));
                createTest.run("'a b c'", List.create(JSONToken.quotedString("'a b c'")));
                createTest.run("'a \"b\" c'", List.create(JSONToken.quotedString("'a \"b\" c'")));
                createTest.run("'a \\\'b\\\' c'", List.create(JSONToken.quotedString("'a \\\'b\\\' c'")));
                createTest.run("'a\\\\\\\\b'", List.create(JSONToken.quotedString("'a\\\\\\\\b'")));
                createTest.run("false", List.create(JSONToken.falseToken));
                createTest.run("true", List.create(JSONToken.trueToken));
                createTest.run("0", List.create(JSONToken.number("0")));
                createTest.run("1", List.create(JSONToken.number("1")));
                createTest.run("-1", List.create(JSONToken.number("-1")));
                createTest.run("10", List.create(JSONToken.number("10")));
                createTest.run("1.2", List.create(JSONToken.number("1.2")));
                createTest.run("1.23", List.create(JSONToken.number("1.23")));
                createTest.run("30.8e5", List.create(JSONToken.number("30.8e5")));
                createTest.run("1e7", List.create(JSONToken.number("1e7")));
                createTest.run("1E7", List.create(JSONToken.number("1E7")));
                createTest.run("1e+89", List.create(JSONToken.number("1e+89")));
                createTest.run("50E-3", List.create(JSONToken.number("50E-3")));
                createTest.run(" ", List.create(JSONToken.whitespace(" ")));
                createTest.run("      ", List.create(JSONToken.whitespace("      ")));
                createTest.run("\t", List.create(JSONToken.whitespace("\t")));
                createTest.run(" \t ", List.create(JSONToken.whitespace(" \t ")));
                createTest.run("//", List.create(JSONToken.lineComment("//")));
                createTest.run("//      ", List.create(JSONToken.lineComment("//      ")));
                createTest.run("// comment text", List.create(JSONToken.lineComment("// comment text")));
                createTest.run("///// comment text", List.create(JSONToken.lineComment("///// comment text")));
                createTest.run("/**/", List.create(JSONToken.blockComment("/**/")));
                createTest.run("/* hello */", List.create(JSONToken.blockComment("/* hello */")));
                createTest.run("/* \r \r\n \n */", List.create(JSONToken.blockComment("/* \r \r\n \n */")));
                createTest.run("/*****/", List.create(JSONToken.blockComment("/*****/")));

                createTest.run("{{{", List.create(
                    JSONToken.leftCurlyBracket,
                    JSONToken.leftCurlyBracket,
                    JSONToken.leftCurlyBracket));
                createTest.run("}}", List.create(
                    JSONToken.rightCurlyBracket,
                    JSONToken.rightCurlyBracket));
                createTest.run("{}", List.create(
                    JSONToken.leftCurlyBracket,
                    JSONToken.rightCurlyBracket));
                createTest.run("\r\r\r\n\n", List.create(
                    JSONToken.carriageReturn,
                    JSONToken.carriageReturn,
                    JSONToken.carriageReturnNewLine,
                    JSONToken.newLine));
                createTest.run("null50", List.create(
                    JSONToken.nullToken,
                    JSONToken.number("50")));
                createTest.run("50null", List.create(
                    JSONToken.number("50"),
                    JSONToken.nullToken));
                createTest.run("1e10true", List.create(
                    JSONToken.number("1e10"),
                    JSONToken.trueToken));
                createTest.run("null true", List.create(
                    JSONToken.nullToken,
                    JSONToken.whitespace(" "),
                    JSONToken.trueToken));
                createTest.run("// 1\n// 2\r\n// 3\r// 4", List.create(
                    JSONToken.lineComment("// 1"),
                    JSONToken.newLine,
                    JSONToken.lineComment("// 2"),
                    JSONToken.carriageReturnNewLine,
                    JSONToken.lineComment("// 3"),
                    JSONToken.carriageReturn,
                    JSONToken.lineComment("// 4")));
            });

            runner.testGroup("isLetter(char)", () ->
            {
                final Action2<Character,Boolean> isLetterTest = (Character character, Boolean expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(character), (Test test) ->
                    {
                        test.assertEqual(expected, JSONTokenizer.isLetter(character));
                    });
                };

                isLetterTest.run('a', true);
                isLetterTest.run('m', true);
                isLetterTest.run('z', true);
                isLetterTest.run('A', true);
                isLetterTest.run('G', true);
                isLetterTest.run('Z', true);

                isLetterTest.run('0', false);
                isLetterTest.run('9', false);
                isLetterTest.run(' ', false);
                isLetterTest.run('.', false);
                isLetterTest.run('*', false);
                isLetterTest.run('_', false);
                isLetterTest.run('~', false);
            });

            runner.testGroup("isDigit(char)", () ->
            {
                final Action2<Character,Boolean> isDigitTest = (Character character, Boolean expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(character), (Test test) ->
                    {
                        test.assertEqual(expected, JSONTokenizer.isDigit(character));
                    });
                };

                isDigitTest.run('0', true);
                isDigitTest.run('5', true);
                isDigitTest.run('9', true);

                isDigitTest.run('a', false);
                isDigitTest.run('z', false);
                isDigitTest.run(' ', false);
                isDigitTest.run('.', false);
                isDigitTest.run('*', false);
                isDigitTest.run('_', false);
            });

            runner.testGroup("isWhitespace(char)", () ->
            {
                final Action2<Character,Boolean> isWhitespaceTest = (Character character, Boolean expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(character), (Test test) ->
                    {
                        test.assertEqual(expected, JSONTokenizer.isWhitespace(character));
                    });
                };

                isWhitespaceTest.run(' ', true);
                isWhitespaceTest.run('\t', true);

                isWhitespaceTest.run('\r', false);
                isWhitespaceTest.run('\n', false);
                isWhitespaceTest.run('a', false);
                isWhitespaceTest.run('z', false);
                isWhitespaceTest.run('.', false);
                isWhitespaceTest.run('*', false);
                isWhitespaceTest.run('_', false);
            });
        });
    }
}
