[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/htrc/HTRC-Tools-SparkUtils/ci.yml?branch=develop)](https://github.com/htrc/HTRC-Tools-SparkUtils/actions/workflows/ci.yml)
[![codecov](https://codecov.io/github/htrc/HTRC-Tools-SparkUtils/branch/develop/graph/badge.svg?token=O71L5M1ORN)](https://codecov.io/github/htrc/HTRC-Tools-SparkUtils)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/htrc/HTRC-Tools-SparkUtils?include_prereleases&sort=semver)](https://github.com/htrc/HTRC-Tools-SparkUtils/releases/latest)

# HTRC-Tools-SparkUtils
Library that adds useful error handling and non-serializable object management capabilities to
Apache Spark applications.

_Note: This work expands on a [previous effort](https://github.com/nerdammer/spark-additions) 
by @nerdammer._


# Build
**Note:** Must use a Java version supported by Apache Spark (8..11 at this time) for running the tests

* To generate a package that can be referenced from other projects:  
  `sbt "+package"`  
  This will cross-build for Scala 2.13.x and 2.12.x; find the result in `target/scala-2.13/` (or similar) folder.

# Usage

## SBT  
`libraryDependencies += "org.hathitrust.htrc" %% "spark-utils" % VERSION`

## Maven
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>spark-utils_2.13</artifactId>
    <version>VERSION</version>
</dependency>
```
