# PurgeDrive

A java based program to find 'text' with regex, iterate over all files inside a drive/folder.
Then quarantine those files into separate folder.
Developed by using IntelliJ IDEA.

Currently is finding
1. IP Address (with a minor tweak)
2. Domain (only using a very simple regex to achieve this)
3. Literal text of - password: / password= / password = / password :

Run Example:
java Main -s /SuperDrive

Scenario:
File '/SuperDrive/Folder/BadFile.txt' contains IP Address.

Action:
Moving the match files to /SuperDrive_Purge/Quarantine/Folder/BadFile.txt
Creating /SuperDrive_Purge/Purge-Changelog.txt

*Note*
Quarantine <=> Root Folder (/SuperDrive)
Changelog records all the changes, and quarantine action.

