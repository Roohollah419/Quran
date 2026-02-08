package com.example.quran.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity representing a Surah in the database.
 */
@Entity(tableName = "surahs")
public class SurahEntity {

    @PrimaryKey
    private int number;

    private String nameArabic;
    private String nameEnglish;
    private String translation;
    private int totalAyahs;
    private String revelationType;

    public SurahEntity() {
    }

    @androidx.room.Ignore
    public SurahEntity(int number, String nameArabic, String nameEnglish, String translation,
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
