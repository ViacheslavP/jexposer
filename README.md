# **JExposer**
Java Agent that allows gaining access to JavaClasses' public methods via [JSON-RPC 2.0](https://www.jsonrpc.org/specification) protocol on the specified socket.

## How to use
Simply insert

> `-javaagent:path/to/jexposer.jar=class.you.want.to.be.Accessed@<port>`

before the `–jar` parameter of your `java` command, then start sending requests via the specified socket port. *Every instance of the class is treated as a distinct JSON-RPC server.* Note that the agent does not interfere with `*.class` files on the disk.


## Built-in methods
As pointed out in JSON-RPC 2.0 specification, using method names prefixed by `rpc` is highly discouraged. If the `method` field of a sent request is one of the following and the method is listed in the target class, the response will not be sent and the corresponding operation will be performed.

 1. `rpcShutdown` -- shuts down the server and the server's thread
 2. `rpcSwitchSync` -- switches method invocation to synchronous mode (default), meaning that the server will not start parsing a new message before it responds to the ongoing request.
 3. `rpcSwitchAsync` -- switches method invocation to asynchronous mode, meaning that the invocations could be executed in parallel (default parallelism is 4).

## Configuration
Certain properties could be specified
 - `-Djexposer.parallelism` is the number of `ForkJoinPool` parallelism to process the *async* invocations. The default number is `4`.
 -  `-Djexposer.thread.daemon` specifies whether the agent dies with an application or not. The default value is `true`.
 - `-Djexposer.thread.name` agent's thread name. The resulting name will be `JSON-RPC@<specified name>`. The default name is the fully qualified class name.
 - `-Djexposer.thread.priority` defines the agent's thread priority. The default value is `5`.
