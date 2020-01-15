package qub;

public interface JSONTokenTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(JSONToken.class, () ->
        {
            runner.testGroup("constructor()", () ->
            {
                runner.test("with null text", (Test test) ->
                {
                    test.assertThrows(() -> new JSONToken(null, JSONTokenType.BlockComment),
                        new PreConditionFailure("text cannot be null."));
                });

                runner.test("with empty text", (Test test) ->
                {
                    test.assertThrows(() -> new JSONToken("", JSONTokenType.BlockComment),
                        new PreConditionFailure("text cannot be empty."));
                });

                runner.test("with null type", (Test test) ->
                {
                    test.assertThrows(() -> new JSONToken("false", null),
                        new PreConditionFailure("type cannot be null."));
                });

                runner.test("with valid values", (Test test) ->
                {
                    final JSONToken token = new JSONToken("false", JSONTokenType.Boolean);
                    test.assertEqual("false", token.getText());
                    test.assertEqual(JSONTokenType.Boolean, token.getType());
                    test.assertEqual("false", token.toString());
                });
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<JSONToken,Object,Boolean> equalsTest = (JSONToken token, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + token + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, token.equals(rhs));
                    });
                };

                equalsTest.run(new JSONToken("123", JSONTokenType.Number), null, false);
                equalsTest.run(new JSONToken("123", JSONTokenType.Number), "oops", false);
                equalsTest.run(new JSONToken("123", JSONTokenType.Number), new JSONToken("12", JSONTokenType.Number), false);
                equalsTest.run(new JSONToken("123", JSONTokenType.Number), new JSONToken("123", JSONTokenType.Whitespace), false);
                equalsTest.run(new JSONToken("123", JSONTokenType.Number), new JSONToken("123", JSONTokenType.Number), true);
            });

            runner.testGroup("equals(JSONToken)", () ->
            {
                final Action3<JSONToken,JSONToken,Boolean> equalsTest = (JSONToken token, JSONToken rhs, Boolean expected) ->
                {
                    runner.test("with " + token + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, token.equals(rhs));
                    });
                };

                equalsTest.run(new JSONToken("123", JSONTokenType.Number), null, false);
                equalsTest.run(new JSONToken("123", JSONTokenType.Number), new JSONToken("12", JSONTokenType.Number), false);
                equalsTest.run(new JSONToken("123", JSONTokenType.Number), new JSONToken("123", JSONTokenType.Whitespace), false);
                equalsTest.run(new JSONToken("123", JSONTokenType.Number), new JSONToken("123", JSONTokenType.Number), true);
            });
        });
    }
}
