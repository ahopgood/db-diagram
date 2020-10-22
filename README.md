# README

## Usage
The components of this system all have fluent builders, default values are available where mentioned below.

### Generator
The `Generator` is the main entrypoint and orchestrator.  
It is responsible for obtaining the database create table statements via a `Source` and parsing them into the various
database model objects via a `DatabaseSyntaxParser`.  
Once these model objects have been generated for each table the `DiagramProducer` is used to convert the model into a 
diagram.  
```
DatabaseEntityRelationshipGenerator generator = DatabaseEntityRelationshipGenerator.builder()
    .parser(parser)
    .producer(producer)
    .source(source)
    .build();

generator.generate();
```
### Sources
A source is the location from which you get the database's `SHOW CREATE TABLE` output from.  
They can take two forms:
* A DatabaseSource - sources directly via a database connection
* A FileSource - sources from a directory of `.sql` files

#### FileSource
The FileSource requires a directory location as input.  
```
Source source = FileSource.builder()
    .directoryPath(Path.of(testPath, packagePath, system).toString())
    .build();
```
The director must contain `.sql` files, **one per table**.  
The files must contain the output of a `SHOW CREATE TABLE <tablename>` command. 
```
CREATE TABLE `inventory` (
  `inventory_id` varchar(36) NOT NULL,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `inventory_id` (`inventory_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC
```
The Source is an Iterator that outputs a List<String> for each file, each `next()` call will produce a new List<String>
with the each String representing a line in the above `.sql` file.  
In this way it will iterate through the contents of the directory converting each file to a list of Strings.

#### DatabaseSource
A DatabaseSource will produce the database `SHOW CREATE TABLE <tableName>` statements directly from a database.  
In order to do this is will need:
* A username
* A password
* A database name
* A database url consisting of `ipaddress:port` or just `ipaddress` and the default port of `3306` will be used.
* (optional) a list of table names to query, if not present the DatabaseSource will query **all** tables in the database
```
Source source = DatabaseSource.builder()
            .username(sourceUsername)
            .password(sourcePassword)
            .databaseName(database)
            .databaseUrl(databaseUrl)
            .tableNames(List.of("attribute_types"))
            .build();
``` 

### DatabaseSyntaxParser
The DatabaseSyntaxParser takes a `Source` as input and converts the information into a series of model objects 
representing database tables, columns, constraints and keys.
Currently only `MySQL` parsing is supported via the `MySqlRegexParser`:
```
DatabaseSyntaxParser parser = new MySqlRegexParser();
```

### DiagramProducer
A diagram producer will take the model created by a DatabaseSyntaxParser and convert it into an image of the database. 
#### PlantUmlProducer
Produces images (png or svg) via plantuml or produces a plantuml (.puml) source file.  
* `filename` the filename to name the generated output file
* `title` the title to put at the top of the image 
* `showOrphanForeignKeys` - whether to show foreign keys that don't have a corresponding source table for their relationship, defaults to `false`.
* `generatePlantUmlFile` - whether to also generate the `filename.puml` source file, defaults to `false`.
* `outputFileFormat` - the output file format to use; default is `OutputFileFormat.PNG` or `OutputFileFormat.SVG`
* `plantumlLimitSize` - the upper resolution limit of the generated images, defaults to `4094` (4094x4094 pixels) 
```
DiagramProducer producer = PlantUmlProducer.builder()
    .filename(system)
    .title(system)
    .showOrphanForeignKeys(true)
    .generatePlantUmlFile(true)
    .outputFileFormat(OutputFileFormat.PNG) //default, can use SVG
    .plantumlLimitSize(4094) //default size
    .build();
```
## To do
* ~~Standardise input sources:~~
    * ~~FileSource~~ 
    * ~~DbSource~~
* Refactor to separate out concerns correctly:
    * ~~Runner class has functionality in it that should be encapsulated in a Source~~
    * ~~DatabaseEntityRelationshipGenerator should be responsible for using a source to obtain a List of Strings describing on a table by table basis.~~
    * DatabaseEntityRelationshipGenerator should use the list of Strings to call the parser to create a table
* Add graphviz check to PlantUmlProducer
* ~~Check tables exist in diagram *before* adding the foreign key dependency in order to reduce vestigial arrows to empty class objects~~ toggleable now
* ~~Add primary key to field declaration or in the field block of the class diagram~~ 
* ~~Add foreign key to field declaration or in the field block of the class diagram~~
* Add default value to field declaration
* Investigate if a two way relationship between Table and Keys makes sense to simplify logic and signatures vs trade off of bending Demeter's law
* Implement hexagonal architecture
    * ~~Use ArchUnit to enforce layering~~
    * Create Immutable models for business logic
    * Change execution order when building Table, Column, ForeignKey and PrimaryKey
* ~~Use `show tables from database` to list database tables~~
* ~~Use `show create table tablename` to create files for processing~~
* Update ERDProducer to operate on Table, Column and Foreign Key objects
    * ~~Specify an DiagramProducer interface~~
    * ~~Create a DotDiagramProducer implementation~~
    * ~~Output the `.dot` file for importing elsewhere~~
