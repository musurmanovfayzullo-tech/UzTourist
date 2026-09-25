import type { LangCode } from '../i18n'

export interface ArMonumentFact {
  id: string
  monumentName: string
  city: string
  builder: string
  yearConstructed: string
  architecturalStyle: string
  heightMeters: number
  azimuthDegrees: number
  distanceMeters: number
  modernImage: string
  ancientImage: string
  ancientEraLabel: string
  description: string
  keyFacts: string[]
  acousticReverbTimeSec: number
  audioDurationSec: number
  restorationEra: string
  multiLingualAudio: Partial<Record<LangCode, string>>
  multiLingualDescription: Partial<Record<LangCode, string>>
}

export function arAudioScript(f: ArMonumentFact, lang: LangCode): string {
  return f.multiLingualAudio[lang] ?? f.multiLingualAudio.uz ?? f.multiLingualAudio.en ?? f.description
}
export function arDescription(f: ArMonumentFact, lang: LangCode): string {
  return f.multiLingualDescription[lang] ?? f.multiLingualDescription.uz ?? f.multiLingualDescription.en ?? f.description
}

export const AR_FACTS: ArMonumentFact[] = [
  {
    id: 'ar_registan_sherdor',
    monumentName: 'Registon Maydoni & Sherdor',
    city: 'Samarkand',
    builder: "Yalangtush Bahodir & Mirzo Ulug'bek",
    yearConstructed: '1417 – 1636 yillar (XV-XVII asr)',
    architecturalStyle: "Sharqona Temuriylar majolika & guldor koshin",
    heightMeters: 34.5,
    azimuthDegrees: 142,
    distanceMeters: 18.4,
    modernImage: '/images/img_registan_1787819168326.jpg',
    ancientImage: '/images/ancient_registan_view_1788109456843.jpg',
    ancientEraLabel: "XV Asr Temuriylar Buyuk Ipak Yo'li Davri",
    description:
      "Registon — qadimiy Samarqandning yuragi. XV asrda bu yer Buyuk Ipak yo'lining eng yirik xalqaro savdo rastasi, karvonsaroylar va ilm-fan markazi bo'lgan.",
    keyFacts: [
      "XV asrda Ulug'bek tomonidan dunyodagi eng ilg'or rasadxona va madrasa qurilgan",
      "Sherdor madrasasi peshtoqida koinot va quyoshni ifodalovchi sher-yo'lbarslar tasvirlangan",
      "250,000 dan ortiq qo'lda sayqallangan feruza va lojuvard koshinlar bilan bezatilgan",
      "Qadimda maydon bo'ylab Xitoy, Hindiston va Fors karvonlari o'tgan",
    ],
    acousticReverbTimeSec: 3.8,
    audioDurationSec: 95,
    restorationEra: 'YUNESKO Butunjahon Madaniy Merosi',
    multiLingualDescription: {
      uz: "Registon — qadimiy Samarqandning yuragi. XV asrda bu yer Buyuk Ipak yo'lining gavjum savdo va ilm-fan markazi bo'lgan.",
      en: 'Registan is the heart of ancient Samarkand. In the 15th century, it was the vibrant crossroads of the Great Silk Road, filled with camel caravans and world-leading scholars.',
      ru: 'Регистан — сердце древнего Самарканда. В XV веке здесь кипела жизнь Великого Шёлкового пути с караванами верблюдов и великими обсерваториями.',
      de: 'Der Registan-Platz ist das Herz des antiken Samarkand. Im 15. Jahrhundert war er ein pulsierendes Zentrum der Seidenstraße.',
      fr: "Le Régistan est le cœur de l'ancienne Samarcande. Au XVe siècle, c'était le carrefour flamboyant de la Route de la Soie.",
      es: 'La Plaza de Registán es el corazón de la antigua Samarcanda. En el siglo XV era el cruce más dinámico de la Ruta de la Seda.',
      tr: "Registan, kadim Semerkant'ın kalbidir. 15. yüzyılda İpek Yolu'nun en görkemli kervan ve bilim merkeziydi.",
      zh: '雷吉斯坦是古代撒马尔罕的心脏。15世纪这里是丝绸之路繁华的贸易与天文学中心。',
      ja: 'レギスタン広場は古代サマルカンドの中心です。15世紀にはシルクロードのラクダ隊商と天文学の中心地として栄えました。',
    },
    multiLingualAudio: {
      uz: "Siz qadimiy Registon maydonining XV asrdagi Temuriylar saltanati davridagi ko'rinishini ko'rib turibsiz. Bu davrda Registon nafaqat Sharqning eng go'zal me'moriy majmuasi, balki Buyuk Ipak yo'lining eng yirik savdo chorrahasi bo'lgan. Mirzo Ulug'bek tomonidan bunyod etilgan madrasada butun dunyodan kelgan allomalar falakiyot, matematika va me'morchilik ilmlarini o'rgangan. Peshtoqdagi moviy gumbazlar va oltin qoplamali naqshlar o'sha davrdan buyon o'zining betakror ulug'vorligini saqlab kelmoqda.",
      en: "You are looking at the ancient 15th-century reconstruction of Samarkand's Registan Square during the Timurid Renaissance. In this golden era, Registan was the bustling nexus of the Great Silk Road, where merchants from China, Persia, and Venice traded silk, gems, and spices alongside the world's most advanced observatory and academies of astronomy led by Ulugh Beg.",
      ru: 'Перед вами историческая 3D-реконструкция площади Регистан XV века эпохи Тимуридов. В это золотое время Регистан был главным перекрёстком Великого Шёлкового пути, где караваны купцов останавливались у величественных медресе, а учёные под руководством Мирзо Улугбека совершали выдающиеся астрономические открытия.',
      de: 'Sie blicken auf die historische Rekonstruktion des Registan-Platzes im 15. Jahrhundert während der Timuriden-Dynastie. Hier trafen Seidenstraßen-Karawanen auf weltberühmte Gelehrte der Astronomie und Architektur.',
      fr: 'Vous contemplez la reconstitution historique de la place du Régistan au XVe siècle sous les Timourides. Ce joyau de la Route de la Soie abritait d\'immenses caravanes marchandes et de prestigieuses académies astronomiques.',
      es: 'Estás contemplando la reconstrucción histórica del siglo XV de la Plaza de Registán. Durante el renacimiento timúrida, fue el cruce estelar de la Ruta de la Seda, con caravanas y templos del saber astronómico.',
      tr: "Şu anda Semerkant Registan Meydanı'nın 15. yüzyıl Timurlular dönemindeki kadim görünümünü izliyorsunuz. Burası İpek Yolu'nun en işlek kervan merkezi ve Uluğ Bey'in gökbilim medresesiyle Doğu'nun incisiydi.",
      zh: '您正在观看15世纪帖木儿王朝时期撒马尔罕雷吉斯坦广场的古代历史复原景象。当时这里是丝绸之路上商旅云集、兀鲁伯天文学院辉煌鼎盛的东方明珠。',
      ja: 'こちらは15世紀ティムール帝国時代のサマルカンド・レギスタン広場の古代復元図です。シルクロードの隊商が行き交い、ウルグ・ベクによる世界最先端の天文学が栄えた黄金期の姿です。',
    },
  },
  {
    id: 'ar_kalyan_minaret',
    monumentName: 'Minorai Kalon Majmuasi',
    city: 'Bukhara',
    builder: 'Arslonxon (Qoraxoniylar sulolasi)',
    yearConstructed: '1127 yil (XII asr)',
    architecturalStyle: "Qoraxoniylar g'isht o'ymakorligi va pishiq g'isht relyefi",
    heightMeters: 45.6,
    azimuthDegrees: 265,
    distanceMeters: 24,
    modernImage: '/images/img_bukhara_1787819199064.jpg',
    ancientImage: '/images/ancient_bukhara_view_1788109477013.jpg',
    ancientEraLabel: "XII Asr Qoraxoniylar Ipak Yo'li Vohasi",
    description:
      "Minorai Kalon — 1127 yilda barpo etilgan, qadimda Qizilqum sahrosidan kelayotgan karvonlarga kechalari mayoq vazifasini o'tagan 45 metrlik boqiy minora.",
    keyFacts: [
      "14 ta betakror relyefli g'isht halqalari bilan o'ralgan",
      "Chingizxon bu minoraning muhtashamligidan lol qolib, uni buzmaslikni buyurgan",
      "Poydevori 10 metr chuqurlikda tuya suti va maxsus ohak qorishmasidan qurilgan",
      "Sahro qumlarida adashmaslik uchun tunda tepasida olov yoqilgan",
    ],
    acousticReverbTimeSec: 2.4,
    audioDurationSec: 82,
    restorationEra: 'YUNESKO Buxoro Tarixiy Markazi',
    multiLingualDescription: {
      uz: "Minorai Kalon — XII asrda Buxoro osmonida porlagan va sahro karvonlariga yo'l ko'rsatgan boqiy me'moriy mo'jiza.",
      en: 'The Kalyan Minaret is a 12th-century desert beacon that guided Silk Road caravans across the Kyzylkum dunes to the sacred oasis of Bukhara.',
      ru: 'Минарет Калян — величественный маяк XII века, указывавший путь караванам Шёлкового пути через пустыню Кызылкум к благородной Бухаре.',
      de: 'Das Kalyan-Minarett diente im 12. Jahrhundert als Wüstenleuchtturm für Karawanen auf dem Weg nach Buchara.',
      fr: 'Le Minaret Kalyan est un phare du désert du XIIe siècle qui guidait les caravanes de la Route de la Soie.',
      es: 'El Minarete de Kalyan es un faro del desierto del siglo XII que guiaba a las caravanas de la Ruta de la Seda hacia Bujará.',
      tr: 'Kalyan Minaresi, 12. yüzyılda İpek Yolu kervanlarına Kızılkum çölünde yol gösteren 45 metrelik kadim bir fenerdi.',
      zh: '卡扬宣礼塔建于12世纪，高45.6米，曾是指引丝绸之路商队穿越克孜勒库姆沙漠的古老灯塔。',
      ja: 'カラーン・ミナレットは12世紀に建てられ、キジルクム砂漠を渡るシルクロードの隊商を導いた砂漠の灯台でした。',
    },
    multiLingualAudio: {
      uz: "Siz ko'rib turgan Minorai Kalon 1127 yilda Qoraxoniylar hukmdori Arslonxon tomonidan qurilgan. XII asrda Qizilqum sahrosi bo'ylab harakatlangan minglab tuyali karvonlar tunda minoraning eng yuqorisida yoqilgan olov yog'dusi orqali Buxoro shahrini topib kelgan. Minora pishiq g'ishtdan shunday mahorat bilan terilganki, unga bironta ham sirlangan koshin ishlatilmagan, barcha naqshlar g'ishtlarning tabiiy soyasi orqali hosil qilingan.",
      en: 'Rising 45.6 meters above the Silk Road, the Kalyan Minaret was built in 1127 AD. For centuries, a beacon fire burned at its summit each night, guiding camel caravans safely across the treacherous Kyzylkum desert sands into the historic oasis bazaar of Bukhara.',
      ru: 'Минарет Калян высотой 45,6 метров был воздвигнут в 1127 году правителем Арслан-ханом. Веками на его вершине по ночам зажигали сигнальный огонь, который служил путеводным маяком для караванов, шедших через знойные пески пустыни Кызылкум в Бухару.',
      de: 'Das 45,6 Meter hohe Kalyan-Minarett wurde 1127 erbaut. Nachts brannte auf seiner Spitze ein Signalfeuer, das den Karawanen den Weg durch die Wüste wies.',
      fr: 'Érigé en 1127, le Minaret Kalyan culmine à 45,6 mètres. La nuit, un feu allumé à son sommet servait de phare aux caravanes traversant le désert vers Boukhara.',
      es: 'Construido en 1127, el Minarete de Kalyan de 45 metros funcionaba como faro nocturno para las caravanas que cruzaban el desierto hacia los bazares de Bujará.',
      tr: "1127 yılında inşa edilen 45,6 metrelik Kalyan Minaresi, yüzyıllar boyunca zirvesinde yakılan ateşle Kızılkum çölünden Buhara'ya gelen kervanlara rehberlik etmiştir.",
      zh: '建于1127年的卡扬宣礼塔高达45.6米。几个世纪以来，每当夜幕降临，塔顶都会点燃明火，为穿越茫茫沙漠的丝路驼队指明通往布哈拉绿洲的方向。',
      ja: '1127年に建立された高さ45.6メートルのカラーン・ミナレット。夜間は塔の頂上に灯火が灯され、キジルクム砂漠を行く隊商たちにブハラの街への目印を示していました。',
    },
  },
  {
    id: 'ar_kalta_minor',
    monumentName: "Kalta Minor & Ichan Qal'a",
    city: 'Khiva',
    builder: 'Muhammad Aminxon (Xiva Xonligi)',
    yearConstructed: '1851 – 1855 yillar (XIX asr)',
    architecturalStyle: "Xorazm feruza va lojuvard koshinli sirkor me'morchilik",
    heightMeters: 29,
    azimuthDegrees: 48,
    distanceMeters: 12,
    modernImage: '/images/img_khiva_1787819215477.jpg',
    ancientImage: '/images/ancient_khiva_view_1788109493393.jpg',
    ancientEraLabel: 'XVIII-XIX Asr Xiva Xonligi Jonli Shahri',
    description:
      "Kalta Minor — Xorazm me'morchiligining tengsiz durdonasi. U butun yuzasi boshdan-oyoq yaltiroq feruza va zumrad koshinlar bilan qoplangan yagona minoradir.",
    keyFacts: [
      "Asl loyihasi bo'yicha 70-80 metr bo'lishi va Buxorogacha ko'rinishi rejalashtirilgan",
      "Asosi 14.2 metr diametrga ega bo'lib, Markaziy Osiyodagi eng keng minoradir",
      "Butun vujudi 'Chashmi bulbul' va zangori koshin naqshlari bilan bezatilgan",
      "Ichan Qal'a to'liq xom g'ishtdan tiklangan va ochiq osmon ostidagi tirik muzey hisoblanadi",
    ],
    acousticReverbTimeSec: 1.9,
    audioDurationSec: 75,
    restorationEra: "YUNESKO Ichan Qal'a Butunjahon Merosi",
    multiLingualDescription: {
      uz: "Kalta Minor — moviy feruza koshinlar jilosi bilan Ichan Qal'a markazida qad ko'targan Xorazm mo'jizasi.",
      en: "Kalta Minor is the jewel of Khiva's Ichan Qala, entirely wrapped in gleaming turquoise and emerald glazed tiles.",
      ru: 'Калта-Минор — бирюзовая жемчужина крепости Ичан-Кала в Хиве, полностью покрытая сверкающей майоликой.',
      de: 'Kalta Minor ist das türkis leuchtende Wahrzeichen von Chiwa, vollständig mit glasierten Fliesen bedeckt.',
      fr: "Le Kalta Minor est l'emblème turquoise étincelant d'Itchan Kala à Khiva, recouvert de somptueuses faïences.",
      es: 'Kalta Minor es la joya turquesa de la fortaleza de Ichan Kala en Jiva, totalmente revestida de mayólica esmaltada.',
      tr: "Kalta Minor, Hiva İçan Kale'nin baştan aşağı firuze çinilerle kaplı eşsiz masalsı kulesidir.",
      zh: '短宣礼塔（卡尔塔米纳尔）是希瓦伊钦卡拉城堡的绿松石珍珠，全身覆盖着华丽的彩釉瓷砖。',
      ja: 'カルタ・ミナルはヒヴァのイチャン・カラにある青緑色の美しいミナレットで、全体が色鮮やかなマヨリカタイルで覆われています。',
    },
    multiLingualAudio: {
      uz: "Siz Ichan Qal'aning XIX asrdagi Xiva xonligi davridagi qadimiy manzarasini va moviy Kalta Minor minorasini ko'rib turibsiz. Muhammad Aminxon bu minorani butun Ipak yo'lidagi eng baland minora — 70 metr qilib qurmoqchi bo'lgan. Garchi qurilish to'xtatilgan bo'lsa-da, uning tubdan cho'qqisigacha yaltirab turgan firuza va lojuvard koshinlari uni jahon me'morchiligining eng betakror ramziga aylantirdi.",
      en: "You are looking at the historical 19th-century view of Khiva's Ichan Qala fortress and the iconic Kalta Minor tower. Originally planned to soar over 70 meters high, its unique diameter and completely glazed turquoise majolica envelope make it a breathtaking masterpiece of Silk Road oasis architecture.",
      ru: 'Перед вами исторический облик крепости Ичан-Кала в Хиве XIX века и бирюзовый минарет Калта-Минор. По замыслу Мухаммад Амин-хана, этот минарет должен был стать самым высоким в Центральной Азии. Его сияющие лазурные узоры до сих пор поражают путешественников со всего мира.',
      de: 'Sie sehen die historische Ansicht der Festung Itchan-Kala in Chiwa aus dem 19. Jahrhundert mit dem türkis glänzenden Minarett Kalta Minor.',
      fr: "Voici la vue historique de la forteresse d'Itchan Kala à Khiva au XIXe siècle avec l'incomparable minaret turquoise Kalta Minor.",
      es: 'Estás viendo la vista histórica del siglo XIX de la fortaleza de Ichan Kala en Jiva y el deslumbrante minarete turquesa Kalta Minor.',
      tr: 'Şu anda Hiva İçan Kale kalesinin 19. yüzyıldaki tarihi atmosferini ve masmavi çinileriyle parlayan Kalta Minor kulesini görüyorsunuz.',
      zh: '您正在观看19世纪希瓦汗国伊钦卡拉古城堡及绿松石短宣礼塔的古代历史风貌。通体镶嵌的青金石瓷砖展现了中亚绿洲建筑的极致之美。',
      ja: 'こちらは19世紀ヒヴァ・ハン国時代のイチャン・カラ城塞と美しい青碧のカルタ・ミナルの歴史的再現です。',
    },
  },
  {
    id: 'ar_gur_amir',
    monumentName: "Go'ri Amir Maqbarasi",
    city: 'Samarkand',
    builder: 'Amir Temur & Muhammad Sulton',
    yearConstructed: '1404 yil (XV asr)',
    architecturalStyle: "Buyuk Temuriylar moviy gumbazli me'morligi",
    heightMeters: 34,
    azimuthDegrees: 190,
    distanceMeters: 15,
    modernImage: '/images/img_registan_1787819168326.jpg',
    ancientImage: '/images/ancient_registan_view_1788109456843.jpg',
    ancientEraLabel: 'XV Asr Temuriylar Saltanati Durlari',
    description:
      "Go'ri Amir — Sohibqiron Amir Temur va uning avlodlari (Ulug'bek, Shohrux) mangu qo'nim topgan moviy qovurg'ali gumbazli muqaddas maqbara.",
    keyFacts: [
      "64 ta qovurg'ali moviy gumbaz Samarqand osmonining ramzi hisoblanadi",
      "Temurning qabri ustidagi tosh qora nefritdan yasalgan bo'lib, unga muqaddas bitiklar o'yilgan",
      "Ichki xonadagi zargarlik naqshlari va kundal uslubidagi tillarang ganchlar tengsiz",
      "Hindistondagi Toj Mahal aynan Go'ri Amir me'morchiligi asosida loyihalashtirilgan",
    ],
    acousticReverbTimeSec: 4.2,
    audioDurationSec: 88,
    restorationEra: 'YUNESKO Butunjahon Merosi',
    multiLingualDescription: {
      uz: "Go'ri Amir — Amir Temurning moviy gumbazli muazzam maqbarasi va Temuriylar me'morchiligining cho'qqisi.",
      en: 'Gur-e-Amir is the mausoleum of Timur the Great, famous for its fluted azure dome and magnificent jade tomb.',
      ru: 'Гур-Эмир — усыпальница великого Амира Темура с уникальным лазурным ребристым куполом и нефритом.',
      de: 'Gur-e-Amir ist das prachtvolle Mausoleum von Timur mit seiner gerippten azurblauen Kuppel.',
      fr: 'Gour Emir est le majestueux mausolée de Tamerlan, célèbre pour son dôme nervuré turquoise.',
      es: 'Gur-e-Amir es el mausoleo de Tamerlán, célebre por su cúpula azul estriada y su lápida de jade.',
      tr: "Gur-i Emir, Timur İmparatorluğu'nun kurucusu Emir Timur'un yivli turkuaz kubbeli anıt mezarıdır.",
      zh: '古尔-埃米尔陵寝是帖木儿大帝的长眠之地，以蔚蓝棱纹圆顶和深绿软玉石棺闻名于世。',
      ja: 'グーリ・アミール廟はティムールが眠る霊廟で、美しい青のフルートドームで世界的に有名です。',
    },
    multiLingualAudio: {
      uz: "Siz Sohibqiron Amir Temur, uning nabirasi Mirzo Ulug'bek va Temuriylar sulolasi dafn etilgan muqaddas Go'ri Amir maqbarasini ko'rib turibsiz. 1404-yilda qurilgan bu obidaning 64 qovurg'ali feruza gumbazi dunyo me'morchiligi durdonasidir. Aynan ushbu maqbara uslubi keyinchalik Hindistonda Boburiylar tomonidan bunyod etilgan jahonga mashhur Toj Mahal obidasiga ilhom manbai bo'lgan.",
      en: 'You are viewing the Gur-e-Amir mausoleum, the final resting place of conqueror Timur and his royal successors, including astronomer king Ulugh Beg. Built in 1404, its azure ribbed dome served as the architectural prototype for the Taj Mahal in Agra.',
      ru: 'Перед вами мавзолей Гур-Эмир — усыпальница Амира Темура и его потомков, включая Мирзо Улугбека. Построенный в 1404 году лазурный купол с 64 гранями стал прообразом индийского Тадж-Махала.',
      de: 'Sie sehen das Gur-e-Amir Mausoleum, erbaut 1404 als Grabstätte von Timur und seiner Dynastie.',
      fr: 'Vous contemplez le mausolée de Gour Emir, chef-d\'œuvre de 1404 abritant la sépulture de Tamerlan.',
      es: 'Estás observando el mausoleo Gur-e-Amir, construido en 1404 como morada eterna de Tamerlán.',
      tr: "Gur-i Emir türbesi, 1404 yılında inşa edilmiş ve Tac Mahal'e ilham vermiş eşsiz bir Timur dönemi şaheseridir.",
      zh: '古尔-埃米尔陵建于1404年，是帖木儿大帝的陵墓，其建筑风格直接启发了印度泰姬陵的建造。',
      ja: '1404年に建立されたグーリ・アミール廟。青く輝くドームは、後にインドのタージ・マハルの手本ともなりました。',
    },
  },
  {
    id: 'ar_ark_bukhara',
    monumentName: "Buxoro Ark Qal'asi",
    city: 'Bukhara',
    builder: "Qadimiy Sug'd va Buxoro amirlari",
    yearConstructed: 'V asr milodiy (2000 yillik)',
    architecturalStyle: "Qadimiy tuproq va pishiq g'ishtli mudofaa forpost",
    heightMeters: 20,
    azimuthDegrees: 310,
    distanceMeters: 28,
    modernImage: '/images/img_bukhara_1787819199064.jpg',
    ancientImage: '/images/ancient_bukhara_view_1788109477013.jpg',
    ancientEraLabel: 'V-XVIII Asrlar Buxoro Amirligi Taxti',
    description:
      "Buxoro Arki — shahar ichidagi shahar. 20 metrli ulkan qal'a devorlari ichida amir saroyi, zarbxona, zindon va xazina joylashgan bo'lgan.",
    keyFacts: [
      "Afsonaga ko'ra, qal'a poydevori Siyovush tomonidan solingan",
      "Ark devorlari 4 gektar maydonni egallagan ulkan qal'a-shaharchadir",
      "Ibn Sino va Umar Xayyom Arkning mashhur kutubxonasida ilmiy asarlar yaratgan",
      "Darvoza ustida soat va amir bayrog'i hilpirab turgan",
    ],
    acousticReverbTimeSec: 2.1,
    audioDurationSec: 78,
    restorationEra: 'YUNESKO Buxoro Markazi',
    multiLingualDescription: {
      uz: "Buxoro Arki — 2000 yillik tarixga ega muhtasham qal'a-shahar va amirlar qarorgohi.",
      en: 'The Ark of Bukhara is a massive 5th-century fortress, home to emirs, royal treasuries, and historic libraries.',
      ru: 'Крепость Арк в Бухаре — древнейший оплот правителей V века, город в городе с царскими дворцами.',
      de: 'Die Festung Ark in Buchara ist eine massive Zitadelle aus dem 5. Jahrhundert.',
      fr: "L'Ark de Boukhara est une forteresse colossale du Ve siècle ayant abrité les émirs et Avicenne.",
      es: 'El Arca de Bujará es una enorme fortaleza del siglo V que albergaba el palacio de los emires.',
      tr: "Buhara Ark Kalesi, İbn-i Sina'nın kütüphanesinde çalıştığı 2000 yıllık devasa hükümdarlık kalesidir.",
      zh: '布哈拉雅克城堡是一座始建于5世纪的庞大泥砖要塞，曾是历代布哈拉埃米尔的王宫。',
      ja: 'ブハラのアーク城塞は5世紀に築かれ、アヴィセンナら多くの学者が集った王宮要塞です。',
    },
    multiLingualAudio: {
      uz: "Siz 2000 yildan ortiq vaqt davomida qad ko'tarib turgan Buxoro Ark qal'asini ko'rib turibsiz. Bu ulkan tuproq qal'a ichida Buxoro amirlarining saroyi, qabulxonasi, zargarlik zarbxonasi va hatto Ibn Sino ta'lim olgan mashhur kutubxona bo'lgan. Uning baland devorlari asrlar davomida sahrodan bostirib kelgan dushman hujumlaridan Buxoro xalqini himoya qilgan.",
      en: 'You are witnessing the Ark of Bukhara, a massive fortress city founded over 1,500 years ago. Inside its colossal ramparts once thrived royal palaces, throne rooms, and legendary libraries where polymath Avicenna studied.',
      ru: 'Перед вами величественная цитадель Арк — сердце древней Бухары с более чем двухтысячелетней историей. За её мощными стенами жили эмиры, чеканились монеты и создавал свои труды Авиценна.',
      de: 'Sie blicken auf die 2000 Jahre alte Festung Ark, das Machtzentrum der Emire von Buchara.',
      fr: "Voici l'Ark de Boukhara, forteresse imprenable où Ibn Sina (Avicenne) étudia dans sa célèbre bibliothèque royale.",
      es: 'Contemplas el Arca de Bujará, una fortaleza de 2000 años donde residían los emires y donde estudió Avicena.',
      tr: "Buhara Ark Kalesi, 2000 yıllık geçmişiyle İpek Yolu'nun en görkemli savunma kalelerinden biridir.",
      zh: '您正在瞻仰拥有2000多年历史的布哈拉雅克城堡，这里曾是历代埃米尔的权力中枢与学术殿堂。',
      ja: 'こちらは2000年以上の歴史を誇るブハラのアーク城塞です。歴代アミールの宮殿として栄えました。',
    },
  },
]
