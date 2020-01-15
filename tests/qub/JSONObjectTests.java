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
                    test.assertEqual(object.getPropertyNames(), object.getKeys());
                    test.assertEqual(object.getPropertyValues(), object.getValues());
                    test.assertEqual("{}", object.toString());
                });

                runner.test("with one property", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(JSONObjectProperty.create("hello", "there"));
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create().setString("hello", "there"), object);
                    test.assertEqual(Iterable.create(JSONObjectProperty.create("hello", "there")), object.getProperties());
                    test.assertEqual(Iterable.create("hello"), object.getPropertyNames());
                    test.assertEqual(Iterable.create(JSONString.get("there")), object.getPropertyValues());
                    test.assertEqual(object.getPropertyNames(), object.getKeys());
                    test.assertEqual(object.getPropertyValues(), object.getValues());
                    test.assertEqual("{\"hello\":\"there\"}", object.toString());
                });

                runner.test("with multiple properties", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(
                        JSONObjectProperty.create("hello", "there"),
                        JSONObjectProperty.create("fun", true),
                        JSONObjectProperty.create("work", JSONNull.segment));
                    test.assertNotNull(object);
                    test.assertEqual(
                        JSONObject.create()
                            .setString("hello", "there")
                            .setBoolean("fun", true)
                            .set("work", JSONNull.segment),
                        object);
                    test.assertEqual(
                        Iterable.create(
                            JSONObjectProperty.create("hello", "there"),
                            JSONObjectProperty.create("fun", true),
                            JSONObjectProperty.create("work", JSONNull.segment)),
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
                    test.assertThrows(() -> JSONObject.create((JSONObjectProperty[])null),
                        new PreConditionFailure("properties cannot be null."));
                });

                runner.test("with empty array", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(new JSONObjectProperty[0]);
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create(), object);
                    test.assertEqual("{}", object.toString());
                });

                runner.test("with one property array", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(new JSONObjectProperty[] { JSONObjectProperty.create("hello", "there") });
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create().setString("hello", "there"), object);
                    test.assertEqual("{\"hello\":\"there\"}", object.toString());
                });

                runner.test("with multiple property array", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(new JSONObjectProperty[]
                    {
                        JSONObjectProperty.create("hello", "there"),
                        JSONObjectProperty.create("fun", true),
                        JSONObjectProperty.create("work", JSONNull.segment)
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
                    test.assertThrows(() -> JSONObject.create((Iterable<JSONObjectProperty>)null),
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
                    final JSONObject object = JSONObject.create(Iterable.create(JSONObjectProperty.create("hello", "there")));
                    test.assertNotNull(object);
                    test.assertEqual(JSONObject.create().setString("hello", "there"), object);
                    test.assertEqual(Iterable.create(JSONObjectProperty.create("hello", "there")), object.getProperties());
                    test.assertEqual("{\"hello\":\"there\"}", object.toString());
                });

                runner.test("with multiple properties", (Test test) ->
                {
                    final JSONObject object = JSONObject.create(Iterable.create(
                        JSONObjectProperty.create("hello", "there"),
                        JSONObjectProperty.create("fun", true),
                        JSONObjectProperty.create("work", JSONNull.segment)));
                    test.assertNotNull(object);
                    test.assertEqual(
                        JSONObject.create()
                            .setString("hello", "there")
                            .setBoolean("fun", true)
                            .set("work", JSONNull.segment),
                        object);
                    test.assertEqual(
                        Iterable.create(
                            JSONObjectProperty.create("hello", "there"),
                            JSONObjectProperty.create("fun", true),
                            JSONObjectProperty.create("work", JSONNull.segment)),
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

                final Action3<JSONObject,String,JSONObjectProperty> getPropertyTest = (JSONObject object, String propertyName, JSONObjectProperty expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getProperty(propertyName).await());
                    });
                };

                getPropertyTest.run(JSONObject.create().setNumber("a", 1), "a", JSONObjectProperty.create("a", 1));
                getPropertyTest.run(JSONObject.create().setString("bats", "hello"), "bats", JSONObjectProperty.create("bats", "hello"));
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

                final Action3<JSONObject,String,JSONArray> getArrayTest = (JSONObject object, String propertyName, JSONArray expected) ->
                {
                    runner.test("with " + object + " and " + Strings.escapeAndQuote(propertyName), (Test test) ->
                    {
                        test.assertEqual(expected, object.getArray(propertyName).await());
                    });
                };

                getArrayTest.run(JSONObject.create().set("a", JSONArray.create()), "a", JSONArray.create());
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

            runner.testGroup("setProperty(JSONObjectProperty)", () ->
            {
                final Action3<JSONObject,JSONObjectProperty,Throwable> setPropertyErrorTest = (JSONObject object, JSONObjectProperty property, Throwable expected) ->
                {
                    runner.test("with " + object + " and " + property, (Test test) ->
                    {
                        test.assertThrows(() -> object.setProperty(property), expected);
                    });
                };

                setPropertyErrorTest.run(JSONObject.create(), null, new PreConditionFailure("property cannot be null."));

                final Action3<JSONObject,JSONObjectProperty,JSONObject> setPropertyTest = (JSONObject object, JSONObjectProperty property, JSONObject expected) ->
                {
                    runner.test("with " + object + " and " + property, (Test test) ->
                    {
                        final JSONObject setResult = object.setProperty(property);
                        test.assertSame(object, setResult);
                        test.assertEqual(expected, object);
                        test.assertTrue(object.containsKey(property.getName()));
                        test.assertTrue(object.containsProperty(property.getName()));
                    });
                };

                setPropertyTest.run(JSONObject.create(), JSONObjectProperty.create("a", 1), JSONObject.create().setNumber("a", 1));
                setPropertyTest.run(JSONObject.create().setNumber("a", 1), JSONObjectProperty.create("a", 2), JSONObject.create().setNumber("a", 2));
                setPropertyTest.run(JSONObject.create().setNumber("a", 1), JSONObjectProperty.create("a", false), JSONObject.create().setBoolean("a", false));
                setPropertyTest.run(JSONObject.create().setNumber("a", 1), JSONObjectProperty.create("a", JSONNull.segment), JSONObject.create().set("a", JSONNull.segment));
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
                        test.assertTrue(object.containsProperty(propertyName));
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
                        test.assertTrue(object.containsProperty(propertyName));
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
                        test.assertTrue(object.containsProperty(propertyName));
                    });
                };

                setNumberTest.run(JSONObject.create(), "a", 5.6, JSONObject.create().setNumber("a", 5.6));
                setNumberTest.run(JSONObject.create().setBoolean("a", false), "a", 7.8, JSONObject.create().setNumber("a", 7.8));
                setNumberTest.run(JSONObject.create().setNumber("a", 1), "a", 9.10, JSONObject.create().setNumber("a", 9.10));
                setNumberTest.run(JSONObject.create().setNumber("b", 1), "a", 11.12, JSONObject.create().setNumber("a", 11.12).setNumber("b", 1));
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
                        JSONObjectProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    JSONObject.create(),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("a", "c")),
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    true);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("A", "b")),
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("a", 1),
                        JSONObjectProperty.create("b", 2)),
                    JSONObject.create(
                        JSONObjectProperty.create("b", 2),
                        JSONObjectProperty.create("a", 1)),
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
                        JSONObjectProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    JSONObject.create(),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("a", "c")),
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    true);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("A", "b")),
                    JSONObject.create(
                        JSONObjectProperty.create("a", "b")),
                    false);
                equalsTest.run(
                    JSONObject.create(
                        JSONObjectProperty.create("a", 1),
                        JSONObjectProperty.create("b", 2)),
                    JSONObject.create(
                        JSONObjectProperty.create("b", 2),
                        JSONObjectProperty.create("a", 1)),
                    true);
            });
        });
    }
}
