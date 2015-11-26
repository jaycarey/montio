# montior

A clear and concise build status monitor/aggregator compatible with TeamCity.

**Features**

- Support for Team City ci servers.
- ~~Annoying~~ Awesome css transitions.
- Apache 2.0 license.

**Quick Start**

Pre-requisites: maven + java.

1. Configure the 'url' property at the bottom of ```montior-web/pom.xml``` to point at your CI server.
2. Start with ```mvn clean jetty:run```.
