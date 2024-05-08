package com.example.projektmunka.repository
import com.example.projektmunka.data.Route
import com.example.projektmunka.data.UserRoute
import com.example.projektmunka.remote.UserRouteDao


class UserRouteRepository(private val userRouteDao: UserRouteDao) {

    suspend fun saveUserRouteIntoFirestore(route: Route, userId: String, userRouteTrackerId: String){
        val userRoute = UserRoute(
            userId = userId,
            userRouteTrackerId = userRouteTrackerId,
            route = route
        )

        println("routeData: ${userRoute.route.path.first()}")
        userRouteDao.saveUserRouteIntoFirestore(userRoute)
    }
}