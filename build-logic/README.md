# Convention Plugins

The `build-logic` folder defines project-specific convention plugins, used to keep a single
source of truth for common module configurations.

This approach is heavily based on
[https://developer.squareup.com/blog/herding-elephants/](https://developer.squareup.com/blog/herding-elephants/)
and
[https://github.com/jjohannes/idiomatic-gradle](https://github.com/jjohannes/idiomatic-gradle).

By setting up convention plugins in `build-logic`, we can avoid duplicated build script setup,
messy `subproject` configurations, without the pitfalls of the `buildSrc` directory.

`build-logic` is an included build, as configured in the root
[`settings.gradle.kts`](../settings.gradle.kts).

Inside `build-logic` is a `convention` module, which defines a set of plugins that all normal
modules can use to configure themselves.

`build-logic` also includes a set of `Kotlin` files used to share logic between plugins themselves,
which is most useful for configuring Android components (libraries vs applications) with shared
code.

These plugins are *additive* and *composable*, and try to only accomplish a single responsibility.
Modules can then pick and choose the configurations they need.
If there is one-off logic for a module without shared code, it's preferable to define that directly
in the module's `build.gradle`, as opposed to creating a convention plugin with module-specific
setup.

Other benefits of convention plugins:

Using convention plugins in an Android project speeds up your build time primarily due to better configuration caching, reduced build script complexity, and improved reusability. Here’s why:

1. Configuration Caching & Avoiding Script Execution Overhead
   When you use convention plugins, Gradle doesn’t need to repeatedly evaluate multiple build.gradle(.kts) files across modules.
   The configuration phase becomes faster because all common settings are preconfigured in a single plugin instead of being repeated in each module.
   Gradle can cache the plugin’s applied settings, reducing redundant work.
2. Reduced Duplication & Simplified Gradle Files
   Without convention plugins, each module might have its own redundant configurations, leading to longer build script processing.
   By centralizing these configurations in a convention plugin, each module's build.gradle(.kts) stays lightweight, reducing the time required to parse and apply settings.
3. Parallel Execution & Incremental Build Benefits
   Convention plugins modularize the build logic, allowing Gradle to parallelize execution better.
   Since the convention plugin is compiled ahead of time, its code executes faster than a raw build.gradle script (which is interpreted at runtime).
4. Better Dependency & Task Caching
   Convention plugins allow more consistent dependency versions across modules, reducing unnecessary dependency resolution steps.
   Tasks using convention-based configurations can better leverage Gradle’s incremental build system because Gradle understands them as predefined, structured rules.
5. Reduced Kotlin DSL Overhead
   Using Kotlin DSL (.kts), convention plugins minimize script compilation overhead by moving logic into precompiled plugins, which execute faster than dynamically interpreted .kts scripts.
