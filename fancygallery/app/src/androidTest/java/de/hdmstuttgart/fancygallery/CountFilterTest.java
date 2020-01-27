package de.hdmstuttgart.fancygallery;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.hdmstuttgart.fancygallery.utils.Utils.childAtPosition;
import static de.hdmstuttgart.fancygallery.utils.Utils.getText;
import static de.hdmstuttgart.fancygallery.utils.Utils.withIndex;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CountFilterTest {

    @Rule
    public ActivityTestRule<AppActivity> mActivityTestRule = new ActivityTestRule<>(AppActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE");

    @Test
    public void countFilterTest() {
        ViewInteraction filterButton = onView(withId(R.id.filter_options));
        filterButton.perform(click());

        ViewInteraction materialTextView = onView(
                allOf(withId(android.R.id.title), withText(R.string.count_descending),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());

        int countStringF1 = Integer.valueOf(getText(withIndex(withId(R.id.count), 0)));
        int countStringF2 = Integer.valueOf(getText(withIndex(withId(R.id.count), 1)));

        assertThat("Folder counts", countStringF1, greaterThan(countStringF2));

    }


}
