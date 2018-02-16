package org.smartregister.path.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.widget.LinearLayout;

import org.smartregister.path.R;
import org.smartregister.path.listener.CustomNavigationBarListener;
import org.smartregister.path.listener.NavigationItemListener;
import org.smartregister.stock.activity.StockActivity;
import org.smartregister.stock.activity.StockControlActivity;

import static org.smartregister.path.activity.LoginActivity.getOpenSRPContext;
import static org.smartregister.util.Log.logError;

/**
 * Created by samuelgithengi on 2/14/18.
 */

public class PathStockActivity extends StockActivity {

    public static final String CURRENT_LOCATION = "org.smartregister.path.activity.Location";

    private NavigationItemListener navigationItemListener;

    private CustomNavigationBarListener customNavigationBarListener;

    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = getIntent().getStringExtra(CURRENT_LOCATION);
        customNavigationBarListener = new CustomNavigationBarListener(this, location);
        navigationItemListener = new NavigationItemListener(this, location);
        initializeCustomNavbarListeners();
    }

    @Override
    protected String getLoggedInUserInitials() {
        try {
            String preferredName = getOpenSRPContext().allSharedPreferences().getANMPreferredName(
                    getOpenSRPContext().allSharedPreferences().fetchRegisteredANM());
            if (!TextUtils.isEmpty(preferredName)) {
                String[] initialsArray = preferredName.split(" ");
                String initials = "";
                if (initialsArray.length > 0) {
                    initials = initialsArray[0].substring(0, 1);
                    if (initialsArray.length > 1) {
                        initials = initials + initialsArray[1].substring(0, 1);
                    }
                }

                return initials.toUpperCase();
            }

        } catch (Exception e) {
            logError("Error on initView : Getting Preferences: Getting Initials");
        }

        return null;
    }

    @Override
    protected NavigationView getNavigationView() {
        NavigationView view = (NavigationView) getLayoutInflater().inflate(R.layout.custom_nav_view_base, null);
        return view;
    }

    @Override
    protected NavigationView.OnNavigationItemSelectedListener getNavigationViewListener() {
        return navigationItemListener;
    }

    @Override
    protected int getNavigationViewWith() {
        return (int) getResources().getDimension(R.dimen.nav_view_width);
    }

    @Override
    protected Class getControlActivity() {
        return StockControlActivity.class;
    }

    private void initializeCustomNavbarListeners() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout syncMenuItem = (LinearLayout) drawer.findViewById(R.id.nav_sync);
        syncMenuItem.setOnClickListener(customNavigationBarListener);

        LinearLayout addchild = (LinearLayout) drawer.findViewById(R.id.nav_register);
        addchild.setOnClickListener(customNavigationBarListener);

        LinearLayout outofcatchment = (LinearLayout) drawer.findViewById(R.id.nav_record_vaccination_out_catchment);
        outofcatchment.setOnClickListener(customNavigationBarListener);

        LinearLayout stockregister = (LinearLayout) drawer.findViewById(R.id.stock_control);
        stockregister.setOnClickListener(customNavigationBarListener);

        LinearLayout hia2 = (LinearLayout) drawer.findViewById(R.id.hia2_reports);
        hia2.setOnClickListener(customNavigationBarListener);

        LinearLayout childregister = (LinearLayout) drawer.findViewById(R.id.child_register);
        childregister.setOnClickListener(customNavigationBarListener);

        LinearLayout coverage = (LinearLayout) drawer.findViewById(R.id.coverage_reports);
        coverage.setOnClickListener(customNavigationBarListener);

        LinearLayout dropout = (LinearLayout) drawer.findViewById(R.id.dropout_reports);
        dropout.setOnClickListener(customNavigationBarListener);

    }
}
