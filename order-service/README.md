# Service Order Service

## Database Setup

### On Local Machine

1.  Connect to your PostgreSQL database and create the `service_order_query` database using the command `CREATE DATABASE service_order_query`.

    ```sh
    $ psql
    psql (11.5)
    Type "help" for help.

    =# CREATE DATABASE service_order_query
    CREATE DATABASE
    ```

2.  Create a `gradle.properties` file in the same directory as `build.gradle`.
3.  Provide the following details in the `gradle.properties` file.

    ```
    liquibaseTaskPrefix=liquibase
    mainUrl=jdbc:postgresql://localhost:5432/service_order_query
    username=<DB username>
    password=<DB Password>
    runList=main
    ```

    **NOTE**: This project uses the liquibase gradle plugin. This plugin creates a Gradle task for each command supported by Liquibase. The `liquibaseTaskPrefix` option will tell the liquibase plugin to capitalize the task name and prefix it with the given prefix. For example, if you put `liquibaseTaskPrefix=liquibase` in `gradle.properties`, then this plugin will create tasks named `liquibaseUpdate`, `liquibaseTag`. `liquibaseTaskPrefix` is optional, but recommended to avoid gradle task name collisions.

4.  At this point, you've created the database and provided the connection parameters and liquibase configurations in the `gradle.properties` file.
    You can now run the `liquibaseUpdate` command to set up the database:

    ```sh
    cd job-service
    ./gradlew liquibaseUpdate
    `````

    In case you did not set liquibaseTaskPrefix in the `gradle.properties` file, you would run:

    ```sh
    ./gradlew update
    ```

    You can overwrite the parameters in the `gralde.properties` file from the command line

    ```sh
     ./gradlew liquibaseUpdate -PmainUrl=jdbc:postgresql://localhost:5432/service_order_query -Pusername=john.doe -Ppassword=123456 -PrunList=main
    ```