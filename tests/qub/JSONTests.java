package qub;

public interface JSONTests
{
    static void test(final TestRunner runner)
    {
        runner.testGroup(JSON.class, () ->
        {
            runner.testGroup("parse()", () ->
            {
                final Action3<String,JSONSegment[],Issue[]> parseTest = (String text, JSONSegment[] expectedDocumentSegments, Issue[] expectedIssues) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final JSONDocument expectedDocument = new JSONDocument(Array.create(expectedDocumentSegments));
                        final JSONDocument actualDocumentWithoutErrors = JSON.parse(text);
                        test.assertEqual(expectedDocument.getSegments(), actualDocumentWithoutErrors.getSegments());

                        final List<Issue> actualIssues = new ArrayList<>();
                        final JSONDocument actualDocumentWithErrors = JSON.parse(text, actualIssues);
                        test.assertEqual(expectedDocument.getSegments(), actualDocumentWithErrors.getSegments());
                        test.assertEqual(Array.create(expectedIssues), actualIssues);
                    });
                };

                parseTest.run(null,
                    new JSONSegment[0],
                    new Issue[0]);
                parseTest.run("",
                    new JSONSegment[0],
                    new Issue[0]);

                parseTest.run(" ",
                    new JSONSegment[]
                        {
                            JSONToken.whitespace(" ", 0)
                        },
                    new Issue[0]);
                parseTest.run("\t",
                    new JSONSegment[]
                        {
                            JSONToken.whitespace("\t", 0)
                        },
                    new Issue[0]);
                parseTest.run("\r",
                    new JSONSegment[]
                        {
                            JSONToken.whitespace("\r", 0)
                        },
                    new Issue[0]);
                parseTest.run("true",
                    new JSONSegment[]
                        {
                            JSONToken.booleanToken("true", 0)
                        },
                    new Issue[0]);
                parseTest.run("false",
                    new JSONSegment[]
                        {
                            JSONToken.booleanToken("false", 0)
                        },
                    new Issue[0]);
                parseTest.run("123.456e-10",
                    new JSONSegment[]
                        {
                            JSONToken.number("123.456e-10", 0)
                        },
                    new Issue[0]);
                parseTest.run("// hello",
                    new JSONSegment[]
                        {
                            JSONToken.lineComment("// hello", 0)
                        },
                    new Issue[0]);
                parseTest.run("/* hello */",
                    new JSONSegment[]
                        {
                            JSONToken.blockComment("/* hello */", 0, true)
                        },
                    new Issue[0]);
                parseTest.run("{",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{", 0)
                        },
                    new Issue[]
                        {
                            JSONIssues.missingClosingRightCurlyBracket(0, 1)
                        });
                parseTest.run("123{}",
                    new JSONSegment[]
                        {
                            JSONToken.number("123"),
                            JSON.parseObject("{}", 3)
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedEndOfFile(3, 2)
                        });
                parseTest.run("{}123",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{}"),
                            JSONToken.number("123", 2)
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedEndOfFile(2, 3)
                        });
                parseTest.run("{\"a\":0\"b\":1}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{\"a\":0\"b\":1}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedCommaOrClosingRightCurlyBracket(6, 3)
                        });
                parseTest.run("{\"a\":0,}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{\"a\":0,}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedPropertyName(7, 1)
                        });
                parseTest.run("{,\"a\":0}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{,\"a\":0}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedPropertyNameOrClosingRightCurlyBracket(1, 1)
                        });
                parseTest.run("{,,}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{,,}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedPropertyNameOrClosingRightCurlyBracket(1, 1),
                            JSONIssues.expectedPropertyName(2, 1),
                            JSONIssues.expectedPropertyName(3, 1)
                        });
                parseTest.run("{123}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{123}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedPropertyNameOrClosingRightCurlyBracket(1, 3)
                        });
                parseTest.run("{\"a\":1,false}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{\"a\":1,false}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedPropertyName(7, 5)
                        });
                parseTest.run("{\"a\":1false}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{\"a\":1false}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedCommaOrClosingRightCurlyBracket(6, 5)
                        });
                parseTest.run("{\"a\"1}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{\"a\"1}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedColon(4, 1),
                            JSONIssues.expectedCommaOrClosingRightCurlyBracket(4, 1)
                        });
                parseTest.run("{\"a\":[]}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{\"a\":[]}")
                        },
                    new Issue[0]);
                parseTest.run("{\"a\"::}",
                    new JSONSegment[]
                        {
                            JSON.parseObject("{\"a\"::}")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedPropertyValue(5, 1),
                            JSONIssues.expectedCommaOrClosingRightCurlyBracket(5, 1)
                        });
                parseTest.run("[",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[")
                        },
                    new Issue[]
                        {
                            JSONIssues.missingClosingRightSquareBracket(0, 1)
                        });
                parseTest.run("[]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[]")
                        },
                    new Issue[0]);
                parseTest.run("[false true]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[false true]")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedCommaOrClosingRightSquareBracket(7, 4)
                        });
                parseTest.run("[{}]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[{}]")
                        },
                    new Issue[0]);
                parseTest.run("[false{}]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[false{}]")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedCommaOrClosingRightSquareBracket(6, 1)
                        });
                parseTest.run("[[]]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[[]]")
                        },
                    new Issue[0]);
                parseTest.run("[false[]]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[false[]]")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedCommaOrClosingRightSquareBracket(6, 1)
                        });
                parseTest.run("[,]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[,]")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedArrayElementOrClosingRightSquareBracket(1, 1),
                            JSONIssues.expectedArrayElement(2, 1)
                        });
                parseTest.run("[,,]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[,,]")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedArrayElementOrClosingRightSquareBracket(1, 1),
                            JSONIssues.expectedArrayElement(2, 1),
                            JSONIssues.expectedArrayElement(3, 1)
                        });
                parseTest.run("[$]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[$]")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedArrayElementOrClosingRightSquareBracket(1, 1)
                        });
                parseTest.run("[true$]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[true$]")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedCommaOrClosingRightSquareBracket(5, 1)
                        });
                parseTest.run("[true,$]",
                    new JSONSegment[]
                        {
                            JSON.parseArray("[true,$]")
                        },
                    new Issue[]
                        {
                            JSONIssues.expectedArrayElement(6, 1)
                        });
            });

            runner.testGroup("object(Action1<JSONObjectBuilder>)", () ->
            {
                runner.test("with null action", (Test test) ->
                {
                    test.assertThrows(() -> JSON.object(null), new PreConditionFailure("action cannot be null."));
                });

                runner.test("with empty action", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) -> {});
                    test.assertNotNull(object);
                    test.assertEqual("{}", object.toString());
                });

                runner.test("with boolean property", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.booleanProperty("apples", true);
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"apples\":true}", object.toString());
                });

                runner.test("with number property with int", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.numberProperty("apples", 50);
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"apples\":50}", object.toString());
                });

                runner.test("with number property with double", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.numberProperty("apples", 12.34);
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"apples\":12.34}", object.toString());
                });

                runner.test("with string property with empty", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.stringProperty("apples", "");
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"apples\":\"\"}", object.toString());
                });

                runner.test("with string property with non-empty", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.stringProperty("apples", "bananas");
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"apples\":\"bananas\"}", object.toString());
                });

                runner.test("with null property", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.nullProperty("huh?");
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"huh?\":null}", object.toString());
                });

                runner.test("with object property with no action", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.objectProperty("o");
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"o\":{}}", object.toString());
                });

                runner.test("with object property with empty action", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.objectProperty("o", (JSONObjectBuilder o) -> {});
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"o\":{}}", object.toString());
                });

                runner.test("with object property with non-empty action", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.objectProperty("o", (JSONObjectBuilder o) ->
                        {
                            o.booleanProperty("true", true);
                            o.numberProperty("age", 10);
                        });
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"o\":{\"true\":true,\"age\":10}}", object.toString());
                });

                runner.test("with array property with no action", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.arrayProperty("a");
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"a\":[]}", object.toString());
                });

                runner.test("with array property with empty action", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.arrayProperty("a", (JSONArrayBuilder a) -> {});
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"a\":[]}", object.toString());
                });

                runner.test("with array property with non-empty action", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.arrayProperty("a", (JSONArrayBuilder a) ->
                        {
                            a.booleanElement(true);
                            a.numberElement(10);
                        });
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"a\":[true,10]}", object.toString());
                });

                runner.test("with string array property with no arguments", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.stringArrayProperty("a");
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"a\":[]}", object.toString());
                });

                runner.test("with string array property with empty Iterable", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.stringArrayProperty("a", Iterable.create());
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"a\":[]}", object.toString());
                });

                runner.test("with string array property with non-empty Iterable", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.stringArrayProperty("a", Iterable.create("b", "c", "d"));
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"a\":[\"b\",\"c\",\"d\"]}", object.toString());
                });

                runner.test("with multiple properties", (Test test) ->
                {
                    final JSONObject object = JSON.object((JSONObjectBuilder builder) ->
                    {
                        builder.stringProperty("apples", "bananas");
                        builder.booleanProperty("pizza", true);
                    });
                    test.assertNotNull(object);
                    test.assertEqual("{\"apples\":\"bananas\",\"pizza\":true}", object.toString());
                });
            });

            runner.testGroup("array(Action1<JSONArrayBuilder>)", () ->
            {
                runner.test("with null action", (Test test) ->
                {
                    test.assertThrows(() -> JSON.array(null), new PreConditionFailure("action cannot be null."));
                });

                runner.test("with empty action", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) -> {});
                    test.assertNotNull(array);
                    test.assertEqual("[]", array.toString());
                });

                runner.test("with boolean element", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.booleanElement(true);
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[true]", array.toString());
                });

                runner.test("with number element with int", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.numberElement(50);
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[50]", array.toString());
                });

                runner.test("with number element with double", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.numberElement(12.34);
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[12.34]", array.toString());
                });

                runner.test("with string element with empty", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.stringElement("");
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[\"\"]", array.toString());
                });

                runner.test("with string element with non-empty", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.stringElement("bananas");
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[\"bananas\"]", array.toString());
                });

                runner.test("with null element", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.nullElement();
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[null]", array.toString());
                });

                runner.test("with object element with no action", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.objectElement();
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[{}]", array.toString());
                });

                runner.test("with object element with empty action", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.objectElement((JSONObjectBuilder o) -> {});
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[{}]", array.toString());
                });

                runner.test("with object element with non-empty action", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.objectElement((JSONObjectBuilder o) ->
                        {
                            o.booleanProperty("true", true);
                            o.numberProperty("age", 10);
                        });
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[{\"true\":true,\"age\":10}]", array.toString());
                });

                runner.test("with multiple elements", (Test test) ->
                {
                    final JSONArray array = JSON.array((JSONArrayBuilder builder) ->
                    {
                        builder.stringElement("bananas");
                        builder.booleanElement(true);
                    });
                    test.assertNotNull(array);
                    test.assertEqual("[\"bananas\",true]", array.toString());
                });
            });
        });
    }
}
