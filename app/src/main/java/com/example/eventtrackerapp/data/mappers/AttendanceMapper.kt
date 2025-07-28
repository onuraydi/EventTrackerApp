package com.example.eventtrackerapp.data.mappers

import com.example.eventtrackerapp.model.firebasemodels.FirebaseAttendance
import com.example.eventtrackerapp.model.roommodels.ProfileEventCrossRef

object AttendanceMapper {
    fun toEntity(firebaseAttendance:FirebaseAttendance):ProfileEventCrossRef{
        return ProfileEventCrossRef(
            profileId = firebaseAttendance.profileId,
            eventId = firebaseAttendance.eventId,
            isAttending = firebaseAttendance.isAttending
        )
    }

    fun toFirebaseModel(profileEventCrossRef: ProfileEventCrossRef):FirebaseAttendance{
        return FirebaseAttendance(
            profileId = profileEventCrossRef.profileId,
            eventId = profileEventCrossRef.eventId,
            isAttending = profileEventCrossRef.isAttending
        )
    }
}