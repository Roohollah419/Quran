package com.example.quran.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity representing an Ayah in the database.
 */
@Entity(tableName = "ayahs")
public class AyahEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int surahNumber;
    private int ayahNumber;
    private String textArabic;
    private String textTranslation;

    public AyahEntity() {
    }

    @androidx.room.Ignore
    public AyahEntity(int surahNumber, int ayahNumber, String textArabic, String textTranslation) {
        this.surahNumber = surahNumber;
        this.ayahNumber = ayahNumber;
        this.textArabic = textArabic;
        this.textTranslation = textTranslation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurahNumber() {
        return surahNumber;
    }

    public void setSurahNumber(int surahNumber) {
        this.surahNumber = surahNumber;
    }

    public int getAyahNumber() {
        return ayahNumber;
    }

    public void setAyahNumber(int ayahNumber) {
        this.ayahNumber = ayahNumber;
    }

    public String getTextArabic() {
        return textArabic;
    }

    public void setTextArabic(String textArabic) {
        this.textArabic = textArabic;
    }

    public String getTextTranslation() {
        return textTranslation;
    }

    public void setTextTranslation(String textTranslation) {
        this.textTranslation = textTranslation;
    }
}
