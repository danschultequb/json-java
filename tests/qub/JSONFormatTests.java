package qub;

public interface JSONFormatTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(JSONFormat.class, () ->
        {
            runner.test("consise", (Test test) ->
            {
                test.assertEqual(
                    JSONFormat.create()
                        .setSingleIndent("")
                        .setNewLine("")
                        .setAfterPropertySeparator(""),
                    JSONFormat.consise);
            });

            runner.test("pretty", (Test test) ->
            {
                test.assertEqual(
                    JSONFormat.create()
                        .setSingleIndent("  ")
                        .setNewLine("\n")
                        .setAfterPropertySeparator(" "),
                    JSONFormat.pretty);
            });

            runner.test("create()", (Test test) ->
            {
                final JSONFormat format = JSONFormat.create();
                test.assertNotNull(format);
                test.assertEqual("", format.getNewLine());
                test.assertEqual("", format.getSingleIndent());
                test.assertEqual("", format.getAfterPropertySeparator());
            });

            runner.testGroup("setNewLine(String)", () ->
            {
                final Action2<String,Throwable> setNewLineErrorTest = (String newLine, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(newLine), (Test test) ->
                    {
                        final JSONFormat format = JSONFormat.create();
                        test.assertThrows(() -> format.setNewLine(newLine),
                            expected);
                        test.assertEqual("", format.getNewLine());
                    });
                };

                setNewLineErrorTest.run(null, new PreConditionFailure("newLine cannot be null."));
                setNewLineErrorTest.run("a", new PreConditionFailure("newLine (a) must contain only [' ','\t','\r','\n']."));

                final Action1<String> setNewLineTest = (String newLine) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(newLine), (Test test) ->
                    {
                        final JSONFormat format = JSONFormat.create();
                        final JSONFormat setNewLineResult = format.setNewLine(newLine);
                        test.assertSame(format, setNewLineResult);
                        test.assertSame(newLine, format.getNewLine());
                    });
                };

                setNewLineTest.run("");
                setNewLineTest.run("\n");
                setNewLineTest.run("\r\n");
            });

            runner.testGroup("setSingleIndent(String)", () ->
            {
                final Action2<String,Throwable> setSingleIndentErrorTest = (String singleIndent, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(singleIndent), (Test test) ->
                    {
                        final JSONFormat format = JSONFormat.create();
                        test.assertThrows(() -> format.setSingleIndent(singleIndent),
                            expected);
                        test.assertEqual("", format.getSingleIndent());
                    });
                };

                setSingleIndentErrorTest.run(null, new PreConditionFailure("singleIndent cannot be null."));
                setSingleIndentErrorTest.run("a", new PreConditionFailure("singleIndent (a) must contain only [' ','\t','\r','\n']."));

                final Action1<String> setSingleIndentTest = (String singleIndent) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(singleIndent), (Test test) ->
                    {
                        final JSONFormat format = JSONFormat.create();
                        final JSONFormat setSingleIndentResult = format.setSingleIndent(singleIndent);
                        test.assertSame(format, setSingleIndentResult);
                        test.assertSame(singleIndent, format.getSingleIndent());
                    });
                };

                setSingleIndentTest.run("");
                setSingleIndentTest.run("\n");
                setSingleIndentTest.run("\r\n");
                setSingleIndentTest.run(" ");
                setSingleIndentTest.run("  ");
                setSingleIndentTest.run("\t");
            });

            runner.testGroup("setAfterPropertySeparator(String)", () ->
            {
                final Action2<String,Throwable> setAfterPropertySeparatorErrorTest = (String afterPropertySeparator, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(afterPropertySeparator), (Test test) ->
                    {
                        final JSONFormat format = JSONFormat.create();
                        test.assertThrows(() -> format.setAfterPropertySeparator(afterPropertySeparator),
                            expected);
                        test.assertEqual("", format.getAfterPropertySeparator());
                    });
                };

                setAfterPropertySeparatorErrorTest.run(null, new PreConditionFailure("afterPropertySeparator cannot be null."));
                setAfterPropertySeparatorErrorTest.run("a", new PreConditionFailure("afterPropertySeparator (a) must contain only [' ','\t','\r','\n']."));

                final Action1<String> setAfterPropertySeparatorTest = (String afterPropertySeparator) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(afterPropertySeparator), (Test test) ->
                    {
                        final JSONFormat format = JSONFormat.create();
                        final JSONFormat setAfterPropertySeparatorResult = format.setAfterPropertySeparator(afterPropertySeparator);
                        test.assertSame(format, setAfterPropertySeparatorResult);
                        test.assertSame(afterPropertySeparator, format.getAfterPropertySeparator());
                    });
                };

                setAfterPropertySeparatorTest.run("");
                setAfterPropertySeparatorTest.run("\n");
                setAfterPropertySeparatorTest.run("\r\n");
                setAfterPropertySeparatorTest.run(" ");
                setAfterPropertySeparatorTest.run("  ");
                setAfterPropertySeparatorTest.run("\t");
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<JSONFormat,Object,Boolean> equalsTest = (JSONFormat format, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(format, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, format.equals(rhs));
                    });
                };

                equalsTest.run(JSONFormat.create(), null, false);
                equalsTest.run(JSONFormat.create(), "hello", false);
                equalsTest.run(JSONFormat.create(), JSONFormat.create(), true);
                equalsTest.run(
                    JSONFormat.create().setSingleIndent(" "),
                    JSONFormat.create(),
                    false);
                equalsTest.run(
                    JSONFormat.create().setSingleIndent(" "),
                    JSONFormat.create().setSingleIndent("\t"),
                    false);
                equalsTest.run(
                    JSONFormat.create().setNewLine("\n"),
                    JSONFormat.create(),
                    false);
                equalsTest.run(
                    JSONFormat.create().setNewLine("\n"),
                    JSONFormat.create().setNewLine("\r\n"),
                    false);
                equalsTest.run(
                    JSONFormat.create().setAfterPropertySeparator(" "),
                    JSONFormat.create(),
                    false);
                equalsTest.run(
                    JSONFormat.create().setAfterPropertySeparator(" "),
                    JSONFormat.create().setAfterPropertySeparator("\t"),
                    false);
            });

            runner.testGroup("equals(JSONFormat)", () ->
            {
                final Action3<JSONFormat,JSONFormat,Boolean> equalsTest = (JSONFormat format, JSONFormat rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(format, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, format.equals(rhs));
                    });
                };

                equalsTest.run(JSONFormat.create(), null, false);
                equalsTest.run(JSONFormat.create(), JSONFormat.create(), true);
                equalsTest.run(
                    JSONFormat.create().setSingleIndent(" "),
                    JSONFormat.create(),
                    false);
                equalsTest.run(
                    JSONFormat.create().setSingleIndent(" "),
                    JSONFormat.create().setSingleIndent("\t"),
                    false);
                equalsTest.run(
                    JSONFormat.create().setNewLine("\n"),
                    JSONFormat.create(),
                    false);
                equalsTest.run(
                    JSONFormat.create().setNewLine("\n"),
                    JSONFormat.create().setNewLine("\r\n"),
                    false);
                equalsTest.run(
                    JSONFormat.create().setAfterPropertySeparator(" "),
                    JSONFormat.create(),
                    false);
                equalsTest.run(
                    JSONFormat.create().setAfterPropertySeparator(" "),
                    JSONFormat.create().setAfterPropertySeparator("\t"),
                    false);
            });

            runner.testGroup("toJson()", () ->
            {
                final Action2<JSONFormat,JSONObject> toJsonTest = (JSONFormat format, JSONObject expected) ->
                {
                    runner.test("with " + format, (Test test) ->
                    {
                        test.assertEqual(expected, format.toJson());
                    });
                };

                toJsonTest.run(
                    JSONFormat.create(),
                    JSONObject.create()
                        .setString("singleIndent", "")
                        .setString("newLine", "")
                        .setString("afterPropertySeparator", ""));
                toJsonTest.run(
                    JSONFormat.create()
                        .setSingleIndent("  ")
                        .setNewLine("\n")
                        .setAfterPropertySeparator(" "),
                    JSONObject.create()
                        .setString("singleIndent", "  ")
                        .setString("newLine", "\\n")
                        .setString("afterPropertySeparator", " "));
            });
        });
    }
}
