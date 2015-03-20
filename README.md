# IPE-Toolkit
Collection of utility solutions for building Integrated Project Enviroments(IPEs) in Scala.

## Overview
This library is beeing created to allow rapid creation of reactive, project oriented desktop applications(e.g., eclipse, jmeter, soapUI,and everything compatibile with "project" or "workspace" idea).

## Design
IPE-Toolkit is built upon Akka and consists of actors and event bus to join them. 
Every feature is represented by actor, which you should instatiate with your actor system.

To use a feature usually you have 3 roads you can go:
- use ready-to-go actor from specifis backend module
- use partially implemented actor from core module
- implement your own actor and receive specific messages connected with this feature

## Features
Most of these features are not implemented yet.
### Event Bus
In our design every message between actors is passed through central event bus of type `ipetoolkit.bus.ClassBasedEventBusLike`. It is simple akka.event.EventBus with class-based message classification. 
Every actor takes such bus as a constructor parameter and default value is `ipetoolkit.bus.IPEEventBus`. 

### Tasks
When you're performing some long-running tasks, it is good to show them(and the progress) to the user. 
Package: `ipetoolkit.task`

### Workspace - IN PROGRESS
Allows you to manage yours workspace/project: add and remove entires, serialize and deserialize workspace.
Package: `ipetoolkit.workspace`

### Messages - TODO
Tool for displaying messages/warnings/errors to the user

### Undo - TODO
Tool for managing actions that can be undone and redone.

### Config - TODO
High-level way to acces and modify app configuration.

## Backends
Currently only JavaFX backend is availible

### JavaFX
Every change of JavaFX GUI elements has to be done form JafaFX thread. Due to this most of the actors from this backend will use `ipetoolkit.util.JavaFXDispatcher.Id` as a dispatcher.
Module: toolkit-javafx
