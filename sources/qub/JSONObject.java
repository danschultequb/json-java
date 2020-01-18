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

    public static JSONObject create(JSONProperty... properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        return JSONObject.create(Iterable.create(properties));
    }

    public static JSONObject create(Iterable<JSONProperty> properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        return JSONObject.create(properties.iterate());
    }

    public static JSONObject create(Iterator<JSONProperty> properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        return JSONObject.create(
            properties.toMap(
                JSONProperty::getName,
                JSONProperty::getValue));
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
    public boolean contains(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.properties.containsKey(propertyName);
    }

    @Override
    public boolean containsKey(String propertyName)
    {
        return this.contains(propertyName);
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

        return this.get(propertyName)
            .then((JSONSegment propertyValue) -> JSON.as(propertyValue, propertyValueType, JSON.thePropertyNamed(propertyName)).await());
    }

    private <T extends JSONSegment> Result<T> getOrNull(String propertyName, java.lang.Class<T> propertyValueType)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.get(propertyName)
            .then((JSONSegment propertyValue) -> JSON.asOrNull(propertyValue, propertyValueType, JSON.thePropertyNamed(propertyName)).await());
    }

    public Result<JSONObject> getObject(String propertyName)
    {
        return this.get(propertyName, JSONObject.class);
    }

    public Result<JSONObject> getObjectOrNull(String propertyName)
    {
        return this.getOrNull(propertyName, JSONObject.class);
    }

    public Result<JSONArray> getArray(String propertyName)
    {
        return this.get(propertyName, JSONArray.class);
    }

    public Result<JSONArray> getArrayOrNull(String propertyName)
    {
        return this.getOrNull(propertyName, JSONArray.class);
    }

    public Result<JSONBoolean> getBooleanSegment(String propertyName)
    {
        return this.get(propertyName, JSONBoolean.class);
    }

    public Result<Boolean> getBoolean(String propertyName)
    {
        return this.getBooleanSegment(propertyName)
            .then(JSONBoolean::getValue);
    }

    public Result<Boolean> getBooleanOrNull(String propertyName)
    {
        return this.getOrNull(propertyName, JSONBoolean.class)
            .then(JSON::toBooleanOrNull);
    }

    public Result<JSONNumber> getNumberSegment(String propertyName)
    {
        return this.get(propertyName, JSONNumber.class);
    }

    public Result<Double> getNumber(String propertyName)
    {
        return this.getNumberSegment(propertyName)
            .then(JSONNumber::getValue);
    }

    public Result<Double> getNumberOrNull(String propertyName)
    {
        return this.getOrNull(propertyName, JSONNumber.class)
            .then(JSON::toNumberOrNull);
    }

    public Result<Void> getNull(String propertyName)
    {
        return this.get(propertyName, JSONNull.class)
            .then(() -> null);
    }

    public Result<JSONString> getStringSegment(String propertyName)
    {
        return this.get(propertyName, JSONString.class);
    }

    public Result<String> getString(String propertyName)
    {
        return this.getStringSegment(propertyName)
            .then(JSONString::getValue);
    }

    public Result<String> getStringOrNull(String propertyName)
    {
        return this.getOrNull(propertyName, JSONString.class)
            .then(JSON::toStringOrNull);
    }

    public Result<JSONProperty> getProperty(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.get(propertyName)
            .then((JSONSegment propertyValue) -> JSONProperty.create(propertyName, propertyValue));
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
    public Iterable<JSONProperty> getProperties()
    {
        return this.properties.map((MapEntry<String,JSONSegment> entry) -> JSONProperty.create(entry.getKey(), entry.getValue()));
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

    @Override
    public JSONObject set(String propertyName, JSONSegment propertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(propertyValue, "propertyValue");

        this.properties.set(propertyName, propertyValue);

        return this;
    }

    @Override
    public JSONObject setAll(Iterable<MapEntry<String,JSONSegment>> properties)
    {
        PreCondition.assertNotNull(properties, "properties");

        MutableMap.super.setAll(properties);

        return this;
    }

    public JSONObject set(MapEntry<String,JSONSegment> property)
    {
        PreCondition.assertNotNull(property, "property");

        return this.set(property.getKey(), property.getValue());
    }

    public JSONObject setObject(String propertyName, JSONObject propertyValue)
    {
        PreCondition.assertNotNull(propertyValue, "propertyValue");

        return this.set(propertyName, propertyValue);
    }

    public JSONObject setObjectOrNull(String propertyName, JSONObject propertyValue)
    {
        return this.set(propertyName, propertyValue == null ? JSONNull.segment : propertyValue);
    }

    public JSONObject setArray(String propertyName, JSONArray propertyValue)
    {
        PreCondition.assertNotNull(propertyValue, "propertyValue");

        return this.set(propertyName, propertyValue);
    }

    public JSONObject setArrayOrNull(String propertyName, JSONArray propertyValue)
    {
        return this.set(propertyName, propertyValue == null ? JSONNull.segment : propertyValue);
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
    public Result<JSONSegment> remove(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        return this.properties.remove(propertyName)
            .convertError(NotFoundException.class, () -> new NotFoundException("No property exists in this JSONObject with the name: " + Strings.escapeAndQuote(propertyName)));
    }

    @Override
    public String toString()
    {
        return JSONSegment.toString(this);
    }

    @Override
    public Result<Integer> toString(IndentedCharacterWriteStream stream, JSONFormat format)
    {
        PreCondition.assertNotNull(stream, "stream");
        PreCondition.assertNotDisposed(stream, "stream");
        PreCondition.assertNotNull(format, "format");

        stream.setSingleIndent(format.getSingleIndent());

        final String newLine = format.getNewLine();
        final boolean hasNewLine = !Strings.isNullOrEmpty(newLine);

        return Result.create(() ->
        {
            int result = 0;

            result += stream.write('{').await();
            stream.increaseIndent();
            try
            {
                boolean wroteProperty = false;
                for (final JSONProperty property : this.getProperties())
                {
                    if (!wroteProperty)
                    {
                        wroteProperty = true;
                    }
                    else
                    {
                        result += stream.write(',').await();
                    }
                    if (hasNewLine)
                    {
                        result += stream.write(newLine).await();
                    }
                    result += property.toString(stream, format).await();
                }
                if (hasNewLine && wroteProperty)
                {
                    result += stream.write(newLine).await();
                }
            }
            finally
            {
                stream.decreaseIndent();
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
