# Gitlet Outline
<!-- ABOUT THE PROJECT -->
 ## Classes and Data Structures
      Main:
        1.Stage _s
        2.CommitTree _commitTree;
        3.String CWD
      Blob:Containg a single file content.
        1.String _blobname:The hash value of the blob.
        2.String _content:given content.
        3.String _fileName:given file hashvalue/name.
      Commits:
        1. String _metadata:tracks the commit's time stamp and log message.
        2. String _parent:A hash reference to its parent.
        3. String _secondParent:A hash reference to its second parent 4 merge purposes.
        4 String _name:The hash value of this commit.
        5.HashMap<String, String> _fileToBlobs:Blob references of its files.
      Stage: The class that performs the staging process.
        1.HashMap<String, String> addToStage: files added to stage
        2.HashMap<String, String> removedStage:files removed from stage.
        3.HashMap<String, String> curCommit:files in the cur commit.
        4.HashMap<String, String> untracked:files being untracked
        5.HashMap<String, String> trackModified
        6.HashMap<String, String> trackDelete
      CommitTree:A class that holds branches of commits.
        1.HashMap<String, String> branches
        2.String _head:The file that the head pointer points to.
        3.String _currDirectory
   ## Algorithms
    Main:
        1.init:The method that initiates a .gitlet directory if not one already. It also starts with one commit as the root.
        2.add:This adds a copy of the file to the staging area in .gitlet.
        3.commit:This tracks the saved files.
        4.rm:This unstages the file if it is curly stages for add.
        5.log:This displays the log history from the most recent commit.
        6.global-log:The displays every commit in no order.
        7.find:This prints out the commit's is with the given commit message.
        8.status:this displays what branches curly exist and mark the cur branch with a *.
        9.checkout:This basically overwrites the cur file with the given.
        10.branch:This creates new branch with given node and point it at the given node.
        11.rm-branch:This deletes the given branch with the given name.
        12.reset:checks out all the files in the given commit. Remove the untracked files.
        13.merge:This combines two files that have been split.
   ## Commands
        1.java gitlet.Main init
        2.java gitlet.Main add [file name]
        3.java gitlet.Main commit [message]
        4.java gitlet.Main rm [file name]
        5.java gitlet.Main log
        6.java gitlet.Main global-log
        7.java gitlet.Main find [commit message]
        8.java gitlet.Main status
        9.java gitlet.Main checkout -- [file name]
        10.java gitlet.Main checkout [commit id] -- [file name]
        11.java gitlet.Main checkout [branch name]
        12.java gitlet.Main branch [branch name]
        13.java gitlet.Main rm-branch [branch name]
        14.java gitlet.Main reset [commit id]
        15.java gitlet.Main merge [branch name]
        16.java gitlet.Main rm-remote [remote name]
        17.java gitlet.Main push [remote name] [remote branch name]
        18.java gitlet.Main fetch [remote name] [remote branch name]
        19.java gitlet.Main pull [remote name] [remote branch name]
        
   Project Spec: https://inst.eecs.berkeley.edu/~cs61b/sp20/materials/proj/proj3/index.html
    
    

