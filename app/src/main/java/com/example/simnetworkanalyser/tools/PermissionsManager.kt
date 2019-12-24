package com.example.simnetworkanalyser.tools

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import timber.log.Timber
import java.util.*


class PermissionsManager(private val mActivity: Activity) {

    companion object {
        private const val ASK_PERMISSIONS_REQUEST_CODE = 124
    }


    fun checkPermissions() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            onPermissionsAllowed()
            return
        }

        //  Get requested permissions
        val packageInfo = mActivity.packageManager.getPackageInfo(
            mActivity.packageName,
            PackageManager.GET_PERMISSIONS
        )

        val requestedPermissions = packageInfo.requestedPermissions

        if (requestedPermissions.isNotEmpty()) {

            val neededPermissions = getNeededPermissions(requestedPermissions)

            if (neededPermissions.isNotEmpty()) {

                showPermissionsDialog(
                    neededPermissions,
                    getNeededPermissionsGroups(neededPermissions)
                )

            } else {
                onPermissionsAllowed()
            }

        } else {
            onPermissionsAllowed()
        }
    }


    // ** ------------------------------------------------ ** //
    // ** ------------------------------------------------ ** //


    private fun getNeededPermissions(requestedPermissions: Array<String>): List<String> {

        val permissionsNeeded = ArrayList<String>()

        for (i in requestedPermissions.indices) {

            val requestedPermission = requestedPermissions[i]

            if ((!addPermission(permissionsNeeded, requestedPermission))
                && (!permissionsNeeded.contains(requestedPermission))
            ) {

                permissionsNeeded.add(requestedPermission)
            }
        }

        return permissionsNeeded
    }

    private fun getNeededPermissionsGroups(neededPermissions: List<String>): List<String> {

        // Need Rationale
        val permissionsGroup = ArrayList<String>()

        for (i in neededPermissions.indices) {

            val permission = neededPermissions[i]
            var group = permission

            try {
                if (permission.contains("android.permission.")) {

                    val permissionInfo =
                        mActivity.packageManager.getPermissionInfo(
                            permission,
                            PackageManager.GET_META_DATA
                        )

                    val split =
                        permissionInfo.group.split("android.permission-group.".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()

                    group = if (split.isNotEmpty()) {
                        split[split.size - 1]
                    } else {
                        permissionInfo.group
                    }
                }
            } catch (e: Exception) {
                Timber.e("Exception: $e")
            }

            if (!permissionsGroup.contains(group)) {
                permissionsGroup.add(group)
            }
        }

        return permissionsGroup
    }

    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {

        if (ActivityCompat.checkSelfPermission(
                mActivity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            permissionsList.add(permission)

            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission))
                return false
        }

        return true
    }

    private fun onPermissionsAllowed() {
        // This method should be overridden if there is something to do when permissions are allowed
    }

    private fun onPermissionsDenied() {

        Toast.makeText(
            mActivity,
            "Permissions denied: the demonstration is impossible.",
            Toast.LENGTH_SHORT
        )

        mActivity.setResult(AccountManager.ERROR_CODE_UNSUPPORTED_OPERATION, Intent())
        mActivity.finish()
    }


    // ** ------------------------------------------------ ** //
    // ** ------------------------------------------------ ** //


    /**
     * Builds and displays the permissions information dialog
     *
     * @param neededPermissions The list of the needed permissions, asked by the system
     * @param permissionsGroups The list of permissions groups displayed in the dialog
     */
    private fun showPermissionsDialog(
        neededPermissions: List<String>,
        permissionsGroups: List<String>
    ) {

        MaterialDialog.Builder(mActivity).autoDismiss(false)
            .title("Permissions!")
            .content(
                String.format(
                    "The following permissions is needed: %s",
                    permissionsGroups.joinToString { it }
                )
            )
            .canceledOnTouchOutside(false)
            .negativeText("Cancel")
            .positiveText("OK")
            .onPositive { dialog, _ ->

                dialog.cancel()

                ActivityCompat.requestPermissions(
                    mActivity,
                    neededPermissions.toTypedArray(),
                    ASK_PERMISSIONS_REQUEST_CODE
                )
            }
            .onNegative { dialog, _ ->
                dialog.cancel()
                onPermissionsDenied()
            }
            .build()
            .show()
    }
}