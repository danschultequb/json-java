package qub;

public class JSONWriteStreamTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(JSONWriteStream.class, () ->
        {
            runner.test("constructor()", test ->
            {
                final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                test.assertFalse(writeStream.isDisposed());
            });

            runner.test("close()", test ->
            {
                final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);

                try
                {
                    writeStream.close();
                    test.assertTrue(writeStream.isDisposed());
                    test.assertTrue(inMemoryWriteStream.isDisposed());

                    writeStream.close();
                    test.assertTrue(writeStream.isDisposed());
                    test.assertTrue(inMemoryWriteStream.isDisposed());
                }
                catch (Exception e)
                {
                    test.fail(e);
                }
            });

            runner.testGroup("writeBoolean(boolean)", () ->
            {
                runner.test("with false", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeBoolean(false).await();
                    test.assertEqual("false", inMemoryWriteStream.getText().await());
                });

                runner.test("with true", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeBoolean(true).await();
                    test.assertEqual("true", inMemoryWriteStream.getText().await());
                });
            });

            runner.test("writeNull()", test ->
            {
                final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                writeStream.writeNull().await();
                test.assertEqual("null", inMemoryWriteStream.getText().await());
            });

            runner.testGroup("writeNumber(double)", () ->
            {
                runner.test("with negative", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeNumber(-12.3).await();
                    test.assertEqual("-12.3", inMemoryWriteStream.getText().await());
                });

                runner.test("with negative zero", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeNumber(-0.0).await();
                    test.assertEqual("-0.0", inMemoryWriteStream.getText().await());
                });

                runner.test("with positive zero", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeNumber(0.0).await();
                    test.assertEqual("0.0", inMemoryWriteStream.getText().await());
                });

                runner.test("with positive", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeNumber(1234567.8).await();
                    test.assertEqual("1234567.8", inMemoryWriteStream.getText().await());
                });
            });

            runner.testGroup("writeNumber(long)", () ->
            {
                runner.test("with negative", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeNumber(-123).await();
                    test.assertEqual("-123", inMemoryWriteStream.getText().await());
                });

                runner.test("with negative zero", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeNumber(-0).await();
                    test.assertEqual("0", inMemoryWriteStream.getText().await());
                });

                runner.test("with positive zero", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeNumber(0).await();
                    test.assertEqual("0", inMemoryWriteStream.getText().await());
                });

                runner.test("with positive", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeNumber(1234567).await();
                    test.assertEqual("1234567", inMemoryWriteStream.getText().await());
                });
            });

            runner.testGroup("writeQuotedString()", () ->
            {
                runner.test("with null", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeQuotedString(null).await();
                    test.assertEqual("\"\"", inMemoryWriteStream.getText().await());
                });

                runner.test("with empty", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeQuotedString("").await();
                    test.assertEqual("\"\"", inMemoryWriteStream.getText().await());
                });

                runner.test("with non-empty", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeQuotedString("test").await();
                    test.assertEqual("\"test\"", inMemoryWriteStream.getText().await());
                });
            });

            runner.testGroup("writeObject()", () ->
            {
                runner.test("with no action", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject().await();
                    test.assertEqual("{}", inMemoryWriteStream.getText().await());
                });

                runner.test("with null action", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject(null).await();
                    test.assertEqual("{}", inMemoryWriteStream.getText().await());
                });

                runner.test("with empty action", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject((JSONObjectWriteStream objectWriteStream) -> {}).await();
                    test.assertEqual("{}", inMemoryWriteStream.getText().await());
                });

                runner.test("with null property", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject((JSONObjectWriteStream objectWriteStream) ->
                    {
                        objectWriteStream.writeNullProperty("apples");
                    }).await();
                    test.assertEqual("{\"apples\":null}", inMemoryWriteStream.getText().await());
                });

                runner.test("with boolean properties", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject((JSONObjectWriteStream objectWriteStream) ->
                    {
                        objectWriteStream.writeBooleanProperty("apples", false);
                        objectWriteStream.writeBooleanProperty("oranges", true);
                    }).await();
                    test.assertEqual("{\"apples\":false,\"oranges\":true}", inMemoryWriteStream.getText().await());
                });

                runner.test("with quoted string property", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject((JSONObjectWriteStream objectWriteStream) ->
                    {
                        objectWriteStream.writeQuotedStringProperty("apples", "oranges");
                    }).await();
                    test.assertEqual("{\"apples\":\"oranges\"}", inMemoryWriteStream.getText().await());
                });

                runner.test("with integer property", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject((JSONObjectWriteStream objectWriteStream) ->
                    {
                        objectWriteStream.writeNumberProperty("apples", 15);
                    }).await();
                    test.assertEqual("{\"apples\":15}", inMemoryWriteStream.getText().await());
                });

                runner.test("with double property", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject((JSONObjectWriteStream objectWriteStream) ->
                    {
                        objectWriteStream.writeNumberProperty("apples", 15.5);
                    }).await();
                    test.assertEqual("{\"apples\":15.5}", inMemoryWriteStream.getText().await());
                });

                runner.test("with object property", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject((JSONObjectWriteStream objectWriteStream) ->
                    {
                        objectWriteStream.writeObjectProperty("apples", (JSONObjectWriteStream objectWriteStream2) -> {});
                    }).await();
                    test.assertEqual("{\"apples\":{}}", inMemoryWriteStream.getText().await());
                });

                runner.test("with array property", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeObject((JSONObjectWriteStream objectWriteStream) ->
                    {
                        objectWriteStream.writeArrayProperty("apples", (JSONArrayWriteStream arrayWriteStream) -> {});
                    }).await();
                    test.assertEqual("{\"apples\":[]}", inMemoryWriteStream.getText().await());
                });
            });

            runner.testGroup("writeArray()", () ->
            {
                runner.test("with no action", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray().await();
                    test.assertEqual("[]", inMemoryWriteStream.getText().await());
                });

                runner.test("with null action", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray(null).await();
                    test.assertEqual("[]", inMemoryWriteStream.getText().await());
                });

                runner.test("with empty action", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray((JSONArrayWriteStream arrayWriteStream) -> {}).await();
                    test.assertEqual("[]", inMemoryWriteStream.getText().await());
                });

                runner.test("with null element", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray((JSONArrayWriteStream arrayWriteStream) ->
                    {
                        arrayWriteStream.writeNull();
                    }).await();
                    test.assertEqual("[null]", inMemoryWriteStream.getText().await());
                });

                runner.test("with boolean elements", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray((JSONArrayWriteStream arrayWriteStream) ->
                    {
                        arrayWriteStream.writeBoolean(false);
                        arrayWriteStream.writeBoolean(true);
                    }).await();
                    test.assertEqual("[false,true]", inMemoryWriteStream.getText().await());
                });

                runner.test("with quoted string element", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray((JSONArrayWriteStream arrayWriteStream) ->
                    {
                        arrayWriteStream.writeQuotedString("oranges");
                    }).await();
                    test.assertEqual("[\"oranges\"]", inMemoryWriteStream.getText().await());
                });

                runner.test("with integer element", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray((JSONArrayWriteStream arrayWriteStream) ->
                    {
                        arrayWriteStream.writeNumber(15);
                    }).await();
                    test.assertEqual("[15]", inMemoryWriteStream.getText().await());
                });

                runner.test("with double element", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray((JSONArrayWriteStream arrayWriteStream) ->
                    {
                        arrayWriteStream.writeNumber(15.5);
                    }).await();
                    test.assertEqual("[15.5]", inMemoryWriteStream.getText().await());
                });

                runner.test("with object element", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray((JSONArrayWriteStream arrayWriteStream) ->
                    {
                        arrayWriteStream.writeObject((JSONObjectWriteStream objectWriteStream) -> {});
                    }).await();
                    test.assertEqual("[{}]", inMemoryWriteStream.getText().await());
                });

                runner.test("with array element", test ->
                {
                    final InMemoryCharacterStream inMemoryWriteStream = new InMemoryCharacterStream();
                    final JSONWriteStream writeStream = new JSONWriteStream(inMemoryWriteStream);
                    writeStream.writeArray((JSONArrayWriteStream arrayWriteStream) ->
                    {
                        arrayWriteStream.writeArray((JSONArrayWriteStream arrayWriteStream2) -> {});
                    }).await();
                    test.assertEqual("[[]]", inMemoryWriteStream.getText().await());
                });
            });
        });
    }
}
