package ua.radio.online.data

/**
 * Тестові радіостанції.
 * Пізніше будуть замінені на дані з Supabase через адмінку.
 */
object TestStations {
    val list = listOf(
        RadioStation(
            id = "1",
            name = "Українське радіо",
            streamUrl = "https://radio.ukr.radio/ur1-mp3",
            description = "Перший канал Суспільного"
        ),
        RadioStation(
            id = "2",
            name = "Радіо Промінь",
            streamUrl = "https://radio.ukr.radio/ur2-mp3",
            description = "Музика та культура"
        ),
        RadioStation(
            id = "3",
            name = "Радіо Культура",
            streamUrl = "https://radio.ukr.radio/ur3-mp3",
            description = "Культурний канал"
        ),
        RadioStation(
            id = "4",
            name = "Тестовий стрім",
            streamUrl = "http://91.218.213.49:8000/ur1-mp3",
            description = "Тестовий HTTP стрім"
        )
    )
}
