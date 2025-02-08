package dev.fralo.bookflix.easyj.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.fralo.bookflix.easyj.annotations.orm.Table;

public class QueryBuilder<T extends Model> {
    private final Class<T> modelClass;
    private final StringBuilder whereClause = new StringBuilder();
    private final List<Object> parameters = new ArrayList<>();
    private static Connection databaseConnection;

    public QueryBuilder(Class<T> modelClass, Connection connection) {
        this.modelClass = modelClass;
        this.databaseConnection = connection;
    }

    public QueryBuilder<T> where(String column, Object value) {
        return this.where(column, "=", value);
    }

    public QueryBuilder<T> where(String column, String operation, Object value) {
        if (this.whereClause.length() == 0) {
            this.whereClause.append(" WHERE ");
        } else {
            this.whereClause.append(" AND ");
        }

        this.whereClause.append(column).append(" ").append(operation).append(" ? ");
        parameters.add(value);
        return this;
    }

    public T get() throws SQLException {
        String sql = "SELECT * FROM ".concat(this.getTableName()).concat(whereClause.toString()).concat(" LIMIT 1");

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            setParameters(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Model.mapResultSetToModel(rs, modelClass);
            }

            return null;
        }
    }

    public List<T> all() throws SQLException {
        String sql = "SELECT * FROM ".concat(this.getTableName()).concat(whereClause.toString());

        List<T> models = new ArrayList<>();
        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            setParameters(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                models.add(Model.mapResultSetToModel(rs, modelClass));
            }
        }
        return models;
    }

    public int insert(List<String> columns, List<Object> values) throws SQLException {
        String sql = String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                this.getTableName(),
                String.join(", ", columns),
                String.join(", ", Collections.nCopies(columns.size(), "?")));
        for (Object value : values) {
            parameters.add(value);
        }

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                System.out.println(rs.getInt(1));
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void update(int id, List<String> columns, List<Object> values) throws SQLException {
        String sql = String.format(
                "UPDATE %s SET %s WHERE id = ?",
                this.getTableName(),
                String.join(", ", columns.stream().map(c -> c + " = ?").toList()));

        for (Object value : values) {
            parameters.add(value);
        }

        parameters.add(id);

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            setParameters(stmt);
            stmt.executeUpdate();
        }
    }

    public void delete() throws SQLException {
        String sql = "DELETE FROM ".concat(this.getTableName()).concat(whereClause.toString());

        try (PreparedStatement stmt = databaseConnection.prepareStatement(sql)) {
            setParameters(stmt);
            stmt.executeUpdate();
        }
    }

    private String getTableName() {
        return modelClass.getAnnotation(Table.class).name();
    }

    private void setParameters(PreparedStatement stmt) throws SQLException {
        for (int i = 0; i < this.parameters.size(); i++) {
            stmt.setObject(i + 1, this.parameters.get(i));
        }
    }

}
