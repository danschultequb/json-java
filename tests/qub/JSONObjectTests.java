package qub;

public interface JSONObjectTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(JSONObject.class, () ->
        {
            runner.testGroup("create(JSONObjectProperty...)", () ->
            {
                runner.test("with no arguments", (Test test) ->
                {
                    final JSONObject object = JSONObject.create();
                    test.assertNotNull(object);
                    test.assertEqual(Iterable.create(), object.getProperties());
                    test.assertEqual(Iterable.create(), object.getPropertyNames());
                    test.assertEqual(Iterable.create(), object.getPropertyValues());
                    test.assertEqual(Iterable.create(), object.iterate().toList());
                    test.assertEqual(object.getPropertyNames(), object.getKeys());
                    test.assertEqual(object.getPropertyValues(), object.getValues());
                    test.assertEqual("{}", object.toString());
                });

                runner.test("with one property", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(JSONProperty.create("hello", "there"));
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create().setString("hello", "there"), object);
                    test.assertEqual(Iterable.create(JSONProperty.create("hello", "there")), object.getProperties());
                    test.assertEqual(Iterable.create("hello"), object.getPropertyNames());
                    test.assertEqual(Iterable.create(JSONString.get("there")), object.getPropertyValues());
                    test.assertEqual(Iterable.create(MapEntry.create("hello", JSONString.get("there"))), object.iterate().toList());
                    test.assertEqual(object.getPropertyNames(), object.getKeys());
                    test.assertEqual(object.getPropertyValues(), object.getValues());
                    test.assertEqual("{\"hello\":\"there\"}", object.toString());
                });

                runner.test("with multiple properties", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(
                        JSONProperty.create("hello", "there"),
                        JSONProperty.create("fun", true),
                        JSONProperty.create("work", JSONNull.segment));
                    test.assertNotNull(object);
                    test.assertEqual(
                        JSONObject.create()
                            .setString("hello", "there")
                            .setBoolean("fun", true)
                            .set("work", JSONNull.segment),
                        object);
                    test.assertEqual(
                        Iterable.create(
                            JSONProperty.create("hello", "there"),
                            JSONProperty.create("fun", true),
                            JSONProperty.create("work", JSONNull.segment)),
                        object.getProperties());
                    test.assertEqual(Iterable.create("hello", "fun", "work"), object.getPropertyNames());
                    test.assertEqual(
                        Iterable.create(
                            JSONString.get("there"),
                            JSONBoolean.trueSegment,
                            JSONNull.segment),
                        object.getPropertyValues());
                    test.assertEqual(object.getPropertyNames(), object.getKeys());
                    test.assertEqual(object.getPropertyValues(), object.getValues());
                    test.assertEqual("{\"hello\":\"there\",\"fun\":true,\"work\":null}", object.toString());
                });

                runner.test("with null array", (Test test) ->
                {
                    test.assertThrows(() -> JSONObject.create((JSONProperty[])null),
                        new PreConditionFailure("properties cannot be null."));
                });

                runner.test("with empty array", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(new JSONProperty[0]);
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create(), object);
                    test.assertEqual("{}", object.toString());
                });

                runner.test("with one property array", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(new JSONProperty[] { JSONProperty.create("hello", "there") });
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create().setString("hello", "there"), object);
                    test.assertEqual("{\"hello\":\"there\"}", object.toString());
                });

                runner.test("with multiple property array", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(new JSONProperty[]
                    {
                        JSONProperty.create("hello", "there"),
                        JSONProperty.create("fun", true),
                        JSONProperty.create("work", JSONNull.segment)
                    });

                    test.assertNotNull(object);
                    test.assertEqual(
                        JSONObject.create()
                            .setString("hello", "there")
                            .setBoolean("fun", true)
                            .set("work", JSONNull.segment),
                        object);
                    test.assertEqual("{\"hello\":\"there\",\"fun\":true,\"work\":null}", object.toString());
                });
            });

            runner.testGroup("create(Iterable<JSONObjectProperty>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSONObject.create((Iterable<JSONProperty>)null),
                        new PreConditionFailure("properties cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(Iterable.create());
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create(), object);
                    test.assertEqual(Iterable.create(), object.getProperties());
                    test.assertEqual("{}", object.toString());
                });

                runner.test("with one property", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(Iterable.create(JSONProperty.create("hello", "there")));
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create().setString("hello", "there"), object);
                    test.assertEqual(Iterable.create(JSONProperty.create("hello", "there")), object.getProperties());
                    test.assertEqual("{\"hello\":\"there\"}", object.toString());
                });

                runner.test("with multiple properties", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(Iterable.create(
                        JSONProperty.create("hello", "there"),
                        JSONProperty.create("fun", true),
                        JSONProperty.create("work", JSONNull.segment)));
                    test.assertNotNull(object);
                    test.assertEqual(
                        JSONObject.create()
                            .setString("hello", "there")
                            .setBoolean("fun", true)
                            .set("work", JSONNull.segment),
                        object);
                    test.assertEqual(
                        Iterable.create(
                            JSONProperty.create("hello", "there"),
                            JSONProperty.create("fun", true),
                            JSONProperty.create("work", JSONNull.segment)),
                        object.getProperties());
                    test.assertEqual("{\"hello\":\"there\",\"fun\":true,\"work\":null}", object.toString());
                });
            });

            runner.testGroup("getProperty(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getPropertyErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getProperty(propertyName).await(), expectedError);
                    });
                };

                getPropertyErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getPropertyErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getPropertyErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getPropertyErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));

                final Action3<JSONObject,String,JSONProperty> getPropertyTest = (JSONObject object, String propertyName, JSONProperty expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getProperty(propertyName).await());
                    });
                };

                getPropertyTest.run(JSONObject.create().setNumber("a", 1), "a", JSONProperty.create("a", 1));
                getPropertyTest.run(JSONObject.create().setString("bats", "hello"), "bats", JSONProperty.create("bats", "hello"));
            });

            runner.testGroup("get(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.get(propertyName).await(), expectedError);
                    });
                };

                getErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));

                final Action3<JSONObject,String,JSONSegment> getTest = (JSONObject object, String propertyName, JSONSegment expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.get(propertyName).await());
                    });
                };

                getTest.run(JSONObject.create().setNumber("a", 1), "a", JSONNumber.get(1));
                getTest.run(JSONObject.create().setString("bats", "hello"), "bats", JSONString.get("hello"));
            });

            runner.testGroup("getObject(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getObjectErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getObject(propertyName).await(), expectedError);
                    });
                };

                getObjectErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getObjectErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getObjectErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getObjectErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getObjectErrorTest.run(JSONObject.create().setNumber("a", 1), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONObject, but was a JSONNumber instead."));
                getObjectErrorTest.run(JSONObject.create().set("a", JSONArray.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONObject, but was a JSONArray instead."));
                getObjectErrorTest.run(JSONObject.create().set("a", JSONNull.segment), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONObject, but was a JSONNull instead."));

                final Action3<JSONObject,String,JSONObject> getObjectTest = (JSONObject object, String propertyName, JSONObject expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getObject(propertyName).await());
                    });
                };

                getObjectTest.run(JSONObject.create().set("a", JSONObject.create()), "a", JSONObject.create());
            });

            runner.testGroup("getObjectOrNull(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getObjectOrNullErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getObjectOrNull(propertyName).await(), expectedError);
                    });
                };

                getObjectOrNullErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getObjectOrNullErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getObjectOrNullErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getObjectOrNullErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getObjectOrNullErrorTest.run(JSONObject.create().setNumber("a", 1), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONObject or JSONNull, but was a JSONNumber instead."));
                getObjectOrNullErrorTest.run(JSONObject.create().set("a", JSONArray.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONObject or JSONNull, but was a JSONArray instead."));

                final Action3<JSONObject,String,JSONSegment> getObjectOrNullTest = (JSONObject object, String propertyName, JSONSegment expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getObjectOrNull(propertyName).await());
                    });
                };

                getObjectOrNullTest.run(JSONObject.create().set("a", JSONObject.create()), "a", JSONObject.create());
                getObjectOrNullTest.run(JSONObject.create().set("a", JSONNull.segment), "a", null);
            });

            runner.testGroup("getOrCreateObject(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getOrCreateObjectErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + English.andList(object, Strings.escapeAndQuote(propertyName)), (Test test) ->
                    {
                        test.assertThrows(() -> object.getOrCreateObject(propertyName).await(), expectedError);
                    });
                };

                getOrCreateObjectErrorTest.run(
                    JSONObject.create(),
                    null,
                    new PreConditionFailure("propertyName cannot be null."));
                getOrCreateObjectErrorTest.run(
                    JSONObject.create(),
                    "",
                    new PreConditionFailure("propertyName cannot be empty."));
                getOrCreateObjectErrorTest.run(
                    JSONObject.create()
                        .setBoolean("a", true),
                    "a",
                    new WrongTypeException("Expected the property named \"a\" to be a JSONObject, but was a JSONBoolean instead."));

                final Action3<JSONObject,String,JSONObject> getOrCreateObjectTest = (JSONObject object, String propertyName, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, Strings.escapeAndQuote(propertyName)), (Test test) ->
                    {
                        test.assertEqual(expected, object.getOrCreateObject(propertyName).await());
                        test.assertEqual(expected, object.getObject(propertyName).await());
                    });
                };

                getOrCreateObjectTest.run(
                    JSONObject.create(),
                    "a",
                    JSONObject.create());
                getOrCreateObjectTest.run(
                    JSONObject.create()
                        .setObject("a", JSONObject.create().setBoolean("b", true)),
                    "a",
                    JSONObject.create().setBoolean("b", true));
            });

            runner.testGroup("getArray(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getArrayErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getArray(propertyName).await(), expectedError);
                    });
                };

                getArrayErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getArrayErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getArrayErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getArrayErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getArrayErrorTest.run(JSONObject.create().setNumber("a", 1), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONArray, but was a JSONNumber instead."));
                getArrayErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONArray, but was a JSONObject instead."));
                getArrayErrorTest.run(JSONObject.create().set("a", JSONNull.segment), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONArray, but was a JSONNull instead."));

                final Action3<JSONObject,String,JSONArray> getArrayTest = (JSONObject object, String propertyName, JSONArray expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getArray(propertyName).await());
                    });
                };

                getArrayTest.run(JSONObject.create().set("a", JSONArray.create()), "a", JSONArray.create());
            });

            runner.testGroup("getArrayOrNull(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getArrayOrNullErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getArrayOrNull(propertyName).await(), expectedError);
                    });
                };

                getArrayOrNullErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getArrayOrNullErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getArrayOrNullErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getArrayOrNullErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getArrayOrNullErrorTest.run(JSONObject.create().setNumber("a", 1), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONArray or JSONNull, but was a JSONNumber instead."));
                getArrayOrNullErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONArray or JSONNull, but was a JSONObject instead."));

                final Action3<JSONObject,String,JSONArray> getArrayOrNullTest = (JSONObject object, String propertyName, JSONArray expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getArrayOrNull(propertyName).await());
                    });
                };

                getArrayOrNullTest.run(JSONObject.create().set("a", JSONArray.create()), "a", JSONArray.create());
                getArrayOrNullTest.run(JSONObject.create().set("a", JSONNull.segment), "a", null);
            });

            runner.testGroup("getOrCreateArray(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getOrCreateArrayErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + English.andList(object, Strings.escapeAndQuote(propertyName)), (Test test) ->
                    {
                        test.assertThrows(() -> object.getOrCreateArray(propertyName).await(), expectedError);
                    });
                };

                getOrCreateArrayErrorTest.run(
                    JSONObject.create(),
                    null,
                    new PreConditionFailure("propertyName cannot be null."));
                getOrCreateArrayErrorTest.run(
                    JSONObject.create(),
                    "",
                    new PreConditionFailure("propertyName cannot be empty."));
                getOrCreateArrayErrorTest.run(
                    JSONObject.create()
                        .setBoolean("a", true),
                    "a",
                    new WrongTypeException("Expected the property named \"a\" to be a JSONArray, but was a JSONBoolean instead."));

                final Action3<JSONObject,String,JSONArray> getOrCreateArrayTest = (JSONObject object, String propertyName, JSONArray expected) ->
                {
                    runner.test("with " + English.andList(object, Strings.escapeAndQuote(propertyName)), (Test test) ->
                    {
                        test.assertEqual(expected, object.getOrCreateArray(propertyName).await());
                        test.assertEqual(expected, object.getArray(propertyName).await());
                    });
                };

                getOrCreateArrayTest.run(
                    JSONObject.create(),
                    "a",
                    JSONArray.create());
                getOrCreateArrayTest.run(
                    JSONObject.create()
                        .setArray("a", JSONArray.create(JSONBoolean.trueSegment)),
                    "a",
                    JSONArray.create(JSONBoolean.trueSegment));
            });

            runner.testGroup("getBoolean(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getBooleanErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getBoolean(propertyName).await(), expectedError);
                    });
                };

                getBooleanErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getBooleanErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getBooleanErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getBooleanErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getBooleanErrorTest.run(JSONObject.create().setNumber("a", 1), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONBoolean, but was a JSONNumber instead."));
                getBooleanErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONBoolean, but was a JSONObject instead."));
                getBooleanErrorTest.run(JSONObject.create().setString("a", "false"), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONBoolean, but was a JSONString instead."));
                getBooleanErrorTest.run(JSONObject.create().set("a", JSONNull.segment), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONBoolean, but was a JSONNull instead."));

                final Action3<JSONObject,String,Boolean> getBooleanTest = (JSONObject object, String propertyName, Boolean expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getBoolean(propertyName).await());
                    });
                };

                getBooleanTest.run(JSONObject.create().setBoolean("a", false), "a", false);
                getBooleanTest.run(JSONObject.create().setBoolean("a", true), "a", true);
            });

            runner.testGroup("getBooleanOrNull(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getBooleanOrNullErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getBooleanOrNull(propertyName).await(), expectedError);
                    });
                };

                getBooleanOrNullErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getBooleanOrNullErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getBooleanOrNullErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getBooleanOrNullErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getBooleanOrNullErrorTest.run(JSONObject.create().setNumber("a", 1), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONBoolean or JSONNull, but was a JSONNumber instead."));
                getBooleanOrNullErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONBoolean or JSONNull, but was a JSONObject instead."));
                getBooleanOrNullErrorTest.run(JSONObject.create().setString("a", "false"), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONBoolean or JSONNull, but was a JSONString instead."));

                final Action3<JSONObject,String,Boolean> getBooleanOrNullTest = (JSONObject object, String propertyName, Boolean expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getBooleanOrNull(propertyName).await());
                    });
                };

                getBooleanOrNullTest.run(JSONObject.create().setBoolean("a", false), "a", false);
                getBooleanOrNullTest.run(JSONObject.create().setBoolean("a", true), "a", true);
                getBooleanOrNullTest.run(JSONObject.create().set("a", JSONNull.segment), "a", null);
            });

            runner.testGroup("getString(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getStringErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getString(propertyName).await(), expectedError);
                    });
                };

                getStringErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getStringErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getStringErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getStringErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getStringErrorTest.run(JSONObject.create().setNumber("a", 1), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString, but was a JSONNumber instead."));
                getStringErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString, but was a JSONObject instead."));
                getStringErrorTest.run(JSONObject.create().set("a", JSONArray.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString, but was a JSONArray instead."));
                getStringErrorTest.run(JSONObject.create().setBoolean("a", false), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString, but was a JSONBoolean instead."));
                getStringErrorTest.run(JSONObject.create().set("a", JSONNull.segment), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString, but was a JSONNull instead."));

                final Action3<JSONObject,String,String> getStringTest = (JSONObject object, String propertyName, String expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getString(propertyName).await());
                    });
                };

                getStringTest.run(JSONObject.create().setString("a", ""), "a", "");
                getStringTest.run(JSONObject.create().setString("a", "hello"), "a", "hello");
            });

            runner.testGroup("getStringOrNull(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getStringOrNullErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getStringOrNull(propertyName).await(), expectedError);
                    });
                };

                getStringOrNullErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getStringOrNullErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getStringOrNullErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getStringOrNullErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getStringOrNullErrorTest.run(JSONObject.create().setNumber("a", 1), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString or JSONNull, but was a JSONNumber instead."));
                getStringOrNullErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString or JSONNull, but was a JSONObject instead."));
                getStringOrNullErrorTest.run(JSONObject.create().set("a", JSONArray.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString or JSONNull, but was a JSONArray instead."));
                getStringOrNullErrorTest.run(JSONObject.create().setBoolean("a", false), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONString or JSONNull, but was a JSONBoolean instead."));

                final Action3<JSONObject,String,String> getStringOrNullTest = (JSONObject object, String propertyName, String expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getStringOrNull(propertyName).await());
                    });
                };

                getStringOrNullTest.run(JSONObject.create().setString("a", ""), "a", "");
                getStringOrNullTest.run(JSONObject.create().setString("a", "hello"), "a", "hello");
                getStringOrNullTest.run(JSONObject.create().set("a", JSONNull.segment), "a", null);
            });

            runner.testGroup("getNumber(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getNumberErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getNumber(propertyName).await(), expectedError);
                    });
                };

                getNumberErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getNumberErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getNumberErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getNumberErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getNumberErrorTest.run(JSONObject.create().setString("a", "hello"), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber, but was a JSONString instead."));
                getNumberErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber, but was a JSONObject instead."));
                getNumberErrorTest.run(JSONObject.create().set("a", JSONArray.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber, but was a JSONArray instead."));
                getNumberErrorTest.run(JSONObject.create().setBoolean("a", false), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber, but was a JSONBoolean instead."));
                getNumberErrorTest.run(JSONObject.create().set("a", JSONNull.segment), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber, but was a JSONNull instead."));

                final Action3<JSONObject,String,Double> getNumberTest = (JSONObject object, String propertyName, Double expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getNumber(propertyName).await());
                    });
                };

                getNumberTest.run(JSONObject.create().setNumber("a", 5), "a", 5.0);
                getNumberTest.run(JSONObject.create().setNumber("a", -20), "a", -20.0);
            });

            runner.testGroup("getNumberOrNull(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getNumberOrNullErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getNumberOrNull(propertyName).await(), expectedError);
                    });
                };

                getNumberOrNullErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getNumberOrNullErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getNumberOrNullErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getNumberOrNullErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getNumberOrNullErrorTest.run(JSONObject.create().setString("a", "hello"), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber or JSONNull, but was a JSONString instead."));
                getNumberOrNullErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber or JSONNull, but was a JSONObject instead."));
                getNumberOrNullErrorTest.run(JSONObject.create().set("a", JSONArray.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber or JSONNull, but was a JSONArray instead."));
                getNumberOrNullErrorTest.run(JSONObject.create().setBoolean("a", false), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNumber or JSONNull, but was a JSONBoolean instead."));

                final Action3<JSONObject,String,Double> getNumberOrNullTest = (JSONObject object, String propertyName, Double expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getNumberOrNull(propertyName).await());
                    });
                };

                getNumberOrNullTest.run(JSONObject.create().setNumber("a", 5), "a", 5.0);
                getNumberOrNullTest.run(JSONObject.create().setNumber("a", -20), "a", -20.0);
                getNumberOrNullTest.run(JSONObject.create().set("a", JSONNull.segment), "a", null);
            });

            runner.testGroup("getNull(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> getNullErrorTest = (JSONObject object, String propertyName, Throwable expectedError) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.getNull(propertyName).await(), expectedError);
                    });
                };

                getNullErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                getNullErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                getNullErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property found with the name: \"a\""));
                getNullErrorTest.run(JSONObject.create().setNumber("A", 1), "a", new NotFoundException("No property found with the name: \"a\""));
                getNullErrorTest.run(JSONObject.create().setString("a", "hello"), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNull, but was a JSONString instead."));
                getNullErrorTest.run(JSONObject.create().set("a", JSONObject.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNull, but was a JSONObject instead."));
                getNullErrorTest.run(JSONObject.create().set("a", JSONArray.create()), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNull, but was a JSONArray instead."));
                getNullErrorTest.run(JSONObject.create().setBoolean("a", false), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNull, but was a JSONBoolean instead."));
                getNullErrorTest.run(JSONObject.create().setNumber("a", 40), "a", new WrongTypeException("Expected the property named \"a\" to be a JSONNull, but was a JSONNumber instead."));

                final Action3<JSONObject,String,Double> getNullTest = (JSONObject object, String propertyName, Double expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getNull(propertyName).await());
                    });
                };

                getNullTest.run(JSONObject.create().set("a", JSONNull.segment), "a", null);
            });

            runner.testGroup("set(MapEntry<String,JSONSegment>)", () ->
            {
                final Action3<JSONObject,JSONProperty,Throwable> setErrorTest = (JSONObject object, JSONProperty property, Throwable expected) ->
                {
                    runner.test("with " + object + " and " + property, (Test test) ->
                    {
                        test.assertThrows(() -> object.set(property), expected);
                    });
                };

                setErrorTest.run(JSONObject.create(), null, new PreConditionFailure("property cannot be null."));

                final Action3<JSONObject,JSONProperty,JSONObject> setTest = (JSONObject object, JSONProperty property, JSONObject expected) ->
                {
                    runner.test("with " + object + " and " + property, (Test test) ->
                    {
                        final JSONObject setResult = object.set(property);
                        test.assertSame(object, setResult);
                        test.assertEqual(expected, object);
                        test.assertTrue(object.containsKey(property.getName()));
                        test.assertTrue(object.contains(property.getName()));
                    });
                };

                setTest.run(JSONObject.create(), JSONProperty.create("a", 1), JSONObject.create().setNumber("a", 1));
                setTest.run(JSONObject.create().setNumber("a", 1), JSONProperty.create("a", 2), JSONObject.create().setNumber("a", 2));
                setTest.run(JSONObject.create().setNumber("a", 1), JSONProperty.create("a", false), JSONObject.create().setBoolean("a", false));
                setTest.run(JSONObject.create().setNumber("a", 1), JSONProperty.create("a", JSONNull.segment), JSONObject.create().set("a", JSONNull.segment));
            });

            runner.testGroup("setAll(Iterable<MapEntry<String,JSONSegment>>)", () ->
            {
                final Action3<JSONObject,Iterable<MapEntry<String,JSONSegment>>,Throwable> setAllErrorTest = (JSONObject object, Iterable<MapEntry<String,JSONSegment>> properties, Throwable expected) ->
                {
                    runner.test("with " + object + " and " + properties, (Test test) ->
                    {
                        test.assertThrows(() -> object.setAll(properties), expected);
                    });
                };

                setAllErrorTest.run(JSONObject.create(), null, new PreConditionFailure("properties cannot be null."));

                final Action3<JSONObject,Iterable<MapEntry<String,JSONSegment>>,JSONObject> setAllTest = (JSONObject object, Iterable<MapEntry<String,JSONSegment>> properties, JSONObject expected) ->
                {
                    runner.test("with " + object + " and " + properties, (Test test) ->
                    {
                        final JSONObject setResult = object.setAll(properties);
                        test.assertSame(object, setResult);
                        test.assertEqual(expected, object);
                        for (final MapEntry<String,JSONSegment> property : properties)
                        {
                            test.assertTrue(object.containsKey(property.getKey()));
                            test.assertTrue(object.contains(property.getKey()));
                        }
                    });
                };

                setAllTest.run(JSONObject.create(), Iterable.create(JSONProperty.create("a", 1)), JSONObject.create().setNumber("a", 1));
                setAllTest.run(JSONObject.create().setNumber("a", 1), Iterable.create(JSONProperty.create("a", 2)), JSONObject.create().setNumber("a", 2));
                setAllTest.run(JSONObject.create().setNumber("a", 1), Iterable.create(JSONProperty.create("a", false)), JSONObject.create().setBoolean("a", false));
                setAllTest.run(JSONObject.create().setNumber("a", 1), Iterable.create(JSONProperty.create("a", JSONNull.segment)), JSONObject.create().set("a", JSONNull.segment));
            });

            runner.testGroup("setBoolean(String,boolean)", () ->
            {
                final Action4<JSONObject,String,Boolean,Throwable> setBooleanErrorTest = (JSONObject object, String propertyName, Boolean propertyValue, Throwable expected) ->
                {
                    runner.test("with " + object + ", " + Strings.escapeAndQuote(propertyName) + ", and " + propertyValue, (Test test) ->
                    {
                        test.assertThrows(() -> object.setBoolean(propertyName, propertyValue), expected);
                    });
                };

                setBooleanErrorTest.run(JSONObject.create(), null, false, new PreConditionFailure("propertyName cannot be null."));
                setBooleanErrorTest.run(JSONObject.create(), "", false, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Boolean,JSONObject> setBooleanTest = (JSONObject object, String propertyName, Boolean propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + object + ", " + Strings.escapeAndQuote(propertyName) + ", and " + propertyValue, (Test test) ->
                    {
                        final JSONObject setResult = object.setBoolean(propertyName, propertyValue);
                        test.assertSame(object, setResult);
                        test.assertEqual(expected, object);
                        test.assertTrue(object.containsKey(propertyName));
                        test.assertTrue(object.contains(propertyName));
                    });
                };

                setBooleanTest.run(JSONObject.create(), "a", false, JSONObject.create().setBoolean("a", false));
                setBooleanTest.run(JSONObject.create().setBoolean("a", false), "a", true, JSONObject.create().setBoolean("a", true));
                setBooleanTest.run(JSONObject.create().setNumber("a", 1), "a", false, JSONObject.create().setBoolean("a", false));
                setBooleanTest.run(JSONObject.create().setNumber("b", 1), "a", true, JSONObject.create().setBoolean("a", true).setNumber("b", 1));
            });

            runner.testGroup("setNumber(String,long)", () ->
            {
                final Action4<JSONObject,String,Long,Throwable> setNumberErrorTest = (JSONObject object, String propertyName, Long propertyValue, Throwable expected) ->
                {
                    runner.test("with " + object + ", " + Strings.escapeAndQuote(propertyName) + ", and " + propertyValue, (Test test) ->
                    {
                        test.assertThrows(() -> object.setNumber(propertyName, propertyValue), expected);
                    });
                };

                setNumberErrorTest.run(JSONObject.create(), null, 1L, new PreConditionFailure("propertyName cannot be null."));
                setNumberErrorTest.run(JSONObject.create(), "", 2L, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Long,JSONObject> setNumberTest = (JSONObject object, String propertyName, Long propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + object + ", " + Strings.escapeAndQuote(propertyName) + ", and " + propertyValue, (Test test) ->
                    {
                        final JSONObject setResult = object.setNumber(propertyName, propertyValue);
                        test.assertSame(object, setResult);
                        test.assertEqual(expected, object);
                        test.assertTrue(object.containsKey(propertyName));
                        test.assertTrue(object.contains(propertyName));
                    });
                };

                setNumberTest.run(JSONObject.create(), "a", 1L, JSONObject.create().setNumber("a", 1));
                setNumberTest.run(JSONObject.create().setBoolean("a", false), "a", 2L, JSONObject.create().setNumber("a", 2));
                setNumberTest.run(JSONObject.create().setNumber("a", 1), "a", 3L, JSONObject.create().setNumber("a", 3));
                setNumberTest.run(JSONObject.create().setNumber("b", 1), "a", 4L, JSONObject.create().setNumber("a", 4).setNumber("b", 1));
            });

            runner.testGroup("setNumber(String,double)", () ->
            {
                final Action4<JSONObject,String,Double,Throwable> setNumberErrorTest = (JSONObject object, String propertyName, Double propertyValue, Throwable expected) ->
                {
                    runner.test("with " + object + ", " + Strings.escapeAndQuote(propertyName) + ", and " + propertyValue, (Test test) ->
                    {
                        test.assertThrows(() -> object.setNumber(propertyName, propertyValue), expected);
                    });
                };

                setNumberErrorTest.run(JSONObject.create(), null, 1.2, new PreConditionFailure("propertyName cannot be null."));
                setNumberErrorTest.run(JSONObject.create(), "", 3.4, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Double,JSONObject> setNumberTest = (JSONObject object, String propertyName, Double propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + object + ", " + Strings.escapeAndQuote(propertyName) + ", and " + propertyValue, (Test test) ->
                    {
                        final JSONObject setResult = object.setNumber(propertyName, propertyValue);
                        test.assertSame(object, setResult);
                        test.assertEqual(expected, object);
                        test.assertTrue(object.containsKey(propertyName));
                        test.assertTrue(object.contains(propertyName));
                    });
                };

                setNumberTest.run(JSONObject.create(), "a", 5.6, JSONObject.create().setNumber("a", 5.6));
                setNumberTest.run(JSONObject.create().setBoolean("a", false), "a", 7.8, JSONObject.create().setNumber("a", 7.8));
                setNumberTest.run(JSONObject.create().setNumber("a", 1), "a", 9.10, JSONObject.create().setNumber("a", 9.10));
                setNumberTest.run(JSONObject.create().setNumber("b", 1), "a", 11.12, JSONObject.create().setNumber("a", 11.12).setNumber("b", 1));
            });

            runner.testGroup("toString()", () ->
            {
                final Action2<JSONObject,String> toStringTest = (JSONObject object, String expected) ->
                {
                    runner.test("with " + object, (Test test) ->
                    {
                        test.assertEqual(expected, object.toString());
                    });
                };

                toStringTest.run(JSONObject.create(), "{}");
                toStringTest.run(
                    JSONObject.create()
                        .setNumber("a", 5),
                    "{\"a\":5}");
                toStringTest.run(
                    JSONObject.create()
                        .setNumber("a", 5.000),
                    "{\"a\":5.0}");
                toStringTest.run(
                    JSONObject.create()
                        .setString("a", "hello"),
                    "{\"a\":\"hello\"}");
                toStringTest.run(
                    JSONObject.create()
                        .setObject("a", JSONObject.create()),
                    "{\"a\":{}}");
                toStringTest.run(
                    JSONObject.create()
                        .setArray("a", JSONArray.create()),
                    "{\"a\":[]}");
            });

            runner.testGroup("toString(JSONFormat)", () ->
            {
                final Action3<JSONObject,JSONFormat,String> toStringTest = (JSONObject object, JSONFormat format, String expected) ->
                {
                    runner.test("with " + object, (Test test) ->
                    {
                        test.assertEqual(expected, object.toString(format));
                    });
                };

                toStringTest.run(JSONObject.create(), JSONFormat.consise, "{}");
                toStringTest.run(
                    JSONObject.create()
                        .setNumber("a", 5),
                    JSONFormat.consise,
                    "{\"a\":5}");
                toStringTest.run(
                    JSONObject.create()
                        .setNumber("a", 5.000),
                    JSONFormat.consise,
                    "{\"a\":5.0}");
                toStringTest.run(
                    JSONObject.create()
                        .setString("a", "hello"),
                    JSONFormat.consise,
                    "{\"a\":\"hello\"}");
                toStringTest.run(
                    JSONObject.create()
                        .setObject("a", JSONObject.create()),
                    JSONFormat.consise,
                    "{\"a\":{}}");
                toStringTest.run(
                    JSONObject.create()
                        .setArray("a", JSONArray.create()),
                    JSONFormat.consise,
                    "{\"a\":[]}");

                toStringTest.run(JSONObject.create(), JSONFormat.pretty, "{}");
                toStringTest.run(
                    JSONObject.create()
                        .setNumber("a", 5),
                    JSONFormat.pretty,
                    "{\n  \"a\": 5\n}");
                toStringTest.run(
                    JSONObject.create()
                        .setNumber("a", 5.000),
                    JSONFormat.pretty,
                    "{\n  \"a\": 5.0\n}");
                toStringTest.run(
                    JSONObject.create()
                        .setString("a", "hello"),
                    JSONFormat.pretty,
                    "{\n  \"a\": \"hello\"\n}");
                toStringTest.run(
                    JSONObject.create()
                        .setObject("a", JSONObject.create()),
                    JSONFormat.pretty,
                    "{\n  \"a\": {}\n}");
                toStringTest.run(
                    JSONObject.create()
                        .setArray("a", JSONArray.create()),
                    JSONFormat.pretty,
                    "{\n  \"a\": []\n}");
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<JSONObject,Object,Boolean> equalsTest = (JSONObject object, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + object + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, object.equals(rhs));
                    });
                };

                equalsTest.run(JSONObject.create(), null, false);
                equalsTest.run(JSONObject.create(), "test", false);
                equalsTest.run(JSONObject.create(), JSONObject.create(), true);
                equalsTest.run(
                    JSONObject.create(),
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    JSONObject.create(),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("a", "c")),
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    true);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("A", "b")),
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("a", 1),
                        JSONProperty.create("b", 2)),
                    JSONObject.create(
                        JSONProperty.create("b", 2),
                        JSONProperty.create("a", 1)),
                    true);
            });

            runner.testGroup("equals(JSONObject)", () ->
            {
                final Action3<JSONObject,JSONObject,Boolean> equalsTest = (JSONObject object, JSONObject rhs, Boolean expected) ->
                {
                    runner.test("with " + object + " and " + rhs, (Test test) ->
                    {
                        test.assertEqual(expected, object.equals(rhs));
                    });
                };

                equalsTest.run(JSONObject.create(), null, false);
                equalsTest.run(JSONObject.create(), JSONObject.create(), true);
                equalsTest.run(
                    JSONObject.create(),
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    JSONObject.create(),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("a", "c")),
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    true);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("A", "b")),
                    JSONObject.create(
                        JSONProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONProperty.create("a", 1),
                        JSONProperty.create("b", 2)),
                    JSONObject.create(
                        JSONProperty.create("b", 2),
                        JSONProperty.create("a", 1)),
                    true);
            });

            runner.testGroup("clear()", () ->
            {
                final Action1<JSONObject> clearTest = (JSONObject object) ->
                {
                    runner.test("with " + object, (Test test) ->
                    {
                        final JSONObject clearResult = object.clear();
                        test.assertSame(object, clearResult);
                        test.assertEqual(JSONObject.create(), object);
                    });
                };

                clearTest.run(JSONObject.create());
                clearTest.run(JSONObject.create().setBoolean("hello", true));
            });

            runner.testGroup("setObject(String,JSONObject)", () ->
            {
                final Action4<JSONObject,String,JSONObject,Throwable> setObjectErrorTest = (JSONObject object, String propertyName, JSONObject propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setObject(propertyName, propertyValue), expected);
                    });
                };

                setObjectErrorTest.run(JSONObject.create(), null, JSONObject.create(), new PreConditionFailure("propertyName cannot be null."));
                setObjectErrorTest.run(JSONObject.create(), "", JSONObject.create(), new PreConditionFailure("propertyName cannot be empty."));
                setObjectErrorTest.run(JSONObject.create(), "a", null, new PreConditionFailure("propertyValue cannot be null."));

                final Action4<JSONObject,String,JSONObject,JSONObject> setObjectTest = (JSONObject object, String propertyName, JSONObject propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setObject(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertSame(propertyValue, object.getObject(propertyName).await());
                    });
                };

                setObjectTest.run(JSONObject.create(), "a", JSONObject.create(), JSONObject.create(JSONProperty.create("a", JSONObject.create())));
            });

            runner.testGroup("setObjectOrNull(String,JSONObject)", () ->
            {
                final Action4<JSONObject,String,JSONObject,Throwable> setObjectOrNullErrorTest = (JSONObject object, String propertyName, JSONObject propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setObjectOrNull(propertyName, propertyValue), expected);
                    });
                };

                setObjectOrNullErrorTest.run(JSONObject.create(), null, JSONObject.create(), new PreConditionFailure("propertyName cannot be null."));
                setObjectOrNullErrorTest.run(JSONObject.create(), "", JSONObject.create(), new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,JSONObject,JSONObject> setObjectOrNullTest = (JSONObject object, String propertyName, JSONObject propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setObjectOrNull(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertSame(propertyValue, object.getObjectOrNull(propertyName).await());
                    });
                };

                setObjectOrNullTest.run(JSONObject.create(), "a", JSONObject.create(), JSONObject.create(JSONProperty.create("a", JSONObject.create())));
                setObjectOrNullTest.run(JSONObject.create(), "a", null, JSONObject.create(JSONProperty.create("a", JSONNull.segment)));
            });

            runner.testGroup("setArray(String,JSONArray)", () ->
            {
                final Action4<JSONObject,String,JSONArray,Throwable> setArrayErrorTest = (JSONObject object, String propertyName, JSONArray propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setArray(propertyName, propertyValue), expected);
                    });
                };

                setArrayErrorTest.run(JSONObject.create(), null, JSONArray.create(), new PreConditionFailure("propertyName cannot be null."));
                setArrayErrorTest.run(JSONObject.create(), "", JSONArray.create(), new PreConditionFailure("propertyName cannot be empty."));
                setArrayErrorTest.run(JSONObject.create(), "a", null, new PreConditionFailure("propertyValue cannot be null."));

                final Action4<JSONObject,String,JSONArray,JSONObject> setArrayTest = (JSONObject object, String propertyName, JSONArray propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setArray(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertSame(propertyValue, object.getArray(propertyName).await());
                    });
                };

                setArrayTest.run(JSONObject.create(), "a", JSONArray.create(), JSONObject.create(JSONProperty.create("a", JSONArray.create())));
            });

            runner.testGroup("setArrayOrNull(String,JSONArray)", () ->
            {
                final Action4<JSONObject,String,JSONArray,Throwable> setArrayOrNullErrorTest = (JSONObject object, String propertyName, JSONArray propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setArrayOrNull(propertyName, propertyValue), expected);
                    });
                };

                setArrayOrNullErrorTest.run(JSONObject.create(), null, JSONArray.create(), new PreConditionFailure("propertyName cannot be null."));
                setArrayOrNullErrorTest.run(JSONObject.create(), "", JSONArray.create(), new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,JSONArray,JSONObject> setArrayOrNullTest = (JSONObject object, String propertyName, JSONArray propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setArrayOrNull(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertSame(propertyValue, object.getArrayOrNull(propertyName).await());
                    });
                };

                setArrayOrNullTest.run(JSONObject.create(), "a", JSONArray.create(), JSONObject.create(JSONProperty.create("a", JSONArray.create())));
                setArrayOrNullTest.run(JSONObject.create(), "a", null, JSONObject.create(JSONProperty.create("a", JSONNull.segment)));
            });

            runner.testGroup("setNull(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> setNullErrorTest = (JSONObject object, String propertyName, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, Strings.escapeAndQuote(propertyName)), (Test test) ->
                    {
                        test.assertThrows(() -> object.setNull(propertyName), expected);
                    });
                };

                setNullErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                setNullErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));

                final Action2<JSONObject,String> setNullTest = (JSONObject object, String propertyName) ->
                {
                    runner.test("with " + English.andList(object, Strings.escapeAndQuote(propertyName)), (Test test) ->
                    {
                        final JSONObject setNullResult = object.setNull(propertyName);
                        test.assertSame(object, setNullResult);
                        test.assertNull(object.getNull(propertyName).await());
                    });
                };

                setNullTest.run(JSONObject.create(), "hello");
            });

            runner.testGroup("setBoolean(String,boolean)", () ->
            {
                final Action4<JSONObject,String,Boolean,Throwable> setBooleanErrorTest = (JSONObject object, String propertyName, Boolean propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setBoolean(propertyName, propertyValue), expected);
                    });
                };

                setBooleanErrorTest.run(JSONObject.create(), null, true, new PreConditionFailure("propertyName cannot be null."));
                setBooleanErrorTest.run(JSONObject.create(), "", false, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Boolean,JSONObject> setBooleanTest = (JSONObject object, String propertyName, Boolean propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setBoolean(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertSame(propertyValue, object.getBoolean(propertyName).await());
                    });
                };

                setBooleanTest.run(JSONObject.create(), "a", false, JSONObject.create(JSONProperty.create("a", false)));
                setBooleanTest.run(JSONObject.create(), "a", true, JSONObject.create(JSONProperty.create("a", true)));
            });

            runner.testGroup("setBooleanOrNull(String,boolean)", () ->
            {
                final Action4<JSONObject,String,Boolean,Throwable> setBooleanOrNullErrorTest = (JSONObject object, String propertyName, Boolean propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setBooleanOrNull(propertyName, propertyValue), expected);
                    });
                };

                setBooleanOrNullErrorTest.run(JSONObject.create(), null, true, new PreConditionFailure("propertyName cannot be null."));
                setBooleanOrNullErrorTest.run(JSONObject.create(), "", false, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Boolean,JSONObject> setBooleanOrNullTest = (JSONObject object, String propertyName, Boolean propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setBooleanOrNull(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertSame(propertyValue, object.getBooleanOrNull(propertyName).await());
                    });
                };

                setBooleanOrNullTest.run(JSONObject.create(), "a", false, JSONObject.create(JSONProperty.create("a", false)));
                setBooleanOrNullTest.run(JSONObject.create(), "a", true, JSONObject.create(JSONProperty.create("a", true)));
                setBooleanOrNullTest.run(JSONObject.create(), "a", null, JSONObject.create(JSONProperty.create("a", JSONNull.segment)));
            });

            runner.testGroup("setNumber(String,long)", () ->
            {
                final Action4<JSONObject,String,Long,Throwable> setNumberErrorTest = (JSONObject object, String propertyName, Long propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setNumber(propertyName, propertyValue), expected);
                    });
                };

                setNumberErrorTest.run(JSONObject.create(), null, 0L, new PreConditionFailure("propertyName cannot be null."));
                setNumberErrorTest.run(JSONObject.create(), "", 100L, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Long,JSONObject> setNumberTest = (JSONObject object, String propertyName, Long propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setNumber(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertEqual(propertyValue.doubleValue(), object.getNumber(propertyName).await());
                    });
                };

                setNumberTest.run(JSONObject.create(), "a", 20L, JSONObject.create(JSONProperty.create("a", 20L)));
            });

            runner.testGroup("setNumber(String,double)", () ->
            {
                final Action4<JSONObject,String,Double,Throwable> setNumberErrorTest = (JSONObject object, String propertyName, Double propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setNumber(propertyName, propertyValue), expected);
                    });
                };

                setNumberErrorTest.run(JSONObject.create(), null, 0.5, new PreConditionFailure("propertyName cannot be null."));
                setNumberErrorTest.run(JSONObject.create(), "", 50.1, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Double,JSONObject> setNumberTest = (JSONObject object, String propertyName, Double propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setNumber(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertEqual(propertyValue, object.getNumber(propertyName).await());
                    });
                };

                setNumberTest.run(JSONObject.create(), "a", 100.0, JSONObject.create(JSONProperty.create("a", 100.0)));
            });

            runner.testGroup("setNumberOrNull(String,Integer)", () ->
            {
                final Action4<JSONObject,String,Integer,Throwable> setNumberOrNullErrorTest = (JSONObject object, String propertyName, Integer propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setNumberOrNull(propertyName, propertyValue), expected);
                    });
                };

                setNumberOrNullErrorTest.run(JSONObject.create(), null, 0, new PreConditionFailure("propertyName cannot be null."));
                setNumberOrNullErrorTest.run(JSONObject.create(), "", 100, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Integer,JSONObject> setNumberOrNullTest = (JSONObject object, String propertyName, Integer propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setNumberOrNull(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertEqual(propertyValue == null ? null : propertyValue.doubleValue(), object.getNumberOrNull(propertyName).await());
                    });
                };

                setNumberOrNullTest.run(JSONObject.create(), "a", 20, JSONObject.create(JSONProperty.create("a", 20L)));
                setNumberOrNullTest.run(JSONObject.create(), "a", null, JSONObject.create(JSONProperty.create("a", JSONNull.segment)));
            });

            runner.testGroup("setNumberOrNull(String,Long)", () ->
            {
                final Action4<JSONObject,String,Long,Throwable> setNumberOrNullErrorTest = (JSONObject object, String propertyName, Long propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setNumberOrNull(propertyName, propertyValue), expected);
                    });
                };

                setNumberOrNullErrorTest.run(JSONObject.create(), null, 0L, new PreConditionFailure("propertyName cannot be null."));
                setNumberOrNullErrorTest.run(JSONObject.create(), "", 100L, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Long,JSONObject> setNumberOrNullTest = (JSONObject object, String propertyName, Long propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setNumberOrNull(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertEqual(propertyValue == null ? null : propertyValue.doubleValue(), object.getNumberOrNull(propertyName).await());
                    });
                };

                setNumberOrNullTest.run(JSONObject.create(), "a", 20L, JSONObject.create(JSONProperty.create("a", 20L)));
                setNumberOrNullTest.run(JSONObject.create(), "a", null, JSONObject.create(JSONProperty.create("a", JSONNull.segment)));
            });

            runner.testGroup("setNumberOrNull(String,Float)", () ->
            {
                final Action4<JSONObject,String,Float,Throwable> setNumberOrNullErrorTest = (JSONObject object, String propertyName, Float propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setNumberOrNull(propertyName, propertyValue), expected);
                    });
                };

                setNumberOrNullErrorTest.run(JSONObject.create(), null, 0f, new PreConditionFailure("propertyName cannot be null."));
                setNumberOrNullErrorTest.run(JSONObject.create(), "", 100f, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Float,JSONObject> setNumberOrNullTest = (JSONObject object, String propertyName, Float propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setNumberOrNull(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertEqual(propertyValue == null ? null : propertyValue.doubleValue(), object.getNumberOrNull(propertyName).await());
                    });
                };

                setNumberOrNullTest.run(JSONObject.create(), "a", 20f, JSONObject.create(JSONProperty.create("a", 20.0)));
                setNumberOrNullTest.run(JSONObject.create(), "a", null, JSONObject.create(JSONProperty.create("a", JSONNull.segment)));
            });

            runner.testGroup("setNumberOrNull(String,Double)", () ->
            {
                final Action4<JSONObject,String,Double,Throwable> setNumberOrNullErrorTest = (JSONObject object, String propertyName, Double propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setNumberOrNull(propertyName, propertyValue), expected);
                    });
                };

                setNumberOrNullErrorTest.run(JSONObject.create(), null, 0.0, new PreConditionFailure("propertyName cannot be null."));
                setNumberOrNullErrorTest.run(JSONObject.create(), "", 100.0, new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,Double,JSONObject> setNumberOrNullTest = (JSONObject object, String propertyName, Double propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setNumberOrNull(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertEqual(propertyValue, object.getNumberOrNull(propertyName).await());
                    });
                };

                setNumberOrNullTest.run(JSONObject.create(), "a", 20.5, JSONObject.create(JSONProperty.create("a", 20.5)));
                setNumberOrNullTest.run(JSONObject.create(), "a", null, JSONObject.create(JSONProperty.create("a", JSONNull.segment)));
            });

            runner.testGroup("setString(String,String)", () ->
            {
                final Action4<JSONObject,String,String,Throwable> setStringErrorTest = (JSONObject object, String propertyName, String propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setString(propertyName, propertyValue), expected);
                    });
                };

                setStringErrorTest.run(JSONObject.create(), null, "b", new PreConditionFailure("propertyName cannot be null."));
                setStringErrorTest.run(JSONObject.create(), "", "b", new PreConditionFailure("propertyName cannot be empty."));
                setStringErrorTest.run(JSONObject.create(), "a", null, new PreConditionFailure("propertyValue cannot be null."));

                final Action4<JSONObject,String,String,JSONObject> setStringTest = (JSONObject object, String propertyName, String propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setString(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertEqual(propertyValue, object.getString(propertyName).await());
                    });
                };

                setStringTest.run(JSONObject.create(), "a", "", JSONObject.create(JSONProperty.create("a", "")));
                setStringTest.run(JSONObject.create(), "a", "b", JSONObject.create(JSONProperty.create("a", "b")));
            });

            runner.testGroup("setStringOrNull(String,String)", () ->
            {
                final Action4<JSONObject,String,String,Throwable> setStringOrNullErrorTest = (JSONObject object, String propertyName, String propertyValue, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        test.assertThrows(() -> object.setStringOrNull(propertyName, propertyValue), expected);
                    });
                };

                setStringOrNullErrorTest.run(JSONObject.create(), null, "b", new PreConditionFailure("propertyName cannot be null."));
                setStringOrNullErrorTest.run(JSONObject.create(), "", "b", new PreConditionFailure("propertyName cannot be empty."));

                final Action4<JSONObject,String,String,JSONObject> setStringOrNullTest = (JSONObject object, String propertyName, String propertyValue, JSONObject expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName, propertyValue), (Test test) ->
                    {
                        final JSONObject setObjectResult = object.setStringOrNull(propertyName, propertyValue);
                        test.assertSame(object, setObjectResult);
                        test.assertEqual(expected, object);
                        test.assertEqual(propertyValue, object.getStringOrNull(propertyName).await());
                    });
                };

                setStringOrNullTest.run(JSONObject.create(), "a", null, JSONObject.create(JSONProperty.create("a", JSONNull.segment)));
                setStringOrNullTest.run(JSONObject.create(), "a", "", JSONObject.create(JSONProperty.create("a", "")));
                setStringOrNullTest.run(JSONObject.create(), "a", "b", JSONObject.create(JSONProperty.create("a", "b")));
            });

            runner.testGroup("remove(String)", () ->
            {
                final Action3<JSONObject,String,Throwable> removeErrorTest = (JSONObject object, String propertyName, Throwable expected) ->
                {
                    runner.test("with " + English.andList(object, propertyName), (Test test) ->
                    {
                        test.assertThrows(() -> object.remove(propertyName).await(), expected);
                    });
                };

                removeErrorTest.run(JSONObject.create(), null, new PreConditionFailure("propertyName cannot be null."));
                removeErrorTest.run(JSONObject.create(), "", new PreConditionFailure("propertyName cannot be empty."));
                removeErrorTest.run(JSONObject.create(), "a", new NotFoundException("No property exists in this JSONObject with the name: \"a\""));
                removeErrorTest.run(JSONObject.create().setString("a", "b"), "A", new NotFoundException("No property exists in this JSONObject with the name: \"A\""));

                final Action4<JSONObject,String,JSONSegment,JSONObject> removeTest = (JSONObject object, String propertyName, JSONSegment removedPropertyValue, JSONObject expectedObject) ->
                {
                    runner.test("with " + English.andList(object, propertyName), (Test test) ->
                    {
                        test.assertEqual(removedPropertyValue, object.remove(propertyName).await());
                        test.assertFalse(object.contains(propertyName));
                        test.assertFalse(object.containsKey(propertyName));
                        test.assertEqual(expectedObject, object);
                    });
                };

                removeTest.run(JSONObject.create().setString("a", "b"), "a", JSONString.get("b"), JSONObject.create());
            });
        });
    }
}
