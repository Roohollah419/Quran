package com.example.quran.utils;

import android.content.Context;

import com.example.quran.data.local.entity.AyahEntity;
import com.example.quran.data.local.entity.SurahEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to parse Quran JSON file from assets.
 */
public class QuranJsonParser {

    /**
     * JSON structure for a chapter/surah
     */
    public static class QuranChapter {
        public int id;
        public String name;
        public String transliteration;
        public String translation;
        public String type;
        public int total_verses;
        public List<QuranVerse> verses;
    }

    /**
     * JSON structure for a verse/ayah
     */
    public static class QuranVerse {
        public int id;
        public String text;
    }

    /**
     * Parse quran.json file from assets and return list of chapters.
     */
    public static List<QuranChapter> parseQuranJson(Context context) throws IOException {
        InputStream is = context.getAssets().open("quran.json");
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<QuranChapter>>() {}.getType();
        List<QuranChapter> chapters = gson.fromJson(reader, listType);

        reader.close();
        is.close();

        return chapters;
    }

    /**
     * Convert QuranChapter to SurahEntity
     */
    public static SurahEntity toSurahEntity(QuranChapter chapter) {
        String revelationType = "meccan".equalsIgnoreCase(chapter.type) ? Constants.MECCAN : Constants.MEDINAN;

        return new SurahEntity(
                chapter.id,
                chapter.name,
                chapter.transliteration != null ? chapter.transliteration : "",
                chapter.translation != null ? chapter.translation : "",
                chapter.total_verses,
                revelationType
        );
    }

    /**
     * Convert QuranVerse to AyahEntity
     */
    public static AyahEntity toAyahEntity(int surahNumber, QuranVerse verse) {
        return new AyahEntity(
                surahNumber,
                verse.id,
                verse.text,
                "" // No translation for now
        );
    }

    /**
     * Parse JSON and convert all data to entities
     */
    public static class QuranData {
        public List<SurahEntity> surahs;
        public List<AyahEntity> ayahs;

        public QuranData() {
            surahs = new ArrayList<>();
            ayahs = new ArrayList<>();
        }
    }

    public static QuranData parseAndConvert(Context context) {
        QuranData data = new QuranData();

        try {
            List<QuranChapter> chapters = parseQuranJson(context);

            for (QuranChapter chapter : chapters) {
                // Convert chapter to SurahEntity
                data.surahs.add(toSurahEntity(chapter));

                // Convert all verses to AyahEntity
                if (chapter.verses != null) {
                    for (QuranVerse verse : chapter.verses) {
                        data.ayahs.add(toAyahEntity(chapter.id, verse));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
