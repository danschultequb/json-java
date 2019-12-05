package qub;

public class JSONPropertyTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(JSONProperty.class, () ->
        {
            runner.testGroup("constructor()", () ->
            {
                final Action6<String,JSONQuotedString,String,JSONToken,JSONSegment,Integer> constructorTest = (String text, JSONQuotedString nameSegment, String name, JSONToken colonSegment, JSONSegment valueSegment, Integer afterEndIndex) ->
                {
                    runner.test("with \"" + text + "\"", (Test test) ->
                    {
                        final JSONProperty propertySegment = JSON.parseProperty(text);

                        test.assertEqual(nameSegment, propertySegment.getNameSegment());
                        test.assertEqual(name, propertySegment.getName());

                        test.assertEqual(colonSegment, propertySegment.getColonSegment());

                        test.assertEqual(valueSegment, propertySegment.getValueSegment());

                        test.assertEqual(text, propertySegment.toString());

                        test.assertEqual(nameSegment.getStartIndex(), propertySegment.getStartIndex());

                        test.assertEqual(afterEndIndex, propertySegment.getAfterEndIndex());

                        test.assertEqual(afterEndIndex - nameSegment.getStartIndex(), propertySegment.getLength());
                    });
                };

                constructorTest.run("\"",
                    JSONToken.quotedString("\"", 0, false),
                    "",
                    null,
                    null,
                    1);
                constructorTest.run("\"\"",
                    JSONToken.quotedString("\"\"", 0, true),
                    "",
                    null,
                    null,
                    2);

                constructorTest.run("\"test",
                    JSONToken.quotedString("\"test", 0, false),
                    "test",
                    null,
                    null,
                    5);
                constructorTest.run("\"test\"",
                    JSONToken.quotedString("\"test\"", 0, true),
                    "test",
                    null,
                    null,
                    6);

                constructorTest.run("\"a\" ",
                    JSONToken.quotedString("\"a\"", 0, true),
                    "a",
                    null,
                    null,
                    4);
                constructorTest.run("\"a\":",
                    JSONToken.quotedString("\"a\"", 0, true),
                    "a",
                    JSONToken.colon(3),
                    null,
                    4);
                constructorTest.run("\"a\" :",
                    JSONToken.quotedString("\"a\"", 0, true),
                    "a",
                    JSONToken.colon(4),
                    null,
                    5);

                constructorTest.run("\"a\":\"b\"",
                    JSONToken.quotedString("\"a\"", 0, true),
                    "a",
                    JSONToken.colon(3),
                    JSONToken.quotedString("\"b\"", 4, true),
                    7);
                constructorTest.run("\"a\":  ",
                    JSONToken.quotedString("\"a\"", 0, true),
                    "a",
                    JSONToken.colon(3),
                    null,
                    6);
                constructorTest.run("\"a\":// comment",
                    JSONToken.quotedString("\"a\"", 0, true),
                    "a",
                    JSONToken.colon(3),
                    null,
                    14);
                constructorTest.run("\"a\":/* comment",
                    JSONToken.quotedString("\"a\"", 0, true),
                    "a",
                    JSONToken.colon(3),
                    null,
                    14);
                constructorTest.run("\"apples\":{}",
                    JSONToken.quotedString("\"apples\"", 0, true),
                    "apples",
                    JSONToken.colon(8),
                    JSON.parseObject("{}", 9),
                    11);
                constructorTest.run("\"apples\":false",
                    JSONToken.quotedString("\"apples\"", 0, true),
                    "apples",
                    JSONToken.colon(8),
                    JSONToken.booleanToken("false", 9),
                    14);
                constructorTest.run("\"apples\":true",
                    JSONToken.quotedString("\"apples\"", 0, true),
                    "apples",
                    JSONToken.colon(8),
                    JSONToken.booleanToken("true", 9),
                    13);
                constructorTest.run("\"apples\":null",
                    JSONToken.quotedString("\"apples\"", 0, true),
                    "apples",
                    JSONToken.colon(8),
                    JSONToken.nullToken("null", 9),
                    13);
                constructorTest.run("\"apples\":3.14",
                    JSONToken.quotedString("\"apples\"", 0, true),
                    "apples",
                    JSONToken.colon(8),
                    JSONToken.number("3.14", 9),
                    13);
            });

            runner.testGroup("getNumberTokenValue()", () ->
            {
                runner.test("with no value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":");
                    test.assertThrows(() -> property.getNumberTokenValue().await(),
                        new NotFoundException("No value was found for the JSONProperty."));
                });

                runner.test("with false token value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":false");
                    test.assertThrows(() -> property.getNumberTokenValue().await(),
                        new WrongTypeException("Expected the value of the property named \"a\" to be a number."));
                });

                runner.test("with object segment value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":{}");
                    test.assertThrows(() -> property.getNumberTokenValue().await(),
                        new WrongTypeException("Expected the value of the property named \"a\" to be a number."));
                });

                runner.test("with number value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":5");
                    test.assertEqual(JSONToken.number("5", 4), property.getNumberTokenValue().await());
                });
            });

            runner.testGroup("getNumberValue()", () ->
            {
                runner.test("with no value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":");
                    test.assertThrows(() -> property.getNumberValue().await(),
                        new NotFoundException("No value was found for the JSONProperty."));
                });

                runner.test("with false token value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":false");
                    test.assertThrows(() -> property.getNumberValue().await(),
                        new WrongTypeException("Expected the value of the property named \"a\" to be a number."));
                });

                runner.test("with object segment value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":{}");
                    test.assertThrows(() -> property.getNumberValue().await(),
                        new WrongTypeException("Expected the value of the property named \"a\" to be a number."));
                });

                runner.test("with number value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":5");
                    test.assertEqual(5.0, property.getNumberValue().await());
                });
            });

            runner.testGroup("getObjectValue()", () ->
            {
                runner.test("with no value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":");
                    test.assertThrows(() -> property.getObjectValue().await(),
                        new NotFoundException("No value was found for the JSONProperty."));
                });

                runner.test("with false token value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":false");
                    test.assertThrows(() -> property.getObjectValue().await(),
                        new WrongTypeException("Expected the value of the property named \"a\" to be an object."));
                });

                runner.test("with array segment value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":[]");
                    test.assertThrows(() -> property.getObjectValue().await(),
                        new WrongTypeException("Expected the value of the property named \"a\" to be an object."));
                });

                runner.test("with object value", (Test test) ->
                {
                    final JSONProperty property = JSON.parseProperty("\"a\":{}");
                    test.assertEqual(JSON.parseObject("{}", 4), property.getObjectValue().await());
                });
            });

            runner.testGroup("equals(Object)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertFalse(propertySegment.equals((Object)null));
                });

                runner.test("with non-JSONProperty", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertFalse(propertySegment.equals((Object)"test"));
                });

                runner.test("with same JSONProperty", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertTrue(propertySegment.equals((Object)propertySegment));
                });

                runner.test("with equal JSONProperty", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertTrue(propertySegment.equals((Object)JSON.parseProperty("\"a\":\"b\"")));
                });

                runner.test("with different JSONProperty", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertFalse(propertySegment.equals((Object)JSON.parseProperty("\"a\":\"c\"")));
                });
            });

            runner.testGroup("equals(JSONProperty)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertFalse(propertySegment.equals((JSONProperty)null));
                });

                runner.test("with same JSONProperty", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertTrue(propertySegment.equals(propertySegment));
                });

                runner.test("with equal JSONProperty", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertTrue(propertySegment.equals(JSON.parseProperty("\"a\":\"b\"")));
                });

                runner.test("with different JSONProperty", (Test test) ->
                {
                    final JSONProperty propertySegment = JSON.parseProperty("\"a\":\"b\"");
                    test.assertFalse(propertySegment.equals(JSON.parseProperty("\"a\":\"c\"")));
                });
            });
        });
    }
}
