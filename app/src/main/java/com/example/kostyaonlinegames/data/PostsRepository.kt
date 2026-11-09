package com.example.kostyaonlinegames.data

class PostsRepository(private val api: PostsApi) {

    // Список тем для разнообразия
    private val newsTopics = listOf(
        "Глобальное обновление 2.0" to "Разработчики выпустили масштабный патч. Добавлена новая локация «Ледяные пики», переработан баланс классов и исправлено более 100 ошибок.",
        "Новый рейд-босс" to "В глубинах подземелья пробудился Древний Кракен. Собирайте команду из 5 человек, чтобы сразиться с ним и получить легендарное снаряжение.",
        "Скидки в магазине" to "Только на этой неделе скидки до 50% на все эпические скины. Успейте пополнить свою коллекцию по выгодной цене!",
        "Киберспортивный турнир" to "Открыта регистрация на осенний кубок. Призовой фонд составляет 1 000 000 рублей. Покажите всем, на что вы способны!",
        "Балансное обновление" to "Мы ослабили класс «Убийца» в PvP и усилили «Целителя». Подробности изменений читайте в полном патчноуте.",
        "Новая система крафта" to "Теперь вы можете создавать уникальные предметы, комбинируя различные ресурсы. Исследуйте новые рецепты и улучшайте экипировку."
    )

    suspend fun getPosts(): Result<List<Post>> = runCatching {
        val apiPosts = api.getPosts()

        // Превращаем посты с сервера в игровые новости
        apiPosts.map { post ->
            val topic = newsTopics[post.id % newsTopics.size]
            post.copy(
                title = topic.first,
                body = "${topic.second}\n\n(Пост ID: ${post.id})"
            )
        }
    }

    suspend fun getPost(id: Int): Result<Post> = runCatching {
        val post = api.getPost(id)
        val topic = newsTopics[post.id % newsTopics.size]
        post.copy(
            title = topic.first,
            body = "${topic.second}\n\n(Пост ID: ${post.id})"
        )
    }
}