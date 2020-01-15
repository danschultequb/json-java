package qub;

public interface JSONArrayTests
{
    static void test(TestRunner runner)
    {
        PreCondition.assertNotNull(runner, "runner");

        runner.testGroup(JSONArray.class, () ->
        {
            runner.testGroup("create(JSONSegment...)", () ->
            {
                runner.test("with no arguments", (Test test) ->
                {
                    final JSONArray array = JSONArray.create();
                    test.assertEqual(Iterable.create(), array);
                });

                runner.test("with non-empty arguments", (Test test) ->
                {
                    final JSONArray array = JSONArray.create(JSONBoolean.trueSegment, JSONBoolean.falseSegment);
                    test.assertEqual(Iterable.create(JSONBoolean.trueSegment, JSONBoolean.falseSegment), array);
                });

                runner.test("with null array", (Test test) ->
                {
                    test.assertThrows(() -> JSONArray.create((JSONSegment[])null),
                        new PreConditionFailure("elements cannot be null."));
                });

                runner.test("with empty array", (Test test) ->
                {
                    final JSONArray array = JSONArray.create(new JSONSegment[0]);
                    test.assertEqual(Iterable.create(), array);
                });

                runner.test("with non-empty array", (Test test) ->
                {
                    final JSONArray array = JSONArray.create(new JSONSegment[] { JSONBoolean.trueSegment, JSONBoolean.falseSegment });
                    test.assertEqual(Iterable.create(JSONBoolean.trueSegment, JSONBoolean.falseSegment), array);
                });
            });

            runner.testGroup("create(JSONSegment...)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> JSONArray.create((Indexable<JSONSegment>)null),
                        new PreConditionFailure("elements cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    final JSONArray array = JSONArray.create(Indexable.create());
                    test.assertEqual(Iterable.create(), array);
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final JSONArray array = JSONArray.create(Indexable.create(JSONBoolean.trueSegment, JSONBoolean.falseSegment));
                    test.assertEqual(Iterable.create(JSONBoolean.trueSegment, JSONBoolean.falseSegment), array);
                });
            });

            runner.testGroup("get(int)", () ->
            {
                final Action3<Indexable<JSONSegment>,Integer,Throwable> getErrorTest = (Indexable<JSONSegment> elements, Integer index, Throwable expectedError) ->
                {
                    runner.test("with " + elements + " and " + index, (Test test) ->
                    {
                        final JSONArray array = JSONArray.create(elements);
                        test.assertThrows(() -> array.get(index), expectedError);
                    });
                };

                getErrorTest.run(Indexable.create(), -1,
                    new PreConditionFailure("Indexable length (0) must be greater than or equal to 1."));
                getErrorTest.run(Indexable.create(), 0,
                    new PreConditionFailure("Indexable length (0) must be greater than or equal to 1."));
                getErrorTest.run(Indexable.create(), 1,
                    new PreConditionFailure("Indexable length (0) must be greater than or equal to 1."));

                getErrorTest.run(Indexable.create(JSONNull.segment, JSONBoolean.falseSegment), -1,
                    new PreConditionFailure("index (-1) must be between 0 and 1."));
                getErrorTest.run(Indexable.create(JSONNull.segment, JSONBoolean.falseSegment), 2,
                    new PreConditionFailure("index (2) must be between 0 and 1."));

                final Action3<Indexable<JSONSegment>,Integer,JSONSegment> getTest = (Indexable<JSONSegment> elements, Integer index, JSONSegment expected) ->
                {
                    runner.test("with " + elements + " and " + index, (Test test) ->
                    {
                        final JSONArray array = JSONArray.create(elements);
                        test.assertEqual(expected, array.get(index));
                    });
                };

                getTest.run(Indexable.create(JSONNull.segment, JSONBoolean.falseSegment), 0, JSONNull.segment);
                getTest.run(Indexable.create(JSONNull.segment, JSONBoolean.falseSegment), 1, JSONBoolean.falseSegment);
            });

            runner.testGroup("toString()", () ->
            {
                final Action2<Indexable<JSONSegment>,String> toStringTest = (Indexable<JSONSegment> elements, String expected) ->
                {
                    runner.test("with " + elements, (Test test) ->
                    {
                        final JSONArray array = JSONArray.create(elements);
                        test.assertEqual(expected, array.toString());
                    });
                };

                toStringTest.run(Indexable.create(), "[]");
                toStringTest.run(Indexable.create(JSONNull.segment), "[null]");
                toStringTest.run(Indexable.create(JSONBoolean.trueSegment, JSONNumber.get(50)), "[true,50]");
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<Indexable<JSONSegment>,Object,Boolean> equalsTest = (Indexable<JSONSegment> elements, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + elements + " and " + rhs, (Test test) ->
                    {
                        final JSONArray array = JSONArray.create(elements);
                        test.assertEqual(expected, array.equals(rhs));
                    });
                };

                equalsTest.run(Indexable.create(), null, false);
                equalsTest.run(Indexable.create(), "hello", false);
                equalsTest.run(Indexable.create(), JSONArray.create(), true);
                equalsTest.run(Indexable.create(), JSONArray.create(JSONNull.segment, JSONNumber.get(50.0)), false);
                equalsTest.run(Indexable.create(JSONNull.segment, JSONNumber.get(50.0)), JSONArray.create(JSONNull.segment, JSONNumber.get(50.0)), true);
            });

            runner.testGroup("equals(JSONArray)", () ->
            {
                final Action3<Indexable<JSONSegment>,JSONArray,Boolean> equalsTest = (Indexable<JSONSegment> elements, JSONArray rhs, Boolean expected) ->
                {
                    runner.test("with " + elements + " and " + rhs, (Test test) ->
                    {
                        final JSONArray array = JSONArray.create(elements);
                        test.assertEqual(expected, array.equals(rhs));
                    });
                };

                equalsTest.run(Indexable.create(), null, false);
                equalsTest.run(Indexable.create(), JSONArray.create(), true);
                equalsTest.run(Indexable.create(), JSONArray.create(JSONNull.segment, JSONNumber.get(50.0)), false);
                equalsTest.run(Indexable.create(JSONNull.segment, JSONNumber.get(50.0)), JSONArray.create(JSONNull.segment, JSONNumber.get(50.0)), true);
            });

            runner.testGroup("insert(int,JSONSegment)", () ->
            {
                final Action4<JSONArray,Integer,JSONSegment,Throwable> insertErrorTest = (JSONArray array, Integer index, JSONSegment element, Throwable expected) ->
                {
                    runner.test("with " + array + ", " + index + ", and " + element, (Test test) ->
                    {
                        test.assertThrows(() -> array.insert(index, element), expected);
                    });
                };

                insertErrorTest.run(JSONArray.create(), -1, JSONBoolean.falseSegment, new PreConditionFailure("insertIndex (-1) must be equal to 0."));
                insertErrorTest.run(JSONArray.create(JSONBoolean.trueSegment), -1, JSONBoolean.falseSegment, new PreConditionFailure("insertIndex (-1) must be between 0 and 1."));
                insertErrorTest.run(JSONArray.create(), 1, JSONBoolean.falseSegment, new PreConditionFailure("insertIndex (1) must be equal to 0."));
                insertErrorTest.run(JSONArray.create(), 0, null, new PreConditionFailure("value cannot be null."));

                final Action4<JSONArray,Integer,JSONSegment,JSONArray> insertTest = (JSONArray array, Integer index, JSONSegment element, JSONArray expected) ->
                {
                    runner.test("with " + array + ", " + index + ", and " + element, (Test test) ->
                    {
                        final JSONArray insertResult = array.insert(index, element);
                        test.assertSame(array, insertResult);
                        test.assertEqual(expected, array);
                    });
                };

                insertTest.run(JSONArray.create(), 0, JSONBoolean.falseSegment, JSONArray.create(JSONBoolean.falseSegment));
                insertTest.run(JSONArray.create(JSONNull.segment), 0, JSONBoolean.falseSegment, JSONArray.create(JSONBoolean.falseSegment, JSONNull.segment));
                insertTest.run(JSONArray.create(JSONNull.segment), 1, JSONBoolean.falseSegment, JSONArray.create(JSONNull.segment, JSONBoolean.falseSegment));
            });

            runner.testGroup("removeAt(int)", () ->
            {
                final Action3<JSONArray,Integer,Throwable> removeAtErrorTest = (JSONArray array, Integer index, Throwable expected) ->
                {
                    runner.test("with " + array + " and " + index, (Test test) ->
                    {
                        test.assertThrows(() -> array.removeAt(index), expected);
                    });
                };

                removeAtErrorTest.run(JSONArray.create(), -1, new PreConditionFailure("Indexable length (0) must be greater than or equal to 1."));
                removeAtErrorTest.run(JSONArray.create(JSONBoolean.trueSegment), -1, new PreConditionFailure("index (-1) must be equal to 0."));
                removeAtErrorTest.run(JSONArray.create(), 1, new PreConditionFailure("Indexable length (0) must be greater than or equal to 1."));
                removeAtErrorTest.run(JSONArray.create(JSONBoolean.trueSegment), 2, new PreConditionFailure("index (2) must be equal to 0."));

                final Action4<JSONArray,Integer,JSONSegment,JSONArray> removeAtTest = (JSONArray array, Integer index, JSONSegment expectedResult, JSONArray expectedArray) ->
                {
                    runner.test("with " + array + " and " + index, (Test test) ->
                    {
                        test.assertEqual(expectedResult, array.removeAt(index));
                        test.assertEqual(expectedArray, array);
                    });
                };

                removeAtTest.run(JSONArray.create(JSONNull.segment), 0, JSONNull.segment, JSONArray.create());
                removeAtTest.run(JSONArray.create(JSONNull.segment, JSONBoolean.falseSegment), 0, JSONNull.segment, JSONArray.create(JSONBoolean.falseSegment));
                removeAtTest.run(JSONArray.create(JSONNull.segment, JSONBoolean.falseSegment), 1, JSONBoolean.falseSegment, JSONArray.create(JSONNull.segment));
            });

            runner.testGroup("set(int,JSONSegment)", () ->
            {
                final Action4<JSONArray,Integer,JSONSegment,Throwable> setErrorTest = (JSONArray array, Integer index, JSONSegment value, Throwable expected) ->
                {
                    runner.test("with " + array + ", " + index + ", and " + value, (Test test) ->
                    {
                        test.assertThrows(() -> array.set(index, value), expected);
                    });
                };

                setErrorTest.run(JSONArray.create(), -1, JSONNull.segment, new PreConditionFailure("Indexable length (0) must be greater than or equal to 1."));
                setErrorTest.run(JSONArray.create(JSONBoolean.trueSegment), -1, JSONNull.segment, new PreConditionFailure("index (-1) must be equal to 0."));
                setErrorTest.run(JSONArray.create(), 1, JSONNull.segment, new PreConditionFailure("Indexable length (0) must be greater than or equal to 1."));
                setErrorTest.run(JSONArray.create(JSONBoolean.trueSegment), 2, JSONNull.segment, new PreConditionFailure("index (2) must be equal to 0."));

                final Action4<JSONArray,Integer,JSONSegment,JSONArray> setTest = (JSONArray array, Integer index, JSONSegment value, JSONArray expectedArray) ->
                {
                    runner.test("with " + array + " and " + index, (Test test) ->
                    {
                        final JSONArray setResult = array.set(index, value);
                        test.assertSame(array, setResult);
                        test.assertEqual(expectedArray, array);
                    });
                };

                setTest.run(JSONArray.create(JSONNull.segment), 0, JSONBoolean.trueSegment, JSONArray.create(JSONBoolean.trueSegment));
                setTest.run(JSONArray.create(JSONNull.segment, JSONBoolean.falseSegment), 0, JSONNumber.get(5), JSONArray.create(JSONNumber.get(5), JSONBoolean.falseSegment));
                setTest.run(JSONArray.create(JSONNull.segment, JSONBoolean.falseSegment), 1, JSONString.get("hello"), JSONArray.create(JSONNull.segment, JSONString.get("hello")));
            });
        });
    }
}
