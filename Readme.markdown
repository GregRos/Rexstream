# Rexstream
Rexstream is a library for reactive programming and data binding. In simple terms, it is an elegant solution for the tricky problem of synchronizing the values of related objects with every change.

It is inspired by other implementations of data binding, such as those found in AngularJS and WPF, but introduces a lot of platform-independent logic and structure that can be adapted to any environment.

It is also inspired by FRP (functional reactive programming), and is similar in some ways to other libraries, such as [Scala.rx](https://github.com/lihaoyi/scala.rx).

Rexstream uses primarily push-based notification internally.

RxFlow is meant to be:

1. Powerful
2. Language-integrated
2. Extensible
3. Intuitive
4. Type-safe
4. Thread-safe
4. Elegant

## Stage
This library is currently in an experimental stage.

## Implementation
RxFlow is implemented in Scala, but will be ported to other environments in the future, such as C# and Javascript.

# Rex Overview
Borrowing some terminology from Scala.rx, Rexstream uses smart variables called Rexes. 

A Rex is a smart variable that supports reading and/or writing, change notification, change propagation, metadata storage, and a few other cool things. Each such Rex may in turn depend on any number of other Rexes, reading from some and writing to others (or both).

There are different kinds of Rexes, each storing different kinds of data. They are distinguished by the traits they implement.

1. `RexValue` is a Rex that manages a single value. Such rexes are also called *scalar*.
2. `RexList` is a Rex that manages a list of objects. Rather than storing a collection-typed value, it acts as an observable and bindable collection itself (specifically, it implements `mutable.Buffer`).

We'll first only talk about scalar Rexes, since they are a lot simpler to understand.

Here is an example of scalar Rexes in use:

	//We create a smart Rex variable
    val double : RexValue[Double] = Rex.var_>(10)
    //We apply a transformation, getting another Rex:
	val integer : RexValue[Int] = floating.convert_>(x => x.toInt, x => x.toDouble)
	
	integer.value = 5
    //now double == 5.0
    double.value = 3.0
    //now integer == 3


In the above example, we create a variable rex (called `Var`, even though the type is hidden) and a conversion rex (called `Convert`). The conversion rex is created via a *Rex transformation* called `convert_>`. Rex transformations are the main way by which you work with Rexes, and they can be quite powerful and complex. 
	
Note that the `convert_>` transformation accepts both an input converter and an output converter. This allows the resulting Rex to also be bi-directional, updating the source on every change.

The suffix `_>` indicates that the result of an operation (typically on an Rx) is also an Rx and may be further enhanced and supports Rx binding (something else that we haven't discussed yet).

The really awesome thing about the `Convert` Rex (and the `convert_>` transformation) is that it can depend on an *additional* Rex for the actual conversion. This makes the whole structure highly reactive and dynamic, allowing you to change the conversion used according to user action for example.

# Rexstreams
A Rexstream is not an actual object, but a concept. In the previous example, you may have noticed that the `convert_>` transformation created a Rex that depends on `integer` for its definition, resulting in a kind of data stream. This relationship can be put into a diagram:

![](https://i.imgur.com/P8uIfxQ.png)

Chains of Rexes lie at the core of the RexStream library. Each chain is structurally immutable (you can't disconnect `toDouble` and connect it to another `Rex` -- it's attached to `number` and that's it). This makes it possible for the same `Var` be a dependency of any number of other Rexes, and each such Rex may be used to write to it:

![](https://i.imgur.com/dKSGgqX.png)

It is important to mention that the actual type of the Rex object is obscured -- all Rexes are the same as far as you're concerned. You can only tell them apart using Inspection (discussed in a later section), and you're not supposed to use this information for functional purposes.

There may be more than 2 members in a Rexstream, since a `Convert` Rx can be a source just like a `Var` Rx (all of them are practically the same). Also, recall that a `Convert` Rx takes another Rx which specifies the conversion. In this example, it is used in order to format a floating point number in a dynamic, user-defined way:

![](http://i.imgur.com/siEAbgF.png)

In the above example, `toString` has multiple dependencies. Changes are propagates back to `toDouble`, but not to `formatProvider` -- that one is only read from. A change in either of the dependencies causes `toString` to update its value.

Oh, and each member of the stream is still a fully functional Rex. You can read or write to it manually if you want, and the changes will propagate accordingly.

## RxList
RexStream treats Rexs of collections differently than scalar (single-value) Rexs. Of course you're free to define a `RexValue[mutable.Buffer[Int]]`, but this isn't a very good idea, since it won't monitor the collection for changes and you'll miss out on lots of features.

The `RexList[T]` trait inherits from `mutable.Buffer[T]`, acting as an observable collection. However, internally, an `RxList[T]` is an observable collection *of scalar Rxs*. This means it's possible to monitor mutations in individual items, as well as in the collection as a whole.

`RexList` supports incremental change notification (so when a change occurs, it reports which items exactly changed).

Applying transformations on a list Rex usually involves applying some kind of component transformation on a per-item basis. For example, the following code maps a collection of integers to a collection of doubles, maintaining bi-directional change propagation:

	val intList = RxList(1, 2, 3, 4)
	val doubleList = intList.Map(rx => rx.convert_>(x => x.toDouble, x => x.toInt))
	
![](http://i.imgur.com/i1vSRuZ.png)

Updating, adding, or removing from `doubleList` still works and has the desired effect, propagating changes back to the source.

There is a slew of other transformations, such as filtering and sorting, that still allow changes to the transformed collection to be translated back to the original. These transformations often accept a configuration Rx, such as one specifying sort or filter criteria.

Another powerful transformation is *aggregation*, which allows you to apply an operation on the list Rex, yielding a scalar (a `ReXValue`). In this case, the result is read-only.

![](http://i.imgur.com/OpHlem4.png)
	
# Rex Binding
So far we've discussed various transformations that let you compose new Rexes from old ones in a structurally immutable manner. And while this is a very big part of the library's purpose, it's not all of it. It's also about data binding.

Rex binding is not the same as Rex composition. A binding is a relationship between two independent Rexes that synchronizes their values together. In effect, it's an extra link that holds two chains of Rexes together.

Bindings can be added or removed with no structural modification any of the participant Rexes.

Binding is a way of synchronizing the contents or values of two already existing objects, such as a list box control and a list of objects.

There are some pretty strict rules to Rex binding. Here are some of them.

1. Binding involves exactly 2 Rexes, not more.
2. Both Rexes must be of the same kind (e.g. both must be `RexValue`, `RexList`, or some other kind).
2. The Rexes must have exactly the same underlying value type.

Each Rex has a member called `binding` which acts as a single binding "slot". It is a mutable member.

A binding consumes the binding slot of one of the participants (that one is called the origin). The other is referenced by the `Binding` object itself and is called the target. This means that each Rex may be the origin of only one binding, but may be the target of any number of bindings.

The `Binding` object is immutable, so in order to change a binding you must create a new one.

Bindings have a number of configurable properties, including:

1. **Directionality**: The direction in which changes propagate (from the source, to the source, or two-way)
2. **Priority**: Only relevant in two-way bindings, determines whether the source or target value is preferred if a conflict occurs. The main source of a conflict is when the binding is set up and both Rexes must be synchronized. Priority determines which value trumps the other.

## Simplicity
Unlike in other implementations on data binding, the `Binding` object itself does very little. You cannot specify converters or validation in a binding object -- that kind of thing belongs in another Rex.  
	
# Deterministic Disposal
Rex objects are deterministic disposable or `closed`. They implement Java's `AutoCloseable`.

They are still subject to garbage collection, but because eventing is used to signal changes, a link is created between a child Rex and a parent Rex. For example, returning to one of the previous diagrams:

![](https://i.imgur.com/P8uIfxQ.png)

In this case, `toDouble`'s event handler must be registered with `number`, and so is reachable from `number`. This will stop it being garbage collected unless `number` is also garbage collected. When dealing with long chains of Rexes, a massive memory leak may result.

Deterministic disposal lets you disconnect `toDouble` from `number`, so it is no longer reachable from it, allowing it to be garbage collected (even when closed, `number` is still reachable from `toDouble`, but that is unimportant).

Thus, closing any link in a long chain of Rexes will allow the end of the chain to be garbage collected.

Deterministic disposal is also used to disconnect scalar Rexes inside `RexList` from event handlers attached while part of the list, since after a Rex is removed from the list it is effectively destroyed.
	
# Inspection
Inspection lets you inspect the structure of a Rex stream. It is a bit like reflection, in that it allows you to see information about program structure that would otherwise remain hidden.

Each Rex has an `info` member that reveals a `RexInfo` informational object containing, among other things:

1. The `rexType` of the Rex, which is a `RexType` object containing rex type or role information (not necessarily the same as runtime type information).
2. The `parents` of the Rex, which is a `NaryDependency` object that lets you inspect the dependencies, as well as the role of each.
3. If the rex `isLazy` or not.

The info object contains structural information, and as such is immutable.

Each Rex also has a `meta` member that reveals a `MetadataCollection` object. This object contains user-supplied metadata. The collection is mutable. Standard metadata names are:

1. `name` -- an informational string describing this instance. In the diagrams above, I rendered the name in italics.
2. `description` -- Description of the instance.

Inspection can be used for debugging and runtime visualization of Rex objects, including constructing flow diagrams. However, note that the actual structure you find may be somewhat different form what you think it might be. There is no reason that a transformation cannot chain two or more Rexes, instead of onlye one.

# Other Features
RexStream is also supposed to include some more features that I haven't gotten around to implementing or even deciding on yet:

1. Validation
2. Exception handling
3. Pull-based notification options
4. Built-in visualization
5. Advanced debugging (called "Rex trace", similar to a stack trace)