package com.example.quran.data.model;

/**
 * Model class representing a Surah (Chapter) in the Quran.
 * This is a POJO used in the UI layer.
 */
public class Surah {

    private int number;
    private String nameArabic;
    private String nameEnglish;
    private String translation;
    private int totalAyahs;
    private String revelationType;

    public Surah() {
    }

    public Surah(int number, String nameArabic, String nameEnglish, String translation,
                 int totalAyahs, String revelationType) {
        this.number = number;
        this.nameArabic = nameArabic;
        this.nameEnglish = nameEnglish;
        this.translation = translation;
        this.totalAyahs = totalAyahs;
        this.revelationType = revelationType;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getNameArabic() {
        return nameArabic;
    }

    public void setNameArabic(String nameArabic) {
        this.nameArabic = nameArabic;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getTotalAyahs() {
        return totalAyahs;
    }

    public void setTotalAyahs(int totalAyahs) {
        this.totalAyahs = totalAyahs;
    }

    public String getRevelationType() {
        return revelationType;
    }

    public void setRevelationType(String revelationType) {
        this.revelationType = revelationType;
    }
}
