# Fly4s
[![Build Status](https://github.com/geirolz/@PRJ_NAME@/actions/workflows/cicd.yml/badge.svg)](https://github.com/geirolz/@PRJ_NAME@/actions)
[![codecov](https://img.shields.io/codecov/c/github/geirolz/@PRJ_NAME@)](https://codecov.io/gh/geirolz/@PRJ_NAME@)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/db3274b55e0c4031803afb45f58d4413)](https://www.codacy.com/manual/david.geirola/@PRJ_NAME@?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=geirolz/@PRJ_NAME@&amp;utm_campaign=Badge_Grade)
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/com.github.geirolz/@MODULE_NAME@_2.13?server=https%3A%2F%2Foss.sonatype.org)](https://mvnrepository.com/artifact/com.github.geirolz/@MODULE_NAME@)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/geirolz/@PRJ_NAME@&style=flat)](https://mergify.io)
[![GitHub license](https://img.shields.io/github/license/geirolz/@PRJ_NAME@)](https://github.com/geirolz/@PRJ_NAME@/blob/main/LICENSE)


https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/<owner>/@PRJ_NAME@&style=flat

A lightweight, simple and functional wrapper for Flyway using cats-effect.

The most famous library to handle database migrations in Java is for sure Flyway.
It works very well and the community edition has a lot of features as well.
But Flyway APIs are written in the standard OOP paradigm, so throwing exceptions, manually managing resources, etc...

`Fly4s` is a lightweight, simple and functional wrapper for Flyway.
The aim of `Fly4s` is straightforward, wrapping the `Flyway` APIs to guarantee
referential transparency, pureness, resource handling and type safety.
To achieve this goal, `Fly4s` use the typelevel libraries `cats` and `cats-effect`.

- [Getting started](#getting-started)
- [Migrations files](#migrations-files)
- [Defining database configuration](#defining-database-configuration)
- [Instantiating Fly4s](#instantiating-fly4s)
- [Using Fly4s](#using-fly4s)
- [Conclusions](#conclusions)
- [Useful links](#useful-links)
---

### Getting started
Fly4s supports Scala 2.13 and 3.
The first step, import the `Fly4s` library in our SBT project.
So, add the dependency in your `build.sbt` file.
Fly4s depends on Flyway, so we'll have access to Flyway as well

```sbt
libraryDependencies += "@ORG@" %% "@MODULE_NAME@" % "@VERSION@"
```

### Migrations files
As the plain Flyway, we have to create a folder that will contain our migrations scripts, often in `resources/db`.

In this folder, we have to put all our migration. We can have:
- [Baseline migrations](https://flywaydb.org/documentation/tutorials/baselineMigrations)
- [Repeatable migrations](https://flywaydb.org/documentation/tutorials/repeatable)
- [Undo migrations](https://flywaydb.org/documentation/tutorials/undo)

For this example, we are going to use a simple `baseline migration` to add a table to our database schema.

Baseline migrations are versioned and executed only when needed. The version is retrieved from the script file name.

So in this case, `V__001_create_user_table.sql`, the version will be `001`(remember the double underscore after `V`).

Here we have our first migration(for MySQL database)

`resources/db/V__001_create_user_table.sql`
```sql
CREATE TABLE `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(30) NOT NULL,
    `surname` varchar(30) NOT NULL
);
```

### Defining database configuration
A good practice is to create a case class to handle the database configuration(this combined with PureConfig 
or others
config libraries make your app very robust from the configuration point of view)

Let's create a simple case class to achieve this.
```scala mdoc:silent
case class DatabaseConfig(
  url: String,
  user: Option[String],
  password: Option[Array[Char]],
  migrationsTable: String,
  migrationsLocations: List[String]
)
``` 

N.B. apart from the common fields such `url`, `user` and `password` we'll use: `migrationsTable` to define the
Flyway table name(used to store the migration status) and `migrationsLocations` to specify a list
of the folders that contain our migration scripts.

### Instantiating Fly4s
Ok so, now we have all our migration scripts in our folder(`resources/db`), we have `Fly4s` as a dependency 
of our project,
and we have a case class that will contain the database configuration.

To instantiate `Fly4s` we can use `make` to create a new DataSource(under the hood) starting from the parameters
or `makeFor` in order to create it for an already existent `DataSource`(for example from Doobie HikariDataSource).
`make` and `makeFor` method returns a [`Resource`](https://typelevel.org/cats-effect/docs/std/resource) type class
that when released/interrupted safely close the `DataSource` connection.

In both `make` and `makeFor` methods, we can specify the parameter `config`. `Fly4sConfig` is a trivial wrapper for
flyway `Configuration` but instead of having a builder we have a case class.

```scala mdoc
import fly4s.core.*
import fly4s.core.data.*
import cats.effect.*

val dbConfig: DatabaseConfig = DatabaseConfig(
  url                 = "url",
  user                = Some("user"),
  password            = None,
  migrationsTable     = "flyway",
  migrationsLocations = List("db")
)

val fly4sRes: Resource[IO, Fly4s[IO]] = Fly4s.make[IO](
  url                 = dbConfig.url,
  user                = dbConfig.user,
  password            = dbConfig.password,
  config = Fly4sConfig(
    table     = dbConfig.migrationsTable,
    locations = Location.of(dbConfig.migrationsLocations)
  )
)
```

### Using Fly4s
Ok, we have done with the configuration!
We are ready to migrate our database schema with the power of Flyway and the safety of Functional Programming!

We can use `use` or `evalMap` from `Resource` to safely access to the Fly4s instance. In case we have
multiple `Resource`s in our application probably `evalMap` allow us to better combine them using and releasing
them all together at the same time.

We can create a simple util method to do this

```scala mdoc
import fly4s.implicits.*

def migrateDb(dbConfig: DatabaseConfig): Resource[IO, MigrateResult] =
  Fly4s.make[IO](
    url                 = dbConfig.url,
    user                = dbConfig.user,
    password            = dbConfig.password,
    config = Fly4sConfig(
      table     = dbConfig.migrationsTable,
      locations = Location.of(dbConfig.migrationsLocations)
    )
  ).evalMap(_.validateAndMigrate.result)
```

### Conclusions
We have done it! So, to recap, we have:
1. Created a folder under `resources` to put our migrations(`db`)
2. Imported `Fly4s` as a dependency in our project
3. Created a configuration case class to describe our database configuration
4. Instantiated a `Fly4s` instance creating a new `DataSource`
5. Migrated our database using `validateAndMigrate`
6. At the application shutdown/interruption `Resource`(from cats-effect) will safely release the `DataSource`

With a few lines, we have migrated our database safely handling the connection and the configuration.

As flyway, Fly4s provides multiple methods such as:
- validateAndMigrate
- migrate
- undo
- validate
- clean
- info
- baseline
- repair


##### Useful links
- https://flywaydb.org/documentation
- https://typelevel.org/cats/
- https://typelevel.org/cats-effect/
- https://pureconfig.github.io/
