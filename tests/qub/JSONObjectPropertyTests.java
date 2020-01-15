package qub;

public interface JSONObjectPropertyTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(JSONObjectProperty.class, () ->
        {
            runner.testGroup("create(String,boolean)", () ->
            {
                final Action3<String,Boolean,Throwable> createErrorTest = (String propertyName, Boolean propertyValue, Throwable expectedError) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        test.assertThrows(() -> JSONObjectProperty.create(propertyName, propertyValue),
                            expectedError);
                    });
                };

                createErrorTest.run(null, false, new PreConditionFailure("name cannot be null."));
                createErrorTest.run("", false, new PreConditionFailure("name cannot be empty."));

                final Action2<String,Boolean> createTest = (String propertyName, Boolean propertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        final JSONObjectProperty property = JSONObjectProperty.create(propertyName, propertyValue);
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
                        test.assertThrows(() -> JSONObjectProperty.create(propertyName, propertyValue),
                            expectedError);
                    });
                };

                createErrorTest.run(null, 10L, new PreConditionFailure("name cannot be null."));
                createErrorTest.run("", 30L, new PreConditionFailure("name cannot be empty."));

                final Action2<String,Long> createTest = (String propertyName, Long propertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        final JSONObjectProperty property = JSONObjectProperty.create(propertyName, propertyValue);
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
                        test.assertThrows(() -> JSONObjectProperty.create(propertyName, propertyValue),
                            expectedError);
                    });
                };

                createErrorTest.run(null, 15.0, new PreConditionFailure("name cannot be null."));
                createErrorTest.run("", 0.001, new PreConditionFailure("name cannot be empty."));

                final Action2<String,Double> createTest = (String propertyName, Double propertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(propertyName) + " and " + propertyValue, (Test test) ->
                    {
                        final JSONObjectProperty property = JSONObjectProperty.create(propertyName, propertyValue);
                        test.assertEqual(propertyName, property.getName());
                        test.assertEqual(propertyName, property.getKey());
                        test.assertEqual(JSONNumber.get(propertyValue), property.getValue());
                        test.assertEqual(Strings.quote(propertyName) + ":" + propertyValue, property.toString());
                    });
                };

                createTest.run("a", 1.23);
                createTest.run("bats", 0.0);
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<JSONObjectProperty,Object,Boolean> equalsTest = (JSONObjectProperty property, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + property + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, property.equals(rhs));
                    });
                };

                equalsTest.run(JSONObjectProperty.create("a", "b"), null, false);
                equalsTest.run(JSONObjectProperty.create("a", "b"), "hello", false);
                equalsTest.run(JSONObjectProperty.create("a", "b"), JSONObjectProperty.create("c", "b"), false);
                equalsTest.run(JSONObjectProperty.create("a", "b"), JSONObjectProperty.create("a", "c"), false);
                equalsTest.run(JSONObjectProperty.create("a", "b"), JSONObjectProperty.create("a", "b"), true);
            });

            runner.testGroup("equals(JSONObjectProperty)", () ->
            {
                final Action3<JSONObjectProperty,JSONObjectProperty,Boolean> equalsTest = (JSONObjectProperty property, JSONObjectProperty rhs, Boolean expected) ->
                {
                    runner.test("with " + property + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, property.equals(rhs));
                    });
                };

                equalsTest.run(JSONObjectProperty.create("a", "b"), null, false);
                equalsTest.run(JSONObjectProperty.create("a", "b"), JSONObjectProperty.create("c", "b"), false);
                equalsTest.run(JSONObjectProperty.create("a", "b"), JSONObjectProperty.create("a", "c"), false);
                equalsTest.run(JSONObjectProperty.create("a", "b"), JSONObjectProperty.create("a", "b"), true);
            });
        });
    }
}
