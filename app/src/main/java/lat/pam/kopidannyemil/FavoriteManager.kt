package lat.pam.kopidannyemil

import android.content.Context

object FavoriteManager {

    private const val PREF_NAME = "KopiFavorite"
    private const val KEY_FAVORITES = "favorites"

    fun getFavorites(context: Context): MutableSet<String> {
        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        return preferences.getStringSet(
            KEY_FAVORITES,
            emptySet()
        )?.toMutableSet() ?: mutableSetOf()
    }

    fun isFavorite(context: Context, productId: String): Boolean {
        return getFavorites(context).contains(productId)
    }

    fun toggleFavorite(
        context: Context,
        productId: String
    ): Boolean {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val favorites = getFavorites(context)

        val isNowFavorite: Boolean

        if (favorites.contains(productId)) {
            favorites.remove(productId)
            isNowFavorite = false
        } else {
            favorites.add(productId)
            isNowFavorite = true
        }

        preferences.edit()
            .putStringSet(KEY_FAVORITES, favorites.toSet())
            .apply()

        return isNowFavorite
    }
}