import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.polinazherdeva.task1.MainActivity
import ru.polinazherdeva.task1.R

@RunWith(AndroidJUnit4::class)
class LevelsFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testLevelsListDisplayed() {
        // Кликаем по кнопке "Начать игру"
        onView(withId(R.id.startButton)).perform(click())

        // После открытия GameActivity проверяем RecyclerView
        onView(withId(R.id.rvLevels)).check(matches(isDisplayed()))
    }
}
