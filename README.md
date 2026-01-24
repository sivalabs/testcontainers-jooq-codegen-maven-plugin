# testcontainers-jooq-codegen-maven-plugin

The `testcontainers-jooq-codegen-maven-plugin` simplifies the jOOQ code generation
by using [Testcontainers](https://www.testcontainers.org/) and applying Flyway or Liquibase database migrations.

[![Build](https://github.com/sivalabs/testcontainers-jooq-codegen-maven-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/sivalabs/testcontainers-jooq-codegen-maven-plugin/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/dev.sivalabs/testcontainers-jooq-codegen-maven-plugin?label=latest-version)](https://central.sonatype.com/artifact/dev.sivalabs/testcontainers-jooq-codegen-maven-plugin)

## Prerequisites
* JDK 21+
* Docker

## Usage
To use the plugin, add the following configuration to your `pom.xml` file.

Check the latest version [here](https://central.sonatype.com/artifact/dev.sivalabs/testcontainers-jooq-codegen-maven-plugin).

Example with `PostgreSQL` and minimal configuration with `Flyway` and `JOOQ`

```xml
<properties>
    <testcontainers-jooq-codegen-maven-plugin.version>1.0.0</testcontainers-jooq-codegen-maven-plugin.version>
    <testcontainers.version>2.0.3</testcontainers.version>
    <jooq.version>3.20.10</jooq.version>
    <postgresql.version>42.7.9</postgresql.version>
</properties>

<plugin>
    <groupId>dev.sivalabs</groupId>
    <artifactId>testcontainers-jooq-codegen-maven-plugin</artifactId>
    <version>${testcontainers-jooq-codegen-maven-plugin.version}</version>
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers-postgresql</artifactId>
            <version>${testcontainers.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgresql.version}</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>generate-jooq-sources</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <phase>generate-sources</phase>
            <configuration>
                <database>
                    <type>POSTGRES</type>
                </database>
                <flyway/>
                <jooq>
                    <generator>
                        <database>
                            <includes>.*</includes>
                            <inputSchema>public</inputSchema>
                        </database>
                        <target>
                            <packageName>org.jooq.codegen.maven.example</packageName>
                            <directory>target/generated-sources/jooq</directory>
                        </target>
                    </generator>
                </jooq>
            </configuration>
        </execution>
    </executions>
</plugin>
```

- Plugin migration and code generation could be skipped using `skip` property
- If you need to reuse the existing database connection - take a look at [Jooq section](#Jooq)

To use **SNAPSHOT** versions from **GitHub Maven Registry**:

```xml
<pluginRepositories>
    <pluginRepository>
        <id>github</id>
        <url>https://maven.pkg.github.com/sivalabs/testcontainers-jooq-codegen-maven-plugin</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>
</pluginRepositories>
```

## Database Configuration

To configure a target database, you need to specify at least database `type` property.

#### Properties

| Parameter      | Required | Default value                                     | Description                           |
|----------------|----------|---------------------------------------------------|---------------------------------------|
| type           | yes      |                                                   | One of `POSTGRES`, `MYSQL`, `MARIADB` |
| containerImage |          | `postgres:18-alpine`, `mysql:9.6.0`, `mariadb:12` | Overrides the default docker image    |
| username       |          | Provided from database container if not specified | Database username for container       |
| password       |          | Provided from database container if not specified | Database password for container       |
| databaseName   |          | Provided from database container if not specified | Database name for container           |

#### `database` block configuration

```xml

<database>
    <type>POSTGRES</type>
    <containerImage>postgres:18-alpine</containerImage>
    <username>test</username>
    <password>test</password>
    <databaseName>test</databaseName>
</database>
```

## Migration tools:

### Flyway

You can use the [Flyway configuration properties](https://documentation.red-gate.com/fd/flyway-namespace-277578913.html) to customize the defaults.

**Zero configuration with defaults:**

```xml
<flyway/>
```

The default configuration uses the database connection properties from the Testcontainers datasource 
and loads the Flyway migrations from `classpath:/db/migration`. The remaining properties will be default Flyway property values.

**Customize Flyway Properties:**

```xml
<flyway>
    <defaultSchema>bank</defaultSchema>
    <createSchemas>true</createSchemas>
    <table>my_custom_history_table</table>
    <locations>
        filesystem:src/main/resources/db/migration/postgres,
        filesystem:src/main/resources/db/migration/postgresql
    </locations>
</flyway>
```

### Liquibase

Liquibase's following configuration properties are supported:

| Property                       | type   | default                                                                                                                        |
|--------------------------------|--------|--------------------------------------------------------------------------------------------------------------------------------|
| changeLogPath                  | String | if changeLogDirectory is provided - db.changelog-root.xml, otherwise - `src/main/resources/db/changelog/db.changelog-root.xml` |
| changeLogDirectory             | String | projectBaseDir                                                                                                                 |
| parameters                     | Map    |                                                                                                                                |
| defaultSchemaName              | String |                                                                                                                                |
| liquibaseSchemaName            | String |                                                                                                                                |
| databaseChangeLogTableName     | String |                                                                                                                                |
| databaseChangeLogLockTableName | String |                                                                                                                                |

Reference to Liquibase documentation - https://docs.liquibase.com/

**Zero configuration with defaults:**

```xml
<liquibase/>
```

The default configuration uses the database connection properties from the Testcontainers datasource
and uses the above-mentioned defaults.

**Customize Liquibase Properties:**

```xml
<liquibase>
    <changeLogPath>db.changelog-root.yml</changeLogPath>
    <changeLogDirectory>src/main/resources/db/postgres/changelog</changeLogPath>
    <defaultSchemaName>custom</defaultSchemaName>
</liquibase> 
```

### JOOQ

You can configure the following jOOQ properties similar to the official [jooq-codegen-maven](https://www.jooq.org/doc/3.20/manual/code-generation/codegen-execution/codegen-maven/)

- `generator` - jOOQ code generation settings. See https://www.jooq.org/doc/latest/manual/code-generation/codegen-configuration for all the supporting configuration properties.  
- `jdbc` - If it has all the necessary JDBC parameters (`url`, `username`, `password`), it will use the existing database, and no container will be spun up.  
- `baseDir` - directory relative to which generated sources will be generated , `{project.basedir}` - default
- `configurationFiles` / `configurationFile` - are not supported yet   

#### `jooq` block configuration

```xml
<jooq>
    <generator>
        <database>
            ...
        </database>
    </generator>
    <jdbc>
        ....
    </jdbc>
</jooq>
```

#### Plugin dependencies configuration

Depending on the database you are using, you need to add the database driver dependency to the plugin:

```xml
<plugin>
    <groupId>dev.sivalabs</groupId>
    <artifactId>testcontainers-jooq-codegen-maven-plugin</artifactId>
    <version>${testcontainers-jooq-codegen-maven-plugin.version}</version>
    <dependencies>
        <!-- if using postgresql -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgresql.version}</version>
        </dependency>
        <!-- if using mysql -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <!-- if using mariadb -->
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
            <version>${mariadb.version}</version>
        </dependency>
    </dependencies>
    <executions>
        ...
    </executions>
</plugin>
```

## Examples

[MariaDB + Flyway](examples/mariadb-flyway-example )   
[MySQL + Flyway](examples/mysql-flyway-example )   
[Postgres + Flyway](examples/postgres-flyway-example )   
[Postgres + Liquibase](examples/postgres-liquibase-example )

### Try with an example application

```shell
$ cd examples/postgres-flyway-example
$ mvn clean package
```

The JOOQ code should be generated under `target/generated-sources/jooq` folder.

## CREDITS

This plugin is heavily based on the official https://github.com/jOOQ/jOOQ/tree/main/jOOQ-codegen-maven.
