package com.example.data.model

data class CountryItem(
    val code: String,
    val nameAr: String,
    val nameEn: String,
    val flag: String,
    val defaultLanguage: String = "العربية"
)

object CountriesProvider {
    val supportedCountries = listOf(
        CountryItem(code = "ALL", nameAr = "كل الدول", nameEn = "All Countries", flag = "🌐", defaultLanguage = "العربية"),
        CountryItem(code = "EG", nameAr = "مصر", nameEn = "Egypt", flag = "🇪🇬", defaultLanguage = "العربية"),
        CountryItem(code = "SA", nameAr = "السعودية", nameEn = "Saudi Arabia", flag = "🇸🇦", defaultLanguage = "العربية"),
        CountryItem(code = "AE", nameAr = "الإمارات", nameEn = "UAE", flag = "🇦🇪", defaultLanguage = "العربية"),
        CountryItem(code = "MA", nameAr = "المغرب", nameEn = "Morocco", flag = "🇲🇦", defaultLanguage = "العربية / Darija"),
        CountryItem(code = "KW", nameAr = "الكويت", nameEn = "Kuwait", flag = "🇰🇼", defaultLanguage = "العربية"),
        CountryItem(code = "JO", nameAr = "الأردن", nameEn = "Jordan", flag = "🇯🇴", defaultLanguage = "العربية"),
        CountryItem(code = "IQ", nameAr = "العراق", nameEn = "Iraq", flag = "🇮🇶", defaultLanguage = "العربية"),
        CountryItem(code = "DZ", nameAr = "الجزائر", nameEn = "Algeria", flag = "🇩🇿", defaultLanguage = "العربية / Français"),
        CountryItem(code = "QA", nameAr = "قطر", nameEn = "Qatar", flag = "🇶🇦", defaultLanguage = "العربية"),
        CountryItem(code = "LB", nameAr = "لبنان", nameEn = "Lebanon", flag = "🇱🇧", defaultLanguage = "العربية")
    )

    val supportedLanguages = listOf(
        "العربية (Arabic)",
        "English (الإنجليزية)",
        "Français (الفرنسية)"
    )
}
