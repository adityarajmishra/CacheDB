# CacheDB

CacheDB is a Java-based key-value store application designed to provide efficient data storage and retrieval. It implements various features such as thread safety, lambda expressions, and Java streams to offer a modern and robust solution for data management.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
- [Installation](#installation)
- [Usage](#usage)
- [Command Reference](#command-reference)
- [Testing](#testing)
- [Architecture](#architecture)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Key-Value Storage**: Efficiently store and retrieve data using unique keys.
- **Thread Safety**: Safe to use in multi-threaded environments.
- **Lambda Expressions**: Leverage Java's functional programming capabilities for enhanced code readability and performance.
- **Streams**: Use Java Streams for processing collections of data.
- **Support for Various Commands**:
  - `PUT`, `GET`, `MGET`, `MPUT`, `DEL`, `MDEL`, `SAVE`, `POP`, and more.
- **Dynamic Data Structures**: Store user data with associated metadata (username and userdata).
- **Flexible TTL (Time to Live)**: Optionally set expiration time for cached entries.

## Getting Started

To get started with CacheDB, follow the instructions below to install and run the application.

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/CacheDB.git
   cd CacheDB
   ```

2. **Build the Project**:
   Make sure you have [Gradle](https://gradle.org/install/) installed. Then, build the project using:
   ```bash
   ./gradlew build
   ```

### Usage

To run the CacheDB application:

```bash
./gradlew run
```

You will be presented with a command line interface (CLI) where you can enter commands to interact with the key-value store.

### Command Reference

Here are some of the commands you can use:

- **PUT**: Adds a new entry to the database.
  - Example: `PUT user001 { username: SpiderMan, userdata: PeterParker }`
  
- **GET**: Retrieves the value associated with a key.
  - Example: `GET user001`
  
- **MGET**: Retrieves values for multiple keys.
  - Example: `MGET [ user001, user002 ]`
  
- **MPUT**: Adds multiple entries at once.
  - Example: `MPUT [ user001 { username: SpiderMan, userdata: PeterParker }, user002 { username: IronMan, userdata: TonyStark } ]`
  
- **DEL**: Deletes a single entry.
  - Example: `DEL user001`
  
- **MDEL**: Deletes multiple entries.
  - Example: `MDEL [ user001, user002 ]`
  
- **SAVE**: Saves a specific entry to persistent storage.
  - Example: `SAVE user001`
  
- **POP**: Removes and returns an entry.
  - Example: `POP user001`

- **EXIT**: Exits the application.

### Testing

To run the tests for CacheDB, use the following command:

```bash
./gradlew test
```

You can view the test report in the `build/reports/tests/test/index.html` file.

### Architecture

CacheDB is designed with a focus on modularity and scalability. The architecture consists of the following components:

- **Command Processor**: Parses and executes user commands.
- **Cache Storage**: Manages the in-memory storage of key-value pairs with support for TTL.
- **Data Structures**: Utilizes concurrent data structures to ensure thread safety.
- **Persistence Layer**: Provides mechanisms for saving and loading data.

### Contributing

Contributions are welcome! If you have suggestions for improvements or new features, please fork the repository and submit a pull request.

### License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
