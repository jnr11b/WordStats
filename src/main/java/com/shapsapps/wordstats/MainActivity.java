package com.shapsapps.wordstats;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shapsapps.wordstats.R;

import java.util.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Map;
import java.util.Enumeration;

public class MainActivity extends FragmentActivity {

    /**
     * The {@link PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static String texts;
    static Hashtable<String,Integer> words = new Hashtable<String,Integer>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //grab text data and put in hash table
        getTextContent();
        texts = makeTextString();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //comment
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.shapsapps.wordstats/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.shapsapps.wordstats/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase();
                case 1:
                    return getString(R.string.title_section2).toUpperCase();
                case 2:
                    return getString(R.string.title_section3).toUpperCase();
            }
            return null;
        }
    }

    public void getTextContent() {
        Uri uri = Uri.parse("content://sms/sent");
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        String[] body = new String[c.getCount()];
        String[] number = new String[c.getCount()];
        String Tag = "MyActivity";
        int totalcount = 0;
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                body[i] = c.getString(c.getColumnIndexOrThrow("body"));
                number[i] = c.getString(c.getColumnIndexOrThrow("address"));
                //split string
                String[] splitted = body[i].split("\\s+");
                int j = 0;
                for(j = 0; j < splitted.length; j++){
                    //if word is alread in hastable
                    if(words.containsKey(splitted[j])){
                        //increment number by 1
                        int prev_val = words.get(splitted[j]);
                        words.put(splitted[j], prev_val + 1);
                    }
                    //otherwise put word in hashtable
                    else {
                        words.put(splitted[j], 1);
                    }
                        totalcount++;
                }
                c.moveToNext();
                //Log.i(Tag, "i= " + totalcount);
            }
        }
        c.close();
    }

    public static String makeTextString(){
        String fullWords = "\n";
        String singleWord;
        Integer singleWordCount;
        String Tag = "MyActivity";
        int i = 0;


        ArrayList<Map.Entry<String, Integer>> sortWords = sortValue(words);

        for(i = 0; i < 10; i++) {
            singleWord = sortWords.get(i).getKey();
            singleWordCount = sortWords.get(i).getValue();
            fullWords = fullWords + "\n " + singleWord + "   x" + singleWordCount;
            Log.i(Tag, "Word = " + singleWord);
        }
        Log.i(Tag, "FullWord = " + fullWords);
        return fullWords;
    }

    public static ArrayList sortValue(Hashtable<String, Integer> t){
        String Tag = "MyActivity";
        //Transfer as List and sort it
        ArrayList<Map.Entry<String, Integer>> l = new ArrayList(t.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {

            public int compare(Map.Entry<String, Integer> o2, Map.Entry<String, Integer> o1) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        //Log.i(Tag, "Word =" + l.get(0));
        return l;
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        public DummySectionFragment() {
        }

        public static final String ARG_SECTION_NUMBER = "section_number";


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            TextView textView = new TextView(getActivity());
            textView.setGravity(Gravity.CENTER);
            Bundle args = getArguments();

            switch (args.getInt(ARG_SECTION_NUMBER)) {
                case 1: // Do stuff for the texts here
                    textView.setText("View your text stats here: \n" + texts);
                    break;
                case 2: // Do stuff for Facebook here
                    textView.setText("View your Facebook stats here:");
                    break;
                case 3: // Do stuff for Twitter here
                    textView.setText("View your Twitter stats here:");
                    break;
            }
            return textView;
        }
    }
}
