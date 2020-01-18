package qub;

public interface JSONPropertyTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(JSONProperty.class, () ->
        {
            runner.testGroup("create(String,boolean)", () ->
            {
                final Action3<String,Boolean,Throwable> createErrorTest = (String propertyName, Boolean propertyValue, Throwable expectedError) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        test.assertThrows(() -> JSONProperty.create(propertyName, propertyValue),
                            expectedError);
                    });
                };

                createErrorTest.run(null, false, new PreConditionFailure("name cannot be null."));
                createErrorTest.run("", false, new PreConditionFailure("name cannot be empty."));

                final Action2<String,Boolean> createTest = (String propertyName, Boolean propertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        final JSONProperty property = JSONProperty.create(propertyName, propertyValue);
                        test.assertEqual(propertyName, property.getName());
                        test.assertEqual(propertyName, property.getKey());
                        test.assertEqual(JSONBoolean.get(propertyValue), property.getValue());
                        test.assertEqual(Strings.quote(propertyName) + ":" + propertyValue, property.toString());
                    });
                };

                createTest.run("a", false);
                createTest.run("bats", true);
            });

            runner.testGroup("create(String,long)", () ->
            {
                final Action3<String,Long,Throwable> createErrorTest = (String propertyName, Long propertyValue, Throwable expectedError) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        test.assertThrows(() -> JSONProperty.create(propertyName, propertyValue),
                            expectedError);
                    });
                };

                createErrorTest.run(null, 10L, new PreConditionFailure("name cannot be null."));
                createErrorTest.run("", 30L, new PreConditionFailure("name cannot be empty."));

                final Action2<String,Long> createTest = (String propertyName, Long propertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        final JSONProperty property = JSONProperty.create(propertyName, propertyValue);
                        test.assertEqual(propertyName, property.getName());
                        test.assertEqual(propertyName, property.getKey());
                        test.assertEqual(JSONNumber.get(propertyValue), property.getValue());
                        test.assertEqual(Strings.quote(propertyName) + ":" + propertyValue, property.toString());
                    });
                };

                createTest.run("a", 700L);
                createTest.run("bats", -20L);
            });

            runner.testGroup("create(String,double)", () ->
            {
                final Action3<String,Double,Throwable> createErrorTest = (String propertyName, Double propertyValue, Throwable expectedError) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        test.assertThrows(() -> JSONProperty.create(propertyName, propertyValue),
                            expectedError);
                    });
                };

                createErrorTest.run(null, 15.0, new PreConditionFailure("name cannot be null."));
                createErrorTest.run("", 0.001, new PreConditionFailure("name cannot be empty."));

                final Action2<String,Double> createTest = (String propertyName, Double propertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        final JSONProperty property = JSONProperty.create(propertyName, propertyValue);
                        test.assertEqual(propertyName, property.getName());
                        test.assertEqual(propertyName, property.getKey());
                        test.assertEqual(JSONNumber.get(propertyValue), property.getValue());
                        test.assertEqual(Strings.quote(propertyName) + ":" + propertyValue, property.toString());
                    });
                };

                createTest.run("a", 1.23);
                createTest.run("bats", 0.0);
            });

            runner.testGroup("create(String,String)", () ->
            {
                final Action3<String,String,Throwable> createErrorTest = (String propertyName, String propertyValue, Throwable expectedError) ->
                {
                    runner.test("with " + English.andList(Iterable.create(propertyName, propertyValue).map(Strings::escapeAndQuote)), (Test test) ->
                    {
                        test.assertThrows(() -> JSONProperty.create(propertyName, propertyValue),
                            expectedError);
                    });
                };

                createErrorTest.run(null, "b", new PreConditionFailure("name cannot be null."));
                createErrorTest.run("", "b", new PreConditionFailure("name cannot be empty."));
                createErrorTest.run("a", null, new PreConditionFailure("value cannot be null."));

                final Action2<String,String> createTest = (String propertyName, String propertyValue) ->
                {
                    runner.test("with " + English.andList(Iterable.create(propertyName, propertyValue).map(Strings::escapeAndQuote)), (Test test) ->
                    {
                        final JSONProperty property = JSONProperty.create(propertyName, propertyValue);
                        test.assertEqual(propertyName, property.getName());
                        test.assertEqual(propertyName, property.getKey());
                        test.assertEqual(JSONString.get(propertyValue), property.getValue());
                        test.assertEqual(Strings.quote(propertyName) + ":" + Strings.quote(propertyValue), property.toString());
                    });
                };

                createTest.run("a", "");
                createTest.run("bats", "yup");
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<JSONProperty,Object,Boolean> equalsTest = (JSONProperty property, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + property + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, property.equals(rhs));
                    });
                };

                equalsTest.run(JSONProperty.create("a", "b"), null, false);
                equalsTest.run(JSONProperty.create("a", "b"), "hello", false);
                equalsTest.run(JSONProperty.create("a", "b"), JSONProperty.create("c", "b"), false);
                equalsTest.run(JSONProperty.create("a", "b"), JSONProperty.create("a", "c"), false);
                equalsTest.run(JSONProperty.create("a", "b"), JSONProperty.create("a", "b"), true);
            });

            runner.testGroup("equals(JSONObjectProperty)", () ->
            {
                final Action3<JSONProperty,JSONProperty,Boolean> equalsTest = (JSONProperty property, JSONProperty rhs, Boolean expected) ->
                {
                    runner.test("with " + property + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, property.equals(rhs));
                    });
                };

                equalsTest.run(JSONProperty.create("a", "b"), null, false);
                equalsTest.run(JSONProperty.create("a", "b"), JSONProperty.create("c", "b"), false);
                equalsTest.run(JSONProperty.create("a", "b"), JSONProperty.create("a", "c"), false);
                equalsTest.run(JSONProperty.create("a", "b"), JSONProperty.create("a", "b"), true);
            });
        });
    }
}
