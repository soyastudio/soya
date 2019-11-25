# Dovetails Ant Component

## Ant Adapters:
FIXME:
- [ ] Apply
- [ ]

1. Project Task Adapters
    - [ ] Project
    - [ ] Property
    - [ ] TaskDef

1. Archive Task Adapters
   - [ ] GUnzip
   - [ ] BUnzip2 
   - [ ] UnXZ 
   - [ ] GZip
   - [ ] BZip2
   - [ ] XZ 
   - [ ] Cab
   - [ ] Ear
   - [ ] Jar
   - [ ] Manifest
   - [ ] Rpm
   - [ ] SignJar
   - [ ] Tar
   - [ ] Unjar
   - [ ] Untar
   - [ ] Unwar
   - [ ] Unzip
   - [ ] War
   - [ ] Zip
    
2. Execution Task Adapters
   - [x] Ant
   - [ ] Apply
   - [ ] Dependset
   - [ ] Exec
   - [ ] Java
   - [ ] Parallel
   - [ ] Sequential
   - [x] Sleep

3. File Task Adapters
   - [ ] Attrib
   - [x] _cd_
   - [ ] Checksum
   - [ ] Chgrop
   - [ ] Chmod
   - [ ] Chown
   - [ ] Concat
   - [x] Copy
   - [x] Delete
   - [ ] Filter
   - [ ] FixCRLF
   - [ ] Get
   - [x] Midir
   - [x] Move
   - [ ] Patch
   - [ ] Replace
   - [ ] ReplaceRegExp
   - [ ] SetPermissions
   - [ ] Sync
   - [ ] Tempfile
   - [ ] Touch

4. Remote Task Adapters:
   - [ ] FTP
   - [ ] Rexec
   - [ ] Scp
   - [ ] setproxy
   - [ ] Sshexec
   - [ ] Telnet

5. Build Task Adapters
   - [ ] Ivy
   - [ ] Javac
 
6. Parsing, Transformation and Code Generation Adapters:
   - [ ] Antlr
   - [ ] AJC
   - [ ] XJC

7. SCM Task Adapters
   - [ ] CVS
   - [ ] Git

8. Test Task Adapters
   - [ ] Junit
   - [ ] JMeter

9.  Misc
   - [x] Echo
   - [ ] 


## Archive Task Adapters

| DSL | Ant Task | Description|
| ------ | ------ | ------ |
| ant://gunzip | GUnzip | Expands a file packed using GZip.|
| ant://bunzip2 | BUnzip2 | Expands a file packed usingBZip2.|
| ant://unxz | UnXZ | Expands a file packed using XZ.|
| ant://gzip | GZip | Packs a file using the GZip algorithm. This task does not do any dependency checking; the output file is always generated|
| ant://bzip2 | BZip2 | Packs a file using the BZip2 algorithm. This task does not do any dependency checking; the output file is always generated|
| ant://xz | XZ | Packs a file using the XZ algorithm. This task does not do any dependency checking; the output file is always generated|
| ant://cab | Cab	| Creates Microsoft CAB archive files. It is invoked similar to the Jar or Zip tasks. This task will work on Windows using the external cabarc tool (provided by Microsoft), which must be located in your executable path.|
| ant://ear | Ear | An extension of the Jar task with special treatment for files that should end up in an Enterprise Application archive.|
| ant://jar | Jar | Jars a set of files.|
| ant://manifect | Manifest | Creates a manifest file.|
| ant://rpm | Rpm	| Invokes the rpm executable to build a Linux installation file. This task currently only works on Linux or other Unix platforms with RPM support.|
| ant://signJar | SignJar |	Signs a jar or zip file with the javasign command-line tool.|
| ant://tar | Tar | Creates a tar archive.|
| ant://unjar | Unjar | Unzips a jarfile.|
| ant://untar | Untar | Untars a tarfile.|
| ant://unwar | Unwar | Unzips a warfile.|
| ant://unzip | Unzip | Unzips a zipfile.|
| ant://unwar | War | An extension of the Jar task with special treatment for files that should end up in the WEB-INF/lib, WEB-INF/classes, or WEB-INF directories of the Web Application Archive.|
| ant://zip | Zip | Creates a zipfile.|

## Execution Task Adapters

| DSL | Ant Task | Description|
| ------ | ------ | ------ |
| ant://ant| Ant | Runs Ant on a supplied buildfile, optionally passing properties (with possibly new values). This task can be used to build sub-projects. |
| ant://apply | Apply | Executes a system command. When the os attribute is specified, the command is only executed when Ant is run on one of the specified operating systems.|
| ant://dependset | Dependset | Compares a set of source files with a set of target files. If any of the source files is newer than any of the target files, all the target files are removed. |
| ant://exec | Exec | Executes a system command. When the os attribute is specified, the command is only executed when Ant is run on one of the specified operating systems. |
| ant://java | Java | Executes a Java class within the running (Ant) JVM, or in another JVM if the fork attribute is specified. |
| ant://parallel | Parallel |  |
| ant://sequential | Sefquential | |
| ant://sleep | Sleep | |

## File Task Adapters

| DSL | Ant Task | Description|
| ------ | ------ | ------ |
| ant://attrib| Attrib | Changes the permissions and/or attributes of a file or all files inside the specified directories. Currently, it has effect only under Windows.|
| ant://cd | N/A | This is not an ant task. Set current directory in TaskSession. Same as the _cd_ command.|
| ant://checksum | Checksum | Generates a checksum for a file or set of files. This task can also be used to perform checksum verifications.|
| ant://chgrp | Chgrp | Changes the group ownership of a file or all files inside the specified directories. Currently, it has effect only under Unix.|
| ant://chmod | Chmod | Changes the permissions of a file or all files inside the specified directories. Currently, it has effect only under Unix. The permissions are also UNIX style, like the arguments for the chmod command.|
| ant://chown | Chown | Changes the owner of a file or all files inside the specified directories. Currently, it has effect only under Unix.|
| ant://concat | Concat | Concatenates multiple files into a single one or to Ant's logging system.|
| ant://copy| Copy | Copies a file or Fileset to a new file or directory.|
| ant://delete | Delete | |
| ant://filter | Filter| |
| ant://fixCRLF| FixCRLF | |
| ant://get | Get | |
| ant://mkdir | Mkdir | |
| ant://move |  Move| |
| ant://patch | Patch | |
| ant://replace | Replace | |
| ant://replaceRegExp | ReplaceRegExp | |
| ant://setPermissions | SetPermissions | |
| ant://sync | Sync | |
| ant://tempfile | Tempfile | |
| ant://touch | Touch | |




### Attrib
### Checksum
### Chgrop
### Chmod
### Chown
### Concat
### Copy
### Delete
### Filter
### FixCRLF
### Get
### Midir

#### Attributes
| Name | Required | Default Value | Description |
| ------ | ------ | ------ | ------ |
| dir | true | null | |
| base | false | "./" | |
| cd | false | false |

#### Examples
1. Basic Usage
2. Make a directory and set as current directory

### Move
### Patch
### Replace
### ReplaceRegExp
### SetPermissions
### Sync
### Tempfile
### Touch

## Remote Task Adapters

| DSL | Ant Task | Description|
| ------ | ------ | ------ |
| ant://ftp| FTP | Implements a basic FTP client that can send, receive, list, and delete files, and create directories.|
| ant://rexec | Rexec | Automates a rexec session.|
| ant://scp | Scp| Copies files to or from a remote server using SSH.|
| ant://setproxy | setproxy | Sets Java's HTTP proxy properties, so that tasks and code run in the same JVM can have access to remote web sites through a firewall.|
| ant://sshexec | Sshexec	| Executes a command on a remote server using SSH.|
| ant://telnet | Telnet |Automates a telnet session. This task uses nested <read> and <write> tags to indicate strings to wait for and specify text to send.|