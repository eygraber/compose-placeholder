import systems.danger.kotlin.*
import java.util.Locale

danger(args) {
  with(github) {
    val labelNames = issue.labels.map { it.name }.toSet()

    /*
    # --------------------------------------------------------------------------------------------------------------------
    # Check if labels were added to the pull request
    #--------------------------------------------------------------------------------------------------------------------
    */
    val labelsToFilter = setOf("hold", "skip release notes")
    val acceptableLabels = labelNames.filter { it !in labelsToFilter }

    if(acceptableLabels.isEmpty()) {
      fail("PR needs labels (hold and skip release notes don't count)")
    }

    /*
    # --------------------------------------------------------------------------------------------------------------------
    # Don't merge if there is a WIP or Hold label applied
    # --------------------------------------------------------------------------------------------------------------------
    */
    if("Hold" in labelNames) fail("This PR cannot be merged with a hold label applied")

    /*
    # --------------------------------------------------------------------------------------------------------------------
    # Check if merge commits were added to the pull request
    # --------------------------------------------------------------------------------------------------------------------
    */
    val mergeCommitRegex = Regex("^Merge branch '${pullRequest.base.ref}'.*")
    if(git.commits.any { it.message.matches(mergeCommitRegex) }) {
      fail("Please rebase to get rid of the merge commits in this PR")
    }
  }

  /*
  # --------------------------------------------------------------------------------------------------------------------
  # Make sure that no crash files or dumps are in the commit
  # --------------------------------------------------------------------------------------------------------------------
  */
  val touchedFiles = git.createdFiles + git.modifiedFiles
  if(touchedFiles.any { it.startsWith("hs_err_pid") || it.startsWith("java_pid") }) {
    fail("Please remove any error logs (hs_err_pid*.log) or heap dumps (java_pid*.hprof)")
  }
}
