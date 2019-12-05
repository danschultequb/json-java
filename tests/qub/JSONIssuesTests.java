package qub;

public class JSONIssuesTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(JSONIssues.class, () ->
        {
            runner.test("constructor()", test ->
            {
                final JSONIssues issues = new JSONIssues();
                test.assertNotNull(issues);
            });

            runner.test("expectedEndOfFile()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected end of file.", 10, 1),
                    JSONIssues.expectedEndOfFile(10, 1));
            });

            runner.test("missingClosingRightSquareBracket()", test ->
            {
                test.assertEqual(
                    Issue.error("Missing closing right square bracket (']').", 11, 1),
                    JSONIssues.missingClosingRightSquareBracket(11, 1));
            });

            runner.test("expectedCommaOrClosingRightSquareBracket()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected comma (',') or closing right square bracket (']').", 12, 4),
                    JSONIssues.expectedCommaOrClosingRightSquareBracket(12, 4));
            });

            runner.test("expectedArrayElementOrClosingRightSquareBracket()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected array element or closing right square bracket (']').", 13, 1),
                    JSONIssues.expectedArrayElementOrClosingRightSquareBracket(13, 1));
            });

            runner.test("expectedArrayElement()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected array element.", 14, 1),
                    JSONIssues.expectedArrayElement(14, 1));
            });

            runner.test("expectedCommaOrClosingRightCurlyBracket()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected comma (',') or closing right curly bracket ('}').", 15, 3),
                    JSONIssues.expectedCommaOrClosingRightCurlyBracket(15, 3));
            });

            runner.test("expectedPropertyName()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected property name.", 16, 4),
                    JSONIssues.expectedPropertyName(16, 4));
            });

            runner.test("expectedPropertynameOrClosingRightCurlyBracket()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected property name or closing right curly bracket ('}').", 17, 1),
                    JSONIssues.expectedPropertyNameOrClosingRightCurlyBracket(17, 1));
            });

            runner.test("expectedColon()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected colon (':').", 18, 2),
                    JSONIssues.expectedColon(18, 2));
            });

            runner.test("expectedPropertyValue()", test ->
            {
                test.assertEqual(
                    Issue.error("Expected property value.", 19, 1),
                    JSONIssues.expectedPropertyValue(19, 1));
            });
        });
    }
}
