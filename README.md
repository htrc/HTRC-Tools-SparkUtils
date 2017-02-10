# HTRC-Tools-SparkUtils
Library that adds useful error handling and non-serializable object management capabilities to
Apache Spark applications.

_Note: This work expands on a [previous effort](https://github.com/nerdammer/spark-additions) 
by @nerdammer._


# Build
* To generate a package that can be referenced from other projects:  
  `sbt test package`  
  then find the result in `target/scala-2.11/` (or similar) folder.

# Usage

## SBT  
`libraryDependencies += "org.hathitrust.htrc" %% "spark-utils" % "1.0.2"`

## Maven
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>spark-utils_2.11</artifactId>
    <version>1.0.2</version>
</dependency>
```
