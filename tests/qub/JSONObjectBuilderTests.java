package qub;

public interface JSONObjectBuilderTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(JSONObjectBuilder.class, () ->
        {
            runner.testGroup("constructor()", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> new JSONObjectBuilder(null),
                        new PreConditionFailure("stream cannot be null."));
                });

                runner.test("with disposed", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    test.assertTrue(stream.dispose().await());
                    test.assertTrue(stream.isDisposed());

                    test.assertThrows(() -> new JSONObjectBuilder(stream),
                        new PreConditionFailure("stream.isDisposed() cannot be true."));
                });

                runner.test("with non-null and non-disposed", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertFalse(builder.isDisposed());
                    test.assertEqual("{", stream.getText().await());
                });
            });

            runner.testGroup("booleanProperty(String,boolean)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.booleanProperty(null, false),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.booleanProperty("", false),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with false property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.booleanProperty("hello", false);
                    test.assertEqual("{\"hello\":false", stream.getText().await());
                });

                runner.test("with true property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.booleanProperty("hello", true);
                    test.assertEqual("{\"hello\":true", stream.getText().await());
                });
            });

            runner.testGroup("numberProperty(String,long)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.numberProperty(null, 0L),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.numberProperty("", 1L),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with negative property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.numberProperty("hello", -10L);
                    test.assertEqual("{\"hello\":-10", stream.getText().await());
                });

                runner.test("with zero property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.numberProperty("hello", 0L);
                    test.assertEqual("{\"hello\":0", stream.getText().await());
                });

                runner.test("with positive property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.numberProperty("hello", 1230L);
                    test.assertEqual("{\"hello\":1230", stream.getText().await());
                });
            });

            runner.testGroup("numberProperty(String,double)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.numberProperty(null, 0.0),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.numberProperty("", 1.1),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with negative property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.numberProperty("hello", -10.01);
                    test.assertEqual("{\"hello\":-10.01", stream.getText().await());
                });

                runner.test("with zero property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.numberProperty("hello", 0.0);
                    test.assertEqual("{\"hello\":0.0", stream.getText().await());
                });

                runner.test("with positive property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.numberProperty("hello", 1230.56789);
                    test.assertEqual("{\"hello\":1230.56789", stream.getText().await());
                });
            });

            runner.testGroup("stringProperty(String,String)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringProperty(null, "b"),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringProperty("", "b"),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with null property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringProperty("a", null),
                        new PreConditionFailure("propertyValue cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringProperty("a", "");
                    test.assertEqual("{\"a\":\"\"", stream.getText().await());
                });

                runner.test("with non-empty property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringProperty("hello", "there");
                    test.assertEqual("{\"hello\":\"there\"", stream.getText().await());
                });

                runner.test("with single-quote property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringProperty("hello", "'");
                    test.assertEqual("{\"hello\":\"'\"", stream.getText().await());
                });

                runner.test("with single-quoted property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringProperty("hello", "'apples'");
                    test.assertEqual("{\"hello\":\"'apples'\"", stream.getText().await());
                });

                runner.test("with double-quote property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringProperty("hello", "\"");
                    test.assertEqual("{\"hello\":\"\\\"\"", stream.getText().await());
                });

                runner.test("with double-quoted property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringProperty("hello", "\"apples\"");
                    test.assertEqual("{\"hello\":\"\\\"apples\\\"\"", stream.getText().await());
                });

                runner.test("with newline property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringProperty("hello", "\n");
                    test.assertEqual("{\"hello\":\"\\n\"", stream.getText().await());
                });

                runner.test("with carriage-return property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringProperty("hello", "\r");
                    test.assertEqual("{\"hello\":\"\\r\"", stream.getText().await());
                });
            });

            runner.testGroup("nullProperty(String)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.nullProperty(null),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.nullProperty(""),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with non-empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.nullProperty("hello");
                    test.assertEqual("{\"hello\":null", stream.getText().await());
                });
            });

            runner.testGroup("stringOrNullProperty(String,String)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringOrNullProperty(null, "b"),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringOrNullProperty("", "b"),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with null property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertNull(builder.stringOrNullProperty("a", null).await());
                    test.assertEqual("{\"a\":null", stream.getText().await());
                });

                runner.test("with empty property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringOrNullProperty("a", "");
                    test.assertEqual("{\"a\":\"\"", stream.getText().await());
                });

                runner.test("with non-empty property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringOrNullProperty("hello", "there");
                    test.assertEqual("{\"hello\":\"there\"", stream.getText().await());
                });

                runner.test("with single-quote property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringOrNullProperty("hello", "'");
                    test.assertEqual("{\"hello\":\"'\"", stream.getText().await());
                });

                runner.test("with single-quoted property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringOrNullProperty("hello", "'apples'");
                    test.assertEqual("{\"hello\":\"'apples'\"", stream.getText().await());
                });

                runner.test("with double-quote property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringOrNullProperty("hello", "\"");
                    test.assertEqual("{\"hello\":\"\\\"\"", stream.getText().await());
                });

                runner.test("with double-quoted property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringOrNullProperty("hello", "\"apples\"");
                    test.assertEqual("{\"hello\":\"\\\"apples\\\"\"", stream.getText().await());
                });

                runner.test("with newline property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringOrNullProperty("hello", "\n");
                    test.assertEqual("{\"hello\":\"\\n\"", stream.getText().await());
                });

                runner.test("with carriage-return property value", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringOrNullProperty("hello", "\r");
                    test.assertEqual("{\"hello\":\"\\r\"", stream.getText().await());
                });
            });

            runner.testGroup("objectProperty(String)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.objectProperty(null),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.objectProperty(""),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with non-empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.objectProperty("hello");
                    test.assertEqual("{\"hello\":{}", stream.getText().await());
                });
            });

            runner.testGroup("objectProperty(String,Action1<JSONObjectBuilder>)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.objectProperty(null, json -> {}),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.objectProperty("", json -> {}),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with null action", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.objectProperty("hello", null),
                        new PreConditionFailure("action cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty action", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.objectProperty("hello", json -> {});
                    test.assertEqual("{\"hello\":{}", stream.getText().await());
                });

                runner.test("with non-empty action", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.objectProperty("hello", json -> { json.stringProperty("a", "b"); });
                    test.assertEqual("{\"hello\":{\"a\":\"b\"}", stream.getText().await());
                });
            });

            runner.testGroup("arrayProperty(String)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.arrayProperty(null),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.arrayProperty(""),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with non-empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.arrayProperty("hello");
                    test.assertEqual("{\"hello\":[]", stream.getText().await());
                });
            });

            runner.testGroup("arrayProperty(String,Action1<JSONObjectBuilder>)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.arrayProperty(null, json -> {}),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.arrayProperty("", json -> {}),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with null action", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.arrayProperty("hello", null),
                        new PreConditionFailure("action cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty action", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.arrayProperty("hello", json -> {});
                    test.assertEqual("{\"hello\":[]", stream.getText().await());
                });

                runner.test("with non-empty action", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.arrayProperty("hello", json -> { json.stringElement("a"); });
                    test.assertEqual("{\"hello\":[\"a\"]", stream.getText().await());
                });
            });

            runner.testGroup("stringArrayProperty(String)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringArrayProperty(null),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringArrayProperty(""),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with non-empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringArrayProperty("hello");
                    test.assertEqual("{\"hello\":[]", stream.getText().await());
                });
            });

            runner.testGroup("stringArrayProperty(String,Iterable<String>)", () ->
            {
                runner.test("with null property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringArrayProperty(null),
                        new PreConditionFailure("propertyName cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty property name", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringArrayProperty(""),
                        new PreConditionFailure("propertyName cannot be empty."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with null stringElements", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertThrows(() -> builder.stringArrayProperty("hello", null),
                        new PreConditionFailure("stringElements cannot be null."));
                    test.assertEqual("{", stream.getText().await());
                });

                runner.test("with empty stringElements", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringArrayProperty("hello", Iterable.create());
                    test.assertEqual("{\"hello\":[]", stream.getText().await());
                });

                runner.test("with non-empty stringElements", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    builder.stringArrayProperty("hello", Iterable.create("a"));
                    test.assertEqual("{\"hello\":[\"a\"]", stream.getText().await());
                });
            });

            runner.testGroup("dispose()", () ->
            {
                runner.test("when not disposed", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertTrue(builder.dispose().await());
                    test.assertTrue(builder.isDisposed());
                    test.assertEqual("{}", stream.getText().await());
                });

                runner.test("when disposed", (Test test) ->
                {
                    final InMemoryCharacterStream stream = new InMemoryCharacterStream();
                    final JSONObjectBuilder builder = new JSONObjectBuilder(stream);
                    test.assertTrue(builder.dispose().await());
                    test.assertTrue(builder.isDisposed());
                    test.assertEqual("{}", stream.getText().await());

                    test.assertFalse(builder.dispose().await());
                    test.assertTrue(builder.isDisposed());
                    test.assertEqual("{}", stream.getText().await());
                });
            });
        });
    }
}
