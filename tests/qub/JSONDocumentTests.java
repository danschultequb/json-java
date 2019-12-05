package qub;

public interface JSONDocumentTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(JSONDocument.class, () ->
        {
            runner.testGroup("constructor()", () ->
            {
                final Action2<String,JSONSegment> constructorTest = (String text, JSONSegment expectedRoot) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final JSONDocument document = JSON.parse(text);
                        test.assertEqual(text, document.toString());
                        test.assertEqual(text.length(), document.getLength());
                    });
                };

                constructorTest.run("", null);
                constructorTest.run("// hello", null);
                constructorTest.run("/* there */", null);
                constructorTest.run("\"fever\"", JSONToken.quotedString("\"fever\"", 0, true));
                constructorTest.run("20", JSONToken.number("20"));
                constructorTest.run("[0, 1, 2, 3, 4]", JSON.parseArray("[0, 1, 2, 3, 4]"));
                constructorTest.run("{}", JSON.parseObject("{}"));
            });

            runner.testGroup("getRoot()", () ->
            {
                final Action3<String,JSONSegment,Throwable> getRootTest = (String text, JSONSegment expectedRoot, Throwable expectedError) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final JSONDocument document = JSON.parse(text);
                        final Result<JSONSegment> rootResult = document.getRoot();
                        if (expectedError != null)
                        {
                            test.assertThrows(rootResult::await, expectedError);
                        }
                        else
                        {
                            test.assertEqual(expectedRoot, rootResult.await());
                        }
                    });
                };

                getRootTest.run("", null, new NotFoundException("No root was found in the JSON document."));
                getRootTest.run("// hello", null, new NotFoundException("No root was found in the JSON document."));
                getRootTest.run("/* there */", null, new NotFoundException("No root was found in the JSON document."));
                getRootTest.run("\"fever\"", null, new NotFoundException("No root was found in the JSON document."));
                getRootTest.run("20", null, new NotFoundException("No root was found in the JSON document."));
                getRootTest.run("[0, 1, 2, 3, 4]", JSON.parseArray("[0, 1, 2, 3, 4]"), null);
                getRootTest.run("{}", JSON.parseObject("{}"), null);
            });

            runner.testGroup("getRootArray()", () ->
            {
                final Action3<String,JSONArray,Throwable> getRootArrayTest = (String text, JSONArray expectedRoot, Throwable expectedError) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final JSONDocument document = JSON.parse(text);
                        final Result<JSONArray> rootArrayResult = document.getRootArray();
                        if (expectedError != null)
                        {
                            test.assertThrows(rootArrayResult::await, expectedError);
                        }
                        else
                        {
                            test.assertEqual(expectedRoot, rootArrayResult.await());
                        }
                    });
                };

                getRootArrayTest.run("", null, new NotFoundException("No root was found in the JSON document."));
                getRootArrayTest.run("// hello", null, new NotFoundException("No root was found in the JSON document."));
                getRootArrayTest.run("/* there */", null, new NotFoundException("No root was found in the JSON document."));
                getRootArrayTest.run("\"fever\"", null, new NotFoundException("No root was found in the JSON document."));
                getRootArrayTest.run("20", null, new NotFoundException("No root was found in the JSON document."));
                getRootArrayTest.run("[0, 1, 2, 3, 4]", JSON.parseArray("[0, 1, 2, 3, 4]"), null);
                getRootArrayTest.run("{}", null, new WrongTypeException("Expected the root of the JSON document to be an array."));
            });

            runner.testGroup("getRootObject()", () ->
            {
                final Action3<String,JSONObject,Throwable> getRootObjectTest = (String text, JSONObject expectedRoot, Throwable expectedError) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final JSONDocument document = JSON.parse(text);
                        final Result<JSONObject> rootObjectResult = document.getRootObject();
                        if (expectedError != null)
                        {
                            test.assertThrows(rootObjectResult::await, expectedError);
                        }
                        else
                        {
                            test.assertEqual(expectedRoot, rootObjectResult.await());
                        }
                    });
                };

                getRootObjectTest.run("", null, new NotFoundException("No root was found in the JSON document."));
                getRootObjectTest.run("// hello", null, new NotFoundException("No root was found in the JSON document."));
                getRootObjectTest.run("/* there */", null, new NotFoundException("No root was found in the JSON document."));
                getRootObjectTest.run("\"fever\"", null, new NotFoundException("No root was found in the JSON document."));
                getRootObjectTest.run("20", null, new NotFoundException("No root was found in the JSON document."));
                getRootObjectTest.run("[0, 1, 2, 3, 4]", null, new WrongTypeException("Expected the root of the JSON document to be an object."));
                getRootObjectTest.run("{}", JSON.parseObject("{}"), null);
            });
        });
    }
}
