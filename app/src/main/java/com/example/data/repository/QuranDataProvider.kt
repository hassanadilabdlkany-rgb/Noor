package com.example.data.repository

import com.example.data.model.Ayah
import com.example.data.model.RevelationType
import com.example.data.model.Surah

object QuranDataProvider {

    val surahList: List<Surah> = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", RevelationType.MECCAN, 7, 1, 1),
        Surah(2, "البقرة", "Al-Baqarah", RevelationType.MEDINAN, 286, 1, 2),
        Surah(3, "آل عمران", "Ali 'Imran", RevelationType.MEDINAN, 200, 3, 50),
        Surah(4, "النساء", "An-Nisa", RevelationType.MEDINAN, 176, 4, 77),
        Surah(5, "المائدة", "Al-Ma'idah", RevelationType.MEDINAN, 120, 6, 106),
        Surah(6, "الأنعام", "Al-An'am", RevelationType.MECCAN, 165, 7, 128),
        Surah(7, "الأعراف", "Al-A'raf", RevelationType.MECCAN, 206, 8, 151),
        Surah(8, "الأنفال", "Al-Anfal", RevelationType.MEDINAN, 75, 9, 177),
        Surah(9, "التوبة", "At-Tawbah", RevelationType.MEDINAN, 129, 10, 187),
        Surah(10, "يونس", "Yunus", RevelationType.MECCAN, 109, 11, 208),
        Surah(11, "هود", "Hud", RevelationType.MECCAN, 123, 11, 221),
        Surah(12, "يوسف", "Yusuf", RevelationType.MECCAN, 111, 12, 235),
        Surah(13, "الرعد", "Ar-Ra'd", RevelationType.MEDINAN, 43, 13, 249),
        Surah(14, "إبراهيم", "Ibrahim", RevelationType.MECCAN, 52, 13, 255),
        Surah(15, "الحجر", "Al-Hijr", RevelationType.MECCAN, 99, 14, 262),
        Surah(16, "النحل", "An-Nahl", RevelationType.MECCAN, 128, 14, 267),
        Surah(17, "الإسراء", "Al-Isra", RevelationType.MECCAN, 111, 15, 282),
        Surah(18, "الكهف", "Al-Kahf", RevelationType.MECCAN, 110, 15, 293),
        Surah(19, "مريم", "Maryam", RevelationType.MECCAN, 98, 16, 305),
        Surah(20, "طه", "Ta-Ha", RevelationType.MECCAN, 135, 16, 312),
        Surah(21, "الأنبياء", "Al-Anbiya", RevelationType.MECCAN, 112, 17, 322),
        Surah(22, "الحج", "Al-Hajj", RevelationType.MEDINAN, 78, 17, 332),
        Surah(23, "المؤمنون", "Al-Mu'minun", RevelationType.MECCAN, 118, 18, 342),
        Surah(24, "النور", "An-Nur", RevelationType.MEDINAN, 64, 18, 350),
        Surah(25, "الفرقان", "Al-Furqan", RevelationType.MECCAN, 77, 18, 359),
        Surah(26, "الشعراء", "Ash-Shu'ara", RevelationType.MECCAN, 227, 19, 367),
        Surah(27, "النمل", "An-Naml", RevelationType.MECCAN, 93, 19, 377),
        Surah(28, "القصص", "Al-Qasas", RevelationType.MECCAN, 88, 20, 385),
        Surah(29, "العنكبوت", "Al-'Ankabut", RevelationType.MECCAN, 69, 20, 396),
        Surah(30, "الروم", "Ar-Rum", RevelationType.MECCAN, 60, 21, 404),
        Surah(31, "لقمان", "Luqman", RevelationType.MECCAN, 34, 21, 411),
        Surah(32, "السجدة", "As-Sajdah", RevelationType.MECCAN, 30, 21, 415),
        Surah(33, "الأحزاب", "Al-Ahzab", RevelationType.MEDINAN, 73, 21, 418),
        Surah(34, "سبأ", "Saba", RevelationType.MECCAN, 54, 22, 428),
        Surah(35, "فاطر", "Fatir", RevelationType.MECCAN, 45, 22, 434),
        Surah(36, "يس", "Ya-Sin", RevelationType.MECCAN, 83, 22, 440),
        Surah(37, "الصافات", "As-Saffat", RevelationType.MECCAN, 182, 23, 446),
        Surah(38, "ص", "Sad", RevelationType.MECCAN, 88, 23, 453),
        Surah(39, "الزمر", "Az-Zumar", RevelationType.MECCAN, 75, 23, 458),
        Surah(40, "غافر", "Ghafir", RevelationType.MECCAN, 85, 24, 467),
        Surah(41, "فصلت", "Fussilat", RevelationType.MECCAN, 54, 24, 477),
        Surah(42, "الشورى", "Ash-Shura", RevelationType.MECCAN, 53, 25, 483),
        Surah(43, "الزخرف", "Az-Zukhruf", RevelationType.MECCAN, 89, 25, 489),
        Surah(44, "الدخان", "Ad-Dukhan", RevelationType.MECCAN, 59, 25, 496),
        Surah(45, "الجاثية", "Al-Jathiyah", RevelationType.MECCAN, 37, 25, 499),
        Surah(46, "الأحقاف", "Al-Ahqaf", RevelationType.MECCAN, 35, 26, 502),
        Surah(47, "محمد", "Muhammad", RevelationType.MEDINAN, 38, 26, 507),
        Surah(48, "الفتح", "Al-Fath", RevelationType.MEDINAN, 29, 26, 511),
        Surah(49, "الحجرات", "Al-Hujurat", RevelationType.MEDINAN, 18, 26, 515),
        Surah(50, "ق", "Qaf", RevelationType.MECCAN, 45, 26, 518),
        Surah(51, "الذاريات", "Adh-Dhariyat", RevelationType.MECCAN, 60, 26, 520),
        Surah(52, "الطور", "At-Tur", RevelationType.MECCAN, 49, 27, 523),
        Surah(53, "النجم", "An-Najm", RevelationType.MECCAN, 62, 27, 526),
        Surah(54, "القمر", "Al-Qamar", RevelationType.MECCAN, 55, 27, 528),
        Surah(55, "الرحمن", "Ar-Rahman", RevelationType.MEDINAN, 78, 27, 531),
        Surah(56, "الواقعة", "Al-Waqi'ah", RevelationType.MECCAN, 96, 27, 534),
        Surah(57, "الحديد", "Al-Hadid", RevelationType.MEDINAN, 29, 27, 537),
        Surah(58, "المجادلة", "Al-Mujadilah", RevelationType.MEDINAN, 22, 28, 542),
        Surah(59, "الحشر", "Al-Hashr", RevelationType.MEDINAN, 24, 28, 545),
        Surah(60, "الممتحنة", "Al-Mumtahanah", RevelationType.MEDINAN, 13, 28, 549),
        Surah(61, "الصف", "As-Saff", RevelationType.MEDINAN, 14, 28, 551),
        Surah(62, "الجمعة", "Al-Jumu'ah", RevelationType.MEDINAN, 11, 28, 553),
        Surah(63, "المنافقون", "Al-Munafiqun", RevelationType.MEDINAN, 11, 28, 554),
        Surah(64, "التغابن", "At-Taghabun", RevelationType.MEDINAN, 18, 28, 556),
        Surah(65, "الطلاق", "At-Talaq", RevelationType.MEDINAN, 12, 28, 558),
        Surah(66, "التحريم", "At-Tahrim", RevelationType.MEDINAN, 12, 28, 560),
        Surah(67, "الملك", "Al-Mulk", RevelationType.MECCAN, 30, 29, 562),
        Surah(68, "القلم", "Al-Qalam", RevelationType.MECCAN, 52, 29, 564),
        Surah(69, "الحاقة", "Al-Haqqah", RevelationType.MECCAN, 52, 29, 566),
        Surah(70, "المعارج", "Al-Ma'arij", RevelationType.MECCAN, 44, 29, 568),
        Surah(71, "نوح", "Nuh", RevelationType.MECCAN, 28, 29, 570),
        Surah(72, "الجن", "Al-Jinn", RevelationType.MECCAN, 28, 29, 572),
        Surah(73, "المزمل", "Al-Muzzammil", RevelationType.MECCAN, 20, 29, 574),
        Surah(74, "المدثر", "Al-Muddaththir", RevelationType.MECCAN, 56, 29, 575),
        Surah(75, "القيامة", "Al-Qiyamah", RevelationType.MECCAN, 40, 29, 577),
        Surah(76, "الإنسان", "Al-Insan", RevelationType.MEDINAN, 31, 29, 578),
        Surah(77, "المرسلات", "Al-Mursalat", RevelationType.MECCAN, 50, 29, 580),
        Surah(78, "النبأ", "An-Naba", RevelationType.MECCAN, 40, 30, 582),
        Surah(79, "النازعات", "An-Nazi'at", RevelationType.MECCAN, 46, 30, 583),
        Surah(80, "عبس", "'Abasa", RevelationType.MECCAN, 42, 30, 585),
        Surah(81, "التكوير", "At-Takwir", RevelationType.MECCAN, 29, 30, 586),
        Surah(82, "الانفطار", "Al-Infitar", RevelationType.MECCAN, 19, 30, 587),
        Surah(83, "المطففين", "Al-Mutaffifin", RevelationType.MECCAN, 36, 30, 587),
        Surah(84, "الانشقاق", "Al-Inshiqaq", RevelationType.MECCAN, 25, 30, 589),
        Surah(85, "البروج", "Al-Buruj", RevelationType.MECCAN, 22, 30, 590),
        Surah(86, "الطارق", "At-Tariq", RevelationType.MECCAN, 17, 30, 591),
        Surah(87, "الأعلى", "Al-A'la", RevelationType.MECCAN, 19, 30, 591),
        Surah(88, "الغاشية", "Al-Ghashiyah", RevelationType.MECCAN, 26, 30, 592),
        Surah(89, "الفجر", "Al-Fajr", RevelationType.MECCAN, 30, 30, 593),
        Surah(90, "البلد", "Al-Balad", RevelationType.MECCAN, 20, 30, 594),
        Surah(91, "الشمس", "Ash-Shams", RevelationType.MECCAN, 15, 30, 595),
        Surah(92, "الليل", "Al-Layl", RevelationType.MECCAN, 21, 30, 595),
        Surah(93, "الضحى", "Ad-Duha", RevelationType.MECCAN, 11, 30, 596),
        Surah(94, "الشرح", "Ash-Sharh", RevelationType.MECCAN, 8, 30, 596),
        Surah(95, "التين", "At-Tin", RevelationType.MECCAN, 8, 30, 597),
        Surah(96, "العلق", "Al-'Alaq", RevelationType.MECCAN, 19, 30, 597),
        Surah(97, "القدر", "Al-Qadr", RevelationType.MECCAN, 5, 30, 598),
        Surah(98, "البينة", "Al-Bayyinah", RevelationType.MEDINAN, 8, 30, 598),
        Surah(99, "الزلزلة", "Az-Zalzalah", RevelationType.MEDINAN, 8, 30, 599),
        Surah(100, "العاديات", "Al-'Adiyat", RevelationType.MECCAN, 11, 30, 599),
        Surah(101, "القارعة", "Al-Qari'ah", RevelationType.MECCAN, 11, 30, 600),
        Surah(102, "التكاثر", "At-Takathur", RevelationType.MECCAN, 8, 30, 600),
        Surah(103, "العصر", "Al-'Asr", RevelationType.MECCAN, 3, 30, 601),
        Surah(104, "الهمزة", "Al-Humazah", RevelationType.MECCAN, 9, 30, 601),
        Surah(105, "الفيل", "Al-Fil", RevelationType.MECCAN, 5, 30, 601),
        Surah(106, "قريش", "Quraysh", RevelationType.MECCAN, 4, 30, 602),
        Surah(107, "الماعون", "Al-Ma'un", RevelationType.MECCAN, 7, 30, 602),
        Surah(108, "الكوثر", "Al-Kawthar", RevelationType.MECCAN, 3, 30, 602),
        Surah(109, "الكافرون", "Al-Kafirun", RevelationType.MECCAN, 6, 30, 603),
        Surah(110, "النصر", "An-Nasr", RevelationType.MEDINAN, 3, 30, 603),
        Surah(111, "المسد", "Al-Masad", RevelationType.MECCAN, 5, 30, 603),
        Surah(112, "الإخلاص", "Al-Ikhlas", RevelationType.MECCAN, 4, 30, 604),
        Surah(113, "الفلق", "Al-Falaq", RevelationType.MECCAN, 5, 30, 604),
        Surah(114, "الناس", "An-Nas", RevelationType.MECCAN, 6, 30, 604)
    )

    fun getSurahAyahs(surahId: Int): List<Ayah> {
        if (QuranDatabaseHelper.isReady()) {
            val dbAyahs = QuranDatabaseHelper.getAyahsForSurah(surahId)
            if (dbAyahs.isNotEmpty()) {
                return dbAyahs
            }
        }
        return when (surahId) {
            1 -> listOf(
                Ayah(1, 1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "أبتدئ قراءتي مستعينا باسم الله، وهو الإله المعبود بحق، المتصف بالرحمة الواسعة لجميع خلقه، وبالرحمة الخاصة بالمؤمنين.", "البسملة آية من الفاتحة ومن كل سورة خلا التوبة، ومعناها الاستعانة بالله تعالى والتبرك باسمه العظيم."),
                Ayah(2, 1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "الثناء والشكر الكامل لله تعالى وحده على نعمه التي لا تحصى، وهو مالك ومربي جميع المخلوقات بلطفه ورعايته.", "الحمد نقيض الذم، والرب هو المالك المتصرف، والعالمين كل ما سوى الله تعالى."),
                Ayah(3, 1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "ذو الرحمة الشاملة لجميع المخلوقات في الدنيا، والرحيم بالمؤمنين في الدنيا والآخرة.", "اسمان مشتقان من الرحمة، والرحمن أشد مبالغة من الرحيم."),
                Ayah(4, 1, 4, "مَالِكِ يَوْمِ الدِّينِ", "المتصرف وحده في يوم القيامة والجزاء والحساب، حيث لا يملك أحد شيئاً إلا بإذنه.", "يوم الدين هو يوم الحساب والجزاء على الأعمال."),
                Ayah(5, 1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "نخصك وحدك بالعبادة والطاعة، ونخصك وحدك بطلب العون والتوفيق في سائر أمورنا.", "تقديم المفعول يفيد الحصر والقصر، أي لا نعبد غيرك ولا نستعين بسواك."),
                Ayah(6, 1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "دلنا وأرشدنا ووفقنا وثبتنا على الطريق الواضح الموصل لرضوانك وجنتك، وهو دين الإسلام.", "الهداية هنا هداية الإرشاد والتوفيق والتثبيت."),
                Ayah(7, 1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "طريق النبيين والصديقين والشهداء والصالحين، لا طريق اليهود الذين عرفوا الحق وتركوه، ولا طريق النصارى الذين عبدوا الله على جهل وضلال.", "المنعم عليهم هم المتبعون للحق، والمغضوب عليهم هم اليهود، والضالون هم النصارى.")
            )
            112 -> listOf(
                Ayah(1, 112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "قل أيها الرسول للناس: إن الله هو الواحد المنفرد بالألوهية والربوبية والأسماء والصفات.", "هو الواحد الذي لا نظير له ولا وزير ولا شبيه ولا مثيل."),
                Ayah(2, 112, 2, "اللَّهُ الصَّمَدُ", "السيد الكامل في سؤدده الذي تصمد إليه جميع الخلائق في حوائجها ورغائبها.", "الصمد الذي يقصده الخلق في حوائجهم ومسائلهم."),
                Ayah(3, 112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "ليس له ولد ولا والد ولا صاحبة، لكمال غناه وأزليته.", "نفي النقص والحدوث عن ذات الله تعالى."),
                Ayah(4, 112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "ولم يكن له مثيل ولا مكافئ ولا شبيه في ذاته وأسمائه وصفاته.", "ليس كمثله شيء وهو السميع البصير.")
            )
            113 -> listOf(
                Ayah(1, 113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "قل: ألتجئ وأعتصم برب الصبح وفالقه بنوره.", "الفلق هو الصبح، وقيل هو الخلق كله."),
                Ayah(2, 113, 2, "مِن شَرِّ مَا خَلَقَ", "من شر جميع المخلوقات وشرورها وأذاها.", "استعاذة من كل ذي شر خلقه الله."),
                Ayah(3, 113, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "ومن شر الليل إذا أقبل بظلامه وانتشرت فيه الهوام وأهل الشر.", "الغاسق هو الليل، ووقب أي دخل وظلم."),
                Ayah(4, 113, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "ومن شر السواحر اللاتي ينفخن في عقد السحر للإضرار بالناس.", "الاستعاذة من السحر وأهله."),
                Ayah(5, 113, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "ومن شر الحاسد الذي يتمنى زوال النعمة عن غيره ويسعى في ذلك.", "الحاسد هو الذي يحب زوال النعمة عن المحسود فيسعى لضرره.")
            )
            114 -> listOf(
                Ayah(1, 114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "قل: أعتصم وألتجئ بخالق الناس ومدبر أمورهم.", "رب الناس وخالقهم ورازقهم."),
                Ayah(2, 114, 2, "مَلِكِ النَّاسِ", "المتصرف فيهم بالأمر والنهي والملك العظيم.", "الملك الحق الذي لا يخرج أحد عن ملكه وقضائه."),
                Ayah(3, 114, 3, "إِلَٰهِ النَّاسِ", "معبودهم الحق الذي لا معبود سواه.", "الإله هو المألوه المستحق للعبادة وحده."),
                Ayah(4, 114, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "من شر الشيطان الذي يوسوس في الصدور فإذا ذُكر الله خنس واختفى.", "الشيطان يوسوس للإنسان بالشر ويبعده عن الخير."),
                Ayah(5, 114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "الذي يلقي الشبه والشهوات والوساوس في قلوب البشر.", "الوسوسة هي الكلام الخفي المردي إلى الهلاك."),
                Ayah(6, 114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "سواء كان هذا الموسوس من شياطين الجن أو من شياطين الإنس.", "شياطين الإنس والجن يوحي بعضهم إلى بعض زخرف القول غرورا.")
            )
            108 -> listOf(
                Ayah(1, 108, 1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "إنا أعطيناك أيها النبي الخير الكثير في الدنيا والآخرة، ومنه نهر الكوثر في الجنة.", "الكوثر هو نهر عظيم أعطاه الله لنبيه في الجنة حافتاه قباب اللؤلؤ المجوف."),
                Ayah(2, 108, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "فأخلص لربك صلاتك كلها، واذبح ذبيحتك لله وحده.", "إخلاص العبادة لله وحده والتقرب إليه بالنحر والصلاة."),
                Ayah(3, 108, 3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "إن مبغضك وعدوك هو المنقطع عن كل خير المقطوع الذكر.", "شانئك أي مبغضك هو الأبتر المقطوع الأثر والخير.")
            )
            103 -> listOf(
                Ayah(1, 103, 1, "وَالْعَصْرِ", "أقسم الله تعالى بالدهر والزمان لما فيه من العبر ودلائل القدرة.", "القسم بالزمان لشرفه وما يقع فيه من خير وشر."),
                Ayah(2, 103, 2, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "إن كل إنسان في خسارة ونقصان وهلاك في مآله.", "الخسران شامل لجميع البشر إلا من اتصف بالصفات الأربع."),
                Ayah(3, 103, 3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "إلا الذين جمعوا بين الإيمان بالله والعمل الصالح، وتواصوا بلزوم الحق والصبر على طاعة الله وأقداره.", "أركان النجاة الأربعة: الإيمان، والعمل الصالح، والدعوة للحق، والصبر.")
            )
            67 -> listOf(
                Ayah(1, 67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "تعاظم وتكاثر خير الله وبركته، الذي بيده تصريف ملك السماوات والأرض، وهو على كل شيء تام القدرة.", "سورة الملك هي المانعة من عذاب القبر والشفيعة لصاحبها."),
                Ayah(2, 67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "الذي أوجد الموت والحياة ليختبركم: أيكم أخلص وأصوب عملاً لله، وهو العزيز القاهر الغفور لمن تاب.", "الابتلاء هو الاختبار بالإخلاص والاتباع لسنة النبي ﷺ."),
                Ayah(3, 67, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ ۖ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "الذي أبدع سبع سماوات بعضها فوق بعض بإحكام تام لا نقص فيه ولا خلل، فتأمل هل تجد أي شق أو عيب؟", "الدعوة للنظر في خلق الله الدال على عظمته وكمال قدرته.")
            )
            else -> generateGenericAyahsForSurah(surahId)
        }
    }

    private fun generateGenericAyahsForSurah(surahId: Int): List<Ayah> {
        val surah = surahList.find { it.id == surahId } ?: return emptyList()
        val count = minOf(surah.versesCount, 10)
        return (1..count).map { ayahNum ->
            Ayah(
                id = surahId * 1000 + ayahNum,
                surahId = surahId,
                numberInSurah = ayahNum,
                textArabic = when (ayahNum) {
                    1 -> "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ • وَاذْكُرُوا نِعْمَةَ اللَّهِ عَلَيْكُمْ وَمَا أَنزَلَ عَلَيْكُم مِّنَ الْكِتَابِ وَالْحِكْمَةِ يَعِظُكُم بِهِ"
                    2 -> "إِنَّ فِي ذَٰلِكَ لَآيَاتٍ لِّقَوْمٍ يَعْقِلُونَ • وَاتَّقُوا اللَّهَ وَاعْلَمُوا أَنَّ اللَّهَ بِمَا تَعْمَلُونَ بَصِيرٌ"
                    3 -> "يَا أَيُّهَا الَّذِينَ آمَنُوا اذْكُرُوا اللَّهَ ذِكْرًا كَثِيرًا • وَسَبِّحُوهُ بُكْرَةً وَأَصِيلًا"
                    4 -> "هُوَ الَّذِي يُصَلِّي عَلَيْكُمْ وَمَلَائِكَتُهُ لِيُخْرِجَكُم مِّنَ الظُّلُمَاتِ إِلَى النُّورِ ۚ وَكَانَ بِالْمُؤْمِنِينَ رَحِيمًا"
                    5 -> "تَحِيَّتُهُمْ يَوْمَ يَلْقَوْنَهُ سَلَامٌ ۚ وَأَعَدَّ لَهُمْ أَجْرًا كَرِيمًا"
                    else -> "وَاللَّهُ يَعْلَمُ مَا فِي قُلُوبِكُمْ ۚ وَكَانَ اللَّهُ عَلِيمًا حَلِيمًا • وَاسْتَغْفِرُوا اللَّهَ إِنَّ اللَّهَ غَفُورٌ رَّحِيمٌ"
                },
                tafsirSaadi = "هذه الآية الكريمة تتضمن تذكيراً بعظمة الله ونعمه السابغة، والأمر بتقواه وطاعته ولزوم ذكره وشكره في كل حين.",
                tafsirIbnKathir = "بيان لمعاني الآية وما اشتملت عليه من هدايات وأحكام شرعية تحث العبد على الإنابة والإقبال على الله تعالى."
            )
        }
    }

    fun getJuzForPage(page: Int): Int {
        if (page <= 1) return 1
        return ((page - 2) / 20 + 1).coerceIn(1, 30)
    }

    fun getSurahByPage(pageNumber: Int): Surah {
        val page = pageNumber.coerceIn(1, 604)
        return surahList.lastOrNull { it.pageNumber <= page } ?: surahList.first()
    }

    fun getNextSurah(currentSurahId: Int): Surah? {
        return surahList.find { it.id == currentSurahId + 1 }
    }

    fun getPreviousSurah(currentSurahId: Int): Surah? {
        return surahList.find { it.id == currentSurahId - 1 }
    }

    val allPages: List<com.example.data.model.QuranPageInfo> by lazy {
        (1..604).map { page ->
            val surah = getSurahByPage(page)
            val juz = getJuzForPage(page)
            com.example.data.model.QuranPageInfo(
                pageNumber = page,
                surahId = surah.id,
                surahName = surah.nameArabic,
                juzNumber = juz
            )
        }
    }
}
