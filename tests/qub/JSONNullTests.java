package qub;

public interface JSONNullTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(JSONNull.class, () ->
        {
            runner.test("segment", (Test test) ->
            {
                test.assertNotNull(JSONNull.segment);
                test.assertEqual("null", JSONNull.segment.toString());
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action2<Object,Boolean> equalsTest = (Object rhs, Boolean expected) ->
                {
                    runner.test("with " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, JSONNull.segment.equals(rhs));
                    });
                };

                equalsTest.run(null, false);
                equalsTest.run("hello", false);
                equalsTest.run(JSONNull.segment, true);
            });
        });
    }
}
