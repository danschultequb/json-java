package qub;

public class JSONArrayTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(JSONArray.class, () ->
        {
            runner.testGroup("constructor()", () ->
            {
                final Action4<String,JSONToken,JSONToken,JSONSegment[]> constructorTest = (String text, JSONToken leftSquareBracket, JSONToken rightSquareBracket, JSONSegment[] elementSegments) ->
                {
                    runner.test("with \"" + text + "\"", (Test test) ->
                    {

                        final JSONArray arraySegment = JSON.parseArray(text);
                        test.assertNotNull(arraySegment);
                        test.assertEqual(leftSquareBracket, arraySegment.getLeftSquareBracket());
                        test.assertEqual(rightSquareBracket, arraySegment.getRightSquareBracket());
                        test.assertEqual(text, arraySegment.toString());
                        test.assertEqual(Array.create(elementSegments), arraySegment.getElements());
                        for (int i = 0; i < elementSegments.length; ++i)
                        {
                            test.assertEqual(elementSegments[i], arraySegment.getElement(i));
                        }
                        test.assertEqual(elementSegments.length, arraySegment.getElementCount());
                        test.assertEqual(0, arraySegment.getStartIndex());
                        test.assertEqual(text.length(), arraySegment.getAfterEndIndex());
                    });
                };

                constructorTest.run("[",
                    JSONToken.leftSquareBracket(0),
                    null,
                    new JSONSegment[0]);

                constructorTest.run("[]",
                    JSONToken.leftSquareBracket(0),
                    JSONToken.rightSquareBracket(1),
                    new JSONSegment[0]);

                constructorTest.run("[ ]",
                    JSONToken.leftSquareBracket(0),
                    JSONToken.rightSquareBracket(2),
                    new JSONSegment[0]);

                constructorTest.run("[0]",
                    JSONToken.leftSquareBracket(0),
                    JSONToken.rightSquareBracket(2),
                    new JSONSegment[]
                        {
                            JSONToken.number("0", 1)
                        });

                constructorTest.run("[0,1]",
                    JSONToken.leftSquareBracket(0),
                    JSONToken.rightSquareBracket(4),
                    new JSONSegment[]
                        {
                            JSONToken.number("0", 1),
                            JSONToken.number("1", 3)
                        });

                constructorTest.run("[,]",
                    JSONToken.leftSquareBracket(0),
                    JSONToken.rightSquareBracket(2),
                    new JSONSegment[]
                        {
                            null,
                            null
                        });

                constructorTest.run("[[]]",
                    JSONToken.leftSquareBracket(0),
                    JSONToken.rightSquareBracket(3),
                    new JSONSegment[]
                        {
                            JSON.parseArray("[]", 1)
                        });
            });
        });
    }
}
