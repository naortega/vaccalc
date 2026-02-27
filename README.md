# VacCalc

## Building

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Compile

```bash
mvn clean compile
```

### Package

```bash
mvn package
```

### Run

Using Maven:

```bash
mvn exec:java@com.calendar.CalendarApp
```

Using Java directly:

```bash
java -jar target/vaccalc-1.0.0.jar
```

## License

This project is licensed under the Zlib license. See the [license](LICENSE)
file for more information.
