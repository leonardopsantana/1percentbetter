

package com.onepercentbetter.lint.designsystem

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression

/**
 * A detector that checks for incorrect usages of Compose Material APIs over equivalents in
 * the One percent better design system module.
 */
class DesignSystemDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UCallExpression::class.java,
        UQualifiedReferenceExpression::class.java,
    )

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val name = node.methodName ?: return
                val preferredName = METHOD_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                val name = node.receiver.asRenderString()
                val preferredName = RECEIVER_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }
        }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "DesignSystem",
            briefDescription = "Design system",
            explanation = "This check highlights calls in code that use Compose Material " +
                "composables instead of equivalents from the One percent better design system " +
                "module.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                DesignSystemDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )

        // Unfortunately :lint is a Java module and thus can't depend on the :core-designsystem
        // Android module, so we can't use composable function references (eg. ::Button.name)
        // instead of hardcoded names.
        val METHOD_NAMES = mapOf(
            "MaterialTheme" to "OPBTheme",
            "Button" to "OPBButton",
            "OutlinedButton" to "OPBOutlinedButton",
            "TextButton" to "OPBTextButton",
            "FilterChip" to "OPBFilterChip",
            "ElevatedFilterChip" to "OPBFilterChip",
            "NavigationBar" to "OPBNavigationBar",
            "NavigationBarItem" to "OPBNavigationBarItem",
            "NavigationRail" to "OPBNavigationRail",
            "NavigationRailItem" to "OPBNavigationRailItem",
            "TabRow" to "OPBTabRow",
            "Tab" to "OPBTab",
            "IconToggleButton" to "OPBIconToggleButton",
            "FilledIconToggleButton" to "OPBIconToggleButton",
            "FilledTonalIconToggleButton" to "OPBIconToggleButton",
            "OutlinedIconToggleButton" to "OPBIconToggleButton",
            "CenterAlignedTopAppBar" to "OPBTopAppBar",
            "SmallTopAppBar" to "OPBTopAppBar",
            "MediumTopAppBar" to "OPBTopAppBar",
            "LargeTopAppBar" to "OPBTopAppBar",
        )
        val RECEIVER_NAMES = mapOf(
            "Icons" to "OPBIcons",
        )

        fun reportIssue(
            context: JavaContext,
            node: UElement,
            name: String,
            preferredName: String,
        ) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Using $name instead of $preferredName",
            )
        }
    }
}
