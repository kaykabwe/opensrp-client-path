package org.smartregister.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.path.domain.Cohort;
import org.smartregister.repository.BaseRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by keyman on 11/01/18.
 */
public class CohortRepository extends BaseRepository {
    private static final String TAG = CohortRepository.class.getCanonicalName();
    public static final SimpleDateFormat DF_YYYYMM = new SimpleDateFormat("yyyy-MM");
    private static final String TABLE_NAME = "cohorts";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";
    private static final String[] TABLE_COLUMNS = {
            COLUMN_ID, COLUMN_MONTH, COLUMN_CREATED_AT, COLUMN_UPDATED_AT
    };

    private static final String COHORT_SQL = "CREATE TABLE " + TABLE_NAME +
            " (" + COLUMN_ID + " INTEGER NOT NULL, " +
            COLUMN_MONTH + " VARCHAR NOT NULL," +
            COLUMN_CREATED_AT + " DATETIME NULL," +
            COLUMN_UPDATED_AT + " TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP)";
    private static final String MONTH_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_MONTH + "_index ON " + TABLE_NAME + "(" + COLUMN_MONTH + " COLLATE NOCASE);";

    public CohortRepository(PathRepository pathRepository) {
        super(pathRepository);

    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(COHORT_SQL);
        database.execSQL(MONTH_INDEX);
    }

    public void add(Cohort cohort) {
        if (cohort == null) {
            return;
        }

        if (cohort.getUpdatedAt() == null) {
            cohort.setUpdatedAt(new Date());
        }

        SQLiteDatabase database = getWritableDatabase();
        if (cohort.getId() == null) {
            cohort.setCreatedAt(new Date());
            cohort.setId(database.insert(TABLE_NAME, null, createValuesFor(cohort)));
        } else {
            //mark the vaccine as unsynced for processing as an updated stock
            String idSelection = COLUMN_ID + " = ?";
            database.update(TABLE_NAME, createValuesFor(cohort), idSelection, new String[]{cohort.getId().toString()});
        }
    }

    public Cohort findByMonth(String month) {
        if (StringUtils.isBlank(month)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS, COLUMN_MONTH + " = ? COLLATE NOCASE ", new String[]{month}, null, null, null, null);
            List<Cohort> cohorts = readAllDataElements(cursor);
            if (!cohorts.isEmpty()) {
                return cohorts.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public Cohort findByMonth(Date month) {
        if (month == null) {
            return null;
        }
        try {
            String monthString = DF_YYYYMM.format(month);
            return findByMonth(monthString);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }


    public Cohort findById(long id) {
        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
            List<Cohort> cohorts = readAllDataElements(cursor);
            if (!cohorts.isEmpty()) {
                return cohorts.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    private List<Cohort> readAllDataElements(Cursor cursor) {
        List<Cohort> cohorts = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Cohort cohort = new Cohort();
                    cohort.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    cohort.setMonth(cursor.getString(cursor.getColumnIndex(COLUMN_MONTH)));
                    cohort.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT))));
                    cohort.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED_AT))));
                    cohorts.add(cohort);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return cohorts;

    }

    private ContentValues createValuesFor(Cohort cohort) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, cohort.getId());
        values.put(COLUMN_MONTH, cohort.getMonth());
        values.put(COLUMN_CREATED_AT, cohort.getCreatedAt() != null ? cohort.getCreatedAt().getTime() : null);
        values.put(COLUMN_UPDATED_AT, cohort.getUpdatedAt() != null ? cohort.getUpdatedAt().getTime() : null);
        return values;
    }


}
