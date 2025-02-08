package dev.fralo.bookflix.easyj.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.fralo.bookflix.easyj.annotations.orm.Column;
import dev.fralo.bookflix.easyj.annotations.orm.Id;

public abstract class Model {

    private static Connection databaseConnection;

    @Id
    @Column(name = "id")
    protected Integer id;

    // Set database connection (call this once when bootstrapping)
    public static void setDatabase(Connection connection) {
        databaseConnection = connection;
    }

    // Save instance to database (insert or update)
    public void save() throws SQLException, IllegalAccessException {
        if (this.id == null) {
            insert();
        } else {
            update();
        }
    }

    public void delete() throws SQLException {
        if (id == null) {
            throw new IllegalStateException("Cannot delete a model without an ID.");
        }

        queryBuilder(this.getClass()).where("id", "=", "value").delete();
    }

    private int insert() throws SQLException, IllegalAccessException {
        List<Field> columns = getPersistableFields();

        return queryBuilder(this.getClass())
                .insert(getColumnNames(columns), getColumnValues(columns));
    }

    private void update() throws SQLException, IllegalAccessException {
        if (id == null) {
            throw new IllegalStateException("Cannot update a model without an ID.");
        }

        List<Field> columns = getPersistableFields();

        queryBuilder(this.getClass())
                .update(this.id, getColumnNames(columns), getColumnValues(columns));
    }

    // Helper methods
    private List<Field> getPersistableFields() {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Column.class) && !f.isAnnotationPresent(Id.class))
                .toList();
    }

    private List<String> getColumnNames(List<Field> fields) {
        return fields.stream()
                .map(f -> f.getAnnotation(Column.class).name())
                .toList();
    }

    private List<Object> getColumnValues(List<Field> fields) throws IllegalAccessException {
        List<Object> values = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            values.add(field.get(this));
        }

        return values;
    }

    public static <T extends Model> QueryBuilder<T> queryBuilder(Class<T> modelClass) {
        return new QueryBuilder<>(modelClass, databaseConnection);
    }

    protected static <T extends Model> T mapResultSetToModel(ResultSet rs, Class<T> modelClass) throws SQLException {
        try {
            T instance = modelClass.getDeclaredConstructor().newInstance();

            List<Field> allFields = getAllFields(modelClass);

            for (Field field : allFields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) {
                    String columnName = field.getAnnotation(Column.class).name();
                    Object parsedValue = parseValueBasedOnType(rs, field, columnName);
                    field.set(instance, parsedValue);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new SQLException("Error creating model instance", e);
        }
    }

    private static List<Field> getAllFields(Class<?> sourceClass) {
        List<Field> fields = new ArrayList<>();
        while (sourceClass != null) {
            fields.addAll(Arrays.asList(sourceClass.getDeclaredFields()));
            if (sourceClass == Model.class) {
                // Here ends our intrested classes
                break;
            }

            sourceClass = sourceClass.getSuperclass();
        }

        return fields;
    }

    /**
     * Parses the value based on the field's type, including special handling
     * for dates.
     */
    private static Object parseValueBasedOnType(ResultSet rs, Field field, String columnName) throws SQLException {
        Class<?> fieldType = field.getType();
        Object value = rs.getObject(columnName);

        if (value == null) {
            return null;
        }

        // Trying to implement Java Dates, to be verified
        // if (fieldType == java.util.Date.class) {
        // return rs.getTimestamp(columnName);
        // } else if (fieldType == java.time.LocalDate.class) {
        // java.sql.Date date = rs.getDate(columnName);
        // return date != null ? date.toLocalDate() : null;
        // } else if (fieldType == java.time.LocalDateTime.class) {
        // java.sql.Timestamp timestamp = rs.getTimestamp(columnName);
        // return timestamp != null ? timestamp.toLocalDateTime() : null;
        // }
        // Handles primitives and other types
        return switch (fieldType.getSimpleName()) {
            case "int", "Integer" ->
                rs.getInt(columnName);
            case "long", "Long" ->
                rs.getLong(columnName);
            case "double", "Double" ->
                rs.getDouble(columnName);
            case "float", "Float" ->
                rs.getFloat(columnName);
            case "boolean", "Boolean" ->
                rs.getBoolean(columnName);
            default ->
                value;
        };
    }

    @Override
    public String toString() {
        String fieldList = "";

        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                fieldList += field.getName() + "=" + field.get(this) + ",";
            } catch (IllegalAccessException e) {
                System.err.println("Cannot access field " + field.getName());
            }
        }

        return String.format(
                "%s(%s)",
                this.getClass().getName(),
                fieldList);
    }
}
