package com.example.quran.data.model;

/**
 * Model class representing an Ayah (Verse) in the Quran.
 * This is a POJO used in the UI layer.
 */
public class Ayah {

    private int id;
    private int surahNumber;
    private int ayahNumber;
    private String textArabic;
    private String textTranslation;

    public Ayah() {
    }

    public Ayah(int id, int surahNumber, int ayahNumber, String textArabic, String textTranslation) {
        this.id = id;
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
