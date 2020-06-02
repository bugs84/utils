import groovy.transform.Immutable

import java.util.concurrent.TimeUnit

//cool tricks
//https://railsware.com/blog/2014/08/11/git-housekeeping-tutorial-clean-up-outdated-branches-in-local-and-remote-repositories/

// vylistovat namergovane branche
// for branch in `git branch -r --merged | grep -v HEAD`; do echo -e `git show --format="%ci %cr %an" $branch | head -n 1` \\t$branch; done | sort -r
//csv format
//for branch in `git branch -r --merged | grep -v HEAD`; do echo -e `git show --format="%ci, %cr, %an", $branch | head -n 1` \\t$branch; done | sort -r

//git branch --merged master


// vylistovat nenamergovane branch
// for branch in `git branch -r --no-merged | grep -v HEAD`; do echo -e `git show --format="%ci %cr %an" $branch | head -n 1` \\t$branch; done | sort -r
//csv format
// for branch in `git branch -r --no-merged | grep -v HEAD`; do echo -e `git show --format="%ci, %cr, %an," $branch | head -n 1` \\t$branch; done | sort -r

//delete remote branch
//git push origin --delete branch-name


// MERGED Branches
//Write branches info to CSV
println "=============== MERGED BRANCHES"
println new BranchInfoFormatter(new GitOldBranchesDeleter().listMergedBranches()).toCsv("-XXX-")

// NOT MERGED Branches
//Write branches info to CSV
println "=============== NOT-MERGED BRANCHES"
println new BranchInfoFormatter(new GitOldBranchesDeleter().listNotMergedBranches()).toCsv("-XXX-")


//Construct delete command
println "=============== Command to delete specific branches"
new GitOldBranchesDeleter().writeDeleteRemoteBranchesGitCommand()


class GitOldBranchesDeleter {

    List<BranchInfo> listMergedBranches() {
        List<String> branches = listAllMergedBranches()
        return constructBranchInfo(branches)
    }

    List<BranchInfo> listNotMergedBranches() {
        List<String> branches = listAllNotMergedBranches()
        return constructBranchInfo(branches)
    }

    void writeDeleteRemoteBranchesGitCommand() {

        List<String> allBranches = """\
ISC-10685
SQA-615
C1001577-fix-error-message
ISC-6047-SAML
ISC-10765
ISC-9534
NU-6853-fix
geb-update-and-modules
""".readLines()

        List<String> protectedBranches = allBranches.findAll { isProtectedBranch(it) }
        if (!protectedBranches.isEmpty()) {
            throw new Exception("Error - noting will be delete contains protected release branches- $protectedBranches . " +
                    "If you really want to delete them. Do it manually")
        }
        allBranches.removeIf { it == null || it.isBlank() }

        allBranches.each {
            String command = "git push origin --delete \"$it\""
            println command

        }


    }

    private List<String> listAllNotMergedBranches() {
        executeAndReturnLines("git branch -r --no-merged").collect { it.strip() }
    }

    private List<String> listAllMergedBranches() {
        //TODO possible imporovement - list all merged into ReleaseBranches
        executeAndReturnLines("git branch -r --merged").collect { it.strip() }
    }

    private List<String> executeAndReturnLines(String command) {
        Process process = command.execute()
        def output = new ByteArrayOutputStream()
        process.consumeProcessOutputStream(output)
        process.consumeProcessErrorStream(System.err)
        assert process.waitFor(1, TimeUnit.MINUTES)
        output.toString().readLines()
    }

    private List<String> removeProtectedBranches(List<String> branches) {
        branches.removeIf { branchName ->
            isProtectedBranch(branchName)
        }
        branches
    }

    private boolean isProtectedBranch(String branchName) {
        return branchName ==~ /^.*origin\/[0-9]+\.[0-9]+.*$/
    }

    private void constructBranchInfo(List<String> branches) {
        removeProtectedBranches(branches)
        List<BranchInfo> output = getInfoAboutBranches(branches)
        output.sort { it.commiterDate }.reverse()
    }

    private List<BranchInfo> getInfoAboutBranches(List<String> branches) {
        branches.collect { String branchName ->
            String delimiter = " -XXX- "
            def info = executeAndReturnLines("git show --format=\"%ci$delimiter%cr$delimiter%an\" $branchName")[0]

            branchName
            def (commiterDate, commiterDateRelative, authorName) = info.split(delimiter)
            new BranchInfo(branchName, commiterDate, commiterDateRelative, authorName)
        }
    }

}

@Immutable
class BranchInfo {
    String name
    String commiterDate
    String commiterDateRelative
    String authorName
}


class BranchInfoFormatter {
    final List<BranchInfo> branches

    BranchInfoFormatter(List<BranchInfo> branches) {
        this.branches = branches
    }

    /** Do not solve correct escaping of csv */
    String toCsv(String separator) {
        branches.collect {
            "${it.commiterDate}$separator${it.commiterDateRelative}$separator${it.authorName}$separator${it.name}"
        }.join("\n")
    }
}

