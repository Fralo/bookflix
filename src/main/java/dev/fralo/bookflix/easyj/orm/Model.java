package dev.fralo.bookflix.easyj.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.fralo.bookflix.easyj.annotations.Column;
import dev.fralo.bookflix.easyj.annotations.Id;
import dev.fralo.bookflix.easyj.annotations.Table;

public abstract class Model {
    private static Connection db;

    @Id
    @Column(name = "id")
    protected Integer id;

    // Set database connection (call this once at startup)
    public static void setDatabase(Connection database) {
        db = database;
    }

    // Save instance to database (insert or update)
    public void save() throws SQLException {
        if (id == null) {
            insert();
        } else {
            update();
        }
    }

    private void insert() throws SQLException {
        String tableName = getTableName();
        List<Field> columns = getPersistableFields();
        
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                String.join(", ", getColumnNames(columns)),
                String.join(", ", Collections.nCopies(columns.size(), "?")));

        
        System.out.println("Query: " + sql);
        
        PreparedStatement preparedStatement = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
        setStatementValues(preparedStatement, columns);
        preparedStatement.executeUpdate();
            
        ResultSet rs = preparedStatement.getGeneratedKeys();
        if (rs.next()) {
            this.id = rs.getInt(1);
        }
    }

    private void update() throws SQLException {
        String tableName = getTableName();
        List<Field> columns = getPersistableFields();
        
        String sql = String.format("UPDATE %s SET %s WHERE id = ?",
                tableName,
                String.join(", ", getColumnNames(columns).stream()
                        .map(c -> c + " = ?")
                        .toList()));

        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            
            setStatementValues(stmt, columns);
            stmt.setInt(columns.size() + 1, this.id);
            stmt.executeUpdate();
        }
    }

    // Get a record by ID
    public static <T extends Model> T get(Class<T> modelClass, int id) throws SQLException {
        String tableName = modelClass.getAnnotation(Table.class).name();
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToModel(rs, modelClass);
                }
            }
        }
        return null;
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

    private void setStatementValues(PreparedStatement stmt, List<Field> fields) throws SQLException {
        int index = 1;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                stmt.setObject(index++, field.get(this));
            } catch (IllegalAccessException e) {
                throw new SQLException("Error accessing field value", e);
            }
        }
    }

    private String getTableName() {
        return this.getClass().getAnnotation(Table.class).name();
    }

    private static <T extends Model> T mapResultSetToModel(ResultSet rs, Class<T> modelClass) throws SQLException {
        try {
            T instance = modelClass.getDeclaredConstructor().newInstance();
            for (Field field : modelClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    String columnName = field.getAnnotation(Column.class).name();
                    Object value = rs.getObject(columnName);
                    field.set(instance, value);
                }
                if (field.isAnnotationPresent(Id.class)) {
                    field.set(instance, rs.getInt("id"));
                }
            }
            return instance;
        } catch (Exception e) {
            throw new SQLException("Error creating model instance", e);
        }
    }

    @Override
    public String toString() {
        String fieldList = "";

        for(Field field : this.getClass().getDeclaredFields()) {
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
            fieldList
        );
    }
}