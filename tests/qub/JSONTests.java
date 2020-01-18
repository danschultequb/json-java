package qub;

public interface JSONTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(JSON.class, () ->
        {
            runner.testGroup("parse(String)", () ->
            {
                final Action2<String,Throwable> parseErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> JSON.parse(text).await(), expected);
                    });
                };

                parseErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                parseErrorTest.run("", new PreConditionFailure("text cannot be empty."));
                parseErrorTest.run("// hello", new ParseException("No JSON tokens found."));
                parseErrorTest.run("/* hello */", new ParseException("No JSON tokens found."));
                parseErrorTest.run(",", new ParseException("Unexpected JSON token: ,"));

                final Action2<String,JSONSegment> parseTest = (String text, JSONSegment expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertEqual(expected, JSON.parse(text).await());
                    });
                };

                parseTest.run("null", JSONNull.segment);
                parseTest.run("false", JSONBoolean.falseSegment);
                parseTest.run("true", JSONBoolean.trueSegment);
                parseTest.run("123", JSONNumber.get("123"));
                parseTest.run("\"hello\"", JSONString.get("hello"));
                parseTest.run("[]", JSONArray.create());
                parseTest.run("{}", JSONObject.create());
            });

            runner.testGroup("parse(Iterable<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parse((Iterable<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });

            runner.testGroup("parse(Iterator<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parse((Iterator<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });

            runner.testGroup("parse(JSONTokenizer)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parse((JSONTokenizer)null),
                        new PreConditionFailure("tokenizer cannot be null."));
                });

                runner.test("with non-started tokenizer", (Test test) ->
                {
                    final JSONTokenizer tokenizer = JSONTokenizer.create("{}");
                    test.assertEqual(JSONObject.create(), JSON.parse(tokenizer).await());
                });

                runner.test("with started tokenizer on whitespace token", (Test test) ->
                {
                    final JSONTokenizer tokenizer = JSONTokenizer.create(" true");
                    tokenizer.next();
                    test.assertEqual(JSONBoolean.trueSegment, JSON.parse(tokenizer).await());
                });

                runner.test("with started tokenizer on newline token", (Test test) ->
                {
                    final JSONTokenizer tokenizer = JSONTokenizer.create("\r\nfalse");
                    tokenizer.next();
                    test.assertEqual(JSONBoolean.falseSegment, JSON.parse(tokenizer).await());
                });

                runner.test("with started tokenizer on line comment token", (Test test) ->
                {
                    final JSONTokenizer tokenizer = JSONTokenizer.create("// line comment\r\nfalse");
                    tokenizer.next();
                    test.assertEqual(JSONBoolean.falseSegment, JSON.parse(tokenizer).await());
                });

                runner.test("with started tokenizer on block comment token", (Test test) ->
                {
                    final JSONTokenizer tokenizer = JSONTokenizer.create("/* block comment */false");
                    tokenizer.next();
                    test.assertEqual(JSONBoolean.falseSegment, JSON.parse(tokenizer).await());
                });
            });

            runner.testGroup("parseObject(String)", () ->
            {
                final Action2<String,Throwable> parseObjectErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> JSON.parseObject(text).await(), expected);
                    });
                };

                parseObjectErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                parseObjectErrorTest.run("", new PreConditionFailure("text cannot be empty."));
                parseObjectErrorTest.run("null", new PreConditionFailure("tokenizer.getCurrent() (null) must be {."));
                parseObjectErrorTest.run("false", new PreConditionFailure("tokenizer.getCurrent() (false) must be {."));
                parseObjectErrorTest.run("true", new PreConditionFailure("tokenizer.getCurrent() (true) must be {."));
                parseObjectErrorTest.run("// hello", new PreConditionFailure("tokenizer.hasCurrent() cannot be false."));
                parseObjectErrorTest.run("/* hello */", new PreConditionFailure("tokenizer.hasCurrent() cannot be false."));
                parseObjectErrorTest.run("123", new PreConditionFailure("tokenizer.getCurrent() (123) must be {."));
                parseObjectErrorTest.run("\"hello\"", new PreConditionFailure("tokenizer.getCurrent() (\"hello\") must be {."));
                parseObjectErrorTest.run("{", new ParseException("Missing object right curly bracket ('}')."));
                parseObjectErrorTest.run("{   ", new ParseException("Missing object right curly bracket ('}')."));
                parseObjectErrorTest.run("{,", new ParseException("Expected quoted-string object property name or right curly bracket ('}')."));
                parseObjectErrorTest.run("{ null", new ParseException("Expected quoted-string object property name or right curly bracket ('}')."));
                parseObjectErrorTest.run("{ true", new ParseException("Expected quoted-string object property name or right curly bracket ('}')."));
                parseObjectErrorTest.run("{ 50", new ParseException("Expected quoted-string object property name or right curly bracket ('}')."));
                parseObjectErrorTest.run("{ \"a\"", new ParseException("Missing object property name and value separator (':')."));
                parseObjectErrorTest.run("{ \"a\":", new ParseException("Missing object property value."));
                parseObjectErrorTest.run("{ \"a\":0", new ParseException("Missing object right curly bracket ('}')."));
                parseObjectErrorTest.run("{ \"a\":0,", new ParseException("Missing object property."));
                parseObjectErrorTest.run("{ \"a\":0, }", new ParseException("Expected quoted-string object property name."));
                parseObjectErrorTest.run("{ \"a\":0,,", new ParseException("Expected quoted-string object property name."));
                parseObjectErrorTest.run("{ \"a\":0 \"b\":1", new ParseException("Expected object property separator (',') or right curly bracket ('}')."));
                parseObjectErrorTest.run("{ \"a\":0 null", new ParseException("Expected object property separator (',') or right curly bracket ('}')."));
                parseObjectErrorTest.run("{ \"a\":0, null", new ParseException("Expected quoted-string object property name."));

                final Action2<String,JSONObject> parseObjectTest = (String text, JSONObject expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertEqual(expected, JSON.parseObject(text).await());
                    });
                };

                parseObjectTest.run("{}", JSONObject.create());
                parseObjectTest.run("{\"b\":null}",
                    JSONObject.create(
                        JSONProperty.create("b", JSONNull.segment)));
                parseObjectTest.run("{\"b\":\"c\"}",
                    JSONObject.create(
                        JSONProperty.create("b", "c")));
                parseObjectTest.run("{\"b\":[1, 2, 3]}",
                    JSONObject.create(
                        JSONProperty.create("b",
                            JSONArray.create(
                                JSONNumber.get(1),
                                JSONNumber.get(2),
                                JSONNumber.get(3)))));
                parseObjectTest.run("{\"c\":{}}",
                    JSONObject.create(
                        JSONProperty.create("c", JSONObject.create())));
                parseObjectTest.run("{\"c\":{},\"d\":50.2}",
                    JSONObject.create(
                        JSONProperty.create("c", JSONObject.create()),
                        JSONProperty.create("d", JSONNumber.get(50.2))));
            });

            runner.testGroup("parseObject(Iterable<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseObject((Iterable<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });

            runner.testGroup("parseObject(Iterator<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseObject((Iterator<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });

            runner.testGroup("parseObject(JSONTokenizer)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseObject((JSONTokenizer)null),
                        new PreConditionFailure("tokenizer cannot be null."));
                });
            });

            runner.testGroup("parseObjectProperty(String)", () ->
            {
                final Action2<String,Throwable> parseObjectPropertyErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> JSON.parseObjectProperty(text).await(), expected);
                    });
                };

                parseObjectPropertyErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                parseObjectPropertyErrorTest.run("", new PreConditionFailure("text cannot be empty."));
                parseObjectPropertyErrorTest.run("// hello", new PreConditionFailure("tokenizer.hasCurrent() cannot be false."));
                parseObjectPropertyErrorTest.run("/* hello */", new PreConditionFailure("tokenizer.hasCurrent() cannot be false."));
                parseObjectPropertyErrorTest.run("   ", new PreConditionFailure("tokenizer.hasCurrent() cannot be false."));
                parseObjectPropertyErrorTest.run("null", new PreConditionFailure("tokenizer.getCurrent().getType() (Null) must be QuotedString."));
                parseObjectPropertyErrorTest.run("false", new PreConditionFailure("tokenizer.getCurrent().getType() (Boolean) must be QuotedString."));
                parseObjectPropertyErrorTest.run("true", new PreConditionFailure("tokenizer.getCurrent().getType() (Boolean) must be QuotedString."));
                parseObjectPropertyErrorTest.run("123", new PreConditionFailure("tokenizer.getCurrent().getType() (Number) must be QuotedString."));
                parseObjectPropertyErrorTest.run("\"hello\"", new ParseException("Missing object property name and value separator (':')."));
                parseObjectPropertyErrorTest.run("\"hello\"   ", new ParseException("Missing object property name and value separator (':')."));
                parseObjectPropertyErrorTest.run("\"hello\"null  ", new ParseException("Expected object property name and value separator (':')."));
                parseObjectPropertyErrorTest.run("\"hello\" null  ", new ParseException("Expected object property name and value separator (':')."));
                parseObjectPropertyErrorTest.run("\"hello\":", new ParseException("Missing object property value."));
                parseObjectPropertyErrorTest.run("\"hello\":,", new ParseException("Expected object property value."));
                parseObjectPropertyErrorTest.run("\"hello\":}", new ParseException("Unexpected object property value token: \"}\""));
                parseObjectPropertyErrorTest.run("\"hello\":]", new ParseException("Unexpected object property value token: \"]\""));

                final Action2<String,JSONProperty> parseObjectPropertyTest = (String text, JSONProperty expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertEqual(expected, JSON.parseObjectProperty(text).await());
                    });
                };

                parseObjectPropertyTest.run("\"a\":false", JSONProperty.create("a", false));
                parseObjectPropertyTest.run("\"a\":true", JSONProperty.create("a", true));
                parseObjectPropertyTest.run("\"a\":null", JSONProperty.create("a", JSONNull.segment));
                parseObjectPropertyTest.run("\"b\" : 123", JSONProperty.create("b", 123));
                parseObjectPropertyTest.run("\"b\" : 123.4", JSONProperty.create("b", 123.4));
                parseObjectPropertyTest.run("\"c\"\n:\n\"hello\"", JSONProperty.create("c", "hello"));
                parseObjectPropertyTest.run("\"hello\":{}", JSONProperty.create("hello", JSONObject.create()));
                parseObjectPropertyTest.run("\"d\":[]", JSONProperty.create("d", JSONArray.create()));
                parseObjectPropertyTest.run("\"d\":[true]", JSONProperty.create("d", JSONArray.create(JSONBoolean.trueSegment)));
            });

            runner.testGroup("parseObjectProperty(Iterable<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseObjectProperty((Iterable<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });

            runner.testGroup("parseObjectProperty(Iterator<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseObjectProperty((Iterator<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });

            runner.testGroup("parseObjectProperty(JSONTokenizer)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseObjectProperty((JSONTokenizer)null),
                        new PreConditionFailure("tokenizer cannot be null."));
                });
            });

            runner.testGroup("parseArray(String)", () ->
            {
                final Action2<String,Throwable> parseArrayErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> JSON.parseArray(text).await(), expected);
                    });
                };

                parseArrayErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                parseArrayErrorTest.run("", new PreConditionFailure("text cannot be empty."));
                parseArrayErrorTest.run("null", new PreConditionFailure("tokenizer.getCurrent() (null) must be [."));
                parseArrayErrorTest.run("false", new PreConditionFailure("tokenizer.getCurrent() (false) must be [."));
                parseArrayErrorTest.run("true", new PreConditionFailure("tokenizer.getCurrent() (true) must be [."));
                parseArrayErrorTest.run("// hello", new PreConditionFailure("tokenizer.hasCurrent() cannot be false."));
                parseArrayErrorTest.run("/* hello */", new PreConditionFailure("tokenizer.hasCurrent() cannot be false."));
                parseArrayErrorTest.run("  \t\n\r\n\r  ", new PreConditionFailure("tokenizer.hasCurrent() cannot be false."));
                parseArrayErrorTest.run("123", new PreConditionFailure("tokenizer.getCurrent() (123) must be [."));
                parseArrayErrorTest.run("\"hello\"", new PreConditionFailure("tokenizer.getCurrent() (\"hello\") must be [."));
                parseArrayErrorTest.run("[", new ParseException("Missing array right square bracket (']')."));
                parseArrayErrorTest.run("[   ", new ParseException("Missing array right square bracket (']')."));
                parseArrayErrorTest.run("[\n  \t", new ParseException("Missing array right square bracket (']')."));
                parseArrayErrorTest.run("[//hello\n  ", new ParseException("Missing array right square bracket (']')."));
                parseArrayErrorTest.run("[  /** hello ***/\n", new ParseException("Missing array right square bracket (']')."));
                parseArrayErrorTest.run("[null", new ParseException("Missing array right square bracket (']')."));
                parseArrayErrorTest.run("[ true ", new ParseException("Missing array right square bracket (']')."));
                parseArrayErrorTest.run("[ true false ", new ParseException("Expected array element separator (',') or right square bracket (']')."));
                parseArrayErrorTest.run("[,", new ParseException("Expected array element."));
                parseArrayErrorTest.run("[true,", new ParseException("Missing array element."));
                parseArrayErrorTest.run("[true,]", new ParseException("Expected array element."));
                parseArrayErrorTest.run("[[]", new ParseException("Missing array right square bracket (']')."));
                parseArrayErrorTest.run("[[][", new ParseException("Expected array element separator (',') or right square bracket (']')."));
                parseArrayErrorTest.run("[:", new ParseException("Unexpected array element token: \":\""));
                parseArrayErrorTest.run("[{", new ParseException("Missing object right curly bracket ('}')."));

                final Action2<String,JSONArray> parseArrayTest = (String text, JSONArray expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertEqual(expected, JSON.parseArray(text).await());
                    });
                };

                parseArrayTest.run("[]", JSONArray.create());
                parseArrayTest.run("[    ]", JSONArray.create());
                parseArrayTest.run("[null]", JSONArray.create(JSONNull.segment));
                parseArrayTest.run("[false]", JSONArray.create(JSONBoolean.falseSegment));
                parseArrayTest.run("[true]", JSONArray.create(JSONBoolean.trueSegment));
                parseArrayTest.run("[ null, true ]", JSONArray.create(JSONNull.segment, JSONBoolean.trueSegment));
                parseArrayTest.run("[ null, true, false ]", JSONArray.create(JSONNull.segment, JSONBoolean.trueSegment, JSONBoolean.falseSegment));
                parseArrayTest.run("[[],[],[  ]]", JSONArray.create(JSONArray.create(), JSONArray.create(), JSONArray.create()));
            });

            runner.testGroup("parseArray(Iterable<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseArray((Iterable<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });

            runner.testGroup("parseArray(Iterator<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseArray((Iterator<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });
            });

            runner.testGroup("parseArray(JSONTokenizer)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSON.parseArray((JSONTokenizer)null),
                        new PreConditionFailure("tokenizer cannot be null."));
                });
            });
        });
    }
}
