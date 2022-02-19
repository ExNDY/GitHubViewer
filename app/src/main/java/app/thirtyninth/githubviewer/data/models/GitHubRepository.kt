package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepository(
    val id: Int? = 0,
    @SerialName("node_id")
    val nodeID: String? = "",
    val name: String? = "",
    @SerialName("full_name")
    val fullName: String? = "",
    val owner: Owner,
    val private: Boolean? = false,
    @SerialName("html_url")
    val htmlURL: String? = "",
    val description: String? = "",
    val fork: Boolean? = false,
    val url: String? = "",
    @SerialName("archive_url")
    val archiveURL: String? = "",
    @SerialName("assignees_url")
    val assigneesURL: String? = "",
    @SerialName("blobs_url")
    val blobsURL: String? = "",
    @SerialName("branches_url")
    val branchesURL: String? = "",
    @SerialName("collaborators_url")
    val collaboratorsURL: String? = "",
    @SerialName("comments_url")
    val commentsURL: String? = "",
    @SerialName("commits_url")
    val commitsURL: String? = "",
    @SerialName("compare_url")
    val compareURL: String? = "",
    @SerialName("contents_url")
    val contentsURL: String? = "",
    @SerialName("contributors_url")
    val contributorsURL: String? = "",
    @SerialName("deployments_url")
    val deploymentsURL: String? = "",
    @SerialName("downloads_url")
    val downloadsURL: String? = "",
    @SerialName("events_url")
    val eventsURL: String? = "",
    @SerialName("forks_url")
    val forksURL: String? = "",
    @SerialName("git_commits_url")
    val gitCommitsURL: String? = "",
    @SerialName("git_refs_url")
    val gitRefsURL: String? = "",
    @SerialName("git_tags_url")
    val gitTagsURL: String? = "",
    @SerialName("git_url")
    val gitURL: String? = "",
    @SerialName("issue_comment_url")
    val issueCommentURL: String? = "",
    @SerialName("issue_events_url")
    val issueEventsURL: String? = "",
    @SerialName("issues_url")
    val issuesURL: String? = "",
    @SerialName("keys_url")
    val keysURL: String? = "",
    @SerialName("labels_url")
    val labelsURL: String? = "",
    @SerialName("languages_url")
    val languagesURL: String? = "",
    @SerialName("merges_url")
    val mergesURL: String? = "",
    @SerialName("milestones_url")
    val milestonesURL: String? = "",
    @SerialName("notifications_url")
    val notificationsURL: String? = "",
    @SerialName("pulls_url")
    val pullsURL: String? = "",
    @SerialName("releases_url")
    val releasesURL: String? = "",
    @SerialName("ssh_url")
    val sshURL: String? = "",
    @SerialName("stargazers_url")
    val stargazersURL: String? = "",
    @SerialName("statuses_url")
    val statusesURL: String? = "",
    @SerialName("subscribers_url")
    val subscribersURL: String? = "",
    @SerialName("subscription_url")
    val subscriptionURL: String? = "",
    @SerialName("tags_url")
    val tagsURL: String? = "",
    @SerialName("teams_url")
    val teamsURL: String? = "",
    @SerialName("trees_url")
    val treesURL: String? = "",
    @SerialName("clone_url")
    val cloneURL: String? = "",
    @SerialName("mirror_url")
    val mirrorURL: String? = "",
    @SerialName("hooks_url")
    val hooksURL: String? = "",
    @SerialName("svn_url")
    val svnURL: String? = "",
    val homepage: String? = "",
    val organization: String? = "",
    val language: String? = "",
    val forks: Int? = 0,
    @SerialName("forks_count")
    val forksCount: Int? = 0,
    @SerialName("stargazers_count")
    val stargazersCount: Int? = 0,
    @SerialName("watchers_count")
    val watchersCount: Int? = 0,
    val watchers: Int? = 0,
    val size: Int? = 0,
    @SerialName("default_branch")
    val defaultBranch: String? = "",
    @SerialName("open_issues")
    val openIssues: Int? = 0,
    @SerialName("open_issues_count")
    val openIssuesCount: Int? = 0,
    @SerialName("is_template")
    val isTemplate: Boolean? = false,
    @SerialName("license")
    val license: License? = null,
    val topics: List<String?>,
    @SerialName("has_issues")
    val hasIssues: Boolean? = false,
    @SerialName("has_projects")
    val hasProjects: Boolean? = false,
    @SerialName("has_wiki")
    val hasWiki: Boolean? = false,
    @SerialName("has_pages")
    val hasPages: Boolean? = false,
    @SerialName("has_downloads")
    val hasDownloads: Boolean? = false,
    val archived: Boolean? = false,
    val disabled: Boolean? = false,
    val visibility: String,
    @SerialName("pushed_at")
    val pushedAt: String? = "",
    @SerialName("created_at")
    val createdAt: String? = "",
    @SerialName("updated_at")
    val updatedAt: String? = "",
    val permissions: Permissions,
    @SerialName("allow_rebase_merge")
    val allowRebaseMerge: Boolean? = false,
    @SerialName("template_repository")
    val templateRepository: Boolean? = false,
    @SerialName("temp_clone_token")
    val tempCloneToken: String? = "",
    @SerialName("allow_squash_merge")
    val allowSquashMerge: Boolean? = false,
    @SerialName("allow_auto_merge")
    val allowAutoMerge: Boolean? = false,
    @SerialName("delete_branch_on_merge")
    val deleteBranchOnMerge: Boolean? = false,
    @SerialName("allow_merge_commit")
    val allowMergeCommit: Boolean? = false,
    @SerialName("subscribers_count")
    val subscribersCount: Int? = 0,
    @SerialName("network_count")
    val networkCount: Int? = 0
)

@Serializable
data class License(
    val key: String? = "",
    val name: String? = "",
    val url: String? = "",
    @SerialName("spdx_id")
    val spdxID: String? = "",
    @SerialName("node_id")
    val nodeID: String? = "",
    @SerialName("html_url")
    val htmlURL: String? = ""
)

@Serializable
data class Owner(
    val login: String? = "",
    val id: Int? = 0,
    @SerialName("node_id")
    val nodeID: String? = "",
    @SerialName("avatar_url")
    val avatarURL: String? = "",
    @SerialName("gravatar_id")
    val gravatarID: String? = "",
    val url: String? = "",
    @SerialName("html_url")
    val htmlURL: String? = "",
    @SerialName("followers_url")
    val followersURL: String? = "",
    @SerialName("following_url")
    val followingURL: String? = "",
    @SerialName("gists_url")
    val gistsURL: String? = "",
    @SerialName("starred_url")
    val starredURL: String? = "",
    @SerialName("subscriptions_url")
    val subscriptionsURL: String? = "",
    @SerialName("organizations_url")
    val organizationsURL: String? = "",
    @SerialName("repos_url")
    val reposURL: String? = "",
    @SerialName("events_url")
    val eventsURL: String? = "",
    @SerialName("received_events_url")
    val receivedEventsURL: String? = "",
    val type: String? = "",
    @SerialName("site_admin")
    val siteAdmin: Boolean? = false
)

@Serializable
data class Permissions(
    val admin: Boolean? = false,
    val push: Boolean? = false,
    val pull: Boolean? = false
)
