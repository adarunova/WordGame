package com.wordgame.dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.wordgame.R;
import com.wordgame.models.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Dictionary database creator.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class DictionaryDatabaseCreator extends SQLiteOpenHelper {
    private static final String VIRTUAL_TABLE_NAME = "word_dictionary";
    private static final String DB_COL_WORD = "word";

    private final Context context;
    private SQLiteDatabase database;
    private final Language language;

    public DictionaryDatabaseCreator(Context context, String name, int version, Language language) {
        super(context, name, null, version);
        this.context = context;
        this.language = language;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        database = sqLiteDatabase;
        database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS " + VIRTUAL_TABLE_NAME +
                " USING fts3 (" + DB_COL_WORD + ")");
        loadDictionary();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VIRTUAL_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private void loadDictionary() {
        new Thread(() -> {
            try {
                loadDictionaryWords();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void loadDictionaryWords() throws IOException {
        final Resources resources = context.getResources();
        InputStream input;
        if (language == Language.ENGLISH) {
            input = resources.openRawResource(R.raw.en);
        } else if (language == Language.RUSSIAN) {
            input = resources.openRawResource(R.raw.ru);
        } else {
            throw new IllegalArgumentException("There is no such language");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String wordLine;
            while ((wordLine = reader.readLine()) != null) {
                String[] words = TextUtils.split(wordLine, "/");
                if (words.length < 1 || words[0].trim().length() < 4) {
                    continue;
                }
                addWordToDictionary(words[0].trim().toLowerCase());
            }
        }
    }

    public void addWordToDictionary(String word) {
        ContentValues values = new ContentValues();
        values.put(DB_COL_WORD, word);
        database.insert(VIRTUAL_TABLE_NAME, null, values);
    }
}
