package com.example.model

data class AudioTourItem(
    val id: String,
    val title: String,
    val monumentName: String,
    val city: String,
    val durationSeconds: Int,
    val narratorName: String,
    val language: String,
    val transcript: String,
    val historicalFact: String,
    val ambientTrack: String // "Dutor & Nay", "Tanbur & Doira", "Silk Road Caravan Bells"
)

data class AmbientMusicTrack(
    val id: String,
    val title: String,
    val instrument: String,
    val mood: String,
    val duration: String
)

object AudioTourRepository {
    val ambientTracks = listOf(
        AmbientMusicTrack("amb_1", "Registon Saxari (Tanbur & Nay)", "Nay & Tanbur", "Meditativ & Ruhlantiruvchi", "03:45"),
        AmbientMusicTrack("amb_2", "Buxoro Karvonlari (Dutor)", "O'zbek Milliy Dutori", "Tinchlantiruvchi Sharqona", "04:12"),
        AmbientMusicTrack("amb_3", "Xiva Minorasi Sirlari", "G'ijjak & Chang", "Sirli & Tarixiy", "03:18"),
        AmbientMusicTrack("amb_4", "Ipak Yo'li Quyosh Botishi", "Nay & Doira", "Shohona & Immersiv", "05:01")
    )

    val tourList = listOf(
        AudioTourItem(
            id = "tour_registan",
            title = "Registon Maydoni: Sharq Gavhari",
            monumentName = "Registon Majmuasi (3 Madrasa)",
            city = "Samarqand",
            durationSeconds = 195,
            narratorName = "Prof. Alisher Zohidov (Tarixchi Gid)",
            language = "O'zbek / English / Russian",
            transcript = "Assalomu alaykum va qadimiy Samarqandga xush kelibsiz! Siz ayni damda dunyoning eng mahobatli me'moriy ansambllaridan biri bo'lgan Registon maydonida turibsiz. Bu yerda 3 ta buyuk madrasa: Mirzo Ulug'bek, Sherdor va Tillakori qad rostlagan. Ulug'bek madrasasi yulduzlar ilmini o'rgatgan bo'lsa, Tillakori madrasasining gumbazi sof tillo bilan jilolangan...",
            historicalFact = "Tillakori madrasasining ichki gumbazi ganchkorlik uslubida 5 kg sof tilla bilan zarhallangan.",
            ambientTrack = "Registon Saxari (Tanbur & Nay)"
        ),
        AudioTourItem(
            id = "tour_kalyan",
            title = "Minorai Kalon: Chingizxon Hayratda Qolgan Obida",
            monumentName = "Poyi Kalon & Minorai Kalon",
            city = "Buxoro",
            durationSeconds = 170,
            narratorName = "Malika Karimova (Madaniyatshunos)",
            language = "O'zbek / English / Russian",
            transcript = "Buxoroyi Sharifning ramzi bo'lgan Minorai Kalon 1127-yilda Qoraxoniylar davrida qurilgan. Balandligi 46.5 metr bo'lib, 900 yildan beri zilzilalar va bo'ronlarga bardosh berib kelmoqda. Rivoyatlarga ko'ra, Chingizxon Buxoroni zabt etganda minora mahobati qarshisida bosh kiyimi tushib ketgan va uni buzmaslikni buyurgan...",
            historicalFact = "Minoraning poydevori qamish, ganch va tuxum oqidan tayyorlangan qadimiy qorishma asosida mustahkamlangan.",
            ambientTrack = "Buxoro Karvonlari (Dutor)"
        ),
        AudioTourItem(
            id = "tour_ichan_kala",
            title = "Ichan Qal'a: Ochiq Osmon Ostidagi Tirik Muzey",
            monumentName = "Ichan Qal'a & Kalta Minor",
            city = "Xiva",
            durationSeconds = 210,
            narratorName = "Farhod Rahimov (Xorazm Davlat Muzeyi)",
            language = "O'zbek / English / Russian",
            transcript = "Xorazm vohasining yuragi Ichan Qal'aga qadam qo'yganingizda vaqt to'xtab qolgandek tuyuladi. 2500 yillik tarixga ega bo'lgan bu qadimiy shahar to'liq devorlar bilan o'ralgan. Moviy sirkor koshinlar bilan bezatilgan Kalta Minor minorasi esa butun dunyoda yagona hisoblanadi...",
            historicalFact = "Kalta Minor agar qurib bitkazilganda 70 metrdan ortiq bo'lib, Buxorogacha bo'lgan yo'lni ko'rish rejalashtirilgan edi.",
            ambientTrack = "Xiva Minorasi Sirlari"
        ),
        AudioTourItem(
            id = "tour_shohi_zinda",
            title = "Shohi Zinda: Moviy Koshinlar Mo''jizasi",
            monumentName = "Shohi Zinda Nekropoli",
            city = "Samarqand",
            durationSeconds = 185,
            narratorName = "Prof. Alisher Zohidov",
            language = "O'zbek / English / Russian",
            transcript = "Shohi Zinda – 'Tirik Shoh' majmuasi 11-asrdan 19-asrgacha bo'lgan 20 dan ortiq muhtasham maqbaralardan iborat. Har bir qadamda moviy, lojuvard va zumrad tusdagi terakota koshinlar sizni o'rta asrlar Temuriylar me'morchiligi cho'qqisiga olib boradi...",
            historicalFact = "Shohi Zinda koshinlaridagi moviy rang siri hanuzgacha to'liq fanga noma'lum bo'lib, quyosh nurlarida o'z rangini o'zgartiradi.",
            ambientTrack = "Ipak Yo'li Quyosh Botishi"
        )
    )
}
