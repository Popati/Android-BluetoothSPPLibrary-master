package app.akexorcist.bluetoothspp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by Popati on 23/9/58.
 */
public class TapsHistory extends FragmentPagerAdapter
{

        public TapsHistory(FragmentManager fm){
            super(fm);
            Log.v("tap set","start");
        }

        @Override
        public Fragment getItem(int index) {
    
        switch (index) {
            case 0:

                Log.v("tap set","1");
                // Top Rated fragment activity
                return new GoogleGraph();
            case 1:

                Log.v("tap set","2");
                // Games fragment activity
                return new MapsHistory2();
            case 2:
                Log.v("tap set","3");
                // Movies fragment activity
                return new GyroHistory();
        }

        return null;
    }

        @Override
        public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}