package qub;

public interface JSONStringTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(JSONString.class, () ->
        {
            runner.testGroup("getFromQuoted(String)", () ->
            {
                final Action2<String,Throwable> getFromQuotedErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> JSONString.getFromQuoted(text), expected);
                    });
                };

                getFromQuotedErrorTest.run(null, new PreConditionFailure("quotedText cannot be null."));
                getFromQuotedErrorTest.run("", new PreConditionFailure("quotedText cannot be empty."));
                getFromQuotedErrorTest.run("hello", new PreConditionFailure("Strings.isQuoted(quotedText) cannot be false."));
                getFromQuotedErrorTest.run("\"", new PreConditionFailure("Strings.isQuoted(quotedText) cannot be false."));
                getFromQuotedErrorTest.run("\"\n\"", new PreConditionFailure("text.contains(\"\\n\") cannot be true."));
                getFromQuotedErrorTest.run("'\n'", new PreConditionFailure("text.contains(\"\\n\") cannot be true."));

                final Action2<String,JSONString> getFromQuotedTest = (String text, JSONString expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertEqual(expected, JSONString.getFromQuoted(text));
                    });
                };

                getFromQuotedTest.run("\"\"", JSONString.get("", '\"'));
                getFromQuotedTest.run("''", JSONString.get("", '\''));
                getFromQuotedTest.run("\"abc\"", JSONString.get("abc", '\"'));
                getFromQuotedTest.run("'abc'", JSONString.get("abc", '\''));
                getFromQuotedTest.run("\"\\n\"", JSONString.get("\\n", '\"'));
                getFromQuotedTest.run("'\\n'", JSONString.get("\\n", '\''));
            });

            runner.testGroup("get(String)", () ->
            {
                final Action2<String,Throwable> getErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> JSONString.get(text), expected);
                    });
                };

                getErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                getErrorTest.run("\n", new PreConditionFailure("text.contains(\"\\n\") cannot be true."));
                getErrorTest.run("\"\n\"", new PreConditionFailure("text.contains(\"\\n\") cannot be true."));
                getErrorTest.run("'\n'", new PreConditionFailure("text.contains(\"\\n\") cannot be true."));

                final Action2<String,JSONString> getTest = (String text, JSONString expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertEqual(expected, JSONString.get(text));
                    });
                };

                getTest.run("", JSONString.get("", '\"'));
                getTest.run("\"\"", JSONString.get("\"\"", '\"'));
                getTest.run("''", JSONString.get("''", '\"'));
                getTest.run("\"abc\"", JSONString.get("\"abc\"", '\"'));
                getTest.run("'abc'", JSONString.get("'abc'", '\"'));
                getTest.run("\"\\n\"", JSONString.get("\"\\n\"", '\"'));
                getTest.run("'\\n'", JSONString.get("'\\n'", '\"'));
            });

            runner.testGroup("get(String,char)", () ->
            {
                final Action3<String,Character,Throwable> getErrorTest = (String text, Character quote, Throwable expected) ->
                {
                    runner.test("with " + English.andList(Strings.escapeAndQuote(text), Characters.escapeAndQuote(quote)), (Test test) ->
                    {
                        test.assertThrows(() -> JSONString.get(text, quote), expected);
                    });
                };

                getErrorTest.run(null, '\"', new PreConditionFailure("text cannot be null."));
                getErrorTest.run("\n", '\"', new PreConditionFailure("text.contains(\"\\n\") cannot be true."));
                getErrorTest.run("\"\n\"", '\"', new PreConditionFailure("text.contains(\"\\n\") cannot be true."));
                getErrorTest.run("'\n'", '\"', new PreConditionFailure("text.contains(\"\\n\") cannot be true."));

                final Action2<String,Character> getTest = (String text, Character quote) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final JSONString string = JSONString.get(text, quote);
                        test.assertNotNull(string);
                        test.assertEqual(text, string.getValue());
                        test.assertEqual(quote, string.getQuote());
                    });
                };

                getTest.run("", '\"');
                getTest.run("", '\'');
                getTest.run("\"\"", '\"');
                getTest.run("\"\"", '\'');
                getTest.run("''", '\"');
                getTest.run("''", '\'');
                getTest.run("\"abc\"", '\"');
                getTest.run("'abc'", '\"');
                getTest.run("\"\\n\"", '\"');
                getTest.run("'\\n'", '\"');
            });

            runner.testGroup("toString()", () ->
            {
                final Action2<JSONString,String> toStringTest = (JSONString string, String expected) ->
                {
                    runner.test("with " + string, (Test test) ->
                    {
                        test.assertEqual(expected, string.toString());
                    });
                };

                toStringTest.run(JSONString.get(""), "\"\"");
                toStringTest.run(JSONString.get("abc"), "\"abc\"");
                toStringTest.run(JSONString.get("She said, \"Hello!\""), "\"She said, \\\"Hello!\\\"\"");
                toStringTest.run(JSONString.get("She said, \"Hello!\"", '\''), "'She said, \"Hello!\"'");
                toStringTest.run(JSONString.get("a'b'c", '\''), "'a\\'b\\'c'");
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<JSONString,Object,Boolean> equalsTest = (JSONString string, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(string, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, string.equals(rhs));
                    });
                };

                equalsTest.run(JSONString.get(""), null, false);
                equalsTest.run(JSONString.get(""), "", false);
                equalsTest.run(JSONString.get(""), JSONString.get(""), true);
                equalsTest.run(JSONString.get("", '\''), JSONString.get("", '\"'), false);
                equalsTest.run(JSONString.get("abc"), JSONString.get("def"), false);
                equalsTest.run(JSONString.get("\""), JSONString.get("\\\""), false);
            });

            runner.testGroup("equals(JSONString)", () ->
            {
                final Action3<JSONString,JSONString,Boolean> equalsTest = (JSONString string, JSONString rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(string, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, string.equals(rhs));
                    });
                };

                equalsTest.run(JSONString.get(""), null, false);
                equalsTest.run(JSONString.get(""), JSONString.get(""), true);
                equalsTest.run(JSONString.get("", '\''), JSONString.get("", '\"'), false);
                equalsTest.run(JSONString.get("abc"), JSONString.get("def"), false);
                equalsTest.run(JSONString.get("\""), JSONString.get("\\\""), false);
            });
        });
    }
}
