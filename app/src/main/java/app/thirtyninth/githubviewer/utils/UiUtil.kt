package app.thirtyninth.githubviewer.utils

import android.app.AlertDialog
import android.content.Context
import app.thirtyninth.githubviewer.R

fun callLogoutDialog(
    context: Context,
    positiveResult: () -> Unit
): AlertDialog = AlertDialog.Builder(context, R.style.GitHubViewer_AlertDialog)
    .setTitle(context.getString(R.string.logout_dialog_title))
    .setMessage(context.getString(R.string.logout_dialog_message))
    .setPositiveButton(context.getString(R.string.logout_dialog_positive)) { _, _ ->
        positiveResult.invoke()
    }
    .setNegativeButton(context.getString(R.string.logout_dialog_negative), null)
    .show()

fun callExceptionDialog(
    title: String,
    message: String,
    context: Context,
    okResult: () -> Unit
): AlertDialog = AlertDialog.Builder(context, R.style.GitHubViewer_AlertDialog)
    .setTitle(title)
    .setMessage(message)
    .setPositiveButton(context.getString(R.string.dialog_ok)) { _, _ ->
        okResult.invoke()
    }
    .show()