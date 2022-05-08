package com.wordgame.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wordgame.models.Language;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Russian and English dictionary executor.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class Dictionary {
    private static final String VIRTUAL_TABLE_NAME = "word_dictionary";
    private static final String DB_COL_WORD = "word";
    private static final int DB_VERSION = 1;

    public final DictionaryDatabaseOpenHelper dictionaryOpenHelper;

    public Dictionary(Context context, Language language) {
        dictionaryOpenHelper = new DictionaryDatabaseOpenHelper(
                context, VIRTUAL_TABLE_NAME, DB_VERSION, language);
        dictionaryOpenHelper.openDataBase();
    }

    public String getWordMatches(String word) {
        // SELECT word FROM word_dictionary WHERE word = '<word>'
        Cursor cursor = dictionaryOpenHelper.getReadableDatabase().query(
                VIRTUAL_TABLE_NAME,
                new String[]{DB_COL_WORD},
                DB_COL_WORD + " = ?",
                new String[]{word},
                null, null, null);

        String result = null;
        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }

    public String[] generateWords(int limit) {
        // SELECT word FROM word_dictionary ORDER BY RANDOM() LIMIT <limit>
        Cursor cursor = dictionaryOpenHelper.getReadableDatabase().query(
                VIRTUAL_TABLE_NAME,
                new String[]{DB_COL_WORD},
                null,
                null,
                null, null, "RANDOM()", String.valueOf(limit));

        cursor.moveToFirst();
        String results[] = new String[limit];
        for (int i = 0; !cursor.isAfterLast(); i++) {
            results[i] = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return results;
    }

    private static class DictionaryDatabaseOpenHelper extends SQLiteOpenHelper {
        private final Context context;
        private final String databaseName;

        DictionaryDatabaseOpenHelper(Context context, String name, int version, Language language) {
            super(context, name + "_" + language.getLanguageCode().toLowerCase(), null, version);
            this.context = context;
            databaseName = name + "_" + language.getLanguageCode().toLowerCase();

            getReadableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        private void copyDataBaseFromAsset() throws IOException {
            InputStream inputData = context.getAssets().open(databaseName);

            String outputDataName = getDatabasePath() + databaseName;
            File f = new File(getDatabasePath());
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream outputData = new FileOutputStream(outputDataName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputData.read(buffer)) > 0) {
                outputData.write(buffer, 0, length);
            }
            outputData.flush();
            outputData.close();
            inputData.close();
        }

        public void openDataBase() throws SQLException {
            try {
                copyDataBaseFromAsset();
            } catch (IOException e) {
                throw new RuntimeException("Database creation failed", e);
            }
            SQLiteDatabase.openDatabase(context.getDatabasePath(databaseName).getPath(), null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
        }

        private String getDatabasePath() {
            return context.getApplicationInfo().dataDir + "/databases/";
        }
    }
}
