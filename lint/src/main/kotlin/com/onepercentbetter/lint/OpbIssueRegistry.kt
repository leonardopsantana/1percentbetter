

package com.onepercentbetter.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.onepercentbetter.lint.designsystem.DesignSystemDetector

class OpbIssueRegistry : IssueRegistry() {

    override val issues = listOf(
        DesignSystemDetector.ISSUE,
        TestMethodNameDetector.FORMAT,
        TestMethodNameDetector.PREFIX,
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "One percent better ",
        feedbackUrl = "https://github.com/leonardopsantana/1percentbetter/issues",
        contact = "https://github.com/leonardopsantana/1percentbetter",
    )
}
