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
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.hdmstuttgart.fancygallery.utils.Utils.childAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HideFoldersTest {

    @Rule
    public ActivityTestRule<AppActivity> mActivityTestRule = new ActivityTestRule<>(AppActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE");

    @Test
    public void hideFoldersTest() {
        //RecyclerView recyclerView = mActivityTestRule.getActivity().findViewById(R.id.recyclerView);
        ViewInteraction frameLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.recyclerView),
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3)),
                        0),
                        isDisplayed()));
        frameLayout.perform(longClick());

        //int count = Integer.parseInt(getText(withId(R.id.text_selected_count)).split("/ ")[1]);
        for(int i = 1; i < 6; i++) {
            //recyclerView.scrollToPosition(i);
            ViewInteraction frameLayout2 = onView(
                    allOf(childAtPosition(
                            allOf(withId(R.id.recyclerView),
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            3)),
                            i),
                            isDisplayed()));
            frameLayout2.perform(click());
        }


        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.hide), withText("Hide"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar_selection),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.image_folder_list_toolbar),
                                        3),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Manage hidden folders"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        for(int i = 0; i < 6; i++) {
            ViewInteraction appCompatImageButton = onView(
                    allOf(withId(R.id.button_reveal),
                            childAtPosition(
                                    childAtPosition(
                                            withId(R.id.recyclerView),
                                            5-i),
                                    1),
                            isDisplayed()));
            appCompatImageButton.perform(click());
        }
    }


}
