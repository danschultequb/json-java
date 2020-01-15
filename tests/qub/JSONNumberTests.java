package qub;

public interface JSONNumberTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(JSONNumber.class, () ->
        {
            runner.test("get(long)", (Test test) ->
            {
                final JSONNumber number = JSONNumber.get(513L);
                test.assertEqual(513, number.getValue());
                test.assertEqual("513", number.toString());
            });

            runner.test("get(double)", (Test test) ->
            {
                final JSONNumber number = JSONNumber.get(51.0);
                test.assertEqual(51.0, number.getValue());
                test.assertEqual("51.0", number.toString());
            });

            runner.testGroup("get(String)", () ->
            {
                final Action2<String,Double> getTest = (String text, Double expectedValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final JSONNumber number = JSONNumber.get(text);
                        test.assertEqual(text, number.toString());
                        test.assertEqual(expectedValue, number.getValue());
                    });
                };

                getTest.run("9", 9.0);
                getTest.run("9.0", 9.0);
                getTest.run("-79", -79.0);
                getTest.run("10e0", 10.0);
                getTest.run("10e1", 100.0);
                getTest.run("10E2", 1000.0);
                getTest.run("10e-2", 0.1);
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<JSONNumber,Object,Boolean> equalsTest = (JSONNumber number, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + number + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, number.equals(rhs));
                    });
                };

                equalsTest.run(JSONNumber.get(0), null, false);
                equalsTest.run(JSONNumber.get(0), "hello", false);
                equalsTest.run(JSONNumber.get(0), JSONNumber.get(0), true);
                equalsTest.run(JSONNumber.get(0), JSONNumber.get(-0), true);
                equalsTest.run(JSONNumber.get(0), JSONNumber.get(1), false);
                equalsTest.run(JSONNumber.get(1), JSONNumber.get(1.0), false);
            });

            runner.testGroup("equals(JSONNumber)", () ->
            {
                final Action3<JSONNumber,JSONNumber,Boolean> equalsTest = (JSONNumber number, JSONNumber rhs, Boolean expected) ->
                {
                    runner.test("with " + number + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, number.equals(rhs));
                    });
                };

                equalsTest.run(JSONNumber.get(0), null, false);
                equalsTest.run(JSONNumber.get(0), JSONNumber.get(0), true);
                equalsTest.run(JSONNumber.get(0), JSONNumber.get(-0), true);
                equalsTest.run(JSONNumber.get(0), JSONNumber.get(1), false);
                equalsTest.run(JSONNumber.get(1), JSONNumber.get(1.0), false);
            });
        });
    }
}
