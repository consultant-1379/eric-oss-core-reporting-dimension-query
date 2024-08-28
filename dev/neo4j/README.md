# Neo4j Dev Environment Setup

[TOC]

This page provides instructions on operating Neo4j using Docker/Podman for neo4j feature development, inclusive of prepopulated RAN and Core data.



## Quick Start

To execute this setup, ensure Docker or Podman is installed and initiated in your environment. Once the `docker` or `podman` command is accessible in your PATH, the `run.sh` script will select one based on availability.


Tip: Most developers on MacOS using Podman. Installation instruction can be found at [Podman Installation](https://podman.io/docs/installation#macos).

```bash
# Navigate to the dev/neo4j folder in this project. The path may differ based on your project's location.
cd ~/gerrit/eric-oss-core-reporting-dimension-query/dev/neo4j
./run.sh
```

## Details

Upon successful initiation, the Neo4j container provides the following default configurations:
- Neo4j UI URL: http://localhost:7474/browser/
- Neo4j bolt URL: bolt://localhost:7687
- Username: neo4j
- Password: password


The aforementioned script initiates Neo4j with Core and RAN data, all injected into the same `neo4j` database. The [Multi-database](https://neo4j.com/docs/cypher-manual/4.0/administration/databases/#administration-databases-create-database) feature, exclusive to the enterprise edition, does not support separate database injection with Neo4j Docker. If you wish to test only the Core or RAN data, you are welcome to modify the `config/apoc.conf`.
