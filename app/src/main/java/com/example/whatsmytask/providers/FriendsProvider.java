package com.example.whatsmytask.providers;

import com.example.whatsmytask.models.Friend;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FriendsProvider {

    CollectionReference mCollection;

    public FriendsProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Friends");
    }

    public void createFriend(Friend friend){
        mCollection.document(friend.getIdUser1()).collection("Users").document(friend.getIdUser2()).set(friend);
        mCollection.document(friend.getIdUser2()).collection("Users").document(friend.getIdUser1()).set(friend);
    }

    public Task<Void> deleteFriendByIdUser1(String idUser, String idFriend){
        return mCollection.document(idUser).collection("Users").document(idFriend).delete();
    }

    public Task<Void> deleteFriendByIdUser2(String idFriend, String idUser){
        return mCollection.document(idFriend).collection("Users").document(idUser).delete();
    }

    public Query getAll(String idUser){
        return mCollection.document(idUser).collection("Users");
    }

}
