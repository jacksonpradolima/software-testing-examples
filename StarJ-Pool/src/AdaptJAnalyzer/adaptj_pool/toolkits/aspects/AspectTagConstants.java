package adaptj_pool.toolkits.aspects;

public final class AspectTagConstants {
    public static final int ASPECT_TAG_INVALID                       = -1;
    public static final int ASPECT_TAG_REGULAR                       =  0;
    public static final int ASPECT_TAG_ADV_EXECUTION                 =  1;
    public static final int ASPECT_TAG_ADV_ARG_SETUP                 =  2;
    public static final int ASPECT_TAG_ADV_TEST                      =  3;
    public static final int ASPECT_TAG_AFTER_THROWING_ADV_HANDLER    =  4;
    public static final int ASPECT_TAG_EXCEPTION_SOFTENING           =  5;
    public static final int ASPECT_TAG_AFTER_RETURNING_EXPOSURE      =  6;
    public static final int ASPECT_TAG_PER_OBJECT_ENTRY              =  7;
    public static final int ASPECT_TAG_CFLOW_EXIT                    =  8;
    public static final int ASPECT_TAG_CFLOW_ENTRY                   =  9;
    public static final int ASPECT_TAG_PRIVATE_METHOD_WRAPPER        = 10;
    public static final int ASPECT_TAG_PRIVATE_FIELD_GETTER          = 11;
    public static final int ASPECT_TAG_PRIVATE_FIELD_SETTER          = 12;
    public static final int ASPECT_TAG_ASPECT_CLASS_INIT             = 13;
    public static final int ASPECT_TAG_INTER_METHOD                  = 14;
    public static final int ASPECT_TAG_INTER_FIELD_GET               = 15;
    public static final int ASPECT_TAG_INTER_FIELD_SET               = 16;
    public static final int ASPECT_TAG_INTER_FIELD_INIT              = 17;
    public static final int ASPECT_TAG_INTER_CONSTRUCTOR_PRE         = 18;
    public static final int ASPECT_TAG_INTER_CONSTRUCTOR_POST        = 19;
    public static final int ASPECT_TAG_INTER_CONSTRUCTOR_CONVERSION  = 20;
    public static final int ASPECT_TAG_PER_OBJECT_SET                = 21;
    public static final int ASPECT_TAG_PER_OBJECT_GET                = 22;
    public static final int ASPECT_TAG_AROUND_CONVERSION             = 23;
    public static final int ASPECT_TAG_AROUND_CALLBACK               = 24;
    public static final int ASPECT_TAG_AROUND_PROCEED                = 25;
    public static final int ASPECT_TAG_CLOSURE_INIT                  = 26;
    public static final int ASPECT_TAG_INLINE_ACCESS_METHOD          = 27;
    public static final int ASPECT_TAG_ADVICE_BODY                   = 28;

    public static final int[] PROPAGATION_TABLE = {
        ASPECT_TAG_REGULAR,                      // REGULAR
        ASPECT_TAG_ADVICE_BODY,                  // ADV_EXECUTION
        ASPECT_TAG_ADV_ARG_SETUP,                // ADV_ARG_SETUP
        ASPECT_TAG_ADV_TEST,                     // ADV_TEST
        ASPECT_TAG_AFTER_THROWING_ADV_HANDLER,   // AFTER_THROWING_ADV_HANDLER
        ASPECT_TAG_EXCEPTION_SOFTENING,          // EXCEPTION_SOFTENING
        ASPECT_TAG_AFTER_RETURNING_EXPOSURE,     // AFTER_RETURNING_EXPOSURE
        ASPECT_TAG_PER_OBJECT_ENTRY,             // PER_OBJECT_ENTRY
        ASPECT_TAG_CFLOW_EXIT,                   // CFLOW_EXIT
        ASPECT_TAG_CFLOW_ENTRY,                  // CFLOW_ENTRY
        ASPECT_TAG_PRIVATE_METHOD_WRAPPER,       // PRIVATE_METHOD_WRAPPER
        ASPECT_TAG_PRIVATE_FIELD_GETTER,         // PRIVATE_FIELD_GETTER
        ASPECT_TAG_PRIVATE_FIELD_SETTER,         // PRIVATE_FIELD_SETTER
        ASPECT_TAG_ASPECT_CLASS_INIT,            // ASPECT_CLASS_INIT
        ASPECT_TAG_ADVICE_BODY,                  // INTER_METHOD
        ASPECT_TAG_INTER_FIELD_GET,              // INTER_FIELD_GET
        ASPECT_TAG_INTER_FIELD_SET,              // INTER_FIELD_SET
        ASPECT_TAG_INTER_FIELD_INIT,             // INTER_FIELD_INIT
        ASPECT_TAG_INTER_CONSTRUCTOR_PRE,        // INTER_CONSTRUCTOR_PRE
        ASPECT_TAG_INTER_CONSTRUCTOR_POST,       // INTER_CONSTRUCTOR_POST
        ASPECT_TAG_INTER_CONSTRUCTOR_CONVERSION, // INTER_CONSTRUCTOR_CONCERSION
        ASPECT_TAG_PER_OBJECT_SET,               // PER_OBJECT_SET
        ASPECT_TAG_PER_OBJECT_GET,               // PER_OBJECT_GET
        ASPECT_TAG_AROUND_CONVERSION,            // AROUND_CONVERSION
        ASPECT_TAG_REGULAR,                      // AROUND_CALLBACK
        ASPECT_TAG_REGULAR,                      // AROUND_PROCEED
        ASPECT_TAG_CLOSURE_INIT,                 // CLOSURE_INIT
        ASPECT_TAG_ADVICE_BODY,                  // INLINE_ACCESS_METHOD
        ASPECT_TAG_ADVICE_BODY                   // ADVICE_BODY
    };

    // REPLACEMENT_TABLE[current][propagated] = new
    public static final int[][] REPLACEMENT_TABLE;

    /* IMPORTANT NOTE: the constants above are *required* to
     * go from 0 to ASPECT_TAG_COUNT - 1 */
    public static final int ASPECT_TAG_COUNT = PROPAGATION_TABLE.length;

    static {
        int[][] repl_tbl = new int[ASPECT_TAG_COUNT][ASPECT_TAG_COUNT];

        for (int i = 0; i < ASPECT_TAG_COUNT; i++) {
            for (int j = 0; j < ASPECT_TAG_COUNT; j++) {
                repl_tbl[i][j] = i;
            }
        }

        // Customization of the replacement table
        repl_tbl[ASPECT_TAG_REGULAR][ASPECT_TAG_ADVICE_BODY] = ASPECT_TAG_ADVICE_BODY;
        for (int i = 0; i < ASPECT_TAG_COUNT; i++) {
            if (PROPAGATION_TABLE[i] == i) {
                // The tag is propagated. Allow it to overwrite 28
                repl_tbl[ASPECT_TAG_ADVICE_BODY][i] = i;
            }
        }

        REPLACEMENT_TABLE = repl_tbl;
    }

    private AspectTagConstants() {
        // no instances
    }

    
/* ========================================================================== *
NONE (-1)
--------

Instructions that do not have a tag will either inherit the tag on the invoke
instruction which called their containing method if that tag is one which
propagates, or will be tagged 0 if it is one that doesn't (ie. if these
instructions represent user-defined code, such as an advice body.)


DEFAULT (0)
-----------

This tag represents instructions that are not any of the following kinds of
overhead introduced by the aspect weaver. If the weaver touches a method, it
will tag all of the instructions that were in the original class file, and not
introduced by the weaver itself, as this kind. Additionally, when an advice
body is executed, the ADVICE_EXECUTE tag should not be propagated to untagged
instructions in the body; this DEFAULT tag should be.


ADVICE_EXECUTE (1)
------------------

This tag represents the overhead associated with executing the method
implementing a piece of advice. Advice bodies are compiled as methods in the
aspect class. When an aspect with advice is woven into the base code, an invoke
instruction for the advice method is added to the relevant joinpoint shadows.
This invoke instruction has this tag. This tag should not be propagated, since
it calls an advice body, which is not overhead. An instruction with this tag
will always be preceded by at least one instruction with the ADVICE_ARG_SETUP
tag, which acquires the aspect instance on which the advice body method call is
made.


ADVICE_ARG_SETUP (2)
--------------------

This tag represents the overhead associated with acquiring an aspect instance
at a joinpoint at which advice is to be executed, and exposing arguments to the
advice body. At least one instruction of this kind will precede an advice
execution instruction. This tag should be propagated.


ADVICE_TEST (3)
---------------

When it cannot be statically determined that a given piece of advice should be
executed at all joinpoints corresponding to the joinpoint shadow at which the
advice invocation instructions have been added, then those invocation
instructions must be wrapped in a test. The dynamic guard to see if a piece of
advice applies at a particular join point.

For example, consider this advice declaration:

    before(): call(void Main.foo()) && if(Main.x > 0) {
        ...
    }

The conditional expression must be evaluated at each joinpoint selected by the
call pointcut. The instructions corresponding to this test are tagged as type
3. The instructions responsible for examining the cflowstack to determine if a
joinpoint is selected by a cflow or cflowbelow pointcut are also tagged as this
kind. This tag should be propagated.


AFTER_THROWING_HANDLER (4)
--------------------------

After advice is that which is executed after the execution of the joinpoints
with which it is associated. This can be after a normal return, or after an
exception is thrown. To accommodate the later, exception handling code is
inserted which catches any exception, executes any pertinent after advice, and
then rethrows the original exception. The instructions responsible for this
have this tag.


EXCEPTION_SOFTENER (5)
----------------------

This tag represents the overhead involved in softening exceptions. The 'declare
soft' declaration in an aspect wraps exceptions of a given type, thrown from
within joinpoints selected by a given pointcut, in the unchecked
org.aspectj.SoftException. The instructions inserted at the joinpoint shadow
that catch and wrap the original exception, then throw the new SoftException,
have this tag.


AFTER_RETURNING_EXPOSURE (6)
----------------------------

It is possible to expose the value returned at a joinpoint to the body of a
piece of after advice. The following advice declaration, for example, exposes
the return value of the method foo() to the advice body:

    after() returning(int i): call(int Main.foo()) {
        do_something_with(i);
    }

The instructions inserted after the call to foo() and before the invocation of
this piece of advice, which expose the return value of foo() to the advice have
this tag.


PEROBJECT_ENTRY (7)
-------------------

By default, aspect instances are singletons. They can, however, be associated
on a per-object basis, either with the execution or target objects at
joinpoints selected by a given pointcut. The instructions inserted at joinpoint
shadows matched by the pointcut to manage these instances have this tag. This
tag should be propagated.


CFLOW_EXIT (8), CFLOW_ENTRY (9)
-------------------------------

The cflow and cflowbelow pointcuts require that a representation of the call
stack be managed during the execution of the program. At every relevant
joinpoint shadow, this representation must be updated. When the joinpoint is
entered, the relevant cflowstack is obtained, and a value is pushed on it; on
exiting the joinpoint, the stack is popped. Consider the following pointcut:

    cflowbelow(call(void Main.foo()))

Before every call to Main.foo(), instructions of type CFLOW_ENTRY are executed,
and after the call, instructions of type CFLOW_EXIT are called. This tag should
be propagated.


PRIV_METHOD (10), PRIV_FIELD_GET (11), PRIV_FIELD_SET (12)
----------------------------------------------------------

An aspect can be declared privileged, which means it has access to the private
members of the classes it crosscuts. In order to support this, public wrapper
methods for the class's private methods, and public accessor methods for the
class's private fields, are inserted during weaving. The instructions in these
new methods have this tag.


CLINIT (13)
-----------

The static initializer of the aspect class, and any static initializer added by
the weaver to another class, receive this tag. In the aspect class, the static
initializer will setup the cflowstack, if necessary, and create the default
singleton instance of the aspect. This tag should be propagated.


INTERMETHOD (14)
----------------

This tag represents the overhead in an inter-type method declaration. When you  
define a new method on a type, the method body is compiled into the aspect
class, and dispatch code is woven into the target type. That dispatch code is   
tagged as overhead of this kind.

For example, consider this aspect:

    public aspect Aspect {
        public void Main.foo() {
        }                       
    }

This results in a method in Aspect which contains the body of foo:

    public static void ajc$interMethod$Aspect$Main$foo(Main ajc$this_)

and a method foo() in Main, which calls the above method. The instructions in   
the body of this foo() method are all tagged as overhead type 14.

This tag should not be propagated, and should be treated as advice invocation
is.


INTERFIELDGET (15), INTERFIELDSET (16)
--------------------------------------

Some inter-type field declarations result in accessor methods being woven into
the target class.

For example, if class M implements interface I, and the following aspect
declares new fields on I:

    public aspect Aspect {
        public int I.i;
        private int I.j;
    }

Then the following fields are woven into M

    namedajc$interField$Aspect$I$i
    ajc$interField$Aspect$I$j

As well as the following accessor methods:

    public int ajc$interFieldGet$Aspect$I$i()
    public void ajc$interFieldSet$Aspect$I$i(int arg0)
    public int ajc$interFieldGet$Aspect$I$j()
    public void ajc$interFieldSet$Aspect$I$j(int arg0)

The instructions in the bodies of the accessors are tagged 15 or 16.


INTERFIELD_INIT (17)
--------------------

Inter-type field declarations result in intialization code being woven
into a class's constructor, or its static initializer. These instructions
invoke initialization methods on the aspect to handle variable initialization.

For example, the following aspect code introduces a variable on the class Main:

    public int Main.x = 1;

The aspect in which this is declared will have an initialization method named

    ajc$interFieldInit$newfield_Aspect$newfield_Main$x

Which sets the initial value of the variable to 1. A call to this method is
added to Main's constructor; this call is tagged 17. This tag should be
propagated.

Interestingly, if a variable is declared without an intial value, an empty
initialization method is still created in the aspect, containing only a return
instruction, and instructions to invoke this method are still added to the
class's constructor on static initializer.


INTERCONSTRUCTOR_PRE (18), INTERCONSTRUCTOR_POST (19)
-----------------------------------------------------

When an aspect has an inter-type constructor declaration, two methods are
created on the aspect: a preInterConstructor method and a postInterConstructor
method. A new constructor method is added to the class, and it invokes both of
these methods. The instructions that load these methods' arguments and invoke
these methods are tagged 18 and 19.

These methods can be somewhat peculiar. Consider the example in the
newconstructor/ directory. The preInterConstructor converts the constructor
arguments (an integer, in this case) into an array of objects. After it
returns, the constructor loads the single element of the array, converts it
back into an integer, and calls the postInterConstructor method with it as an
argument, wherein is the actual body of the constructor.

18 should be propagated, and since postInterConstructor contains the body of
the new constructor, 19 should not be propagated.


INTERCONSTRUCTOR_CONVERSION (20)
--------------------------------

This represents the overhead involved in calling methods on
org.aspectj.runtime.internal.Conversions from within the added constructors, as
described above. It should be propagated.


PEROBJECT_GET (21), PEROBJECT_SET (22)
--------------------------------------

These tags are related to tag 7. When an aspect is declared as being a
per-object aspect, as opposed to a singleton, the aspect instances are
associated with particular objects. Accessor methods are added to these objects
to acquire these aspect instances. The isntructions in these accessors receive
tags 21 and 22. These tags should be propagated.


AROUND_CONVERSION (23)
----------------------

This represents the conversion of arguments to and return values from a
proceed() call within around advice. This conversion is done by making calls to
methods on org.aspectj.runtime.internal.Conversions, which convert between
primitive types and objects. It should be propagated.


AROUND_CALLBACK (24), AROUND_PROCEED (25)
-----------------------------------------

Both of these tags represent overhead involved in making a proceed() call from
within around advice. 24 is specific to the run method on closure classes. They
should not be propagated, since proceed() calls the original user-defined
method body.


CLOSURE_INIT (26)
-----------------

Weaving an aspect with around advice into a class may result in the creation
of a closure class for the method to which the around advice applies. For
example, if an aspect weaves around a method in a class named Main, the closure
class may be named Main$AjcClosure1. This closure class has a constructor which
takes in an array of objects, representing the state of the closure, and calls
the superconstructor in org.aspectj.runtime.internal.AroundClosure with this
array as argument. The instructions in this constructor are all tagged 26. This
tag should be propagated.


INLINE_ACCESS_METHOD (27)
-------------------------

This tag represents the overhead involved in calling a method defined on an
aspect when there is a static dispatch method. The instructions of the static
dispatch method are tagged 27. Since the method being called is user-defined,
this tag should not be propagated. And example of instructions of this kind can
be found in the bean benchmark.

ADVICE_BODY (28)
----------------

This tag is purely dynamic and is propagated instead of the ADVICE_EXECUTION (1)
tag. It corresponds to the execution of pieces of advice. This tag should be
propagated.

 * =========================================================================== */
}
