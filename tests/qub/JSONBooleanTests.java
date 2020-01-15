package qub;

public interface JSONBooleanTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(JSONBoolean.class, () ->
        {
            runner.test("trueSegment", (Test test) ->
            {
                test.assertNotNull(JSONBoolean.trueSegment);
                test.assertTrue(JSONBoolean.trueSegment.getValue());
                test.assertEqual("true", JSONBoolean.trueSegment.toString());
            });

            runner.test("falseSegment", (Test test) ->
            {
                test.assertNotNull(JSONBoolean.falseSegment);
                test.assertFalse(JSONBoolean.falseSegment.getValue());
                test.assertEqual("false", JSONBoolean.falseSegment.toString());
            });

            runner.testGroup("get(boolean)", () ->
            {
                runner.test("with true", (Test test) ->
                {
                    test.assertSame(JSONBoolean.trueSegment, JSONBoolean.get(true));
                });

                runner.test("with false", (Test test) ->
                {
                    test.assertSame(JSONBoolean.falseSegment, JSONBoolean.get(false));
                });
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<JSONBoolean,Object,Boolean> equalsTest = (JSONBoolean booleanSegment, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + booleanSegment + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, booleanSegment.equals(rhs));
                    });
                };

                equalsTest.run(JSONBoolean.trueSegment, null, false);
                equalsTest.run(JSONBoolean.trueSegment, "true", false);
                equalsTest.run(JSONBoolean.trueSegment, true, false);
                equalsTest.run(JSONBoolean.trueSegment, JSONBoolean.trueSegment, true);
                equalsTest.run(JSONBoolean.trueSegment, JSONBoolean.falseSegment, false);
            });

            runner.testGroup("equals(JSONBoolean)", () ->
            {
                final Action3<JSONBoolean,JSONBoolean,Boolean> equalsTest = (JSONBoolean booleanSegment, JSONBoolean rhs, Boolean expected) ->
                {
                    runner.test("with " + booleanSegment + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, booleanSegment.equals(rhs));
                    });
                };

                equalsTest.run(JSONBoolean.trueSegment, null, false);
                equalsTest.run(JSONBoolean.trueSegment, JSONBoolean.trueSegment, true);
                equalsTest.run(JSONBoolean.trueSegment, JSONBoolean.falseSegment, false);
            });
        });
    }
}
