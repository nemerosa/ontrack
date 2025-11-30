Yontrack documentation
======================

## Local development

```shell
python3 -m venv .venv
source .venv/bin/activate
pip install .
```

Start serving the documentation:

```shell
mkdocs serve --livereload
```

## Generating the website

Generate the descriptions from the code _and_ the website:

```shell
./gradlew build
```

To generate only the website:

```shell
./gradlew buildDocs
```
