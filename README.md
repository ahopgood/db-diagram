# README

To do:

* Check tables exist in diagram *before* adding the foreign key dependency in order to reduce vestigial arrows to empty class objects
* Add primary key to field declaration or in the field block of the class diagram 
* Add foreign key to field declaration or in the field block of the class diagram
* Add default value to field declaration
* Investigate if a two way relationship between Table and Keys makes sense to simplify logic and signatures vs trade off of bending Demeter's law
* Implement hexagonal architecture
    * Use ArchUnit to enforce layering
    * Create Immutable models for business logic
    * Change execution order when building Table, Column, ForeignKey and PrimaryKey
* Use `show tables from database` to list database tables
* Use `show create table tablename` to create files for processing
* Update ERDProducer to operate on Table, Column and Foreign Key objects
    * Specify an DiagramProducer interface
    * Create a DotDiagramProducer implementation
    * Output the `.dot` file for importing elsewhere