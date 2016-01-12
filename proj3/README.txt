cs61b Project3 - Gitlet version-control system

Author: Chen Meng & Jim Bai

Description: implement a version-control system that mimics some of the basic features of the popular version-control system git. Since it is smaller and simpler, we have named it gitlet.

The main functionality that gitlet supports is:

- Saving backups of directories of files. In gitlet, this is called committing, and the backups themselves are called commits.
- Restoring a backup version of one or more files or entire commits. In gitlet, this is called checking out those files or that commit.
- Viewing the history of your backups. In gitlet, you view this history in something called the log.
- Maintaining related sequences of commits, called branches.
- Merging changes made in one branch into another.