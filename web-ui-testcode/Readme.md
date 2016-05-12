# app-amq-management

Short summary of what the application does.

## Configuration & Setup

This is a standard `app` application with the following requirements:

Dependency | Required (Version)
--- | ---
Gradle | 2.4
Java | 1.8

### Dependency Clients

The application configures one client:

* `intapi-XXX` `Client` This is a standard intapi client which is responsible for something

## Successful Operation

If everything works as intended this template application will create and notify Template objects.

### Failure Reasons

The following error conditions are recognized and used in the collected metrics

Failure Reason Code | Entity | Explanation
--- | --- | --- 
`UNEXPECTED_ERROR` | Template | Something unexpeted occured
`ALREADY_NOTIFIED` | Template | A template was previously notified already and can't be notified again

## Metrics

In addition to the standard IntApi metrics, the following data is added to every NewRelic Transaction:

Attribute | Type | Meaning | Restrictions
--- | --- | --- | --- | ---
`templateId` | Text | The ID of the Template that was processed during this transaction | This is only present if a template ID was given

### Custom Events

A custom `TemplateModification` event is created for every modification operation on a template, containing all the above attributes as well as the standard IntApi metrics

### Insight Application

The custom events and data can be examined at the [app-amq-management Insight Application](https://insights.newrelic.com/apps/accounts/63648/app-amq-management app-amq-management Insight Application)


## Development environment

Download and install [Node.js](https://nodejs.org/dist/v5.8.0/node-v5.8.0.pkg) version 5.8.0

Run `gradle frontend:npmInstall` to setup front-end and install dependencies.


### Running the Application

**Run the app server**
`gradle server:runInMemory`
By default starts on https://localhost:8465
To run dev server on different port `gradle -Papplication.port=8466 server:runInMemory`

**Run the dev server**
`gradle frontend:runDevServer`
By default starts on http://localhost:8453
To run dev server on different port `gradle -Pdev.server.port=3066 frontend:runDevServer`
To run dev server in debug mode `gradle frontend:debugDevServer`. Debug configuration for IntelliJ [NodeJS Remote Debug](https://www.jetbrains.com/idea/help/run-debug-configuration-node-js-remote-debug.html).

