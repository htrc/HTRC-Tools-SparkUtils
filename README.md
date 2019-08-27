# HTRC-Tools-SparkUtils
Library that adds useful error handling and non-serializable object management capabilities to
Apache Spark applications.

_Note: This work expands on a [previous effort](https://github.com/nerdammer/spark-additions) 
by @nerdammer._


# Build
* To generate a package that can be referenced from other projects:  
  `sbt "+package"`  
  This will cross-build for Scala 2.12.x and 2.11.x; find the result in `target/scala-2.12/` (or similar) folder.

# Usage

## SBT  
`libraryDependencies += "org.hathitrust.htrc" %% "spark-utils" % "1.3"`

## Maven
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>spark-utils_2.12</artifactId>
    <version>1.3</version>
</dependency>
```
