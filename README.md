# clj-lonely

Find unused namespaces

## Usage

Add the project to your lein profile:

```clj
[lein-lonely "0.1.0"]
```

Run `lein lonely <namespace> <namespace>` with the main/entrypoint namespaces of your project. Any namespace in `src` that is not referenced recursively by one of those will be printed.
