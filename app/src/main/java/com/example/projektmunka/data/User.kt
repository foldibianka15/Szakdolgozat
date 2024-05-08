package com.example.projektmunka.data
//import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.Collections.emptyList

@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val age: String = "",
    val weight: String = "",
    val image: String = "",
    val gender: String = "",
    val fitnessLevel: Double = 50.0,
    val friends: List<User> = emptyList(),
    val friendRequests: MutableList<User> = emptyList(),
    val profileCompleted: Int = 0
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (email != other.email) return false
        if (age != other.age) return false
        if (weight != other.weight) return false
        if (image != other.image) return false
        if (gender != other.gender) return false
        if (fitnessLevel != other.fitnessLevel) return false
        if (friends != other.friends) return false
        if (friendRequests != other.friendRequests) return false
        if (profileCompleted != other.profileCompleted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + age.hashCode()
        result = 31 * result + weight.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + fitnessLevel.hashCode()
        result = 31 * result + friends.hashCode()
        result = 31 * result + friendRequests.hashCode()
        result = 31 * result + profileCompleted
        return result
    }
}

