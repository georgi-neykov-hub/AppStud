package appstud.neykov.com.appstudassigment.base.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * FragmentManager does not supply a developer with a fragment stack.
 * It gives us a fragment *transaction* stack.
 *
 * To be sane, we need *fragment* stack.
 */
public class FragmentStackController {

    public interface OnFragmentRemovedListener {
        void onFragmentRemoved(Fragment fragment);
    }

    private Activity activity;
    private FragmentManager manager;
    @IdRes
    private int containerId;

    private
    @AnimRes
    int enterAnimationResId;
    private
    @AnimRes
    int exitAnimationResId;
    private
    @AnimRes
    int popEnterAnimationResId;
    private
    @AnimRes
    int popExitAnimationResId;

    @Nullable
    private OnFragmentRemovedListener onFragmentRemovedListener;

    public FragmentStackController(Activity activity, FragmentManager manager, @IdRes int containerId, @Nullable OnFragmentRemovedListener onFragmentRemovedListener) {
        this.activity = activity;
        this.manager = manager;
        this.containerId = containerId;
        this.onFragmentRemovedListener = onFragmentRemovedListener;
    }

    public void setCustomAnimations(@AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit) {
        this.enterAnimationResId = enter;
        this.exitAnimationResId = exit;
        this.popEnterAnimationResId = popEnter;
        this.popExitAnimationResId = popExit;
    }

    /**
     * Returns the number of fragments in the stack.
     *
     * @return the number of fragments in the stack.
     */
    public int size() {
        return getFragments().size();
    }

    /**
     * Pushes a fragment to the top of the stack.
     */
    public void push(Fragment fragment) {
        this.push(fragment, null, null);
    }

    public void pushDialog(DialogFragment fragment) {
        Fragment top = peek();
        int index = top != null ? manager.getBackStackEntryCount() + 1 : 0;
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        fragment.show(transaction, indexToTag(index));
        manager.executePendingTransactions();
    }

    /**
     * Pushes a fragment to the top of the stack.
     */
    @SuppressLint("CommitTransaction")
    public void push(Fragment fragment, View sharedElement, String name) {
        Fragment top = peek();
        android.support.v4.app.FragmentTransaction transaction;
        if (top != null) {
            transaction = manager.beginTransaction()
                    .remove(top)
                    .add(containerId, fragment, indexToTag(manager.getBackStackEntryCount() + 1))
                    .addToBackStack(null);
        } else {
            transaction = manager.beginTransaction()
                    .add(containerId, fragment, indexToTag(0));
        }

        if (sharedElement != null && name != null) {
            transaction.addSharedElement(sharedElement, name);
        }
        transaction.commit();

        manager.executePendingTransactions();
    }

    /**
     * Pops the top item if the stack.
     * If the fragment implements {@link OnBackPressedListener}, calls {@link OnBackPressedListener#onBackPressed()} instead.
     * If {@link OnBackPressedListener#onBackPressed()} returns false the fragment gets popped.
     *
     * @return true if a fragment has been popped or if {@link OnBackPressedListener#onBackPressed()} returned true;
     */
    public boolean back() {
        Fragment top = peek();
        if (top instanceof OnBackPressedListener) {
            if (((OnBackPressedListener)top).onBackPressed())
                return true;
        }
        return pop();
    }

    /**
     * Pops the topmost fragment from the stack.
     * The lowest fragment can't be popped, it can only be replaced.
     *
     * @return false if the stack can't pop or true if a top fragment has been popped.
     */
    public boolean pop() {
        if (manager.getBackStackEntryCount() == 0)
            return false;
        Fragment top = peek();
        manager.popBackStackImmediate();
        if (onFragmentRemovedListener != null)
            onFragmentRemovedListener.onFragmentRemoved(top);
        return true;
    }

    public boolean popWithStateLoss() {
        if (manager.getBackStackEntryCount() > 1) return false;
        Fragment next = manager.getFragments().get(manager.getBackStackEntryCount()-1);
        Fragment top = peek();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId,next);
        transaction.commitAllowingStateLoss();
        if (onFragmentRemovedListener != null)
            onFragmentRemovedListener.onFragmentRemoved(top);
        return true;
    }

    /**
     * Replaces stack contents with just one fragment.
     */
    public void replace(Fragment fragment) {
        this.replace(fragment, null, null);
    }

    /**
     * Replaces stack contents with just one fragment.
     */
    @SuppressLint("CommitTransaction")
    public void replace(Fragment fragment, View sharedElement, String name) {
        List<Fragment> fragments = getFragments();

        manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction()
                .replace(containerId, fragment, indexToTag(0));
        if (sharedElement != null && name != null) {
            transaction.addSharedElement(sharedElement, name);
        }
        transaction.commit();
        manager.executePendingTransactions();

        if (onFragmentRemovedListener != null) {
            for (Fragment fragment1 : fragments)
                onFragmentRemovedListener.onFragmentRemoved(fragment1);
        }
    }

    /**
     * Returns the topmost fragment in the stack.
     */
    public Fragment peek() {
        return manager.findFragmentById(containerId);
    }

    /**
     * Returns a back fragment if the fragment is of given class.
     * If such fragment does not exist and activity implements the given class then the activity will be returned.
     *
     * @param fragment     a fragment to search from.
     * @param callbackType a class of type for callback to search.
     * @param <T>          a type of callback.
     * @return a back fragment or activity.
     */
    @SuppressWarnings("unchecked")
    public <T> T findCallback(Fragment fragment, Class<T> callbackType) {

        Fragment back = getBackFragment(fragment);

        if (back != null && callbackType.isAssignableFrom(back.getClass()))
            return (T)back;

        if (callbackType.isAssignableFrom(activity.getClass()))
            return (T)activity;

        return null;
    }

    private Fragment getBackFragment(Fragment fragment) {
        List<Fragment> fragments = getFragments();
        for (int f = fragments.size() - 1; f >= 0; f--) {
            if (fragments.get(f) == fragment && f > 0)
                return fragments.get(f - 1);
        }
        return null;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>(manager.getBackStackEntryCount() + 1);
        for (int i = 0; i < manager.getBackStackEntryCount() + 1; i++) {
            Fragment fragment = manager.findFragmentByTag(indexToTag(i));
            if (fragment != null)
                fragments.add(fragment);
        }
        return fragments;
    }

    private String indexToTag(int index) {
        return Integer.toString(index);
    }
}
