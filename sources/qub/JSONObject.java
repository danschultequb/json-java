package qub;

/**
 * A JSON object.
 */
public class JSONObject implements JSONSegment, MutableMap<String,JSONSegment>
{
    private final MutableMap<String,JSONSegment> properties;

    private JSONObject(MutableMap<String,JSONSegment> properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        this.properties = properties;
    }

    public static JSONObject create(JSONObjectProperty... properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        return JSONObject.create(Iterable.create(properties));
    }

    public static JSONObject create(Iterable<JSONObjectProperty> properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        return JSONObject.create(properties.iterate());
    }

    public static JSONObject create(Iterator<JSONObjectProperty> properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        return JSONObject.create(
            properties.toMap(
                JSONObjectProperty::getName,
                JSONObjectProperty::getValue));
    }

    public static JSONObject create(Map<String,JSONSegment> properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        return new JSONObject(Map.create(properties));
    }

    /**
     * Get whether or not this JSONObject contains a property with the provided name.
     * @param propertyName The name of the property to look for.
     * @return Whether or not this JSONObject contains a property with the provided name.
     */
    public boolean containsProperty(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.properties.containsKey(propertyName);
    }

    @Override
    public boolean containsKey(String propertyName)
    {
        return this.containsProperty(propertyName);
    }

    private static String getWrongTypeExceptionMessage(String propertyName, String expectedTypeName, JSONSegment actualObject)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNullAndNotEmpty(expectedTypeName, "expectedTypeName");
        PreCondition.assertNotNull(actualObject, "actualObject");

        return "Expected the property named " + Strings.escapeAndQuote(propertyName) + " to be a " + expectedTypeName + ", but was a " + Types.getTypeName(actualObject) + " instead.";
    }

    private static <T extends JSONSegment> String getWrongTypeExceptionMessage(String propertyName, java.lang.Class<T> expectedType, JSONSegment actualObject)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(expectedType, "expectedType");
        PreCondition.assertNotNull(actualObject, "actualObject");

        return JSONObject.getWrongTypeExceptionMessage(propertyName, Types.getTypeName(expectedType), actualObject);
    }

    @Override
    public Result<JSONSegment> get(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.properties.get(propertyName)
            .convertError(NotFoundException.class, () -> new NotFoundException("No property found with the name: " + Strings.escapeAndQuote(propertyName)));
    }

    public <T extends JSONSegment> Result<T> get(String propertyName, java.lang.Class<T> propertyValueType)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return Result.create(() ->
        {
            final JSONSegment propertyValueSegment = this.get(propertyName).await();
            final T result = Types.as(propertyValueSegment, propertyValueType);
            if (result == null)
            {
                throw new WrongTypeException(JSONObject.getWrongTypeExceptionMessage(propertyName, propertyValueType, propertyValueSegment));
            }
            return result;
        });
    }

    private <T extends JSONSegment> Result<T> getTypedOrNull(String propertyName, java.lang.Class<T> propertyValueType)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(propertyValueType, "propertyValueType");

        return this.get(propertyName, propertyValueType)
            .catchError(WrongTypeException.class, () -> this.getNull(propertyName).await())
            .convertError(WrongTypeException.class, () ->
                new WrongTypeException(
                    JSONObject.getWrongTypeExceptionMessage(
                        propertyName,
                        Types.getTypeName(propertyValueType) + " or " + Types.getTypeName(JSONNull.class),
                        this.get(propertyName).await())));
    }

    public Result<JSONObject> getObject(String propertyName)
    {
        return this.get(propertyName, JSONObject.class);
    }

    public Result<JSONObject> getObjectOrNull(String propertyName)
    {
        return this.getTypedOrNull(propertyName, JSONObject.class);
    }

    public Result<JSONArray> getArray(String propertyName)
    {
        return this.get(propertyName, JSONArray.class);
    }

    public Result<JSONArray> getArrayOrNull(String propertyName)
    {
        return this.getTypedOrNull(propertyName, JSONArray.class);
    }

    public Result<Boolean> getBoolean(String propertyName)
    {
        return this.get(propertyName, JSONBoolean.class)
            .then(JSONBoolean::getValue);
    }

    public Result<Boolean> getBooleanOrNull(String propertyName)
    {
        return this.getTypedOrNull(propertyName, JSONBoolean.class)
            .then((JSONBoolean value) -> value == null ? null : value.getValue());
    }

    public Result<String> getString(String propertyName)
    {
        return this.get(propertyName, JSONString.class)
            .then(JSONString::getValue);
    }

    public Result<String> getStringOrNull(String propertyName)
    {
        return this.getTypedOrNull(propertyName, JSONString.class)
            .then((JSONString string) -> string == null ? null : string.getValue());
    }

    public Result<Double> getNumber(String propertyName)
    {
        return this.get(propertyName, JSONNumber.class)
            .then(JSONNumber::getValue);
    }

    public Result<Void> getNull(String propertyName)
    {
        return this.get(propertyName, JSONNull.class)
            .then(() -> null);
    }

    public Result<JSONObjectProperty> getProperty(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.get(propertyName)
            .then((JSONSegment propertyValue) -> JSONObjectProperty.create(propertyName, propertyValue));
    }

    /**
     * Get the names of the properties in this JSONObject.
     * @return The names of the properties in this JSONObject.
     */
    public Iterable<String> getPropertyNames()
    {
        return this.properties.getKeys();
    }

    @Override
    public Iterable<String> getKeys()
    {
        return this.getPropertyNames();
    }

    /**
     * Get the values of the properties in this JSONObject.
     * @return The values of the properties in this JSONObject.
     */
    public Iterable<JSONSegment> getPropertyValues()
    {
        return this.properties.getValues();
    }

    @Override
    public Iterable<JSONSegment> getValues()
    {
        return this.getPropertyValues();
    }

    /**
     * Get the properties in this JSONObject.
     * @return The properties in this JSONObject.
     */
    public Iterable<JSONObjectProperty> getProperties()
    {
        return this.properties.map((MapEntry<String,JSONSegment> entry) -> JSONObjectProperty.create(entry.getKey(), entry.getValue()));
    }

    @Override
    public Iterator<MapEntry<String,JSONSegment>> iterate()
    {
        return this.properties.iterate();
    }

    @Override
    public JSONObject clear()
    {
        this.properties.clear();
        return this;
    }

    public JSONObject setProperty(JSONObjectProperty property)
    {
        PreCondition.assertNotNull(property, "property");

        return this.set(property.getName(), property.getValue());
    }

    public JSONObject setBoolean(String propertyName, boolean propertyValue)
    {
        return this.set(propertyName, JSONBoolean.get(propertyValue));
    }

    public JSONObject setBooleanOrNull(String propertyName, Boolean propertyValue)
    {
        return this.set(propertyName, propertyValue == null ? JSONNull.segment : JSONBoolean.get(propertyValue));
    }

    public JSONObject setNumber(String propertyName, long propertyValue)
    {
        return this.set(propertyName, JSONNumber.get(propertyValue));
    }

    public JSONObject setNumber(String propertyName, double propertyValue)
    {
        return this.set(propertyName, JSONNumber.get(propertyValue));
    }

    public JSONObject setNumberOrNull(String propertyName, Integer propertyValue)
    {
        return this.set(propertyName, propertyValue == null ? JSONNull.segment : JSONNumber.get(propertyValue));
    }

    public JSONObject setNumberOrNull(String propertyName, Long propertyValue)
    {
        return this.set(propertyName, propertyValue == null ? JSONNull.segment : JSONNumber.get(propertyValue));
    }

    public JSONObject setNumberOrNull(String propertyName, Float propertyValue)
    {
        return this.set(propertyName, propertyValue == null ? JSONNull.segment : JSONNumber.get(propertyValue));
    }

    public JSONObject setNumberOrNull(String propertyName, Double propertyValue)
    {
        return this.set(propertyName, propertyValue == null ? JSONNull.segment : JSONNumber.get(propertyValue));
    }

    public JSONObject setString(String propertyName, String propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(propertyValue, "propertyValue");

        return this.set(propertyName, JSONString.get(propertyValue));
    }

    public JSONObject setStringOrNull(String propertyName, String propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.set(propertyName, propertyValue == null ? JSONNull.segment : JSONString.get(propertyValue));
    }

    @Override
    public JSONObject set(String propertyName, JSONSegment propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(propertyValue, "propertyValue");

        this.properties.set(propertyName, propertyValue);

        return this;
    }

    @Override
    public Result<JSONSegment> remove(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.properties.remove(propertyName);
    }

    @Override
    public String toString()
    {
        return JSONSegment.toString(this);
    }

    @Override
    public Result<Integer> toString(IndentedCharacterWriteStream stream)
    {
        PreCondition.assertNotNull(stream, "stream");
        PreCondition.assertNotDisposed(stream, "stream");

        return Result.create(() ->
        {
            int result = 0;

            result += stream.write('{').await();
            boolean firstProperty = true;
            for (final JSONObjectProperty property : this.getProperties())
            {
                if (firstProperty)
                {
                    firstProperty = false;
                }
                else
                {
                    result += stream.write(',').await();
                }
                result += property.toString(stream).await();
            }
            result += stream.write('}').await();

            PostCondition.assertGreaterThanOrEqualTo(result, 2, "result");

            return result;
        });
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof JSONObject && this.equals((JSONObject)rhs);
    }

    public boolean equals(JSONObject rhs)
    {
        return rhs != null &&
            this.properties.equals(rhs.properties);
    }
}
