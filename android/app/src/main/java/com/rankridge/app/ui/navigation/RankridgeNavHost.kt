package com.rankridge.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rankridge.app.ui.auth.UserRole
import com.rankridge.app.ui.auth.login.LoginScreen
import com.rankridge.app.ui.auth.otp.OtpScreen
import com.rankridge.app.ui.academichead.AcademicHeadDashboardScreen
import com.rankridge.app.ui.admin.AdminConsoleScreen
import com.rankridge.app.ui.auth.register.CreateAccountScreen
import com.rankridge.app.ui.common.rankridgeEnterTransition
import com.rankridge.app.ui.common.rankridgeExitTransition
import com.rankridge.app.ui.common.rankridgePopEnterTransition
import com.rankridge.app.ui.common.rankridgePopExitTransition
import com.rankridge.app.ui.counsellor.CounsellorDashboardScreen
import com.rankridge.app.ui.mentor.MentorDashboardScreen
import com.rankridge.app.ui.parent.ParentDashboardScreen
import com.rankridge.app.ui.student.StudentDashboardScreen
import com.rankridge.app.ui.teacher.TeacherDashboardScreen

object Routes {
    const val LOGIN = "login"
    const val CREATE_ACCOUNT = "create_account"
    const val OTP = "otp"
    const val STUDENT_HOME = "home"
    const val PARENT_HOME = "parent_home"
    const val MENTOR_HOME = "mentor_home"
    const val TEACHER_HOME = "teacher_home"
    const val COUNSELLOR_HOME = "counsellor_home"
    const val ACADEMIC_HEAD_HOME = "academic_head_home"
    const val ADMIN_HOME = "admin_home"

    const val ARG_PHONE = "phone"
    const val ARG_ROLE = "role"
    const val OTP_WITH_ARGS = "$OTP/{$ARG_PHONE}/{$ARG_ROLE}"
    fun otp(phone: String, role: UserRole) = "$OTP/$phone/${role.name}"

    /**
     * Every role has its own destination. Exhaustive on purpose: the previous `else ->
     * STUDENT_HOME` fallback meant an Admin sign-in silently opened the Student dashboard, and
     * any role added later would have inherited the same bug instead of failing to compile.
     */
    fun homeFor(role: UserRole) = when (role) {
        UserRole.STUDENT -> STUDENT_HOME
        UserRole.PARENT -> PARENT_HOME
        UserRole.MENTOR -> MENTOR_HOME
        UserRole.TEACHER -> TEACHER_HOME
        UserRole.COUNSELLOR -> COUNSELLOR_HOME
        UserRole.ACADEMIC_HEAD -> ACADEMIC_HEAD_HOME
        UserRole.ADMIN -> ADMIN_HOME
    }
}

@Composable
fun RankridgeNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        enterTransition = { rankridgeEnterTransition() },
        exitTransition = { rankridgeExitTransition() },
        popEnterTransition = { rankridgePopEnterTransition() },
        popExitTransition = { rankridgePopExitTransition() },
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onOtpRequested = { phone, role ->
                    navController.navigate(Routes.otp(phone, role)) { launchSingleTop = true }
                },
                onNavigateToCreateAccount = {
                    navController.navigate(Routes.CREATE_ACCOUNT) { launchSingleTop = true }
                },
            )
        }

        composable(Routes.CREATE_ACCOUNT) {
            CreateAccountScreen(
                onOtpRequested = { phone, role ->
                    navController.navigate(Routes.otp(phone, role)) { launchSingleTop = true }
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.OTP_WITH_ARGS,
            arguments = listOf(
                navArgument(Routes.ARG_PHONE) { type = NavType.StringType },
                navArgument(Routes.ARG_ROLE) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString(Routes.ARG_PHONE).orEmpty()
            val role = UserRole.entries.firstOrNull {
                it.name == backStackEntry.arguments?.getString(Routes.ARG_ROLE)
            } ?: UserRole.STUDENT
            OtpScreen(
                phone = phone,
                onVerified = {
                    val home = Routes.homeFor(role)
                    navController.navigate(home) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.STUDENT_HOME) {
            StudentDashboardScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.STUDENT_HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.PARENT_HOME) {
            ParentDashboardScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PARENT_HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.MENTOR_HOME) {
            MentorDashboardScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MENTOR_HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.TEACHER_HOME) {
            TeacherDashboardScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.TEACHER_HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.COUNSELLOR_HOME) {
            CounsellorDashboardScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.COUNSELLOR_HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.ACADEMIC_HEAD_HOME) {
            AcademicHeadDashboardScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ACADEMIC_HEAD_HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminConsoleScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ADMIN_HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
