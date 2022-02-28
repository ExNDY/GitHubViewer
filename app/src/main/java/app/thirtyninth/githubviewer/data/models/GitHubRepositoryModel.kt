package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// FIXME в нашей задаче не требуется настолько много данных, сколько описано тут.
//  Зачем описывать вообще все данные которые может выдать сервер?
@Serializable
data class GitHubRepositoryModel(
    val id: Int? = 0,

    @SerialName("node_id")
    val nodeID: String? = null,

    val name: String? = null,

    @SerialName("full_name")
    val fullName: String? = null,

    val private: Boolean? = false,
    @SerialName("owner")
    val owner: Organization? = null,

    @SerialName("html_url")
    val htmlURL: String? = null,

    val description: String?,
    val fork: Boolean? = false,
    val url: String? = null,

    @SerialName("forks_url")
    val forksURL: String? = null,

    @SerialName("keys_url")
    val keysURL: String? = null,

    @SerialName("collaborators_url")
    val collaboratorsURL: String? = null,

    @SerialName("teams_url")
    val teamsURL: String? = null,

    @SerialName("hooks_url")
    val hooksURL: String? = null,
    @SerialName("issue_events_url")
    val issueEventsURL: String? = null,

    @SerialName("events_url")
    val eventsURL: String? = null,

    @SerialName("assignees_url")
    val assigneesURL: String? = null,

    @SerialName("branches_url")
    val branchesURL: String? = null,

    @SerialName("tags_url")
    val tagsURL: String? = null,

    @SerialName("blobs_url")
    val blobsURL: String? = null,

    @SerialName("git_tags_url")
    val gitTagsURL: String? = null,

    @SerialName("git_refs_url")
    val gitRefsURL: String? = null,

    @SerialName("trees_url")
    val treesURL: String? = null,

    @SerialName("statuses_url")
    val statusesURL: String? = null,

    @SerialName("languages_url")
    val languagesURL: String? = null,

    @SerialName("stargazers_url")
    val stargazersURL: String? = null,

    @SerialName("contributors_url")
    val contributorsURL: String? = null,

    @SerialName("subscribers_url")
    val subscribersURL: String? = null,

    @SerialName("subscription_url")
    val subscriptionURL: String? = null,

    @SerialName("commits_url")
    val commitsURL: String? = null,

    @SerialName("git_commits_url")
    val gitCommitsURL: String? = null,

    @SerialName("comments_url")
    val commentsURL: String? = null,

    @SerialName("issue_comment_url")
    val issueCommentURL: String? = null,

    @SerialName("contents_url")
    val contentsURL: String? = null,
    @SerialName("compare_url")
    val compareURL: String? = null,
    @SerialName("merges_url")
    val mergesURL: String? = null,
    @SerialName("archive_url")
    val archiveURL: String? = null,
    @SerialName("downloads_url")
    val downloadsURL: String? = null,
    @SerialName("issues_url")
    val issuesURL: String? = null,
    @SerialName("pulls_url")
    val pullsURL: String? = null,
    @SerialName("milestones_url")
    val milestonesURL: String? = null,
    @SerialName("notifications_url")
    val notificationsURL: String? = null,
    @SerialName("labels_url")
    val labelsURL: String? = null,
    @SerialName("releases_url")
    val releasesURL: String? = null,
    @SerialName("deployments_url")
    val deploymentsURL: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("pushed_at")
    val pushedAt: String? = null,
    @SerialName("git_url")
    val gitURL: String? = null,
    @SerialName("ssh_url")
    val sshURL: String? = null,
    @SerialName("clone_url")
    val cloneURL: String? = null,
    @SerialName("svn_url")
    val svnURL: String? = null,
    val homepage: String? = null,
    val size: Int? = 0,
    @SerialName("stargazers_count")
    val stargazersCount: Int? = 0,
    @SerialName("watchers_count")
    val watchersCount: Int? = 0,
    val language: String? = null,
    @SerialName("has_issues")
    val hasIssues: Boolean? = false,
    @SerialName("has_projects")
    val hasProjects: Boolean? = false,
    @SerialName("has_downloads")
    val hasDownloads: Boolean? = false,
    @SerialName("has_wiki")
    val hasWiki: Boolean? = false,
    @SerialName("has_pages")
    val hasPages: Boolean? = false,
    @SerialName("forks_count")
    val forksCount: Int? = 0,
    @SerialName("mirror_url")
    val mirrorURL: String? = null,
    val archived: Boolean? = false,
    val disabled: Boolean? = false,
    @SerialName("open_issues_count")
    val openIssuesCount: Int? = 0,
    val license: License? = null,
    @SerialName("allow_forking")
    val allowForking: Boolean? = false,
    @SerialName("is_template")
    val isTemplate: Boolean? = false,
    val topics: List<String>? = null,
    val visibility: String? = null,
    val forks: Int? = 0,
    @SerialName("open_issues")
    val openIssues: Int? = 0,
    val watchers: Int? = 0,
    @SerialName("default_branch")
    val defaultBranch: String? = null,
    @SerialName("temp_clone_token")
    val tempCloneToken: String? = null,
    @SerialName("organization")
    val organization: Organization? = null,
    @SerialName("network_count")
    val networkCount: Int? = 0,
    @SerialName("subscribers_count")
    val subscribersCount: Int? = 0
)

@Serializable
data class License(
    val key: String? = null,
    val name: String? = null,
    @SerialName("spdx_id")
    val spdxID: String? = null,
    val url: String? = null,
    @SerialName("node_id")
    val nodeID: String? = null
)

@Serializable
data class Organization(
    val login: String? = null,
    val id: Int? = 0,
    @SerialName("node_id")
    val nodeID: String? = null,
    @SerialName("avatar_url")
    val avatarURL: String? = null,
    @SerialName("gravatar_id")
    val gravatarID: String? = null,
    val url: String? = null,
    @SerialName("html_url")
    val htmlURL: String? = null,
    @SerialName("followers_url")
    val followersURL: String? = null,
    @SerialName("following_url")
    val followingURL: String? = null,
    @SerialName("gists_url")
    val gistsURL: String? = null,
    @SerialName("starred_url")
    val starredURL: String? = null,
    @SerialName("subscriptions_url")
    val subscriptionsURL: String? = null,
    @SerialName("organizations_url")
    val organizationsURL: String? = null,
    @SerialName("repos_url")
    val reposURL: String? = null,
    @SerialName("events_url")
    val eventsURL: String? = null,
    @SerialName("received_events_url")
    val receivedEventsURL: String? = null,
    val type: String? = null,
    @SerialName("site_admin")
    val siteAdmin: Boolean? = false
)
