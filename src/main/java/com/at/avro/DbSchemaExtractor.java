package com.at.avro;

import static java.util.stream.Collectors.toList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import com.at.avro.config.AvroConfig;

import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.*;
import schemacrawler.utility.SchemaCrawlerUtility;

/**
 * Connects to a db and populates AvroSchema beans for existing tables.
 *
 * @author artur@callfire.com
 */
public class DbSchemaExtractor {

    private final String connectionUrl;
    private final Properties connectionProperties;

    public DbSchemaExtractor(String connectionUrl, String user, String password) {
        this.connectionProperties = new Properties();
        this.connectionProperties.put("nullNamePatternMatchesAll", "true");
        this.connectionProperties.put("user", user);
        this.connectionProperties.put("password", password);

        this.connectionUrl = connectionUrl;
    }

    /** Returns all AvroSchemas that are present in a target DB */
    public List<AvroSchema> getAll(AvroConfig avroConfig) {
        return get(avroConfig, null);
    }

    /** Returns all AvroSchemas that are present in given DB schema */
    public List<AvroSchema> getForSchema(AvroConfig avroConfig, String dbSchemaName) {
        return get(avroConfig, dbSchemaName);
    }

    /** Returns AvroSchemas for each of given tables */
    public List<AvroSchema> getForTables(AvroConfig avroConfig, String dbSchemaName, String... tableNames) {
        return get(avroConfig, dbSchemaName, tableNames);
    }

    /** Returns AvroSchema for a specific table */
    public AvroSchema getForTable(AvroConfig avroConfig, String dbSchemaName, String tableName) {
        List<AvroSchema> schemas = get(avroConfig, dbSchemaName, tableName);
        if (schemas.isEmpty()) {
            return null;
        }
        else {
            return schemas.get(0);
        }
    }

    private List<AvroSchema> get(AvroConfig avroConfig, String dbSchemaName, String... tableNames) {
        try (Connection connection = DriverManager.getConnection(connectionUrl, connectionProperties)) {

            LimitOptionsBuilder limitOptionsBuilder = defaultLimitOptionsBuilder();
            LoadOptionsBuilder loadOptionsBuilder = defaultLoadOptionsBuilder();

            // pull database name out of connection string if specified (MS SQL Server)
            final String databaseName;
            if (connectionUrl.contains("databaseName=")) {
                String str = connectionUrl.substring(connectionUrl.indexOf("databaseName=") + 13);
                databaseName = str.contains("&") ? str.substring(0, str.indexOf("&")) : str;
            } else {
                databaseName = null;
            }

            // filter load by schema name
            if (databaseName != null && dbSchemaName != null) {
                limitOptionsBuilder.includeSchemas(new RegularExpressionInclusionRule(databaseName + "\\.((?i)" + dbSchemaName + ")"));
            } else if (dbSchemaName != null) {
                limitOptionsBuilder.includeSchemas(new RegularExpressionInclusionRule(".*((?i)" + dbSchemaName + ")"));
            }

            // filter load by table names
            if (tableNames != null && tableNames.length > 0) {

                StringBuilder tablePrefix = new StringBuilder();
                if (databaseName != null) {
                    tablePrefix.append(databaseName);
                    tablePrefix.append(".");
                }
                if (dbSchemaName != null) {
                    tablePrefix.append(dbSchemaName);
                    tablePrefix.append(".");
                }

                Set<String> fullTableNames = Arrays.stream(tableNames)
                    .map(tableName -> tablePrefix.toString() + tableName)
                    .collect(Collectors.toSet());
                limitOptionsBuilder.includeTables(fullTableNames::contains);
            }

            SchemaCrawlerOptionsBuilder crawlerOptionsBuilder = SchemaCrawlerOptionsBuilder.builder()
                .withLimitOptionsBuilder(limitOptionsBuilder)
                .withLoadOptionsBuilder(loadOptionsBuilder);

            Catalog catalog = SchemaCrawlerUtility.getCatalog(connection, crawlerOptionsBuilder.toOptions());

            List<Schema> dbSchemas = new ArrayList<>(catalog.getSchemas());
            if (dbSchemaName != null) {
                dbSchemas = dbSchemas.stream()
                        .filter(schema -> dbSchemaName.equalsIgnoreCase(schema.getCatalogName()) || dbSchemaName.equalsIgnoreCase(schema.getName()))
                        .collect(toList());
            }

            List<AvroSchema> schemas = new LinkedList<>();

            for (Schema dbSchema : dbSchemas) {
                for (Table table : catalog.getTables(dbSchema)) {
                    schemas.add(new AvroSchema(table, avroConfig));
                }
            }

            return schemas;
        }
        catch (SQLException e) {
            throw new IllegalArgumentException("Can not get connection to " + connectionUrl, e);
        }
        catch (SchemaCrawlerException e) {
            throw new RuntimeException(e);
        }
    }

    private LimitOptionsBuilder defaultLimitOptionsBuilder() {
        return LimitOptionsBuilder.builder()
            .tableNamePattern("%")
            .includeRoutines(new ExcludeAll())
            .includeColumns(new IncludeAll());
    }

    private LoadOptionsBuilder defaultLoadOptionsBuilder() {
        return LoadOptionsBuilder.builder()
            // Set what details are required in the schema - this affects the
            // time taken to crawl the schema
            .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    }

    private boolean containsIgnoreCase(String[] array, String word) {
        for (String s : array) {
            if (s.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }
}
